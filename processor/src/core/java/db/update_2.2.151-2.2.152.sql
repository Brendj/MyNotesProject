--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.152

--Новый флаг запрета на оплату при расхождении времени
alter table cf_orgs add column allowRegistryChangeEmployee integer not null default 0;

--Очистка флагов работы ОО в летний период
update cf_orgs set isWorkInSummerTime = 0;

--Таблица с номерами карт, по которым пропускаем проверку ЦП
create table cf_cards_special (
  cardno bigint NOT NULL,
  CONSTRAINT cf_cards_special_pk PRIMARY KEY (cardno)
);