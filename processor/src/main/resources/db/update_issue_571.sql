--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- флаг "Использовать Web-АРМ Администратора"
ALTER TABLE cf_orgs
    ADD COLUMN usewebarmadmin boolean NOT NULL DEFAULT FALSE;
COMMENT ON COLUMN cf_orgs.usewebarmadmin
  IS 'Включено использование веб - модуля АРМа Администратора (true - да. false - нет)';
