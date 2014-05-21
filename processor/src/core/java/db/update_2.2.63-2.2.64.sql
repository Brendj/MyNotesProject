--! Фикс построения отчетов для фин. показателей
delete from cf_projectstate_data where type>=2000 and type<=3000;