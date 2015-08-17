--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.99

ALTER TABLE cf_visitors ADD COLUMN freedocname CHARACTER VARYING(1024);
ALTER TABLE cf_visitors ADD COLUMN freedocnumber CHARACTER VARYING(50);
ALTER TABLE cf_visitors ADD COLUMN freedocdate BIGINT;

ALTER TABLE cf_menudetails ALTER COLUMN protein TYPE DECIMAL(10, 2);
ALTER TABLE cf_menudetails ALTER COLUMN fat TYPE DECIMAL(10, 2);
ALTER TABLE cf_menudetails ALTER COLUMN carbohydrates TYPE DECIMAL(10, 2);
ALTER TABLE cf_menudetails ALTER COLUMN calories TYPE DECIMAL(10, 2);
ALTER TABLE cf_menudetails ALTER COLUMN vitb1 TYPE DECIMAL(10, 2);
ALTER TABLE cf_menudetails ALTER COLUMN vitc TYPE DECIMAL(10, 2);
ALTER TABLE cf_menudetails ALTER COLUMN vita TYPE DECIMAL(10, 2);
ALTER TABLE cf_menudetails ALTER COLUMN vite TYPE DECIMAL(10, 2);
ALTER TABLE cf_menudetails ALTER COLUMN minca TYPE DECIMAL(10, 2);
ALTER TABLE cf_menudetails ALTER COLUMN minp TYPE DECIMAL(10, 2);
ALTER TABLE cf_menudetails ALTER COLUMN minmg TYPE DECIMAL(10, 2);
ALTER TABLE cf_menudetails ALTER COLUMN minfe TYPE DECIMAL(10, 2);
ALTER TABLE cf_menudetails ALTER COLUMN vitb2 TYPE DECIMAL(10, 2);
ALTER TABLE cf_menudetails ALTER COLUMN vitpp TYPE DECIMAL(10, 2);
