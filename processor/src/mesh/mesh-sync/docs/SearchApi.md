# SearchApi

All URIs are relative to */*

Method | HTTP request | Description
------------- | ------------- | -------------
[**classesGet**](SearchApi.md#classesGet) | **GET** /classes | Поиск классов
[**personsGet**](SearchApi.md#personsGet) | **GET** /persons | Поиск персон

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
//import SearchApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

SearchApi apiInstance = new SearchApi();
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
    System.err.println("Exception when calling SearchApi#classesGet");
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
//import SearchApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

SearchApi apiInstance = new SearchApi();
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
    System.err.println("Exception when calling SearchApi#personsGet");
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

