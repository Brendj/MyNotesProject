--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.106

ALTER TABLE cf_orgregistrychange_item ADD CONSTRAINT cf_orgregistrychange_item_mainregistry_fkey FOREIGN KEY (mainregistry)
REFERENCES cf_orgregistrychange (idoforgregistrychange) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE NO ACTION;

CREATE INDEX cf_orgregistrychange_item_mainregistry_idx
ON cf_orgregistrychange_item
USING btree
(mainregistry);

--! ФИНАЛИЗИРОВАН (Семенов, 161110) НЕ МЕНЯТЬ


