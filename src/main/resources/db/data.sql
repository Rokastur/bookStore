INSERT INTO Book(barcode, title, quantity, unit_price, Book_Type) VALUES('ABC', 'A', 5,5.55, 'Book');
INSERT INTO Book(barcode, title, quantity, unit_price, Book_Type) VALUES('ABCD', 'B', 450,7.32, 'Book');
INSERT INTO Book(barcode, title, quantity, unit_price, Book_Type) VALUES('ABCDE', 'C', 35,8.24, 'Book');
INSERT INTO Book(barcode, title, quantity, unit_price, Book_Type) VALUES('ABCDEF', 'D', 2 ,3.78, 'Book');
INSERT INTO Book(barcode, title, quantity, unit_price, Book_Type) VALUES('ABCDEFG', 'E', 10,54.29, 'Book');



INSERT INTO Author (id, name, last_name) VALUES (1,'X','XX');
INSERT INTO Author (id, name, last_name) VALUES (2,'Y','YY');
INSERT INTO Author (id, name, last_name) VALUES (3,'Z','ZZ');
INSERT INTO Author (id, name, last_name) VALUES (4,'T','TT');

INSERT INTO Book_Authors(barcode, author_id) VALUES ('ABC',1);
INSERT INTO Book_Authors(barcode, author_id) VALUES ('ABCD',2);
INSERT INTO Book_Authors(barcode, author_id) VALUES ('ABCDE',3);
INSERT INTO Book_Authors(barcode, author_id) VALUES ('ABCDEF',4);
INSERT INTO Book_Authors(barcode, author_id) VALUES ('ABCDEFG',4);

