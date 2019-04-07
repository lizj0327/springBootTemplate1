package com.tmp.jpa.data;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

/**
 * 动态查询条件
 */
public class SpecificationUtil {

    /**
     * 创建动态查询条件组合.（且）
     *
     * @param classz       实体对象
     * @param searchParams 查询条件
     * @return
     */
    public static <T> Specification<T> buildSpecification(Class<T> classz,
                                                          Map<String, Object> searchParams) {

        return SpecificationUtil.buildSpecification(classz, searchParams, null, LinkType.and, null);
    }

    /**
     * 创建动态查询条件组合
     *
     * @param classz       实体对象
     * @param searchParams 查询条件
     * @param linkType     or还是and查询
     * @return
     */
    public static <T> Specification<T> buildSpecification(Class<T> classz,
                                                          Map<String, Object> searchParams,
                                                          LinkType linkType) {

        return SpecificationUtil.buildSpecification(classz, searchParams, null, linkType, null);
    }

    /**
     * 创建动态查询条件组合
     *
     * @param classz       实体对象
     * @param searchParams 查询条件
     * @param linkType     or还是and查询
     * @param conditions   条件集合
     * @return
     */
    public static <T> Specification<T> buildSpecification(Class<T> classz,
                                                          Map<String, Object> searchParams,
                                                          LinkType linkType,
                                                          List<Predicate> conditions) {

        return SpecificationUtil.buildSpecification(classz, searchParams, null, linkType, conditions);
    }

    /**
     * 创建动态查询条件组合
     *
     * @param classz       实体对象
     * @param searchParams 查询条件
     * @param groups       分组
     * @param linkType     or还是and查询
     * @param conditions   条件集合
     * @return
     */
    public static <T> Specification<T> buildSpecification(Class<T> classz,
                                                          Map<String, Object> searchParams,
                                                          Collection<String> groups,
                                                          LinkType linkType,
                                                          List<Predicate> conditions) {

        /* 泛型转换为Class

          Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

         */

        Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
        Specification<T> spec = DynamicSpecifications.bySearchFilter(filters.values(), linkType, groups, classz, conditions);

        /*
        自定义复杂的查询条件
        new Specification<T>(){
            @Nullable
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("id"),1);
            }
        }
        */

        return spec;
    }


}
