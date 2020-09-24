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

import org.junit.Test;
import org.junit.Ignore;

import java.util.List;

/**
 * API tests for CategoryApi
 */
@Ignore
public class CategoryApiTest {

    private final CategoryApi api = new CategoryApi();

    /**
     * Создать категорию
     *
     * Метод создания категории
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void addCategoryTest() throws ApiException {
        Category body = null;
        Category response = api.addCategory(body);

        // TODO: test validations
    }
    /**
     * Удаление категории
     *
     * Помечает категорию как удаленную
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void deleteCategoryTest() throws ApiException {
        Integer id = null;
        api.deleteCategory(id);

        // TODO: test validations
    }
    /**
     * Поиск категории
     *
     * 
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void getCategoriesTest() throws ApiException {
        String filter = null;
        String top = null;
        String skip = null;
        String orderby = null;
        List<Category> response = api.getCategories(filter, top, skip, orderby);

        // TODO: test validations
    }
    /**
     * Получить категорию по идентификатору
     *
     * 
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void getCategoryByIdTest() throws ApiException {
        Integer id = null;
        Category response = api.getCategoryById(id);

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
}