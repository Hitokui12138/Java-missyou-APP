package com.lin.missyou.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lin.missyou.exception.http.ServerErrorException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.HashMap;
import java.util.Map;

/**
 * javax.persistence
 * 为了能让这个类被JPA调用到,最常见的方法就是继承一个接口,再打上一个注解
 * AttributeConverter是一个范型接口
 * 第一个范型类是java定义的类型
 * 第二个范型类是数据库的类型, 默认读入的json转换成String
 */
@Converter
public class MapAndJson implements AttributeConverter<Map<String, Object>, String> {
    //把Jackson的对象注入进来
    @Autowired
    private ObjectMapper mapper;
    /**
     * 模型字段序列化数据库字段
     * @param stringObjectMap
     * @return
     */
    @Override
    public String convertToDatabaseColumn(Map<String, Object> stringObjectMap) {
        //这里有个IOException需要处理一下
        try {
            return mapper.writeValueAsString(stringObjectMap);
        } catch (JsonProcessingException e) {
            //记录日志或者定义一个异常
            e.printStackTrace();
            throw new ServerErrorException(9999);
        }

    }

    /**
     * 数据库字段反序列化模型字段
     * @param s
     * @return
     */
    @Override
    @SuppressWarnings("unckecked")
    public Map<String, Object> convertToEntityAttribute(String s) {
        try {
            if(null == s){
                System.out.println("数据错误");
                throw new ServerErrorException(9999);
            }
            return mapper.readValue(s, HashMap.class);//Map是个接口
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new ServerErrorException(9999);
        }
    }
}
