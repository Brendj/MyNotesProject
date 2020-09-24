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
import ru.iteco.client.model.Category;
import ru.iteco.client.model.ModelClass;
import ru.iteco.client.model.Person;
import ru.iteco.client.model.PersonAddress;
import ru.iteco.client.model.PersonAgent;
import ru.iteco.client.model.PersonCategory;
import ru.iteco.client.model.PersonContact;
import ru.iteco.client.model.PersonDocument;
import ru.iteco.client.model.PersonEducation;
import ru.iteco.client.model.PersonInfo;
import ru.iteco.client.model.PersonPrevention;

import org.junit.Test;
import org.junit.Ignore;

import java.util.List;

/**
 * API tests for UpdateApi
 */
@Ignore
public class UpdateApiTest {

    private final UpdateApi api = new UpdateApi();

    /**
     * Пакетное изменение данных об обучении персоны
     *
     * 
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void personsBatchEducationPutTest() throws ApiException {
        List<PersonEducation> body = null;
        api.personsBatchEducationPut(body);

        // TODO: test validations
    }
    /**
     * Изменение основных данных персоны
     *
     * 
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void personsIdPutTest() throws ApiException {
        String id = null;
        PersonInfo body = null;
        PersonInfo response = api.personsIdPut(id, body);

        // TODO: test validations
    }
    /**
     * Изменение адреса персоны
     *
     * 
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void personsPersonIdAddressesIdPutTest() throws ApiException {
        String personId = null;
        String id = null;
        PersonAddress body = null;
        PersonAddress response = api.personsPersonIdAddressesIdPut(personId, id, body);

        // TODO: test validations
    }
    /**
     * Изменение связи персоны и представителя
     *
     * 
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void personsPersonIdAgentsIdPutTest() throws ApiException {
        String personId = null;
        String id = null;
        PersonAgent body = null;
        PersonAgent response = api.personsPersonIdAgentsIdPut(personId, id, body);

        // TODO: test validations
    }
    /**
     * Изменение данных о категории
     *
     * 
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void personsPersonIdCategoryIdPutTest() throws ApiException {
        String personId = null;
        String id = null;
        PersonCategory body = null;
        PersonCategory response = api.personsPersonIdCategoryIdPut(personId, id, body);

        // TODO: test validations
    }
    /**
     * Изменение контакта персоны
     *
     * 
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void personsPersonIdContactsIdPutTest() throws ApiException {
        String personId = null;
        String id = null;
        PersonContact body = null;
        PersonContact response = api.personsPersonIdContactsIdPut(personId, id, body);

        // TODO: test validations
    }
    /**
     * Изменение документа персоны
     *
     * 1. Находится документ с указанным идентификатором. 2. Проверяется что документ принадлежит персоне с указанным идентификатором 3. Находится запись person_document для указанного документа 4. Поле actual_to присваивается значение системного времени приема запроса 5. Создается новая запись в таблице person_document, где поле actual_from равна системной дате приема запроса. actual_to - в будущем 6. возвращается объект Документ
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void personsPersonIdDocumentsIdPutTest() throws ApiException {
        String personId = null;
        String id = null;
        PersonDocument body = null;
        PersonDocument response = api.personsPersonIdDocumentsIdPut(personId, id, body);

        // TODO: test validations
    }
    /**
     * Изменение данных об обучении персоны
     *
     * 
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void personsPersonIdEducationIdPutTest() throws ApiException {
        String personId = null;
        String id = null;
        PersonEducation body = null;
        PersonEducation response = api.personsPersonIdEducationIdPut(personId, id, body);

        // TODO: test validations
    }
    /**
     * Изменение идентификаторов персоны
     *
     * 
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void personsPersonIdIdsPutTest() throws ApiException {
        String personId = null;
        Person body = null;
        Person response = api.personsPersonIdIdsPut(personId, body);

        // TODO: test validations
    }
    /**
     * Изменение данных об учете персоны
     *
     * 
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void personsPersonIdPreventionsIdPutTest() throws ApiException {
        String personId = null;
        String id = null;
        PersonPrevention body = null;
        PersonPrevention response = api.personsPersonIdPreventionsIdPut(personId, id, body);

        // TODO: test validations
    }
    /**
     * Обновить описание созданной категории
     *
     * 
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void updateCategoryTest() throws ApiException {
        Category body = null;
        Integer id = null;
        Category response = api.updateCategory(body, id);

        // TODO: test validations
    }
    /**
     * Обновить описание созданного класса
     *
     * 
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void updateClassTest() throws ApiException {
        ModelClass body = null;
        String id = null;
        ModelClass response = api.updateClass(body, id);

        // TODO: test validations
    }
}