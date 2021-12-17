--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.79

-- Расширение полей в связи с недостаточным размером
alter table cf_not_planned_orgs alter column officialname type VARCHAR(256);
alter table cf_not_planned_orgs alter column address type VARCHAR(256);

alter table cf_orgs alter column officialname type VARCHAR(256);

--! ФИНАЛИЗИРОВАН (Сунгатов, 141023) НЕ МЕНЯТЬ