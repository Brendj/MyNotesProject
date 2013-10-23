--! Добавление индекса по дате отправления для таблицы с отправленными смс
CREATE INDEX cf_clientsms_servicesenddate_idx
ON cf_clientsms (servicesenddate ASC NULLS LAST);