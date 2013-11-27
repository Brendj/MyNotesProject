update cf_orgs set RefectoryType=3 where idoforg in (SELECT distinct idofsourceorg FROM cf_menuexchangerules);

UPDATE cf_goods g1 SET idofconfigurationprovider=(SELECT idofconfigurationprovider
                                                  FROM cf_orgs
                                                  where idoforg = (SELECT distinct orgowner FROM cf_goods g2
                                                  where orgowner=idoforg and g2.idofgood = g1.idofgood))
WHERE g1.idofconfigurationprovider is null;

UPDATE cf_goods_groups g1 SET idofconfigurationprovider=(SELECT idofconfigurationprovider
                                                         FROM cf_orgs
                                                         where idoforg = (SELECT distinct orgowner FROM cf_goods_groups g2
                                                         where orgowner=idoforg and g2.idofgoodsgroup = g1.idofgoodsgroup))
WHERE g1.idofconfigurationprovider is null;

UPDATE cf_trade_material_goods g1 SET idofconfigurationprovider=(SELECT idofconfigurationprovider
                                                                 FROM cf_orgs
                                                                 where idoforg = (SELECT distinct orgowner FROM cf_trade_material_goods g2
                                                                 where orgowner=idoforg and g2.idoftradematerialgood = g1.idoftradematerialgood))
WHERE g1.idofconfigurationprovider is null;

UPDATE cf_good_basic_basket_price g1 SET idofconfigurationprovider=(SELECT idofconfigurationprovider
                                                                    FROM cf_orgs
                                                                    where idoforg = (SELECT distinct orgowner FROM cf_good_basic_basket_price g2
                                                                    where orgowner=idoforg and g2.idofgoodbasicbasketprice = g1.idofgoodbasicbasketprice))
WHERE g1.idofconfigurationprovider is null;

update cf_reportinfo c set idofcontragentreceiver = (SELECT cast((regexp_matches(reportfile, 'SSTSReport\-\d+\-(\d+)\-*')::text[])[1] as integer) from cf_reportinfo c2 where c2.idofreportinfo = c.idofreportinfo);