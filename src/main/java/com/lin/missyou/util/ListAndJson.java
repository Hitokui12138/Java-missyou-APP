package com.lin.missyou.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lin.missyou.exception.http.ServerErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;
import java.util.List;


public class ListAndJson implements AttributeConverter<List<Object>, String> {
    @Autowired
    private ObjectMapper mapper;

    /**
     * 序列化差不多一样
     * @param objects
     * @return
     */
    @Override
    public String convertToDatabaseColumn(List<Object> objects) {
        try {
            return mapper.writeValueAsString(objects);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new ServerErrorException(9999);
        }

    }

    /**
     * 反序列化
     * @param s
     * @return
     */
    @Override
    public List<Object> convertToEntityAttribute(String s) {
        try{
            if (null == s){
                return null;
            }
            List<Object> t = mapper.readValue(s, List.class);
            return t;
        } catch(Exception e){
            e.printStackTrace();
            throw new ServerErrorException(9999);
        }
    }


}
