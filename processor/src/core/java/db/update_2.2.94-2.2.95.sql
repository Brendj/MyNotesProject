--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.94
delete from cf_account_operations a where a.idofaccountoperation  in (select tem.id from ( select max(idofaccountoperation) as id, idoforg,idofoperation from cf_account_operations group by idoforg,idofoperation having count(*)>1) tem )


ALTER TABLE cf_account_operations ADD CONSTRAINT account_operations_UQ UNIQUE (idoforg,idofoperation)

--! ФИНАЛИЗИРОВАН (Сунгатов, 150403) НЕ МЕНЯТЬ