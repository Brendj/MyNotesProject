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

UPDATE cf_orgs SET uniqueaddressid = additionalidbuilding;

UPDATE cf_orgs SET uniqueaddressid = 9022644 WHERE uniqueaddressid = 254;
UPDATE cf_orgs SET uniqueaddressid = 9022645 WHERE uniqueaddressid = 5105;
UPDATE cf_orgs SET uniqueaddressid = 9022646 WHERE uniqueaddressid = 5106;
UPDATE cf_orgs SET uniqueaddressid = 9022647 WHERE uniqueaddressid =  5298;
UPDATE cf_orgs SET uniqueaddressid = 9022648 WHERE uniqueaddressid = 5299;
UPDATE cf_orgs SET uniqueaddressid = 9022649 WHERE uniqueaddressid = 5256615;
UPDATE cf_orgs SET uniqueaddressid = 9022650 WHERE uniqueaddressid = 5256616;
UPDATE cf_orgs SET uniqueaddressid = 9022651 WHERE uniqueaddressid = 5256623;
UPDATE cf_orgs SET uniqueaddressid = 9022652 WHERE uniqueaddressid = 5256624;
UPDATE cf_orgs SET uniqueaddressid = 9022653 WHERE uniqueaddressid = 5256626;
UPDATE cf_orgs SET uniqueaddressid = 9022654 WHERE uniqueaddressid = 5256628;
UPDATE cf_orgs SET uniqueaddressid = 9022655 WHERE uniqueaddressid = 5256629;
UPDATE cf_orgs SET uniqueaddressid = 9022656 WHERE uniqueaddressid = 5256630;
UPDATE cf_orgs SET uniqueaddressid = 9022657 WHERE uniqueaddressid = 5256631;
UPDATE cf_orgs SET uniqueaddressid = 9022658 WHERE uniqueaddressid = 5256632;
UPDATE cf_orgs SET uniqueaddressid = 9022659 WHERE uniqueaddressid = 5256633;
UPDATE cf_orgs SET uniqueaddressid = 9022660 WHERE uniqueaddressid = 5256634;
UPDATE cf_orgs SET uniqueaddressid = 9022661 WHERE uniqueaddressid = 5256635;
UPDATE cf_orgs SET uniqueaddressid = 9022662 WHERE uniqueaddressid = 5256636;
UPDATE cf_orgs SET uniqueaddressid = 9022663 WHERE uniqueaddressid = 5256637;
UPDATE cf_orgs SET uniqueaddressid = 9022664 WHERE uniqueaddressid = 5256639;
UPDATE cf_orgs SET uniqueaddressid = 9022665 WHERE uniqueaddressid = 5256643;
UPDATE cf_orgs SET uniqueaddressid = 9022666 WHERE uniqueaddressid = 5256645;
UPDATE cf_orgs SET uniqueaddressid = 9022667 WHERE uniqueaddressid = 5256646;

--! ФИНАЛИЗИРОВАН (Семенов, 150818) НЕ МЕНЯТЬ