-- Пакет обновлений issue 1044

alter table cf_info_messages add column mtype integer not null default 0;
comment on column cf_info_messages.mtype is 'Тип сообщения: 0 - для толстого арма, 1 - для веб арма';
