-- initial roles
INSERT INTO role(id) VALUES ('ROLE_USER');
INSERT INTO role(id) VALUES ('ROLE_ADMIN');
INSERT INTO role(id) VALUES ('ROLE_UNCONFIRMED');

-- user for testing
INSERT INTO tlrl_user(email, enabled, name, role_id)
  VALUES ('thong@gnoht.com', true, 'thong', 'ROLE_USER');
  
-- 
INSERT INTO webpage(title, url, user_id, date_created, date_modified)
  VALUES ('Craigslist.org', 'http://losangeles.craigslist.org/', 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());