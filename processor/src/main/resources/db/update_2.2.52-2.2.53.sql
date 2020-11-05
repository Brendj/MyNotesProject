--! Пока скрипт не финализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.53

CREATE TABLE cf_do_org_current_version (
  IdDOOrgCurrentVersion bigserial NOT NULL,
  ObjectId integer not null,
  IdOfOrg bigint not null,
  LastVersion bigint not null,
  CONSTRAINT cf_do_org_current_version_pk PRIMARY KEY (IdDOOrgCurrentVersion)
);

-- Расширение таблицы с правилами соц. скидок - добавление колонки, группирующей несколько правил
alter table cf_discountrules add column subcategory varchar(100) default '';
--! Обновление добавленной колонки
update cf_discountrules set subcategory='Начальная школа 1-4 кл (завтрак)'
where description='1-4кл/завтрак';
update cf_discountrules set subcategory='Шк Здоровья 1-4 кл.(завтрак+обед)'
where description='1-4/Здоровья/завтрак+обед';
update cf_discountrules set subcategory='Шк Здоровья 5-11 кл.(завтрак+обед)'
where description='5-8/Здоровья/завтрак+обед' or description='9-11/Здоровья/завтра+обед';
update cf_discountrules set subcategory='Многодетные 1-4 кл.(завтрак+обед)'
where description='1-4 кл+многодетные/завтрак+обед';
update cf_discountrules set subcategory='Многодетные 5-11 кл.(завтрак+обед)'
where description='5-8 кл+многодетные/завтрак+обед' or description='9-11 кл+многодетные/завтрак+обед';
update cf_discountrules set subcategory='Соц./незащищ. 1-4 кл.(завтрак+обед)'
where description='Социально-незащищенные 1-4';
update cf_discountrules set subcategory='Соц./незащищ. 5-11 кл.(завтрак+обед)'
where description='Социально-незащищенные 5-8' or description='Социально-незащищенные 9-11';

update cf_discountrules set subcategory='Многодетные 5-11 кл.(завтрак+обед)'
where description='9-11 кл+малообесп/завтрак+обед' or
      description='5-8 кл+опека/завтрак+обед' or
      description='5-8 кл+малообесп/завтрак+обед' or
      description='5-8 кл+инв/завтрак+обед' or
      description='9-11 кл+инв/завтрак+обед';

--! ФИНАЛИЗИРОВАН (Кадыров, 131227) НЕ МЕНЯТЬ