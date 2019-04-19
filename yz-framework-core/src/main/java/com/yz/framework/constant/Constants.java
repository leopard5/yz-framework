package com.yz.framework.constant;

/**
 * <p>
 * 常量定义，包括常用数字，字符串，字符集，mime等
 * </p>
 *
 * @author yazhong.qi
 * @since 1.0
 */
public interface Constants {

    public final static int NUM_INT0 = 0;
    public final static int NUM_INT1 = 1;
    public final static int NUM_INT2 = 2;
    public final static int NUM_INT3 = 3;
    public final static int NUM_INT4 = 4;

    public final static int NUM_INT64 = 64;

    public final static int NUM_INT48 = 48;
    public final static int NUM_INT57 = 57;
    public final static int NUM_INT97 = 97;
    public final static int NUM_INT122 = 122;

    public final static int NUM_INT65 = 65;
    public final static int NUM_INT90 = 90;


    public final static int NUM_INT255 = 255;
    /**
     * 数字常量256
     */
    public final static int NUM_INT256 = 256;
    /**
     * 数字常量1024
     */
    public final static int NUM_INT1024 = 1024;
    /**
     * 数字常量2048
     */
    public final static int NUM_INT2048 = 2048;
    /**
     * 数字常量4096
     */
    public final static int NUM_INT4096 = 4096;
    /**
     * 数字常量8192
     */
    public final static int NUM_INT8192 = 8192;

    /**
     * 数字10000
     */
    public final static int NUM_INT10000 = 10000;
    /**
     * 数字常量65535
     */
    public final static int NUM_INT65535 = 65535;
    /**
     * http get请求
     */
    public final static String HTTP_GET = "GET";
    /**
     * http post请求
     */
    public final static String HTTP_POST = "POST";
    /**
     * 图片gif类型
     */
    public final static String IMAGE_GIF = "image/gif";
    /**
     * 图片jpeg类型
     */
    public final static String IMAGE_JPEG = "image/jpeg";
    /**
     * 图片png类型
     */
    public final static String IMAGE_PNG = "image/png";
    /**
     * 图片bmp类型
     */
    public final static String IMAGE_BMP = "application/x-bmp";
    /**
     * 字符集UTF8
     */
    public final static String CHARSET_UTF8 = "UTF-8";

    /**
     * 响应类型
     */
    public final static String TEXT_HTML_CHARSET_UTF8 =
            "text/html; charset=utf-8";

    /**
     * 字符集GBK
     */
    public final static String CHARSET_GBK = "GBK";
    /**
     * 字符集ISO8859-1
     */
    public final static String CHARSET_ISO88591 = "ISO8859-1";
    /**
     * md5摘要
     */
    public final static String DIGESST_MD5 = "MD5";
    /**
     * GZIP常量
     */
    public final static String G_ZIP = "gzip";
    /**
     * compress常量
     */
    public final static String COMPRESS = "compress";
    /**
     * deflate常量
     */
    public final static String DEFLATE = "deflate";
    /**
     * zlib常量
     */
    public final static String ZLIB = "zlib";
    /**
     * 字符?
     */
    public final static String CHAR_QUESETION = "?";
    /**
     * 字符&
     */
    public final static String CHAR_AND = "&";
    /**
     * 字符=
     */
    public final static String CHAR_EQUAL = "=";
    /**
     * http协议内容编码，是否采用压缩等
     */
    public final static String CONTENT_ENCODING = "Content-Encoding";
    /**
     * 流mime
     */
    public final static String APPLICATION_OCTET_STREAM =
            "application/octet-stream";
    /**
     * https协议名字
     */
    public final static String HTTPS = "https";
    /**
     * http协议名字
     */
    public final static String HTTP = "http";

    /**
     * tils通信名称
     */
    public final static String TLS = "TLS";

    /**
     * SSL通信名称
     */
    public final static String SSL = "SSL";

    /**
     * SSLv2通信名称
     */
    public final static String SSLV2 = "SSLv2";

    /**
     * 中文zh
     */
    public final static String LANG_ZH = "zh";

    /**
     * 英文en
     */
    public final static String LANG_EN = "en";

    /**
     * meta-inf目录
     */
    public final static String META_INF = "META-INF";

    /**
     * classpath：字符串
     */
    public final static String STRING_CLASSPATH_PREFIX = "classpath:";

    /**
     * 逗号分隔符
     */
    public final static String STRING_DH = ",";

    /**
     * 模板文件路径名
     */
    public final static String TEMPLATE_FILE_PATH = "templateFilePath";

    /**
     * wsif配置条目id属性名
     */
    public final static String TEMPLATE_CONFIG_ATTR_ID = "id";

    /**
     * wsif配置条目alias属性名
     */
    public final static String TEMPLATE_CONFIG_ATTR_ALIAS = "alias";

    /**
     * wsif配置条目type属性名
     */
    public final static String TEMPLATE_CONFIG_ATTR_TYPE = "type";

    /**
     * wsif配置条目className属性名
     */
    public final static String TEMPLATE_CONFIG_ATTR_CLASSNAME = "className";

    /**
     * wsif配置条目path属性名
     */
    public final static String TEMPLATE_CONFIG_ATTR_PATH = "path";

    /**
     * wsif配置条目need-init属性名
     */
    public final static String TEMPLATE_CONFIG_ATTR_NEEDINIT = "need-init";

    /**
     * wsif配置条目description属性名
     */
    public final static String TEMPLATE_CONFIG_ATTR_DESCRIPTION = "description";

    /**
     * 工程根目录常量，在web.xml文件中配置
     */
    public final static String WSIF_ROOT = "WSIF.ROOT";

    public final static String SERIAL_VERSION_ID = "serialVersionUID";

    /**
     * 保存用户信息到session中去
     */
    public final static String WEB_USER_SESSION_KEY = "webUserSessionKey";

    /**
     * 验证码
     */
    public final static String VERIFY_CODE = "VERIFY_CODE";
    /**
     * 自定义请求时候返回错误,登陆错误
     */
    public final static int NO_LOGIN_ERROR = 2001;

    /**
     * 自定义请求时候返回错误,xss特殊字符
     */
    public final static int XSS_PARAM_ERROR = 2002;

    /**
     * 自定义请求时候返回错误,xss特殊字符
     */
    public final static int PARAM_BIND_EXCEPTION = 2004;
    /**
     * 自定义请求时候返回错误,sql异常
     */
    public final static int SQL_EXCEPTION = 2005;
    /**
     * 自定义请求时候返回错误,未知异常或错误
     */
    public final static int UNKNOWN_ERROR = 2006;
    /**
     * 自定义请求时候返回错误,无权限
     */
    public final static int NO_RIGHTS_ACCESS = 2007;

    /**
     * 中文语言
     */
    public final static String LANGUAGE_ZH = "zh";
    /**
     * 英文语言
     */
    public final static String LANGUAGE_EN = "en";

    /**
     * 验证码默认请求地址
     */
    public final static String VERFY_CODE_URL = "/verifyCode.jpg";

    /**
     * context-param: WSIFConfigLocation系统wsif配置文件名称
     */
    public final static String WSIF_CONFIG_LOCATION = "WSIFConfigLocation";

    /**
     * 上下文监听器事件常量
     */
    public final static String CONTEXT_EVENT = "context-event";
    /**
     * session监听器事件常量
     */
    public final static String SESSION_EVENT = "session-event";

    /**
     * filter监听器事件常量
     */
    public final static String FILTER_EVENT = "filter-event";

    /**
     * 拦截器监听器事件常量
     */
    public final static String INTERCEPTOR_EVENT = "interceptor-event";

    /**
     * csrf安全检查请求头
     */
    public final static String CSRF_SECURITY_TOKEN = "csrfToken";

    /**
     * 字符串unknow
     */
    public final static String STRING_UNKNOW = "unknown";

    /**
     * 字符串X-Forwarded-For
     */
    public final static String X_FORWARD_FOR = "X-Forwarded-For";

    /**
     * 字符串Proxy-Client-IP
     */
    public final static String PROXY_CLIENT_IP = "Proxy-Client-IP";

    /**
     * 字符串WL-Proxy-Client-IP
     */
    public final static String WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";

    /**
     * 字符串x-requested-with
     */
    public final static String X_REQUESTED_WITH = "x-requested-with";
    /**
     * 字符串XMLHttpRequest
     */
    public final static String XML_HTTP_REQUEST = "XMLHttpRequest";

    /**
     * mybatis: 处理类名称
     */
    public final static String MYBATIS_DIALECT_CLASS = "DialectClass";
    /**
     * mybatis: 处理类别
     */
    public final static String MYBATIS_DIALECT = "Dialect";

    /**
     * 系统配置文件别名
     */
    public final static String CONFIG = "config";

    /**
     * 系统事件别名
     */
    public final static String EVENTS = "events";

    /**
     * 日期格式字符串常量 "yyyy-MM-dd"
     */
    public final static String PATTERN_DATE = "yyyy-MM-dd";

    /**
     * 日期+时间格式字符串常量 "yyyy-MM-dd HH:mm:ss"
     */
    public final static String PATTERN_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

    /**
     * 后台代码逻辑引起的错误，都统称为“内部服务器错误”
     */
    public final static String SYSTEM_ERROR = "内部服务器错误";

    public final static String RUIXUE_SDK_CONFIG_KEY = "RuixueSdkConfig";
    public final static String RUIXUE_APP_KEY = "appkey";
    public final static String RUIXUE_APP_SECRET = "appsecret";
    public final static String RUIXUE_APP_ROPURL = "ropurl";
    public final static String RUIXUE_APP_FORMAT = "format";

}
