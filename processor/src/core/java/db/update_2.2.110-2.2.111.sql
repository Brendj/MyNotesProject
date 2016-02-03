--Добавление обязательного поля shortnameinfoservice для таблицы с загруженными из файла организациями
ALTER TABLE cf_not_planned_orgs ADD COLUMN shortnameinfoservice character varying(128);