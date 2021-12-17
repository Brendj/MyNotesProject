--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.125

--Новый первичный ключ в таблице cf_taloon_approval
alter table cf_taloon_approval drop constraint cf_taloon_approval_pk, add column idOfTaloonApproval bigserial NOT NULL;

alter table cf_taloon_approval ADD CONSTRAINT cf_taloon_approval_pk PRIMARY KEY (idOfTaloonApproval);

--уник. индекс
CREATE UNIQUE INDEX cf_taloon_approval_idoforg_taloondate_taloonname_goodsguid_idx ON cf_taloon_approval USING btree
(idoforg, taloondate, taloonname, goodsguid);

ALTER TABLE CF_Generators ADD COLUMN idOfTaloonApproval BIGINT NOT NULL DEFAULT 0;
update cf_generators set idOfTaloonApproval = (select case when max(idOfTaloonApproval) is null THEN 0 else (max(idOfTaloonApproval)+1) end  from cf_taloon_approval );

--изменение типа поля с bigserial на bigint
ALTER TABLE cf_taloon_approval ALTER COLUMN IdOfTaloonApproval DROP DEFAULT;

--! ФИНАЛИЗИРОВАН (Семенов, 081116) НЕ МЕНЯТЬ