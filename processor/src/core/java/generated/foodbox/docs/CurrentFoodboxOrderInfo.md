# CurrentFoodboxOrderInfo

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **Long** | Идентификатор заказа. | 
**foodboxOrderNumber** | **Long** | Номер фудбокс-заказа | 
**status** | **String** | Статус заказа в системе | 
**dishes** | [**List&lt;OrderDish&gt;**](OrderDish.md) |  | 
**expiresAt** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время, до которого клиент может забрать заказ | 
**timeOrder** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время создания заказа | 
**orderPrice** | **Long** | Общая стоимость заказа в копейках | 
