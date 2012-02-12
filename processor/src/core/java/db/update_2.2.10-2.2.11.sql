CREATE TABLE CF_TransactionJournal
(
  idOfTransactionJournal bigserial NOT NULL,
  transDate bigint NOT NULL,
  idOfOrg bigint NOT NULL,
  idOfInternalOperation bigint NOT NULL,
  OGRN character varying(32),
  clientSan character varying(15),
  clientType character varying(32),
  enterName character varying(100),
  serviceCode character varying(10),
  transactionCode character varying(10),
  cardTypeCode character varying(10),
  cardIdentityCode character varying(10),
  cardIdentityName character varying(32),
  uecid character varying(10),
  contractId bigint,
  financialAmount bigint,
  accountingDate bigint,
  CONSTRAINT cf_transaction_journal_pk PRIMARY KEY (idOfTransactionJournal)
);

/* Таблица категорий Организаций */
CREATE TABLE cf_categoryorg
(
  idofcategoryorg bigint NOT NULL,
  categoryname character varying(255),
  CONSTRAINT cf_categoryorg_pk PRIMARY KEY (idofcategoryorg )
);

/* Таблица связка между CategoryOrg и Org */
CREATE TABLE cf_orgscategories
(
  idoforgscategories bigserial NOT NULL,
  idoforg bigint,
  idofcategoryorg bigint,
  CONSTRAINT cf_orgscategories_pk PRIMARY KEY (idoforgscategories ),
  CONSTRAINT cf_orgscategories_idofcategoryorg FOREIGN KEY (idofcategoryorg)
  REFERENCES cf_categoryorg (idofcategoryorg),
  CONSTRAINT cf_orgscategories_idoforg FOREIGN KEY (idoforg)
  REFERENCES cf_orgs (idoforg)
);


/* Таблица связка между CategoryOrg и CategoryDiscountRule */
CREATE TABLE cf_catorgcatdiscrule
(
  idofcatorgcatdiscrule bigserial NOT NULL,
  idofcategoryorg bigint,
  idofcategorydiscount bigint,
  CONSTRAINT cf_catorgcatdiscrule_categorydiscount FOREIGN KEY (idofcategorydiscount)
  REFERENCES cf_categorydiscounts (idofcategorydiscount),
  CONSTRAINT cf_catorgcatdiscrule_categoryorg FOREIGN KEY (idofcategoryorg)
  REFERENCES cf_categoryorg (idofcategoryorg)
);

/* Таблица связка между DiscountRules и CategoryDiscountRule */
CREATE TABLE cf_discountrulescategorydiscount
(
  idofdrcd bigserial NOT NULL,
  idofrule bigint NOT NULL,
  idofcategorydiscount bigint NOT NULL,
  CONSTRAINT cf_discountrulescategorydiscount_pk PRIMARY KEY (idofrule , idofcategorydiscount ),
  CONSTRAINT cf_discountrulescategorydiscount_idofcategorydiscount FOREIGN KEY (idofcategorydiscount)
  REFERENCES cf_categorydiscounts (idofcategorydiscount),
  CONSTRAINT cf_discountrulescategorydiscount_idofdiscountrules FOREIGN KEY (idofrule)
  REFERENCES cf_discountrules (idofrule)
);
