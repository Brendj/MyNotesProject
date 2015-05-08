--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.97


--обновляем орг ид у карт
update cf_cards set idoforg=s.i
from (select c.idofcard a, cl.idoforg as i from cf_cards c inner join cf_clients cl on c.idofclient = cl.idofclient) as s
where s.a = cf_cards.idofcard

