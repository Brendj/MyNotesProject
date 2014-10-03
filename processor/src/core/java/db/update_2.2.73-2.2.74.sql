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


-- показатели, отображающие планируемое внедрение ИС ПП
CREATE TABLE cf_system_implementation_forecast (
  Vawe     	       INTEGER NOT NULL,
  Period           varchar(30) NOT NULL,
  Region           varchar(30) NOT NULL,
  ToImplement  	   INTEGER,
  CONSTRAINT cf_system_implementation_forecast_pk PRIMARY KEY (Vawe, Period, Region)
);
-- добавление текущих показателей Второй волны (в работе)
INSERT INTO cf_system_implementation_forecast VALUES (2, '2014 - в работе', 'Восточный', 31);
INSERT INTO cf_system_implementation_forecast VALUES (2, '2014 - в работе', 'Западный', 3);
INSERT INTO cf_system_implementation_forecast VALUES (2, '2014 - в работе', 'Зеленоградский', NULL);
INSERT INTO cf_system_implementation_forecast VALUES (2, '2014 - в работе', 'Московская область', NULL);
INSERT INTO cf_system_implementation_forecast VALUES (2, '2014 - в работе', 'Северный', 11);
INSERT INTO cf_system_implementation_forecast VALUES (2, '2014 - в работе', 'Северо-Восточный', 4);
INSERT INTO cf_system_implementation_forecast VALUES (2, '2014 - в работе', 'Северо-Западный', 17);
INSERT INTO cf_system_implementation_forecast VALUES (2, '2014 - в работе', 'Троицкий и новомосковский', NULL);
INSERT INTO cf_system_implementation_forecast VALUES (2, '2014 - в работе', 'Центральный', 22);
INSERT INTO cf_system_implementation_forecast VALUES (2, '2014 - в работе', 'Юго-Восточный', 9);
INSERT INTO cf_system_implementation_forecast VALUES (2, '2014 - в работе', 'Юго-Западный', 5);
INSERT INTO cf_system_implementation_forecast VALUES (2, '2014 - в работе', 'Южный', 8);
-- добавление текущих показателей Второй волны (планируемых)
INSERT INTO cf_system_implementation_forecast VALUES (2, '2014 - план', 'Восточный', 54);
INSERT INTO cf_system_implementation_forecast VALUES (2, '2014 - план', 'Западный', 2);
INSERT INTO cf_system_implementation_forecast VALUES (2, '2014 - план', 'Зеленоградский', 2);
INSERT INTO cf_system_implementation_forecast VALUES (2, '2014 - план', 'Московская область', NULL);
INSERT INTO cf_system_implementation_forecast VALUES (2, '2014 - план', 'Северный', 2);
INSERT INTO cf_system_implementation_forecast VALUES (2, '2014 - план', 'Северо-Восточный', 5);
INSERT INTO cf_system_implementation_forecast VALUES (2, '2014 - план', 'Северо-Западный', 4);
INSERT INTO cf_system_implementation_forecast VALUES (2, '2014 - план', 'Троицкий и новомосковский', NULL);
INSERT INTO cf_system_implementation_forecast VALUES (2, '2014 - план', 'Центральный', 7);
INSERT INTO cf_system_implementation_forecast VALUES (2, '2014 - план', 'Юго-Восточный', 51);
INSERT INTO cf_system_implementation_forecast VALUES (2, '2014 - план', 'Юго-Западный', 5);
INSERT INTO cf_system_implementation_forecast VALUES (2, '2014 - план', 'Южный', 3);
-- добавление текущих показателей Третьей волны (планируемых)
INSERT INTO cf_system_implementation_forecast VALUES (3, '2014 - 2015', 'Восточный', 96);
INSERT INTO cf_system_implementation_forecast VALUES (3, '2014 - 2015', 'Западный', 217);
INSERT INTO cf_system_implementation_forecast VALUES (3, '2014 - 2015', 'Зеленоградский', 62);
INSERT INTO cf_system_implementation_forecast VALUES (3, '2014 - 2015', 'Московская область', NULL);
INSERT INTO cf_system_implementation_forecast VALUES (3, '2014 - 2015', 'Северный', 195);
INSERT INTO cf_system_implementation_forecast VALUES (3, '2014 - 2015', 'Северо-Восточный', 246);
INSERT INTO cf_system_implementation_forecast VALUES (3, '2014 - 2015', 'Северо-Западный', 65);
INSERT INTO cf_system_implementation_forecast VALUES (3, '2014 - 2015', 'Троицкий и новомосковский', NULL);
INSERT INTO cf_system_implementation_forecast VALUES (3, '2014 - 2015', 'Центральный', 108);
INSERT INTO cf_system_implementation_forecast VALUES (3, '2014 - 2015', 'Юго-Восточный', 207);
INSERT INTO cf_system_implementation_forecast VALUES (3, '2014 - 2015', 'Юго-Западный', 164);
INSERT INTO cf_system_implementation_forecast VALUES (3, '2014 - 2015', 'Южный', 135);