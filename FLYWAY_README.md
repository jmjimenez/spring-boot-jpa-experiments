# Flyway Database Migration Setup

This project uses Flyway for database migrations with PostgreSQL support, including repeatable migrations for development and testing environments.

## Configuration

### Dependencies
- `flyway-core`: Core Flyway functionality
- `flyway-database-postgresql`: PostgreSQL-specific support

### Properties Configuration
```properties
# Flyway Configuration
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration,classpath:db/migration/dev,classpath:db/migration/test
spring.flyway.baseline-on-migrate=true
spring.flyway.validate-on-migrate=true

# Disable default schema initialization since we're using Flyway
spring.sql.init.mode=never
```

### Profile-Specific Configuration

#### Dev Profile Properties
```properties
# Dev Profile Configuration
spring.profiles.active=dev
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration,classpath:db/migration/dev
```

#### Test Profile Properties
```properties
# Test Profile Configuration
spring.profiles.active=test
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration,classpath:db/migration/test
```

## Migration Files

Migrations are located in `src/main/resources/db/migration/` and follow the naming convention:
- `V{version}__{description}.sql` for versioned migrations
- `R__{description}.sql` for repeatable migrations

### File Structure
```
src/main/resources/db/migration/
├── V1__Create_initial_schema.sql          # Versioned migration (runs once)
├── V2__Insert_sample_data.sql             # Versioned migration (runs once)
├── dev/
│   └── R__Insert_dev_data.sql             # Repeatable migration (runs every time)
└── test/
    └── R__Insert_test_data.sql             # Repeatable migration (runs every time)
```

### Migration Types

1. **Versioned Migrations** (`V1__`, `V2__`, etc.)
   - Run only once
   - Tracked in Flyway's schema history
   - Used for schema changes

2. **Repeatable Migrations** (`R__`)
   - Run every time the application starts
   - Not tracked in schema history
   - Perfect for data seeding in development and testing

### Current Migrations

1. **V1__Create_initial_schema.sql**: Creates the initial database schema
   - `Users` table with id, name, email, username
   - `Post` table with id, uuid, user_id, title, body
   - Foreign key relationship between Post and Users

2. **V2__Insert_sample_data.sql**: Inserts sample data
   - Sample users (John Doe, Jane Smith, Bob Johnson)
   - Sample posts with UUIDs and relationships

3. **R__Insert_dev_data.sql**: Repeatable migration for development
   - **Location**: `src/main/resources/db/migration/dev/R__Insert_dev_data.sql`
   - **Behavior**: Executes every time the application starts with the `dev` profile
   - **Data**: Inserts 5 users and 50 posts (10 per user)

4. **R__Insert_test_data.sql**: Repeatable migration for testing
   - **Location**: `src/main/resources/db/migration/test/R__Insert_test_data.sql`
   - **Behavior**: Executes every time the application starts with the `test` profile
   - **Data**: Inserts test data optimized for testing scenarios

## Usage

### Running Migrations
Migrations run automatically when the application starts. You can also run them manually:

```bash
# Using Maven
mvn flyway:migrate

# Using Flyway CLI (if installed)
flyway migrate
```

### Running with Dev Profile
```bash
# Set the dev profile
export DEFAULT_PROFILE=dev

# Or run with explicit profile
java -jar your-app.jar --spring.profiles.active=dev

# Or using Maven
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Running with Test Profile
```bash
# Set the test profile
export DEFAULT_PROFILE=test

# Or run with explicit profile
java -jar your-app.jar --spring.profiles.active=test

# Or using Maven
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

### Running Tests
```bash
# Run tests with test profile
mvn test -Dspring.profiles.active=test

# Or run specific test class
mvn test -Dtest=UserRepositoryTest -Dspring.profiles.active=test
```

### Creating New Migrations

1. Create a new SQL file in `src/main/resources/db/migration/`
2. Use the naming convention: `V{next_version}__{description}.sql`
3. Example: `V3__Add_user_roles.sql`

## Repeatable Migration Behavior

### How It Works

- **Dev Profile**: Migration executes every startup
- **Test Profile**: Migration executes every startup
- **Other Profiles**: Migrations are ignored
- **Data Freshness**: Always starts with clean data
- **No Conflicts**: No duplicate key issues

### What Happens
1. **First Run**: Creates users and posts
2. **Subsequent Runs**: 
   - Deletes all existing data
   - Re-inserts fresh data
   - Ensures consistent state

### Key Features
✅ **Always fresh data**: Clears existing data and re-inserts  
✅ **Profile-specific**: Only runs when `dev` or `test` profile is active  
✅ **Simple SQL**: No Java code needed  
✅ **Repeatable**: Runs every application startup  
✅ **Same data structure**: Exact replica of your JSON data  

### Data Structure

#### Dev Profile Data
The dev migration inserts:
- **5 users** with names, emails, and usernames
- **50 posts** (10 per user) with UUIDs, titles, and bodies
- **Proper relationships** via foreign keys

#### Test Profile Data
The test migration inserts:
- **Test users** optimized for testing scenarios
- **Test posts** with predictable content
- **Consistent data** for reliable test execution

## Migration Best Practices

1. **Versioning**: Always increment version numbers sequentially
2. **Descriptive Names**: Use clear, descriptive names for migration files
3. **Idempotent**: Write migrations that can be run multiple times safely
4. **Rollback**: Consider creating rollback scripts for complex migrations
5. **Testing**: Test migrations in a development environment before production

## PostgreSQL-Specific Features

The project uses `flyway-database-postgresql` which provides:
- Native PostgreSQL support
- Better performance with PostgreSQL-specific optimizations
- Support for PostgreSQL-specific data types and features

## Migration History

Flyway maintains a `flyway_schema_history` table in your database that tracks:
- Applied migrations
- Version information
- Checksums for validation
- Execution timestamps

## Integration with Spring Boot

Flyway integrates seamlessly with Spring Boot:
- Automatic migration on application startup
- Configuration through `application.properties`
- Integration with Spring Boot's database auto-configuration
- Support for different profiles (dev, test, prod)

## Advantages of Repeatable Migrations

1. **Simplicity**: Pure SQL, no Java code
2. **Fresh Data**: Always starts with clean, consistent data
3. **Profile Isolation**: Only affects dev and test environments
4. **Flyway Integration**: Part of the migration system
5. **Team Consistency**: All developers get the same data
6. **Test Reliability**: Tests always start with known state

## Troubleshooting

### Common Issues

1. **Migration Validation Failed**: Check that migration files haven't been modified after being applied
2. **Version Conflicts**: Ensure migration versions are sequential and unique
3. **Database Connection**: Verify PostgreSQL connection settings in `application.properties`

### Migration Not Running
1. Ensure the correct profile is active (`dev` or `test`)
2. Check that Flyway is enabled
3. Verify the database connection
4. Confirm the profile-specific folder is in Flyway locations

### Data Issues
- The migration clears existing data first
- If you need to preserve data, modify the migration
- Check logs for any SQL errors

### Profile Issues
- Verify `spring.profiles.active=dev` or `spring.profiles.active=test` is set
- Check that the profile folder is in the Flyway locations
- Ensure tests are running with the correct profile

### Test-Specific Issues
- Ensure tests use the `test` profile
- Verify test data is consistent across test runs
- Check that test database is properly configured

### Useful Commands

```bash
# Check migration status
mvn flyway:info

# Validate migrations
mvn flyway:validate

# Clean database (DANGER: removes all data)
mvn flyway:clean

# Repair migration history
mvn flyway:repair
``` 