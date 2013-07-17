
insert into cf_persons values (168880, '', '', '', 0);
insert into cf_persons values (168885, '', '', '', 0);
insert into cf_contragents (IdOfContragent, Version, IdOfContactPerson, ContragentName, ClassId, Flags, Title, Address, CreatedDate, LastUpdate, PublicKey, NeedAccountTranslate) values (16,168880,168885,'ГУП СП Юго-восток',2,1,'','',1339764261121,1345811029643,'',0);
-- ОРГАНИЗАЦИИ
insert into cf_orgs (IdOfOrg, Version, ShortName, OfficialName, Address, IdOfOfficialPerson, OfficialPosition, ContractId, ContractDate, State, CardLimit, PublicKey, IdOfPacket, LastClientContractId, PriceOfSms, SubscriptionPrice, DefaultSupplier, guid) values (0, 1, 'ГБОУ СОШ №327', 'ГБОУ СОШ №327', '109457, г. Москва, ул. Шумилова, д. 9', 168880, '', 1, 1324497600000, 1, 0, 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC4L90T2sgpb2AOFmaKbtQrkxWpeCDlLwTsvUrkNlG8lfi3g04qo1RBh59ph54VyGUk/cgqWw7lhWxuVvVekdqUh3ITBzJsY5ebWm9GjmQfyqPyr+D0UE3ZNcLIlxHWejCk4qQrIFvHVI42PCIyUSVy6GjT1PT8pwCYXIJsFexfhwIDAQAB', 1621, 473, 0, 0, 16, '000');
insert into cf_orgs (IdOfOrg, Version, ShortName, OfficialName, Address, IdOfOfficialPerson, OfficialPosition, ContractId, ContractDate, State, CardLimit, PublicKey, IdOfPacket, LastClientContractId, PriceOfSms, SubscriptionPrice, DefaultSupplier, guid) values (3, 1, 'ГБОУ СОШ №355', 'ГБОУ СОШ №355', '109507, г. Москва, Ферганский пр-д, д. 5, к. 2', 168880, '', 1, 1324497600000, 1, 0, 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC4L90T2sgpb2AOFmaKbtQrkxWpeCDlLwTsvUrkNlG8lfi3g04qo1RBh59ph54VyGUk/cgqWw7lhWxuVvVekdqUh3ITBzJsY5ebWm9GjmQfyqPyr+D0UE3ZNcLIlxHWejCk4qQrIFvHVI42PCIyUSVy6GjT1PT8pwCYXIJsFexfhwIDAQAB', 1621, 473, 0, 0, 16, '111');
insert into cf_friendly_organization (idoffriendlyorg, currentorg, friendlyorg) values (1, 0, 0);
insert into cf_friendly_organization (idoffriendlyorg, currentorg, friendlyorg) values (2, 3, 3);

-- КЛИЕНТЫ
insert into cf_persons values (100, 'Петр', 'Петров', 'Петрович', 0);
insert into cf_persons values (101, 'Иван', 'Иванов', 'Иванович', 0);
insert into cf_persons values (102, 'Павел', 'Павлов', 'Павлович', 0);
insert into cf_persons values (103, 'Удалить', 'Удалить', 'Удалить', 0);
insert into cf_persons values (200, 'Родитель Петр', 'Петрович', 'Петров', 0);

insert into cf_clients
(IdOfClient, Version, IdOfOrg, IdOfPerson, IdOfContractPerson, ClientRegistryVersion, Flags, Address, NotifyViaEmail, NotifyViaSMS, LastUpdate, ContractId, ContractDate, ContractState, Password, PayForSMS, FreePayCount, DiscountMode, Balance, Limits, clientguid, idofclientgroup, remarks) values
(100, 1, 0, 100, 200, 1, 0, 'г. Москва', 0, 0, 0, 123, 0, 1, 'Pswd123', 0, 0, 0, 100, 50, '1', 1000000002, '');
insert into cf_clients
(IdOfClient, Version, IdOfOrg, IdOfPerson, IdOfContractPerson, ClientRegistryVersion, Flags, Address, NotifyViaEmail, NotifyViaSMS, LastUpdate, ContractId, ContractDate, ContractState, Password, PayForSMS, FreePayCount, DiscountMode, Balance, Limits, clientguid, idofclientgroup, remarks) values
(101, 1, 0, 101, 200, 1, 0, 'г. Москва', 0, 0, 0, 1234, 0, 1, 'Pswd123', 0, 0, 0, 100, 50, '12', 1000000003, '');
insert into cf_clients
(IdOfClient, Version, IdOfOrg, IdOfPerson, IdOfContractPerson, ClientRegistryVersion, Flags, Address, NotifyViaEmail, NotifyViaSMS, LastUpdate, ContractId, ContractDate, ContractState, Password, PayForSMS, FreePayCount, DiscountMode, Balance, Limits, clientguid, idofclientgroup, remarks) values
(102, 1, 0, 102, 200, 1, 0, 'г. Москва', 0, 0, 0, 1235, 0, 1, 'Pswd123', 0, 0, 0, 100, 50, '123', 1000000002, '');
insert into cf_clients
(IdOfClient, Version, IdOfOrg, IdOfPerson, IdOfContractPerson, ClientRegistryVersion, Flags, Address, NotifyViaEmail, NotifyViaSMS, LastUpdate, ContractId, ContractDate, ContractState, Password, PayForSMS, FreePayCount, DiscountMode, Balance, Limits, clientguid, idofclientgroup, remarks) values
(103, 1, 3, 103, 200, 1, 0, 'г. Москва', 0, 0, 0, 12345, 0, 1, 'Pswd123', 0, 0, 0, 100, 50, '12345', 1000000004, '');

-- группы клиентов
insert into cf_clientgroups (idoforg, idofclientgroup, groupname) values (0, 1000000002, '6А');
insert into cf_clientgroups (idoforg, idofclientgroup, groupname) values (0, 1000000003, '10А');
insert into cf_clientgroups (idoforg, idofclientgroup, groupname) values (3, 1000000004, '10А');