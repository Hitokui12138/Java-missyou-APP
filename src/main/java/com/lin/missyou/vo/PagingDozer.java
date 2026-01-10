package com.lin.missyou.vo;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import com.github.dozermapper.core.DozerBeanMapper;

/**
 * 作用: 既需要分页数据, 又需要做数据精简时使用
 * 范型类的继承, 对不确定类型的数据进行范型处理
 * class PagingDozer<T> extends Paging
 * 考虑到PagingDozer需要做属性复制, 因此还有另一个对象类K
 * 所以PagingDozer<T, K>
 */
public class PagingDozer<T, K> extends Paging{
    //构造函数
    @SuppressWarnings("unckecked")//貌似并没有去掉警告?
    public PagingDozer(Page<T> pageT, Class<K> classK){//在参数里把目标的原类传进来
        this.initPaeParameter(pageT);
        //1. 先拿到元数据
        List<T> tList = pageT.getContent();
        //2. 创建Mapper
        Mapper mapper = DozerBeanMapperBuilder.buildDefault();
        //3.创建目标, 如何获得K的原类? 在参数里把目标的原类传进来
        List<K> voList = new ArrayList<>();
        tList.forEach(t -> {    //Consumer
            // SpuSimplifyVO vo = mapper.map(s, SpuSimplifyVO.class);
            K vo = mapper.map(t, classK);
            voList.add(vo);
        });
        //4. 设置
        this.setItems(voList);
    }
}
