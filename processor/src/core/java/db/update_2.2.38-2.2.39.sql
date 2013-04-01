-- Расширение поля source в связи с подключением платежей из РНИП (размер идентификатора = 32 символам)
ALTER TABLE cf_transactions ALTER COLUMN source TYPE VARCHAR(50);

-- Расширение таблицы contragents в связи с подключением платежей из РНИП
--! РНИП требует наличия данных полей в запросах на создание каталогов
ALTER TABLE cf_contragents ADD COLUMN kpp VARCHAR(10) NOT NULL DEFAULT '';
ALTER TABLE cf_contragents ADD COLUMN ogrn VARCHAR(15) NOT NULL DEFAULT '';