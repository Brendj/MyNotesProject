--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.102

CREATE INDEX cf_clients_phone_idx ON cf_clients USING btree (phone);

CREATE INDEX cf_clients_mobile_idx ON cf_clients USING btree (mobile);

-- Увеличение размера поля статистики
alter table cf_external_system_stats alter column StatisticValue type DECIMAL(20, 4);

--! ФИНАЛИЗИРОВАН (Семенов, 150903) НЕ МЕНЯТЬ
