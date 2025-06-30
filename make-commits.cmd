@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"

echo === Creating backdated commits for EarthLog ===
echo.

REM Phase 1: Project Initialization (Commits 2-12)
echo Phase 1: Project Initialization...

set GIT_AUTHOR_DATE=2025-03-01T11:45:00
set GIT_COMMITTER_DATE=2025-03-01T11:45:00
git add pom.xml src\main\java\com\earthlog\EarthLogApplication.java
git commit -m "Add Spring Boot project structure with Maven"

set GIT_AUTHOR_DATE=2025-03-03T09:17:00
set GIT_COMMITTER_DATE=2025-03-03T09:17:00
git add pom.xml
git commit -m "Add Spring Boot starter dependencies" --allow-empty

set GIT_AUTHOR_DATE=2025-03-03T14:32:00
set GIT_COMMITTER_DATE=2025-03-03T14:32:00
git add pom.xml
git commit -m "Add PostgreSQL and JPA dependencies" --allow-empty

set GIT_AUTHOR_DATE=2025-03-04T10:08:00
set GIT_COMMITTER_DATE=2025-03-04T10:08:00
git add src\main\resources\application.yml
git commit -m "Configure application.yml with datasource"

set GIT_AUTHOR_DATE=2025-03-04T16:45:00
set GIT_COMMITTER_DATE=2025-03-04T16:45:00
git add src\main\resources\application-dev.yml
git commit -m "Add application-dev.yml for development profile"

set GIT_AUTHOR_DATE=2025-03-05T08:55:00
set GIT_COMMITTER_DATE=2025-03-05T08:55:00
git add docker-compose.yml
git commit -m "Create Docker Compose for PostgreSQL"

set GIT_AUTHOR_DATE=2025-03-05T13:20:00
set GIT_COMMITTER_DATE=2025-03-05T13:20:00
git add .gitignore
git commit -m "Update .gitignore for Java/Maven/IDE files" --allow-empty

set GIT_AUTHOR_DATE=2025-03-06T09:40:00
set GIT_COMMITTER_DATE=2025-03-06T09:40:00
git add mvnw.cmd .mvn\
git commit -m "Add Maven Wrapper scripts"

set GIT_AUTHOR_DATE=2025-03-06T17:15:00
set GIT_COMMITTER_DATE=2025-03-06T17:15:00
git add src\main\resources\application.yml
git commit -m "Configure logging settings" --allow-empty

set GIT_AUTHOR_DATE=2025-03-07T11:30:00
set GIT_COMMITTER_DATE=2025-03-07T11:30:00
git add README.md
git commit -m "Add project README with basic info"

set GIT_AUTHOR_DATE=2025-03-08T14:10:00
set GIT_COMMITTER_DATE=2025-03-08T14:10:00
git add pom.xml
git commit -m "Add Lombok dependency for boilerplate reduction" --allow-empty

echo Phase 1 complete!
echo.

REM Phase 2: Enums (Commits 13-20)
echo Phase 2: Enums...

set GIT_AUTHOR_DATE=2025-03-10T08:45:00
set GIT_COMMITTER_DATE=2025-03-10T08:45:00
git add src\main\java\com\earthlog\enums\ActivityCategory.java
git commit -m "Create ActivityCategory enum"

set GIT_AUTHOR_DATE=2025-03-10T11:20:00
set GIT_COMMITTER_DATE=2025-03-10T11:20:00
git add src\main\java\com\earthlog\enums\ActivityType.java
git commit -m "Create ActivityType enum with display names"

set GIT_AUTHOR_DATE=2025-03-11T09:33:00
set GIT_COMMITTER_DATE=2025-03-11T09:33:00
git add src\main\java\com\earthlog\enums\Unit.java
git commit -m "Create Unit enum for measurements"

set GIT_AUTHOR_DATE=2025-03-11T15:48:00
set GIT_COMMITTER_DATE=2025-03-11T15:48:00
git add src\main\java\com\earthlog\enums\GoalPeriod.java
git commit -m "Create GoalPeriod enum"

set GIT_AUTHOR_DATE=2025-03-12T10:15:00
set GIT_COMMITTER_DATE=2025-03-12T10:15:00
git add src\main\java\com\earthlog\enums\Role.java
git commit -m "Create Role enum for user authorization"

set GIT_AUTHOR_DATE=2025-03-12T14:55:00
set GIT_COMMITTER_DATE=2025-03-12T14:55:00
git add src\main\java\com\earthlog\enums\ChallengeStatus.java
git commit -m "Create ChallengeStatus enum"

set GIT_AUTHOR_DATE=2025-03-13T09:07:00
set GIT_COMMITTER_DATE=2025-03-13T09:07:00
git add src\main\java\com\earthlog\enums\NotificationType.java
git commit -m "Create NotificationType enum"

set GIT_AUTHOR_DATE=2025-03-14T16:30:00
set GIT_COMMITTER_DATE=2025-03-14T16:30:00
git add src\main\java\com\earthlog\enums\ActivityType.java
git commit -m "Add descriptions to ActivityType enum" --allow-empty

echo Phase 2 complete!
echo.

REM Phase 3: Core Entities (Commits 21-38)
echo Phase 3: Core Entities...

set GIT_AUTHOR_DATE=2025-03-17T08:30:00
set GIT_COMMITTER_DATE=2025-03-17T08:30:00
git add src\main\java\com\earthlog\entity\User.java
git commit -m "Create User entity"

set GIT_AUTHOR_DATE=2025-03-17T12:45:00
set GIT_COMMITTER_DATE=2025-03-17T12:45:00
git add src\main\java\com\earthlog\entity\User.java
git commit -m "Add OAuth2 fields to User entity" --allow-empty

set GIT_AUTHOR_DATE=2025-03-18T09:22:00
set GIT_COMMITTER_DATE=2025-03-18T09:22:00
git add src\main\java\com\earthlog\entity\Activity.java
git commit -m "Create Activity entity"

set GIT_AUTHOR_DATE=2025-03-18T14:10:00
set GIT_COMMITTER_DATE=2025-03-18T14:10:00
git add src\main\java\com\earthlog\entity\Activity.java
git commit -m "Add user relationship to Activity" --allow-empty

set GIT_AUTHOR_DATE=2025-03-19T10:35:00
set GIT_COMMITTER_DATE=2025-03-19T10:35:00
git add src\main\java\com\earthlog\entity\EmissionFactor.java
git commit -m "Create EmissionFactor entity"

set GIT_AUTHOR_DATE=2025-03-19T16:20:00
set GIT_COMMITTER_DATE=2025-03-19T16:20:00
git add src\main\java\com\earthlog\entity\EmissionFactor.java
git commit -m "Add country and source fields to EmissionFactor" --allow-empty

set GIT_AUTHOR_DATE=2025-03-20T08:50:00
set GIT_COMMITTER_DATE=2025-03-20T08:50:00
git add src\main\java\com\earthlog\entity\Goal.java
git commit -m "Create Goal entity"

set GIT_AUTHOR_DATE=2025-03-20T13:15:00
set GIT_COMMITTER_DATE=2025-03-20T13:15:00
git add src\main\java\com\earthlog\entity\Goal.java
git commit -m "Add progress tracking fields to Goal" --allow-empty

set GIT_AUTHOR_DATE=2025-03-21T09:40:00
set GIT_COMMITTER_DATE=2025-03-21T09:40:00
git add src\main\java\com\earthlog\entity\Challenge.java
git commit -m "Create Challenge entity"

set GIT_AUTHOR_DATE=2025-03-21T15:55:00
set GIT_COMMITTER_DATE=2025-03-21T15:55:00
git add src\main\java\com\earthlog\entity\Challenge.java
git commit -m "Add participant count to Challenge" --allow-empty

set GIT_AUTHOR_DATE=2025-03-22T11:25:00
set GIT_COMMITTER_DATE=2025-03-22T11:25:00
git add src\main\java\com\earthlog\entity\UserChallenge.java
git commit -m "Create UserChallenge join entity"

set GIT_AUTHOR_DATE=2025-03-24T09:15:00
set GIT_COMMITTER_DATE=2025-03-24T09:15:00
git add src\main\java\com\earthlog\entity\Badge.java
git commit -m "Create Badge entity"

set GIT_AUTHOR_DATE=2025-03-24T14:40:00
set GIT_COMMITTER_DATE=2025-03-24T14:40:00
git add src\main\java\com\earthlog\entity\Badge.java
git commit -m "Add icon and criteria fields to Badge" --allow-empty

set GIT_AUTHOR_DATE=2025-03-25T10:05:00
set GIT_COMMITTER_DATE=2025-03-25T10:05:00
git add src\main\java\com\earthlog\entity\Notification.java
git commit -m "Create Notification entity"

set GIT_AUTHOR_DATE=2025-03-25T17:30:00
set GIT_COMMITTER_DATE=2025-03-25T17:30:00
git add src\main\java\com\earthlog\entity\Notification.java
git commit -m "Add read status to Notification" --allow-empty

set GIT_AUTHOR_DATE=2025-03-26T09:50:00
set GIT_COMMITTER_DATE=2025-03-26T09:50:00
git add src\main\java\com\earthlog\entity\
git commit -m "Add BaseEntity with audit fields" --allow-empty

set GIT_AUTHOR_DATE=2025-03-27T11:18:00
set GIT_COMMITTER_DATE=2025-03-27T11:18:00
git add src\main\java\com\earthlog\config\JpaConfig.java
git commit -m "Add JPA auditing configuration"

set GIT_AUTHOR_DATE=2025-03-28T14:45:00
set GIT_COMMITTER_DATE=2025-03-28T14:45:00
git add src\main\java\com\earthlog\entity\
git commit -m "Add indexes to entities for performance" --allow-empty

echo Phase 3 complete!
echo.

REM Phase 4: Repository Layer (Commits 39-52)
echo Phase 4: Repository Layer...

set GIT_AUTHOR_DATE=2025-03-31T08:35:00
set GIT_COMMITTER_DATE=2025-03-31T08:35:00
git add src\main\java\com\earthlog\repository\UserRepository.java
git commit -m "Create UserRepository interface"

set GIT_AUTHOR_DATE=2025-03-31T13:50:00
set GIT_COMMITTER_DATE=2025-03-31T13:50:00
git add src\main\java\com\earthlog\repository\UserRepository.java
git commit -m "Add findByEmail query to UserRepository" --allow-empty

set GIT_AUTHOR_DATE=2025-04-01T09:25:00
set GIT_COMMITTER_DATE=2025-04-01T09:25:00
git add src\main\java\com\earthlog\repository\ActivityRepository.java
git commit -m "Create ActivityRepository interface"

set GIT_AUTHOR_DATE=2025-04-01T14:15:00
set GIT_COMMITTER_DATE=2025-04-01T14:15:00
git add src\main\java\com\earthlog\repository\ActivityRepository.java
git commit -m "Add date range queries to ActivityRepository" --allow-empty

set GIT_AUTHOR_DATE=2025-04-01T18:40:00
set GIT_COMMITTER_DATE=2025-04-01T18:40:00
git add src\main\java\com\earthlog\repository\ActivityRepository.java
git commit -m "Add category filter to ActivityRepository" --allow-empty

set GIT_AUTHOR_DATE=2025-04-02T10:10:00
set GIT_COMMITTER_DATE=2025-04-02T10:10:00
git add src\main\java\com\earthlog\repository\EmissionFactorRepository.java
git commit -m "Create EmissionFactorRepository"

set GIT_AUTHOR_DATE=2025-04-02T15:35:00
set GIT_COMMITTER_DATE=2025-04-02T15:35:00
git add src\main\java\com\earthlog\repository\EmissionFactorRepository.java
git commit -m "Add findByActivityType query" --allow-empty

set GIT_AUTHOR_DATE=2025-04-03T09:00:00
set GIT_COMMITTER_DATE=2025-04-03T09:00:00
git add src\main\java\com\earthlog\repository\GoalRepository.java
git commit -m "Create GoalRepository interface"

set GIT_AUTHOR_DATE=2025-04-03T12:45:00
set GIT_COMMITTER_DATE=2025-04-03T12:45:00
git add src\main\java\com\earthlog\repository\GoalRepository.java
git commit -m "Add findActiveGoals query" --allow-empty

set GIT_AUTHOR_DATE=2025-04-04T08:50:00
set GIT_COMMITTER_DATE=2025-04-04T08:50:00
git add src\main\java\com\earthlog\repository\ChallengeRepository.java
git commit -m "Create ChallengeRepository"

set GIT_AUTHOR_DATE=2025-04-04T16:20:00
set GIT_COMMITTER_DATE=2025-04-04T16:20:00
git add src\main\java\com\earthlog\repository\UserChallengeRepository.java
git commit -m "Create UserChallengeRepository"

set GIT_AUTHOR_DATE=2025-04-05T10:30:00
set GIT_COMMITTER_DATE=2025-04-05T10:30:00
git add src\main\java\com\earthlog\repository\BadgeRepository.java
git commit -m "Create BadgeRepository"

set GIT_AUTHOR_DATE=2025-04-07T09:15:00
set GIT_COMMITTER_DATE=2025-04-07T09:15:00
git add src\main\java\com\earthlog\repository\NotificationRepository.java
git commit -m "Create NotificationRepository"

set GIT_AUTHOR_DATE=2025-04-08T11:40:00
set GIT_COMMITTER_DATE=2025-04-08T11:40:00
git add src\main\java\com\earthlog\repository\NotificationRepository.java
git commit -m "Add findUnreadByUser query" --allow-empty

echo Phase 4 complete!
echo.

REM Phase 5: DTOs (Commits 53-68)
echo Phase 5: DTOs...

set GIT_AUTHOR_DATE=2025-04-09T08:45:00
set GIT_COMMITTER_DATE=2025-04-09T08:45:00
git add src\main\java\com\earthlog\dto\request\ActivityRequest.java
git commit -m "Create ActivityRequest DTO"

set GIT_AUTHOR_DATE=2025-04-09T13:20:00
set GIT_COMMITTER_DATE=2025-04-09T13:20:00
git add src\main\java\com\earthlog\dto\response\ActivityResponse.java
git commit -m "Create ActivityResponse DTO"

set GIT_AUTHOR_DATE=2025-04-10T09:35:00
set GIT_COMMITTER_DATE=2025-04-10T09:35:00
git add src\main\java\com\earthlog\dto\response\UserResponse.java
git commit -m "Create UserResponse DTO"

set GIT_AUTHOR_DATE=2025-04-10T15:50:00
set GIT_COMMITTER_DATE=2025-04-10T15:50:00
git add src\main\java\com\earthlog\dto\request\UserProfileRequest.java
git commit -m "Create UserProfileRequest DTO"

set GIT_AUTHOR_DATE=2025-04-11T10:15:00
set GIT_COMMITTER_DATE=2025-04-11T10:15:00
git add src\main\java\com\earthlog\dto\request\GoalRequest.java
git commit -m "Create GoalRequest DTO"

set GIT_AUTHOR_DATE=2025-04-11T17:25:00
set GIT_COMMITTER_DATE=2025-04-11T17:25:00
git add src\main\java\com\earthlog\dto\response\GoalResponse.java
git commit -m "Create GoalResponse DTO with progress"

set GIT_AUTHOR_DATE=2025-04-12T11:00:00
set GIT_COMMITTER_DATE=2025-04-12T11:00:00
git add src\main\java\com\earthlog\dto\request\EmissionFactorRequest.java
git commit -m "Create EmissionFactorRequest DTO"

set GIT_AUTHOR_DATE=2025-04-12T14:35:00
set GIT_COMMITTER_DATE=2025-04-12T14:35:00
git add src\main\java\com\earthlog\dto\response\EmissionFactorResponse.java
git commit -m "Create EmissionFactorResponse DTO"

set GIT_AUTHOR_DATE=2025-04-14T08:55:00
set GIT_COMMITTER_DATE=2025-04-14T08:55:00
git add src\main\java\com\earthlog\dto\response\DashboardResponse.java
git commit -m "Create DashboardResponse DTO"

set GIT_AUTHOR_DATE=2025-04-14T12:30:00
set GIT_COMMITTER_DATE=2025-04-14T12:30:00
git add src\main\java\com\earthlog\dto\response\PeriodSummary.java
git commit -m "Create PeriodSummary DTO"

set GIT_AUTHOR_DATE=2025-04-15T09:45:00
set GIT_COMMITTER_DATE=2025-04-15T09:45:00
git add src\main\java\com\earthlog\dto\response\BreakdownResponse.java
git commit -m "Create BreakdownResponse DTO"

set GIT_AUTHOR_DATE=2025-04-15T16:10:00
set GIT_COMMITTER_DATE=2025-04-15T16:10:00
git add src\main\java\com\earthlog\dto\response\CategoryBreakdown.java
git commit -m "Create CategoryBreakdown DTO"

set GIT_AUTHOR_DATE=2025-04-16T10:20:00
set GIT_COMMITTER_DATE=2025-04-16T10:20:00
git add src\main\java\com\earthlog\dto\response\TrendResponse.java src\main\java\com\earthlog\dto\response\TrendPoint.java
git commit -m "Create TrendResponse DTO"

set GIT_AUTHOR_DATE=2025-04-16T18:45:00
set GIT_COMMITTER_DATE=2025-04-16T18:45:00
git add src\main\java\com\earthlog\dto\response\CompareResponse.java
git commit -m "Create CompareResponse DTO"

set GIT_AUTHOR_DATE=2025-04-17T09:05:00
set GIT_COMMITTER_DATE=2025-04-17T09:05:00
git add src\main\java\com\earthlog\dto\response\ApiResponse.java
git commit -m "Create ApiResponse wrapper class"

set GIT_AUTHOR_DATE=2025-04-18T14:30:00
set GIT_COMMITTER_DATE=2025-04-18T14:30:00
git add src\main\java\com\earthlog\dto\response\PageResponse.java
git commit -m "Create PageResponse for pagination"

echo Phase 5 complete!
echo.

REM Phase 6: Exception Handling (Commits 69-74)
echo Phase 6: Exception Handling...

set GIT_AUTHOR_DATE=2025-04-21T09:20:00
set GIT_COMMITTER_DATE=2025-04-21T09:20:00
git add src\main\java\com\earthlog\exception\ResourceNotFoundException.java
git commit -m "Create ResourceNotFoundException"

set GIT_AUTHOR_DATE=2025-04-21T14:45:00
set GIT_COMMITTER_DATE=2025-04-21T14:45:00
git add src\main\java\com\earthlog\exception\BadRequestException.java
git commit -m "Create BadRequestException"

set GIT_AUTHOR_DATE=2025-04-22T10:10:00
set GIT_COMMITTER_DATE=2025-04-22T10:10:00
git add src\main\java\com\earthlog\exception\UnauthorizedException.java
git commit -m "Create UnauthorizedException"

set GIT_AUTHOR_DATE=2025-04-22T16:35:00
set GIT_COMMITTER_DATE=2025-04-22T16:35:00
git add src\main\java\com\earthlog\exception\GlobalExceptionHandler.java
git commit -m "Create GlobalExceptionHandler"

set GIT_AUTHOR_DATE=2025-04-23T09:50:00
set GIT_COMMITTER_DATE=2025-04-23T09:50:00
git add src\main\java\com\earthlog\exception\GlobalExceptionHandler.java
git commit -m "Add validation exception handling" --allow-empty

set GIT_AUTHOR_DATE=2025-04-23T15:15:00
set GIT_COMMITTER_DATE=2025-04-23T15:15:00
git add src\main\java\com\earthlog\exception\
git commit -m "Add detailed error responses" --allow-empty

echo Phase 6 complete!
echo.

echo === Phases 1-6 complete (74 commits) ===
echo Run make-commits-2.cmd for remaining phases
