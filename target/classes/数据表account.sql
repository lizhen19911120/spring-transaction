-- create table account
-- !create_table_account
CREATE TABLE account (
  `id` int(11) UNSIGNED ZEROFILL NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `money` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT money_not_zero CHECK (money>=0)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- insert_table_account
-- !insert_table_account
INSERT INTO account VALUES ('1', 'aaa', '1000');
INSERT INTO account VALUES ('2', 'bbb', '1000');
INSERT INTO account VALUES ('3', 'ccc', '1000');

-- create table book
-- !create_table_book
CREATE TABLE book (
  `isbn` int(11) NOT NULL AUTO_INCREMENT,
  `book_name` varchar(20) NOT NULL,
  `price` double DEFAULT NULL,
  PRIMARY KEY (`isbn`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- insert_table_book
-- !insert_table_book
INSERT INTO book(book_name,price) VALUES ('java编程思想', '200');
INSERT INTO book(book_name,price) VALUES ('数据结构和算法', '250');
INSERT INTO book(book_name,price) VALUES ('tcp/ip协议(卷一)', '350');
INSERT INTO book(book_name,price) VALUES ('Python入门', '300');

-- create table book_stock
-- !create_table_book_stock
CREATE TABLE book_stock (
  `isbn` int(11) NOT NULL,
  `stock` INT NOT NULL,
  PRIMARY KEY (`isbn`),
  foreign key(`isbn`) references `book`(`isbn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- insert_table_book_stock
-- !insert_table_book_stock
INSERT INTO book_stock VALUES (1, '3');
INSERT INTO book_stock VALUES (2, '1');
INSERT INTO book_stock VALUES (3, '5');
INSERT INTO book_stock VALUES (4, '10');
INSERT INTO book_stock VALUES (5, '4');