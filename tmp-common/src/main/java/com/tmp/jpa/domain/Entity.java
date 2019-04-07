package com.tmp.jpa.domain;

/**
 * 领域entity基类的抽象
 *
 * @param <ID> 主键
 */
public abstract class Entity<ID> extends ValueObject<ID> implements Identity<ID> {

}
