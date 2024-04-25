package cn.com.finance.ema.utils;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;

/**
 * @date: 2021/05/18 14:36
 * @author: zhang_sir
 * @version: 1.0
 */
public class HttpClientHelper {

    private static final int MAX_TOTAL_CONNECTIONS = 200;
    private static final int MAX_CONNECTIONS_PER_ROUTE = 200;
    private static final int SOCKET_TIMEOUT = 120000; //读超时时间
    private static final int CONNECT_TIMEOUT = 120000;   //连接超时时间
    private static final int CONNECTION_REQUEST_TIMEOUT = 120010; //从池中获取连接超时时间

    private CloseableHttpClient httpClient;

    private HttpClientHelper() {
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(SSLContexts.createSystemDefault()))
                .build();
        //初始化PoolingHttpClientConnectionManager
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        cm.setMaxTotal(MAX_TOTAL_CONNECTIONS);// 整个连接池最大连接数
        cm.setDefaultMaxPerRoute(MAX_CONNECTIONS_PER_ROUTE);// 每路由最大连接数，默认值是2

        // Create socket configuration
        SocketConfig socketConfig = SocketConfig.custom()
                .setTcpNoDelay(true)
                .setSoReuseAddress(true)
                .build();
        // Configure the connection manager to use socket configuration either
        // by default or for a specific host.
        cm.setDefaultSocketConfig(socketConfig);

        //RequestConfig
        RequestConfig config = RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT)
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT).build();

        httpClient = HttpClients.custom().setConnectionManager(cm).setDefaultRequestConfig(config).build();
    }

    /**
     * 获取实例
     */
    public static HttpClientHelper getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * 通过连接池获取HttpClient
     */
    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * 通过静态内部类实现单例
     */
    private static class SingletonHolder {
        static final HttpClientHelper instance = new HttpClientHelper();
    }
}
