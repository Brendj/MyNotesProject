--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.146

--связь пользователей с ролью директор с главным корпусом ОО
CREATE TABLE cf_user_director_org
(
  idofuserdirectororg bigint NOT NULL,
  idofuser bigint NOT NULL,
  idoforg bigint NOT NULL,
  CONSTRAINT cf_userdirectororgs_pk PRIMARY KEY (idofuserdirectororg),
  CONSTRAINT cf_userdirectororgs_user_uq UNIQUE (idofuser)
)
WITH (
OIDS=FALSE
);
COMMENT ON TABLE cf_user_director_org
IS 'связь пользователей с ролью директор с главным корпусом ОО';

--! ФИНАЛИЗИРОВАН (Семенов, 171019) НЕ МЕНЯТЬ