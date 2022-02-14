# _Api

All URIs are relative to *http://mes-api.mos.ru/meals*

Method | HTTP request | Description
------------- | ------------- | -------------
[**addPersonFoodboxOrder**](_Api.md#addPersonFoodboxOrder) | **POST** /orders/foodbox | Создаёт заказ
[**addPersonRegularOrders**](_Api.md#addPersonRegularOrders) | **POST** /orders/regular | Добавляет заказы.
[**addPersonSingleOrder**](_Api.md#addPersonSingleOrder) | **POST** /orders/single | Создаёт заказ
[**deletePersonFoodboxOrder**](_Api.md#deletePersonFoodboxOrder) | **DELETE** /orders/foodbox | Удаляет заказ.
[**deletePersonRegularOrders**](_Api.md#deletePersonRegularOrders) | **DELETE** /orders/regular/{orderId} | Удаляет заказы.
[**deletePersonSingleOrders**](_Api.md#deletePersonSingleOrders) | **DELETE** /orders/single/{orderId} | Удаляет заказ.
[**getFoodboxInfo**](_Api.md#getFoodboxInfo) | **GET** /foodbox/info | Возвращает список разрешений по Фудбоксу для ОУ и клиента
[**getPersonFoodboxOrderList**](_Api.md#getPersonFoodboxOrderList) | **GET** /orders/foodbox | Возвращает список всех фудбокс-заказов.
[**getPersonHandedOrders**](_Api.md#getPersonHandedOrders) | **GET** /orders/handed | Возвращает список выданных заказов.
[**getPersonOrdersSummary**](_Api.md#getPersonOrdersSummary) | **GET** /orders/summary | Отчёт по предзаказам на 14 дней
[**getPersonProhibitions**](_Api.md#getPersonProhibitions) | **GET** /menu/prohibitions | Возвращает список ограничений.
[**getPersonRegularOrders**](_Api.md#getPersonRegularOrders) | **GET** /orders/regular | Возвращает список заказов.
[**getPersonSingleOrders**](_Api.md#getPersonSingleOrders) | **GET** /orders/single | Возвращает список заказов.
[**putFoodboxInfo**](_Api.md#putFoodboxInfo) | **PUT** /foodbox/info | Устанавливает разрешение по Фудбоксу для клиента
[**setPersonProhibitions**](_Api.md#setPersonProhibitions) | **PUT** /menu/prohibitions | Установка ограничений на категории, подкатегории, блюда для клиента.
[**setPersonSingleOrderAmount**](_Api.md#setPersonSingleOrderAmount) | **PUT** /orders/single/{orderId} | Обновляет отдельные параметры заказа.
[**updatePersonRegularOrder**](_Api.md#updatePersonRegularOrder) | **PUT** /orders/regular/{orderId} | Обновляет заказ.

<a name="addPersonFoodboxOrder"></a>
# **addPersonFoodboxOrder**
> OrderErrorInfo addPersonFoodboxOrder(body, clientId)

Создаёт заказ

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api._Api;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: agent
ApiKeyAuth agent = (ApiKeyAuth) defaultClient.getAuthentication("agent");
agent.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//agent.setApiKeyPrefix("Token");

// Configure API key authorization: apiKey
ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("apiKey");
apiKey.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//apiKey.setApiKeyPrefix("Token");


_Api apiInstance = new _Api();
FoodboxOrder body = new FoodboxOrder(); // FoodboxOrder | 
ClientId clientId = new ClientId(); // ClientId | Идентификатор персоны из МЭШ.Контингент
try {
    OrderErrorInfo result = apiInstance.addPersonFoodboxOrder(body, clientId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling _Api#addPersonFoodboxOrder");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**FoodboxOrder**](FoodboxOrder.md)|  |
 **clientId** | [**ClientId**](.md)| Идентификатор персоны из МЭШ.Контингент |

### Return type

[**OrderErrorInfo**](OrderErrorInfo.md)

### Authorization

[agent](../README.md#agent)[apiKey](../README.md#apiKey)[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="addPersonRegularOrders"></a>
# **addPersonRegularOrders**
> List&lt;Long&gt; addPersonRegularOrders(body, clientId, replace)

Добавляет заказы.

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api._Api;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: agent
ApiKeyAuth agent = (ApiKeyAuth) defaultClient.getAuthentication("agent");
agent.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//agent.setApiKeyPrefix("Token");

// Configure API key authorization: apiKey
ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("apiKey");
apiKey.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//apiKey.setApiKeyPrefix("Token");


_Api apiInstance = new _Api();
List<RegularOrder> body = Arrays.asList(new RegularOrder()); // List<RegularOrder> | 
ClientId clientId = new ClientId(); // ClientId | Идентификатор персоны из МЭШ.Контингент
Boolean replace = true; // Boolean | true - заменить список
try {
    List<Long> result = apiInstance.addPersonRegularOrders(body, clientId, replace);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling _Api#addPersonRegularOrders");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**List&lt;RegularOrder&gt;**](RegularOrder.md)|  |
 **clientId** | [**ClientId**](.md)| Идентификатор персоны из МЭШ.Контингент |
 **replace** | **Boolean**| true - заменить список | [optional]

### Return type

**List&lt;Long&gt;**

### Authorization

[agent](../README.md#agent)[apiKey](../README.md#apiKey)[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="addPersonSingleOrder"></a>
# **addPersonSingleOrder**
> Long addPersonSingleOrder(body, clientId)

Создаёт заказ

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api._Api;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: agent
ApiKeyAuth agent = (ApiKeyAuth) defaultClient.getAuthentication("agent");
agent.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//agent.setApiKeyPrefix("Token");

// Configure API key authorization: apiKey
ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("apiKey");
apiKey.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//apiKey.setApiKeyPrefix("Token");


_Api apiInstance = new _Api();
SingleOrder body = new SingleOrder(); // SingleOrder | 
ClientId clientId = new ClientId(); // ClientId | Идентификатор персоны из МЭШ.Контингент
try {
    Long result = apiInstance.addPersonSingleOrder(body, clientId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling _Api#addPersonSingleOrder");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**SingleOrder**](SingleOrder.md)|  |
 **clientId** | [**ClientId**](.md)| Идентификатор персоны из МЭШ.Контингент |

### Return type

**Long**

### Authorization

[agent](../README.md#agent)[apiKey](../README.md#apiKey)[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="deletePersonFoodboxOrder"></a>
# **deletePersonFoodboxOrder**
> deletePersonFoodboxOrder(clientId, id)

Удаляет заказ.

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api._Api;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: agent
ApiKeyAuth agent = (ApiKeyAuth) defaultClient.getAuthentication("agent");
agent.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//agent.setApiKeyPrefix("Token");

// Configure API key authorization: apiKey
ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("apiKey");
apiKey.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//apiKey.setApiKeyPrefix("Token");


_Api apiInstance = new _Api();
ClientId clientId = new ClientId(); // ClientId | Идентификатор персоны из МЭШ.Контингент
Long id = 789L; // Long | Идентификатор заказа.
try {
    apiInstance.deletePersonFoodboxOrder(clientId, id);
} catch (ApiException e) {
    System.err.println("Exception when calling _Api#deletePersonFoodboxOrder");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **clientId** | [**ClientId**](.md)| Идентификатор персоны из МЭШ.Контингент |
 **id** | **Long**| Идентификатор заказа. | [optional]

### Return type

null (empty response body)

### Authorization

[agent](../README.md#agent)[apiKey](../README.md#apiKey)[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="deletePersonRegularOrders"></a>
# **deletePersonRegularOrders**
> deletePersonRegularOrders(clientId, orderId, id)

Удаляет заказы.

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api._Api;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: agent
ApiKeyAuth agent = (ApiKeyAuth) defaultClient.getAuthentication("agent");
agent.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//agent.setApiKeyPrefix("Token");

// Configure API key authorization: apiKey
ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("apiKey");
apiKey.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//apiKey.setApiKeyPrefix("Token");


_Api apiInstance = new _Api();
ClientId clientId = new ClientId(); // ClientId | Идентификатор персоны из МЭШ.Контингент
Long orderId = 789L; // Long | Идентификатор предзаказа.
List<Long> id = Arrays.asList(56L); // List<Long> | Список идентификаторов заказов. При отсутствии списка удаляются все заказы.
try {
    apiInstance.deletePersonRegularOrders(clientId, orderId, id);
} catch (ApiException e) {
    System.err.println("Exception when calling _Api#deletePersonRegularOrders");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **clientId** | [**ClientId**](.md)| Идентификатор персоны из МЭШ.Контингент |
 **orderId** | **Long**| Идентификатор предзаказа. |
 **id** | [**List&lt;Long&gt;**](Long.md)| Список идентификаторов заказов. При отсутствии списка удаляются все заказы. | [optional]

### Return type

null (empty response body)

### Authorization

[agent](../README.md#agent)[apiKey](../README.md#apiKey)[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="deletePersonSingleOrders"></a>
# **deletePersonSingleOrders**
> deletePersonSingleOrders(orderId)

Удаляет заказ.

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api._Api;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: agent
ApiKeyAuth agent = (ApiKeyAuth) defaultClient.getAuthentication("agent");
agent.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//agent.setApiKeyPrefix("Token");

// Configure API key authorization: apiKey
ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("apiKey");
apiKey.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//apiKey.setApiKeyPrefix("Token");


_Api apiInstance = new _Api();
Long orderId = 789L; // Long | Идентификатор предзаказа.
try {
    apiInstance.deletePersonSingleOrders(orderId);
} catch (ApiException e) {
    System.err.println("Exception when calling _Api#deletePersonSingleOrders");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **orderId** | **Long**| Идентификатор предзаказа. |

### Return type

null (empty response body)

### Authorization

[agent](../README.md#agent)[apiKey](../README.md#apiKey)[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getFoodboxInfo"></a>
# **getFoodboxInfo**
> GetFoodboxInfo getFoodboxInfo(clientId)

Возвращает список разрешений по Фудбоксу для ОУ и клиента

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api._Api;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: agent
ApiKeyAuth agent = (ApiKeyAuth) defaultClient.getAuthentication("agent");
agent.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//agent.setApiKeyPrefix("Token");

// Configure API key authorization: apiKey
ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("apiKey");
apiKey.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//apiKey.setApiKeyPrefix("Token");


_Api apiInstance = new _Api();
ClientId clientId = new ClientId(); // ClientId | Идентификатор персоны из МЭШ.Контингент
try {
    GetFoodboxInfo result = apiInstance.getFoodboxInfo(clientId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling _Api#getFoodboxInfo");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **clientId** | [**ClientId**](.md)| Идентификатор персоны из МЭШ.Контингент |

### Return type

[**GetFoodboxInfo**](GetFoodboxInfo.md)

### Authorization

[agent](../README.md#agent)[apiKey](../README.md#apiKey)[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getPersonFoodboxOrderList"></a>
# **getPersonFoodboxOrderList**
> HistoryFoodboxOrderInfo getPersonFoodboxOrderList(clientId, from, to, foodboxOrderNumber, sort)

Возвращает список всех фудбокс-заказов.

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api._Api;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: agent
ApiKeyAuth agent = (ApiKeyAuth) defaultClient.getAuthentication("agent");
agent.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//agent.setApiKeyPrefix("Token");

// Configure API key authorization: apiKey
ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("apiKey");
apiKey.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//apiKey.setApiKeyPrefix("Token");


_Api apiInstance = new _Api();
ClientId clientId = new ClientId(); // ClientId | Идентификатор персоны из МЭШ.Контингент
LocalDate from = new LocalDate(); // LocalDate | Дата начала выборки.
LocalDate to = new LocalDate(); // LocalDate | Дата конца выборки.
Long foodboxOrderNumber = 789L; // Long | Номер фудбокс-заказа
String sort = "sort_example"; // String | Сортировка по дате факта.
try {
    HistoryFoodboxOrderInfo result = apiInstance.getPersonFoodboxOrderList(clientId, from, to, foodboxOrderNumber, sort);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling _Api#getPersonFoodboxOrderList");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **clientId** | [**ClientId**](.md)| Идентификатор персоны из МЭШ.Контингент |
 **from** | **LocalDate**| Дата начала выборки. |
 **to** | **LocalDate**| Дата конца выборки. |
 **foodboxOrderNumber** | **Long**| Номер фудбокс-заказа |
 **sort** | **String**| Сортировка по дате факта. | [optional] [enum: asc, desc]

### Return type

[**HistoryFoodboxOrderInfo**](HistoryFoodboxOrderInfo.md)

### Authorization

[agent](../README.md#agent)[apiKey](../README.md#apiKey)[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getPersonHandedOrders"></a>
# **getPersonHandedOrders**
> HandedOrders getPersonHandedOrders(clientId, from, to, offset, limit, sort)

Возвращает список выданных заказов.

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api._Api;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: agent
ApiKeyAuth agent = (ApiKeyAuth) defaultClient.getAuthentication("agent");
agent.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//agent.setApiKeyPrefix("Token");

// Configure API key authorization: apiKey
ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("apiKey");
apiKey.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//apiKey.setApiKeyPrefix("Token");


_Api apiInstance = new _Api();
ClientId clientId = new ClientId(); // ClientId | Идентификатор персоны из МЭШ.Контингент
LocalDate from = new LocalDate(); // LocalDate | Дата начала выборки.
LocalDate to = new LocalDate(); // LocalDate | Дата конца выборки.
Integer offset = 56; // Integer | Смещение.
Integer limit = 56; // Integer | Максимальное количество элементов в выборке.
String sort = "sort_example"; // String | Сортировка по дате факта.
try {
    HandedOrders result = apiInstance.getPersonHandedOrders(clientId, from, to, offset, limit, sort);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling _Api#getPersonHandedOrders");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **clientId** | [**ClientId**](.md)| Идентификатор персоны из МЭШ.Контингент |
 **from** | **LocalDate**| Дата начала выборки. |
 **to** | **LocalDate**| Дата конца выборки. |
 **offset** | **Integer**| Смещение. | [optional] [enum: ]
 **limit** | **Integer**| Максимальное количество элементов в выборке. | [optional] [enum: ]
 **sort** | **String**| Сортировка по дате факта. | [optional] [enum: asc, desc]

### Return type

[**HandedOrders**](HandedOrders.md)

### Authorization

[agent](../README.md#agent)[apiKey](../README.md#apiKey)[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getPersonOrdersSummary"></a>
# **getPersonOrdersSummary**
> OrdersSummary getPersonOrdersSummary(clientId)

Отчёт по предзаказам на 14 дней

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api._Api;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: agent
ApiKeyAuth agent = (ApiKeyAuth) defaultClient.getAuthentication("agent");
agent.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//agent.setApiKeyPrefix("Token");

// Configure API key authorization: apiKey
ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("apiKey");
apiKey.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//apiKey.setApiKeyPrefix("Token");


_Api apiInstance = new _Api();
ClientId clientId = new ClientId(); // ClientId | Идентификатор персоны из МЭШ.Контингент
try {
    OrdersSummary result = apiInstance.getPersonOrdersSummary(clientId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling _Api#getPersonOrdersSummary");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **clientId** | [**ClientId**](.md)| Идентификатор персоны из МЭШ.Контингент |

### Return type

[**OrdersSummary**](OrdersSummary.md)

### Authorization

[agent](../README.md#agent)[apiKey](../README.md#apiKey)[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getPersonProhibitions"></a>
# **getPersonProhibitions**
> Prohibitions getPersonProhibitions(clientId)

Возвращает список ограничений.

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api._Api;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: agent
ApiKeyAuth agent = (ApiKeyAuth) defaultClient.getAuthentication("agent");
agent.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//agent.setApiKeyPrefix("Token");

// Configure API key authorization: apiKey
ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("apiKey");
apiKey.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//apiKey.setApiKeyPrefix("Token");


_Api apiInstance = new _Api();
ClientId clientId = new ClientId(); // ClientId | Идентификатор персоны из МЭШ.Контингент
try {
    Prohibitions result = apiInstance.getPersonProhibitions(clientId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling _Api#getPersonProhibitions");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **clientId** | [**ClientId**](.md)| Идентификатор персоны из МЭШ.Контингент |

### Return type

[**Prohibitions**](Prohibitions.md)

### Authorization

[agent](../README.md#agent)[apiKey](../README.md#apiKey)[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getPersonRegularOrders"></a>
# **getPersonRegularOrders**
> RegularOrders getPersonRegularOrders(clientId, onDate)

Возвращает список заказов.

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api._Api;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: agent
ApiKeyAuth agent = (ApiKeyAuth) defaultClient.getAuthentication("agent");
agent.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//agent.setApiKeyPrefix("Token");

// Configure API key authorization: apiKey
ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("apiKey");
apiKey.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//apiKey.setApiKeyPrefix("Token");


_Api apiInstance = new _Api();
ClientId clientId = new ClientId(); // ClientId | Идентификатор персоны из МЭШ.Контингент
LocalDate onDate = new LocalDate(); // LocalDate | Дата, на которую необходимо вернуть данные
try {
    RegularOrders result = apiInstance.getPersonRegularOrders(clientId, onDate);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling _Api#getPersonRegularOrders");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **clientId** | [**ClientId**](.md)| Идентификатор персоны из МЭШ.Контингент |
 **onDate** | **LocalDate**| Дата, на которую необходимо вернуть данные |

### Return type

[**RegularOrders**](RegularOrders.md)

### Authorization

[agent](../README.md#agent)[apiKey](../README.md#apiKey)[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getPersonSingleOrders"></a>
# **getPersonSingleOrders**
> List&lt;SingleOrder&gt; getPersonSingleOrders(clientId, onDate)

Возвращает список заказов.

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api._Api;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: agent
ApiKeyAuth agent = (ApiKeyAuth) defaultClient.getAuthentication("agent");
agent.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//agent.setApiKeyPrefix("Token");

// Configure API key authorization: apiKey
ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("apiKey");
apiKey.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//apiKey.setApiKeyPrefix("Token");


_Api apiInstance = new _Api();
ClientId clientId = new ClientId(); // ClientId | Идентификатор персоны из МЭШ.Контингент
LocalDate onDate = new LocalDate(); // LocalDate | Дата, на которую необходимо вернуть данные
try {
    List<SingleOrder> result = apiInstance.getPersonSingleOrders(clientId, onDate);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling _Api#getPersonSingleOrders");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **clientId** | [**ClientId**](.md)| Идентификатор персоны из МЭШ.Контингент |
 **onDate** | **LocalDate**| Дата, на которую необходимо вернуть данные |

### Return type

[**List&lt;SingleOrder&gt;**](SingleOrder.md)

### Authorization

[agent](../README.md#agent)[apiKey](../README.md#apiKey)[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="putFoodboxInfo"></a>
# **putFoodboxInfo**
> putFoodboxInfo(clientId, foodboxAvailability)

Устанавливает разрешение по Фудбоксу для клиента

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api._Api;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: agent
ApiKeyAuth agent = (ApiKeyAuth) defaultClient.getAuthentication("agent");
agent.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//agent.setApiKeyPrefix("Token");

// Configure API key authorization: apiKey
ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("apiKey");
apiKey.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//apiKey.setApiKeyPrefix("Token");


_Api apiInstance = new _Api();
ClientId clientId = new ClientId(); // ClientId | Идентификатор персоны из МЭШ.Контингент
Boolean foodboxAvailability = true; // Boolean | Признак доступности использования фудбокса
try {
    apiInstance.putFoodboxInfo(clientId, foodboxAvailability);
} catch (ApiException e) {
    System.err.println("Exception when calling _Api#putFoodboxInfo");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **clientId** | [**ClientId**](.md)| Идентификатор персоны из МЭШ.Контингент |
 **foodboxAvailability** | **Boolean**| Признак доступности использования фудбокса | [optional]

### Return type

null (empty response body)

### Authorization

[agent](../README.md#agent)[apiKey](../README.md#apiKey)[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="setPersonProhibitions"></a>
# **setPersonProhibitions**
> setPersonProhibitions(clientId, body)

Установка ограничений на категории, подкатегории, блюда для клиента.

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api._Api;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: agent
ApiKeyAuth agent = (ApiKeyAuth) defaultClient.getAuthentication("agent");
agent.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//agent.setApiKeyPrefix("Token");

// Configure API key authorization: apiKey
ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("apiKey");
apiKey.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//apiKey.setApiKeyPrefix("Token");


_Api apiInstance = new _Api();
ClientId clientId = new ClientId(); // ClientId | Идентификатор персоны из МЭШ.Контингент
List<Prohibition> body = Arrays.asList(new Prohibition()); // List<Prohibition> | 
try {
    apiInstance.setPersonProhibitions(clientId, body);
} catch (ApiException e) {
    System.err.println("Exception when calling _Api#setPersonProhibitions");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **clientId** | [**ClientId**](.md)| Идентификатор персоны из МЭШ.Контингент |
 **body** | [**List&lt;Prohibition&gt;**](Prohibition.md)|  | [optional]

### Return type

null (empty response body)

### Authorization

[agent](../README.md#agent)[apiKey](../README.md#apiKey)[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="setPersonSingleOrderAmount"></a>
# **setPersonSingleOrderAmount**
> setPersonSingleOrderAmount(orderId, amount)

Обновляет отдельные параметры заказа.

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api._Api;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: agent
ApiKeyAuth agent = (ApiKeyAuth) defaultClient.getAuthentication("agent");
agent.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//agent.setApiKeyPrefix("Token");

// Configure API key authorization: apiKey
ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("apiKey");
apiKey.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//apiKey.setApiKeyPrefix("Token");


_Api apiInstance = new _Api();
Long orderId = 789L; // Long | Идентификатор предзаказа.
Integer amount = 56; // Integer | Заказываемое количество комплексов.
try {
    apiInstance.setPersonSingleOrderAmount(orderId, amount);
} catch (ApiException e) {
    System.err.println("Exception when calling _Api#setPersonSingleOrderAmount");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **orderId** | **Long**| Идентификатор предзаказа. |
 **amount** | **Integer**| Заказываемое количество комплексов. | [optional]

### Return type

null (empty response body)

### Authorization

[agent](../README.md#agent)[apiKey](../README.md#apiKey)[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="updatePersonRegularOrder"></a>
# **updatePersonRegularOrder**
> updatePersonRegularOrder(body, clientId, orderId)

Обновляет заказ.

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api._Api;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: agent
ApiKeyAuth agent = (ApiKeyAuth) defaultClient.getAuthentication("agent");
agent.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//agent.setApiKeyPrefix("Token");

// Configure API key authorization: apiKey
ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("apiKey");
apiKey.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//apiKey.setApiKeyPrefix("Token");


_Api apiInstance = new _Api();
RegularOrder body = new RegularOrder(); // RegularOrder | 
ClientId clientId = new ClientId(); // ClientId | Идентификатор персоны из МЭШ.Контингент
Long orderId = 789L; // Long | Идентификатор предзаказа.
try {
    apiInstance.updatePersonRegularOrder(body, clientId, orderId);
} catch (ApiException e) {
    System.err.println("Exception when calling _Api#updatePersonRegularOrder");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**RegularOrder**](RegularOrder.md)|  |
 **clientId** | [**ClientId**](.md)| Идентификатор персоны из МЭШ.Контингент |
 **orderId** | **Long**| Идентификатор предзаказа. |

### Return type

null (empty response body)

### Authorization

[agent](../README.md#agent)[apiKey](../README.md#apiKey)[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

