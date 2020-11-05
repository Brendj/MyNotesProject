--! Пока скрипт не финализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.63

--! Фикс построения отчетов для фин. показателей
delete from cf_projectstate_data where type>=2000 and type<=3000;
--! insert into CF_Schema_version_info(MajorVersionNum, MiddleVersionNum, MinorVersionNum, BuildVersionNum, UpdateTime, CommitText) VALUES(2, 2, 62, 140522, 0, '');
--! ФИНАЛИЗИРОВАН (Кадыров, 140522) НЕ МЕНЯТЬ