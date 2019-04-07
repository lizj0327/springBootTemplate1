package com.tmp.jpa.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Spring Data JPA 全局扩展基类
 *
 * @param <T>  实体对象类型
 * @param <ID> 唯一标示数据类型
 */
public interface CustomJpaRepository<T, ID extends Serializable> extends GeneralRepository<T, ID>,
        JpaRepository<T, ID>,
        JpaSpecificationExecutor<T> {

}
