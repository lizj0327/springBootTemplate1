package com.tmp.jpa.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 查询接口
 *
 * @param <T>  对象
 * @param <ID> 对象的主键
 */
public interface QueryService<T extends Serializable, ID extends Serializable> {

    /**
     * 根据主键查询
     *
     * @param pk 主键
     * @return
     */
    Optional<T> findByPK(ID pk);

    /**
     * 根据主键集合查询
     *
     * @param pks 主键集合
     * @return
     */
    List<T> findByPKs(Iterable<ID> pks);

    /**
     * 根据查询条件，获取满足条件的第一个。多条件通过【且】的方式查询
     *
     * @param searchParams 查询条件及值
     * @return
     */
    Optional<T> findOne(Map<String, Object> searchParams);

    /**
     * 分页查询，多条件通过【且】的方式查询
     *
     * @param searchParams 查询条件及值
     * @param page         分页配置
     * @return
     */
    Page<T> findPage(Map<String, Object> searchParams, Pageable page);

    /**
     * 快速查询，分页，多条件通过【或】的方式查询
     *
     * @param searchParams 查询条件及值
     * @param page         分页配置
     * @return
     */
    Page<T> findPageByQuick(Map<String, Object> searchParams, Pageable page);

    /**
     * 查询所有
     *
     * @return
     */
    List<T> findAll();

    /**
     * 根据条件查询，多条件通过【且】的方式查询
     *
     * @param searchParams 查询条件及值
     * @return
     */
    List<T> findAll(Map<String, Object> searchParams);

    /**
     * 根据条件查询，多条件通过【且】的方式查询
     *
     * @param searchParams 查询条件及值
     * @param sort         排序
     * @return
     */
    List<T> findAll(Map<String, Object> searchParams, Sort sort);

    /**
     * 根据条件查询，多条件通过【或】的方式查询
     *
     * @param searchParams 查询条件及值
     * @param sort         排序
     * @return
     */
    List<T> findAllByQuick(Map<String, Object> searchParams, Sort sort);
}
