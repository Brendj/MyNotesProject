CONNECT 'jdbc:derby:ecafe_processor_db';

alter table CF_MenuExchange add column Flags int not null default 0;
alter table CF_Menu add column Flags int not null default 0;