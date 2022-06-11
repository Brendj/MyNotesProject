# DefaultApi

All URIs are relative to *http://mes-api.mos.ru/meals*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getClientData**](DefaultApi.md#getClientData) | **GET** /clients | Возвращает данные клиента
[**getPersonBuffetMenu**](DefaultApi.md#getPersonBuffetMenu) | **GET** /menu/buffet | Возвращает меню буфета.
[**getPersonComplexMenu**](DefaultApi.md#getPersonComplexMenu) | **GET** /menu/complexes | Возвращает меню столовой.
[**setPersonExpenseConstraints**](DefaultApi.md#setPersonExpenseConstraints) | **PUT** /clients/expenseConstraints | Устанавливает ограничение на покупку.
[**setPersonFoodboxAllowed**](DefaultApi.md#setPersonFoodboxAllowed) | **PUT** /clients/foodboxAllowed | Устанавливает разрешение по Фудбоксу для клиента
[**setPersonPreorderAllowed**](DefaultApi.md#setPersonPreorderAllowed) | **PUT** /clients/preorderAllowed | Устанавливает согласие представителя на получение услуги предзаказа

<a name="getClientData"></a>
# **getClientData**
> List&lt;ClientData&gt; getClientData(clientId)

Возвращает данные клиента

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api.DefaultApi;

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


DefaultApi apiInstance = new DefaultApi();
ClientId clientId = new ClientId(); // ClientId | Идентификатор персоны из МЭШ.Контингент
try {
    List<ClientData> result = apiInstance.getClientData(clientId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#getClientData");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **clientId** | [**ClientId**](.md)| Идентификатор персоны из МЭШ.Контингент |

### Return type

[**List&lt;ClientData&gt;**](ClientData.md)

### Authorization

[agent](../README.md#agent)[apiKey](../README.md#apiKey)[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getPersonBuffetMenu"></a>
# **getPersonBuffetMenu**
> PersonBuffetMenu getPersonBuffetMenu(clientId, onDate)

Возвращает меню буфета.

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api.DefaultApi;

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


DefaultApi apiInstance = new DefaultApi();
ClientId clientId = new ClientId(); // ClientId | Идентификатор персоны из МЭШ.Контингент
LocalDate onDate = new LocalDate(); // LocalDate | Дата, на которую необходимо вернуть данные
try {
    PersonBuffetMenu result = apiInstance.getPersonBuffetMenu(clientId, onDate);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#getPersonBuffetMenu");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **clientId** | [**ClientId**](.md)| Идентификатор персоны из МЭШ.Контингент |
 **onDate** | **LocalDate**| Дата, на которую необходимо вернуть данные | [optional]

### Return type

[**PersonBuffetMenu**](PersonBuffetMenu.md)

### Authorization

[agent](../README.md#agent)[apiKey](../README.md#apiKey)[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getPersonComplexMenu"></a>
# **getPersonComplexMenu**
> PersonComplexMenu getPersonComplexMenu(clientId, onDate)

Возвращает меню столовой.

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api.DefaultApi;

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


DefaultApi apiInstance = new DefaultApi();
ClientId clientId = new ClientId(); // ClientId | Идентификатор персоны из МЭШ.Контингент
LocalDate onDate = new LocalDate(); // LocalDate | Дата, на которую необходимо вернуть данные
try {
    PersonComplexMenu result = apiInstance.getPersonComplexMenu(clientId, onDate);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#getPersonComplexMenu");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **clientId** | [**ClientId**](.md)| Идентификатор персоны из МЭШ.Контингент |
 **onDate** | **LocalDate**| Дата, на которую необходимо вернуть данные | [optional]

### Return type

[**PersonComplexMenu**](PersonComplexMenu.md)

### Authorization

[agent](../README.md#agent)[apiKey](../README.md#apiKey)[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="setPersonExpenseConstraints"></a>
# **setPersonExpenseConstraints**
> setPersonExpenseConstraints(clientId, body)

Устанавливает ограничение на покупку.

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api.DefaultApi;

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


DefaultApi apiInstance = new DefaultApi();
ClientId clientId = new ClientId(); // ClientId | Идентификатор персоны из МЭШ.Контингент
ExpenseConstraints body = new ExpenseConstraints(); // ExpenseConstraints | 
try {
    apiInstance.setPersonExpenseConstraints(clientId, body);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#setPersonExpenseConstraints");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **clientId** | [**ClientId**](.md)| Идентификатор персоны из МЭШ.Контингент |
 **body** | [**ExpenseConstraints**](ExpenseConstraints.md)|  | [optional]

### Return type

null (empty response body)

### Authorization

[agent](../README.md#agent)[apiKey](../README.md#apiKey)[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="setPersonFoodboxAllowed"></a>
# **setPersonFoodboxAllowed**
> setPersonFoodboxAllowed(clientId, foodboxAllowed)

Устанавливает разрешение по Фудбоксу для клиента

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api.DefaultApi;

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


DefaultApi apiInstance = new DefaultApi();
ClientId clientId = new ClientId(); // ClientId | Идентификатор персоны из МЭШ.Контингент
Boolean foodboxAllowed = true; // Boolean | Признак разрешения на использование фудбокса
try {
    apiInstance.setPersonFoodboxAllowed(clientId, foodboxAllowed);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#setPersonFoodboxAllowed");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **clientId** | [**ClientId**](.md)| Идентификатор персоны из МЭШ.Контингент |
 **foodboxAllowed** | **Boolean**| Признак разрешения на использование фудбокса |

### Return type

null (empty response body)

### Authorization

[agent](../README.md#agent)[apiKey](../README.md#apiKey)[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="setPersonPreorderAllowed"></a>
# **setPersonPreorderAllowed**
> setPersonPreorderAllowed(clientId, body)

Устанавливает согласие представителя на получение услуги предзаказа

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api.DefaultApi;

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


DefaultApi apiInstance = new DefaultApi();
ClientId clientId = new ClientId(); // ClientId | Идентификатор персоны из МЭШ.Контингент
Boolean body = true; // Boolean | 
try {
    apiInstance.setPersonPreorderAllowed(clientId, body);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#setPersonPreorderAllowed");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **clientId** | [**ClientId**](.md)| Идентификатор персоны из МЭШ.Контингент |
 **body** | [**Boolean**](Boolean.md)|  | [optional]

### Return type

null (empty response body)

### Authorization

[agent](../README.md#agent)[apiKey](../README.md#apiKey)[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

