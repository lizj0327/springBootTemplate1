package com.tmp.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 简单封装Jackson，实现JSON String<->Java Object的Mapper.
 * <p>
 * 封装不同的输出风格, 使用不同的builder函数创建实例.
 * <p>
 * 需要忽略 默认值,生成的json带有大量的value是0的内容,去掉这些内容能有效减少josn体积,经过我仔细查看代码找到这个选项:mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
 * <p>
 * json是不带类型的,这样就不能完整记录对象信息,只需要打开下面这个选项
 * mapper.enableDefaultTyping();
 * <p>
 * 一般情况下面2个选项也很重要mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES , false);
 * mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
 * <p>
 * 复杂的泛型类型反射支持:
 * T可以是比较复杂的类型,例如List<Map<String,String>>
 * 这样就解决了一般的嵌套泛型容器的问题
 * public final static <T> T deserialize(String json) {
 * try {
 * return mapper.readValue(json, new TypeReference<T>(){});
 * } catch (IOException e) {
 * throw new RuntimeException(e.getMessage(), e);
 * }
 * }
 * <p>
 * 键 支持 不带双引号！
 * mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
 */
public class JsonMapperUtil extends JacksonObjectMapper {

    private static Logger logger = LoggerFactory.getLogger(JsonMapperUtil.class);

    private ObjectMapper mapper;

    public JsonMapperUtil() {
        this.mapper = new ObjectMapper();
    }

    public JsonMapperUtil(ObjectMapper mapper) {

        if (mapper != null) {
            this.mapper = mapper;
        } else {
            this.mapper = new ObjectMapper();
        }
    }

    /**
     * Object可以是POJO，也可以是Collection或数组。
     * 如果对象为Null, 返回"null".
     * 如果集合为空集合, 返回"[]".
     */
    public String toJson(Object object) {

        try {
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            logger.warn("write to json string error:" + object, e);
            return null;
        }
    }

    /**
     * 反序列化POJO或简单Collection如List<String>.
     * <p>
     * 如果JSON字符串为Null或"null"字符串, 返回Null.
     * 如果JSON字符串为"[]", 返回空集合.
     * <p>
     * 如需反序列化复杂Collection如List<MyBean>, 请使用fromJson(String, JavaType)
     *
     * @see #fromJson(String, JavaType)
     */
    public <T> T fromJson(String jsonString, Class<T> clazz) {

        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }

        try {
            return mapper.readValue(jsonString, clazz);
        } catch (IOException e) {
            logger.warn("parse json string error:" + jsonString, e);
            return null;
        }
    }

    /**
     * 反序列化复杂Collection如List<Bean>, 先使用createCollectionType()或contructMapType()构造类型, 然后调用本函数.
     *
     * @see # createCollectionType(Class, Class...)
     */
    public <T> T fromJson(String jsonString, JavaType javaType) {

        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }

        try {
            return (T) mapper.readValue(jsonString, javaType);
        } catch (IOException e) {
            logger.warn("parse json string error:" + jsonString, e);
            return null;
        }
    }

    /**
     * 构造Collection类型.
     */
    public JavaType constructCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass) {

        return mapper.getTypeFactory().constructCollectionType(collectionClass, elementClass);
    }

    /**
     * 构造Map类型.
     */
    public JavaType constructMapType(Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {

        return mapper.getTypeFactory().constructMapType(mapClass, keyClass, valueClass);
    }

    /**
     * 当JSON里只含有Bean的部分属性时，更新一个已存在Bean，只覆盖该部分的属性.
     */
    public void update(String jsonString, Object object) {

        try {
            mapper.readerForUpdating(object).readValue(jsonString);
        } catch (JsonProcessingException e) {
            logger.warn("update json string:" + jsonString + " to object:" + object + " error.", e);
        } catch (IOException e) {
            logger.warn("update json string:" + jsonString + " to object:" + object + " error.", e);
        }
    }

    /**
     * 输出JSONP格式数据.
     */
    public String toJsonP(String functionName, Object object) {

        return toJson(new JSONPObject(functionName, object));
    }

    /**
     * 取出Mapper做进一步的设置或使用其他序列化API.
     */
    @Override
    public ObjectMapper getObjectMapper() {

        return mapper;
    }


    /*********************  static 方法 *************************/

    /**
     * 创建只输出非Null且非Empty(如List.isEmpty)的属性到Json字符串的Mapper,建议在外部接口中使用.
     *
     * @return
     */
    public static JsonMapperUtil nonEmptyMapper() {

        JsonMapperUtil jsonMapperUtil = new JsonMapperUtil();
        jsonMapperUtil.nonEmpty();

        return jsonMapperUtil;
    }

    /**
     * 创建只输出非Null且非Empty(如List.isEmpty)的属性到Json字符串的Mapper,建议在外部接口中使用.
     *
     * @param mapper
     * @return
     */
    public static JsonMapperUtil nonEmptyMapper(ObjectMapper mapper) {

        JsonMapperUtil jsonMapperUtil = new JsonMapperUtil(mapper);
        jsonMapperUtil.nonEmpty();

        return jsonMapperUtil;
    }

    /**
     * 创建只输出初始值被改变的属性到Json字符串的Mapper, 最节约的存储方式，建议在内部接口中使用。
     *
     * @return
     */
    public static JsonMapperUtil nonDefaultMapper() {

        JsonMapperUtil jsonMapperUtil = new JsonMapperUtil();
        jsonMapperUtil.nonDefault();

        return jsonMapperUtil;
    }

    /**
     * Object 转换为 json字符串
     * <br>
     * java属性与json属性名称是一样的
     * <br>
     * 只输出非Null且非Empty(如List.isEmpty)的属性
     */
    public static String toSameJson(Object obj) {

        JsonMapperUtil mapper = JsonMapperUtil.nonEmptyMapper();
        return mapper.toJson(obj);
    }

    /**
     * Json字符串 转换为 Object
     * <br>
     * java属性与json属性名称是一样的
     */
    public static <T> T fromSameJ(String jsonString, Class<T> clazz) {

        JsonMapperUtil mapper = JsonMapperUtil.nonEmptyMapper();
        return mapper.fromJson(jsonString, clazz);
    }

    /**
     * Json字符串 转换为 Object,复杂的对象时
     * <br>
     * java属性与json属性名称是一样的
     */
    public static <T> T fromSameJ(String jsonString, JavaType javaType) {

        JsonMapperUtil mapper = JsonMapperUtil.nonEmptyMapper();
        return mapper.fromJson(jsonString, javaType);
    }

    /**
     * Json数组字符串 转换为 Object
     * <br>
     * java属性与json属性名称是一样的
     */
    public static <T> List<T> fromSameJList(String jsonString, Class<T> clazz) {

        JsonMapperUtil mapper = JsonMapperUtil.nonEmptyMapper();
        return mapper.fromJson(jsonString, mapper.constructCollectionType(List.class, clazz));
    }

    /**
     * Object 转换为 json字符串
     * <br>
     * java 属性是驼峰式、json属性是下划线
     * <br>
     * 只输出非Null且非Empty(如List.isEmpty)的属性
     *
     * @param obj
     * @return
     * @author baizt E-mail:baizt@03199.com
     * @version 创建时间：2016年5月19日 下午3:13:09
     */
    public static String toJ(Object obj) {

        JsonMapperUtil mapper = JsonMapperUtil.nonEmptyMapper();
        // java属性是驼峰式、json属性是下划线
        mapper.getObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        return mapper.toJson(obj);
    }

    /**
     * Json字符串 转换为 Object
     * <br>
     * java 属性是驼峰式、json属性是下划线
     */
    public static <T> T fromJ(String jsonString, Class<T> clazz) {

        JsonMapperUtil mapper = JsonMapperUtil.nonEmptyMapper();
        // java属性是驼峰式、json属性是下划线
        mapper.getObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        return mapper.fromJson(jsonString, clazz);
    }

    /**
     * Json数组字符串 转换为 Object
     * <br>
     * java 属性是驼峰式、json属性是下划线
     */
    public static <T> List<T> fromJList(String jsonString, Class<T> clazz) {

        JsonMapperUtil mapper = JsonMapperUtil.nonEmptyMapper();
        // java属性是驼峰式、json属性是下划线
        mapper.getObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        return mapper.fromJson(jsonString, mapper.constructCollectionType(List.class, clazz));
    }

    /**
     * 通过此方法实现对象深复制
     *
     * @param source
     * @param destinationType
     * @param <S>
     * @param <D>
     * @return
     */
    public static <S, D> D newObject(S source, Class<D> destinationType) {

        return JsonMapperUtil.fromSameJ(JsonMapperUtil.toSameJson(source), destinationType);
    }
}
