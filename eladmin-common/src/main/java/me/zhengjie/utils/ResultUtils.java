package me.zhengjie.utils;

import me.zhengjie.entity.Result;
import me.zhengjie.entity.ResultCodeEnum;


public class ResultUtils {

    private static final String DEFAULT_SUCCESS_MSG = "成功";

    private static final String DEFAULT_FAIL_MSG = "失败";

    public static Result getSuccess() {
        return new Result().setCode(ResultCodeEnum.SUCCESS.code)
                .setSuccess(true)
                .setMsg(DEFAULT_SUCCESS_MSG);
    }

    public static <T> Result<T> getSuccess(T data) {
        return new Result().setCode(ResultCodeEnum.SUCCESS.code)
                .setSuccess(true)
                .setData(data)
                .setMsg(DEFAULT_SUCCESS_MSG);
    }

    public static Result getSuccess(String msg) {
        return new Result().setCode(ResultCodeEnum.SUCCESS.code)
                .setSuccess(true)
                .setMsg(msg);
    }

    public static Result getFail() {
        return new Result().setCode(ResultCodeEnum.FAIL.code)
                .setSuccess(false)
                .setMsg(DEFAULT_FAIL_MSG);
    }

    public static Result getFail(String msg) {
        return new Result().setCode(ResultCodeEnum.FAIL.code)
                .setSuccess(false)
                .setMsg(DEFAULT_FAIL_MSG)
                .setMsg(msg);
    }

}
