package com.bokecc.util;

import com.bokecc.config.PropertiesObtainConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.execchain.RequestAbortedException;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StopWatch;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * https工具类
 **/

@Slf4j
@SuppressWarnings("all")
public class HttpsUtil {

    private static final String HTTP = "http";

    private static final String HTTPS = "https";

    public static final Map<String, String> HEAD = Collections.singletonMap("content-type","application/json;charset=utf-8");

    private static SSLConnectionSocketFactory sslfac = null;

    private static PoolingHttpClientConnectionManager cm = null;

    public static List<Class<? extends IOException>> list;

    static {

            list =  Arrays.asList(RequestAbortedException.class, UnknownHostException.class,  SSLException.class);

        try {

            SSLContextBuilder builder = new SSLContextBuilder();

            /** 全部信任 不做身份鉴定 **/
            builder.loadTrustMaterial(null, new TrustStrategy() {

                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                    return true;
                }
            });

            sslfac = new SSLConnectionSocketFactory(builder.build(), new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2"}, null, NoopHostnameVerifier.INSTANCE);

            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()

                    .register(HTTP, new PlainConnectionSocketFactory())

                    .register(HTTPS, sslfac)

                    .build();

            cm = new PoolingHttpClientConnectionManager(registry);

            cm.setMaxTotal(200);

            cm.setDefaultMaxPerRoute(20);

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    /**
     * 获取https客户端
     * @param retry 重试次数
     * @param protect true:幂等保护；false：不保护
     * @return org.apache.http.impl.client.CloseableHttpClient
     **/
    private static CloseableHttpClient getHttpClient(int retry, boolean protect) throws Exception {

        HttpRequestRetryHandler retryHandler;

        if(!protect){

            retryHandler = new IdempotentProtectRetryHandler(retry, false, list);
        }else{

            retryHandler = new IgnoreIdempotentRetryHandler(retry, false, list);
        }

        return HttpClients.custom()

                .setSSLSocketFactory(sslfac)

                .setRetryHandler(retryHandler)

                .setConnectionManager(cm)

                .setConnectionManagerShared(false)

                .build();
    }

    /**
     * http get 请求
     * @param url  请求url
     * @param params 参数
     * @param  header 头
     * @param retry 重试次数
     * @param timeout 超时时间
     * @return org.apache.commons.lang3.tuple.Pair<java.lang.Integer,java.lang.String>
     **/
    public static Pair<Integer, String> httpGet(String url, Map<String, String> params, Map<String, String> header, int retry, int timeout){

        return sendHttp(url, params, header, retry, timeout, 1);
    }

    public static Pair<Integer, String> httpGetForceRetry(String url, Map<String, String> params, Map<String, String> header, int retry, int timeout){

        return sendHttp(url, params, header, retry, timeout, 1);
    }

    /**
     * http delete 请求
     * @param url  请求url
     * @param params 参数
     * @param  header 头
     * @param retry 重试次数
     * @param timeout 超时时间
     * @return org.apache.commons.lang3.tuple.Pair<java.lang.Integer,java.lang.String>
     **/
    public static Pair<Integer, String> httpDeleteForceRetry(String url, Map<String, String> params, Map<String, String> header, int retry, int timeout){

        return sendHttp(url, params, header, retry, timeout, 2);
    }

    public static Pair<Integer, String> httpDelete(String url, Map<String, String> params, Map<String, String> header, int retry, int timeout){

        return sendHttp(url, params, header, retry, timeout, 2);
    }


    /**
     * http  post请求
     * @param url  请求url
     * @param entity 参数
     * @param  header 头
     * @param retry 重试次数
     * @param timeout 超时时间
     * @return org.apache.commons.lang3.tuple.Pair<java.lang.Integer,java.lang.String>
     **/
    public static Pair<Integer, String> httpPost(String url, Map<String, String> params, HttpEntity entity, Map<String, String> header, int retry, int timeout){

        return sendHttp(url, params, entity, header, retry, timeout, 2, false);
    }

    public static Pair<Integer, String> httpPut(String url, Map<String, String> params, HttpEntity entity, Map<String, String> header, int retry, int timeout){

        return sendHttp(url, params, entity, header, retry, timeout, 1, false);
    }

    /**
     * post 方法，没有幂等保护，
     * 根据配置的规则强制重试
     * @param url url
     * @param params 参数
     * @param entity body
     * @param header 头
     * @param retry 重试次数
     * @param timeout 重试
     * @return pair
     */
    public static Pair<Integer, String> httpPostForceRetry(String url, Map<String, String> params, HttpEntity entity, Map<String, String> header, int retry, int timeout){

        return sendHttp(url, params, entity, header, retry, timeout, 2, true);
    }

    /**
     * put 方法，没有幂等保护，
     * 根据配置的规则强制重试
     * @param url url
     * @param params 参数
     * @param entity body
     * @param header 头
     * @param retry 重试次数
     * @param timeout 重试
     * @return pair
     */
    public static Pair<Integer, String> httpPutForceRetry(String url, Map<String, String> params, HttpEntity entity, Map<String, String> header, int retry, int timeout){

        return sendHttp(url, params, entity, header, retry, timeout, 1, true);
    }



    /**
     *  get, delete
     * @param url url
     * @param params 参数
     * @param header 头
     * @param retry  重试
     * @param timeout 超时
     * @param method  方法 1：get方法；2：delete方法
     * @return pair
     */
    private static Pair<Integer, String> sendHttp(

            String url, Map<String, String> params,Map<String, String> header,

            int retry, int timeout, int method)
    {

        StopWatch stopWatch = new StopWatch();

        stopWatch.start(url);

        String result = null;

        CloseableHttpClient httpClient = null;

        CloseableHttpResponse response = null;

        try {

            URIBuilder uriBuilder = new URIBuilder(url);

            if(null != params && !params.isEmpty()){

                List<NameValuePair> paramsList = params.entrySet().stream().map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue())).collect(Collectors.toList());

                uriBuilder.setParameters(paramsList);
            }

            URI uri = uriBuilder.build();

            if(1 == method) {

                HttpGet httpGet = new HttpGet(uri);

                if (MapUtils.isNotEmpty(header)) {

                    header.forEach((k, v) -> httpGet.addHeader(new BasicHeader(k, v)));
                }

                RequestConfig config = RequestConfig.custom()
                        .setConnectTimeout(timeout)
                        .setConnectionRequestTimeout(timeout)
                        .setSocketTimeout(timeout)
                        .build();

                httpGet.setConfig(config);

                httpClient = getHttpClient(retry, true);

                response = httpClient.execute(httpGet);

                int status = response.getStatusLine().getStatusCode();

                if (status == HttpStatus.SC_OK) {

                    result = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
                }

                stopWatch.stop();

                log.info("{} 接口调用耗时 --> {}", url, stopWatch.shortSummary());

                return Pair.of(status, result);

            }else {

                HttpDelete httpDelete = new HttpDelete(uri);

                if (MapUtils.isNotEmpty(header)) {

                    header.forEach((k, v) -> httpDelete.addHeader(new BasicHeader(k, v)));
                }

                RequestConfig config = RequestConfig.custom()
                        .setConnectTimeout(timeout)
                        .setConnectionRequestTimeout(timeout)
                        .setSocketTimeout(timeout)
                        .build();

                httpDelete.setConfig(config);

                httpClient = getHttpClient(retry, true);

                response = httpClient.execute(httpDelete);

                int status = response.getStatusLine().getStatusCode();

                if (status == HttpStatus.SC_OK) {

                    result = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
                }

                stopWatch.stop();

                log.info("{} 接口调用耗时 --> {}", url, stopWatch.shortSummary());

                return Pair.of(status, result);
            }


        } catch (Exception e) {

            String dingUrl = PropertiesObtainConfig.env.getProperty("app-config.ding-url");

            alarm(e, dingUrl, url);

            log.error("", e);

            return Pair.of(-1, null);

        } finally {

            if(response != null) {

                try {

                    response.close();

                } catch (IOException e) {

                    log.error("关闭response异常");
                }
            }
        }

    }

    /**
     *  发送请求
     * @param url url
     * @param params url query参数
     * @param entity body 参数
     * @param header  请求头
     * @param retry   是否重试
     * @param timeout 超时时间
     * @param method 1:PUT;2:POST
     * @param forceRetry true:放开幂等保护，强制重试
     * @return pair
     */
    private static Pair<Integer, String> sendHttp(
            String url, Map<String, String> params, HttpEntity entity,
            Map<String, String> header, int retry, int timeout,
            int method, boolean forceRetry){

        StopWatch stopWatch = new StopWatch();

        stopWatch.start(url);

        String result = null;

        CloseableHttpClient httpClient;

        CloseableHttpResponse response = null;

        try {

            URIBuilder uriBuilder = new URIBuilder(url);

            if(null != params && !params.isEmpty()){

                List<NameValuePair> paramsList = params.entrySet().stream().map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue())).collect(Collectors.toList());

                uriBuilder.setParameters(paramsList);
            }

            URI uri = uriBuilder.build();

            if(1 == method){

                HttpPut httpPut= new HttpPut(uri);

                httpPut.setEntity(entity);

                if (MapUtils.isNotEmpty(header)) {

                    header.forEach((k, v) -> httpPut.addHeader(new BasicHeader(k, v)));
                }

                RequestConfig config = RequestConfig.custom()
                        .setConnectTimeout(timeout)
                        .setConnectionRequestTimeout(timeout)
                        .setSocketTimeout(timeout)
                        .build();

                httpPut.setConfig(config);

                httpClient = getHttpClient(retry, forceRetry);

                response = httpClient.execute(httpPut);

                int status = response.getStatusLine().getStatusCode();

                if (status == HttpStatus.SC_OK) {

                    result = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
                }

                stopWatch.stop();

                log.info("{} 接口调用耗时 --> {}", url, stopWatch.shortSummary());

                return Pair.of(status, result);

            }else {

                HttpPost httpPost= new HttpPost(uri);

                httpPost.setEntity(entity);

                if (MapUtils.isNotEmpty(header)) {

                    header.forEach((k, v) -> httpPost.addHeader(new BasicHeader(k, v)));
                }

                RequestConfig config = RequestConfig.custom()
                        .setConnectTimeout(timeout)
                        .setConnectionRequestTimeout(timeout)
                        .setSocketTimeout(timeout)
                        .build();

                httpPost.setConfig(config);

                httpClient = getHttpClient(retry, forceRetry);

                response = httpClient.execute(httpPost);

                int status = response.getStatusLine().getStatusCode();

                if (null != response.getEntity()) {

                    result = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
                }

                stopWatch.stop();

                log.info("{} 接口调用耗时 --> {}", url, stopWatch.shortSummary());

                return Pair.of(status, result);
            }

        } catch (Exception e) {

            log.error("", e);

            String dingUrl = PropertiesObtainConfig.env.getProperty("app-config.ding-url");

            alarm(e, dingUrl, url);

            return Pair.of(-1, null);

        } finally {

            try {

                if(null != response) {

                    response.close();
                }

            } catch (IOException e) {

                log.error("", e);
            }
        }
    }

    private static void alarm(Exception e, String dingUrl, String url){

        StringBuilder sb = new StringBuilder();
        sb.append("request url: \n ")
                .append(url)
                .append(":")
                .append(e.getClass().getName())
                .append(": ")
                .append(e.getMessage());


        DingMsg dingMsg = DingMsg
                .builder()
                .title("Error")
                .text(sb.toString())
                .build();

        Pair<Integer, String> pair = HttpsUtil.httpPost(
                dingUrl,
                new HashMap<>(1),
                new StringEntity(dingMsg.toString(), "utf-8"),
                HEAD, 3, 5000);

        log.info("return = {}", pair);
    }

    private static class IdempotentProtectRetryHandler implements HttpRequestRetryHandler {


        public static final DefaultHttpRequestRetryHandler INSTANCE = new DefaultHttpRequestRetryHandler();

        /** the number of times a method will be retried */
        private final int retryCount;

        /** Whether or not methods that have successfully sent their request will be retried */
        private final boolean requestSentRetryEnabled;

        private final Set<Class<? extends IOException>> nonRetriableClasses;

        /**
         * Create the request retry handler using the specified IOException classes
         *
         * @param retryCount how many times to retry; 0 means no retries
         * @param requestSentRetryEnabled true if it's OK to retry requests that have been sent
         * @param clazzes the IOException types that should not be retried
         * @since 4.3
         */
        IdempotentProtectRetryHandler(
                final int retryCount,
                final boolean requestSentRetryEnabled,
                final Collection<Class<? extends IOException>> clazzes) {
            super();
            this.retryCount = retryCount;
            this.requestSentRetryEnabled = requestSentRetryEnabled;
            this.nonRetriableClasses = new HashSet<Class<? extends IOException>>();
            for (final Class<? extends IOException> clazz: clazzes) {
                this.nonRetriableClasses.add(clazz);
            }
        }

        /**
         * Create the request retry handler using the following list of
         * non-retriable IOException classes: <br>
         * <ul>
         * <li>InterruptedIOException</li>
         * <li>UnknownHostException</li>
         * <li>ConnectException</li>
         * <li>SSLException</li>
         * </ul>
         * @param retryCount how many times to retry; 0 means no retries
         * @param requestSentRetryEnabled true if it's OK to retry non-idempotent requests that have been sent
         */
        @SuppressWarnings("unchecked")
        IdempotentProtectRetryHandler(final int retryCount, final boolean requestSentRetryEnabled) {
            this(retryCount, requestSentRetryEnabled, Arrays.asList(
                    InterruptedIOException.class,
                    UnknownHostException.class,
                    ConnectException.class,
                    SSLException.class));
        }

        /**
         * Create the request retry handler with a retry count of 3, requestSentRetryEnabled false
         * and using the following list of non-retriable IOException classes: <br>
         * <ul>
         * <li>InterruptedIOException</li>
         * <li>UnknownHostException</li>
         * <li>ConnectException</li>
         * <li>SSLException</li>
         * </ul>
         */
        public IdempotentProtectRetryHandler() {
            this(3, false);
        }
        /**
         * Used {@code retryCount} and {@code requestSentRetryEnabled} to determine
         * if the given method should be retried.
         */
        @Override
        public boolean retryRequest(
                final IOException exception,
                final int executionCount,
                final HttpContext context) {
            Args.notNull(exception, "Exception parameter");
            Args.notNull(context, "HTTP context");
            if (executionCount > this.retryCount) {
                // Do not retry if over max retry count
                return false;
            }
            if (this.nonRetriableClasses.contains(exception.getClass())) {
                return false;
            } else {
                for (final Class<? extends IOException> rejectException : this.nonRetriableClasses) {
                    if (rejectException.isInstance(exception)) {
                        return false;
                    }
                }
            }
            final HttpClientContext clientContext = HttpClientContext.adapt(context);
            final HttpRequest request = clientContext.getRequest();

            if(requestIsAborted(request)){
                return false;
            }

            if (handleAsIdempotent(request)) {
                // Retry if the request is considered idempotent
                return true;
            }

            if (!clientContext.isRequestSent() || this.requestSentRetryEnabled) {
                // Retry if the request has not been sent fully or
                // if it's OK to retry methods that have been sent
                return true;
            }
            // otherwise do not retry
            return false;
        }

        /**
         * @return {@code true} if this handler will retry methods that have
         * successfully sent their request, {@code false} otherwise
         */
        public boolean isRequestSentRetryEnabled() {
            return requestSentRetryEnabled;
        }

        /**
         * @return the maximum number of times a method will be retried
         */
        public int getRetryCount() {
            return retryCount;
        }

        /**
         * @since 4.2
         */
        boolean handleAsIdempotent(final HttpRequest request) {
            return !(request instanceof HttpEntityEnclosingRequest);
        }

        /**
         * @since 4.2
         *
         *
         */

        boolean requestIsAborted(final HttpRequest request) {
            HttpRequest req = request;
            if (request instanceof HttpRequestWrapper) { // does not forward request to original
                req = ((HttpRequestWrapper) request).getOriginal();
            }
            return (req instanceof HttpUriRequest && ((HttpUriRequest)req).isAborted());
        }

    }

    private static class IgnoreIdempotentRetryHandler implements HttpRequestRetryHandler {


        public static final DefaultHttpRequestRetryHandler INSTANCE = new DefaultHttpRequestRetryHandler();

        /** the number of times a method will be retried */
        private final int retryCount;

        /** Whether or not methods that have successfully sent their request will be retried */
        private final boolean requestSentRetryEnabled;

        private final Set<Class<? extends IOException>> nonRetriableClasses;

        /**
         * Create the request retry handler using the specified IOException classes
         *
         * @param retryCount how many times to retry; 0 means no retries
         * @param requestSentRetryEnabled true if it's OK to retry requests that have been sent
         * @param clazzes the IOException types that should not be retried
         * @since 4.3
         */
        IgnoreIdempotentRetryHandler(
                final int retryCount,
                final boolean requestSentRetryEnabled,
                final Collection<Class<? extends IOException>> clazzes) {
            super();
            this.retryCount = retryCount;
            this.requestSentRetryEnabled = requestSentRetryEnabled;
            this.nonRetriableClasses = new HashSet<Class<? extends IOException>>();
            for (final Class<? extends IOException> clazz: clazzes) {
                this.nonRetriableClasses.add(clazz);
            }
        }

        /**
         * Create the request retry handler using the following list of
         * non-retriable IOException classes: <br>
         * <ul>
         * <li>InterruptedIOException</li>
         * <li>UnknownHostException</li>
         * <li>ConnectException</li>
         * <li>SSLException</li>
         * </ul>
         * @param retryCount how many times to retry; 0 means no retries
         * @param requestSentRetryEnabled true if it's OK to retry non-idempotent requests that have been sent
         */
        @SuppressWarnings("unchecked")
        IgnoreIdempotentRetryHandler(final int retryCount, final boolean requestSentRetryEnabled) {
            this(retryCount, requestSentRetryEnabled, Arrays.asList(
                    InterruptedIOException.class,
                    UnknownHostException.class,
                    ConnectException.class,
                    SSLException.class));
        }

        /**
         * Create the request retry handler with a retry count of 3, requestSentRetryEnabled false
         * and using the following list of non-retriable IOException classes: <br>
         * <ul>
         * <li>InterruptedIOException</li>
         * <li>UnknownHostException</li>
         * <li>ConnectException</li>
         * <li>SSLException</li>
         * </ul>
         */
        public IgnoreIdempotentRetryHandler() {
            this(3, false);
        }
        /**
         * Used {@code retryCount} and {@code requestSentRetryEnabled} to determine
         * if the given method should be retried.
         */
        @Override
        public boolean retryRequest(
                final IOException exception,
                final int executionCount,
                final HttpContext context) {
            Args.notNull(exception, "Exception parameter");
            Args.notNull(context, "HTTP context");
            if (executionCount > this.retryCount) {
                // Do not retry if over max retry count
                return false;
            }
            if (this.nonRetriableClasses.contains(exception.getClass())) {
                return false;
            } else {
                for (final Class<? extends IOException> rejectException : this.nonRetriableClasses) {
                    if (rejectException.isInstance(exception)) {
                        return false;
                    }
                }
            }
            final HttpClientContext clientContext = HttpClientContext.adapt(context);
            final HttpRequest request = clientContext.getRequest();

            if(requestIsAborted(request)){
                return false;
            }

            if (handleAsIdempotent(request)) {
                // Retry if the request is considered idempotent
                return true;
            }

            if (!clientContext.isRequestSent() || this.requestSentRetryEnabled) {
                // Retry if the request has not been sent fully or
                // if it's OK to retry methods that have been sent
                return true;
            }
            // otherwise do not retry
            return false;
        }

        /**
         * @return {@code true} if this handler will retry methods that have
         * successfully sent their request, {@code false} otherwise
         */
        public boolean isRequestSentRetryEnabled() {
            return requestSentRetryEnabled;
        }

        /**
         * @return the maximum number of times a method will be retried
         */
        public int getRetryCount() {
            return retryCount;
        }

        /**
         * @since 4.2
         */
        protected boolean handleAsIdempotent(final HttpRequest request) {
            return true;
        }


        /**
         * @since 4.2
         *
         *
         */

        boolean requestIsAborted(final HttpRequest request) {
            HttpRequest req = request;
            if (request instanceof HttpRequestWrapper) { // does not forward request to original
                req = ((HttpRequestWrapper) request).getOriginal();
            }
            return (req instanceof HttpUriRequest && ((HttpUriRequest)req).isAborted());
        }

    }
}
