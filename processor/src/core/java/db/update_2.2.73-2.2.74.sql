-- Изменения в БД процессинга по задаче https://bugtracker.axetta.ru:9901/browse/ELIBRARY-207
ALTER TABLE cf_bbk ADD COLUMN sendall integer;
ALTER TABLE cf_bbk ADD CONSTRAINT cf_bbk_guid_name_key UNIQUE (name);

ALTER TABLE cf_bbk_details ADD CONSTRAINT cf_bbk_details_name_and_code_key UNIQUE (name, code);