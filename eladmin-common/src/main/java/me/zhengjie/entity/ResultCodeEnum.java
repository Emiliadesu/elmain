package me.zhengjie.entity;

/**
 * 状态码
 */
public enum  ResultCodeEnum {

    SUCCESS(200),
    FAIL(400),
    UNAUTHORIZED(401),//未认证（签名错误
    NOT_FOUND(404),//接口不存在
    INTERNAL_SERVER_ERROR(500);//服务器内部错误

    public Integer code;

    ResultCodeEnum(Integer code) {
        this.code = code;
    }
}
