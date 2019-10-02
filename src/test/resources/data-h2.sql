-- TODO: remove this
-- INSERT INTO user (id, first_name, last_name, username, password) VALUES
--   -- User and admin roles
--   (1, 'Michael', 'Jones', 'mjones', '$2a$10$.E7RjddSYnrH4iL49IFiPectcHJCFpHAIRyRAbf3kX4q4lsl6EYDS'),
--   -- User role only
--   (2, 'Dennis', 'Rodman', 'drodman', '$2a$10$.E7RjddSYnrH4iL49IFiPectcHJCFpHAIRyRAbf3kX4q4lsl6EYDS');

INSERT INTO role (id, type) VALUES
  (1, 'ROLE_USER'),
  (2, 'ROLE_ADMIN');

-- TODO: remove this
-- INSERT INTO user_role (user_id, role_id) VALUES
--   (1, 1),
--   (1, 2),
--   (2, 1);
--
-- INSERT INTO note (id, created_at, user_id, title, body) VALUES
--   (1, '2018-02-02 00:00:00', 1, 'Title', 'Body'),
--   (2, '2018-02-02 00:00:00', 1, 'Title', 'Body'),
--   (3, '2018-02-02 00:00:00',  1, 'Title', 'Body'),
--   (4, '2018-02-02 00:00:00',  1, 'Title', 'Body'),
--   (5, '2018-02-02 00:00:00', 1, 'Title', 'Body'),
--   (6, '2018-02-02 00:00:00', 1, 'Title', 'Body'),
--   (7, '2018-02-02 00:00:00', 1, 'Title', 'Body'),
--   (8, '2018-02-02 00:00:00', 1, 'Title', 'Body'),
--   (9, '2018-02-02 00:00:00', 1, 'Title', 'Body'),
--   (10, '2018-02-02 00:00:00', 1, 'Title', 'Body'),
--   (11, '2018-02-02 00:00:00', 1, 'Title', 'Body'),
--   (12, '2018-02-02 00:00:00', 1, 'Title', 'Body'),
--   (13, '2018-02-02 00:00:00', 2, 'Title', 'Body'),
--   (14, '2018-02-02 00:00:00', 2, 'Title', 'Body'),
--   (15, '2018-02-02 00:00:00', 2, 'Title', 'Body'),
--   (16, '2018-02-02 00:00:00', 2, 'Title', 'Body'),
--   (17, '2018-02-02 00:00:00', 2, 'Title', 'Body'),
--   (18, '2018-02-02 00:00:00', 2, 'Title', 'Body'),
--   (19, '2018-02-02 00:00:00', 2, 'Title', 'Body'),
--   (20, '2018-02-02 00:00:00', 2, 'Title', 'Body'),
--   (21, '2018-02-02 00:00:00', 2, 'Title', 'Body'),
--   (22, '2018-02-02 00:00:00', 2, 'Title', 'Body'),
--   (23, '2018-02-02 00:00:00', 2, 'Title', 'Body'),
--   (24, '2018-02-02 00:00:00', 2, 'Title', 'Body');