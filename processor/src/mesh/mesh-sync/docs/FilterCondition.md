# FilterCondition

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**and** | [**List&lt;FilterCondition&gt;**](FilterCondition.md) |  |  [optional]
**or** | [**List&lt;FilterCondition&gt;**](FilterCondition.md) |  |  [optional]
**field** | **String** | Название поля |  [optional]
**op** | **String** | Операция |  [optional]
**value** | **Object** | Значение. Для операций between ожидается массив из 2х элементов, для операции in - массив из любого количества элементов. Для операций. Для остальных операция ожидается строковое значение |  [optional]
