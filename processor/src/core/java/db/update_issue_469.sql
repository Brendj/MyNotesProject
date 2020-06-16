
-- Установка значений для Самрт-Часов
update cf_cards
set cardsigncertnum = 16
where cardtype = 10 and cardsigncertnum is null;