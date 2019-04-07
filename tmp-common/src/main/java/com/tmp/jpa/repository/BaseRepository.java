package com.tmp.jpa.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * JPA 封装的基类
 * <br>
 * 1、使用JPQL
 * 2、使用SQL
 * 3、调用存储过程
 * 
 * @author baizt
 *
 */
public abstract class BaseRepository {

    // 把EntityManager实体管理器注进来，用@PersistenceContext进行注入
    @PersistenceContext
    private EntityManager entityManager;

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
