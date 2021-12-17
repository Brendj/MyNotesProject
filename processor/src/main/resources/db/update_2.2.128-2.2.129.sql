--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.129

--Исправление templateFileName - записываем только название файла, а не весь адрес к нему
UPDATE cf_reporthandlerules AS r
SET templatefilename = i.t FROM
(SELECT idofreporthandlerule, trim(FROM substring(templatefilename, length(templatefilename) - position('\' in reverse(templatefilename)) + 2))
as t FROM cf_reporthandlerules
where trim(FROM substring(templatefilename, length(templatefilename) - position('\' in reverse(templatefilename)) + 2)) <> '') i
WHERE i.idofreporthandlerule = r.idofreporthandlerule;

--Удаление лишних записей с ИД старой ОО = ИД новой ОО в таблице перемещений между ОО
DELETE FROM cf_clientmigrationhistory WHERE idoforg = idofoldorg;

--удаление индекса
DROP INDEX cf_taloon_approval_idoforg_taloondate_taloonname_goodsguid_idx;

--уник. индекс
CREATE UNIQUE INDEX cf_taloon_approval_idoforg_taloondate_taloonname_goodsguid_idx ON cf_taloon_approval USING btree
(idoforg, taloondate, taloonname, goodsguid, price);

-- изменение полей ИД группы в ClientGroupMigrationHistory с long на Long (чтобы сохранять null вместо 0)
UPDATE cf_clientgroup_migrationhistory SET oldgroupid = DEFAULT WHERE oldgroupid = 0;
UPDATE cf_clientgroup_migrationhistory SET newgroupid = DEFAULT WHERE newgroupid = 0;

--Удаление лишних записей с названием старой группы = названием новой группы в таблице перемещений между группами внутри ОО
DELETE FROM cf_clientgroup_migrationhistory WHERE oldgroupname = newgroupname;

--! ФИНАЛИЗИРОВАН (Семенов, 140217) НЕ МЕНЯТЬ