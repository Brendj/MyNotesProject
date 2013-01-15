-- Пакет обновлений 2.2.32
-- Добавлена настройка текстого сообщения для принтера
--! в таблицу cf_ecafesettings добавлено строковое необязательное значение.
ALTER TABLE cf_ecafesettings ADD COLUMN settingtext character varying(128);