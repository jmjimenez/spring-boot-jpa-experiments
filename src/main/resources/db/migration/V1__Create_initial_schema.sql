CREATE TABLE IF NOT EXISTS Users (
    id SERIAL PRIMARY KEY,
    name varchar(100) NOT NULL,
    email varchar(100) NOT NULL,
    username varchar(50)
);

CREATE TABLE IF NOT EXISTS Post (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    user_id INT NOT NULL,
    title varchar(250) NOT NULL,
    body text NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users (id)
);