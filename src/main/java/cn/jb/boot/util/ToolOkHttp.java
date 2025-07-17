package cn.jb.boot.util;

import okhttp3.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;


public class ToolOkHttp {//org.apache.logging.log4j.LogManager
    // main函数中slf4j无法正常显示,这里暂时使用log4j2输出日志,迁移到web项目时,建议使用slf4j
//	private static final Logger LOG = LoggerFactory.getLogger(OkHttp4Test.class);
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger();
    private static final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    private static final long MAX_CONN_TIMEOUT = 60;    // 设置连接主机超时时间,默认10秒,0表示没有超时
    private static final long MAX_READ_TIMEOUT = 60;    // 设置从主机读取数据超时时间,默认10秒,0表示没有超时,建议设置比connectTimeout时间稍长
    private static final long MAX_WRITE_TIMEOUT = 60;   // 设置新连接的默认写入超时时间,默认10秒,0表示没有超时

    /**
     * post文件请求类型:application/json;
     */
    public static final String CONTENT_TYPET_JSON = "application/json;";
    /**
     * post文件请求类型:application/xml;
     */
    public static final String CONTENT_TYPE_XML = "application/xml;";
    /**
     * post文件请求类型:text/xml;
     */
    public static final String CONTENT_TYPE_TEXT_XML = "text/xml;";


    // ==================== send get start ====================

    /**
     * <h5>功能:发送一个普通get请求</h5>
     *
     * @param url 请求地址
     * @return
     */
    public static String sendGet(String url) {
        // 参数为""或者new HashMap<String, Object>()
        return sendGet(url, new HashMap<String, Object>());
    }

    /**
     * <h5>功能:发送一个普通get请求</h5>
     *
     * @param url       请求地址
     * @param headerMap header头信息
     * @param paramsMap 请求数据信息
     * @return
     */
    public static String sendGet(String url, Map<String, Object> paramsMap) {
        StringBuffer sbf = new StringBuffer();
        String paramStr = null;
        if (null != paramsMap && paramsMap.size() > 0) {
            for (String key : paramsMap.keySet()) {
                sbf.append(key + "=" + paramsMap.get(key));
                sbf.append("&");
            }
            paramStr = sbf.toString().substring(0, sbf.length() - 1);
        }
        // 发送一个普通get请求
        return sendGet(url, paramStr);
    }

    /**
     * <h5>功能:发送一个普通get请求</h5>
     *
     * @param url      请求地址
     * @param paramStr 请求数据信息
     * @return
     */
    public static String sendGet(String url, String paramStr) {
        // 发送一个普通get请求
        return sendGet(url, null, paramStr);
    }

    /**
     * <h5>功能:发送一个普通get请求</h5>
     *
     * @param url       请求地址
     * @param headerMap header头信息
     * @param paramsMap 请求数据信息
     * @return
     */
    public static String sendGet(String url, Map<String, String> headerMap, Map<String, Object> paramsMap) {
        // 打印请求信息
        printRequestInfoLog("get", url, headerMap, paramsMap, null, null, null, null);

        StringBuffer sbf = new StringBuffer();
        String paramStr = null;
        if (null != paramsMap && paramsMap.size() > 0) {
            for (String key : paramsMap.keySet()) {
                sbf.append(key + "=" + paramsMap.get(key));
                sbf.append("&");
            }
            paramStr = sbf.toString().substring(0, sbf.length() - 1);
        }
        // 发送一个普通get请求
        return sendGet(url, headerMap, paramStr);
    }

    /**
     * <h5>功能:发送一个普通get请求</h5>
     *
     * @param url       请求地址
     * @param headerMap header头信息
     * @param paramStr  请求数据信息
     * @return
     */
    public static String sendGet(String url, Map<String, String> headerMap, String paramStr) {
        // 打印请求信息
        printRequestInfoLog("get", url, headerMap, null, paramStr, null, null, null);

        // 1.组合url请求地址
        if (null != paramStr && paramStr.length() > 0) {
            url += ("?" + paramStr);
        }
        // 2.创建OkHttpClient对象
        OkHttpClient client = getClient(headerMap);
        // 3.设置头部信息
        Headers headers = setHeaders(headerMap);
        // 4.创建Request
        Request request = new Request.Builder().url(url).headers(headers).build();
        // 5.发送请求并返回请求结果
        return sendRequest("get", url, client, headers, request);
    }
    // ==================== send get end ====================

    // ==================== send post start ====================

    /**
     * <h5>功能:发送一个普通post请求</h5>
     *
     * @param url       请求地址
     * @param paramsMap 请求数据信息
     * @return
     */
    public static String sendPost(String url, Map<String, Object> paramsMap) {
        return sendPost(url, null, paramsMap);
    }

    /**
     * <h5>功能:发送一个普通post请求</h5>
     *
     * @param url       请求地址
     * @param headerMap header头信息
     * @param paramsMap 请求数据信息
     * @return
     */
    public static String sendPost(String url, Map<String, String> headerMap, Map<String, Object> paramsMap) {
        // 打印请求信息
        printRequestInfoLog("post", url, headerMap, paramsMap, null, null, null, null);

        // 1.创建OkHttpClient对象
        OkHttpClient client = getClient(headerMap);
        // 2.设置头部信息
        Headers headers = setHeaders(headerMap);
        // 3.添加参数到form表单,Content-Type默认为application/x-www-form-urlencoded
        FormBody formBody = setFormBody(paramsMap);
        // 4.创建Request
        Request request = new Request.Builder().url(url).headers(headers).post(formBody).build();
        // 5.发送请求并返回请求结果
        return sendRequest("post", url, client, headers, request);
    }

    /**
     * <h5>功能:发送一个普通post请求</h5>
     *
     * @param url      请求地址
     * @param paramStr 请求数据信息
     * @return
     */
    public static String sendPost(String url, String paramStr) {
        return sendPost(url, null, paramStr, null);
    }

    /**
     * <h5>功能:发送一个普通post请求</h5>
     *
     * @param url       请求地址
     * @param headerMap header头信息
     * @param paramStr  请求数据信息
     * @return
     */
    public static String sendPost(String url, Map<String, String> headerMap, String paramStr) {
        return sendPost(url, headerMap, paramStr, null);
    }

    /**
     * <h5>功能:发送一个普通post请求</h5>
     *
     * @param url     请求地址
     * @param jsonStr 请求数据信息,JSON格式
     * @return
     */
    public static String sendPostJson(String url, String jsonStr) {
        return sendPostJson(url, null, jsonStr);
    }

    /**
     * <h5>功能:发送一个普通post请求</h5>
     *
     * @param url    请求地址
     * @param xmlStr 请求数据信息,XML格式
     * @return
     */
    public static String sendPostXml(String url, String xmlStr) {
        return sendPostXml(url, null, xmlStr);
    }

    /**
     * <h5>功能:发送一个普通post请求</h5>
     *
     * @param url       请求地址
     * @param headerMap header头信息
     * @param jsonStr   请求数据信息,JSON格式
     * @return
     */
    public static String sendPostJson(String url, Map<String, String> headerMap, String jsonStr) {
        return sendPost(url, headerMap, jsonStr, CONTENT_TYPET_JSON);
    }

    /**
     * <h5>功能:发送一个普通post请求</h5>
     *
     * @param url       请求地址
     * @param headerMap header头信息
     * @param xmlStr    请求数据信息,XML格式
     * @return
     */
    public static String sendPostXml(String url, Map<String, String> headerMap, String xmlStr) {
        return sendPost(url, null, xmlStr, CONTENT_TYPE_XML);
    }

    /**
     * <h5>功能:发送一个普通post请求</h5>
     *
     * @param url         请求地址
     * @param headerMap   header头信息
     * @param paramStr    请求数据信息
     * @param contentType post数据提交方式,默认"application/x-www-form-urlencoded",其他:"application/json;","application/xml;"
     * @return
     */
    public static String sendPost(String url, Map<String, String> headerMap, String paramStr, String contentType) {
        // 打印请求信息
        printRequestInfoLog("post", url, headerMap, null, paramStr, contentType, null, null);

        // 1.创建OkHttpClient对象
        OkHttpClient client = getClient(headerMap);
        // 2.设置头部信息
        Headers headers = setHeaders(headerMap);
        // 3.创建一个请求体并赋值
        MediaType mediaTypem = null;
        if (null == contentType || "".equals(contentType)) {
            mediaTypem = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
        } else {
            mediaTypem = MediaType.parse(contentType + " charset=utf-8");
        }
        System.out.println(paramStr);
        RequestBody requestBody = RequestBody.create(mediaTypem, paramStr);
        // 4.创建Request
        Request request = new Request.Builder().url(url).headers(headers).post(requestBody).build();
        // 5.发送请求并返回请求结果
        return sendRequest("post", url, client, headers, request);
    }
    // ==================== send post end ====================

    // ==================== send file start ====================

    /**
     * <h5>功能:文件请求</h5>
     *
     * @param url      请求地址
     * @param filesMap 文件信息
     * @param mimeType 文件类型,可参考https://www.w3school.com.cn/media/media_mimeref.asp
     * @return
     */
    public static String postFile(String url, Map<String, File> filesMap, String mimeType) {
        return postMultipart(url, null, null, filesMap, mimeType);
    }

    /**
     * <h5>功能:混合参数和文件请求</h5>
     *
     * @param url       请求地址
     * @param paramsMap 请求数据信息
     * @param filesMap  文件信息
     * @param mimeType  文件类型,可参考https://www.w3school.com.cn/media/media_mimeref.asp
     * @return
     */
    public static String postMultipart(String url, Map<String, Object> paramsMap, Map<String, File> filesMap, String mimeType) {
        return postMultipart(url, null, paramsMap, filesMap, mimeType);
    }

    /**
     * <h5>功能:混合参数和文件请求</h5>
     *
     * @param url       请求地址
     * @param headerMap header头信息
     * @param paramsMap 请求数据信息
     * @param filesMap  文件信息
     * @param mimeType  文件类型,可参考https://www.w3school.com.cn/media/media_mimeref.asp
     * @return
     */
    public static String postMultipart(String url, Map<String, String> headerMap, Map<String, Object> paramsMap,
                                       Map<String, File> filesMap, String mimeType) {
        // 打印请求信息
        printRequestInfoLog("post", url, headerMap, paramsMap, null, null, filesMap, mimeType);

        // 1.创建OkHttpClient对象
        OkHttpClient client = getClient(headerMap);
        // 2.设置头部信息
        Headers headers = setHeaders(headerMap);
        // 3.构建多部件builder
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.ALTERNATIVE);
        // 4.获取参数并放到请求体中
        if (null != paramsMap) {
            for (String key : paramsMap.keySet()) {
                bodyBuilder.addFormDataPart(key, null == paramsMap.get(key) ? null : paramsMap.get(key).toString());
            }
        }
        // 5.添加文件集合
        if (null != filesMap) {
            for (String key : filesMap.keySet()) {
                File file = filesMap.get(key);
                bodyBuilder.addFormDataPart(key, file.getName(), RequestBody.create(MediaType.parse(mimeType), file));
            }
        }
        // 6.创建Request
        Request request = new Request.Builder().url(url).post(bodyBuilder.build()).build();
        // 7.发送请求并返回请求结果
        return sendRequest("post", url, client, headers, request);
    }
    // ==================== send file end ====================

    // ==================== private method ====================

    /**
     * <h5>功能:创建OkHttpClient对象</h5>
     *
     * @return
     */
    private static OkHttpClient getClient(Map<String, String> headerMap) {
        return buildOKHttpClient()
                .connectTimeout(MAX_CONN_TIMEOUT, TimeUnit.SECONDS)    // 设置连接主机超时时间,默认10秒,0表示没有超时
                .readTimeout(MAX_READ_TIMEOUT, TimeUnit.SECONDS)    // 设置从主机读取数据超时时间,默认10秒,0表示没有超时
                .writeTimeout(MAX_WRITE_TIMEOUT, TimeUnit.SECONDS)    // 设置新连接的默认写入超时时间,默认10秒,0表示没有超时
                .cookieJar(new CookieJar() {//OkHttp可以不用我们管理Cookie，自动携带，保存和更新Cookie。 方法是在创建OkHttpClient设置管理Cookie的CookieJar
                    @Override
                    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                        if (headerMap != null && headerMap.size() > 0) {
                            //是否保存Cookie 1：保存 0：不保存
                            String bSaveCookie = headerMap.containsKey("bSaveCookie") ? headerMap.get("bSaveCookie") : "0";
                            if (bSaveCookie.equals("1")) {
                                cookieStore.put(httpUrl.host(), list);
                            }
                        }
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                        List<Cookie> cookies = cookieStore.get(httpUrl.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
                .retryOnConnectionFailure(true)
                .build();
    }

    public static OkHttpClient.Builder buildOKHttpClient() {
        try {
            TrustManager[] trustAllCerts = buildTrustManagers();
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
            return builder;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
            return new OkHttpClient.Builder();
        }
    }

    private static TrustManager[] buildTrustManagers() {
        return new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }
                }
        };
    }

    /**
     * <h5>功能:设置头部信息</h5>
     *
     * @param headerMap
     * @return
     */
    private static Headers setHeaders(Map<String, String> headerMap) {
        Headers.Builder headersBuilder = new Headers.Builder();
        if (null != headerMap) {
            for (String key : headerMap.keySet()) {
                headersBuilder.add(key, headerMap.get(key));
            }
        }
        return headersBuilder.build();
    }

    /**
     * <h5>功能:添加参数到form表单</h5>
     * FormBody继承了RequestBody,它已经指定了数据类型Content-Type默认为application/x-www-form-urlencoded
     *
     * @param paramsMap 请求数据信息
     * @return
     */
    private static FormBody setFormBody(Map<String, Object> paramsMap) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if (null != paramsMap) {
            // 4. 添加参数到表单
            for (String key : paramsMap.keySet()) {
                formBodyBuilder.add(key, null == paramsMap.get(key) ? null : paramsMap.get(key).toString());
            }
        }
        return formBodyBuilder.build();
    }

    /**
     * <h5>功能:发送请求并返回请求结果</h5>
     *
     * @param sendType 请求类型,get或者post
     * @param url      请求地址
     * @param client   OkHttpClient对象
     * @param headers  header信息
     * @param request  请求对象
     * @return
     */
    private static String sendRequest(String sendType, String url, OkHttpClient client, Headers headers, Request request) {
        String responseBody = null;
        Response response = null;
        try {
            // 5.同步请求并获取Response
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                // 6.获取网络请求返回内容
                responseBody = response.body().string();
                LOG.warn("{}请求[{}]返回数据为[{}]", sendType, url, responseBody);
                System.out.println(sendType + "请求[" + url + "]返回数据为[" + responseBody + "]");
            } else {
                LOG.warn("code[{}], url[{}], returnBody[{}]", response.code(), request.url(),
                        null == request.body() ? "" : request.body().toString());
            }
        } catch (IOException e) {
            formatException(sendType, e, url, headers);
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return responseBody;
    }

    /**
     * <h5>功能:异常处理</h5>
     *
     * @param e   异常
     * @param url 请求地址
     */
    private static void formatException(String sendType, IOException e, String url, Headers headers) {
        if (e instanceof ConnectException) {
            LOG.error("发送[{}]请求[{}]连接主机超时,headers[{}]", sendType, url, headers.toString());
            e.printStackTrace();
            System.out.println("发送" + sendType + "请求[" + url + "]连接主机超时");
        } else if (e instanceof SocketTimeoutException) {
            LOG.error("发送[{}]请求[{}]从主机读取数据超时", sendType, url, headers.toString());
            e.printStackTrace();
            System.out.println("发送" + sendType + "请求[" + url + "]从主机读取数据超时");
        } else {
            LOG.error("发送[{}]请求[{}]异常", sendType, url, headers.toString());
            e.printStackTrace();
            System.out.println("发送" + sendType + "请求[" + url + "]异常");
        }
    }

    /**
     * <h5>功能:打印请求信息</h5>
     *
     * @param requestType 请求类型
     * @param url         请求地址
     * @param headerMap   header信息
     * @param paramsMap   请求数据信息
     * @param paramStr    请求数据信息,字符串类型
     * @param contentType post数据提交方式
     * @param filesMap    文件信息
     * @param mimeType    文件类型
     */
    private static void printRequestInfoLog(String requestType, String url, Map<String, String> headerMap,
                                            Map<String, Object> paramsMap, String paramStr, String contentType, Map<String, File> filesMap,
                                            String mimeType) {
        StringBuffer requestInfo = new StringBuffer();
        requestInfo.append("send[" + requestType + "]request,");
        requestInfo.append("url[" + url + "],");
        if (null != headerMap) {
            requestInfo.append("header[" + headerMap.toString() + "],");
        }
        if (null != paramsMap) {
            requestInfo.append("params[" + paramsMap.toString() + "],");
        }
        if (null != paramStr) {
            requestInfo.append("paramStr[" + paramStr + "],");
        }
        if (null != contentType) {
            requestInfo.append("contentType[" + contentType + "],");
        }
        if (null != filesMap) {
            requestInfo.append("files[" + filesMap.toString() + "],");
        }
        if (null != mimeType) {
            requestInfo.append("mimeType[" + mimeType + "]");
        }
        LOG.info(requestInfo.toString());
    }
}
