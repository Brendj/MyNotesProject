/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.sberbank_msk;

import ru.axetta.ecafe.processor.core.logic.PaymentProcessResult;

import java.util.HashMap;
import java.util.Map;

public enum SBMSKPaymentsDesc {
    TEMPORARY_ERROR(1, "Сервис временно недоступен. Пожалуйста обратитесь позднее"),
    CLIENT_NOT_FOUND_ERROR(3, "По номеру телефона не найдены привязанные лицевые счета детей. Проверьте правильность ввода"),
    CLIENT_NOT_FOUND_ERROR_SUMMARY(3, "По номеру телефона не найдены привязанные лицевые счета детей. Проверьте правильность ввода"),
    CLIENT_NOT_FOUND_ERROR_CHECK(3, "Лицевой счет ребенка не найден. Проверьте правильность ввода"),
    INVALID_MOBILE_ERROR(13, "По номеру телефона не найдены привязанные лицевые счета детей. Проверьте корректность ввода номера"),
    NOT_FOUND_INN(14, "Отсутствует ИНН контрагента. Информация о лицевом счете ребенка недоступна"),
    INTERNAL_ERROR(300, "Внутренняя ошибка. Обратитесь позднее или повторите операцию");

    private Integer code;
    private String description;


    static Map<Integer, SBMSKPaymentsDesc> map = new HashMap<Integer, SBMSKPaymentsDesc>();

    static {
        for (SBMSKPaymentsDesc type : SBMSKPaymentsDesc.values()) {
            map.put(type.getCode(), type);
        }
    }

    private SBMSKPaymentsDesc(int code, String description) {
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

    public static SBMSKPaymentsDesc fromInteger(Integer value) {
        return map.get(value);
    }

    public static SBMSKPaymentsDesc getFromPaymentProcessResultCode(Integer paymentProcessResultCode, String type) {
        if (type.equals(SBMSKOnlinePaymentRequestParser.ACTION_CHECK)) {
            if (paymentProcessResultCode.equals(PaymentProcessResult.CLIENT_NOT_FOUND.getCode())) {
                return CLIENT_NOT_FOUND_ERROR_CHECK;
            }
        }
        if (type.equals(SBMSKOnlinePaymentRequestParser.ACTION_SUMMARY)) {
            if (paymentProcessResultCode.equals(SBMSKPaymentsCodes.CLIENT_NOT_FOUND_ERROR.getCode())) {
                return CLIENT_NOT_FOUND_ERROR_SUMMARY;
            }
        }
        return null;
    }

    public static SBMSKPaymentsDesc getFromPaymenResultCode(Integer paymentResultCode, String type) {
        if (paymentResultCode.equals(SBMSKPaymentsCodes.TEMPORARY_ERROR.getCode())) {
            return TEMPORARY_ERROR;
        } else if (paymentResultCode.equals(SBMSKPaymentsCodes.CLIENT_NOT_FOUND_ERROR.getCode())) {
            if (type.equals(SBMSKOnlinePaymentRequestParser.ACTION_SUMMARY)) {
                return CLIENT_NOT_FOUND_ERROR_SUMMARY;
            } else if (type.equals(SBMSKOnlinePaymentRequestParser.ACTION_CHECK)) {
                return CLIENT_NOT_FOUND_ERROR_CHECK;
            } else return null;
        } else if (paymentResultCode.equals(SBMSKPaymentsCodes.INVALID_MOBILE_ERROR.getCode())) {
            if (type.equals(SBMSKOnlinePaymentRequestParser.ACTION_SUMMARY))
                return INVALID_MOBILE_ERROR;
        } else if (paymentResultCode.equals(SBMSKPaymentsCodes.NOT_FOUND_INN.getCode())) {
            if (type.equals(SBMSKOnlinePaymentRequestParser.ACTION_SUMMARY))
                return NOT_FOUND_INN;
        } else if (paymentResultCode.equals(SBMSKPaymentsCodes.INTERNAL_ERROR.getCode())) {
            return INTERNAL_ERROR;
        }
        return null;
    }
}
