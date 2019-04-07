package com.tmp.jpa.repository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import com.tmp.jpa.domain.ValueObject;



/**
 * 自定义 JPA仓库实现 基类
 *
 * @param <T>  实体对象的类型
 * @param <ID> 主键的数据类型
 * @author baizt
 */
@NoRepositoryBean
public class CustomJpaRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID>
        implements CustomJpaRepository<T, ID> {

    @SuppressWarnings("unused")
    private final EntityManager entityManager;

    public CustomJpaRepositoryImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
        entityManager = em;
    }

    public CustomJpaRepositoryImpl(final JpaEntityInformation<T, ID> entityInformation,
                                   final EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    // @Override
    // public Optional<T> findById(ID id) {
    //
    //     if (id == null) {
    //         return Optional.empty();
    //     }
    //
    //     return super.findOne(new Specification<T>() {
    //         @Nullable
    //         @Override
    //         public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
    //             return criteriaBuilder.equal(root.get("id"), id);
    //         }
    //     });
    // }

    @Override
    @Transactional
    public <S extends T> S save(String account, S entity) {

        return super.save(beforeSaveEntity(account, entity));
    }

    @Override
    @Transactional
    public <S extends T> S saveAndFlush(String account, S entity) {

        return super.saveAndFlush(beforeSaveEntity(account, entity));
    }

    @Override
    @Transactional
    public <S extends T> List<S> saveAndFlush(String account, Iterable<S> entities) {

        List<S> result = new ArrayList<>();

        int n = 30;
        int i = 0;
        for (S entity : entities) {
            result.add(save(account, entity));
            i++;
            if (i % n == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }

        if (i % n > 0) {
            entityManager.flush();
            entityManager.clear();
        }

        return result;
    }

    // ====================

    /**
     * 修改实体对象的创建者、修改者信息
     */
    public <S extends T> S beforeSaveEntity(String account, S entity) {

        if ((entity != null) && (entity instanceof ValueObject)) {

            ValueObject ent = (ValueObject) entity;
            ent.setLastModifiedBy(account);
            ent.setLastModifiedDate(LocalDateTime.now());

            if (ent.getCreateDate() == null) {
                ent.setCreateBy(account);
                ent.setCreateDate(ent.getLastModifiedDate());
            }
        }

        return entity;
    }

    @Override
    public void flushAndClear() {

        entityManager.flush();
        entityManager.clear();
    }

    @Override
    public void clear() {

        entityManager.clear();
    }
}
