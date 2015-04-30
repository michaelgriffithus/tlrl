-- sql patch to accompany commit 86a4c77
ALTER TABLE read_later ADD read_later_status character varying(255);
UPDATE read_later SET read_later_status = 'NA';
ALTER TABLE read_later ALTER read_later_status SET DEFAULT 'NA';
ALTER TABLE read_later ALTER read_later_status SET NOT NULL;
ALTER TABLE webpage ADD fetched BOOLEAN;
UPDATE webpage set fetched = TRUE;
ALTER TABLE webpage ALTER fetched SET DEFAULT FALSE;
ALTER TABLE webpage ALTER fetched SET NOT NULL;