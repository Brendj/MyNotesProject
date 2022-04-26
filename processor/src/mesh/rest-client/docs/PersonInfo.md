# PersonInfo

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **Long** | Идентификатор | 
**personId** | [**UUID**](UUID.md) | Ссылка на идентификатор персоны в рамках системы | 
**mergedTo** | [**UUID**](UUID.md) | Ссылка на персону, с которой была слита данная |  [optional]
**lastname** | **String** | Фамилия | 
**firstname** | **String** | Имя | 
**patronymic** | **String** | Отчество |  [optional]
**birthdate** | [**LocalDate**](LocalDate.md) | Дата рождения | 
**birthplace** | **String** | Место рождения |  [optional]
**snils** | **String** | Номер СНИЛС |  [optional]
**genderId** | **Integer** | Пол (каталог НСИ3 &lt;a href&#x3D;\&quot;https://wiki.edu.mos.ru/pages/viewpage.action?pageId&#x3D;18394317\&quot;&gt;GENDER&lt;/href&gt;) | 
**citizenshipId** | **Integer** | Код страны по справочнику ОКСМ (каталог НСИ3 &lt;a href&#x3D;\&quot;https://wiki.edu.mos.ru/pages/viewpage.action?pageId&#x3D;18394242\&quot;&gt;COUNTRY&lt;/href&gt;) |  [optional]
**validationStateId** | **Integer** | Статус проверки |  [optional]
**validatedAt** | [**OffsetDateTime**](OffsetDateTime.md) | Дата проверки |  [optional]
**actualFrom** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время начала действия версии персоны |  [optional]
**actualTo** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время окончания действия версии персоны |  [optional]
**createdBy** | [**UUID**](UUID.md) | Идентификатор системы-источника |  [optional]
**updatedBy** | [**UUID**](UUID.md) | Идентификатор системы-источника |  [optional]
**createdAt** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время создания |  [optional]
**updatedAt** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время изменения |  [optional]
**addresses** | [**List&lt;PersonAddress&gt;**](PersonAddress.md) | Адреса персоны |  [optional]
**documents** | [**List&lt;PersonDocument&gt;**](PersonDocument.md) | Документы персоны |  [optional]
**contacts** | [**List&lt;PersonContact&gt;**](PersonContact.md) | Контакты персоны |  [optional]
**preventions** | [**List&lt;PersonPrevention&gt;**](PersonPrevention.md) | Инфомация о постановках на учет |  [optional]
**categories** | [**List&lt;PersonCategory&gt;**](PersonCategory.md) |  |  [optional]
**ids** | [**Person**](Person.md) |  |  [optional]
**agents** | [**List&lt;PersonAgent&gt;**](PersonAgent.md) | Список представителей |  [optional]
**children** | [**List&lt;PersonAgent&gt;**](PersonAgent.md) | Список детей |  [optional]
**education** | [**List&lt;PersonEducation&gt;**](PersonEducation.md) | ОО, в которых персона проходила или проходит обучение |  [optional]
**citizenship** | [**Citizenship**](Citizenship.md) |  |  [optional]
**validationErrors** | **String** | Ошибки валидации из внешних систем |  [optional]
