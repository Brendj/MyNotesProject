# GetApi

All URIs are relative to */*

Method | HTTP request | Description
------------- | ------------- | -------------
[**classesGet**](GetApi.md#classesGet) | **GET** /classes | Поиск классов
[**getCategories**](GetApi.md#getCategories) | **GET** /categories | Поиск категории
[**getCategoryById**](GetApi.md#getCategoryById) | **GET** /categories/{id} | Получить категорию по идентификатору
[**getClassById**](GetApi.md#getClassById) | **GET** /classes/{id} | Получить класс по идентификатору

<a name="classesGet"></a>
# **classesGet**
> List&lt;ModelClass&gt; classesGet(filter, expand, top, skip, orderby, actualOn)

Поиск классов

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import GetApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

GetApi apiInstance = new GetApi();
String filter = "filter_example"; // String | сериализованная коллекция /components/schemas/Filter
String expand = "expand_example"; // String | Список полей, которые должны быть в ответе, через запятую
String top = "top_example"; // String | Количество возвращаемых записей. Значение по умолчанию задается в параметрах Системы.
String skip = "skip_example"; // String | Количество пропускаемых записей. По умолчанию, 0.
String orderby = "orderby_example"; // String | Cписок полей, по которым требуется сортировать, через запятую
OffsetDateTime actualOn = new OffsetDateTime(); // OffsetDateTime | Дата актуальности данных. По умолчанию, текущая.
try {
    List<ModelClass> result = apiInstance.classesGet(filter, expand, top, skip, orderby, actualOn);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling GetApi#classesGet");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **filter** | **String**| сериализованная коллекция /components/schemas/Filter |
 **expand** | **String**| Список полей, которые должны быть в ответе, через запятую | [optional]
 **top** | **String**| Количество возвращаемых записей. Значение по умолчанию задается в параметрах Системы. | [optional]
 **skip** | **String**| Количество пропускаемых записей. По умолчанию, 0. | [optional]
 **orderby** | **String**| Cписок полей, по которым требуется сортировать, через запятую | [optional]
 **actualOn** | **OffsetDateTime**| Дата актуальности данных. По умолчанию, текущая. | [optional]

### Return type

[**List&lt;ModelClass&gt;**](ModelClass.md)

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
//import GetApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

GetApi apiInstance = new GetApi();
String filter = "filter_example"; // String | сериализованная коллекция /components/schemas/Filter
String top = "top_example"; // String | Количество возвращаемых записей. Значение по умолчанию задается в параметрах Системы.
String skip = "skip_example"; // String | Количество пропускаемых записей. По умолчанию, 0.
String orderby = "orderby_example"; // String | Cписок полей, по которым требуется сортировать, через запятую
try {
    List<Category> result = apiInstance.getCategories(filter, top, skip, orderby);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling GetApi#getCategories");
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
//import GetApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

GetApi apiInstance = new GetApi();
Integer id = 56; // Integer | Идентификатор категории
try {
    Category result = apiInstance.getCategoryById(id);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling GetApi#getCategoryById");
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

<a name="getClassById"></a>
# **getClassById**
> ModelClass getClassById(id)

Получить класс по идентификатору

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import GetApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

GetApi apiInstance = new GetApi();
UUID id = new UUID(); // UUID | Идентификатор класса
try {
    ModelClass result = apiInstance.getClassById(id);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling GetApi#getClassById");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | [**UUID**](.md)| Идентификатор класса |

### Return type

[**ModelClass**](ModelClass.md)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

