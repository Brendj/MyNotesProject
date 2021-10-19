/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.sberbank_msk;

import ru.axetta.ecafe.processor.core.logic.PaymentProcessResult;

import java.util.HashMap;
import java.util.Map;

public enum SBMSKPaymentsCodes {
    OK(0, "Успешное завершение операции"),
    TEMPORARY_ERROR(1, "Временная ошибка. Повторите запрос позже"),
    UNKNOWN_REQUEST_ERROR(2, "Неизвестный тип запроса"),
    CLIENT_NOT_FOUND_ERROR(3, "Абонент не найден"),
    ILLEGAL_CONTRACT_ID_ERROR(4, "Неверный формат идентификатора абонента"),
    CLIENT_NOT_ACTIVE_ERROR(5, "Счет абонента не активен"),
    INVALID_PAY_ID_VALUE_ERROR(6, "Неверное значение идентификатора транзакции"),
    PAYMENT_IS_PROHIBITED_ERROR(7, "Прием платежа запрещен по техническим причинам"),
    DUPLICATE_TRANSACTION_ERROR(8, "Дублирование транзакции"),
    INVALID_PAYMENT_SUM_ERROR(9, "Неверная сумма платежа"),
    TOO_SMALL_AMOUNT_ERROR(10, "Сумма слишком мала"),
    TOO_LARGE_AMOUNT_ERROR(11,	"Сумма слишком велика"),
    INVALID_DATE_VALUE_ERROR(12, "Неверное значение даты"),
    INVALID_MOBILE_ERROR(13, "Неверный номер телефона"),
    INTERNAL_ERROR(300, "Внутренняя ошибка Организации");

    private Integer code;
    private String description;


    static Map<Integer,SBMSKPaymentsCodes> map = new HashMap<Integer,SBMSKPaymentsCodes>();
    static {
        for (SBMSKPaymentsCodes type : SBMSKPaymentsCodes.values()) {
            map.put(type.getCode(), type);
        }
    }

    private SBMSKPaymentsCodes(int code, String description){
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String toString() {
        return description;
    }

    public static SBMSKPaymentsCodes fromInteger(Integer value){
        return map.get(value);
    }

    public static SBMSKPaymentsCodes getFromPaymentProcessResultCode(Integer paymentProcessResultCode){
        if(paymentProcessResultCode.equals(PaymentProcessResult.OK.getCode())){
            return OK;
        } else if(paymentProcessResultCode.equals(PaymentProcessResult.CLIENT_NOT_FOUND.getCode())){
            return CLIENT_NOT_FOUND_ERROR;
        } else if(paymentProcessResultCode.equals(PaymentProcessResult.PAYMENT_NOT_FOUND.getCode())){
            return INVALID_PAY_ID_VALUE_ERROR;
        } else if(paymentProcessResultCode.equals(PaymentProcessResult.PAYMENT_ALREADY_REGISTERED.getCode())){
            return DUPLICATE_TRANSACTION_ERROR;
        } else return INTERNAL_ERROR;
    }
}
