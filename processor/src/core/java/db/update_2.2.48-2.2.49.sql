-- Добавление возможности описывать причину ошибки при сверке с ИС Реестры
alter table cf_registrychange_errors add column errordetails varchar(256) default '';

-- Добавление возможности определения срока хранения отчетов для выбранного правила
alter table CF_ReportHandleRules add column StoragePeriod bigint default -1;