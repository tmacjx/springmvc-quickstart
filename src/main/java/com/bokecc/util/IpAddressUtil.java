package com.bokecc.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * IP工具类
 **/
@Slf4j
@Component
public class IpAddressUtil {

    private static final String LOCALHOST_FORMAT = "127.0.0.1";

    private static Pattern localhostPattern = Pattern.compile(LOCALHOST_FORMAT);

    /**
     * 获取Ip4地址. Support Windows/Linux
     *
     * @return ip4
     */
    public static String getLocalIP4Address() {
        String localIP4Address = "";
        Enumeration<NetworkInterface> netInterfaces;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) netInterfaces
                        .nextElement();
                Enumeration<InetAddress> addresses = netInterface
                        .getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = (InetAddress) addresses.nextElement();
                    if (ip != null && ip instanceof Inet4Address
                            && ip.getHostAddress().indexOf(".") != -1) {
                        localIP4Address = ip.getHostAddress().trim();
                        if (!localhostPattern.matcher(localIP4Address).find()) {
                            return localIP4Address;
                        }
                    }
                }// end while
            }// end while
        } catch (SocketException e) {
            log.error(e.getMessage());
        }
        return localIP4Address;
    }

}
