--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений issue 479

update cf_client_guardian
set version = (select max(version) + 1 from cf_client_guardian),
  relation = 3
where relation in (4, 5);

update cf_client_guardian
set version = (select max(version) + 1 from cf_client_guardian),
  relation = 2
where relation in (6, 7);