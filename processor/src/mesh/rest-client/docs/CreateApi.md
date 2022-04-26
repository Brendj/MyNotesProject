# CreateApi

All URIs are relative to */*

Method | HTTP request | Description
------------- | ------------- | -------------
[**addCategory**](CreateApi.md#addCategory) | **POST** /categories | Создать категорию
[**addClass**](CreateApi.md#addClass) | **POST** /classes | Создать класс
[**personsBatchEducationPost**](CreateApi.md#personsBatchEducationPost) | **POST** /persons/batch/education | Пакетное создание данных об обучении персоны
[**personsPersonIdAddressesPost**](CreateApi.md#personsPersonIdAddressesPost) | **POST** /persons/{person_id}/addresses | Создание нового адреса персоны
[**personsPersonIdAgentsPost**](CreateApi.md#personsPersonIdAgentsPost) | **POST** /persons/{person_id}/agents | 
[**personsPersonIdCategoryPost**](CreateApi.md#personsPersonIdCategoryPost) | **POST** /persons/{person_id}/category | Добавление новых данных о категории
[**personsPersonIdContactsPost**](CreateApi.md#personsPersonIdContactsPost) | **POST** /persons/{person_id}/contacts | Создание нового контакта персоны
[**personsPersonIdDocumentsPost**](CreateApi.md#personsPersonIdDocumentsPost) | **POST** /persons/{person_id}/documentItems | Создание нового документа персоны
[**personsPersonIdEducationPost**](CreateApi.md#personsPersonIdEducationPost) | **POST** /persons/{person_id}/education | Добавление новых данных об обучении
[**personsPersonIdPreventionsPost**](CreateApi.md#personsPersonIdPreventionsPost) | **POST** /persons/{person_id}/preventions | Добавление информации об учете персоны

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
//import CreateApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

CreateApi apiInstance = new CreateApi();
Category body = new Category(); // Category | Описание категории, который нужно создать
try {
    Category result = apiInstance.addCategory(body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling CreateApi#addCategory");
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
//import CreateApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

CreateApi apiInstance = new CreateApi();
ModelClass body = new ModelClass(); // ModelClass | Описание класса, который нужно создать
try {
    ModelClass result = apiInstance.addClass(body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling CreateApi#addClass");
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

<a name="personsBatchEducationPost"></a>
# **personsBatchEducationPost**
> personsBatchEducationPost(body)

Пакетное создание данных об обучении персоны

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import CreateApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

CreateApi apiInstance = new CreateApi();
List<PersonEducation> body = Arrays.asList(new PersonEducation()); // List<PersonEducation> | 
try {
    apiInstance.personsBatchEducationPost(body);
} catch (ApiException e) {
    System.err.println("Exception when calling CreateApi#personsBatchEducationPost");
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

<a name="personsPersonIdAddressesPost"></a>
# **personsPersonIdAddressesPost**
> PersonAddress personsPersonIdAddressesPost(personId, body)

Создание нового адреса персоны

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import CreateApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

CreateApi apiInstance = new CreateApi();
String personId = "personId_example"; // String | Идентификатор персоны</br> (пример \"449ab5bd-4f09-47a0-ba89-70ae2cd49692\")
PersonAddress body = new PersonAddress(); // PersonAddress | 
try {
    PersonAddress result = apiInstance.personsPersonIdAddressesPost(personId, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling CreateApi#personsPersonIdAddressesPost");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **personId** | **String**| Идентификатор персоны&lt;/br&gt; (пример \&quot;449ab5bd-4f09-47a0-ba89-70ae2cd49692\&quot;) |
 **body** | [**PersonAddress**](PersonAddress.md)|  | [optional]

### Return type

[**PersonAddress**](PersonAddress.md)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="personsPersonIdAgentsPost"></a>
# **personsPersonIdAgentsPost**
> PersonAgent personsPersonIdAgentsPost(personId, body)



Создание нового представителя персоны или добавление существующей персоны в качестве представителя. Если добавяется существующая персона, agent_person не указывается, вместо нее должен присутствовать атрибут agent_person_id с идентификатором персоны представителя

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import CreateApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

CreateApi apiInstance = new CreateApi();
String personId = "personId_example"; // String | Идентификатор персоны
PersonAgent body = new PersonAgent(); // PersonAgent | 
try {
    PersonAgent result = apiInstance.personsPersonIdAgentsPost(personId, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling CreateApi#personsPersonIdAgentsPost");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **personId** | **String**| Идентификатор персоны |
 **body** | [**PersonAgent**](PersonAgent.md)|  | [optional]

### Return type

[**PersonAgent**](PersonAgent.md)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="personsPersonIdCategoryPost"></a>
# **personsPersonIdCategoryPost**
> PersonCategory personsPersonIdCategoryPost(personId, body)

Добавление новых данных о категории

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import CreateApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

CreateApi apiInstance = new CreateApi();
String personId = "personId_example"; // String | Идентификатор персоны
PersonCategory body = new PersonCategory(); // PersonCategory | 
try {
    PersonCategory result = apiInstance.personsPersonIdCategoryPost(personId, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling CreateApi#personsPersonIdCategoryPost");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **personId** | **String**| Идентификатор персоны |
 **body** | [**PersonCategory**](PersonCategory.md)|  | [optional]

### Return type

[**PersonCategory**](PersonCategory.md)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="personsPersonIdContactsPost"></a>
# **personsPersonIdContactsPost**
> PersonContact personsPersonIdContactsPost(personId, body)

Создание нового контакта персоны

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import CreateApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

CreateApi apiInstance = new CreateApi();
String personId = "personId_example"; // String | Идентификатор персоны
PersonContact body = new PersonContact(); // PersonContact | 
try {
    PersonContact result = apiInstance.personsPersonIdContactsPost(personId, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling CreateApi#personsPersonIdContactsPost");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **personId** | **String**| Идентификатор персоны |
 **body** | [**PersonContact**](PersonContact.md)|  | [optional]

### Return type

[**PersonContact**](PersonContact.md)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="personsPersonIdDocumentsPost"></a>
# **personsPersonIdDocumentsPost**
> PersonDocument personsPersonIdDocumentsPost(personId, body)

Создание нового документа персоны

1. ищется персона с указанным id.&lt;br/&gt;2. Создается документ с указанными параметрами.&lt;br/&gt; 3. заполняется таблица person_document.&lt;br/&gt; 4. В результате возвращается созданная структура описания документа&lt;br/&gt;

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import CreateApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

CreateApi apiInstance = new CreateApi();
String personId = "personId_example"; // String | Идентификатор персоны
PersonDocument body = new PersonDocument(); // PersonDocument | 
try {
    PersonDocument result = apiInstance.personsPersonIdDocumentsPost(personId, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling CreateApi#personsPersonIdDocumentsPost");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **personId** | **String**| Идентификатор персоны |
 **body** | [**PersonDocument**](PersonDocument.md)|  | [optional]

### Return type

[**PersonDocument**](PersonDocument.md)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="personsPersonIdEducationPost"></a>
# **personsPersonIdEducationPost**
> PersonEducation personsPersonIdEducationPost(personId, body)

Добавление новых данных об обучении

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import CreateApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

CreateApi apiInstance = new CreateApi();
String personId = "personId_example"; // String | Идентификатор персоны
PersonEducation body = new PersonEducation(); // PersonEducation | 
try {
    PersonEducation result = apiInstance.personsPersonIdEducationPost(personId, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling CreateApi#personsPersonIdEducationPost");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **personId** | **String**| Идентификатор персоны |
 **body** | [**PersonEducation**](PersonEducation.md)|  | [optional]

### Return type

[**PersonEducation**](PersonEducation.md)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="personsPersonIdPreventionsPost"></a>
# **personsPersonIdPreventionsPost**
> PersonPrevention personsPersonIdPreventionsPost(personId, body)

Добавление информации об учете персоны

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import CreateApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

CreateApi apiInstance = new CreateApi();
String personId = "personId_example"; // String | Идентификатор персоны
PersonPrevention body = new PersonPrevention(); // PersonPrevention | 
try {
    PersonPrevention result = apiInstance.personsPersonIdPreventionsPost(personId, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling CreateApi#personsPersonIdPreventionsPost");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **personId** | **String**| Идентификатор персоны |
 **body** | [**PersonPrevention**](PersonPrevention.md)|  | [optional]

### Return type

[**PersonPrevention**](PersonPrevention.md)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

