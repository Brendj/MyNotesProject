-- Пакет обновлений v 288

update cf_clients set foodboxavailability = false where foodboxavailability is null;
alter table cf_clients alter column foodboxavailability set not null;
alter table cf_clients alter column foodboxavailability set default false;

update cf_clients set foodboxavailabilityguardian = false where foodboxavailabilityguardian is null;
alter table cf_clients alter column foodboxavailabilityguardian set not null;
alter table cf_clients alter column foodboxavailabilityguardian set default false;

--! ФИНАЛИЗИРОВАН 15.06.2022, НЕ МЕНЯТЬ