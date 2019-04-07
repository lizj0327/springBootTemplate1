package com.tmp.jpa.data;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.service.spi.ServiceException;
import org.springframework.data.jpa.domain.Specification;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tmp.jpa.domain.Entity;
import com.tmp.util.Collections3;
import com.tmp.util.JavaBeanUtil;

/**
 * 动态拼查询条件
 */
public class DynamicSpecifications {

    private static final String SEPARATOR_CHARS = ".";

    public static <T> Specification<T> bySearchFilter(final Collection<SearchFilter> filters
            , LinkType link
            , final Class<T> classz) {

        return DynamicSpecifications.bySearchFilter(filters, link, classz, null);
    }

    public static <T> Specification<T> bySearchFilter(final Collection<SearchFilter> filters
            , LinkType link
            , final Class<T> classz
            , List<Predicate> conditions) {

        return DynamicSpecifications.bySearchFilter(filters, link, null, classz, null);

    }

    /**
     * 动态构建条件 <br>
     * 条件判断 {@link live.jialing.core.data.SearchFilter.Operator} <br>
     * 例如 Person{ String name; Account account; List《Group》 groups;}<br>
     * Account{ String username;String password; } <br>
     * Group{ String name;String code;String type} <br>
     * 1、根据name模糊查询，请输入LIKE_name <br>
     * 2、根据Account的username等于查询，请输入EQ_account.username <br>
     * 3、根据Group的type类型查询，如查询xxx类型group的所有人，请输入EQ_groups.type <br>
     * 也可以处理 object（root） - list - list ；Object（root）- Object - list 等等
     *
     * @param filters    where条件集合
     * @param link       where条件的and、or连接
     * @param groups     分组的字段
     * @param classz     对象的类型
     * @param conditions 外部条件，与内部条件一起组合为where
     * @return
     */
    public static <T> Specification<T> bySearchFilter(final Collection<SearchFilter> filters
            , LinkType link
            , final Collection<String> groups
            , final Class<T> classz
            , List<Predicate> conditions) {

        return (Specification<T>) (root, query, builder) -> {
            // where
            if (Collections3.isNotEmpty(filters)) {

                // 所有的条件
                List<Predicate> predicates = Lists.newArrayList();
                // left join 关联表。如：key=[tos.partyTo,xxxxxx]
                Map<String, Path> leftJoinMap = Maps.newHashMap();

                for (SearchFilter filter : filters) {

                    // 属性路径
                    // nested path translate, 如Task的名为"user.name"的filedName, 转换为Task.user.name属性
                    String[] names = StringUtils.split(filter.fieldName, SEPARATOR_CHARS);
                    Path expression = root; //
                    String nested = ""; // 记录条件的属性路径
                    for (String name : names) {

                        nested = (StringUtils.isBlank(nested) ? "" : nested + ".") + name;
                        // 如果存在left join关联，则直接取
                        if (leftJoinMap.containsKey(name)) {
                            expression = leftJoinMap.get(name);
                            continue;
                        }
                        //
                        Path currPage = expression.get(name);
                        if ((currPage.getJavaType() == List.class)) {
                            // 如果是List关联表，则重新通过join方式处理
                            if (expression instanceof Root) {
                                Root root2 = (Root) expression;
                                expression = root2.join(root2.getModel().getList(name), JoinType.LEFT);

                            } else if (expression instanceof ListJoin) {
                                expression = ((ListJoin) expression).joinList(name, JoinType.LEFT);

                            } else {
                                // 父级是其他
                                expression = ((From) expression).join(name, JoinType.LEFT);
                            }
                            // 缓存
                            leftJoinMap.put(nested, expression);

                        } else if (Entity.class.isAssignableFrom(currPage.getJavaType())) {
                            // 如果是单关联表，则重新通过join方式处理。如果继承于DomainEntity则为外键关联表
                            expression = ((From) expression).join(name, JoinType.LEFT);
                            // 缓存
                            leftJoinMap.put(nested, expression);

                        } else {
                            expression = currPage;
                        }
                    }

                    // 有值条件
                    if (filter.value != null) {
                        Optional optional = JavaBeanUtil.convertObjectFromObject(filter.value, expression.getJavaType());
                        if (!optional.isPresent()) {// 判断数据转化是否成功，特别是枚举类型、数值类型
                            throw new ServiceException("查询条件数据不正确");
                        }
                        switch (filter.operator) {
                            case EQ:
                                predicates.add(builder.equal(expression, optional.get()));
                                break;
                            case LIKE:
                                predicates.add(builder.like(expression, "%" + filter.value + "%"));
                                break;
                            case GT:
                                predicates.add(builder.greaterThan(expression, (Comparable) optional.get()));
                                break;
                            case LT:
                                predicates.add(builder.lessThan(expression, (Comparable) optional.get()));
                                break;
                            case GTE:
                                predicates.add(builder.greaterThanOrEqualTo(expression, (Comparable) optional.get()));
                                break;
                            case LTE:
                                predicates.add(builder.lessThanOrEqualTo(expression, (Comparable) optional.get()));
                                break;
                            case IN:
                                predicates.add(builder.in(expression).value(optional.get()));
                                break;
                        }
                    } else {
                        // 无值条件
                        switch (filter.operator) {
                            case ISNULL:
                                predicates.add(builder.isNull(expression));
                                break;
                            case ISNOTNULL:
                                predicates.add(builder.isNotNull(expression));
                                break;
                        }
                    }
                }

                // 添加外部条件
                if (Collections3.isNotEmpty(conditions)) {
                    predicates.addAll(conditions);
                }

                switch (link) {
                    case and:
                        query.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));
                        break;
                    case or:
                        query.where(builder.or(predicates.toArray(new Predicate[predicates.size()])));
                        break;
                }
            }

            // group by
            if (Collections3.isNotEmpty(groups)) {
                // 设定select字段
                query.multiselect(groups.stream().distinct().map(f -> root.get(f)).collect(Collectors.toList()));
                // group
                query.groupBy(groups.stream().distinct().map(f -> root.get(f)).collect(Collectors.toList()));
            }

            // return builder.conjunction();
            return query.getRestriction();
        };
    }


}
