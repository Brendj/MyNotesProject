# PersonAgent

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **Long** | Идентификатор | 
**personId** | [**UUID**](UUID.md) | Идентификатор персоны |  [optional]
**agentPersonId** | [**UUID**](UUID.md) | Идентификатор персоны представителя |  [optional]
**agentTypeId** | **Integer** | Идентификатор типа представительства (каталог НСИ3 &lt;a href&#x3D;\&quot;https://wiki.edu.mos.ru/pages/viewpage.action?pageId&#x3D;18393963\&quot;&gt;LEGAL_REPRESENT&lt;/href&gt;) | 
**validationStateId** | **Integer** | Статус проверки |  [optional]
**validatedAt** | [**OffsetDateTime**](OffsetDateTime.md) | Дата проверки |  [optional]
**actualFrom** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время начала действия связи |  [optional]
**actualTo** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время окончания действия связи |  [optional]
**createdBy** | [**UUID**](UUID.md) | Идентификатор системы-источника, создавшую запись |  [optional]
**updatedBy** | [**UUID**](UUID.md) | Идентификатор системы-источника, изменившую запись |  [optional]
**createdAt** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время создания |  [optional]
**updatedAt** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время изменения |  [optional]
**agentType** | [**AgentType**](AgentType.md) |  |  [optional]
**agentPerson** | [**PersonInfo**](PersonInfo.md) |  |  [optional]
**validationErrors** | **String** | Ошибки валидации из внешних систем |  [optional]
