# SingleOrder

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **Long** | Идентификатор предзаказа. Игнорируется при создании | 
**onDate** | [**LocalDate**](LocalDate.md) | Дата, на которую сделан предзаказа. | 
**complexId** | **Long** | Идентификатор комплекса. | 
**amount** | **Integer** | Заказываемое количество комплексов. | 
**deliveryWay** | **Integer** | Способ выдачи: 1 - Стандартный; 2 - Фудбокс | 
**dishes** | [**List&lt;OrderDish&gt;**](OrderDish.md) | Список блюд в заказе. |  [optional]
