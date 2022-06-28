# swagger-java-client

## Requirements

Building the API client library requires [Maven](https://maven.apache.org/) to be installed.

## Installation

To install the API client library to your local Maven repository, simply execute:

```shell
mvn install
```

To deploy it to a remote Maven repository instead, configure the settings of the repository and execute:

```shell
mvn deploy
```

Refer to the [official documentation](https://maven.apache.org/plugins/maven-deploy-plugin/usage.html) for more information.

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
    <groupId>io.swagger</groupId>
    <artifactId>swagger-java-client</artifactId>
    <version>1.0.0</version>
    <scope>compile</scope>
</dependency>
```

### Gradle users

Add this dependency to your project's build file:

```groovy
compile "io.swagger:swagger-java-client:1.0.0"
```

### Others

At first generate the JAR by executing:

    mvn package

Then manually install the following JARs:

* target/swagger-java-client-1.0.0.jar
* target/lib/*.jar

## Getting Started

Please follow the [installation](#installation) instruction and execute the following Java code:

```java
import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.DefaultApi;

import java.io.File;
import java.util.*;

public class DefaultApiExample {

    public static void main(String[] args) {
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
    }
}
```

## Documentation for API Endpoints

All URIs are relative to *http://mes-api.mos.ru/meals*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*DefaultApi* | [**getClientData**](docs/DefaultApi.md#getClientData) | **GET** /clients | Возвращает данные клиента
*DefaultApi* | [**getPersonBuffetMenu**](docs/DefaultApi.md#getPersonBuffetMenu) | **GET** /menu/buffet | Возвращает меню буфета.
*DefaultApi* | [**getPersonComplexMenu**](docs/DefaultApi.md#getPersonComplexMenu) | **GET** /menu/complexes | Возвращает меню столовой.
*DefaultApi* | [**setPersonExpenseConstraints**](docs/DefaultApi.md#setPersonExpenseConstraints) | **PUT** /clients/expenseConstraints | Устанавливает ограничение на покупку.
*DefaultApi* | [**setPersonFoodboxAllowed**](docs/DefaultApi.md#setPersonFoodboxAllowed) | **PUT** /clients/foodboxAllowed | Устанавливает разрешение по Фудбоксу для клиента
*DefaultApi* | [**setPersonPreorderAllowed**](docs/DefaultApi.md#setPersonPreorderAllowed) | **PUT** /clients/preorderAllowed | Устанавливает согласие представителя на получение услуги предзаказа
*_Api* | [**addPersonFoodboxOrder**](docs/_Api.md#addPersonFoodboxOrder) | **POST** /orders/foodbox | Создаёт заказ
*_Api* | [**addPersonRegularOrders**](docs/_Api.md#addPersonRegularOrders) | **POST** /orders/regular | Добавляет заказы.
*_Api* | [**addPersonSingleOrder**](docs/_Api.md#addPersonSingleOrder) | **POST** /orders/single | Создаёт заказ
*_Api* | [**deletePersonFoodboxOrder**](docs/_Api.md#deletePersonFoodboxOrder) | **DELETE** /orders/foodbox/{foodboxOrderId} | Удаляет заказ по идентификатору заказа.
*_Api* | [**deletePersonRegularOrders**](docs/_Api.md#deletePersonRegularOrders) | **DELETE** /orders/regular/{orderId} | Удаляет заказы.
*_Api* | [**deletePersonSingleOrders**](docs/_Api.md#deletePersonSingleOrders) | **DELETE** /orders/single/{orderId} | Удаляет заказ.
*_Api* | [**getPersonFoodboxOrder**](docs/_Api.md#getPersonFoodboxOrder) | **GET** /orders/foodbox/{foodboxOrderId} | Возвращает заказ по идентификатору заказа.
*_Api* | [**getPersonFoodboxOrders**](docs/_Api.md#getPersonFoodboxOrders) | **GET** /orders/foodbox | Возвращает список всех фудбокс-заказов.
*_Api* | [**getPersonHandedOrders**](docs/_Api.md#getPersonHandedOrders) | **GET** /orders/handed | Возвращает список выданных заказов.
*_Api* | [**getPersonOrdersSummary**](docs/_Api.md#getPersonOrdersSummary) | **GET** /orders/summary | Отчёт по предзаказам на 14 дней
*_Api* | [**getPersonProhibitions**](docs/_Api.md#getPersonProhibitions) | **GET** /menu/prohibitions | Возвращает список ограничений.
*_Api* | [**getPersonRegularOrders**](docs/_Api.md#getPersonRegularOrders) | **GET** /orders/regular | Возвращает список заказов.
*_Api* | [**getPersonSingleOrders**](docs/_Api.md#getPersonSingleOrders) | **GET** /orders/single | Возвращает список заказов.
*_Api* | [**setPersonProhibitions**](docs/_Api.md#setPersonProhibitions) | **PUT** /menu/prohibitions | Установка ограничений на категории, подкатегории, блюда для клиента.
*_Api* | [**setPersonSingleOrderAmount**](docs/_Api.md#setPersonSingleOrderAmount) | **PUT** /orders/single/{orderId} | Обновляет отдельные параметры заказа.
*_Api* | [**updatePersonRegularOrder**](docs/_Api.md#updatePersonRegularOrder) | **PUT** /orders/regular/{orderId} | Обновляет заказ.

## Documentation for Models

 - [BuffetCategory](docs/BuffetCategory.md)
 - [BuffetSubcategory](docs/BuffetSubcategory.md)
 - [ClientData](docs/ClientData.md)
 - [ClientId](docs/ClientId.md)
 - [Complex](docs/Complex.md)
 - [CurrentFoodboxOrderInfo](docs/CurrentFoodboxOrderInfo.md)
 - [Dish](docs/Dish.md)
 - [Error](docs/Error.md)
 - [FoodboxOrder](docs/FoodboxOrder.md)
 - [FoodboxOrderInfo](docs/FoodboxOrderInfo.md)
 - [HandedOrder](docs/HandedOrder.md)
 - [HandedOrderItem](docs/HandedOrderItem.md)
 - [HandedOrders](docs/HandedOrders.md)
 - [HistoryFoodboxOrderInfo](docs/HistoryFoodboxOrderInfo.md)
 - [OrderDaySummary](docs/OrderDaySummary.md)
 - [OrderDish](docs/OrderDish.md)
 - [OrdersSummary](docs/OrdersSummary.md)
 - [Organization](docs/Organization.md)
 - [PersonBuffetMenu](docs/PersonBuffetMenu.md)
 - [PersonComplexMenu](docs/PersonComplexMenu.md)
 - [Prohibition](docs/Prohibition.md)
 - [Prohibitions](docs/Prohibitions.md)
 - [RegularOrder](docs/RegularOrder.md)
 - [RegularOrders](docs/RegularOrders.md)
 - [SingleOrder](docs/SingleOrder.md)
 - [WeekSchedule](docs/WeekSchedule.md)

## Documentation for Authorization

Authentication schemes defined for the API:
### agent

- **Type**: API key
- **API key parameter name**: agent
- **Location**: HTTP header

### apiKey

- **Type**: API key
- **API key parameter name**: X-Api-Key
- **Location**: HTTP header

### bearerAuth



## Recommendation

It's recommended to create an instance of `ApiClient` per thread in a multithreaded environment to avoid any potential issues.

## Author


