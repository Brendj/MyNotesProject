--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений issue 659

CREATE INDEX cf_preorder_linkod_idoforg_idoforder_idx ON cf_preorder_linkod USING btree(idoforg, idoforder);
