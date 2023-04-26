package me.zhengjie.support;

public interface CommonClient {
    <T> CommonResponse request(Class<T> clazz);

    <T> CommonResponse request(Class<T> clazz,CommonApiParam apiParam);

    String getSign();
}
