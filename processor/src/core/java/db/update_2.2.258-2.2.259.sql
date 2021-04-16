-- Пакет обновлений 259

--868
ALTER TABLE cf_cards
    ADD COLUMN longCardNo BIGINT;
CREATE INDEX cf_cards_longCardNo_idx ON cf_cards(longCardNo);

COMMENT ON COLUMN cf_cards.longCardNo IS 'Длинный UID-идентификатор карты';

ALTER TABLE cf_cards_temp ADD COLUMN longCardNo BIGINT;
COMMENT ON COLUMN cf_cards_temp.longCardNo IS 'Длинный UID-идентификатор карты';

ALTER TABLE cf_cards_temp ADD COLUMN isLongUID BOOLEAN;
COMMENT ON COLUMN cf_cards_temp.isLongUID IS 'Режим длинных UID';

--870
ALTER TABLE cf_orgs
    ADD COLUMN useLongCardNo BOOLEAN;

COMMENT ON COLUMN cf_orgs.useLongCardNo IS 'Режим работы с длинными UID карт';

--920
ALTER TABLE cf_enterevents ADD COLUMN longCardId BIGINT;

COMMENT ON COLUMN CF_EnterEvents.longCardId IS 'Длинный UID карты';

--970
ALTER TABLE cf_orgs
    ADD COLUMN governmentContract BOOLEAN;

COMMENT ON COLUMN CF_Orgs.governmentContract IS 'Наличие государственного контракта';

--955
CREATE SEQUENCE public.cf_client_qr_code_id
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1;

CREATE TABLE cf_client_qr_code (
                                   idQRCode int8 DEFAULT nextval('cf_client_qr_code_id') NOT NULL,
                                   IdOfClient int8 null,
                                   qr bytea null,
                                   startDate int8 null,
                                   endDate int8 null,
                                   createDate int8 null
);

--975
drop table cf_wt_org_groups_aud;
drop table cf_wt_org_group_relations_aud;
drop table cf_wt_menu_org_aud;
drop table cf_wt_menu_aud;
drop table cf_wt_complexes_org_aud;
drop table cf_wt_complexes_aud;
drop table cf_wt_revision_info;

update cf_wt_menu_group_relationships g set deletestate = 1
where deletestate = 0 and (select m.deletestate from cf_wt_menu m where m.idofmenu = g.idofmenu) = 1;


--! ФИНАЛИЗИРОВАН 08.04.2021, НЕ МЕНЯТЬ