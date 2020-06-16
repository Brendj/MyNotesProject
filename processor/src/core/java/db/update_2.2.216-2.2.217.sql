--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 217

ALTER TABLE cf_wt_category_items
  ADD COLUMN deletestate INTEGER DEFAULT 0;

alter table cf_categorydiscounts
  add column deletedState integer NOT NULL DEFAULT 0,
  add column deleteDate bigint;

COMMENT ON COLUMN cf_categorydiscounts.deletedState IS 'Статус удаления записи (0 - рабочая категория, 1 - удалена)';
COMMENT ON COLUMN cf_categorydiscounts.deleteDate IS 'Дата удаления';

-- Справочник формы обучения
CREATE TABLE cf_kf_training_form
(
  global_id        bigint primary key,
  system_object_id bigint,
  id               integer,
  code             varchar(36),
  title            varchar(255),
  education_form   varchar(255),
  archive          boolean   not null default false,
  createdate       timestamp not null,
  lastupdate       timestamp not null,
  is_deleted       integer            default 0
);

-- Первичное наполнение справочника
insert into cf_kf_training_form (global_id, system_object_id, id, code, title, education_form, archive, createdate, lastupdate)
values (11439, 1, 1, 'full-time', 'Очная', 'В образовательной организации', false, now(), now()),
  (11440, 2, 2, 'correspondence', 'Заочная', 'В образовательной организации', false, now(), now()),
  (11441, 3, 3, 'part-time', 'Очно-заочная', 'В образовательной организации', false, now(), now()),
  (11442, 4, 4, 'at_home', 'На дому', 'Вне образовательной организации', true, now(), now()),
  (11443, 5, 5, 'self-education', 'Самообразование', 'Вне образовательной организации', false, now(), now()),
  (11444, 6, 6, 'family', 'Семейное образование', 'Вне образовательной организации', false, now(), now()),
  (11445, 7, 7, 'external', 'Полный экстернат', 'Вне образовательной организации', true, now(), now()),
  (31957065, 8, 8, 'Partially-external', 'Частичный экстернат', 'В образовательной организации', true, now(),now()),
  (31957066, 9, 9, 'Evening-classes', 'Очно-заочная (вечерняя)', 'В образовательной организации', true, now(),now());

COMMENT ON TABLE cf_kf_training_form IS 'Справочник формы обучения';

COMMENT ON COLUMN cf_kf_training_form.global_id IS 'Глобальный идентификатор (в НСИ-3)';
COMMENT ON COLUMN cf_kf_training_form.system_object_id IS 'Системный идентификатор (в НСИ-3)';
COMMENT ON COLUMN cf_kf_training_form.id IS 'Идентификатор (внутренний)';
COMMENT ON COLUMN cf_kf_training_form.code IS 'Код';
COMMENT ON COLUMN cf_kf_training_form.title IS 'Наименование';
COMMENT ON COLUMN cf_kf_training_form.education_form IS 'Форма получения образования';
COMMENT ON COLUMN cf_kf_training_form.archive IS 'Признак архивности (от НСИ-3)';
COMMENT ON COLUMN cf_kf_training_form.createdate IS 'Дата создания записи';
COMMENT ON COLUMN cf_kf_training_form.lastupdate IS 'Дата последнего обновления';
COMMENT ON COLUMN cf_kf_training_form.is_deleted IS 'Признак удаления записи (для АРМ)';

-- Таблицы для взаимодействия с МЭШ.Контингент
create table cf_mh_persons
(
  personguid varchar(255) not null
    constraint cf_mh_persons_pkey
    primary key,
  createdate timestamp not null,
  lastupdate timestamp not null,
  birthdate timestamp,
  classname varchar(255),
  classuid varchar(255),
  deletestate boolean,
  firstname varchar(255),
  genderid integer,
  lastname varchar(255),
  organizationid bigint,
  parallelid integer,
  patronymic varchar(255),
  invaliddata boolean default false not null,
  educationstageid integer,
  comment varchar(255)
);

create table cf_mh_entity_changes
(
  id bigint not null
    constraint cf_mh_entity_changes_pkey
    primary key,
  action integer not null,
  createdate timestamp not null,
  lastupdate timestamp not null,
  entity integer not null,
  entityid varchar(255),
  mergedpersonids varchar(255),
  personguid varchar(255) not null,
  constraint cf_mh_entity_changes_personguid_entity_uk
  unique (personguid, entity)
);

COMMENT ON TABLE cf_mh_entity_changes IS 'Таблица для обработки проблемных пакетов';

COMMENT ON COLUMN cf_mh_entity_changes.id IS 'ID записи';
COMMENT ON COLUMN cf_mh_entity_changes.action IS 'Тип изменения (создание, удаление, изменение, слияние)';
COMMENT ON COLUMN cf_mh_entity_changes.createdate IS 'Дата создания записи';
COMMENT ON COLUMN cf_mh_entity_changes.lastupdate IS 'Дата последнего обновления';
COMMENT ON COLUMN cf_mh_entity_changes.entity IS 'Код измененой сущности';
COMMENT ON COLUMN cf_mh_entity_changes.entityid IS 'ID сущности';
COMMENT ON COLUMN cf_mh_entity_changes.mergedpersonids IS 'Старые GUIDs после слияния';
COMMENT ON COLUMN cf_mh_entity_changes.personguid IS 'GUID NSI-3 клиента';

COMMENT ON TABLE cf_mh_persons IS 'Промежуточная таблица ';

COMMENT ON COLUMN cf_mh_persons.personguid IS 'GUID NSI-3 клиента';
COMMENT ON COLUMN cf_mh_persons.createdate IS 'Дата создания записи';
COMMENT ON COLUMN cf_mh_persons.lastupdate IS 'Дата последнего обновления';
COMMENT ON COLUMN cf_mh_persons.birthdate IS 'Дата рождение';
COMMENT ON COLUMN cf_mh_persons.classname IS 'Название класса';
COMMENT ON COLUMN cf_mh_persons.classuid IS 'UID класса';
COMMENT ON COLUMN cf_mh_persons.deletestate IS 'Флаг удаления';
COMMENT ON COLUMN cf_mh_persons.firstname IS 'Имя';
COMMENT ON COLUMN cf_mh_persons.genderid IS 'Код пола';
COMMENT ON COLUMN cf_mh_persons.lastname IS 'Фамилия';
COMMENT ON COLUMN cf_mh_persons.organizationid IS 'ID ОО из НСИ-3';
COMMENT ON COLUMN cf_mh_persons.parallelid IS 'Код параллели';
COMMENT ON COLUMN cf_mh_persons.patronymic IS 'Отчество';
COMMENT ON COLUMN cf_mh_persons.invaliddata IS 'Флаг ошибки обработки';
COMMENT ON COLUMN cf_mh_persons.educationstageid IS 'ID уровня образования';
COMMENT ON COLUMN cf_mh_persons.comment IS 'Комментарий к ошибке обработки данных';

--! ФИНАЛИЗИРОВАН 02.06.2020, НЕ МЕНЯТЬ