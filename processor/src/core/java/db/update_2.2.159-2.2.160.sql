--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.160

--Код товара
alter table cf_menudetails add column itemcode character varying(32);

--Календарь меню - замена idOfMenu на Guid
alter table cf_menus_calendar drop column idofmenu,
  add column guidofmenu CHARACTER VARYING(36);

--Код товара в блюде предзаказа
alter table cf_preorder_menudetail add column itemcode character varying(32);

--поле статуса предзаказа
alter table cf_preorder_menudetail add column state integer not null default 0;

--ИД заявки для предзаказа
alter table cf_preorder_complex add column idofgoodsrequestposition bigint;

--ИД заявки для предзаказа
alter table cf_preorder_menudetail add column idofgoodsrequestposition bigint;

--вид рациона, тип возрастной категории и флаг ежедневной продажи
alter table cf_goods add column goodtype integer not null default 0,
	add column agegroup integer not null default 0,
	add column dailysale integer not null default 0;

--Статус перехода УИД карты от одной организации к другой
alter table cf_cards add column transitionstate integer not null default 0;