# FoodboxOrder

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **Long** | Идентификатор заказа. | 
**createdAt** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время совершения заказа. | 
**dishes** | [**List&lt;OrderDish&gt;**](OrderDish.md) | Список блюд в заказе. |  [optional]
**orderPrice** | **Long** | Общая стоимость заказа в копейках |  [optional]
