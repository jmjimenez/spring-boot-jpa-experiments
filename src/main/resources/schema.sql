CREATE TABLE IF NOT EXISTS Post (
   id SERIAL PRIMARY KEY,
   user_id INT NOT NULL,
   title varchar(250) NOT NULL,
   body text NOT NULL
);