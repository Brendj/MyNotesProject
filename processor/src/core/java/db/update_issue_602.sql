
DROP TABLE IF EXISTS public.cf_plan_orders;
CREATE TABLE public.cf_plan_orders
(
    idofplanorder BIGSERIAL PRIMARY KEY,
    idoforg bigint ,
    groupname character varying(256),
    idofclient bigint,
    plandate bigint NOT NULL,
    idofcomplex bigint,
    complexname character varying(256),
    userclassid bigint,
    topay integer NOT NULL,
    idoforder bigint,
    userzavid bigint,
    createdate bigint NOT NULL DEFAULT (date_part('epoch'::text, now()) * (1000)::double precision),
    lastupdate bigint NOT NULL DEFAULT (date_part('epoch'::text, now()) * (1000)::double precision),
    CONSTRAINT planorders_unique_constraint UNIQUE (idoforg, idofclient, plandate, idofcomplex),
    CONSTRAINT userclassid_fk FOREIGN KEY (userclassid)
        REFERENCES public.cf_users (idofuser) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT userzavid_fk FOREIGN KEY (userzavid)
        REFERENCES public.cf_users (idofuser) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT idofclient_fk FOREIGN KEY (idofclient)
        REFERENCES public.cf_clients (idofclient) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT idoforg_fk FOREIGN KEY (idoforg)
        REFERENCES public.cf_orgs (idoforg) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.cf_plan_orders
    OWNER to postgres;
COMMENT ON TABLE public.cf_plan_orders
    IS 'План питания';