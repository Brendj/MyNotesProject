--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

CREATE INDEX cf_groupnames_to_orgs_idoforg_idx
    ON cf_groupnames_to_orgs USING btree(idoforg)

CREATE INDEX cf_preorder_linkod_preorderguid_idx
    ON cf_preorder_linkod USING btree(preorderguid)