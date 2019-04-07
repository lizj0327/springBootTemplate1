package com.tmp.web.base;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.tmp.jpa.data.Servlets;

/**
 *  * 功能说明：restful api controller 的基类，
 *  
 */
public abstract class SimpleRestController {

    /**
     * 从请求中获取查询条件
     * <p>
     * 前缀为search_， 如：search_LIKE_mobile 手机号模糊查询<br>
     * 子对象查询条件的写法如  live.jialing.data.persistence.DynamicSpecifications  search_EQ_groups.type
     * </p>
     * @param request
     * @return
     * @author baizt E-mail:baizt@03199.com
     * @version 创建时间：2016年5月10日 下午8:36:13
     */
    public Map<String, Object> getSearchParams(HttpServletRequest request) {

        Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, "search_");
        return searchParams;
    }

}
