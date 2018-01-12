insert into user (id, first_name, last_name, username, password) values
  -- User and admin roles
  (1, 'Michael', 'Jones', 'mjones', '$2y$10$58hikU/OYmVojQkUwGQHRO9.oKPVPG6t3WShvU4NqHNTzzloZpwXC'),
  -- User and admin roles
  (2, 'Andrea', 'Cole', 'acole', '$2y$10$58hikU/OYmVojQkUwGQHRO9.oKPVPG6t3WShvU4NqHNTzzloZpwXC'),
  -- User role only
  (3, 'Jennifer', 'Ellis', 'jellis', '$2a$10$CfqMgQAD1neowMElhzaStOqhrpZf0RZO7t9TTuU4zOytvRnsXIcny'),
  -- User role only
  (4, 'Dennis', 'Rodman', 'drodman', '$2y$10$58hikU/OYmVojQkUwGQHRO9.oKPVPG6t3WShvU4NqHNTzzloZpwXC');

insert into role (id, type) values
  (1, 'ROLE_USER'),
  (2, 'ROLE_ADMIN');

insert into user_role (user_id, role_id) values
  (1, 1),
  (1, 2),
  (2, 1),
  (2, 2),
  (3, 1),
  (4, 1);

insert into note (id, title, body) values
  (1, 'Title', 'Body'),
  (2, 'Title', 'Body'),
  (3, 'Title', 'Body'),
  (4, 'Title', 'Body'),
  (5, 'Title', 'Body'),
  (6, 'Title', 'Body'),
  (7, 'Title', 'Body'),
  (8, 'Title', 'Body'),
  (9, 'Title', 'Body'),
  (10, 'Title', 'Body'),
  (11, 'Title', 'Body'),
  (12, 'Title', 'Body');