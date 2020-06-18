--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 218

-- 506
ALTER TABLE cf_orderdetails
    ADD COLUMN idofcomplex bigint,
    ADD COLUMN idofdish bigint;

COMMENT ON COLUMN cf_orderdetails.idofcomplex IS 'Идентификатор комплекса от веб-поставщика';
COMMENT ON COLUMN cf_orderdetails.idofdish IS 'Идентификатор блюда от веб-поставщика';