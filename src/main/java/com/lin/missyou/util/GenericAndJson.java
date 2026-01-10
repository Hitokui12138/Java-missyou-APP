package com.lin.missyou.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lin.missyou.exception.http.ServerErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component //因为有@Autowired, 又没有继承特定类, 因此需要手动加入容器
public class GenericAndJson {
    //@Autowired 不能注入Static
    private static ObjectMapper mapper;

    /**
     * 通过方法来桥接依赖注入和Static变量
     * 这样,在容器运行时, 就会把ObjectMapper注入到setMapper()这个方法里面
     * @param mapper
     */
    @Autowired
    public void setMapper(ObjectMapper mapper){
        GenericAndJson.mapper = mapper;
    }

    /**
     * 序列化
     * 静态方法如何依赖注入? 依赖注入静态成员变量?
     * 这是不可能的, 依赖注入肯定是有一个实例化的过程
     * @return
     * @param <T>
     */
    public static <T> String objectToJson(T o){
        try {
            return GenericAndJson.mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new ServerErrorException(9999);
        }
    }

    /**
     * 单一Json处理
     * 如何传入范型T的原类?
     * 模仿PagingDozer, 传入 Class<T> tClass
     * 另外, 在使用时, ( List<Spec>.class )也是错误的语法
     * @param s
     * @return
     */
//    public <T> T jsonToObject(String s, Class<T> tClass) {
//        try{
//            if (null == s){
//                return null;
//            }
//            T o = GenericAndJson.mapper.readValue(s ,tClass);
//            return o;
//        } catch(Exception e){
//            e.printStackTrace();
//            throw new ServerErrorException(9999);
//        }
//    }

    /**
     * 在使用时,会遇到List<Spec>.class这样的问题, 不符合Java语法
     * 使用Jackson的一个抽象类TypeReference
     * 不管是单体还是List都可以用这个方法
     * @param s
     * @param tr
     * @return
     * @param <T>
     */
    public static <T> T jsonToObject(String s, TypeReference<T> tr){
        try{
            if (null == s){
                return null;
            }
            T o = GenericAndJson.mapper.readValue(s ,tr);
            return o;
        } catch(Exception e){
            e.printStackTrace();
            throw new ServerErrorException(9999);
        }
    }

    /**
     * 演示将`List<Spec>`整体看作T的情况
     * 好处是TypeReference的定义可以放在工具类里面
     * 但这个方法有个问题
     * 打断点可以得知并没有解析成Spec, 而是HashMap,
     * 虽然不报错,但违反了强类型,因此最好用上面那个方法
     */
//    public static <T> List<T> jsonToList(String s){
//        try{
//            if (null == s){
//                return null;
//            }
//            List<T> list = GenericAndJson.mapper.readValue(s,
//                    new TypeReference<List<T>>() {});
//            return list;
//        } catch(Exception e){
//            e.printStackTrace();
//            throw new ServerErrorException(9999);
//        }
//    }
}
