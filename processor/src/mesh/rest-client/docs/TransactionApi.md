# TransactionApi

All URIs are relative to */*

Method | HTTP request | Description
------------- | ------------- | -------------
[**transaction**](TransactionApi.md#transaction) | **POST** /transaction | Выполнение набора операций в рамках транзакции

<a name="transaction"></a>
# **transaction**
> transaction(body)

Выполнение набора операций в рамках транзакции

Выполнение набора операций в рамках транзакции

### Example
```java
// Import classes:
//import ApiClient;
//import ApiException;
//import Configuration;
//import io.swagger.client.auth.*;
//import TransactionApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: ApiKeyAuth
ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
ApiKeyAuth.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//ApiKeyAuth.setApiKeyPrefix("Token");

TransactionApi apiInstance = new TransactionApi();
List<Transaction> body = Arrays.asList(new Transaction()); // List<Transaction> | Список элементов для выполнения операций в рамках транзакции
try {
    apiInstance.transaction(body);
} catch (ApiException e) {
    System.err.println("Exception when calling TransactionApi#transaction");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**List&lt;Transaction&gt;**](Transaction.md)| Список элементов для выполнения операций в рамках транзакции |

### Return type

null (empty response body)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

