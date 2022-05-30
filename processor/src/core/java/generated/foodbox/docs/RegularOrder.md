# RegularOrder

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **Long** | Идентификатор предзаказа. | 
**contractId** | **Long** | Номер лицевого счета клиента | 
**from** | [**LocalDate**](LocalDate.md) | Дата начала действия регуляра. | 
**to** | [**LocalDate**](LocalDate.md) | Дата окончания действия регуляра. | 
**complexId** | **Long** | Идентификатор комплекса. |  [optional]
**dishId** | **Long** | Идентификатор блюда. |  [optional]
**amount** | **Integer** | Заказываемое количество комплексов. | 
**days** | [**WeekSchedule**](WeekSchedule.md) |  | 
