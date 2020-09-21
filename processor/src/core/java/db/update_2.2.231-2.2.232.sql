--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 232

---666

CREATE TABLE public.cf_cr_cardactionitems (
    requestid varchar NULL,
    blockdate int8 NULL,
    unblockdate int8 NULL,
    operation varchar NULL,
    extclientid varchar NULL,
    firstname varchar NULL,
    lastname varchar NULL,
    middlename varchar NULL,
    groupname varchar NULL,
    contractidp int8 NULL,
    firp varchar NULL,
    lastp varchar NULL,
    middp varchar NULL,
    shortname varchar NULL,
    address varchar NULL,
    cardstate varchar NULL,
    cardno int8 NULL,
    cardprintedno int8 NULL,
    idofclient int8 NULL,
    idoforg int8 NULL
);

--! ФИНАЛИЗИРОВАН 21.09.2020, НЕ МЕНЯТЬ