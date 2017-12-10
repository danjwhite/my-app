create table note (
  id identity,
  title varchar(140) not null,
  created_at timestamp not null,
  body varchar(5000) not null
);