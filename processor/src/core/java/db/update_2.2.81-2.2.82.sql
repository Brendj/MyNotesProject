-- Увеличение размера поля статистики
alter table cf_external_system_stats alter column StatisticValue type DECIMAL(10, 4);