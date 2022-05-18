CREATE TABLE CF_Dul_Guide
(
    Document_type_id INT,
    Name VARCHAR(255),

    CONSTRAINT CF_Dul_Guide_pk PRIMARY KEY (Document_type_id)
);

COMMENT ON TABLE CF_Dul_Guide IS 'Справочник ДУЛ';
COMMENT ON COLUMN CF_Dul_Guide.Document_type_id IS 'Код типа ДУЛ';
COMMENT ON COLUMN CF_Dul_Guide.Name IS 'Наименование';

CREATE SEQUENCE CF_Dul_Detail_id
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1;

CREATE TABLE CF_Dul_Detail
(
    Id INT8 DEFAULT nextval('CF_Dul_Detail_id') NOT NULL,
    IdMKDocument INT8,
    IdOfClient INT8 NOT NULL,
    Document_Type_Id INT4 NOT NULL,
    Series VARCHAR(64),
    Number VARCHAR(64) NOT NULL,
    Subdivision_Code VARCHAR(64),
    Issuer VARCHAR(64),
    issued INT8,
    Expiration INT8,
    CreateDate INT8 NOT NULL,
    LastUpdate INT8 NOT NULL,
    DeleteState BOOLEAN DEFAULT false,

    CONSTRAINT CF_Dul_Detail_pk PRIMARY KEY (Id),
    CONSTRAINT CF_Dul_Detail_Client_fk FOREIGN KEY (IdOfClient) REFERENCES cf_clients (IdOfClient),
    CONSTRAINT CF_Dul_Detail_Guide_fk FOREIGN KEY (document_type_id) REFERENCES CF_Dul_Guide (document_type_id)
);

COMMENT ON TABLE CF_Dul_Detail IS 'ДУЛ по представителям';
COMMENT ON COLUMN CF_Dul_Detail.Id IS 'Идентификатор документа';
COMMENT ON COLUMN CF_Dul_Detail.IdMKDocument IS 'Идентификатор документа в МЭШ Контингенте';
COMMENT ON COLUMN CF_Dul_Detail.IdOfClient IS 'Ссылка на идентификатор клиента';
COMMENT ON COLUMN CF_Dul_Detail.Document_Type_Id IS 'Идентификатор типа документа по справочнику "Вид документа"';
COMMENT ON COLUMN CF_Dul_Detail.Series IS 'Серия документа';
COMMENT ON COLUMN CF_Dul_Detail.Number IS 'Номер документа';
COMMENT ON COLUMN CF_Dul_Detail.Subdivision_Code IS 'Код подразделения (для паспорта)';
COMMENT ON COLUMN CF_Dul_Detail.Issuer IS 'Кем выдан';
COMMENT ON COLUMN CF_Dul_Detail.issued IS 'Когда выдан';
COMMENT ON COLUMN CF_Dul_Detail.Expiration IS 'Дата истечения срока действия';
COMMENT ON COLUMN CF_Dul_Detail.CreateDate IS 'Дата создания записи';
COMMENT ON COLUMN CF_Dul_Detail.LastUpdate IS 'Дата изменения записи';
COMMENT ON COLUMN CF_Dul_Detail.DeleteState IS 'Признак удаления ';

INSERT INTO CF_Dul_Guide (Document_type_id, Name) VALUES (15, 'Паспорт РФ');
INSERT INTO CF_Dul_Guide (Document_type_id, Name) VALUES (5, 'Справка об освобождении');
INSERT INTO CF_Dul_Guide (Document_type_id, Name) VALUES (19, 'Паспорт моряка');
INSERT INTO CF_Dul_Guide (Document_type_id, Name) VALUES (3, 'Свидетельство о рождении');
INSERT INTO CF_Dul_Guide (Document_type_id, Name) VALUES (2, 'Заграничный паспорт гражданина СССР');
INSERT INTO CF_Dul_Guide (Document_type_id, Name) VALUES (18, 'Удостоверение личности военнослужащего Российской Федерации');
INSERT INTO CF_Dul_Guide (Document_type_id, Name) VALUES (17, 'Свидетельство о рождении, выданное уполномоченным органом иностранного государства');
INSERT INTO CF_Dul_Guide (Document_type_id, Name) VALUES (16, 'Заграничный паспорт гражданина Российской Федерации');
INSERT INTO CF_Dul_Guide (Document_type_id, Name) VALUES (14, 'Свидетельство о предоставлении временного убежища на территории Российской Федерации');
INSERT INTO CF_Dul_Guide (Document_type_id, Name) VALUES (13, 'Разрешение на временное проживание лица без гражданства в Российской Федерации');
INSERT INTO CF_Dul_Guide (Document_type_id, Name) VALUES (12, 'Временное удостоверение личности гражданина Российской Федерации');
INSERT INTO CF_Dul_Guide (Document_type_id, Name) VALUES (11, 'Удостоверение беженца');
INSERT INTO CF_Dul_Guide (Document_type_id, Name) VALUES (10, 'Вид на жительство');
INSERT INTO CF_Dul_Guide (Document_type_id, Name) VALUES (8, 'Дипломатический паспорт гражданина Российской Федерации');
INSERT INTO CF_Dul_Guide (Document_type_id, Name) VALUES (7, 'Временное удостоверение, выданное взамен военного билета');
INSERT INTO CF_Dul_Guide (Document_type_id, Name) VALUES (1, 'Паспорт гражданина СССР');
INSERT INTO CF_Dul_Guide (Document_type_id, Name) VALUES (6, 'Военный билет');
INSERT INTO CF_Dul_Guide (Document_type_id, Name) VALUES (900, 'Паспорт иностранного гражданина');
INSERT INTO CF_Dul_Guide (Document_type_id, Name) VALUES (901, 'Документ, удостоверяющий личность на период рассмотрения заявления о признании гражданином РФ или о приеме в гражданство РФ');
INSERT INTO CF_Dul_Guide (Document_type_id, Name) VALUES (902, 'Свидетельство о рассмотрении ходатайства о признании беженцем на территории РФ по существу');


INSERT INTO cf_dul_detail (idofclient, document_type_id, series, number, createdate, lastupdate)
SELECT idofclient, int4(15), passportseries, passportnumber,
       cast(extract(epoch from now()) * 1000 as bigint),
       cast(extract(epoch from now()) * 1000 as bigint)
FROM cf_clients
where passportseries is not null
  and passportseries != ''
  and passportnumber is not null
  and passportnumber != '';

update cf_clients set san = replace(replace(san, ' ', '' ), '-', '' ) where san is not null and san != '';

ALTER TABLE cf_client_guardian ADD COLUMN IdOfRole INT4;
COMMENT ON COLUMN cf_client_guardian.IdOfRole IS 'Вид представительства из МК (1 = "Родитель", 2 = "Опекун", 3 = "Попечитель", 4 = "Представитель органа опеки и попечительства", 5 = "Доверенный представитель")';
