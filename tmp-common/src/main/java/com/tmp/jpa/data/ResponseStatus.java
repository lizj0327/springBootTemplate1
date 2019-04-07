package com.tmp.jpa.data;

import java.io.Serializable;

/**
 * 服务端数据接口响应的内部状态码
 * <br>
 * 业务自定义状态码定义示例
 * <br>
 * 服务相关
 * 
 * 600 : 服务无法继续执行下去，有前置条件没有完成
 * 
 * <br>
 * 授权相关
 * 401: 无权限访问
 * 401001: access_token过期
 * 401002: access_token无效
 * 401003:无token
 * ...
 * 
 * <br>
 * 账号相关
 * 601001: 账号无效!系统中无此账号
 * 601002: 该账号被禁用!
 * 601003：该账号已过期!
 * 601004：账号/密码错误!
 * 601011：账号重复，创建账号是验证此账号是否已经存在，要唯一；
 * 601012：账号格式不正确；
 * 601013：密钥格式正确；
 * 
 * <br>
 * 业务1
 * 602001: 业务1XXX
 * 602002: 业务1XXX
 * 
 * <br>
 * 业务2
 * 
 */
public final class ResponseStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 200：服务器接收到请求，并成功完成请求
     */
    public static final ResponseStatus SUCCEED = createConstant(200, "成功");

    /**
     * 400（错误请求） 服务器不理解请求的语法。
     */
    public static final ResponseStatus BAD_REQUEST = createConstant(400, "服务器不理解请求的语法");

    /**
     * 401：认证授权失败。 包括密钥信息不正确；数字签名错误；授权已超时等等未认证信息+认证信息校验失败；
     */
    public static final ResponseStatus Unauthorized = createConstant(401, "未授权或授权不正确");

    /**
     * 403：已禁止,服务器拒绝请求
     */
    public static final ResponseStatus FORBIDDEN = createConstant(403, "已禁止,服务器拒绝请求");

    /**
     * 404：服务器找不到该请求
     */
    public static final ResponseStatus NOT_FOUND = createConstant(404, "找不到该请求");

    /**
     * 406：无法使用请求的内容特性来响应请求.服务器接收到请求，但是无法正确解析请求，如接收到的参数不正确、数据校验不通过
     */
    public static final ResponseStatus NOT_ACCEPTABLE = createConstant(406, "参数不正确或数据校验不通过");

    /**
     * 500：服务器接收到请求，但是服务器内部处理时发生异常。异常详情msg
     */
    public static final ResponseStatus EXCEPTION = createConstant(500, "数据服务异常");

    /**
     * 601 : 服务无法继续执行下去，有前置条件没有完成
     */
    public static final ResponseStatus PAUSE = createConstant(501, "服务无法继续执行下去,有前置条件没有完成");

    /**
     * 110110：业务告警
     */
    public static final ResponseStatus WARNING = createConstant(110110, "业务告警,请检查业务数据或业务操作");


    public static ResponseStatus createConstant(int code, String message) {
        return new ResponseStatus(code, message);
    }

    public static ResponseStatus parseResponseStatus(int code) {
        return new ResponseStatus(code);
    }

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    private ResponseStatus(int code) {
        this.code = code;
    }

    private ResponseStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ResponseStatus valueOf(int code) {

        return new ResponseStatus(code);
    }

    @Override
    public String toString() {
        return String.valueOf(this.getCode());
    }

}
