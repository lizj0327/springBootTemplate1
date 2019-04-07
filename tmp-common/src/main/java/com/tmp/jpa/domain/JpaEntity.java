package com.tmp.jpa.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.base.Objects;
import com.tmp.util.ClockUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 基于JPA配置的领域entity基类.
 */
@Setter
@Getter
@ToString
@MappedSuperclass
public abstract class JpaEntity<ID> extends Entity<ID> {

    private static final long serialVersionUID = 1L;

    // 创建者
    @CreatedBy
    @Column(length = 20, updatable = false)
    @Length(max = 20, message = "创建者长度不能超过20")
    protected String createBy;

    // @NumberFormat

    // 创建时间
    @CreatedDate
    @Column(columnDefinition = "timestamp(6) null", updatable = false)
    // 反序列化时: string(json)->object
    @DateTimeFormat(pattern = ClockUtil.LOCAL_DATE_TIME_FORMATTER_PATTERN)
    // 序列化时：object->string(json)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ClockUtil.LOCAL_DATE_TIME_FORMATTER_PATTERN, timezone = ClockUtil.DEFAULT_TIME_ZONE)
    protected LocalDateTime createDate;

    // 最后修改者
    @LastModifiedBy
    @Column(length = 20)
    @Length(max = 20, message = "创建者长度不能超过20")
    protected String lastModifiedBy;

    // 最后修改时间
    @LastModifiedDate
    @Column(columnDefinition = "timestamp(6) null")
    @DateTimeFormat(pattern = ClockUtil.LOCAL_DATE_TIME_FORMATTER_PATTERN)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ClockUtil.LOCAL_DATE_TIME_FORMATTER_PATTERN, timezone = ClockUtil.DEFAULT_TIME_ZONE)
    protected LocalDateTime lastModifiedDate;


    @Override
    public int hashCode() {

        return Objects.hashCode(getId());
    }

}
