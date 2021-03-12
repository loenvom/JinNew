create table User
(
    nickname varchar(30) NOT NULL,
    userid varchar(30) NOT NULL,
    password varchar(30) NOT NULL
)charset=utf8;

create table Comment
(
    userid varchar(30) NOT NULL,
    uniquekey varchar(100) NOT NULL,
    words varchar(1000) NOT NULL,
    time varchar(30) NOT NULL
)charset=utf8;

create table Collect
(
    userid varchar(30) NOT NULL,
    uniquekey varchar(100) NOT NULL,
    title varchar(30) NOT NULL,
    image_src varchar(200) NOT NULL,
    content_url varchar(100) NOT NULL
)charset=utf8;