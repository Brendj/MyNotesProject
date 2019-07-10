--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.187

--новое поле id OO, которая внесла изменения в заявление на блокировку средств
alter table cf_clientbalance_hold add column idOfOrgLastChange bigint,
add column lastChangeStatus integer not null default 0;

alter table cf_clientbalance_hold add CONSTRAINT cf_clientbalance_hold_orglastchange_fk
FOREIGN KEY (idOfOrgLastChange) REFERENCES cf_orgs(idoforg);

