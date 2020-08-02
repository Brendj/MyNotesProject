/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

/*
 * API МЭШ.Контингент
 * Описание REST API МЭШ.Контингент
 *
 * OpenAPI spec version: 0.0.1
 * Contact: fixme@ktelabs.ru
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ru.iteco.client.api;

import ru.iteco.client.ApiException;
import ru.iteco.client.model.Transaction;

import org.junit.Test;
import org.junit.Ignore;

import java.util.List;

/**
 * API tests for TransactionApi
 */
@Ignore
public class TransactionApiTest {

    private final TransactionApi api = new TransactionApi();

    /**
     * Выполнение набора операций в рамках транзакции
     *
     * Выполнение набора операций в рамках транзакции
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void transactionTest() throws ApiException {
        List<Transaction> body = null;
        api.transaction(body);

        // TODO: test validations
    }
}
