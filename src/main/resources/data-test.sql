--
-- Populate with initial roles USER, ADMIN and UNCONFIRMED
--
INSERT INTO tlrl_role(id) 
SELECT 'ROLE_USER' WHERE NOT EXISTS (
	SELECT id FROM tlrl_role WHERE id = 'ROLE_USER');
   
INSERT INTO tlrl_role(id) 
SELECT 'ROLE_UNCONFIRMED' WHERE NOT EXISTS (
	SELECT id FROM tlrl_role WHERE id = 'ROLE_UNCONFIRMED');
   
INSERT INTO tlrl_role(id) 
SELECT 'ROLE_ADMIN' WHERE NOT EXISTS (
	SELECT id FROM tlrl_role WHERE id = 'ROLE_ADMIN');

--
-- Local dev user for testing
-- 
INSERT INTO tlrl_user (id, email, enabled, name, role_id)
SELECT nextval('user_id_seq'), 'thong@gnoht.com', true, 'thong', 'ROLE_USER' WHERE NOT EXISTS (
	SELECT id FROM tlrl_user WHERE email = 'thong@gnoht.com');

--
-- Test data
-- 
INSERT INTO weburl(id, title, url, date_created, date_modified)
  VALUES (nextval('weburl_id_seq'), 'Craigslist.org', 'http://losangeles.craigslist.org/', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
  
  
  