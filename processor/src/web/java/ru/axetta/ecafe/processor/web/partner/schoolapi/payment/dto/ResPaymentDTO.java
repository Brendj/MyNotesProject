/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.payment.dto;

public class ResPaymentDTO {
    private final long idOfOrder;
    private final int result;
    private final String error;

    public ResPaymentDTO(long idOfOrder, int result, String error) {
        this.idOfOrder = idOfOrder;
        this.result = result;
        this.error = error;
    }

    public static ResPaymentDTO success(long idOfOrder) {
        return new ResPaymentDTO(idOfOrder, 0, null);
    }

    public static ResPaymentDTO error(long idOfOrder, int result, String error) {
        return new ResPaymentDTO(idOfOrder, result, error);
    }

    public long getIdOfOrder() {
        return idOfOrder;
    }

    public int getResult() {
        return result;
    }

    public String getError() {
        return error;
    }


}
