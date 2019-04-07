package com.tmp.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

public abstract class JacksonObjectMapper {

    public abstract ObjectMapper getObjectMapper();

    /**
     * 设定是否使用Enum的toString函数来读写Enum,
     * 为False时时使用Enum的name()函数来读写Enum, 默认为False.
     * 注意本函数一定要在Mapper创建后, 所有的读写动作之前调用.
     */
    public JacksonObjectMapper enableEnumUseToString() {

        getObjectMapper().enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        getObjectMapper().enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        return this;
    }

    /**
     * 支持使用Jaxb的Annotation，使得POJO上的annotation不用与Jackson耦合。
     * 默认会先查找jaxb的annotation，如果找不到再找jackson的。
     */
    public JacksonObjectMapper enableJaxbAnnotation() {

        JaxbAnnotationModule module = new JaxbAnnotationModule();
        getObjectMapper().registerModule(module);
        return this;
    }

    /**
     * jdk8的时间
     */
    public JacksonObjectMapper enableJavaTime() {

        JavaTimeModule module = new JavaTimeModule();
        getObjectMapper().registerModule(module);
        return this;
    }

    /**
     * Hibernate5
     */
    public JacksonObjectMapper enableHibernate5() {

        Hibernate5Module module = new Hibernate5Module();
        // 默认使用@Transient注解的不序列化、反序列化
        // 关闭此配置，使@Transient注解的可以序列化、反序列化
        module.disable(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION);
        getObjectMapper().registerModule(module);
        return this;
    }

    /**
     * Guava
     */
    public JacksonObjectMapper enableGuava() {

        GuavaModule module = new GuavaModule();
        getObjectMapper().registerModule(module);
        return this;
    }

    /*
    public JacksonObjectMapper enableHppc() {

        HppcModule module = new HppcModule();
        getObjectMapper().registerModule(module);
        return this;
    }

    public JacksonObjectMapper enablePCollections() {

        PCollectionsModule module = new PCollectionsModule();
        getObjectMapper().registerModule(module);
        return this;
    }
    */

    /**
     * json中带数据类型
     *
     * @return
     */
    public JacksonObjectMapper enableDefaultTyping() {

        // 是不带类型的,这样就不能完整记录对象信息,只需要打开下面这个选项
        getObjectMapper().enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        return this;
    }

    /**
     * 初始化ObjectMapper，只有简单的配置
     *
     * @param include
     * @return
     */
    public JacksonObjectMapper init(JsonInclude.Include include) {

        // if (mapper == null) {
        //     mapper = new ObjectMapper();
        // }

        // 设置输出时包含属性的风格
        if (include != null) {
            getObjectMapper().setSerializationInclusion(include);
        }

        // 序列化配置：
        // 当找不到对应的序列化器时 忽略此字段
        getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //SerializationFeature.INDENT_OUTPUT：是否缩放排列输出，默认false，有些场合为了便于排版阅读则需要对输出做缩放排列
        //SerializationFeature.WRITE_DATES_AS_TIMESTAMPS：序列化日期时以timestamps输出，默认true
        //SerializationFeature.WRITE_ENUMS_USING_TO_STRING：序列化枚举是以toString()来输出，默认false，即默认以name()来输出
        //SerializationFeature.WRITE_ENUMS_USING_INDEX：序列化枚举是以ordinal()来输出，默认false
        //SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED：序列化单元素数组时不以数组来输出，默认false
        //SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS：序列化Map时对key进行排序操作，默认false
        //SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS：序列化char[]时以json数组输出，默认false
        //SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN：序列化BigDecimal时之间输出原始数字还是科学计数，默认false，即是否以toPlainString()科学计数方式来输出
        //http://wiki.fasterxml.com/JacksonFeaturesSerialization

        // java属性是驼峰式、json属性是下划线
        // getObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        // 反序列化配置：
        // 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
        getObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        //http://wiki.fasterxml.com/JacksonFeaturesDeserialization

        // 使Jackson JSON支持Unicode编码非ASCII字符

        // 配置可见级别
        getObjectMapper().setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);


        return this;
    }

    /**
     * 创建只输出非Null且非Empty(如List.isEmpty)的属性到Json字符串的Mapper,建议在外部接口中使用.
     */
    public JacksonObjectMapper nonEmpty() {

        init(JsonInclude.Include.NON_EMPTY)
                .enableEnumUseToString()
                .enableJavaTime()
                .enableJaxbAnnotation()
                .enableHibernate5()
                .enableGuava();

        return this;
    }

    /**
     * 创建只输出初始值被改变的属性到Json字符串的Mapper, 最节约的存储方式，建议在内部接口中使用。
     */
    public JacksonObjectMapper nonDefault() {

        init(JsonInclude.Include.NON_DEFAULT)
                .enableEnumUseToString()
                .enableJavaTime()
                .enableJaxbAnnotation()
                .enableHibernate5()
                .enableGuava();

        return this;
    }

}
