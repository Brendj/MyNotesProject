# PersonContact

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **Long** | Идентификатор | 
**personId** | [**UUID**](UUID.md) | Идентификатор персоны |  [optional]
**actualFrom** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время начала действия связи |  [optional]
**actualTo** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время окончания действия связи |  [optional]
**createdBy** | [**UUID**](UUID.md) | Идентификатор системы-источника, создавшую запись |  [optional]
**updatedBy** | [**UUID**](UUID.md) | Идентификатор системы-источника, изменившую запись |  [optional]
**createdAt** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время создания |  [optional]
**updatedAt** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время изменения |  [optional]
**typeId** | **Integer** | Тип контакта (каталог НСИ3 &lt;a href&#x3D;\&quot;https://wiki.edu.mos.ru/pages/viewpage.action?pageId&#x3D;18394024\&quot;&gt;CONTACT_TYPE&lt;/href&gt;) | 
**data** | **String** | Данные контакта | 
**_default** | **Boolean** | Признак по умолчанию. У персоны может быть только один default контакт каждого типа. При установке контакта основным, остальные контакты данного типа у персоны получают признак default&#x3D;false. |  [optional]
**type** | [**ContactType**](ContactType.md) |  |  [optional]
**validationStateId** | **Integer** | Статус проверки |  [optional]
**validatedAt** | [**OffsetDateTime**](OffsetDateTime.md) | Дата проверки |  [optional]
**validationErrors** | **String** | Ошибки валидации из внешних систем |  [optional]
