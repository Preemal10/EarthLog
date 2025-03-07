# EarthLog

A Personal Carbon Footprint Tracker API built with Spring Boot.

EarthLog helps users track their daily activities and automatically calculates their carbon emissions. Users can set reduction goals, view analytics, and participate in challenges to reduce their environmental impact. The application features an event-driven architecture using RabbitMQ for asynchronous processing and email notifications.

## Features

### Core Features
- **Activity Logging** - Log daily activities (transport, food, energy, shopping, waste)
- **Automatic Emission Calculation** - System calculates CO2 based on Germany-specific emission factors
- **Goals & Progress Tracking** - Set weekly/monthly/yearly reduction targets with real-time progress monitoring
- **Analytics Dashboard** - View breakdowns, trends, and comparisons
- **User Authentication** - OAuth2 login with Google + JWT token authentication

### Event-Driven Architecture
- **RabbitMQ Integration** - Asynchronous message processing for scalability
- **Activity Events** - Automatic event publishing when activities are logged
- **Goal Progress Monitoring** - Real-time goal progress evaluation
- **Badge Evaluation** - Automatic badge awarding based on achievements
- **Dead Letter Queue** - Failed message handling and retry mechanism

### Notification System
- **Email Notifications** - Beautiful HTML email templates powered by Thymeleaf
- **Welcome Emails** - Sent when new users register
- **Goal Alerts** - Notifications when approaching or exceeding goals
- **Badge Notifications** - Celebrate user achievements
- **Weekly Summaries** - Regular progress updates

### Gamification Features
- **Badges** - Earn badges for achievements (First Step, Week Warrior, etc.)
- **Challenges** - Join and complete community challenges
- **Streaks** - Track consecutive days of activity logging

## Tech Stack

| Component | Technology |
|-----------|------------|
| Framework | Spring Boot 3.2.3 |
| Language | Java 17 |
| Database | PostgreSQL 15 |
| Message Queue | RabbitMQ 3.12 |
| ORM | JPA / Hibernate |
| Security | OAuth2 (Google) + JWT |
| Build Tool | Maven (with Wrapper) |
| API Docs | Swagger / OpenAPI 3.0 |
| Email | Spring Mail + Thymeleaf |
| Containerization | Docker / Docker Compose |
| Testing | JUnit 5, Mockito, Testcontainers |

## Prerequisites

Before running this application, ensure you have:

- **Java 17** or higher installed
- **Docker Desktop** installed and running
- **Google OAuth2 credentials** (Client ID & Secret) - optional for development

## Project Structure

```
EarthLog/
├── src/
│   ├── main/
│   │   ├── java/com/earthlog/
│   │   │   ├── config/          # Security, Swagger, RabbitMQ configs
│   │   │   ├── controller/      # REST API endpoints
│   │   │   ├── service/         # Business logic
│   │   │   ├── repository/      # Data access layer
│   │   │   ├── entity/          # JPA entities
│   │   │   ├── dto/             # Request/Response objects
│   │   │   ├── enums/           # Enumerations
│   │   │   ├── event/           # Event DTOs for RabbitMQ
│   │   │   ├── messaging/       # Event publishers & listeners
│   │   │   ├── exception/       # Custom exceptions
│   │   │   ├── mapper/          # Entity-DTO mappers
│   │   │   └── security/        # JWT & OAuth2 handlers
│   │   └── resources/
│   │       ├── application.yml      # Main configuration
│   │       ├── application-dev.yml  # Development profile
│   │       └── templates/email/     # HTML email templates
│   └── test/                        # Unit & integration tests
├── docker-compose.yml               # PostgreSQL + RabbitMQ containers
├── pom.xml                          # Maven dependencies
└── README.md                        # This file
```

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd EarthLog
```

### 2. Setup Environment Variables

Create a `.env` file in the project root (or set system environment variables):

```env
# Database
DB_USERNAME=earthlog
DB_PASSWORD=earthlog123

# Google OAuth2 (optional for development)
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret

# JWT
JWT_SECRET=your-super-secret-jwt-key-min-256-bits-long

# RabbitMQ
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=earthlog
RABBITMQ_PASSWORD=earthlog123

# Email (optional - disabled by default)
MAIL_ENABLED=false
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

### 3. Start PostgreSQL and RabbitMQ with Docker

```bash
docker-compose up -d
```

This will start:
- **PostgreSQL** on port `5432`
  - Database: `earthlog`
  - Username: `earthlog`
  - Password: `earthlog123`
- **RabbitMQ** on ports `5672` (AMQP) and `15672` (Management UI)
  - Username: `earthlog`
  - Password: `earthlog123`
  - Virtual Host: `earthlog`

### 4. Run the Application

```bash
# Using Maven Wrapper (Windows)
.\mvnw.cmd spring-boot:run

# Using Maven Wrapper (Linux/Mac)
./mvnw spring-boot:run

# Or using the full command
java -Dmaven.multiModuleProjectDirectory=. -classpath ".mvn\wrapper\maven-wrapper.jar" org.apache.maven.wrapper.MavenWrapperMain spring-boot:run
```

The application will start on `http://localhost:8080`

### 5. Access the Application

| URL | Description |
|-----|-------------|
| http://localhost:8080/swagger-ui.html | Swagger UI - API Documentation |
| http://localhost:8080/api-docs | OpenAPI JSON Specification |
| http://localhost:8080/api/public/health | Health Check Endpoint |
| http://localhost:15672 | RabbitMQ Management UI |

## API Endpoints

### Public Endpoints (No Authentication Required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/public/health` | Health check |
| GET | `/api/public/categories` | List activity categories |
| GET | `/api/public/activity-types` | List activity types |
| GET | `/api/public/test-token/{userId}` | Generate test JWT (DEV only) |

### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/oauth2/authorization/google` | Initiate Google OAuth2 login |
| GET | `/api/auth/me` | Get current user profile |
| PUT | `/api/auth/profile` | Update user profile |

### Activities

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/activities` | Log a new activity |
| GET | `/api/activities` | Get user's activities (paginated) |
| GET | `/api/activities/{id}` | Get single activity |
| PUT | `/api/activities/{id}` | Update an activity |
| DELETE | `/api/activities/{id}` | Delete an activity |

### Goals

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/goals` | Create a new goal |
| GET | `/api/goals` | Get user's goals with progress |
| GET | `/api/goals/{id}` | Get goal details |
| PUT | `/api/goals/{id}` | Update a goal |
| DELETE | `/api/goals/{id}` | Delete a goal |

### Analytics

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/analytics/dashboard` | Get dashboard summary |
| GET | `/api/analytics/breakdown` | Category-wise breakdown |
| GET | `/api/analytics/trends` | Emission trends over time |
| GET | `/api/analytics/compare` | Compare with platform average |

### Admin (Requires ADMIN role)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/emission-factors` | List all emission factors |
| POST | `/api/admin/emission-factors` | Create new factor |
| PUT | `/api/admin/emission-factors/{id}` | Update a factor |
| DELETE | `/api/admin/emission-factors/{id}` | Delete a factor |

## Activity Categories

| Category | Activity Types |
|----------|---------------|
| **TRANSPORT** | CAR_PETROL, CAR_DIESEL, CAR_ELECTRIC, BUS, TRAIN_REGIONAL, TRAIN_ICE, FLIGHT_SHORT, FLIGHT_LONG, BIKE, WALK |
| **FOOD** | BEEF_MEAL, LAMB_MEAL, PORK_MEAL, CHICKEN_MEAL, FISH_MEAL, VEGETARIAN_MEAL, VEGAN_MEAL |
| **ENERGY** | ELECTRICITY, NATURAL_GAS, HEATING_OIL |
| **SHOPPING** | CLOTHING_NEW, CLOTHING_SECONDHAND, ELECTRONICS, FURNITURE |
| **WASTE** | LANDFILL, RECYCLED, COMPOSTED |

## RabbitMQ Architecture

### Exchanges

| Exchange | Type | Description |
|----------|------|-------------|
| `earthlog.events` | Topic | Main event exchange for routing messages |
| `earthlog.dlx` | Direct | Dead Letter Exchange for failed messages |

### Queues

| Queue | Routing Key | Description |
|-------|-------------|-------------|
| `activity.created` | `activity.created` | New activity events |
| `goal.progress` | `goal.progress` | Goal progress updates |
| `badge.evaluation` | `badge.evaluation` | Badge evaluation requests |
| `notification.email` | `notification.email` | Email notification requests |
| `earthlog.dlq` | `earthlog.dlq` | Dead letter queue |

### Event Flow

```
Activity Created
      │
      ▼
┌─────────────────┐
│ EventPublisher  │
└────────┬────────┘
         │
         ▼
┌─────────────────────────┐
│   earthlog.events       │
│   (Topic Exchange)      │
└────────┬────────────────┘
         │
    ┌────┼────┬────────────┐
    ▼    ▼    ▼            ▼
┌──────┐┌──────┐┌──────┐┌──────┐
│Activity││Goal  ││Badge ││Email │
│Listener││Listen││Listen││Listen│
└──────┘└──────┘└──────┘└──────┘
```

## Email Templates

The application includes 8 beautiful HTML email templates:

| Template | Trigger |
|----------|---------|
| `welcome.html` | New user registration |
| `activity-logged.html` | Activity logged (optional) |
| `goal-approaching.html` | Goal 80% reached |
| `goal-exceeded.html` | Goal exceeded |
| `badge-earned.html` | Badge awarded |
| `weekly-summary.html` | Weekly digest |
| `challenge-joined.html` | Joined a challenge |
| `challenge-completed.html` | Challenge completed |

To enable email notifications:
1. Set `MAIL_ENABLED=true`
2. Configure SMTP settings (Gmail, SendGrid, etc.)

## Example API Usage

### Generate Test Token (Development)

```bash
curl http://localhost:8080/api/public/test-token/1
```

### Log an Activity

```bash
curl -X POST http://localhost:8080/api/activities \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -d '{
    "category": "TRANSPORT",
    "activityType": "CAR_PETROL",
    "quantity": 50,
    "unit": "KM",
    "date": "2026-03-08",
    "notes": "Commute to work and back"
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "Activity logged successfully",
  "data": {
    "id": 1,
    "category": "TRANSPORT",
    "activityType": "CAR_PETROL",
    "quantity": 50,
    "unit": "KM",
    "calculatedCo2": 9.5,
    "date": "2026-03-08",
    "notes": "Commute to work and back",
    "createdAt": "2026-03-08T11:35:42.310"
  }
}
```

### Create a Goal

```bash
curl -X POST http://localhost:8080/api/goals \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -d '{
    "targetEmission": 100,
    "period": "MONTHLY",
    "startDate": "2026-03-01",
    "endDate": "2026-03-31"
  }'
```

### Get Dashboard

```bash
curl http://localhost:8080/api/analytics/dashboard \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response:**
```json
{
  "success": true,
  "data": {
    "today": { "totalCo2": 37.7, "activitiesCount": 4 },
    "thisWeek": { "totalCo2": 37.7, "activitiesCount": 4 },
    "thisMonth": { "totalCo2": 37.7, "activitiesCount": 4 },
    "topCategory": "TRANSPORT",
    "streak": 0,
    "activeGoals": 2
  }
}
```

## Emission Factors

The application comes pre-seeded with emission factors for Germany. Sources include:
- UK DEFRA Conversion Factors 2023
- German Federal Environment Agency (UBA)
- Carbon Footprint Ltd

### Sample Factors (Germany)

| Activity | Factor | Unit |
|----------|--------|------|
| Electricity | 0.380 | kg CO2/kWh |
| Car (Petrol) | 0.19 | kg CO2/km |
| Car (Diesel) | 0.17 | kg CO2/km |
| Car (Electric) | 0.05 | kg CO2/km |
| Train (ICE) | 0.029 | kg CO2/km |
| Train (Regional) | 0.041 | kg CO2/km |
| Bus | 0.089 | kg CO2/km |
| Flight (Short) | 0.255 | kg CO2/km |
| Flight (Long) | 0.195 | kg CO2/km |
| Beef Meal | 6.5 | kg CO2/meal |
| Vegan Meal | 0.3 | kg CO2/meal |

## Testing

### Run All Tests

```bash
# Windows
.\mvnw.cmd test

# Linux/Mac
./mvnw test
```

### Test Results

The application includes 66 tests covering:
- Unit tests for services (ActivityService, GoalService, BadgeService, etc.)
- Integration tests for RabbitMQ messaging
- Controller tests for REST endpoints

```
Tests run: 66, Failures: 0, Errors: 0, Skipped: 0
```

### Run with Test Profile

```bash
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=test
```

## Docker Commands

```bash
# Start PostgreSQL and RabbitMQ
docker-compose up -d

# Stop containers
docker-compose down

# View logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f rabbitmq
docker-compose logs -f postgres

# Reset database (removes all data)
docker-compose down -v
docker-compose up -d

# Check container status
docker-compose ps
```

## Setting up Google OAuth2

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Navigate to **APIs & Services** > **Credentials**
4. Click **Create Credentials** > **OAuth client ID**
5. Select **Web application**
6. Add authorized redirect URI: `http://localhost:8080/login/oauth2/code/google`
7. Copy the **Client ID** and **Client Secret**
8. Add them to your environment variables

## Troubleshooting

### Application won't start

1. **Check Docker is running**: `docker ps`
2. **Check ports are available**: 5432 (PostgreSQL), 5672 & 15672 (RabbitMQ), 8080 (App)
3. **Reset containers**: `docker-compose down -v && docker-compose up -d`

### RabbitMQ connection issues

1. **Check RabbitMQ is running**: http://localhost:15672 (login: earthlog/earthlog123)
2. **Verify virtual host exists**: Check Admin > Virtual Hosts in RabbitMQ UI
3. **Check application logs** for connection errors

### Database connection issues

1. **Check PostgreSQL is running**: `docker-compose ps`
2. **Verify credentials** in application.yml match docker-compose.yml
3. **Check database exists**: Connect with `psql -h localhost -U earthlog -d earthlog`

## Configuration Reference

### application.yml

Key configuration options:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/earthlog
  rabbitmq:
    host: localhost
    port: 5672
    virtual-host: earthlog
  mail:
    host: smtp.gmail.com
    port: 587

app:
  jwt:
    secret: ${JWT_SECRET}
    expiration: 86400000  # 24 hours
  mail:
    enabled: false  # Set to true to enable emails
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License.

## Acknowledgments

- Emission factors sourced from UK DEFRA and German UBA
- Built with Spring Boot, PostgreSQL, and RabbitMQ
- Email templates styled with responsive HTML/CSS
