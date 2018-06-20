--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.160

--Код товара
alter table cf_menudetails add column itemcode character varying(32);

--Календарь меню - замена idOfMenu на Guid
alter table cf_menus_calendar drop column idofmenu,
  add column guidofmenu CHARACTER VARYING(36);
