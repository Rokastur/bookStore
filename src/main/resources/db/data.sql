insert into Book(barcode, title, quantity, unit_price, Book_Type) values('9787096271682', 'A', 5,5.55, 'Book');
insert into Book(barcode, title, quantity, unit_price, Book_Type) values('9787096271683', 'B', 450,7.32, 'Book');
insert into Book(barcode, title, quantity, unit_price, Book_Type) values('9787096271684', 'C', 35,8.24, 'Book');
insert into Book(barcode, title, quantity, unit_price, Book_Type) values('0715955209', 'D', 15,8, 'Book');
insert into Book(barcode, title, quantity, unit_price, Book_Type) values('0715955205', 'E', 49,8.84, 'Book');

insert into Book(barcode, title, quantity, unit_price, Book_Type, release_year) values('0483008134', 'AA', 2 ,378.99, 'AntiqueBook', '1885-01-01');
insert into Book(barcode, title, quantity, unit_price, Book_Type, release_year) values('0483008135', 'BB', 5 ,200.85, 'AntiqueBook', '1768-10-05');
insert into Book(barcode, title, quantity, unit_price, Book_Type, release_year) values('9788142093159', 'CC', 20 ,45.43, 'AntiqueBook', '1899-11-22');

insert into Book(barcode, title, quantity, unit_price, Book_Type, science_index) values('9782798520922', 'AAA', 5,54.29, 'ScienceJournal', 3);
insert into Book(barcode, title, quantity, unit_price, Book_Type, science_index) values('9782798520923', 'BBB', 20,25.44, 'ScienceJournal', 9);
insert into Book(barcode, title, quantity, unit_price, Book_Type, science_index) values('9782798520924', 'CCC', 100,13.50, 'ScienceJournal', 2);

insert into Author (id, name, last_name) values (1,'John','Doe');
insert into Author (id, name, last_name) values (2,'Jane','Doe');
insert into Author (id, name, last_name) values (3,'Grace','Goe');
insert into Author (id, name, last_name) values (4,'Larry','Loe');

/*authors for regular books*/
insert into Book_Authors(barcode, author_id) values ('9787096271682',1);
insert into Book_Authors(barcode, author_id) values ('9787096271683',1);
insert into Book_Authors(barcode, author_id) values ('9787096271683',2);
insert into Book_Authors(barcode, author_id) values ('9787096271684',2);
insert into Book_Authors(barcode, author_id) values ('0715955209',3);
insert into Book_Authors(barcode, author_id) values ('0715955205',4);

/*authors for antique books*/
insert into Book_Authors(barcode, author_id) values ('0483008134',1);
insert into Book_Authors(barcode, author_id) values ('0483008135',1);
insert into Book_Authors(barcode, author_id) values ('9788142093159',2);
insert into Book_Authors(barcode, author_id) values ('9788142093159',3);
insert into Book_Authors(barcode, author_id) values ('9788142093159',4);

/*authors for science journals*/
insert into Book_Authors(barcode, author_id) values ('9782798520922',1);
insert into Book_Authors(barcode, author_id) values ('9782798520923',1);
insert into Book_Authors(barcode, author_id) values ('9782798520923',2);
insert into Book_Authors(barcode, author_id) values ('9782798520923',3);
insert into Book_Authors(barcode, author_id) values ('9782798520923',4);
insert into Book_Authors(barcode, author_id) values ('9782798520924',2);

