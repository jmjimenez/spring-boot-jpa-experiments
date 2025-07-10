# Flyway Database Migration Setup

This project uses Flyway for database migrations with PostgreSQL support.

## Configuration

### Dependencies
- `flyway-core`: Core Flyway functionality
- `flyway-database-postgresql`: PostgreSQL-specific support

### Properties Configuration
```properties
# Flyway Configuration
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
spring.flyway.validate-on-migrate=true

# Disable default schema initialization since we're using Flyway
spring.sql.init.mode=never
```

## Migration Files

Migrations are located in `src/main/resources/db/migration/` and follow the naming convention:
- `V{version}__{description}.sql`

### Current Migrations

1. **V1__Create_initial_schema.sql**: Creates the initial database schema
   - `Users` table with id, name, email, username
   - `Post` table with id, uuid, user_id, title, body
   - Foreign key relationship between Post and Users

2. **V2__Insert_sample_data.sql**: Inserts sample data
   - Sample users (John Doe, Jane Smith, Bob Johnson)
   - Sample posts with UUIDs and relationships

## Usage

### Running Migrations
Migrations run automatically when the application starts. You can also run them manually:

```bash
# Using Maven
mvn flyway:migrate

# Using Flyway CLI (if installed)
flyway migrate
```

### Creating New Migrations

1. Create a new SQL file in `src/main/resources/db/migration/`
2. Use the naming convention: `V{next_version}__{description}.sql`
3. Example: `V3__Add_user_roles.sql`

### Migration Best Practices

1. **Versioning**: Always increment version numbers sequentially
2. **Descriptive Names**: Use clear, descriptive names for migration files
3. **Idempotent**: Write migrations that can be run multiple times safely
4. **Rollback**: Consider creating rollback scripts for complex migrations
5. **Testing**: Test migrations in a development environment before production

### PostgreSQL-Specific Features

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

## Troubleshooting

### Common Issues

1. **Migration Validation Failed**: Check that migration files haven't been modified after being applied
2. **Version Conflicts**: Ensure migration versions are sequential and unique
3. **Database Connection**: Verify PostgreSQL connection settings in `application.properties`

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

## Integration with Spring Boot

Flyway integrates seamlessly with Spring Boot:
- Automatic migration on application startup
- Configuration through `application.properties`
- Integration with Spring Boot's database auto-configuration
- Support for different profiles (dev, test, prod) 