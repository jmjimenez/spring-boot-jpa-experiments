CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    name varchar(100) NOT NULL,
    email varchar(100) NOT NULL UNIQUE,
    username varchar(50) UNIQUE,
    password varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS post (
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
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_tag (
    user_id INT NOT NULL,
    tag_id INT NOT NULL,
    PRIMARY KEY (user_id, tag_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS post_comment (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    post_id INT NOT NULL,
    user_id INT NOT NULL,
    comment TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
