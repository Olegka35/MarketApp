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

1. Start from IDE (use active profile dev)
- Execute MarketAppApplication class with configured environment variables (.env file)
- Execute PaymentServiceApplication class with configured environment variables (.env file)

2. Start locally
- Build modules into .jar files. Execute from root directory (on the parent module):
```
./mvnw clean install
```
- Launch compiled .jar files:
``` 
java -jar market-app-2.1.0.jar
java -jar payment-service-1.0.0.jar --spring.profiles.active=dev (OR --server.port=8081)
```

3. Start as a Docker containers
- Launch Docker locally
- Execute ```docker compose up --build``` to start Docker containers.
- No need to specify ports / profiles manually when using Docker compose. Ports will be set automatically.