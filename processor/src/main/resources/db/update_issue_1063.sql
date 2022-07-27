--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

CREATE INDEX cf_orgs_organizationidfromnsi_idx
    ON cf_orgs USING btree(organizationidfromnsi)
