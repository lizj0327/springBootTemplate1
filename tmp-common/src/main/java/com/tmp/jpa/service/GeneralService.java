package com.tmp.jpa.service;


import java.io.Serializable;

import com.tmp.jpa.domain.ValueObject;

/**
 * 一般对象的服务的接口
 *
 * @param <T>  一般对象
 * @param <ID> 一般对象的主键
 * @author baizt
 */
public interface GeneralService<T extends ValueObject, ID extends Serializable> extends CommandService<T, ID>, QueryService<T, ID> {

}
