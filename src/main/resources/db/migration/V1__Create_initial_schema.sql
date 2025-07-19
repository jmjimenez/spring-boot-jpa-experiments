CREATE TABLE IF NOT EXISTS Users (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    name varchar(100) NOT NULL,
    email varchar(100) NOT NULL UNIQUE,
    username varchar(50) UNIQUE
);

CREATE TABLE IF NOT EXISTS Post (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    user_id INT NOT NULL,
    title varchar(250) NOT NULL UNIQUE,
    body text NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tag (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    tag varchar(60) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS post_tag (
    post_id INT NOT NULL,
    tag_id INT NOT NULL,
    PRIMARY KEY (post_id, tag_id),
    FOREIGN KEY (post_id) REFERENCES Post (id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_tag (
    user_id INT NOT NULL,
    tag_id INT NOT NULL,
    PRIMARY KEY (user_id, tag_id),
    FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag (id) ON DELETE CASCADE
);