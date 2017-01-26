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
