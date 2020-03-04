--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений issue 414

ALTER TABLE cf_preorder_menudetail
  ADD COLUMN usedsum BIGINT,
  ADD COLUMN usedamount BIGINT;
