--! Пока скрипт не финализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.65

-- связка пользователи организации по рассылкам уведомлений по отмененным заказам
ALTER TABLE cf_userorgs ADD COLUMN usernotificationtype integer;
UPDATE cf_UserOrgs SET UserNotificationType = 0;
ALTER TABLE cf_userorgs ALTER COLUMN usernotificationtype SET NOT NULL;
ALTER TABLE cf_UserOrgs DROP CONSTRAINT cf_userorgs_uq;
ALTER TABLE cf_UserOrgs ADD CONSTRAINT cf_userorgs_uq UNIQUE (idofuser, idoforg, userNotificationType);
--! ФИНАЛИЗИРОВАН (Кадыров, 140609) НЕ МЕНЯТЬ