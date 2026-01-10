/**
 * @作者 7七月
 * @微信公号 林间有风
 * @开源项目 $ http://talelin.com
 * @免费专栏 $ http://course.talelin.com
 * @我的课程 $ http://imooc.com/t/4294850
 * @创建时间 2020-02-24 20:32
 */
package com.lin.missyou.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.lin.missyou.util.GenericAndJson;
//import com.lin.missyou.util.SuperGenericAndJson;
//import com.vladmihalcea.hibernate.type.json.JsonStringType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.lin.missyou.util.GenericAndJson;
import com.lin.missyou.util.ListAndJson;
import com.lin.missyou.util.MapAndJson;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Where(clause = "delete_time is null and online = 1")
public class Sku extends BaseEntity {
    @Id
    private Long id;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Boolean online;
    private String img;
    private String title;
    private Long spuId;
    private Long categoryId;
    private Long rootCategoryId;


    private String code;
    private Long stock;

    /**
     * json类型的处理
     * 使用MapAndJson,ListAndJson处理
     */
//    @Convert(converter = MapAndJson.class)
//    private Map<String, Object> test;
//    @Convert(converter = ListAndJson.class)
//    private List<Object> specs;

    /**
     * 使用GenericAndJson
     */
    private String specs;
    public List<Spec> getSpecs() {
        if(this.specs == null){
            return Collections.emptyList();
        }
        return GenericAndJson.jsonToObject(this.specs, new TypeReference<List <Spec>>(){}); //新建抽象类->匿名类,加上花括号
        //return GenericAndJson.jsonToList(this.specs);
    }
    public void setSpecs(List<Spec> specs) {
        if(specs.isEmpty()){
            return;
        }
        this.specs = GenericAndJson.objectToJson(specs);
    }

    public BigDecimal getActualPrice() {
        return discountPrice == null ? this.price : this.discountPrice;
    }

//    public List<Spec> getSpecs() {
//        if (this.specs == null) {
//            return Collections.emptyList();
//        }
//        return GenericAndJson.jsonToObject(this.specs, new TypeReference<List<Spec>>(){});
//    }
//
//    public void setSpecs(List<Spec> specs) {
//        if(specs.isEmpty()){
//            return;
//        }
//        this.specs = GenericAndJson.objectToJson(specs);
//    }
//

    /**
     * 专门获取所有规格值的方法
     * 另外这个方法不需要返回前端, 因此加上JsonIgnore
     */
    @JsonIgnore
    public List<String> getSpecValueList() {
        return this.getSpecs()
                .stream()
                .map(Spec::getValue)
                .collect(Collectors.toList());
    }

}
