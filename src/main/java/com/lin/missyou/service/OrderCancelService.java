package com.lin.missyou.service;

import com.lin.missyou.bo.OrderMessageBO;
import com.lin.missyou.exception.http.ServerErrorException;
import com.lin.missyou.model.Order;
import com.lin.missyou.repository.OrderRepository;
import com.lin.missyou.repository.SkuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class OrderCancelService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    SkuRepository skuRepository;
    /**
     * 归还库存
     *
     * @param orderMessageBO
     */
    @Transactional
    public void cancel(OrderMessageBO orderMessageBO){
        if(orderMessageBO.getOrderId() <= 0){
            throw new ServerErrorException(9999);
        }
        this.cancel(orderMessageBO.getOrderId());
    }

    private void cancel(Long oid){
        Optional<Order> orderOptional = orderRepository.findById(oid);
        Order order = orderOptional.orElseThrow(() -> new ServerErrorException(9999));

        int res = orderRepository.cancelOrder(oid);
        if(res != 1){
            return; //如果没有更新成功,就不要执恢复库存了
        }
        // 读取每一个sku购买数量
        order.getSnapItems().stream().forEach(i->{
            skuRepository.recoverStock(i.getId(), i.getCount().longValue());
        });
    }
}
