insert into note (title, body) values
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

insert into user (first_name, last_name, username, password, active) values
  ('User1', 'Test', 'admin', '$2y$10$ssxSU/nxbDFVMrLzc4/cZ.9yokHPImaezdoYrz9Hcr.2BzD12YnR6', 1),
  ('User2', 'Test', 'user', '$2y$10$ssxSU/nxbDFVMrLzc4/cZ.9yokHPImaezdoYrz9Hcr.2BzD12YnR6', 1);

insert into role (type) values
  ('ROLE_USER'),
  ('ROLE_ADMIN');

insert into user_role (user_id, role_id) values
  (1, 1),
  (1, 2),
  (2, 1);