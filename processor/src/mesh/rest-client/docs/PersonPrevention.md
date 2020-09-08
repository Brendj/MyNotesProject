# PersonPrevention

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **Long** | Идентификатор версии | 
**personId** | [**UUID**](UUID.md) | Идентификатор персоны |  [optional]
**preventionTypeId** | **Integer** | Идентификатор вида учета (каталог НСИ3 &lt;a href&#x3D;\&quot;https://wiki.edu.mos.ru/pages/viewpage.action?pageId&#x3D;18394301\&quot;&gt;ACCOUNTING_TYPE&lt;/a&gt;) | 
**startedAt** | [**LocalDate**](LocalDate.md) | Дата постановки на учет | 
**startedDocumentIssued** | [**LocalDate**](LocalDate.md) | Дата документа постановки на учет | 
**startedDocumentNumber** | **String** | Номер документа постановки на учет | 
**startedReasons** | **List&lt;Integer&gt;** | Причины постановки на учет |  [optional]
**finishedAt** | [**LocalDate**](LocalDate.md) | Дата снятия с учета |  [optional]
**finishedDocumentIssued** | [**LocalDate**](LocalDate.md) | Дата документа о снятии с учета |  [optional]
**finishedDocumentNumber** | **String** | Номер документа о снятии с учета |  [optional]
**finishedReasons** | **List&lt;Integer&gt;** | Причины снятия с учета |  [optional]
**validationStateId** | **Integer** | Статус проверки |  [optional]
**validatedAt** | [**OffsetDateTime**](OffsetDateTime.md) | Дата проверки |  [optional]
**actualFrom** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время начала действия связи |  [optional]
**actualTo** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время окончания действия связи |  [optional]
**createdBy** | [**UUID**](UUID.md) | Идентификатор системы-источника, создавшую запись |  [optional]
**updatedBy** | [**UUID**](UUID.md) | Идентификатор системы-источника, изменившую запись |  [optional]
**createdAt** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время создания |  [optional]
**updatedAt** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время изменения |  [optional]
**preventionType** | [**PreventionType**](PreventionType.md) |  |  [optional]
**validationErrors** | **String** | Ошибки валидации из внешних систем |  [optional]
