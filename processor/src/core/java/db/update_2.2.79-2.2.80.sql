-- Расширение полей в связи с недостаточным размером
alter table cf_not_planned_orgs alter column officialname type VARCHAR(256);
alter table cf_not_planned_orgs alter column address type VARCHAR(256);

alter table cf_orgs alter column officialname type VARCHAR(256);