--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений issue 253

--Таблица активности карт
CREATE TABLE cf_preorder_check (
  idOfPreorderCheck bigserial NOT NULL primary key,
  date bigint NOT NULL,
  preorderAmount bigint,
  goodRequestAmount bigint,
  createdDate bigint,
  lastUpdate bigint NOT NULL,
  alarm integer NOT NULL DEFAULT 0
);
