--! Пока скрипт не финализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.60

CREATE TABLE cf_user_report_settings
(
  idOfUserReportSetting bigserial NOT NULL,
  numberOfReport integer NOT NULL,
  idOfUser bigint NOT NULL,
  settings text,
  CONSTRAINT cf_user_report_setting_pk PRIMARY KEY (idOfUserReportSetting),
  CONSTRAINT cf_user_report_settings_fk_users FOREIGN KEY (idOfUser) REFERENCES cf_users (idofuser)
);
--! ФИНАЛИЗИРОВАН (Кадыров, 140204) НЕ МЕНЯТЬ