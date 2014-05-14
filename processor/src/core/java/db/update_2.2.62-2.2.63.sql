-- изменение наименований супер-категорий, в настоящий момент (14.05.14), применяется только в справках расходования средств
update cf_discountrules set subcategory='Средние и  старшие классы 5-11 (завтрак + обед + полдник)'
where subcategory='Средние и  старшие калссы 5-11 (завтрак + обед + полдник)';
update cf_discountrules set subcategory='Бесплатники 1-4 кл.(завтрак+обед)'
where subcategory='Шк Здоровья 1-4 кл.(завтрак+обед)';
update cf_discountrules set subcategory='Бесплатники 5-11 кл.(завтрак+обед)'
where subcategory='Шк Здоровья 5-11 кл.(завтрак+обед)'