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

-- Таблица категорий Организаций
CREATE TABLE CF_CategoryOrg
(
  idofcategoryorg bigserial NOT NULL,
  categoryname character varying(255),
  CONSTRAINT cf_categoryorg_pk PRIMARY KEY (idofcategoryorg )
);

-- Таблица связка между CategoryOrg и Org
CREATE TABLE CF_CategoryOrg_Orgs
(
  idoforgscategories bigserial NOT NULL,
  idoforg bigint,
  idofcategoryorg bigint,
  CONSTRAINT cf_orgscategories_pk PRIMARY KEY (idoforgscategories ),
  CONSTRAINT cf_orgscategories_idofcategoryorg FOREIGN KEY (idofcategoryorg)
  REFERENCES CF_CategoryOrg (idofcategoryorg),
  CONSTRAINT cf_orgscategories_idoforg FOREIGN KEY (idoforg)
  REFERENCES Cf_Orgs (idoforg)
);

-- Таблица связка между DiscountRules и CategoryDiscountRule
CREATE TABLE CF_DiscountRules_CategoryDiscounts
(
  idofdrcd bigserial NOT NULL,
  idofrule bigint NOT NULL,
  idofcategorydiscount bigint NOT NULL,
  CONSTRAINT cf_discountrulescategorydiscount_pk PRIMARY KEY (idofrule , idofcategorydiscount ),
  CONSTRAINT cf_discountrulescategorydiscount_idofcategorydiscount FOREIGN KEY (idofcategorydiscount)
  REFERENCES CF_CategoryDiscounts (idofcategorydiscount),
  CONSTRAINT cf_discountrulescategorydiscount_idofdiscountrules FOREIGN KEY (idofrule)
  REFERENCES CF_DiscountRules (idofrule)
);

-- Таблица связка между CategoryOrg и DiscountRule
CREATE TABLE CF_DiscountRules_CategoryOrg
(
  idofcatorgdiscrule bigserial NOT NULL,
  idofcategoryorg bigint,
  idofrule bigint,
  CONSTRAINT cf_catorgdiscrule_pk PRIMARY KEY (idofcatorgdiscrule),
  CONSTRAINT cf_catorgdiscrule_categoryorg FOREIGN KEY (idofcategoryorg)
  REFERENCES CF_CategoryOrg (idofcategoryorg),
  CONSTRAINT cf_catorgdiscrule_discountrule FOREIGN KEY (idofrule)
  REFERENCES CF_DiscountRules (idofrule)
);


-- Таблица связка между Client и CategoryCF_Clients_CategoryDiscountsDiscountRule
CREATE TABLE CF_Clients_CategoryDiscounts
(
  idofclienscategorydiscount bigserial NOT NULL,
  idofclient bigint,
  idofcategorydiscount bigint,
  CONSTRAINT cf_clienscategorydiscount_pk PRIMARY KEY (idofclienscategorydiscount ),
  CONSTRAINT cf_clienscategorydiscount_categorydiscount FOREIGN KEY (idofcategorydiscount)
  REFERENCES CF_CategoryDiscounts (idofcategorydiscount),
  CONSTRAINT cf_clienscategorydiscount_client FOREIGN KEY (idofclient)
  REFERENCES CF_Clients (idofclient)
);

