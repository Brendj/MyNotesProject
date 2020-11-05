--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.140

--Новый флаг "Реализация невостребованных порций" + "Авто создание карт для новых клиентов с суидом (СПб)"
alter table cf_orgs add column isrecyclingenabled integer NOT NULL DEFAULT 0,
  add column autocreatecards integer NOT NULL DEFAULT 0;

--Номер недели циклограммы ВП
alter table cf_clients_cycle_diagrams add column StartWeekPosition integer;

--! ФИНАЛИЗИРОВАН (Семенов, 170829) НЕ МЕНЯТЬ