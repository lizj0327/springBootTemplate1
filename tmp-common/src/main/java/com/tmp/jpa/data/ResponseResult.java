package com.tmp.jpa.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;

/**
 * 统一请求响应数据对象
 *
 * @author baizt
 */
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResponseResult<T> implements Serializable {

    /**
     * 业务状态码，通过http状态码来演变，如http状态是400，则code就是400001 - 400999
     */
    private Object code;
    /**
     * 业务消息，可能是错误消息
     */
    private String msg;
    /**
     * 业务数据
     */
    private T data;


    public ResponseResult() {

        setCode(ResponseStatus.SUCCEED.toString());
    }

    public ResponseResult(ResponseStatus status) {

        setCode(status.toString());
    }

    public ResponseResult(ResponseStatus status, String msg) {

        setCode(status.toString());
        this.setMsg(msg);
    }

    /**************  构造ResponseEntity ****************/

    /**
     * OK
     *
     * @param <T>
     * @return
     */
    public static <T> ResponseEntity<ResponseResult<T>> success() {

        return success(null, null);
    }

    /**
     * OK
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ResponseEntity<ResponseResult<T>> success(T data) {

        return success(data, null);
    }

    /**
     * OK
     *
     * @param data
     * @param message
     * @param <T>
     * @return
     */
    public static <T> ResponseEntity<ResponseResult<T>> success(T data, String message) {

        ResponseResult<T> body = new ResponseResult<>();
        body.setCode(ResponseStatus.SUCCEED.toString());

        if (StringUtils.isNotBlank(message)) {
            body.setMsg(message);
        } else {
            body.setMsg("成功");
        }

        if (data != null) {
            body.setData(data);
        }

        ResponseEntity<ResponseResult<T>> responseEntity = new ResponseEntity<>(body, HttpStatus.OK);
        return responseEntity;
    }

    /**
     * 警告
     *
     * @param <T>
     * @return
     */
    public static <T> ResponseEntity<ResponseResult<T>> warn() {

        return warn(null);
    }

    /**
     * 警告
     *
     * @param warnMsg 业务消息
     * @param <T>
     * @return
     */
    public static <T> ResponseEntity<ResponseResult<T>> warn(String warnMsg) {

        return warn(null, warnMsg, null);
    }

    /**
     * 警告
     *
     * @param warnStatus 业务状态码
     * @param warnMsg    业务消息
     * @param <T>
     * @return
     */
    public static <T> ResponseEntity<ResponseResult<T>> warn(ResponseStatus warnStatus, String warnMsg) {

        return warn(warnStatus, warnMsg, null);
    }

    /**
     * 警告
     *
     * @param warnStatus 业务状态码
     * @param warnMsg    业务消息
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ResponseEntity<ResponseResult<T>> warn(ResponseStatus warnStatus, String warnMsg, T data) {

        ResponseResult<T> body = new ResponseResult<>();

        if (warnStatus == null) {
            body.setCode(ResponseStatus.WARNING.toString());
        } else {
            body.setCode(warnStatus.toString());
        }

        if (StringUtils.isNotBlank(warnMsg)) {
            body.setMsg(warnMsg);
        } else {
            body.setMsg("警告");
        }

        if (data != null) {
            body.setData(data);
        }

        ResponseEntity<ResponseResult<T>> responseEntity = new ResponseEntity<>(body, HttpStatus.OK);
        return responseEntity;
    }

    /**
     * 异常
     *
     * @param <T>
     * @return
     */
    public static <T> ResponseEntity<ResponseResult<T>> error() {

        return error("异常");
    }

    /**
     * 异常
     *
     * @param errorMessage 异常消息
     * @param <T>
     * @return
     */
    public static <T> ResponseEntity<ResponseResult<T>> error(String errorMessage) {

        return error(null, errorMessage, null);
    }

    /**
     * 异常
     *
     * @param businessStatus 业务状态码
     * @param errorMessage   异常消息
     * @param <T>
     * @return
     */

    public static <T> ResponseEntity<ResponseResult<T>> error(ResponseStatus businessStatus, String errorMessage) {
        return error(businessStatus, errorMessage, null);
    }

    /**
     * 异常
     *
     * @param businessStatus 业务状态码
     * @param errorMessage   异常消息
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ResponseEntity<ResponseResult<T>> error(ResponseStatus businessStatus, String errorMessage, T data) {

        ResponseResult<T> body = new ResponseResult<>();

        if (businessStatus == null) {
            body.setCode(ResponseStatus.EXCEPTION.toString());
        } else {
            body.setCode(businessStatus.toString());
        }

        if (StringUtils.isNotBlank(errorMessage)) {
            body.setMsg(errorMessage);
        } else {
            body.setMsg("异常");
        }

        if (data != null) {
            body.setData(data);
        }

        ResponseEntity<ResponseResult<T>> responseEntity = new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
        return responseEntity;
    }

    /**
     * 认证授权失败：包括
     *
     * @param <T>
     * @return
     */
    public static <T> ResponseEntity<ResponseResult<T>> unauthorized() {

        ResponseResult<T> body = new ResponseResult<>();
        body.setCode(ResponseStatus.Unauthorized.toString());
        body.setMsg(ResponseStatus.Unauthorized.getMessage());

        ResponseEntity<ResponseResult<T>> responseEntity = new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
        return responseEntity;
    }

    /**
     * rest api 返回值
     *
     * @param businessStatus 业务状态
     * @param businessMsg    业务消息
     * @param businessData   业务数据
     * @param headers        自定义http header配置
     * @param status         自定义http status
     * @param <T>            业务数据类型
     * @return
     */
    public static <T> ResponseEntity<ResponseResult<T>> handleResponseResult(ResponseStatus businessStatus, String businessMsg, T businessData, HttpHeaders headers, HttpStatus status) {

        ResponseResult<T> body = new ResponseResult<>();
        if (businessStatus == null) {
            businessStatus = ResponseStatus.SUCCEED;
        }
        body.setCode(businessStatus.toString());
        body.setMsg(businessMsg);
        body.setData(businessData);

        ResponseEntity<ResponseResult<T>> responseEntity = headers == null ? new ResponseEntity<>(body, status) : new ResponseEntity<>(body, headers, status);
        return responseEntity;
    }


}
