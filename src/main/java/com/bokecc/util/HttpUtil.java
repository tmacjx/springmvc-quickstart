package com.bokecc.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.execchain.RequestAbortedException;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * http 工具类
 */
@Slf4j
public class HttpUtil {

    public static final int HTTP_RETRY = 1;

    public static final int HTTP_TIMEOUT = 3 * 1000;

    public static Constructor<DefaultHttpRequestRetryHandler> constructor;

    public static List<Class> list;


    static{

        log.info("initialize the RetryHandler......");

        try {

            constructor = DefaultHttpRequestRetryHandler.class.getDeclaredConstructor(int.class, boolean.class, Collection.class);

        } catch (NoSuchMethodException e) {

            log.error("", e);
        }

        constructor.setAccessible(true);

        list =  Arrays.asList(RequestAbortedException.class, UnknownHostException.class, ConnectException.class, SSLException.class);
    }

    public static Pair<Integer, String> httpGet(String url, Map<String, String> param, Map<String, String> header, int retry, int timeout) {

        DefaultHttpRequestRetryHandler retryHandler = null;
        try {
            retryHandler = constructor.newInstance(retry, false, list);

        } catch (Exception e) {

            log.error("", e);
        }

        CloseableHttpClient httpclient = HttpClients.custom().setRetryHandler(retryHandler).build();

        String result = null;
	    try {
                List<NameValuePair> params = param.entrySet().stream().map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue())).collect(Collectors.toList());

                URI uri = new URIBuilder(url)
                        .setParameters(params)
                        .build();
                HttpGet get = new HttpGet(uri);
                if (MapUtils.isNotEmpty(header)) {
                    header.forEach((k, v) -> get.addHeader(new BasicHeader(k, v)));
                }

                RequestConfig config = RequestConfig.custom()
                        .setConnectTimeout(timeout)
                        .setConnectionRequestTimeout(timeout)
                        .setSocketTimeout(timeout)
                        .build();
                get.setConfig(config);

                HttpResponse response = httpclient.execute(get);
                int status = response.getStatusLine().getStatusCode();
                if (status == HttpStatus.SC_OK) {
                    result = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
                }

                return Pair.of(status, result);

        } catch (Exception e) {
            log.error("", e);
            return Pair.of(-1, null);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                log.error("", e);
            }
        }
    }

    public static Pair<Integer, String> octoHttpGet(String url, Map<String, String> param, String octoToken, Map<String, String> header, int retry, int timeout) {
        Map<String, String> _param = null == param ? new HashMap<>() : new HashMap<>(param);
        _param.put("_octo", octoToken);

	    return httpGet(url, _param, header, retry, timeout);
    }

    public static Pair<Integer, String> octoHttpGet(String url, Map<String, String> param, String octoToken) {
        return octoHttpGet(url, param, octoToken, null, HTTP_RETRY, HTTP_TIMEOUT);
    }

    public static Pair<Integer, String> httpPost(String url, Map<String, Object> jsonData, Map<String, String> header, int retry, int timeout) {
        CloseableHttpClient httpclient = HttpClients.custom().setRetryHandler(new DefaultHttpRequestRetryHandler(retry, false)).build();
        String result = null;

        try {
            URI uri = new URIBuilder(url)
                    .build();
            HttpPost post = new HttpPost(uri);

            List<NameValuePair> list = new ArrayList<>();
	        for(Map.Entry<String, Object> entry : jsonData.entrySet()) {
                list.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            }

            UrlEncodedFormEntity httpEntity = null;
            try {
                httpEntity = new UrlEncodedFormEntity(list, Charset.forName("UTF-8"));
            } catch (Exception e) {
                log.error("new httpEntity failed;" + e.getMessage());
                return Pair.of(-10, null);
            }

            post.setEntity(httpEntity);

            if (MapUtils.isNotEmpty(header)) {
                header.forEach((k, v) -> post.addHeader(new BasicHeader(k, v)));
            }

            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(timeout)
                    .setConnectionRequestTimeout(timeout)
                    .setSocketTimeout(timeout)
                    .build();

            post.setConfig(config);

            HttpResponse response = httpclient.execute(post);
            int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
            }

            return Pair.of(status, result);


        } catch (Exception e) {
            log.error("url:" + url, e);
            return Pair.of(-1, null);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                log.error("", e);
            }
        }

    }

    public static Pair<Integer, String> httpPost(String url, HttpEntity entity, Map<String, String> header, int retry, int timeout) {
        CloseableHttpClient httpclient = HttpClients.custom().setRetryHandler(new DefaultHttpRequestRetryHandler(retry, false)).build();
        String result = null;

        try {
                URI uri = new URIBuilder(url)
                        .build();
                HttpPost post = new HttpPost(uri);

                post.setEntity(entity);

                if (MapUtils.isNotEmpty(header)) {
                    header.forEach((k, v) -> post.addHeader(new BasicHeader(k, v)));
                }

                RequestConfig config = RequestConfig.custom()
                        .setConnectTimeout(timeout)
                        .setConnectionRequestTimeout(timeout)
                        .setSocketTimeout(timeout)
                        .build();

                post.setConfig(config);

                HttpResponse response = httpclient.execute(post);
                int status = response.getStatusLine().getStatusCode();
                if (status == HttpStatus.SC_OK) {
                    result = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
                }

                return Pair.of(status, result);


        } catch (Exception e) {
            log.error("url:" + url, e);
            return Pair.of(-1, null);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                log.error("", e);
            }
        }

    }

    public static Pair<Integer, String> httpPut(String url, HttpEntity entity, Map<String, String> header, int retry, int timeout) {
        CloseableHttpClient httpclient = HttpClients.custom().setRetryHandler(new DefaultHttpRequestRetryHandler(retry, false)).build();
        String result = null;

        try {

            URI uri = new URIBuilder(url)
                    .build();
            HttpPut put = new HttpPut(uri);
            put.setEntity(entity);

            if (MapUtils.isNotEmpty(header)) {
                header.forEach((k, v) -> put.addHeader(new BasicHeader(k, v)));
            }

            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(timeout)
                    .setConnectionRequestTimeout(timeout)
                    .setSocketTimeout(timeout)
                    .build();

            put.setConfig(config);

            HttpResponse response = httpclient.execute(put);
            int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
            }

            return Pair.of(status, result);


        } catch (Exception e) {
            log.error("url:" + url, e);
            return Pair.of(-1, null);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                log.error("", e);
            }
        }
    }

	public static Pair<Integer, String> httpDelete(String url, HttpEntity entity, Map<String, String> header, int retry, int timeout) {
		CloseableHttpClient httpclient = HttpClients.custom().setRetryHandler(new DefaultHttpRequestRetryHandler(retry, false)).build();
		String result = null;

		try {

			URI uri = new URIBuilder(url)
					.build();
			HttpDelete delete = new HttpDelete(uri);


			if (MapUtils.isNotEmpty(header)) {
				header.forEach((k, v) -> delete.addHeader(new BasicHeader(k, v)));
			}

			RequestConfig config = RequestConfig.custom()
					.setConnectTimeout(timeout)
					.setConnectionRequestTimeout(timeout)
					.setSocketTimeout(timeout)
					.build();

			delete.setConfig(config);

			HttpResponse response = httpclient.execute(delete);
			int status = response.getStatusLine().getStatusCode();
			if (status == HttpStatus.SC_OK) {
				result = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
			}

			return Pair.of(status, result);


		} catch (Exception e) {
            log.error("url:" + url, e);
            return Pair.of(-1, null);
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
                log.error("", e);
            }
		}
	}




    public static Pair<Integer, String> octoHttpPost(String url, String octoToken, HttpEntity entity, Map<String, String> header, int retry, int timeout) {
        String finalUrl = null;
        try {
            finalUrl = new URIBuilder(url).addParameter("_octo", octoToken).build().toString();
        } catch (URISyntaxException e) {
            log.error("", e);
            return Pair.of(-1, null);
        }

        return httpPost(finalUrl, entity, header, retry, timeout);
    }

    public static Pair<Integer, String> octoHttpPut(String url, String octoToken, HttpEntity entity, Map<String, String> header, int retry, int timeout) {
        String finalUrl = null;
        try {
            finalUrl = new URIBuilder(url).addParameter("_octo", octoToken).build().toString();
        } catch (URISyntaxException e) {
            log.error("", e);
            return Pair.of(-1, null);
        }

        return httpPut(finalUrl, entity, header, retry, timeout);
    }


	public static Pair<Integer, String> octoHttpDelete(String url, String octoToken, HttpEntity entity, Map<String, String> header, int retry, int timeout) {
		String finalUrl = null;
		try {
			finalUrl = new URIBuilder(url).addParameter("_octo", octoToken).build().toString();
		} catch (URISyntaxException e) {
            log.error("", e);
            return Pair.of(-1, null);
		}

		return httpDelete(finalUrl, entity, header, retry, timeout);
	}


    public static Pair<Integer, String> octoHttpPost(String url, String octoToken, HttpEntity entity) {
        return octoHttpPost(url, octoToken, entity, null, HTTP_RETRY, HTTP_TIMEOUT);
    }

    public static Pair<Integer, String> octoHttpPut(String url, String octoToken, HttpEntity entity) {
        return octoHttpPut(url, octoToken, entity, null, HTTP_RETRY, HTTP_TIMEOUT);
    }

    /**
     * ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     */

    public static <T> T httpGet(String url, Map<String, String> param, Map<String, String> header, int retry, int timeout, ResponseHandler<T> responseHandler) throws Exception {
        T result = null;
        CloseableHttpClient httpclient = HttpClients.custom().setRetryHandler(new DefaultHttpRequestRetryHandler(retry, false)).build();

        try {


                List<NameValuePair> params = param.entrySet().stream().map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue())).collect(Collectors.toList());

                URI uri = new URIBuilder(url)
                        .setParameters(params)
                        .build();
                HttpGet get = new HttpGet(uri);
                if (MapUtils.isNotEmpty(header)) {
                    header.forEach((k, v) -> get.addHeader(new BasicHeader(k, v)));
                }

                RequestConfig config = RequestConfig.custom()
                        .setConnectTimeout(timeout)
                        .setConnectionRequestTimeout(timeout)
                        .setSocketTimeout(timeout)
                        .build();
                get.setConfig(config);


                result = httpclient.execute(get, responseHandler);

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            httpclient.close();
        }

        return result;
    }

    public static <T> T httpPost(String url, HttpEntity entity, Map<String, String> header, int retry, int timeout, ResponseHandler<T> responseHandler) throws Exception {
        T result = null;
        CloseableHttpClient httpclient = HttpClients.custom().setRetryHandler(new DefaultHttpRequestRetryHandler(retry, false)).build();

        try {

                URI uri = new URIBuilder(url)
                        .build();
                HttpPost post = new HttpPost(uri);
                post.setEntity(entity);

                if (MapUtils.isNotEmpty(header)) {
                    header.forEach((k, v) -> post.addHeader(new BasicHeader(k, v)));
                }

                RequestConfig config = RequestConfig.custom()
                        .setConnectTimeout(timeout)
                        .setConnectionRequestTimeout(timeout)
                        .setSocketTimeout(timeout)
                        .build();

                post.setConfig(config);

                result = httpclient.execute(post, responseHandler);

        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            httpclient.close();
        }

        return result;
    }

    public static <T> T octoHttpGet(String url, Map<String, String> param, String octoToken,  Map<String, String> header, int retry, int timeout, ResponseHandler<T> responseHandler) throws Exception {
        Map<String, String> _param = null == param ? new HashMap<>() : new HashMap<>(param);
        _param.put("_octo", octoToken);

        return httpGet(url, _param, header, retry, timeout, responseHandler);
    }

    public static <T> T octoHttpGet(String url, Map<String, String> param, String octoToken, ResponseHandler<T> responseHandler) throws Exception {
        return octoHttpGet(url, param, octoToken, null, HTTP_RETRY, HTTP_TIMEOUT, responseHandler);
    }

    public static <T> T octoHttpPost(String url, String octoToken, HttpEntity entity, Map<String, String> header, int retry, int timeout, ResponseHandler<T> responseHandler) throws Exception {
        String finalUrl = new URIBuilder(url).addParameter("_octo", octoToken).build().toString();

        return httpPost(finalUrl, entity, header, retry, timeout, responseHandler);
    }

    public static <T> T octoHttpPost(String url, String octoToken, HttpEntity entity, ResponseHandler<T> responseHandler) throws Exception {
        return octoHttpPost(url, octoToken, entity, null, HTTP_RETRY, HTTP_TIMEOUT, responseHandler);
    }


}
