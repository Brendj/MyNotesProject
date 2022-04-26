# UpdateApi

All URIs are relative to */*

Method | HTTP request | Description
------------- | ------------- | -------------
[**personsBatchEducationPut**](UpdateApi.md#personsBatchEducationPut) | **PUT** /persons/batch/education | Пакетное изменение данных об обучении персоны
[**personsIdPut**](UpdateApi.md#personsIdPut) | **PUT** /persons/{id} | Изменение основных данных персоны
[**personsPersonIdAddressesIdPut**](UpdateApi.md#personsPersonIdAddressesIdPut) | **PUT** /persons/{person_id}/addresses/{id} | Изменение адреса персоны
[**personsPersonIdAgentsIdPut**](UpdateApi.md#personsPersonIdAgentsIdPut) | **PUT** /persons/{person_id}/agents/{id} | Изменение связи персоны и представителя
[**personsPersonIdCategoryIdPut**](UpdateApi.md#personsPersonIdCategoryIdPut) | **PUT** /persons/{person_id}/category/{id} | Изменение данных о категории
[**personsPersonIdContactsIdPut**](UpdateApi.md#personsPersonIdContactsIdPut) | **PUT** /persons/{person_id}/contacts/{id} | Изменение контакта персоны
[**personsPersonIdDocumentsIdPut**](UpdateApi.md#personsPersonIdDocumentsIdPut) | **PUT** /persons/{person_id}/documentItems/{id} | Изменение документа персоны
[**personsPersonIdEducationIdPut**](UpdateApi.md#personsPersonIdEducationIdPut) | **PUT** /persons/{person_id}/education/{id} | Изменение данных об обучении персоны
[**personsPersonIdIdsPut**](UpdateApi.md#personsPersonIdIdsPut) | **PUT** /persons/{person_id}/ids | Изменение идентификаторов персоны
[**personsPersonIdPreventionsIdPut**](UpdateApi.md#personsPersonIdPreventionsIdPut) | **PUT** /persons/{person_id}/preventions/{id} | Изменение данных об учете персоны
[**updateCategory**](UpdateApi.md#updateCategory) | **PUT** /categories/{id} | Обновить описание созданной категории
[**updateClass**](UpdateApi.md#updateClass) | **PUT** /classes/{id} | Обновить описание созданного класса

<a name="personsBatchEducationPut"></a>
# **personsBatchEducationPut**
> personsBatchEducationPut(body)

Пакетное изменение данных об обучении персоны

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import UpdateApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

UpdateApi apiInstance = new UpdateApi();
List<PersonEducation> body = Arrays.asList(new PersonEducation()); // List<PersonEducation> | 
try {
    apiInstance.personsBatchEducationPut(body);
} catch (ApiException e) {
    System.err.println("Exception when calling UpdateApi#personsBatchEducationPut");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**List&lt;PersonEducation&gt;**](PersonEducation.md)|  | [optional]

### Return type

null (empty response body)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="personsIdPut"></a>
# **personsIdPut**
> PersonInfo personsIdPut(id, body)

Изменение основных данных персоны

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import UpdateApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

UpdateApi apiInstance = new UpdateApi();
String id = "id_example"; // String | Идентификатор версии персоны
PersonInfo body = new PersonInfo(); // PersonInfo | 
try {
    PersonInfo result = apiInstance.personsIdPut(id, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling UpdateApi#personsIdPut");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **String**| Идентификатор версии персоны |
 **body** | [**PersonInfo**](PersonInfo.md)|  | [optional]

### Return type

[**PersonInfo**](PersonInfo.md)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="personsPersonIdAddressesIdPut"></a>
# **personsPersonIdAddressesIdPut**
> PersonAddress personsPersonIdAddressesIdPut(personId, id, body)

Изменение адреса персоны

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import UpdateApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

UpdateApi apiInstance = new UpdateApi();
String personId = "personId_example"; // String | Идентификатор персоны</br> (пример \"449ab5bd-4f09-47a0-ba89-70ae2cd49692\")
String id = "id_example"; // String | Идентификатор версии адреса
PersonAddress body = new PersonAddress(); // PersonAddress | 
try {
    PersonAddress result = apiInstance.personsPersonIdAddressesIdPut(personId, id, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling UpdateApi#personsPersonIdAddressesIdPut");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **personId** | **String**| Идентификатор персоны&lt;/br&gt; (пример \&quot;449ab5bd-4f09-47a0-ba89-70ae2cd49692\&quot;) |
 **id** | **String**| Идентификатор версии адреса |
 **body** | [**PersonAddress**](PersonAddress.md)|  | [optional]

### Return type

[**PersonAddress**](PersonAddress.md)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="personsPersonIdAgentsIdPut"></a>
# **personsPersonIdAgentsIdPut**
> PersonAgent personsPersonIdAgentsIdPut(personId, id, body)

Изменение связи персоны и представителя

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import UpdateApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

UpdateApi apiInstance = new UpdateApi();
String personId = "personId_example"; // String | Идентификатор персоны
String id = "id_example"; // String | Идентификатор связи с представителем
PersonAgent body = new PersonAgent(); // PersonAgent | 
try {
    PersonAgent result = apiInstance.personsPersonIdAgentsIdPut(personId, id, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling UpdateApi#personsPersonIdAgentsIdPut");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **personId** | **String**| Идентификатор персоны |
 **id** | **String**| Идентификатор связи с представителем |
 **body** | [**PersonAgent**](PersonAgent.md)|  | [optional]

### Return type

[**PersonAgent**](PersonAgent.md)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="personsPersonIdCategoryIdPut"></a>
# **personsPersonIdCategoryIdPut**
> PersonCategory personsPersonIdCategoryIdPut(personId, id, body)

Изменение данных о категории

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import UpdateApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

UpdateApi apiInstance = new UpdateApi();
String personId = "personId_example"; // String | Идентификатор персоны
String id = "id_example"; // String | Идентификатор связи
PersonCategory body = new PersonCategory(); // PersonCategory | 
try {
    PersonCategory result = apiInstance.personsPersonIdCategoryIdPut(personId, id, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling UpdateApi#personsPersonIdCategoryIdPut");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **personId** | **String**| Идентификатор персоны |
 **id** | **String**| Идентификатор связи |
 **body** | [**PersonCategory**](PersonCategory.md)|  | [optional]

### Return type

[**PersonCategory**](PersonCategory.md)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="personsPersonIdContactsIdPut"></a>
# **personsPersonIdContactsIdPut**
> PersonContact personsPersonIdContactsIdPut(personId, id, body)

Изменение контакта персоны

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import UpdateApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

UpdateApi apiInstance = new UpdateApi();
String personId = "personId_example"; // String | Идентификатор персоны
String id = "id_example"; // String | Идентификатор контакта
PersonContact body = new PersonContact(); // PersonContact | 
try {
    PersonContact result = apiInstance.personsPersonIdContactsIdPut(personId, id, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling UpdateApi#personsPersonIdContactsIdPut");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **personId** | **String**| Идентификатор персоны |
 **id** | **String**| Идентификатор контакта |
 **body** | [**PersonContact**](PersonContact.md)|  | [optional]

### Return type

[**PersonContact**](PersonContact.md)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="personsPersonIdDocumentsIdPut"></a>
# **personsPersonIdDocumentsIdPut**
> PersonDocument personsPersonIdDocumentsIdPut(personId, id, body)

Изменение документа персоны

1. Находится документ с указанным идентификатором. 2. Проверяется что документ принадлежит персоне с указанным идентификатором 3. Находится запись person_document для указанного документа 4. Поле actual_to присваивается значение системного времени приема запроса 5. Создается новая запись в таблице person_document, где поле actual_from равна системной дате приема запроса. actual_to - в будущем 6. возвращается объект Документ

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import UpdateApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

UpdateApi apiInstance = new UpdateApi();
String personId = "personId_example"; // String | Идентификатор персоны
String id = "id_example"; // String | Идентификатор документа
PersonDocument body = new PersonDocument(); // PersonDocument | 
try {
    PersonDocument result = apiInstance.personsPersonIdDocumentsIdPut(personId, id, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling UpdateApi#personsPersonIdDocumentsIdPut");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **personId** | **String**| Идентификатор персоны |
 **id** | **String**| Идентификатор документа |
 **body** | [**PersonDocument**](PersonDocument.md)|  | [optional]

### Return type

[**PersonDocument**](PersonDocument.md)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="personsPersonIdEducationIdPut"></a>
# **personsPersonIdEducationIdPut**
> PersonEducation personsPersonIdEducationIdPut(personId, id, body)

Изменение данных об обучении персоны

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import UpdateApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

UpdateApi apiInstance = new UpdateApi();
String personId = "personId_example"; // String | Идентификатор персоны
String id = "id_example"; // String | Идентификатор связи
PersonEducation body = new PersonEducation(); // PersonEducation | 
try {
    PersonEducation result = apiInstance.personsPersonIdEducationIdPut(personId, id, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling UpdateApi#personsPersonIdEducationIdPut");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **personId** | **String**| Идентификатор персоны |
 **id** | **String**| Идентификатор связи |
 **body** | [**PersonEducation**](PersonEducation.md)|  | [optional]

### Return type

[**PersonEducation**](PersonEducation.md)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="personsPersonIdIdsPut"></a>
# **personsPersonIdIdsPut**
> Person personsPersonIdIdsPut(personId, body)

Изменение идентификаторов персоны

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import UpdateApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

UpdateApi apiInstance = new UpdateApi();
String personId = "personId_example"; // String | Идентификатор персоны
Person body = new Person(); // Person | 
try {
    Person result = apiInstance.personsPersonIdIdsPut(personId, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling UpdateApi#personsPersonIdIdsPut");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **personId** | **String**| Идентификатор персоны |
 **body** | [**Person**](Person.md)|  | [optional]

### Return type

[**Person**](Person.md)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="personsPersonIdPreventionsIdPut"></a>
# **personsPersonIdPreventionsIdPut**
> PersonPrevention personsPersonIdPreventionsIdPut(personId, id, body)

Изменение данных об учете персоны

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import UpdateApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

UpdateApi apiInstance = new UpdateApi();
String personId = "personId_example"; // String | Идентификатор персоны
String id = "id_example"; // String | Идентификатор связи
PersonPrevention body = new PersonPrevention(); // PersonPrevention | 
try {
    PersonPrevention result = apiInstance.personsPersonIdPreventionsIdPut(personId, id, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling UpdateApi#personsPersonIdPreventionsIdPut");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **personId** | **String**| Идентификатор персоны |
 **id** | **String**| Идентификатор связи |
 **body** | [**PersonPrevention**](PersonPrevention.md)|  | [optional]

### Return type

[**PersonPrevention**](PersonPrevention.md)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
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
//import UpdateApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

UpdateApi apiInstance = new UpdateApi();
Category body = new Category(); // Category | Описание категории, которую нужно обновить
Integer id = 56; // Integer | Идентификатор категории
try {
    Category result = apiInstance.updateCategory(body, id);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling UpdateApi#updateCategory");
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
//import UpdateApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

UpdateApi apiInstance = new UpdateApi();
ModelClass body = new ModelClass(); // ModelClass | Описание класса, который нужно обновить
String id = "id_example"; // String | Идентификатор версии класса
try {
    ModelClass result = apiInstance.updateClass(body, id);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling UpdateApi#updateClass");
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

