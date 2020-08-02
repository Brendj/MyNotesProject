# PersonEducation

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **Long** | Идентификатор | 
**personId** | [**UUID**](UUID.md) | Ссылка на идентификатор персоны |  [optional]
**organizationId** | **Long** | Идентификатор образовательной оранизации (каталог НСИ3 &lt;a href&#x3D;\&quot;https://wiki.edu.mos.ru/pages/viewpage.action?pageId&#x3D;13710039\&quot;&gt;ORGANIZATION_REGISTRY&lt;/href&gt;) |  [optional]
**classUid** | [**UUID**](UUID.md) | Идентификатор класса/группы/кружка |  [optional]
**notes** | **String** | Примечание |  [optional]
**educationFormId** | **Integer** | Форма обучения (каталог НСИ3 &lt;a href&#x3D;\&quot;https://wiki.edu.mos.ru/pages/viewpage.action?pageId&#x3D;18394215\&quot;&gt;TRAINING_FORM&lt;/href&gt;) | 
**educationStageId** | **Integer** | Уровень обучения (каталог НСИ3 &lt;a href&#x3D;\&quot;https://wiki.edu.mos.ru/pages/viewpage.action?pageId&#x3D;18394370\&quot;&gt;EDUCATION_STAGE&lt;/href&gt;) | 
**financingTypeId** | **Integer** | Вид финансирования (каталог НСИ3 &lt;a href&#x3D;\&quot;https://wiki.edu.mos.ru/pages/viewpage.action?pageId&#x3D;18396612\&quot;&gt;FINANCING_TYPE&lt;/href&gt;) | 
**serviceTypeId** | **Integer** | Вид услуги (каталог НСИ3 &lt;a href&#x3D;\&quot;https://wiki.edu.mos.ru/pages/viewpage.action?pageId&#x3D;23727577\&quot;&gt;SERVICE_TYPE&lt;/href&gt;) |  [optional]
**deductionReasonId** | **Integer** | Причина отчисления (каталог НСИ3 &lt;a href&#x3D;\&quot;https://wiki.edu.mos.ru/pages/viewpage.action?pageId&#x3D;18396842\&quot;&gt;DEDUCTION_REASON&lt;/href&gt;) |  [optional]
**trainingBeginAt** | [**LocalDate**](LocalDate.md) | Дата начала обучения |  [optional]
**trainingEndAt** | [**LocalDate**](LocalDate.md) | Дата окончания обучения |  [optional]
**actualFrom** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время начала действия версии |  [optional]
**actualTo** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время окончания действия версии |  [optional]
**createdBy** | [**UUID**](UUID.md) | Идентификатор системы-источника, создавшую запись |  [optional]
**updatedBy** | [**UUID**](UUID.md) | Идентификатор системы-источника, изменившую запись |  [optional]
**createdAt** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время создания |  [optional]
**updatedAt** | [**OffsetDateTime**](OffsetDateTime.md) | Дата и время изменения |  [optional]
**propertyClass** | [**ModelClass**](ModelClass.md) |  |  [optional]
**educationForm** | [**EducationForm**](EducationForm.md) |  |  [optional]
**educationStage** | [**EducationStage**](EducationStage.md) |  |  [optional]
**financingType** | [**FinancingType**](FinancingType.md) |  |  [optional]
**deductionReason** | [**DeductionReason**](DeductionReason.md) |  |  [optional]
**serviceType** | [**ServiceType**](ServiceType.md) |  |  [optional]
