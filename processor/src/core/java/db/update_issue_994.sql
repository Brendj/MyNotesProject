-- Пакет обновлений issue 994

CREATE SEQUENCE cf_specialdates_Id_Gen_seq INCREMENT BY 32;
select setval('cf_specialdates_Id_Gen_seq', (select coalesce(max(IdOfSpecialDate), 0) + 1 from cf_specialdates));

CREATE SEQUENCE CF_ComplexInfo_Id_Gen_seq INCREMENT BY 128;
select setval('CF_ComplexInfo_Id_Gen_seq', (select coalesce(max(IdOfComplexInfo), 0) + 1 from CF_ComplexInfo));

CREATE SEQUENCE CF_ComplexInfoDetail_Id_Gen_seq INCREMENT BY 256;
select setval('CF_ComplexInfoDetail_Id_Gen_seq', (select coalesce(max(IdOfComplexInfoDetail), 0) + 1 from CF_ComplexInfoDetail));
