# CategoryApi

All URIs are relative to */*

Method | HTTP request | Description
------------- | ------------- | -------------
[**addCategory**](CategoryApi.md#addCategory) | **POST** /categories | Создать категорию
[**deleteCategory**](CategoryApi.md#deleteCategory) | **DELETE** /categories/{id} | Удаление категории
[**getCategories**](CategoryApi.md#getCategories) | **GET** /categories | Поиск категории
[**getCategoryById**](CategoryApi.md#getCategoryById) | **GET** /categories/{id} | Получить категорию по идентификатору
[**updateCategory**](CategoryApi.md#updateCategory) | **PUT** /categories/{id} | Обновить описание созданной категории

<a name="addCategory"></a>
# **addCategory**
> Category addCategory(body)

Создать категорию

Метод создания категории

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import CategoryApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

CategoryApi apiInstance = new CategoryApi();
Category body = new Category(); // Category | Описание категории, который нужно создать
try {
    Category result = apiInstance.addCategory(body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling CategoryApi#addCategory");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**Category**](Category.md)| Описание категории, который нужно создать |

### Return type

[**Category**](Category.md)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="deleteCategory"></a>
# **deleteCategory**
> deleteCategory(id)

Удаление категории

Помечает категорию как удаленную

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import CategoryApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

CategoryApi apiInstance = new CategoryApi();
Integer id = 56; // Integer | Идентификатор категории
try {
    apiInstance.deleteCategory(id);
} catch (ApiException e) {
    System.err.println("Exception when calling CategoryApi#deleteCategory");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **Integer**| Идентификатор категории |

### Return type

null (empty response body)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getCategories"></a>
# **getCategories**
> List&lt;Category&gt; getCategories(filter, top, skip, orderby)

Поиск категории

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import CategoryApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

CategoryApi apiInstance = new CategoryApi();
String filter = "filter_example"; // String | сериализованная коллекция /components/schemas/Filter
String top = "top_example"; // String | Количество возвращаемых записей. Значение по умолчанию задается в параметрах Системы.
String skip = "skip_example"; // String | Количество пропускаемых записей. По умолчанию, 0.
String orderby = "orderby_example"; // String | Cписок полей, по которым требуется сортировать, через запятую
try {
    List<Category> result = apiInstance.getCategories(filter, top, skip, orderby);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling CategoryApi#getCategories");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **filter** | **String**| сериализованная коллекция /components/schemas/Filter |
 **top** | **String**| Количество возвращаемых записей. Значение по умолчанию задается в параметрах Системы. | [optional]
 **skip** | **String**| Количество пропускаемых записей. По умолчанию, 0. | [optional]
 **orderby** | **String**| Cписок полей, по которым требуется сортировать, через запятую | [optional]

### Return type

[**List&lt;Category&gt;**](Category.md)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getCategoryById"></a>
# **getCategoryById**
> Category getCategoryById(id)

Получить категорию по идентификатору

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import CategoryApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

CategoryApi apiInstance = new CategoryApi();
Integer id = 56; // Integer | Идентификатор категории
try {
    Category result = apiInstance.getCategoryById(id);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling CategoryApi#getCategoryById");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **Integer**| Идентификатор категории |

### Return type

[**Category**](Category.md)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="updateCategory"></a>
# **updateCategory**
> Category updateCategory(body, id)

Обновить описание созданной категории

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import CategoryApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

CategoryApi apiInstance = new CategoryApi();
Category body = new Category(); // Category | Описание категории, которую нужно обновить
Integer id = 56; // Integer | Идентификатор категории
try {
    Category result = apiInstance.updateCategory(body, id);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling CategoryApi#updateCategory");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**Category**](Category.md)| Описание категории, которую нужно обновить |
 **id** | **Integer**| Идентификатор категории |

### Return type

[**Category**](Category.md)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

