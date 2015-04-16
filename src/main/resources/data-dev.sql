-- initial roles
INSERT INTO tlrl_role(id) VALUES ('ROLE_USER');
INSERT INTO tlrl_role(id) VALUES ('ROLE_ADMIN');
INSERT INTO tlrl_role(id) VALUES ('ROLE_UNCONFIRMED');

-- user for testing
INSERT INTO tlrl_user(email, enabled, name, role_id)
  VALUES ('test@gnoht.com', true, 'thong', 'ROLE_USER');