package com.tmp.jpa.service;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tmp.jpa.data.LinkType;
import com.tmp.jpa.domain.Entity;
import com.tmp.jpa.repository.CustomJpaRepository;
import com.tmp.util.Collections3;
import com.tmp.valid.BeanValidators;
import com.tmp.valid.First;
import com.tmp.valid.Second;

/**
 * 一般对象的服务
 * <p>
 * 基于JPA的实现
 * </p>
 *
 * @param <T>  一般实体
 * @param <ID> 一般实体的主键数据类型
 * @author baizt E-mail:baizt@03199.com
 * @version 创建时间：2016年4月27日 下午2:17:44
 */
public abstract class SimpleGeneralService<T extends Entity<ID>, ID extends Serializable> implements GeneralService<T, ID> {

    @Autowired
    protected Validator validator;

    /**
     * 实体对应的仓储
     *
     * @return
     */
    public abstract CustomJpaRepository<T, ID> getRepository();

    /**
     * 分页查询：查询条件
     *
     * @param classz       领域对象
     * @param searchParams 查询条件及值
     * @param linkType     sql where连接类型
     * @return
     * @author baizt E-mail:baizt@03199.com
     * @version 创建时间：2016年4月27日 下午2:54:49
     */
    public abstract Specification<T> buildSpecification(Class<T> classz, Map<String, Object> searchParams, LinkType linkType);

    // timeout 单位为秒
    @Override
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, timeout = 2, rollbackFor = Exception.class)
    public T create(T entity) {

        if (entity == null) {
            throw new NullPointerException("新增时传入对象为空");
        }

        BeanValidators.validateWithException(validator, entity, First.class);

        if (entity.getId() != null) {
            throw new ServiceException("新增实体时ID有值，必须为NULL");
        }

        return getRepository().saveAndFlush(StringUtils.EMPTY, entity);

    }

    @Override
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void batchCreate(Iterable<T> list) {

        if (Collections3.isEmpty(list)) {
            throw new NullPointerException("批量新增时传入对象为空");
        }

        list.forEach(o -> {
            BeanValidators.validateWithException(validator, o, First.class);
        });

        getRepository().saveAndFlush(StringUtils.EMPTY, list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(ID pk) {

        if (pk == null) {
            throw new NullPointerException("删除时传入对象为空");
        }

        getRepository().deleteById(pk);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(Iterable<T> pks) {

        if (pks == null) {
            throw new NullPointerException("批量删除时传入对象为空");
        }

        getRepository().deleteInBatch(pks);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public T update(T entity) {

        if (entity == null) {
            throw new NullPointerException("更新时传入对象为空");
        }

        BeanValidators.validateWithException(validator, entity, Second.class);

        if (entity.getId() == null) {
            throw new ServiceException("更新实体时ID为NULL，必须有值");
        }

        return getRepository().saveAndFlush(StringUtils.EMPTY, entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdate(Iterable<T> list) {

        if (Collections3.isEmpty(list)) {
            throw new NullPointerException("批量更新时传入对象为空");
        }

        list.forEach(o -> {
            BeanValidators.validateWithException(validator, o, Second.class);
        });

        getRepository().saveAndFlush(StringUtils.EMPTY, list);
    }

    /**
     * 根据组建获取对象
     *
     * @param pk 主键
     * @return
     */
    @Override
    public Optional<T> findByPK(ID pk) {

        if (pk == null) {
            return Optional.empty();
        }

        return getRepository().findById(pk);
    }

    /**
     * 根据查询条件，获取满足条件的第一个。多条件通过【且】的方式查询
     *
     * @param searchParams 查询条件及值
     * @return
     */
    public Optional<T> findOne(Map<String, Object> searchParams) {

        Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        Specification<T> spec = buildSpecification(entityClass, searchParams, LinkType.and);
        return getRepository().findOne(spec);
    }

    /**
     * 批量获取制定ID的对象
     *
     * @param pks 主键集合
     * @return
     */
    @Override
    public List<T> findByPKs(Iterable<ID> pks) {

        if (Collections3.isEmpty(pks)) {
            return Lists.newArrayList();
        }

        Map searchParams = Maps.newHashMap();
        searchParams.put("IN_id", pks);

        return findAll(searchParams);
    }

    @Override
    public Page<T> findPage(Map<String, Object> searchParams, Pageable page) {

        return findPage(searchParams, page, LinkType.and);
    }

    @Override
    public Page<T> findPageByQuick(Map<String, Object> searchParams, Pageable page) {

        return findPage(searchParams, page, LinkType.or);
    }

    /**
     * 查询所有
     *
     * @return
     */
    @Override
    public List<T> findAll() {

        return getRepository().findAll();
    }

    /**
     * 根据条件查询，多条件通过【且】的方式查询
     *
     * @param searchParams 查询条件及值
     * @return
     */
    @Override
    public List<T> findAll(Map<String, Object> searchParams) {

        return findAll(searchParams, null, LinkType.and);
    }

    /**
     * 根据条件查询，多条件通过【且】的方式查询
     *
     * @param searchParams 查询条件及值
     * @param sort         排序
     * @return
     */
    @Override
    public List<T> findAll(Map<String, Object> searchParams, Sort sort) {

        return findAll(searchParams, sort, LinkType.and);
    }

    /**
     * 根据条件查询，多条件通过【或】的方式查询
     *
     * @param searchParams 查询条件及值
     * @param sort         排序
     * @return
     */
    @Override
    public List<T> findAllByQuick(Map<String, Object> searchParams, Sort sort) {

        return findAll(searchParams, sort, LinkType.or);
    }

    /**
     * 分页查询
     *
     * @param searchParams 查询条件及值
     * @param sort         排序
     * @param linkType     or查询或者and查询
     * @return
     */
    public List<T> findAll(Map<String, Object> searchParams, Sort sort, LinkType linkType) {

        Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        Specification<T> spec = buildSpecification(entityClass, searchParams, linkType);

        if (sort == null) {
            return getRepository().findAll(spec);
        }

        return getRepository().findAll(spec, sort);
    }

    /**
     * 分页查询
     *
     * @param searchParams 查询条件及值
     * @param page         分页配置
     * @param linkType     or查询或者and查询
     * @return
     */
    public Page<T> findPage(Map<String, Object> searchParams, Pageable page, LinkType linkType) {

        Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

        Specification<T> spec = buildSpecification(entityClass, searchParams, linkType);

        return getRepository().findAll(spec, page);
    }

    /**
     * 分页查询
     *
     * @param searchParams 查询条件及值
     * @param pageNumber   当前页码，从0开始
     * @param pageSize     每页多少条
     * @param sort         排序
     * @param linkType     or查询或者and查询
     * @return
     */
    public Page<T> findPage(Map<String, Object> searchParams, int pageNumber, int pageSize, Sort sort, LinkType linkType) {

        Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        Specification<T> spec = buildSpecification(entityClass, searchParams, linkType);

        return getRepository().findAll(spec, pageRequest);
    }


}
