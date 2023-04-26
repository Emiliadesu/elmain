package me.zhengjie.utils;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public class BeanUtils {

    public static <T> T parseMap2Object(Map<String, Object> paramMap, Class<T> cls) {
        return JSONObject.parseObject(JSONObject.toJSONString(paramMap), cls);
    }


}
