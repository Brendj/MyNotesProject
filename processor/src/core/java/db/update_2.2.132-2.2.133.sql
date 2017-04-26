--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.133

--Количество дней для выгрузки меню + версия записи
alter table cf_provider_configurations add column menuSyncCountDays integer,
  add column version bigint not null default 0;

--история изменений данных электронной сверки
alter table cf_taloon_approval add column remarks text;

--Поле источник создания клиента (МПГУ или иное)
alter table cf_clients add column createdFrom integer not null default 0;