--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 219

-- 468
ALTER TABLE cf_regular_preorders
    ADD COLUMN idofdish bigint;

COMMENT ON COLUMN cf_regular_preorders.idofdish IS 'Идентификатор блюда от веб-поставщика';