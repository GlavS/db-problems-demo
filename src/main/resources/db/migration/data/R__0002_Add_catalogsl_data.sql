set identity_insert directors on;
insert into directors(id, name)
values (1, 'Админ Супервайзерович Рут'),
       (2, 'Валентина Игоревна Априори'),
       (3, 'Питуний Дельфинович Автостопов'),
       (4, 'Инокентий Купертинович Жужа');
set identity_insert directors off;

set identity_insert genres on;
insert into genres(id, name)
values (1, 'Детектив'),
       (2, 'Фантастика'),
       (3, 'Фэнтези'),
       (4, 'Боевик');
set identity_insert genres off;



