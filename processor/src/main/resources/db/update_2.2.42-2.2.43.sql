--! Пока скрипт не винализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.43
--! переименована колонка
ALTER TABLE cf_cards_temp RENAME myclienttype TO visitortype;
ALTER TABLE cf_cards_temp ALTER COLUMN visitortype TYPE integer;
ALTER TABLE cf_cards_temp ALTER COLUMN visitortype SET DEFAULT 0;
ALTER TABLE cf_cards_temp ALTER COLUMN visitortype SET NOT NULL;

-- Добавлен тип постетителя (DEFAULT 0 обычный, EMPLOYEE 1 работник)
ALTER TABLE cf_visitors ADD COLUMN VisitorType integer NOT NULL DEFAULT 0;

-- Добавлен индекс для отчета по проходам инженеров
CREATE INDEX cf_enterevents_idvisevtdt_idx ON cf_enterevents USING btree (idofvisitor , evtdatetime );

--! ФИНАЛИЗИРОВАН (Кадыров, 130821) НЕ МЕНЯТЬ