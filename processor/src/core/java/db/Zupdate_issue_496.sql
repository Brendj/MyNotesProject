--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений ***

-- 496
ALTER TABLE cf_goods_requests_positions
    ADD COLUMN idofdish bigint,
    ADD COLUMN type integer NOT NULL DEFAULT 0;

COMMENT ON COLUMN cf_regular_preorders.idofdish IS 'Идентификатор блюда от веб-поставщика';
COMMENT ON COLUMN cf_goods_requests_positions.type IS 'Тип позиции (0 – общий тип, 1 – ЛП, 2 – ПП, 3 – АП, 4 – ВП, 5 - предзаказы)';
