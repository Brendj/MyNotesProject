/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

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