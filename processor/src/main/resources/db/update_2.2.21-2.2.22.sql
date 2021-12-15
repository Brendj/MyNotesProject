CREATE TABLE cf_banks
(
  name character varying(128),
  logourl character varying(128),
  terminalsurl character varying(128),
  enrollmenttype character varying(128),
  idofbank bigserial NOT NULL,
  minrate double precision,
  rate double precision,
  CONSTRAINT cf_banks_pkey PRIMARY KEY (idofbank)
);

ALTER TABLE CF_ClientPayments ADD COLUMN IdOfContragentReceiver bigint;
ALTER TABLE CF_ClientPayments ADD CONSTRAINT cf_clientpayments_to_ca_rcvr_fk FOREIGN KEY (IdOfContragentReceiver)
   REFERENCES CF_Contragents (IdOfContragent);

ALTER TABLE CF_Contragents ADD COLUMN PublicKeyGOSTAlias varchar(64);

ALTER TABLE CF_EnterEvents ALTER COLUMN TurnstileAddr TYPE VARCHAR(30);

--! ФИНАЛИЗИРОВАН (Кадыров, 120821) НЕ МЕНЯТЬ