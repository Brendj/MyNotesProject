/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.order;

import ru.iteco.restservice.controller.base.BaseConverter;
import ru.iteco.restservice.controller.order.responseDTO.OrderDetailResponseDTO;
import ru.iteco.restservice.controller.order.responseDTO.OrderResponseDTO;
import ru.iteco.restservice.model.MenuDetail;
import ru.iteco.restservice.model.Order;
import ru.iteco.restservice.model.OrderDetail;
import ru.iteco.restservice.model.wt.WtDish;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Set;

@Component
public class OrderConverter extends BaseConverter<OrderResponseDTO, Order> {

    @Override
    public OrderResponseDTO toDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();

        dto.setCancel(order.getState() == 1);
        dto.setIdOfOrder(order.getCompositeId().getIdOfOrder());
        dto.setSum(order.getRsum());
        dto.setTime(order.getOrderDate());

        for(OrderDetail detail : order.getOrderDetailSet()){
            OrderDetailResponseDTO detailDTO = new OrderDetailResponseDTO();
            detailDTO.setAmount(detail.getQty());
            detailDTO.setIdOfOrderDetail(detail.getCompositeId().getIdOfOrderDetail());

            if(OrderDetail.TYPE_COMPLEX_MIN <= detail.getMenuType() && detail.getMenuType() <= OrderDetail.TYPE_COMPLEX_MAX ){
                detailDTO.setComplexName(detail.getMenuDetailName());
                detailDTO.setCurrentPrice(detail.getRprice() * detail.getQty());
                detailDTO.setDiscountComplex(
                        order.getOrderType().equals(Order.DISCOUNT_TYPE)
                                || order.getOrderType().equals(Order.DISCOUNT_TYPE_RESERVE)
                );
                if(detail.getRationType() != null) {
                    detailDTO.setGoodType(detail.getRationType().toString());
                }
            } else {
                detailDTO.setName(detail.getMenuDetailName());
                detailDTO.setPrice(detail.getRprice() * detail.getQty());
                detailDTO.setOutput(detail.getMenuOutput());

                if(detail.getDish() != null){
                    WtDish dish = detail.getDish();

                    detailDTO.setCalories(dish.getCalories());
                    detailDTO.setProtein(dish.getProtein());
                    detailDTO.setFat(dish.getFat());
                    detailDTO.setCarbohydrates(dish.getCarbohydrates());
                } else if(detail.getMenu() != null){
                    MenuDetail menuDetail = findMenuDetailByName(detail.getMenu().getMenuDetails(), detail.getMenuDetailName());
                    if(menuDetail == null){
                        continue;
                    }
                    detailDTO.setCalories(menuDetail.getCalories());
                    detailDTO.setProtein(menuDetail.getProtein());
                    detailDTO.setFat(menuDetail.getFat());
                    detailDTO.setCarbohydrates(menuDetail.getCarbohydrates());
                } else {
                    continue;
                }
            }
            dto.getDetails().add(detailDTO);
        }

        return dto;
    }

    private MenuDetail findMenuDetailByName(Set<MenuDetail> menuDetails, String menuDetailName) {
        if(CollectionUtils.isEmpty(menuDetails)){
            return null;
        }
        for(MenuDetail md : menuDetails){
            if(md.getMenuDetailName().equals(menuDetailName)){
                return md;
            }
        }
        return null;
    }
}
