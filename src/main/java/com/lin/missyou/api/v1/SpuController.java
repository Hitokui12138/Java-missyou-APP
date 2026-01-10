package com.lin.missyou.api.v1;

import com.github.dozermapper.core.DozerBeanMapper;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.lin.missyou.bo.PageCounter;
import com.lin.missyou.dto.PersonDTO;
import com.lin.missyou.exception.http.ForbiddenException;
import com.lin.missyou.exception.http.NotFoundException;
import com.lin.missyou.model.Banner;
import com.lin.missyou.model.Spu;
import com.lin.missyou.sample.IConnect;
import com.lin.missyou.sample.ISkill;
import com.lin.missyou.service.BannerService;
import com.lin.missyou.service.SpuService;
import com.lin.missyou.util.CommonUtil;
import com.lin.missyou.vo.PagingDozer;
import com.lin.missyou.vo.SpuSimplifyVO;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */
@RestController
@RequestMapping("/spu")
@Validated//让@Max等等注解生效
public class SpuController {
    //业务接口
    @Autowired
    private SpuService spuService;

    /**
     * 获取完整的detail
     * http://localhost:8080/v1/spu/id/2/detail
     */
    @GetMapping("/id/{id}/detail")
    public Spu getById(@PathVariable @Positive(message = "{id.positive}") Long id){
        Spu spu = this.spuService.getSpu(id);
        if(spu == null){
            throw new NotFoundException(30003);
        }
        return spu;
    }

    /**
     * 获取瀑布流的SpuList, 分页机制
     * http://localhost:8080/v1/spu/latest
     * 简化前返回List<SpuSimplifyVO>
     * 简化后返回PagingDozer<Spu, SpuSimplifyVO>
     */
    @GetMapping("/latest")
    //@GetMapping("/latest?start= & count=") ?后面的也不需要明确写出来,
    public PagingDozer<Spu, SpuSimplifyVO> getLatestSpuList(
            @RequestParam(defaultValue = "0") Integer start,
            @RequestParam(defaultValue = "10") Integer count
    ) {
        //return this.spuService.getLatestPagingSpu();
        //1. 获取page和size两个参数()
        PageCounter pageCounter = CommonUtil.convertToPageParameter(start, count);
        Page<Spu> page = this.spuService.getLatestPagingSpu(
                pageCounter.getPage(),
                pageCounter.getCount()
        );
        //2. 将Page和原类直接代入pagingDozer的
        return new PagingDozer<>(page, SpuSimplifyVO.class);
    }

    /**
     *
     * @param id 类别的id
     * @param isRoot 判断是否为一级分类
     * @return
     */
    @GetMapping("/by/category/{id}")
    public PagingDozer<Spu, SpuSimplifyVO> getByCategoryId(
            @PathVariable @Positive Long id,
            @RequestParam(name = "is_root", defaultValue = "false") Boolean isRoot,
            @RequestParam(defaultValue = "0") Integer start,
            @RequestParam(defaultValue = "10") Integer count
    ){
        //封装成PageCounter
        PageCounter pageCounter = CommonUtil.convertToPageParameter(start, count);
        //查询
        Page<Spu> page = this.spuService.getByCategory(
                id,
                isRoot,
                pageCounter.getPage(),
                pageCounter.getCount()
        );
        return new PagingDozer<>(page, SpuSimplifyVO.class);
    }
}
