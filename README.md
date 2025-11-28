# MarketApp

Market application to buy items.

# Application launch

## 1. Prepare DataBase 
1. Create new PostgreSQL database.
2. Execute DDL for schema creation (schema.sql).
3. (Optional) Insert necessary offering items into *offerings* table.

## 2. Configure environment variables

1. Create .env file in project root directory
2. Specify environment variables for your DB connection:

```
DB_URL={your DB url} (example: r2dbc:postgresql://localhost:5432/market_db)
DB_USERNAME={DB username}
DB_PASSWORD={DB password}
IMAGE_DIRECTORY={path to directory with offering images}
```

## 3. Start application

There are 3 launch options: from IDE, local launch, docker launch.

1. Start from IDE
- Execute MarketAppApplication class with configured environment variables (.env file)

2. Start locally
- Build project into executable .jar file
```
./mvnw clean install
```
- Launch compiled .jar file:
``` 
java -jar market-app-1.0.0.jar
```

3. Start as a Docker container
- Launch Docker locally
- Execute ```docker compose up --build``` to start Docker container