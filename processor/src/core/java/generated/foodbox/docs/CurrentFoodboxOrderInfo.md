# CurrentFoodboxOrderInfo

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**foodboxOrderId** | **Long** | Идентификатор Фудбокс-заказа, передаваемый от ИС ПП | 
**status** | **String** | Статус заказа в системе | 
**dishes** | [**List&lt;OrderDish&gt;**](OrderDish.md) |  | 
**expiredAt** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время, до которого клиент может забрать заказ | 
**createdAt** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время создания заказа | 
**totalPrice** | **Long** | Общая стоимость заказа в копейках | 
**balanceLimit** | **Long** | Лимит дневных трат | 
**balance** | **Long** | Остаток денежных средств | 
