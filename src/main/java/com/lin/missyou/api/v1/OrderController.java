package com.lin.missyou.api.v1;


import com.lin.missyou.bo.PageCounter;
import com.lin.missyou.core.LocalUser;
import com.lin.missyou.core.interceptors.ScopeLevel;
import com.lin.missyou.dto.OrderDTO;
import com.lin.missyou.exception.http.NotFoundException;
import com.lin.missyou.logic.OrderChecker;
import com.lin.missyou.model.Order;
import com.lin.missyou.service.OrderService;
import com.lin.missyou.util.CommonUtil;
import com.lin.missyou.vo.OrderIdVO;
import com.lin.missyou.vo.OrderPureVO;
import com.lin.missyou.vo.OrderSimplifyVO;
import com.lin.missyou.vo.PagingDozer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("order")
@Validated
public class OrderController {
    @Autowired
    OrderService orderService;
    @Value("${missyou.order.pay-time-limit}")
    private Long payTimeLimit;


    @PostMapping("")
    @ScopeLevel()
    public OrderIdVO placeOrder(@RequestBody OrderDTO orderDTO){
        Long uid = LocalUser.getUser().getId();
        // 1. 订单校验
        OrderChecker orderChecker = this.orderService.isOk(uid, orderDTO);
        // 2. 下单
        Long oid = this.orderService.placeOrder(uid, orderDTO, orderChecker);
        return new OrderIdVO(oid);
    }

    @GetMapping("/status/unpaid")
    @ScopeLevel
    public PagingDozer getUnpaid(@RequestParam(defaultValue = "0") Integer start,
                                             @RequestParam(defaultValue = "10") Integer count){
        PageCounter page = CommonUtil.convertToPageParameter(start, count);
        Page<Order> orderPage = this.orderService.getUnpaid(
                page.getPage(), page.getCount()
        );
        PagingDozer<Order, OrderSimplifyVO> pagingDozer = new PagingDozer<>(orderPage, OrderSimplifyVO.class);// 简化
        pagingDozer.getItems()
                .forEach(o -> ((OrderSimplifyVO)o).setPeriod(this.payTimeLimit));
        return pagingDozer;
    }
    @GetMapping("/by/status/{status}")
    @ScopeLevel
    public PagingDozer getByStatus(@PathVariable int status,
                                 @RequestParam(defaultValue = "0") Integer start,
                                 @RequestParam(defaultValue = "10") Integer count){
        PageCounter page = CommonUtil.convertToPageParameter(start, count);
        Page<Order> orderPage = this.orderService.getByStatus(
                status,page.getPage(), page.getCount()
        );
        PagingDozer<Order, OrderSimplifyVO> pagingDozer = new PagingDozer<>(orderPage, OrderSimplifyVO.class);// 简化
        pagingDozer.getItems()
                .forEach(o -> ((OrderSimplifyVO)o).setPeriod(this.payTimeLimit));
        return pagingDozer;
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/detail/{oid}")
    @ScopeLevel
    public OrderPureVO getByStatus(@PathVariable Long oid){
        Optional<Order> orderOptional = this.orderService.getOrder(oid);
        return orderOptional.map((o) -> new OrderPureVO(o ,payTimeLimit))
                .orElseThrow(() -> {
                    throw new NotFoundException(50009);
                });
    }
}
