USE test;
DROP TABLE IF EXISTS test.student;
CREATE TABLE  test.student(
   ID int(10) NOT NULL AUTO_INCREMENT,
   NAME varchar(100) NOT NULL,
   BRANCH varchar(255) NOT NULL,
   PERCENTAGE int(3) NOT NULL,
   PHONE int(10) NOT NULL,
   EMAIL varchar(255) NOT NULL,
   PRIMARY KEY ( ID )
);

CREATE TABLE  test.GuBaTopic(
  topic_id int(16) NOT NULL,
  author_id int(16) NOT NULL,
  author_name varchar(256) NOT NULL,
  title varchar(32) NOT NULL,
  stock_code varchar(16) NOT NULL,
  content varchar(512) NOT NULL,
  time timestamp NOT NULL,
  PRIMARY KEY ( topic_id )
);

