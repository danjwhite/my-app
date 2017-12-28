drop table if exists note;

create table note (
  id int auto_increment primary key,
  created_at timestamp not null default current_timestamp,
  title varchar(140) not null,
  body varchar(5000) not null
);

insert into note(title, body) values
  ('Title', 'Body'),
  ('Title', 'Body'),
  ('Title', 'Body'),
  ('Title', 'Body'),
  ('Title', 'Body'),
  ('Title', 'Body'),
  ('Title', 'Body'),
  ('Title', 'Body'),
  ('Title', 'Body'),
  ('Title', 'Body'),
  ('Title', 'Body'),
  ('Title', 'Body');