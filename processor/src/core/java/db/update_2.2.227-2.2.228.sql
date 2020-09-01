--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 228

-- 612
ALTER TABLE cf_discountrules
    ADD COLUMN deletedstate boolean NOT NULL DEFAULT false;

COMMENT ON COLUMN cf_discountrules.deletedstate IS 'Признак удаления (true - удален, false - не удален)';

ALTER TABLE cf_wt_discountrules
    ADD COLUMN deletedstate boolean NOT NULL DEFAULT false;

COMMENT ON COLUMN cf_wt_discountrules.deletedstate IS 'Признак удаления (true - удален, false - не удален)';