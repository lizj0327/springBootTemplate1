package com.tmp.jpa.data;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.http.MediaType;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.net.HttpHeaders;
import com.tmp.util.Collections3;
import com.tmp.util.EncodeUtil;
import com.tmp.util.JsonMapperUtil;

/**
 * Http与Servlet工具类.
 */
public class Servlets {

    // -- 常用数值定义 --//
    /**
     * 一年换算为妙
     */
    public static final long ONE_YEAR_SECONDS = 60 * 60 * 24 * 365;

    /**
     * 设置客户端缓存过期时间 的Header.
     */
    public static void setExpiresHeader(HttpServletResponse response, long expiresSeconds) {
        // Http 1.0 header, set a fix expires date.
        response.setDateHeader(HttpHeaders.EXPIRES, System.currentTimeMillis() + (expiresSeconds * 1000));
        // Http 1.1 header, set a time after now.
        response.setHeader(HttpHeaders.CACHE_CONTROL, "private, max-age=" + expiresSeconds);
    }

    /**
     * 设置禁止客户端缓存的Header.
     */
    public static void setNoCacheHeader(HttpServletResponse response) {
        // Http 1.0 header
        response.setDateHeader(HttpHeaders.EXPIRES, 1L);
        response.addHeader(HttpHeaders.PRAGMA, "no-cache");
        // Http 1.1 header
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, max-age=0");
    }

    /**
     * 设置LastModified Header.
     */
    public static void setLastModifiedHeader(HttpServletResponse response, long lastModifiedDate) {
        response.setDateHeader(HttpHeaders.LAST_MODIFIED, lastModifiedDate);
    }

    /**
     * 设置Etag Header.
     */
    public static void setEtag(HttpServletResponse response, String etag) {
        response.setHeader(HttpHeaders.ETAG, etag);
    }

    /**
     * 根据浏览器If-Modified-Since Header, 计算文件是否已被修改.
     * <p>
     * 如果无修改, checkIfModify返回false ,设置304 not modify status.
     *
     * @param lastModified 内容的最后修改时间.
     */
    public static boolean checkIfModifiedSince(HttpServletRequest request, HttpServletResponse response, long lastModified) {
        long ifModifiedSince = request.getDateHeader(HttpHeaders.IF_MODIFIED_SINCE);
        if ((ifModifiedSince != -1) && (lastModified < (ifModifiedSince + 1000))) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return false;
        }
        return true;
    }

    /**
     * 根据浏览器 If-None-Match Header, 计算Etag是否已无效.
     * <p>
     * 如果Etag有效, checkIfNoneMatch返回false, 设置304 not modify status.
     *
     * @param etag 内容的ETag.
     */
    public static boolean checkIfNoneMatchEtag(HttpServletRequest request, HttpServletResponse response, String etag) {
        String headerValue = request.getHeader(HttpHeaders.IF_NONE_MATCH);
        if (headerValue != null) {
            boolean conditionSatisfied = false;
            if (!"*".equals(headerValue)) {
                StringTokenizer commaTokenizer = new StringTokenizer(headerValue, ",");

                while (!conditionSatisfied && commaTokenizer.hasMoreTokens()) {
                    String currentToken = commaTokenizer.nextToken();
                    if (currentToken.trim().equals(etag)) {
                        conditionSatisfied = true;
                    }
                }
            } else {
                conditionSatisfied = true;
            }

            if (conditionSatisfied) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                response.setHeader(HttpHeaders.ETAG, etag);
                return false;
            }
        }
        return true;
    }

    /**
     * 设置让浏览器弹出下载对话框的Header.
     *
     * @param fileName 下载后的文件名.
     */
    public static void setFileDownloadHeader(HttpServletRequest request, HttpServletResponse response, String fileName) {
        // 中文文件名支持
        String encodedfileName = null;
        // 替换空格，否则firefox下有空格文件名会被截断,其他浏览器会将空格替换成+号
        encodedfileName = fileName.trim().replaceAll(" ", "_");
        String agent = request.getHeader("User-Agent");
        boolean isMSIE = ((agent != null) && (agent.toUpperCase().indexOf("MSIE") != -1));
        if (isMSIE) {
            encodedfileName = EncodeUtil.encodeBase64UrlSafe(fileName.getBytes());
        } else {
            encodedfileName = new String(fileName.getBytes(), Charsets.ISO_8859_1);
        }

        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedfileName + "\"");

    }

    /**
     * 取得带相同前缀的Request Parameters, copy from spring WebUtils.
     * <p>
     * 返回的结果的Parameter名已去除前缀.
     *
     * @param request HTTP请求
     * @param prefix  参数前缀
     * @return
     */
    public static Map<String, Object> getParametersStartingWith(ServletRequest request, String prefix) {

        Validate.notNull(request, "Request must not be null");
        Enumeration paramNames = request.getParameterNames();
        Map<String, Object> params = new TreeMap<String, Object>();
        if (prefix == null) {
            prefix = "search_";
        }

        while ((paramNames != null) && paramNames.hasMoreElements()) {
            // 请求中的参数
            String paramName = (String) paramNames.nextElement();
            if ("".equals(prefix) || paramName.startsWith(prefix)) {
                // 去掉前缀后参数
                String unPrefixed = paramName.substring(prefix.length());
                // 请求中的参数值
                String[] values = request.getParameterValues(paramName);
                if ((values == null) || (values.length == 0)) {
                    // 如果没有值则不处理
                } else {
                    List<String> list = Lists.newArrayList(values);
                    long count = list.stream().filter(v -> StringUtils.isNotBlank(v)).count();
                    if (count == 1) {
                        params.put(unPrefixed, list.stream().filter(v -> StringUtils.isNotBlank(v)).findFirst().get());
                    } else if (count > 1) {
                        params.put(unPrefixed, list.stream().filter(v -> StringUtils.isNotBlank(v)).collect(Collectors.toList()));
                    }
                }
            }
        }

        return params;
    }

    /**
     * 组合Parameters生成Query String的Parameter部分, 并在paramter name上加上prefix.
     *
     * @see #getParametersStartingWith
     */
    public static String encodeParameterStringWithPrefix(Map<String, Object> params, String prefix) {
        if (Collections3.isEmpty(params)) {
            return "";
        }

        if (prefix == null) {
            prefix = "";
        }

        StringBuilder queryStringBuilder = new StringBuilder();
        Iterator<Entry<String, Object>> it = params.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, Object> entry = it.next();
            queryStringBuilder.append(prefix).append(entry.getKey()).append('=').append(entry.getValue());
            if (it.hasNext()) {
                queryStringBuilder.append('&');
            }
        }
        return queryStringBuilder.toString();
    }

    /**
     * 客户端对Http Basic验证的 Header进行编码.
     */
    public static String encodeHttpBasic(String userName, String password) {
        String encode = userName + ":" + password;
        return "Basic " + EncodeUtil.encodeBase64(encode.getBytes());
    }

    /**
     * 获取不包含应用名字的URI的路径, 并去掉最前面的"/", <br>
     * 如路径为http://localhost:8080/appName/user/list.do, 得到的值为"user/list.do",其中appNames为应用的名字
     *
     * @param request
     * @return String
     */
    public static String getNoAppNamedRequestURI(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String uri = request.getRequestURI();
        String realUri = uri.replaceFirst(contextPath, "");
        while (realUri.startsWith("/")) {
            realUri = realUri.substring(1);
        }
        realUri = realUri.replaceAll("/+", "/");
        return realUri;
    }

    /**
     * 获取request中指定属性值
     *
     * @param request
     * @param key
     * @return
     */
    public static Object getParameter(ServletRequest request, String key) {

        Validate.notNull(request, "Request must not be null");
        Validate.notBlank(key, "属性值不能为空");

        Object obj = request.getParameter(key);
        Validate.notNull(obj, "Request 无此属性");
        return obj;
    }

    /***
     * 获取 request 中 json 字符串的内容
     *
     * <p>
     * 前提是添加live.jialing.platform.core.filter.HttpServletRequestReplacedFilter。不然RequestBody只能被读取一次
     *
     * @param request
     * @return : <code>byte[]</code>
     * @throws IOException
     */
    public static String getRequestJsonString(HttpServletRequest request) throws IOException {

        String submitMehtod = request.getMethod();
        if (submitMehtod.equals("GET")) {
            // GET
            String paras = request.getQueryString();
            if (StringUtils.isBlank(paras)) {
                return StringUtils.EMPTY;
            }
            return new String(request.getQueryString().getBytes("iso-8859-1"), "utf-8").replaceAll("%22", "\"");
        } else {
            // POST
            // JSON数据格式
            String contentType = request.getHeader(org.springframework.http.HttpHeaders.CONTENT_TYPE);
            if (StringUtils.isNotBlank(contentType) && (contentType.indexOf(MediaType.APPLICATION_JSON_VALUE) >= 0)) {
                return getRequestPostStr(request);
            }
            // 其他数据格式
            Map<String, String[]> pramMap = request.getParameterMap();
            return JsonMapperUtil.toJ(pramMap);
        }
    }

    /**
     * 描述:获取 post 请求内容
     * <p>
     * <pre>
     * 举例：
     * </pre>
     *
     * @param request
     * @return
     * @throws IOException
     */
    public static String getRequestPostStr(HttpServletRequest request) throws IOException {

        byte buffer[] = getRequestPostBytes(request);
        String charEncoding = request.getCharacterEncoding();
        if (charEncoding == null) {
            charEncoding = "UTF-8";
        }
        return new String(buffer, charEncoding);
    }

    /**
     * 描述:获取 post 请求的 byte[] 数组
     * <p>
     * <pre>
     * 举例：
     * </pre>
     *
     * @param request
     * @return
     * @throws IOException
     */
    public static byte[] getRequestPostBytes(HttpServletRequest request) throws IOException {

        int contentLength = request.getContentLength();
        if (contentLength < 0) {
            return null;
        }
        byte buffer[] = new byte[contentLength];
        for (int i = 0; i < contentLength; ) {

            int readlen = request.getInputStream().read(buffer, i, contentLength - i);
            if (readlen == -1) {
                break;
            }
            i += readlen;
        }
        return buffer;
    }

    /**
     * 配置 CORS 实现跨域请求
     * <br>
     * 请使用WebMvcConfigurerAdapter.addCorsMappings配置
     */
    @Deprecated
    public static void setCorsHeader(HttpServletResponse response) {

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE,PATCH, OPTIONS");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with,accept, content-type");
    }

    /**
     * 向response中写入utf-8格式的json信息
     *
     * @param response
     * @param code
     * @param msg
     */
    public static void responseJsonUtf8Value(ServletResponse response, ResponseStatus code, String msg) {

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        ResponseResult ro = new ResponseResult();
        ro.setCode(code.getCode());
        ro.setMsg(msg);

        // 设置服务器端的编码
        httpResponse.setCharacterEncoding("utf-8");
        httpResponse.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

        try {
            httpResponse.getWriter().write(JsonMapperUtil.toJ(ro));
            httpResponse.getWriter().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向response中写入utf-8格式的json信息
     *
     * @param response
     * @param content
     */
    public static void responseJsonUtf8Value(ServletResponse response, String content) {

        // 设置服务器端的编码
        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        try {
            response.getWriter().write(content);
            response.getWriter().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 401响应,utf-8格式的json信息
     *
     * @param response
     * @param content
     */
    public static void response401JsonUtf8Value(HttpServletResponse response, String content) {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        responseJsonUtf8Value(response, content);
    }

    /**
     * 500响应,utf-8格式的json信息
     *
     * @param response
     * @param content
     */
    public static void response500JsonUtf8Value(HttpServletResponse response, String content) {

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        responseJsonUtf8Value(response, content);
    }

    /**
     * 获取请求的ip地址
     *
     * @param request
     * @return
     */
    public static String getRemoteAddr(HttpServletRequest request) {

        String ip = request.getHeader("x-forwarded-for");
        if ((ip == null) || (ip.length() == 0) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if ((ip == null) || (ip.length() == 0) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if ((ip == null) || (ip.length() == 0) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    private static String getContextPath_2_5(ServletContext context) {

        String contextPath = context.getContextPath();

        if ((contextPath == null) || (contextPath.length() == 0)) {
            contextPath = "/";
        }

        return contextPath;
    }

    public static String getContextPath(ServletContext context) {

        if ((context.getMajorVersion() == 2) && (context.getMinorVersion() < 5)) {
            return null;
        }

        try {
            return getContextPath_2_5(context);
        } catch (NoSuchMethodError error) {
            return null;
        }
    }

}
