package org.tmp.web;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * 如果需要通过打包的方式在web容器中进行部署，则需要继承 SpringBootServletInitializer 覆盖configure(SpringApplicationBuilder)方法
 */
public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(App.class);
    }

}
