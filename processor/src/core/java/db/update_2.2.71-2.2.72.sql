-- Добавление поля даты последней загрузки платежей из РНИП
ALTER TABLE cf_contragents ADD COLUMN LastRNIPUpdate VARCHAR(15) NOT NULL DEFAULT '';