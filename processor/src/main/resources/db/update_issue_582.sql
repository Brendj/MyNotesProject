CREATE TABLE cf_SfcUserOrgs
(
    idOfSfcUserOrg bigserial NOT NULL,
    idOfUser bigint NOT NULL,
    idOfOrg bigint NOT NULL,
    CONSTRAINT cf_SfcUserOrgs_pk PRIMARY KEY (idOfSfcUserOrg),
    CONSTRAINT cf_SfcUserOrgs_User_fk FOREIGN KEY (idOfUser) REFERENCES cf_users (IdOfUser),
    CONSTRAINT cf_SfcUserOrgs_Org_fk FOREIGN KEY (idOfOrg) REFERENCES cf_orgs (IdOfOrg),
    CONSTRAINT cf_SfcUserOrgs_uq UNIQUE (idofuser, idoforg)
);

COMMENT ON TABLE cf_SfcUserOrgs IS 'Таблица связки сфк пользователей с организациями';
COMMENT ON COLUMN cf_SfcUserOrgs.idOfSfcUserOrg IS 'Идентификатор записи';
COMMENT ON COLUMN cf_SfcUserOrgs.idOfUser IS 'Идентификатор пользователя, ссылка на таблицу cf_users';
COMMENT ON COLUMN cf_SfcUserOrgs.idOfOrg IS 'Идентификатор организации, ссылка на таблицу cf_orgs';