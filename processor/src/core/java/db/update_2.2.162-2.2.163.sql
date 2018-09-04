--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.163

-- ограничение на уникальность
CREATE UNIQUE INDEX cf_preorder_complex_idofclient_preorderdate_armcomplexid_unique ON cf_preorder_complex (idofclient, preorderdate, armcomplexid) WHERE deletedState = 0;

