package com.tmp.jpa.service;

import java.io.Serializable;

/**
 * 命令接口
 *
 * @param <T>  实体
 * @param <ID> 实体的主键
 * @author baizt
 */
public interface CommandService<T extends Serializable, ID extends Serializable> {

    /**
     * 新增
     *
     * @param obj 对象
     */
    T create(T obj);

    /**
     * 批量新增
     *
     * @param objs 对象集合
     */
    void batchCreate(Iterable<T> objs);

    /**
     * 删除
     *
     * @param pk 主键
     */
    void delete(ID pk);

    /**
     * 批量删除
     *
     * @param pks 主键集合
     */
    void batchDelete(Iterable<T> pks);

    /**
     * 更新
     *
     * @param obj 对象
     */
    T update(T obj);

    /**
     * 批量更新
     *
     * @param objs 对象集合
     */
    void batchUpdate(Iterable<T> objs);
}
