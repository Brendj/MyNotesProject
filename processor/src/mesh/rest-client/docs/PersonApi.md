# PersonApi

All URIs are relative to */*

Method | HTTP request | Description
------------- | ------------- | -------------
[**personsBatchEducationPost**](PersonApi.md#personsBatchEducationPost) | **POST** /persons/batch/education | Пакетное создание данных об обучении персоны
[**personsBatchEducationPut**](PersonApi.md#personsBatchEducationPut) | **PUT** /persons/batch/education | Пакетное изменение данных об обучении персоны
[**personsGet**](PersonApi.md#personsGet) | **GET** /persons | Поиск персон
[**personsIdGet**](PersonApi.md#personsIdGet) | **GET** /persons/{id} | Получить персону по идентификатору
[**personsIdPut**](PersonApi.md#personsIdPut) | **PUT** /persons/{id} | Изменение основных данных персоны
[**personsPersonIdAddressesIdDelete**](PersonApi.md#personsPersonIdAddressesIdDelete) | **DELETE** /persons/{person_id}/addresses/{id} | Удаление адреса персоны
[**personsPersonIdAddressesIdPut**](PersonApi.md#personsPersonIdAddressesIdPut) | **PUT** /persons/{person_id}/addresses/{id} | Изменение адреса персоны
[**personsPersonIdAddressesPost**](PersonApi.md#personsPersonIdAddressesPost) | **POST** /persons/{person_id}/addresses | Создание нового адреса персоны
[**personsPersonIdAgentsIdDelete**](PersonApi.md#personsPersonIdAgentsIdDelete) | **DELETE** /persons/{person_id}/agents/{id} | Удаление представителя персоны
[**personsPersonIdAgentsIdPut**](PersonApi.md#personsPersonIdAgentsIdPut) | **PUT** /persons/{person_id}/agents/{id} | Изменение связи персоны и представителя
[**personsPersonIdAgentsPost**](PersonApi.md#personsPersonIdAgentsPost) | **POST** /persons/{person_id}/agents | 
[**personsPersonIdCategoryIdDelete**](PersonApi.md#personsPersonIdCategoryIdDelete) | **DELETE** /persons/{person_id}/category/{id} | Удаление данных о категории персоны
[**personsPersonIdCategoryIdPut**](PersonApi.md#personsPersonIdCategoryIdPut) | **PUT** /persons/{person_id}/category/{id} | Изменение данных о категории
[**personsPersonIdCategoryPost**](PersonApi.md#personsPersonIdCategoryPost) | **POST** /persons/{person_id}/category | Добавление новых данных о категории
[**personsPersonIdContactsIdDelete**](PersonApi.md#personsPersonIdContactsIdDelete) | **DELETE** /persons/{person_id}/contacts/{id} | Удаление контакта персоны
[**personsPersonIdContactsIdPut**](PersonApi.md#personsPersonIdContactsIdPut) | **PUT** /persons/{person_id}/contacts/{id} | Изменение контакта персоны
[**personsPersonIdContactsPost**](PersonApi.md#personsPersonIdContactsPost) | **POST** /persons/{person_id}/contacts | Создание нового контакта персоны
[**personsPersonIdDocumentsIdDelete**](PersonApi.md#personsPersonIdDocumentsIdDelete) | **DELETE** /persons/{person_id}/documents/{id} | Удаление документа персоны
[**personsPersonIdDocumentsIdPut**](PersonApi.md#personsPersonIdDocumentsIdPut) | **PUT** /persons/{person_id}/documents/{id} | Изменение документа персоны
[**personsPersonIdDocumentsPost**](PersonApi.md#personsPersonIdDocumentsPost) | **POST** /persons/{person_id}/documents | Создание нового документа персоны
[**personsPersonIdEducationIdDelete**](PersonApi.md#personsPersonIdEducationIdDelete) | **DELETE** /persons/{person_id}/education/{id} | Удаление данных об обучении персоны. Метод должен использоваться только в случае добавления ошибочной записи об обучении. В случае отчисления необходимо использовать метод PUT.
[**personsPersonIdEducationIdPut**](PersonApi.md#personsPersonIdEducationIdPut) | **PUT** /persons/{person_id}/education/{id} | Изменение данных об обучении персоны
[**personsPersonIdEducationPost**](PersonApi.md#personsPersonIdEducationPost) | **POST** /persons/{person_id}/education | Добавление новых данных об обучении
[**personsPersonIdIdsPut**](PersonApi.md#personsPersonIdIdsPut) | **PUT** /persons/{person_id}/ids | Изменение идентификаторов персоны
[**personsPersonIdMergePost**](PersonApi.md#personsPersonIdMergePost) | **POST** /persons/{person_id}/merge | Слияние двух или более персон
[**personsPersonIdMergesGet**](PersonApi.md#personsPersonIdMergesGet) | **GET** /persons/{person_id}/merges | Получить даты слияний персоны с другими. При слиянии персона должна была выступать в качестве основной.
[**personsPersonIdPreventionsIdDelete**](PersonApi.md#personsPersonIdPreventionsIdDelete) | **DELETE** /persons/{person_id}/preventions/{id} | Удаление информации об учете
[**personsPersonIdPreventionsIdPut**](PersonApi.md#personsPersonIdPreventionsIdPut) | **PUT** /persons/{person_id}/preventions/{id} | Изменение данных об учете персоны
[**personsPersonIdPreventionsPost**](PersonApi.md#personsPersonIdPreventionsPost) | **POST** /persons/{person_id}/preventions | Добавление информации об учете персоны
[**personsPersonIdUnmergePost**](PersonApi.md#personsPersonIdUnmergePost) | **POST** /persons/{person_id}/unmerge | Откат слияния персон
[**personsPost**](PersonApi.md#personsPost) | **POST** /persons | Создание персоны

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
//import PersonApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

PersonApi apiInstance = new PersonApi();
List<PersonEducation> body = Arrays.asList(new PersonEducation()); // List<PersonEducation> | 
try {
    apiInstance.personsBatchEducationPost(body);
} catch (ApiException e) {
    System.err.println("Exception when calling PersonApi#personsBatchEducationPost");
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
//import PersonApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

PersonApi apiInstance = new PersonApi();
List<PersonEducation> body = Arrays.asList(new PersonEducation()); // List<PersonEducation> | 
try {
    apiInstance.personsBatchEducationPut(body);
} catch (ApiException e) {
    System.err.println("Exception when calling PersonApi#personsBatchEducationPut");
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

<a name="personsGet"></a>
# **personsGet**
> List&lt;PersonInfo&gt; personsGet(filter, expand, top, skip, orderby, actualOn, trainingOn)

Поиск персон

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import PersonApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

PersonApi apiInstance = new PersonApi();
String filter = "filter_example"; // String | сериализованная коллекция /components/schemas/Filter
String expand = "expand_example"; // String | Список полей, которые должны быть \"раскрыты\" в ответе. Перечисляются через запятую. Если параметр не указан, возвращаются только основные данные персоны.
String top = "top_example"; // String | Количество возвращаемых записей. Значение по умолчанию задается в параметрах Системы.
String skip = "skip_example"; // String | Количество пропускаемых записей. По умолчанию, 0.
String orderby = "orderby_example"; // String | Cписок полей, по которым требуется сортировать, через запятую
OffsetDateTime actualOn = new OffsetDateTime(); // OffsetDateTime | Дата актуальности данных. По умолчанию, текущая. Пример: 2019-12-01T00:00:00.000Z
LocalDate trainingOn = new LocalDate(); // LocalDate | Дата нахождения в ОО/классе. Пример: 2019-12-01. По умолчанию, не установлена для возможности искать персоны, которые нигде не обучаются.
try {
    List<PersonInfo> result = apiInstance.personsGet(filter, expand, top, skip, orderby, actualOn, trainingOn);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling PersonApi#personsGet");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **filter** | **String**| сериализованная коллекция /components/schemas/Filter |
 **expand** | **String**| Список полей, которые должны быть \&quot;раскрыты\&quot; в ответе. Перечисляются через запятую. Если параметр не указан, возвращаются только основные данные персоны. | [optional]
 **top** | **String**| Количество возвращаемых записей. Значение по умолчанию задается в параметрах Системы. | [optional]
 **skip** | **String**| Количество пропускаемых записей. По умолчанию, 0. | [optional]
 **orderby** | **String**| Cписок полей, по которым требуется сортировать, через запятую | [optional]
 **actualOn** | **OffsetDateTime**| Дата актуальности данных. По умолчанию, текущая. Пример: 2019-12-01T00:00:00.000Z | [optional]
 **trainingOn** | **LocalDate**| Дата нахождения в ОО/классе. Пример: 2019-12-01. По умолчанию, не установлена для возможности искать персоны, которые нигде не обучаются. | [optional]

### Return type

[**List&lt;PersonInfo&gt;**](PersonInfo.md)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="personsIdGet"></a>
# **personsIdGet**
> PersonInfo personsIdGet(id, actualOn, expand)

Получить персону по идентификатору

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import PersonApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

PersonApi apiInstance = new PersonApi();
String id = "id_example"; // String | Идентификатор персоны</br> (пример \"449ab5bd-4f09-47a0-ba89-70ae2cd49692\")
OffsetDateTime actualOn = new OffsetDateTime(); // OffsetDateTime | Дата актуальности
String expand = "expand_example"; // String | Список полей, которые должны быть \"раскрыты\" в ответе. Перечисляются через запятую. Если параметр не указан, возвращаются только основные данные персоны.
try {
    PersonInfo result = apiInstance.personsIdGet(id, actualOn, expand);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling PersonApi#personsIdGet");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **String**| Идентификатор персоны&lt;/br&gt; (пример \&quot;449ab5bd-4f09-47a0-ba89-70ae2cd49692\&quot;) |
 **actualOn** | **OffsetDateTime**| Дата актуальности | [optional]
 **expand** | **String**| Список полей, которые должны быть \&quot;раскрыты\&quot; в ответе. Перечисляются через запятую. Если параметр не указан, возвращаются только основные данные персоны. | [optional]

### Return type

[**PersonInfo**](PersonInfo.md)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: Not defined
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
//import PersonApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

PersonApi apiInstance = new PersonApi();
String id = "id_example"; // String | Идентификатор версии персоны
PersonInfo body = new PersonInfo(); // PersonInfo | 
try {
    PersonInfo result = apiInstance.personsIdPut(id, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling PersonApi#personsIdPut");
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
//import PersonApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

PersonApi apiInstance = new PersonApi();
String personId = "personId_example"; // String | Идентификатор персоны</br> (пример \"449ab5bd-4f09-47a0-ba89-70ae2cd49692\")
String id = "id_example"; // String | Идентификатор версии адреса
try {
    apiInstance.personsPersonIdAddressesIdDelete(personId, id);
} catch (ApiException e) {
    System.err.println("Exception when calling PersonApi#personsPersonIdAddressesIdDelete");
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
//import PersonApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

PersonApi apiInstance = new PersonApi();
String personId = "personId_example"; // String | Идентификатор персоны</br> (пример \"449ab5bd-4f09-47a0-ba89-70ae2cd49692\")
String id = "id_example"; // String | Идентификатор версии адреса
PersonAddress body = new PersonAddress(); // PersonAddress | 
try {
    PersonAddress result = apiInstance.personsPersonIdAddressesIdPut(personId, id, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling PersonApi#personsPersonIdAddressesIdPut");
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
//import PersonApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

PersonApi apiInstance = new PersonApi();
String personId = "personId_example"; // String | Идентификатор персоны</br> (пример \"449ab5bd-4f09-47a0-ba89-70ae2cd49692\")
PersonAddress body = new PersonAddress(); // PersonAddress | 
try {
    PersonAddress result = apiInstance.personsPersonIdAddressesPost(personId, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling PersonApi#personsPersonIdAddressesPost");
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
//import PersonApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

PersonApi apiInstance = new PersonApi();
String personId = "personId_example"; // String | Идентификатор персоны
String id = "id_example"; // String | Идентификатор представителя
try {
    apiInstance.personsPersonIdAgentsIdDelete(personId, id);
} catch (ApiException e) {
    System.err.println("Exception when calling PersonApi#personsPersonIdAgentsIdDelete");
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
//import PersonApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

PersonApi apiInstance = new PersonApi();
String personId = "personId_example"; // String | Идентификатор персоны
String id = "id_example"; // String | Идентификатор связи с представителем
PersonAgent body = new PersonAgent(); // PersonAgent | 
try {
    PersonAgent result = apiInstance.personsPersonIdAgentsIdPut(personId, id, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling PersonApi#personsPersonIdAgentsIdPut");
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
//import PersonApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

PersonApi apiInstance = new PersonApi();
String personId = "personId_example"; // String | Идентификатор персоны
PersonAgent body = new PersonAgent(); // PersonAgent | 
try {
    PersonAgent result = apiInstance.personsPersonIdAgentsPost(personId, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling PersonApi#personsPersonIdAgentsPost");
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
//import PersonApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

PersonApi apiInstance = new PersonApi();
String personId = "personId_example"; // String | Идентификатор персоны
String id = "id_example"; // String | Идентификатор связи
try {
    apiInstance.personsPersonIdCategoryIdDelete(personId, id);
} catch (ApiException e) {
    System.err.println("Exception when calling PersonApi#personsPersonIdCategoryIdDelete");
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
//import PersonApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

PersonApi apiInstance = new PersonApi();
String personId = "personId_example"; // String | Идентификатор персоны
String id = "id_example"; // String | Идентификатор связи
PersonCategory body = new PersonCategory(); // PersonCategory | 
try {
    PersonCategory result = apiInstance.personsPersonIdCategoryIdPut(personId, id, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling PersonApi#personsPersonIdCategoryIdPut");
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
//import PersonApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

PersonApi apiInstance = new PersonApi();
String personId = "personId_example"; // String | Идентификатор персоны
PersonCategory body = new PersonCategory(); // PersonCategory | 
try {
    PersonCategory result = apiInstance.personsPersonIdCategoryPost(personId, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling PersonApi#personsPersonIdCategoryPost");
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
//import PersonApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

PersonApi apiInstance = new PersonApi();
String personId = "personId_example"; // String | Идентификатор персоны
String id = "id_example"; // String | Идентификатор версии контакта
try {
    apiInstance.personsPersonIdContactsIdDelete(personId, id);
} catch (ApiException e) {
    System.err.println("Exception when calling PersonApi#personsPersonIdContactsIdDelete");
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
//import PersonApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

PersonApi apiInstance = new PersonApi();
String personId = "personId_example"; // String | Идентификатор персоны
String id = "id_example"; // String | Идентификатор контакта
PersonContact body = new PersonContact(); // PersonContact | 
try {
    PersonContact result = apiInstance.personsPersonIdContactsIdPut(personId, id, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling PersonApi#personsPersonIdContactsIdPut");
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
//import PersonApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

PersonApi apiInstance = new PersonApi();
String personId = "personId_example"; // String | Идентификатор персоны
PersonContact body = new PersonContact(); // PersonContact | 
try {
    PersonContact result = apiInstance.personsPersonIdContactsPost(personId, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling PersonApi#personsPersonIdContactsPost");
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
//import PersonApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

PersonApi apiInstance = new PersonApi();
String personId = "personId_example"; // String | Идентификатор персоны
String id = "id_example"; // String | Идентификатор документа
try {
    apiInstance.personsPersonIdDocumentsIdDelete(personId, id);
} catch (ApiException e) {
    System.err.println("Exception when calling PersonApi#personsPersonIdDocumentsIdDelete");
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
//import PersonApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

PersonApi apiInstance = new PersonApi();
String personId = "personId_example"; // String | Идентификатор персоны
String id = "id_example"; // String | Идентификатор документа
PersonDocument body = new PersonDocument(); // PersonDocument | 
try {
    PersonDocument result = apiInstance.personsPersonIdDocumentsIdPut(personId, id, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling PersonApi#personsPersonIdDocumentsIdPut");
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
//import PersonApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

PersonApi apiInstance = new PersonApi();
String personId = "personId_example"; // String | Идентификатор персоны
PersonDocument body = new PersonDocument(); // PersonDocument | 
try {
    PersonDocument result = apiInstance.personsPersonIdDocumentsPost(personId, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling PersonApi#personsPersonIdDocumentsPost");
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
//import PersonApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

PersonApi apiInstance = new PersonApi();
String personId = "personId_example"; // String | Идентификатор персоны
String id = "id_example"; // String | Идентификатор связи
try {
    apiInstance.personsPersonIdEducationIdDelete(personId, id);
} catch (ApiException e) {
    System.err.println("Exception when calling PersonApi#personsPersonIdEducationIdDelete");
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
//import PersonApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

PersonApi apiInstance = new PersonApi();
String personId = "personId_example"; // String | Идентификатор персоны
String id = "id_example"; // String | Идентификатор связи
PersonEducation body = new PersonEducation(); // PersonEducation | 
try {
    PersonEducation result = apiInstance.personsPersonIdEducationIdPut(personId, id, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling PersonApi#personsPersonIdEducationIdPut");
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
//import PersonApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

PersonApi apiInstance = new PersonApi();
String personId = "personId_example"; // String | Идентификатор персоны
PersonEducation body = new PersonEducation(); // PersonEducation | 
try {
    PersonEducation result = apiInstance.personsPersonIdEducationPost(personId, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling PersonApi#personsPersonIdEducationPost");
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
//import PersonApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

PersonApi apiInstance = new PersonApi();
String personId = "personId_example"; // String | Идентификатор персоны
Person body = new Person(); // Person | 
try {
    Person result = apiInstance.personsPersonIdIdsPut(personId, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling PersonApi#personsPersonIdIdsPut");
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

<a name="personsPersonIdMergePost"></a>
# **personsPersonIdMergePost**
> PersonInfo personsPersonIdMergePost(personId, body)

Слияние двух или более персон

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import PersonApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

PersonApi apiInstance = new PersonApi();
String personId = "personId_example"; // String | Идентификатор версии персоны
Body body = new Body(); // Body | 
try {
    PersonInfo result = apiInstance.personsPersonIdMergePost(personId, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling PersonApi#personsPersonIdMergePost");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **personId** | **String**| Идентификатор версии персоны |
 **body** | [**Body**](Body.md)|  | [optional]

### Return type

[**PersonInfo**](PersonInfo.md)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="personsPersonIdMergesGet"></a>
# **personsPersonIdMergesGet**
> List&lt;OffsetDateTime&gt; personsPersonIdMergesGet(personId)

Получить даты слияний персоны с другими. При слиянии персона должна была выступать в качестве основной.

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import PersonApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

PersonApi apiInstance = new PersonApi();
String personId = "personId_example"; // String | Идентификатор персоны
try {
    List<OffsetDateTime> result = apiInstance.personsPersonIdMergesGet(personId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling PersonApi#personsPersonIdMergesGet");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **personId** | **String**| Идентификатор персоны |

### Return type

[**List&lt;OffsetDateTime&gt;**](OffsetDateTime.md)

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
//import PersonApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

PersonApi apiInstance = new PersonApi();
String personId = "personId_example"; // String | Идентификатор персоны
String id = "id_example"; // String | Идентификатор связи
try {
    apiInstance.personsPersonIdPreventionsIdDelete(personId, id);
} catch (ApiException e) {
    System.err.println("Exception when calling PersonApi#personsPersonIdPreventionsIdDelete");
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
//import PersonApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

PersonApi apiInstance = new PersonApi();
String personId = "personId_example"; // String | Идентификатор персоны
String id = "id_example"; // String | Идентификатор связи
PersonPrevention body = new PersonPrevention(); // PersonPrevention | 
try {
    PersonPrevention result = apiInstance.personsPersonIdPreventionsIdPut(personId, id, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling PersonApi#personsPersonIdPreventionsIdPut");
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
//import PersonApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

PersonApi apiInstance = new PersonApi();
String personId = "personId_example"; // String | Идентификатор персоны
PersonPrevention body = new PersonPrevention(); // PersonPrevention | 
try {
    PersonPrevention result = apiInstance.personsPersonIdPreventionsPost(personId, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling PersonApi#personsPersonIdPreventionsPost");
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

<a name="personsPersonIdUnmergePost"></a>
# **personsPersonIdUnmergePost**
> PersonInfo personsPersonIdUnmergePost(personId, body)

Откат слияния персон

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import PersonApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

PersonApi apiInstance = new PersonApi();
String personId = "personId_example"; // String | Идентификатор персоны
Body1 body = new Body1(); // Body1 | 
try {
    PersonInfo result = apiInstance.personsPersonIdUnmergePost(personId, body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling PersonApi#personsPersonIdUnmergePost");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **personId** | **String**| Идентификатор персоны |
 **body** | [**Body1**](Body1.md)|  | [optional]

### Return type

[**PersonInfo**](PersonInfo.md)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="personsPost"></a>
# **personsPost**
> PersonInfo personsPost(body)

Создание персоны

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import PersonApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

PersonApi apiInstance = new PersonApi();
PersonInfo body = new PersonInfo(); // PersonInfo | 
try {
    PersonInfo result = apiInstance.personsPost(body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling PersonApi#personsPost");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**PersonInfo**](PersonInfo.md)|  | [optional]

### Return type

[**PersonInfo**](PersonInfo.md)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

