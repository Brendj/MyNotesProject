--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.78
                                             --! добавление индекса о состоянии отправки СМС
CREATE index "cf_clientsms_deliverystatus_idx" ON cf_clientsms (deliverystatus);