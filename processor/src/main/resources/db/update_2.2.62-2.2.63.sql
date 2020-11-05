--! Пока скрипт не финализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.63

-- Добавляем индекс по организации к таблице клиентов
CREATE INDEX cf_clients_idoforg_idx ON cf_clients USING btree (idoforg);

-- Добавляем индекс по группе к таблице группе клиентов
CREATE INDEX cf_clientgroups_idoforg_idx ON cf_clientgroups USING btree (idoforg);
-- изменение наименований супер-категорий, в настоящий момент (14.05.14), применяется только в справках расходования средств
update cf_discountrules set subcategory='Средние и  старшие классы 5-11 (завтрак + обед + полдник)' where subcategory='Средние и  старшие калссы 5-11 (завтрак + обед + полдник)';
update cf_discountrules set subcategory='Бесплатники 1-4 кл.(завтрак+обед)' where subcategory='Шк Здоровья 1-4 кл.(завтрак+обед)';
update cf_discountrules set subcategory='Бесплатники 5-11 кл.(завтрак+обед)' where subcategory='Шк Здоровья 5-11 кл.(завтрак+обед)';

--! insert into CF_Schema_version_info(MajorVersionNum, MiddleVersionNum, MinorVersionNum, BuildVersionNum, UpdateTime, CommitText) VALUES(2, 2, 63, 140515, 0, '');
--! ФИНАЛИЗИРОВАН (Кадыров, 140515) НЕ МЕНЯТЬ