package com.tmp.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.tmp.jpa.repository.RepositoryFactoryBean;

@SpringBootApplication
@ComponentScan(basePackages = {"com.tmp"})
@EnableTransactionManagement(proxyTargetClass = true)
@EntityScan(basePackages = {"com.tmp.model"})
@EnableJpaRepositories(basePackages = {"com.tmp.repository"}
        , transactionManagerRef = "transactionManager"
        , entityManagerFactoryRef = "entityManagerFactory"
        , repositoryFactoryBeanClass = RepositoryFactoryBean.class
        , repositoryImplementationPostfix = "CustomImpl")
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

}
