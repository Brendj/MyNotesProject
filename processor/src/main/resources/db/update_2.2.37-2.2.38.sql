--! Пока скрипт не винализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.38

--! Добавление индекса, если требуется, раскомментировать
--! CREATE index "cf_menudetails_path_price_idx" ON CF_menudetails (menupath, price);

-- Переводим клиентов с нилевой ссылкой на группу в корень, то есть делаем их без группы
UPDATE cf_clients
   SET idofclientgroup=null
 WHERE idofclient in (
   select
     c.idofclient
   from cf_clients c
   left join cf_clientgroups g on c.idoforg=g.idoforg and c.idofclientgroup=g.idofclientgroup
   where c.idofclientgroup is not null and g.idofclientgroup is null
);

--! Поправляем ошибки с кривыми данными с группами клиентов
ALTER TABLE cf_clients ADD CONSTRAINT cf_clients_fk_orgclientsgroup FOREIGN KEY (idoforg, idofclientgroup)
REFERENCES cf_clientgroups (idoforg, idofclientgroup);

-- Исправлен не дочет по категориям скидок, далее после изменения параметров клиента в части категорий скидок данные будут валидными.
-- Обработка клиентов вынесена в отдельный тип синхронизации что дает быстрое изменеие параметров клиента
-- Произведен рефакторинг логики синхронизации
-- Переконфигурированы связи между сущностями Распределенных объектов

--! ФИНАЛИЗИРОВАН (Кадыров, 130320) НЕ МЕНЯТЬ
