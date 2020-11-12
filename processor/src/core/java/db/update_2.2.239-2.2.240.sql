--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 240
COMMENT ON COLUMN public.cf_cr_cardactionclient.idcardactionclient IS 'Идентификатор записи';
COMMENT ON COLUMN public.cf_cr_cardactionclient.idcardactionrequest IS 'Идентификатор запроса (ссылка на cf_cr_cardactionrequests)';
COMMENT ON COLUMN public.cf_cr_cardactionclient.idofclient IS 'Идентификатор клиента или сотрудника (ссылка на cf_clients)';
COMMENT ON COLUMN public.cf_cr_cardactionclient.idclientchild IS 'Идентификатор опекаемого (ссылка на cf_clients)';
COMMENT ON COLUMN public.cf_cr_cardactionclient.idofcard IS 'Идентификатор карты (ссылка на cf_cards)';
COMMENT ON COLUMN public.cf_cr_cardactionclient."comment" IS 'Описание результата операции';
COMMENT ON COLUMN public.cf_cr_cardactionrequests.idcardactionrequest IS 'Идентификатор записи';
COMMENT ON COLUMN public.cf_cr_cardactionrequests.requestid IS 'Идентификатор запроса (приходит из внешней системы)';
COMMENT ON COLUMN public.cf_cr_cardactionrequests.contingentid IS 'Идентификатор клиента (приходит из внешней системы)';
COMMENT ON COLUMN public.cf_cr_cardactionrequests.staffid IS 'Идентификатор сотрудника (приходит из внешней системы)';
COMMENT ON COLUMN public.cf_cr_cardactionrequests.firstname IS 'Имя (приходит из внешней системы)';
COMMENT ON COLUMN public.cf_cr_cardactionrequests.lastname IS 'Фамилия (приходит из внешней системы)';
COMMENT ON COLUMN public.cf_cr_cardactionrequests.middlename IS 'Отчество (приходит из внешней системы)';
COMMENT ON COLUMN public.cf_cr_cardactionrequests.birthday IS 'Дата рождения (приходит из внешней системы)';
COMMENT ON COLUMN public.cf_cr_cardactionrequests.organization_ids IS 'Идентификаторы организаций для сотрудника (приходит из внешней системы)';
COMMENT ON COLUMN public.cf_cr_cardactionrequests."action" IS 'Действие: 0 - Блокировка, 1 - Разблокировка';
COMMENT ON COLUMN public.cf_cr_cardactionrequests."comment" IS 'Результат выполнения операции';
COMMENT ON COLUMN public.cf_cr_cardactionrequests.processed IS 'Успешность: true - Запрос успешно обработан, false - При обработке запроса возникли ошибки';
COMMENT ON COLUMN public.cf_cr_cardactionrequests.createdate IS 'Дата приема запроса';
COMMENT ON COLUMN public.cf_cr_cardactionrequests.lastupdate IS 'Дата обновления запроса';
COMMENT ON COLUMN public.cf_cr_cardactionrequests.idofclient IS 'Идентификатор клиента (для поддержки старых запросов)';
COMMENT ON COLUMN public.cf_cr_cardactionrequests.previdcardrequest IS 'Идентификатор записи на блокировку с таким же requestid';


--756
drop table cf_GuardSan;
ALTER TABLE public.cf_clients DROP COLUMN guardsan;

--551
ALTER TABLE public.cf_complex_schedules ALTER COLUMN groupsids TYPE varchar USING cast(groupsids as varchar);