CREATE TABLE tmp_idofmenu (
  IdOfMenuDetail          BIGINT,
  CONSTRAINT tmp_idofmenu_pk PRIMARY KEY (IdOfMenuDetail)
);

delete from tmp_idofmenu;
insert into tmp_idofmenu (select menudetail1_.IdOfMenuDetail as tmp_idofmenu from CF_MenuDetails menudetail1_
where menudetail1_.IdOfMenu in (
  select m.IdOfMenu from CF_Menu m where m.IdOfOrg not in (
    select distinct mer.idOfSourceOrg from CF_MenuExchangeRules mer) and m.MenuDate < 1385424000000
));

delete from CF_ComplexInfoDetail where IdOfMenuDetail in (select idofmenudetail from tmp_idofmenu);
delete from CF_ComplexInfo where IdOfMenuDetail in (select idofmenudetail from tmp_idofmenu);
delete from CF_MenuDetails where IdOfMenuDetail in (select idofmenudetail from tmp_idofmenu);
delete from CF_Menu m where m.IdOfOrg not in (select distinct mer.idOfSourceOrg from CF_MenuExchangeRules mer)
                            and m.MenuDate < 1385424000000;

