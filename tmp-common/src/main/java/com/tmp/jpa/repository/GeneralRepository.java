package com.tmp.jpa.repository;

import java.io.Serializable;
import java.util.List;

/**
 * 一般对象的仓库的接口
 *
 * @param <T>  对象
 * @param <ID> 对象主键
 */
public interface GeneralRepository<T, ID extends Serializable> {

    /**
     * 保存实体（延迟写入），记录创建、修改信息
     *
     * @param account 操作人
     * @param entity  对象
     * @return
     */
    <S extends T> S save(String account, S entity);

    /**
     * 保存实体（立即写入），记录创建、修改信息
     *
     * @param account 操作人
     * @param entity  对象
     * @return
     */
    <S extends T> S saveAndFlush(String account, S entity);

    /**
     * 批量保存实体（立即写入），记录创建、修改信息
     * <br>
     * 内部实现时没50条数据flush一次
     *
     * @param account  操作人
     * @param entities 对象集合
     * @return
     */
    <S extends T> List<S> saveAndFlush(String account, Iterable<S> entities);

    /**
     * 将所有挂起的更改刷新到数据库。<br>
     * 清除持久性上下文，导致所有托管实体变得独立。
     */
    void flushAndClear();

    /**
     * 清除持久性上下文，导致所有托管实体变得独立。对未刷新到数据库的实体所做的更改将不会持久。
     */
    void clear();

}
