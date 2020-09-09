# PersonDocument

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **Long** | Идентификатор | 
**personId** | [**UUID**](UUID.md) | Ссылка на уникальный идентификатор персоны |  [optional]
**validationStateId** | **Integer** | Статус проверки |  [optional]
**validatedAt** | [**OffsetDateTime**](OffsetDateTime.md) | Дата проверки |  [optional]
**actualFrom** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время начала действия связи |  [optional]
**actualTo** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время окончания действия связи |  [optional]
**createdBy** | [**UUID**](UUID.md) | Идентификатор системы-источника, создавшую запись |  [optional]
**updatedBy** | [**UUID**](UUID.md) | Идентификатор системы-источника, изменившую запись |  [optional]
**createdAt** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время создания |  [optional]
**updatedAt** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время изменения |  [optional]
**documentTypeId** | **Integer** | Тип документа (каталог НСИ3 &lt;a href&#x3D;\&quot;https://wiki.edu.mos.ru/pages/viewpage.action?pageId&#x3D;18393921\&quot;&gt;TYPE_IDENT_DOC&lt;/href&gt;) | 
**series** | **String** | Серия |  [optional]
**number** | **String** | Номер | 
**subdivisionCode** | **String** | Код подразделения |  [optional]
**issuer** | **String** | Кем выдан |  [optional]
**issued** | [**LocalDate**](LocalDate.md) | Когда выдан |  [optional]
**expiration** | [**LocalDate**](LocalDate.md) | Дата истечения срока действия документа |  [optional]
**documentType** | [**DocumentType**](DocumentType.md) |  |  [optional]
**validationErrors** | **String** | Ошибки валидации из внешних систем |  [optional]
