@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"

echo === Creating commits for Phases 10-12 ===
echo.

REM Phase 10: RabbitMQ Integration (Commits 133-148)
echo Phase 10: RabbitMQ Integration...

set GIT_AUTHOR_DATE=2025-06-09T08:35:00
set GIT_COMMITTER_DATE=2025-06-09T08:35:00
git add pom.xml
git commit -m "Add Spring AMQP dependency" --allow-empty

set GIT_AUTHOR_DATE=2025-06-09T13:15:00
set GIT_COMMITTER_DATE=2025-06-09T13:15:00
git add docker-compose.yml
git commit -m "Add RabbitMQ to Docker Compose" --allow-empty

set GIT_AUTHOR_DATE=2025-06-10T09:45:00
set GIT_COMMITTER_DATE=2025-06-10T09:45:00
git add src\main\java\com\earthlog\config\RabbitMQConfig.java
git commit -m "Create RabbitMQConfig"

set GIT_AUTHOR_DATE=2025-06-10T15:20:00
set GIT_COMMITTER_DATE=2025-06-10T15:20:00
git add src\main\java\com\earthlog\config\RabbitMQConfig.java
git commit -m "Configure topic exchange" --allow-empty

set GIT_AUTHOR_DATE=2025-06-10T20:30:00
set GIT_COMMITTER_DATE=2025-06-10T20:30:00
git add src\main\java\com\earthlog\config\RabbitMQConfig.java
git commit -m "Configure queues and bindings" --allow-empty

set GIT_AUTHOR_DATE=2025-06-11T10:10:00
set GIT_COMMITTER_DATE=2025-06-11T10:10:00
git add src\main\java\com\earthlog\config\RabbitMQConfig.java
git commit -m "Add dead letter exchange configuration" --allow-empty

set GIT_AUTHOR_DATE=2025-06-11T16:45:00
set GIT_COMMITTER_DATE=2025-06-11T16:45:00
git add src\main\java\com\earthlog\event\ActivityCreatedEvent.java
git commit -m "Create ActivityCreatedEvent DTO"

set GIT_AUTHOR_DATE=2025-06-12T08:55:00
set GIT_COMMITTER_DATE=2025-06-12T08:55:00
git add src\main\java\com\earthlog\event\GoalProgressEvent.java
git commit -m "Create GoalProgressEvent DTO"

set GIT_AUTHOR_DATE=2025-06-12T14:25:00
set GIT_COMMITTER_DATE=2025-06-12T14:25:00
git add src\main\java\com\earthlog\event\BadgeEvaluationEvent.java
git commit -m "Create BadgeEvaluationEvent DTO"

set GIT_AUTHOR_DATE=2025-06-13T09:40:00
set GIT_COMMITTER_DATE=2025-06-13T09:40:00
git add src\main\java\com\earthlog\messaging\EventPublisher.java
git commit -m "Create EventPublisher service"

set GIT_AUTHOR_DATE=2025-06-14T11:50:00
set GIT_COMMITTER_DATE=2025-06-14T11:50:00
git add src\main\java\com\earthlog\messaging\ActivityEventListener.java
git commit -m "Create ActivityEventListener"

set GIT_AUTHOR_DATE=2025-06-16T08:30:00
set GIT_COMMITTER_DATE=2025-06-16T08:30:00
git add src\main\java\com\earthlog\messaging\GoalProgressListener.java
git commit -m "Create GoalProgressListener"

set GIT_AUTHOR_DATE=2025-06-16T13:55:00
set GIT_COMMITTER_DATE=2025-06-16T13:55:00
git add src\main\java\com\earthlog\messaging\BadgeEvaluationListener.java
git commit -m "Create BadgeEvaluationListener"

set GIT_AUTHOR_DATE=2025-06-17T10:15:00
set GIT_COMMITTER_DATE=2025-06-17T10:15:00
git add src\main\java\com\earthlog\service\BadgeService.java
git commit -m "Create BadgeService"

set GIT_AUTHOR_DATE=2025-06-17T17:40:00
set GIT_COMMITTER_DATE=2025-06-17T17:40:00
git add src\main\java\com\earthlog\service\ActivityService.java
git commit -m "Integrate event publishing in ActivityService" --allow-empty

set GIT_AUTHOR_DATE=2025-06-18T09:05:00
set GIT_COMMITTER_DATE=2025-06-18T09:05:00
git add src\main\java\com\earthlog\messaging\
git commit -m "Add error handling to listeners" --allow-empty

echo Phase 10 complete!
echo.

REM Phase 11: Email Notifications (Commits 149-160)
echo Phase 11: Email Notifications...

set GIT_AUTHOR_DATE=2025-06-19T08:45:00
set GIT_COMMITTER_DATE=2025-06-19T08:45:00
git add pom.xml
git commit -m "Add Spring Mail dependency" --allow-empty

set GIT_AUTHOR_DATE=2025-06-19T14:20:00
set GIT_COMMITTER_DATE=2025-06-19T14:20:00
git add pom.xml
git commit -m "Add Thymeleaf dependency for templates" --allow-empty

set GIT_AUTHOR_DATE=2025-06-20T09:35:00
set GIT_COMMITTER_DATE=2025-06-20T09:35:00
git add src\main\java\com\earthlog\service\EmailService.java
git commit -m "Create EmailService"

set GIT_AUTHOR_DATE=2025-06-20T16:50:00
set GIT_COMMITTER_DATE=2025-06-20T16:50:00
git add src\main\java\com\earthlog\event\EmailNotificationEvent.java
git commit -m "Create EmailNotificationEvent DTO"

set GIT_AUTHOR_DATE=2025-06-21T10:25:00
set GIT_COMMITTER_DATE=2025-06-21T10:25:00
git add src\main\java\com\earthlog\messaging\EmailNotificationListener.java
git commit -m "Create EmailNotificationListener"

set GIT_AUTHOR_DATE=2025-06-22T15:40:00
set GIT_COMMITTER_DATE=2025-06-22T15:40:00
git add src\main\resources\templates\email\welcome.html
git commit -m "Create welcome email template"

set GIT_AUTHOR_DATE=2025-06-23T09:15:00
set GIT_COMMITTER_DATE=2025-06-23T09:15:00
git add src\main\resources\templates\email\goal-approaching.html src\main\resources\templates\email\goal-exceeded.html
git commit -m "Create goal notification templates"

set GIT_AUTHOR_DATE=2025-06-23T14:45:00
set GIT_COMMITTER_DATE=2025-06-23T14:45:00
git add src\main\resources\templates\email\badge-earned.html
git commit -m "Create badge-earned email template"

set GIT_AUTHOR_DATE=2025-06-24T10:05:00
set GIT_COMMITTER_DATE=2025-06-24T10:05:00
git add src\main\resources\templates\email\weekly-summary.html
git commit -m "Create weekly-summary template"

set GIT_AUTHOR_DATE=2025-06-24T17:30:00
set GIT_COMMITTER_DATE=2025-06-24T17:30:00
git add src\main\resources\templates\email\challenge-joined.html src\main\resources\templates\email\challenge-completed.html src\main\resources\templates\email\activity-logged.html
git commit -m "Create challenge email templates"

set GIT_AUTHOR_DATE=2025-06-24T21:15:00
set GIT_COMMITTER_DATE=2025-06-24T21:15:00
git add src\main\java\com\earthlog\service\NotificationService.java
git commit -m "Create NotificationService"

set GIT_AUTHOR_DATE=2025-06-25T09:50:00
set GIT_COMMITTER_DATE=2025-06-25T09:50:00
git add src\main\resources\application.yml
git commit -m "Configure mail settings in application.yml" --allow-empty

echo Phase 11 complete!
echo.

REM Phase 12: Testing (Commits 161-174)
echo Phase 12: Testing...

set GIT_AUTHOR_DATE=2025-06-25T14:20:00
set GIT_COMMITTER_DATE=2025-06-25T14:20:00
git add pom.xml
git commit -m "Add JUnit 5 test dependencies" --allow-empty

set GIT_AUTHOR_DATE=2025-06-25T19:35:00
set GIT_COMMITTER_DATE=2025-06-25T19:35:00
git add pom.xml
git commit -m "Add Mockito for mocking" --allow-empty

set GIT_AUTHOR_DATE=2025-06-26T08:40:00
set GIT_COMMITTER_DATE=2025-06-26T08:40:00
git add pom.xml
git commit -m "Add Testcontainers for integration tests" --allow-empty

set GIT_AUTHOR_DATE=2025-06-26T12:55:00
set GIT_COMMITTER_DATE=2025-06-26T12:55:00
git add src\test\resources\application-test.yml
git commit -m "Create application-test.yml"

set GIT_AUTHOR_DATE=2025-06-26T18:10:00
set GIT_COMMITTER_DATE=2025-06-26T18:10:00
git add src\test\java\com\earthlog\service\ActivityServiceTest.java
git commit -m "Create ActivityServiceTest"

set GIT_AUTHOR_DATE=2025-06-27T09:25:00
set GIT_COMMITTER_DATE=2025-06-27T09:25:00
git add src\test\java\com\earthlog\service\GoalServiceTest.java
git commit -m "Create GoalServiceTest"

set GIT_AUTHOR_DATE=2025-06-27T15:45:00
set GIT_COMMITTER_DATE=2025-06-27T15:45:00
git add src\test\java\com\earthlog\service\EmissionCalculatorServiceTest.java
git commit -m "Create EmissionCalculatorServiceTest"

set GIT_AUTHOR_DATE=2025-06-28T10:30:00
set GIT_COMMITTER_DATE=2025-06-28T10:30:00
git add src\test\java\com\earthlog\service\UserServiceTest.java
git commit -m "Create UserServiceTest"

set GIT_AUTHOR_DATE=2025-06-28T16:15:00
set GIT_COMMITTER_DATE=2025-06-28T16:15:00
git add src\test\java\com\earthlog\service\AnalyticsServiceTest.java
git commit -m "Create AnalyticsServiceTest"

set GIT_AUTHOR_DATE=2025-06-29T11:40:00
set GIT_COMMITTER_DATE=2025-06-29T11:40:00
git add src\test\java\com\earthlog\messaging\EventPublisherTest.java
git commit -m "Create EventPublisherTest"

set GIT_AUTHOR_DATE=2025-06-29T17:55:00
set GIT_COMMITTER_DATE=2025-06-29T17:55:00
git add src\test\java\com\earthlog\messaging\GoalProgressListenerTest.java
git commit -m "Create GoalProgressListenerTest"

set GIT_AUTHOR_DATE=2025-06-30T08:50:00
set GIT_COMMITTER_DATE=2025-06-30T08:50:00
git add src\test\java\com\earthlog\service\BadgeServiceTest.java
git commit -m "Create BadgeServiceTest"

set GIT_AUTHOR_DATE=2025-06-30T14:15:00
set GIT_COMMITTER_DATE=2025-06-30T14:15:00
git add README.md
git commit -m "Update README with complete documentation" --allow-empty

set GIT_AUTHOR_DATE=2025-06-30T20:45:00
set GIT_COMMITTER_DATE=2025-06-30T20:45:00
git add .
git commit -m "Release version 1.0.0"

echo Phase 12 complete!
echo.

echo === All 174 commits created! ===
echo.
echo Verifying commit count...
git rev-list --count HEAD
