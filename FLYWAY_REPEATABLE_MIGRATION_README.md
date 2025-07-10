# Flyway Repeatable Migration for Dev Profile

This project uses Flyway repeatable migrations to automatically insert sample data when running with the `dev` profile.

## Implementation Details

### File Structure
```
src/main/resources/db/migration/
├── V1__Create_initial_schema.sql          # Versioned migration (runs once)
└── dev/
    └── R__Insert_dev_data.sql             # Repeatable migration (runs every time)
```

### Migration Types

1. **Versioned Migrations** (`V1__`, `V2__`, etc.)
   - Run only once
   - Tracked in Flyway's schema history
   - Used for schema changes

2. **Repeatable Migrations** (`R__`)
   - Run every time the application starts
   - Not tracked in schema history
   - Perfect for data seeding in development

## How It Works

### R__Insert_dev_data.sql
- **Location**: `src/main/resources/db/migration/dev/R__Insert_dev_data.sql`
- **Behavior**: Executes every time the application starts with the `dev` profile
- **Data**: Inserts the same data as the original `UserAndPostDataLoader`

### Key Features
✅ **Always fresh data**: Clears existing data and re-inserts  
✅ **Profile-specific**: Only runs when `dev` profile is active  
✅ **Simple SQL**: No Java code needed  
✅ **Repeatable**: Runs every application startup  
✅ **Same data structure**: Exact replica of your JSON data  

## Configuration

### Main Application Properties
```properties
# Flyway Configuration
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration,classpath:db/migration/dev
spring.flyway.baseline-on-migrate=true
spring.flyway.validate-on-migrate=true
```

### Dev Profile Properties
```properties
# Dev Profile Configuration
spring.profiles.active=dev
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration,classpath:db/migration/dev
```

## Usage

### Running with Dev Profile
```bash
# Set the dev profile
export DEFAULT_PROFILE=dev

# Or run with explicit profile
java -jar your-app.jar --spring.profiles.active=dev

# Or using Maven
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### What Happens
1. **First Run**: Creates users and posts
2. **Subsequent Runs**: 
   - Deletes all existing data
   - Re-inserts fresh data
   - Ensures consistent state

### Production
When running without the `dev` profile (e.g., `prod` profile), the repeatable migration won't execute.

## Advantages

1. **Simplicity**: Pure SQL, no Java code
2. **Fresh Data**: Always starts with clean, consistent data
3. **Profile Isolation**: Only affects dev environment
4. **Flyway Integration**: Part of the migration system
5. **Team Consistency**: All developers get the same data

## Data Structure

The migration inserts:
- **5 users** with names, emails, and usernames
- **50 posts** (10 per user) with UUIDs, titles, and bodies
- **Proper relationships** via foreign keys

## Migration Behavior

- **Dev Profile**: Migration executes every startup
- **Other Profiles**: Migration is ignored
- **Data Freshness**: Always starts with clean data
- **No Conflicts**: No duplicate key issues

## Troubleshooting

### Migration Not Running
1. Ensure the `dev` profile is active
2. Check that Flyway is enabled
3. Verify the database connection

### Data Issues
- The migration clears existing data first
- If you need to preserve data, modify the migration
- Check logs for any SQL errors

### Profile Issues
- Verify `spring.profiles.active=dev` is set
- Check that the dev folder is in the Flyway locations 