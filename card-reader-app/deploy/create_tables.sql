DROP TABLE IF EXISTS card_reader_db.student_attendence;
DROP TABLE IF EXISTS card_reader_db.student_class_registration;
DROP TABLE IF EXISTS card_reader_db.student;
DROP TABLE IF EXISTS card_reader_db.class;
DROP TABLE IF EXISTS card_reader_db.card_reader;


CREATE TABLE card_reader_db.student (
	student_id varchar(100) NOT NULL,
	card_id varchar(100) NOT NULL,
	name varchar(100) NOT NULL,
	PRIMARY KEY (student_id),
	UNIQUE(card_id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;


CREATE TABLE card_reader_db.card_reader (
	card_reader_id varchar(100) NOT NULL,
	classroom varchar(100) NOT NULL,
	PRIMARY KEY (card_reader_id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;


CREATE TABLE card_reader_db.class (
	class_id varchar(100) NOT NULL,
	class_name varchar(100) NOT NULL,
	classroom varchar(100) NOT NULL,
	from_time varchar(100) NOT NULL,
	to_time varchar(100) NOT NULL,
	PRIMARY KEY (class_id),
	UNIQUE(class_name)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;



CREATE TABLE card_reader_db.student_class_registration (
	student_id varchar(100) NOT NULL,
	class_id varchar(100) NOT NULL,
	PRIMARY KEY (student_id, class_id),
	FOREIGN KEY (student_id) REFERENCES card_reader_db.student(student_id),
	FOREIGN KEY (class_id) REFERENCES card_reader_db.class(class_id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;


CREATE TABLE card_reader_db.student_attendence (
	student_id varchar(100) NOT NULL,
  class_id varchar(100) NOT NULL,
  date date NOT NULL,
  PRIMARY KEY (student_id,class_id,date),
  KEY class_id (`class_id`),
  FOREIGN KEY (student_id) REFERENCES student (student_id),
  FOREIGN KEY (class_id) REFERENCES class (class_id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;

INSERT INTO card_reader_db.card_reader
VALUES
('84cca8aa1840', 'A3'),
('84cca8aa1811', 'A2'),
('84cca8aa18e5', 'A1');


INSERT INTO card_reader_db.class
VALUES
('class1', 'Prog 1', 'A3', '00:00:00', '23:00:00'),
('class2', 'Matek1', 'A2', '00:00:00', '23:00:00'),
('class3', 'Szakdolgozat', 'A1', '00:00:00', '23:00:00');

INSERT INTO card_reader_db.student
VALUES
('S1', '006BB32', 'Gipsz Jakab'),
('S2', '55D942A', 'Kassai Laszlo'),
('S3', '9EF35BF', 'Toth Reka'),
('S4', '0731132', 'Kovacs Janos'),
('S5', '056B832', 'Cserepes Virag'),
('S6', '0C75232', 'Kassai Csaba');

INSERT INTO card_reader_db.student_class_registration
VALUES
('S1', 'class3'),
('S2', 'class3'),
('S3', 'class3'),
('S6', 'class3');

