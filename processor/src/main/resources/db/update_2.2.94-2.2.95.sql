--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.94
delete from cf_account_operations a where a.idofaccountoperation  in (select tem.id from ( select max(idofaccountoperation) as id, idoforg,idofoperation from cf_account_operations group by idoforg,idofoperation having count(*)>1) tem );


ALTER TABLE cf_account_operations ADD CONSTRAINT account_operations_UQ UNIQUE (idoforg,idofoperation);

update cf_clientpayments
set idofpayment = idofpayment || '-DOUBLE-' || round(random()*1000000000)
where idofclientpayment in (
  select max(cp.idofclientpayment) as idofclientpayment
  from cf_transactions as t, cf_clientpayments as cp, cf_clients as c
  where t.idoftransaction = cp.idoftransaction and t.idofclient = c.idofclient
  group by cp.idofcontragent, c.contractId, t.idofclient, cp.idofpayment, cp.paysum
  having count(*) > 1);

-- Constraint: cf_clientpayments_idofcontragent_and_idofpayment_uk

-- ALTER TABLE cf_clientpayments DROP CONSTRAINT cf_clientpayments_idofcontragent_and_idofpayment_uk;

ALTER TABLE cf_clientpayments
ADD CONSTRAINT cf_clientpayments_idofcontragent_and_idofpayment_uk UNIQUE(idofcontragent, idofpayment);

--! ФИНАЛИЗИРОВАН (Сунгатов, 150403) НЕ МЕНЯТЬ