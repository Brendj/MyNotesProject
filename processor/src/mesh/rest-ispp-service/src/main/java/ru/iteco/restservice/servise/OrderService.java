/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.servise;

import ru.iteco.restservice.controller.order.responseDTO.OrderDetailResponseDTO;
import ru.iteco.restservice.controller.order.responseDTO.OrderResponseDTO;
import ru.iteco.restservice.db.repo.readonly.ClientReadOnlyRepo;
import ru.iteco.restservice.db.repo.readonly.MenuDetailsReadOnlyRepo;
import ru.iteco.restservice.db.repo.readonly.OrderReadOnlyRepo;
import ru.iteco.restservice.errors.NotFoundException;
import ru.iteco.restservice.model.MenuDetail;
import ru.iteco.restservice.model.Order;
import ru.iteco.restservice.model.OrderDetail;
import ru.iteco.restservice.model.wt.WtDish;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.LinkedList;
import java.util.List;

@Validated
@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderReadOnlyRepo readOnlyRepo;
    private final ClientReadOnlyRepo clientReadOnlyRepo;
    private final MenuDetailsReadOnlyRepo menuRepo;

    public OrderService(OrderReadOnlyRepo readOnlyRepo,
                        MenuDetailsReadOnlyRepo menuRepo,
                        ClientReadOnlyRepo clientReadOnlyRepo) {
        this.readOnlyRepo = readOnlyRepo;
        this.menuRepo = menuRepo;
        this.clientReadOnlyRepo = clientReadOnlyRepo;
    }

    @Transactional
    public Page<OrderResponseDTO> getOrdersList(@NotNull Long contractId, @NotNull Long startDate, @NotNull Long endDate,
            @NotNull Pageable pageable) {
        if(startDate > endDate){
            throw new IllegalArgumentException("Дата начала выборки больше даты окончания");
        }
        if(!clientReadOnlyRepo.existsByContractId(contractId)){
            throw new NotFoundException(String.format("Не найден клиент по л/с %d", contractId));
        }

        Page<Order> orders = readOnlyRepo.findByDateAndContractId(contractId, startDate, endDate, pageable);
        List<OrderResponseDTO> res = new LinkedList<>();
        for (Order o : orders){
            OrderResponseDTO dto = toDTO(o, startDate, endDate);
            res.add(dto);
        }

        return new PageImpl<>(res, pageable, orders.getTotalElements());
    }

    private OrderResponseDTO toDTO(Order o, Long startDate, Long endDate) {
        OrderResponseDTO dto = new OrderResponseDTO();

        dto.setCancel(o.getState() == 1);
        dto.setIdOfOrder(o.getCompositeId().getIdOfOrder().toString());
        dto.setSum(o.getRsum());
        dto.setTime(o.getCreatedDate());

        for(OrderDetail detail : o.getOrderDetailSet()){
            OrderDetailResponseDTO detailDTO = new OrderDetailResponseDTO();
            detailDTO.setAmount(detail.getQty());
            detailDTO.setIdOfOrderDetail(detail.getCompositeId().getIdOfOrderDetail().toString());

            if(OrderDetail.TYPE_COMPLEX_MIN <= detail.getMenuType() && detail.getMenuType() <= OrderDetail.TYPE_COMPLEX_MAX){
                detailDTO.setComplexName(detail.getMenuDetailName());
                detailDTO.setCurrentPrice(detail.getRprice() * detail.getQty());
                detailDTO.setDiscountComplex(
                        o.getOrderType().equals(Order.DISCOUNT_TYPE) || o.getOrderType().equals(Order.DISCOUNT_TYPE_RESERVE));
                if(detail.getRationType() != null) {
                    detailDTO.setGoodType(detail.getRationType().toString());
                }
                detailDTO.setIdOfComplex(detail.getIdOfComplex());
                detailDTO.setComplexDetail(new LinkedList<>());
                dto.getDetails().add(detailDTO);
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
                } else {
                    List<MenuDetail> menuDetails = menuRepo.getDetailsByPeriodAndIdOfMenu(detail.getIdOfMenuFromSync(),
                            startDate, endDate, detail.getMenuDetailName(), detail.getCompositeId().getIdOfOrg(),
                            PageRequest.of(0, 1));
                    if(CollectionUtils.isNotEmpty(menuDetails)) {
                        MenuDetail menuDetail = menuDetails.get(0); // list of one element
                        detailDTO.setCalories(menuDetail.getCalories());
                        detailDTO.setProtein(menuDetail.getProtein());
                        detailDTO.setFat(menuDetail.getFat());
                        detailDTO.setCarbohydrates(menuDetail.getCarbohydrates());
                    }
                }
                if(detail.getIdOfComplex() != null) {
                    OrderDetailResponseDTO complexDTO = dto.getDetails()
                            .stream()
                            .filter((c) -> detail.getIdOfComplex().equals(c.getIdOfComplex()))
                            .findFirst().orElse(null);
                    complexDTO.getComplexDetail().add(detailDTO);
                } else {
                    dto.getDetails().add(detailDTO);
                }
            }
        }
        return dto;
    }
}
