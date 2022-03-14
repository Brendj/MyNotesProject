# FoodboxOrderInfo

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**foodboxOrderId** | **Long** | Идентификатор Фудбокс-заказа, передаваемый от ИС ПП | 
**status** | **String** | Статус заказа в системе. При запросе списка заказов передавать статусы всех заказов, в т.ч. и текущих | 
**expiredAt** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время, до которого клиент может забрать заказ | 
**createdAt** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время создания заказа | 
**dishes** | [**List&lt;OrderDish&gt;**](OrderDish.md) |  | 
**totalPrice** | **Long** | Общая стоимость заказа в копейках | 
