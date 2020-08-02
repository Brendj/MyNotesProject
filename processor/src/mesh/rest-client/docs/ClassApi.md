# ClassApi

All URIs are relative to */*

Method | HTTP request | Description
------------- | ------------- | -------------
[**addClass**](ClassApi.md#addClass) | **POST** /classes | Создать класс
[**classesGet**](ClassApi.md#classesGet) | **GET** /classes | Поиск классов
[**deleteClass**](ClassApi.md#deleteClass) | **DELETE** /classes/{id} | Удаление класса
[**getClassById**](ClassApi.md#getClassById) | **GET** /classes/{id} | Получить класс по идентификатору
[**updateClass**](ClassApi.md#updateClass) | **PUT** /classes/{id} | Обновить описание созданного класса

<a name="addClass"></a>
# **addClass**
> ModelClass addClass(body)

Создать класс

Метод создания класса

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import ClassApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

ClassApi apiInstance = new ClassApi();
ModelClass body = new ModelClass(); // ModelClass | Описание класса, который нужно создать
try {
    ModelClass result = apiInstance.addClass(body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ClassApi#addClass");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**ModelClass**](ModelClass.md)| Описание класса, который нужно создать |

### Return type

[**ModelClass**](ModelClass.md)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

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
//import ClassApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

ClassApi apiInstance = new ClassApi();
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
    System.err.println("Exception when calling ClassApi#classesGet");
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

<a name="deleteClass"></a>
# **deleteClass**
> deleteClass(id)

Удаление класса

Помечает класс как удаленный

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import ClassApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

ClassApi apiInstance = new ClassApi();
String id = "id_example"; // String | Идентификатор версии класса
try {
    apiInstance.deleteClass(id);
} catch (ApiException e) {
    System.err.println("Exception when calling ClassApi#deleteClass");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **String**| Идентификатор версии класса |

### Return type

null (empty response body)

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
//import ClassApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

ClassApi apiInstance = new ClassApi();
UUID id = new UUID(); // UUID | Идентификатор класса
try {
    ModelClass result = apiInstance.getClassById(id);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ClassApi#getClassById");
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

<a name="updateClass"></a>
# **updateClass**
> ModelClass updateClass(body, id)

Обновить описание созданного класса

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import ClassApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

ClassApi apiInstance = new ClassApi();
ModelClass body = new ModelClass(); // ModelClass | Описание класса, который нужно обновить
String id = "id_example"; // String | Идентификатор версии класса
try {
    ModelClass result = apiInstance.updateClass(body, id);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ClassApi#updateClass");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**ModelClass**](ModelClass.md)| Описание класса, который нужно обновить |
 **id** | **String**| Идентификатор версии класса |

### Return type

[**ModelClass**](ModelClass.md)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

