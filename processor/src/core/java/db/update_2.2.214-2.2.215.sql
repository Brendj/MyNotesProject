--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 215


---------329 Флаг, что пользователю отправлено оповещение об удалении регуляра
ALTER TABLE public.cf_regular_preorders ADD sendeddailynotification bool NULL;

