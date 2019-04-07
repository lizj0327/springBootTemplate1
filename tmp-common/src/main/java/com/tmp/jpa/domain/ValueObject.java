package com.tmp.jpa.domain;

import java.time.LocalDateTime;

/**
 * 值对象抽象
 *
 * @param <ID> 主键
 */
public abstract class ValueObject<ID> implements java.io.Serializable {

    public abstract String getCreateBy();

    public abstract void setCreateBy(String createBy);

    public abstract LocalDateTime getCreateDate();

    public abstract void setCreateDate(LocalDateTime createDate);

    public abstract String getLastModifiedBy();

    public abstract void setLastModifiedBy(String lastModifiedBy);

    public abstract LocalDateTime getLastModifiedDate();

    public abstract void setLastModifiedDate(LocalDateTime lastModifiedDate);

}
