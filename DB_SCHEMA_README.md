# Database Schema Documentation

This document describes the database structure for the Spring Boot JPA Experiments project.

## Overview

The database uses PostgreSQL and follows a blog-like structure with users, posts, and tags. The schema is managed through Flyway migrations and includes comprehensive sample data for development and testing.

## Database Technology Stack

- **Database**: PostgreSQL
- **Migration Tool**: Flyway
- **ORM**: JPA/Hibernate
- **Connection Pool**: HikariCP (Spring Boot default)

## Schema Structure

### Core Tables

#### 1. Users Table
```sql
CREATE TABLE Users (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    name varchar(100) NOT NULL,
    email varchar(100) NOT NULL UNIQUE,
    username varchar(50) UNIQUE
);
```

**Purpose**: Stores user information for the blog system.

**Fields**:
- `id`: Auto-incrementing primary key
- `uuid`: Unique identifier for external API usage
- `name`: User's full name (required)
- `email`: User's email address (required, unique)
- `username`: Optional username (unique if provided)

**Constraints**:
- Primary key on `id`
- Unique constraint on `uuid`
- Unique constraint on `email`
- Unique constraint on `username` (nullable)

#### 2. Post Table
```sql
CREATE TABLE Post (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    user_id INT NOT NULL,
    title varchar(250) NOT NULL UNIQUE,
    body text NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE
);
```

**Purpose**: Stores blog posts created by users.

**Fields**:
- `id`: Auto-incrementing primary key
- `uuid`: Unique identifier for external API usage
- `user_id`: Foreign key to Users table (required)
- `title`: Post title (required, unique)
- `body`: Post content (required, text field)

**Constraints**:
- Primary key on `id`
- Unique constraint on `uuid`
- Unique constraint on `title`
- Foreign key constraint on `user_id` with CASCADE DELETE

#### 3. Tag Table
```sql
CREATE TABLE tag (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    tag varchar(60) NOT NULL UNIQUE
);
```

**Purpose**: Stores tags that can be associated with posts and users.

**Fields**:
- `id`: Auto-incrementing primary key
- `uuid`: Unique identifier for external API usage
- `tag`: Tag name (required, unique)

**Constraints**:
- Primary key on `id`
- Unique constraint on `uuid`
- Unique constraint on `tag`

### Junction Tables

#### 4. post_tag Table
```sql
CREATE TABLE post_tag (
    post_id INT NOT NULL,
    tag_id INT NOT NULL,
    PRIMARY KEY (post_id, tag_id),
    FOREIGN KEY (post_id) REFERENCES Post (id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag (id) ON DELETE CASCADE
);
```

**Purpose**: Many-to-many relationship between posts and tags.

**Fields**:
- `post_id`: Foreign key to Post table
- `tag_id`: Foreign key to Tag table

**Constraints**:
- Composite primary key on `(post_id, tag_id)`
- Foreign key constraints with CASCADE DELETE

#### 5. user_tag Table
```sql
CREATE TABLE user_tag (
    user_id INT NOT NULL,
    tag_id INT NOT NULL,
    PRIMARY KEY (user_id, tag_id),
    FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag (id) ON DELETE CASCADE
);
```

**Purpose**: Many-to-many relationship between users and tags.

**Fields**:
- `user_id`: Foreign key to Users table
- `tag_id`: Foreign key to Tag table

**Constraints**:
- Composite primary key on `(user_id, tag_id)`
- Foreign key constraints with CASCADE DELETE

## Entity Relationships

### JPA Entity Mappings

#### User Entity
- **One-to-Many** with Post: A user can have multiple posts
- **Many-to-Many** with Tag: A user can be associated with multiple tags

#### Post Entity
- **Many-to-One** with User: Each post belongs to one user
- **Many-to-Many** with Tag: A post can have multiple tags

#### Tag Entity
- **Many-to-Many** with User: A tag can be associated with multiple users
- **Many-to-Many** with Post: A tag can be associated with multiple posts

## Sample Data

The development environment includes comprehensive sample data

## Indexes and Performance

### Primary Keys
- All tables use auto-incrementing integer primary keys
- UUID fields have unique constraints for external API usage

### Foreign Keys
- All foreign key relationships are properly indexed
- CASCADE DELETE ensures referential integrity

### Unique Constraints
- Email addresses are unique across users
- Post titles are unique
- Tag names are unique
- Usernames are unique (when provided)

## Data Types and Constraints

### String Fields
- `name`: varchar(100) - User names
- `email`: varchar(100) - Email addresses
- `username`: varchar(50) - Optional usernames
- `title`: varchar(250) - Post titles
- `body`: text - Post content (unlimited length)
- `tag`: varchar(60) - Tag names

### UUID Fields
- All entities have UUID fields for external API identification
- UUIDs are generated using Java's UUID.randomUUID()

### Integer Fields
- Primary keys use SERIAL (auto-incrementing)
- Foreign keys use INT for referential integrity

## API Integration

### External Identifiers
- All entities use UUIDs for external API identification
- Internal IDs are used for database relationships
- UUIDs provide stable external references

### JSON Serialization
- User entities include post UUIDs and tag names in responses
- Post entities include user UUID in responses
- Circular references are handled with @JsonIgnore

## Security Considerations

### Data Validation
- Bean validation annotations on entity fields
- Database constraints for data integrity
- Input validation at API layer

### Access Control
- No built-in authentication/authorization
- Designed for demonstration and experimentation
- Production deployment would require security layer