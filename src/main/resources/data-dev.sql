-- initial roles
INSERT INTO tlrl_role(id) VALUES ('ROLE_USER');
INSERT INTO tlrl_role(id) VALUES ('ROLE_ADMIN');
INSERT INTO tlrl_role(id) VALUES ('ROLE_UNCONFIRMED');

-- user for testing
INSERT INTO tlrl_user (id, email, enabled, name, role_id)
  VALUES (nextval('user_id_seq'), 'thong@gnoht.com', true, 'thong', 'ROLE_USER');