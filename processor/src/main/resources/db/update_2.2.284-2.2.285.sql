/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

-- Пакет обновлений v 285
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

ALTER TABLE cf_users ADD COLUMN deleteDateForBlock bigint;

COMMENT ON COLUMN cf_users.deleteDateForBlock IS 'Дата блокировки учетной записи';

CREATE TABLE CF_ReportHandleRules_routes (
                                             idofreporthandleruleroute BIGSERIAL PRIMARY KEY,
                                             route VARCHAR(128) NOT NULL,
                                             idofreporthandlerule BIGINT NOT NULL REFERENCES CF_ReportHandleRules(idofreporthandlerule) ON DELETE CASCADE
);

COMMENT ON COLUMN CF_ReportHandleRules_routes.idofreporthandleruleroute IS 'ID записи';
COMMENT ON COLUMN CF_ReportHandleRules_routes.route IS 'Адрес рассылки';
COMMENT ON COLUMN CF_ReportHandleRules_routes.idofreporthandlerule IS 'Ссылка на родительское правило для рассылки';

CREATE OR REPLACE FUNCTION transit_route() RETURNS INTEGER
    LANGUAGE 'plpgsql'
AS
'
    DECLARE
        r   RECORD;
    cnt INTEGER;
BEGIN
    cnt = 0;
    FOR r IN SELECT * FROM CF_ReportHandleRules ORDER BY idofreporthandlerule
    LOOP
            IF r.route0 IS NOT NULL AND r.route0 NOT LIKE '''' THEN
    INSERT INTO CF_ReportHandleRules_routes (route, idofreporthandlerule)
    VALUES (r.route0, r.idofreporthandlerule);
    END IF;
    IF r.route1 IS NOT NULL AND r.route1 NOT LIKE '''' THEN
    INSERT INTO CF_ReportHandleRules_routes (route, idofreporthandlerule)
    VALUES (r.route1, r.idofreporthandlerule);
    END IF;
    IF r.route2 IS NOT NULL AND r.route2 NOT LIKE '''' THEN
    INSERT INTO CF_ReportHandleRules_routes (route, idofreporthandlerule)
    VALUES (r.route2, r.idofreporthandlerule);
    END IF;
    IF r.route3 IS NOT NULL AND r.route3 NOT LIKE '''' THEN
    INSERT INTO CF_ReportHandleRules_routes (route, idofreporthandlerule)
    VALUES (r.route3, r.idofreporthandlerule);
    END IF;
    IF r.route4 IS NOT NULL AND r.route4 NOT LIKE '''' THEN
    INSERT INTO CF_ReportHandleRules_routes (route, idofreporthandlerule)
    VALUES (r.route4, r.idofreporthandlerule);
    END IF;
    IF r.route5 IS NOT NULL AND r.route5 NOT LIKE '''' THEN
    INSERT INTO CF_ReportHandleRules_routes (route, idofreporthandlerule)
    VALUES (r.route5, r.idofreporthandlerule);
    END IF;
    IF r.route6 IS NOT NULL AND r.route6 NOT LIKE '''' THEN
    INSERT INTO CF_ReportHandleRules_routes (route, idofreporthandlerule)
    VALUES (r.route6, r.idofreporthandlerule);
    END IF;
    IF r.route7 IS NOT NULL AND r.route7 NOT LIKE '''' THEN
    INSERT INTO CF_ReportHandleRules_routes (route, idofreporthandlerule)
    VALUES (r.route7, r.idofreporthandlerule);
    END IF;
    IF r.route8 IS NOT NULL AND r.route8 NOT LIKE '''' THEN
    INSERT INTO CF_ReportHandleRules_routes (route, idofreporthandlerule)
    VALUES (r.route8, r.idofreporthandlerule);
    END IF;
    IF r.route9 IS NOT NULL AND r.route9 NOT LIKE '''' THEN
    INSERT INTO CF_ReportHandleRules_routes (route, idofreporthandlerule)
    VALUES (r.route9, r.idofreporthandlerule);
    END IF;
    cnt = cnt + 1;
    END LOOP;
    RETURN cnt;
    END;
';

SELECT transit_route();

ALTER TABLE CF_ReportHandleRules
    DROP COLUMN route0,
    DROP COLUMN route1,
    DROP COLUMN route2,
    DROP COLUMN route3,
    DROP COLUMN route4,
    DROP COLUMN route5,
    DROP COLUMN route6,
    DROP COLUMN route7,
    DROP COLUMN route8,
    DROP COLUMN route9;

--! ФИНАЛИЗИРОВАН 24.03.2022, НЕ МЕНЯТЬ