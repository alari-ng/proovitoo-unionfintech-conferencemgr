DROP TABLE IF EXISTS participant;
DROP TABLE IF EXISTS conference;

CREATE TABLE conference (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(250) NOT NULL UNIQUE,
  seats INT NOT NULL,
  cancelled BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE participant (
  id INT AUTO_INCREMENT PRIMARY KEY,
  conference_id INT NOT NULL REFERENCES conference,
  participant_name VARCHAR(250) NOT NULL,
  UNIQUE (conference_id, participant_name)
);
