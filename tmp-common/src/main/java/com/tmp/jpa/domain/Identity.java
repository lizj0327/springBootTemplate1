package com.tmp.jpa.domain;

/**
 * 统一定义id的entity抽象
 * 
 * Oracle需要每个Entity独立定义id的SEQUCENCE时，不继承于本类而改为实现一个Idable的接口。
 * 
 */
public interface Identity<ID> extends java.io.Serializable {

    ID getId();

    void setId(ID id);
}