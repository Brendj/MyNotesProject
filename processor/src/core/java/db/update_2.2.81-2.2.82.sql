-- Увеличение размера поля статистики
alter table cf_external_system_stats alter column StatisticValue type DECIMAL(10, 4);

--! добавление индекса о состоянии отправки СМС
CREATE index "cf_clientsms_deliverystatus_idx" ON cf_clientsms (deliverystatus);