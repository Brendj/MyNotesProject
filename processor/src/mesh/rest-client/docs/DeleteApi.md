# DeleteApi

All URIs are relative to */*

Method | HTTP request | Description
------------- | ------------- | -------------
[**deleteCategory**](DeleteApi.md#deleteCategory) | **DELETE** /categories/{id} | Удаление категории
[**deleteClass**](DeleteApi.md#deleteClass) | **DELETE** /classes/{id} | Удаление класса
[**personsPersonIdAddressesIdDelete**](DeleteApi.md#personsPersonIdAddressesIdDelete) | **DELETE** /persons/{person_id}/addresses/{id} | Удаление адреса персоны
[**personsPersonIdAgentsIdDelete**](DeleteApi.md#personsPersonIdAgentsIdDelete) | **DELETE** /persons/{person_id}/agents/{id} | Удаление представителя персоны
[**personsPersonIdCategoryIdDelete**](DeleteApi.md#personsPersonIdCategoryIdDelete) | **DELETE** /persons/{person_id}/category/{id} | Удаление данных о категории персоны
[**personsPersonIdContactsIdDelete**](DeleteApi.md#personsPersonIdContactsIdDelete) | **DELETE** /persons/{person_id}/contacts/{id} | Удаление контакта персоны
[**personsPersonIdDocumentsIdDelete**](DeleteApi.md#personsPersonIdDocumentsIdDelete) | **DELETE** /persons/{person_id}/documentItems/{id} | Удаление документа персоны
[**personsPersonIdEducationIdDelete**](DeleteApi.md#personsPersonIdEducationIdDelete) | **DELETE** /persons/{person_id}/education/{id} | Удаление данных об обучении персоны. Метод должен использоваться только в случае добавления ошибочной записи об обучении. В случае отчисления необходимо использовать метод PUT.
[**personsPersonIdPreventionsIdDelete**](DeleteApi.md#personsPersonIdPreventionsIdDelete) | **DELETE** /persons/{person_id}/preventions/{id} | Удаление информации об учете

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
//import DeleteApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

DeleteApi apiInstance = new DeleteApi();
Integer id = 56; // Integer | Идентификатор категории
try {
    apiInstance.deleteCategory(id);
} catch (ApiException e) {
    System.err.println("Exception when calling DeleteApi#deleteCategory");
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
//import DeleteApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

DeleteApi apiInstance = new DeleteApi();
String id = "id_example"; // String | Идентификатор версии класса
try {
    apiInstance.deleteClass(id);
} catch (ApiException e) {
    System.err.println("Exception when calling DeleteApi#deleteClass");
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

<a name="personsPersonIdAddressesIdDelete"></a>
# **personsPersonIdAddressesIdDelete**
> personsPersonIdAddressesIdDelete(personId, id)

Удаление адреса персоны

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import DeleteApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

DeleteApi apiInstance = new DeleteApi();
String personId = "personId_example"; // String | Идентификатор персоны</br> (пример \"449ab5bd-4f09-47a0-ba89-70ae2cd49692\")
String id = "id_example"; // String | Идентификатор версии адреса
try {
    apiInstance.personsPersonIdAddressesIdDelete(personId, id);
} catch (ApiException e) {
    System.err.println("Exception when calling DeleteApi#personsPersonIdAddressesIdDelete");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **personId** | **String**| Идентификатор персоны&lt;/br&gt; (пример \&quot;449ab5bd-4f09-47a0-ba89-70ae2cd49692\&quot;) |
 **id** | **String**| Идентификатор версии адреса |

### Return type

null (empty response body)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="personsPersonIdAgentsIdDelete"></a>
# **personsPersonIdAgentsIdDelete**
> personsPersonIdAgentsIdDelete(personId, id)

Удаление представителя персоны

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import DeleteApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

DeleteApi apiInstance = new DeleteApi();
String personId = "personId_example"; // String | Идентификатор персоны
String id = "id_example"; // String | Идентификатор представителя
try {
    apiInstance.personsPersonIdAgentsIdDelete(personId, id);
} catch (ApiException e) {
    System.err.println("Exception when calling DeleteApi#personsPersonIdAgentsIdDelete");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **personId** | **String**| Идентификатор персоны |
 **id** | **String**| Идентификатор представителя |

### Return type

null (empty response body)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="personsPersonIdCategoryIdDelete"></a>
# **personsPersonIdCategoryIdDelete**
> personsPersonIdCategoryIdDelete(personId, id)

Удаление данных о категории персоны

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import DeleteApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

DeleteApi apiInstance = new DeleteApi();
String personId = "personId_example"; // String | Идентификатор персоны
String id = "id_example"; // String | Идентификатор связи
try {
    apiInstance.personsPersonIdCategoryIdDelete(personId, id);
} catch (ApiException e) {
    System.err.println("Exception when calling DeleteApi#personsPersonIdCategoryIdDelete");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **personId** | **String**| Идентификатор персоны |
 **id** | **String**| Идентификатор связи |

### Return type

null (empty response body)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="personsPersonIdContactsIdDelete"></a>
# **personsPersonIdContactsIdDelete**
> personsPersonIdContactsIdDelete(personId, id)

Удаление контакта персоны

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import DeleteApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

DeleteApi apiInstance = new DeleteApi();
String personId = "personId_example"; // String | Идентификатор персоны
String id = "id_example"; // String | Идентификатор версии контакта
try {
    apiInstance.personsPersonIdContactsIdDelete(personId, id);
} catch (ApiException e) {
    System.err.println("Exception when calling DeleteApi#personsPersonIdContactsIdDelete");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **personId** | **String**| Идентификатор персоны |
 **id** | **String**| Идентификатор версии контакта |

### Return type

null (empty response body)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="personsPersonIdDocumentsIdDelete"></a>
# **personsPersonIdDocumentsIdDelete**
> personsPersonIdDocumentsIdDelete(personId, id)

Удаление документа персоны

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import DeleteApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

DeleteApi apiInstance = new DeleteApi();
String personId = "personId_example"; // String | Идентификатор персоны
String id = "id_example"; // String | Идентификатор документа
try {
    apiInstance.personsPersonIdDocumentsIdDelete(personId, id);
} catch (ApiException e) {
    System.err.println("Exception when calling DeleteApi#personsPersonIdDocumentsIdDelete");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **personId** | **String**| Идентификатор персоны |
 **id** | **String**| Идентификатор документа |

### Return type

null (empty response body)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="personsPersonIdEducationIdDelete"></a>
# **personsPersonIdEducationIdDelete**
> personsPersonIdEducationIdDelete(personId, id)

Удаление данных об обучении персоны. Метод должен использоваться только в случае добавления ошибочной записи об обучении. В случае отчисления необходимо использовать метод PUT.

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import DeleteApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

DeleteApi apiInstance = new DeleteApi();
String personId = "personId_example"; // String | Идентификатор персоны
String id = "id_example"; // String | Идентификатор связи
try {
    apiInstance.personsPersonIdEducationIdDelete(personId, id);
} catch (ApiException e) {
    System.err.println("Exception when calling DeleteApi#personsPersonIdEducationIdDelete");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **personId** | **String**| Идентификатор персоны |
 **id** | **String**| Идентификатор связи |

### Return type

null (empty response body)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="personsPersonIdPreventionsIdDelete"></a>
# **personsPersonIdPreventionsIdDelete**
> personsPersonIdPreventionsIdDelete(personId, id)

Удаление информации об учете

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import DeleteApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

DeleteApi apiInstance = new DeleteApi();
String personId = "personId_example"; // String | Идентификатор персоны
String id = "id_example"; // String | Идентификатор связи
try {
    apiInstance.personsPersonIdPreventionsIdDelete(personId, id);
} catch (ApiException e) {
    System.err.println("Exception when calling DeleteApi#personsPersonIdPreventionsIdDelete");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **personId** | **String**| Идентификатор персоны |
 **id** | **String**| Идентификатор связи |

### Return type

null (empty response body)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

