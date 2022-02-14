# FoodboxOrderInfo

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **Long** | Идентификатор заказа. | 
**status** | **String** | Статус заказа в системе. При запросе списка заказов передавать статусы всех заказов, в т.ч. и текущих | 
**expiresAt** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время, до которого клиент может забрать заказ |  [optional]
**timeOrder** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время создания заказа |  [optional]
**issueTime** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время получения заказа. Параметр обязателен для выданных заказов |  [optional]
**foodboxOrderNumber** | **Long** | Номер фудбокс-заказа |  [optional]
**dishes** | [**List&lt;OrderDish&gt;**](OrderDish.md) |  | 
**orderPrice** | **Long** | Общая стоимость заказа в копейках |  [optional]
