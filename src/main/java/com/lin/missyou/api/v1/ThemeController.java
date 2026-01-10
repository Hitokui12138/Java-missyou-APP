package com.lin.missyou.api.v1;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.lin.missyou.exception.http.NotFoundException;
import com.lin.missyou.model.Theme;
import com.lin.missyou.service.ThemeService;
import com.lin.missyou.vo.ThemePureVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 开发流程
 * 1. 先定义Theme的接口(参数以及返回类型等),也就是先定义和前端的契约
 * 2. 再设计数据库
 *
 * 要学着去看ddl
 */

@RestController
@RequestMapping("theme")
@Validated
public class ThemeController {
    @Autowired
    private ThemeService themeService;

    /**
     * 取得没有spu的简略信息
     * @param names
     * @return
     */
    @GetMapping("/by/names")
    public List<ThemePureVO> getThemeGroupByNames(
            @RequestParam(name = "names") String names) {
        List<String> nameList = Arrays.asList(names.split(","));
        List<Theme> themes = themeService.findByName(nameList);
        List<ThemePureVO> voList = new ArrayList<>();
        Mapper mapper = DozerBeanMapperBuilder.buildDefault();
        themes.forEach(t->{
            ThemePureVO vo = mapper.map(t, ThemePureVO.class);
            voList.add(vo);
        });
        return voList;
    }


    /**
     * Optional的优点
     * 1. 简化代码
     * 2. 给判空操作提供标准写法(强制需要考虑空值的情况)
     *  2.1 告诉函数调用者, 我这个方法是可能返回空值的, 你需要处理
     * @param name
     * @return
     */
    @GetMapping("name/{name}/with_spu")
    public Theme getThemeByNameWithSpu(
            @PathVariable String name
    ){
        // if (this.themeService.findByName(name).isEmpty){}
        Optional<Theme> optionalTheme = this.themeService.findByName(name);
        // 注意这里不能throw一个Exception, 里面是Supplier()
        return optionalTheme.orElseThrow(() -> new NotFoundException(30003));
    }
}
