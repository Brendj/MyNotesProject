--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.97


--обновляем орг ид у карт
update cf_cards set idoforg=s.i
from (select c.idofcard a, cl.idoforg as i from cf_cards c inner join cf_clients cl on c.idofclient = cl.idofclient) as s
where s.a = cf_cards.idofcard;



alter table cf_orgs_sync add column LastAccRegistrySync bigint  ;


update cf_cards
set state =1
where state =2;

ALTER TABLE cf_goods_requests_positions ADD COLUMN notified BOOLEAN DEFAULT TRUE;