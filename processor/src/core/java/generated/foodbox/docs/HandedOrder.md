# HandedOrder

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **Long** | Идентификатор заказа | 
**occuredAt** | **String** | Дата и время пробития заказа. | 
**orderType** | **Integer** | Тип заказа: 1 - заказ на комплексное питание; 2 - заказ на дополнительные блюда | 
**deliveryWay** | **Integer** | Способ выдачи: 1 - Стандартный; 2 - Фудбокс |  [optional]
**sum** | **Long** | Сумма покупки в копейках | 
**cancel** | **Boolean** | Признак сторнирования. | 
**items** | [**List&lt;HandedOrderItem&gt;**](HandedOrderItem.md) | Блюда в заказе. |  [optional]
