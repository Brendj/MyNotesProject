# ModelClass

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **Long** | Идентификатор | 
**uid** | [**UUID**](UUID.md) | Идентификатор класса |  [optional]
**name** | **String** | Название | 
**organizationId** | **Long** | Идентификатор образовательной организации (каталог НСИ3 &lt;a href&#x3D;\&quot;https://wiki.edu.mos.ru/pages/viewpage.action?pageId&#x3D;13710039\&quot;&gt;ORGANIZATION_REGISTRY&lt;/href&gt;) | 
**staffIds** | [**List&lt;UUID&gt;**](UUID.md) | Идентификаторы классных руководителей |  [optional]
**academicYearId** | **Integer** | Идентификатор учебного года (каталог НСИ3 &lt;a href&#x3D;\&quot;https://wiki.edu.mos.ru/pages/viewpage.action?pageId&#x3D;18394031\&quot;&gt;НСИ ACADEMIC_YEAR&lt;/a&gt;) |  [optional]
**openAt** | [**LocalDate**](LocalDate.md) | Дата открытия класса/группы | 
**closeAt** | [**LocalDate**](LocalDate.md) | Дата закрытия классы/группы |  [optional]
**parallelId** | **Integer** | Параллель (каталог НСИ3 &lt;a href&#x3D;\&quot;https://wiki.edu.mos.ru/pages/viewpage.action?pageId&#x3D;18394351\&quot;&gt;PARALLELS&lt;/href&gt;) |  [optional]
**educationStageId** | **Integer** | Уровень образования (каталог НСИ3 &lt;a href&#x3D;\&quot;https://wiki.edu.mos.ru/pages/viewpage.action?pageId&#x3D;18394370\&quot;&gt;EDUCATION_LEVEL&lt;/href&gt;) | 
**letter** | **String** | Буква класса |  [optional]
**ageGroupId** | **Integer** | Справочник возрастных категорий (каталог НСИ3 &lt;a href&#x3D;\&quot;https://wiki.edu.mos.ru/pages/viewpage.action?pageId&#x3D;18394236\&quot;&gt;AGE_GROUP&lt;/href&gt;) |  [optional]
**notes** | **String** | Примечание |  [optional]
**actualFrom** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время начала действия версии |  [optional]
**actualTo** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время окончания действия версии |  [optional]
**createdBy** | [**UUID**](UUID.md) | Идентификатор системы-источника, создавшую запись |  [optional]
**updatedBy** | [**UUID**](UUID.md) | Идентификатор системы-источника, изменившую запись |  [optional]
**createdAt** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время создания |  [optional]
**updatedAt** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время изменения |  [optional]
**parallel** | [**Parallel**](Parallel.md) |  |  [optional]
**organization** | [**Organization**](Organization.md) |  |  [optional]
