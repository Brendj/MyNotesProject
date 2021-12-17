--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.76

-- Изменения в БД процессинга по задаче https://bugtracker.axetta.ru:9901/browse/ELIBRARY-207
ALTER TABLE cf_bbk ADD COLUMN sendall integer;
ALTER TABLE cf_bbk ADD CONSTRAINT cf_bbk_guid_name_key UNIQUE (name);

ALTER TABLE cf_bbk_details ADD CONSTRAINT cf_bbk_details_name_and_code_key UNIQUE (name, code);

-- Изменения в БД процессинга по задаче https://bugtracker.axetta.ru:9901/browse/ELIBRARY-235
DELETE FROM cf_do_confirms WHERE distributedobjectclassname = 'AccompanyingDocument';
UPDATE cf_accompanyingdocuments SET sendall = '0';

DELETE FROM cf_do_confirms WHERE distributedobjectclassname = 'Circulation';
UPDATE cf_circulations SET sendall = '0';

DELETE FROM cf_do_confirms WHERE distributedobjectclassname = 'LibVisit';
UPDATE cf_libvisits SET sendall = '0';

DELETE FROM cf_do_confirms WHERE distributedobjectclassname = 'RetirementReason';
UPDATE cf_retirementreasons SET sendall = '0';

DELETE FROM cf_do_confirms WHERE distributedobjectclassname = 'Source';
UPDATE cf_sources SET sendall = '0';

DELETE FROM cf_do_confirms WHERE distributedobjectclassname = 'TypeOfAccompanyingDocument';
UPDATE cf_typesofaccompanyingdocuments SET sendall = '0';

--! ФИНАЛИЗИРОВАН (Сунгатов, 141009) НЕ МЕНЯТЬ