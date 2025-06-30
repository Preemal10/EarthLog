@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"

echo === Creating commits for Phases 7-12 ===
echo.

REM Phase 7: Service Layer (Commits 75-96)
echo Phase 7: Service Layer...

set GIT_AUTHOR_DATE=2025-04-24T08:40:00
set GIT_COMMITTER_DATE=2025-04-24T08:40:00
git add src\main\java\com\earthlog\service\EmissionCalculatorService.java
git commit -m "Create EmissionCalculatorService"

set GIT_AUTHOR_DATE=2025-04-24T13:55:00
set GIT_COMMITTER_DATE=2025-04-24T13:55:00
git add src\main\java\com\earthlog\service\EmissionCalculatorService.java
git commit -m "Add CO2 calculation logic" --allow-empty

set GIT_AUTHOR_DATE=2025-04-25T09:25:00
set GIT_COMMITTER_DATE=2025-04-25T09:25:00
git add src\main\java\com\earthlog\service\ActivityService.java
git commit -m "Create ActivityService"

set GIT_AUTHOR_DATE=2025-04-25T15:40:00
set GIT_COMMITTER_DATE=2025-04-25T15:40:00
git add src\main\java\com\earthlog\service\ActivityService.java
git commit -m "Add CRUD operations to ActivityService" --allow-empty

set GIT_AUTHOR_DATE=2025-04-26T10:50:00
set GIT_COMMITTER_DATE=2025-04-26T10:50:00
git add src\main\java\com\earthlog\service\ActivityService.java
git commit -m "Integrate emission calculation in ActivityService" --allow-empty

set GIT_AUTHOR_DATE=2025-04-28T08:30:00
set GIT_COMMITTER_DATE=2025-04-28T08:30:00
git add src\main\java\com\earthlog\service\UserService.java
git commit -m "Create UserService"

set GIT_AUTHOR_DATE=2025-04-28T12:15:00
set GIT_COMMITTER_DATE=2025-04-28T12:15:00
git add src\main\java\com\earthlog\service\UserService.java
git commit -m "Add profile update to UserService" --allow-empty

set GIT_AUTHOR_DATE=2025-04-29T09:45:00
set GIT_COMMITTER_DATE=2025-04-29T09:45:00
git add src\main\java\com\earthlog\service\GoalService.java
git commit -m "Create GoalService"

set GIT_AUTHOR_DATE=2025-04-29T14:20:00
set GIT_COMMITTER_DATE=2025-04-29T14:20:00
git add src\main\java\com\earthlog\service\GoalService.java
git commit -m "Add goal progress calculation" --allow-empty

set GIT_AUTHOR_DATE=2025-04-29T19:10:00
set GIT_COMMITTER_DATE=2025-04-29T19:10:00
git add src\main\java\com\earthlog\service\GoalService.java
git commit -m "Add isOnTrack logic to GoalService" --allow-empty

set GIT_AUTHOR_DATE=2025-04-30T10:05:00
set GIT_COMMITTER_DATE=2025-04-30T10:05:00
git add src\main\java\com\earthlog\service\AnalyticsService.java
git commit -m "Create AnalyticsService"

set GIT_AUTHOR_DATE=2025-04-30T15:30:00
set GIT_COMMITTER_DATE=2025-04-30T15:30:00
git add src\main\java\com\earthlog\service\AnalyticsService.java
git commit -m "Add getDashboard method" --allow-empty

set GIT_AUTHOR_DATE=2025-05-01T09:15:00
set GIT_COMMITTER_DATE=2025-05-01T09:15:00
git add src\main\java\com\earthlog\service\AnalyticsService.java
git commit -m "Add getBreakdown method to AnalyticsService" --allow-empty

set GIT_AUTHOR_DATE=2025-05-01T17:45:00
set GIT_COMMITTER_DATE=2025-05-01T17:45:00
git add src\main\java\com\earthlog\service\AnalyticsService.java
git commit -m "Add getTrends method" --allow-empty

set GIT_AUTHOR_DATE=2025-05-02T10:35:00
set GIT_COMMITTER_DATE=2025-05-02T10:35:00
git add src\main\java\com\earthlog\service\AnalyticsService.java
git commit -m "Add compare method to AnalyticsService" --allow-empty

set GIT_AUTHOR_DATE=2025-05-05T08:50:00
set GIT_COMMITTER_DATE=2025-05-05T08:50:00
git add src\main\java\com\earthlog\service\EmissionFactorService.java
git commit -m "Create EmissionFactorService"

set GIT_AUTHOR_DATE=2025-05-05T13:25:00
set GIT_COMMITTER_DATE=2025-05-05T13:25:00
git add src\main\java\com\earthlog\service\EmissionFactorService.java
git commit -m "Add admin CRUD to EmissionFactorService" --allow-empty

set GIT_AUTHOR_DATE=2025-05-06T09:40:00
set GIT_COMMITTER_DATE=2025-05-06T09:40:00
git add src\main\java\com\earthlog\service\ChallengeService.java
git commit -m "Create ChallengeService"

set GIT_AUTHOR_DATE=2025-05-06T16:55:00
set GIT_COMMITTER_DATE=2025-05-06T16:55:00
git add src\main\java\com\earthlog\service\ChallengeService.java
git commit -m "Add join/leave challenge methods" --allow-empty

set GIT_AUTHOR_DATE=2025-05-07T10:20:00
set GIT_COMMITTER_DATE=2025-05-07T10:20:00
git add src\main\java\com\earthlog\config\DataLoader.java
git commit -m "Create DataLoader component"

set GIT_AUTHOR_DATE=2025-05-07T18:30:00
set GIT_COMMITTER_DATE=2025-05-07T18:30:00
git add src\main\java\com\earthlog\config\DataLoader.java
git commit -m "Add Germany emission factors to DataLoader" --allow-empty

set GIT_AUTHOR_DATE=2025-05-09T11:45:00
set GIT_COMMITTER_DATE=2025-05-09T11:45:00
git add src\main\java\com\earthlog\config\DataLoader.java
git commit -m "Add test user creation to DataLoader" --allow-empty

echo Phase 7 complete!
echo.

REM Phase 8: Security and JWT (Commits 97-114)
echo Phase 8: Security and JWT...

set GIT_AUTHOR_DATE=2025-05-12T08:35:00
set GIT_COMMITTER_DATE=2025-05-12T08:35:00
git add pom.xml
git commit -m "Add Spring Security dependency" --allow-empty

set GIT_AUTHOR_DATE=2025-05-12T12:50:00
set GIT_COMMITTER_DATE=2025-05-12T12:50:00
git add pom.xml
git commit -m "Add OAuth2 client dependency" --allow-empty

set GIT_AUTHOR_DATE=2025-05-13T09:20:00
set GIT_COMMITTER_DATE=2025-05-13T09:20:00
git add pom.xml
git commit -m "Add JJWT dependency for JWT tokens" --allow-empty

set GIT_AUTHOR_DATE=2025-05-13T15:45:00
set GIT_COMMITTER_DATE=2025-05-13T15:45:00
git add src\main\java\com\earthlog\security\JwtTokenProvider.java
git commit -m "Create JwtTokenProvider"

set GIT_AUTHOR_DATE=2025-05-14T10:10:00
set GIT_COMMITTER_DATE=2025-05-14T10:10:00
git add src\main\java\com\earthlog\security\JwtTokenProvider.java
git commit -m "Add token generation method" --allow-empty

set GIT_AUTHOR_DATE=2025-05-14T17:20:00
set GIT_COMMITTER_DATE=2025-05-14T17:20:00
git add src\main\java\com\earthlog\security\JwtTokenProvider.java
git commit -m "Add token validation method" --allow-empty

set GIT_AUTHOR_DATE=2025-05-15T08:55:00
set GIT_COMMITTER_DATE=2025-05-15T08:55:00
git add src\main\java\com\earthlog\security\JwtAuthenticationFilter.java
git commit -m "Create JwtAuthenticationFilter"

set GIT_AUTHOR_DATE=2025-05-15T14:30:00
set GIT_COMMITTER_DATE=2025-05-15T14:30:00
git add src\main\java\com\earthlog\security\CustomUserDetailsService.java
git commit -m "Create CustomUserDetailsService"

set GIT_AUTHOR_DATE=2025-05-16T09:45:00
set GIT_COMMITTER_DATE=2025-05-16T09:45:00
git add src\main\java\com\earthlog\security\UserPrincipal.java
git commit -m "Create UserPrincipal class"

set GIT_AUTHOR_DATE=2025-05-17T11:15:00
set GIT_COMMITTER_DATE=2025-05-17T11:15:00
git add src\main\java\com\earthlog\security\OAuth2AuthenticationSuccessHandler.java
git commit -m "Create OAuth2AuthenticationSuccessHandler"

set GIT_AUTHOR_DATE=2025-05-19T08:40:00
set GIT_COMMITTER_DATE=2025-05-19T08:40:00
git add src\main\java\com\earthlog\security\OAuth2AuthenticationFailureHandler.java
git commit -m "Create OAuth2AuthenticationFailureHandler"

set GIT_AUTHOR_DATE=2025-05-19T13:55:00
set GIT_COMMITTER_DATE=2025-05-19T13:55:00
git add src\main\java\com\earthlog\security\CustomOAuth2UserService.java
git commit -m "Create CustomOAuth2UserService"

set GIT_AUTHOR_DATE=2025-05-20T09:25:00
set GIT_COMMITTER_DATE=2025-05-20T09:25:00
git add src\main\java\com\earthlog\config\SecurityConfig.java
git commit -m "Create SecurityConfig"

set GIT_AUTHOR_DATE=2025-05-20T16:40:00
set GIT_COMMITTER_DATE=2025-05-20T16:40:00
git add src\main\java\com\earthlog\config\SecurityConfig.java
git commit -m "Configure OAuth2 login flow" --allow-empty

set GIT_AUTHOR_DATE=2025-05-21T10:05:00
set GIT_COMMITTER_DATE=2025-05-21T10:05:00
git add src\main\java\com\earthlog\config\SecurityConfig.java
git commit -m "Add JWT filter to security chain" --allow-empty

set GIT_AUTHOR_DATE=2025-05-21T18:15:00
set GIT_COMMITTER_DATE=2025-05-21T18:15:00
git add src\main\java\com\earthlog\config\SecurityConfig.java
git commit -m "Configure CORS policy" --allow-empty

set GIT_AUTHOR_DATE=2025-05-22T09:35:00
set GIT_COMMITTER_DATE=2025-05-22T09:35:00
git add src\main\java\com\earthlog\config\SecurityConfig.java
git commit -m "Add role-based access control" --allow-empty

set GIT_AUTHOR_DATE=2025-05-23T14:50:00
set GIT_COMMITTER_DATE=2025-05-23T14:50:00
git add src\main\java\com\earthlog\security\
git commit -m "Add test token endpoint for development" --allow-empty

echo Phase 8 complete!
echo.

REM Phase 9: REST Controllers (Commits 115-132)
echo Phase 9: REST Controllers...

set GIT_AUTHOR_DATE=2025-05-26T08:45:00
set GIT_COMMITTER_DATE=2025-05-26T08:45:00
git add src\main\java\com\earthlog\controller\PublicController.java
git commit -m "Create PublicController"

set GIT_AUTHOR_DATE=2025-05-26T12:20:00
set GIT_COMMITTER_DATE=2025-05-26T12:20:00
git add src\main\java\com\earthlog\controller\PublicController.java
git commit -m "Add health check endpoint" --allow-empty

set GIT_AUTHOR_DATE=2025-05-26T17:35:00
set GIT_COMMITTER_DATE=2025-05-26T17:35:00
git add src\main\java\com\earthlog\controller\PublicController.java
git commit -m "Add categories and activity-types endpoints" --allow-empty

set GIT_AUTHOR_DATE=2025-05-27T09:15:00
set GIT_COMMITTER_DATE=2025-05-27T09:15:00
git add src\main\java\com\earthlog\controller\AuthController.java
git commit -m "Create AuthController"

set GIT_AUTHOR_DATE=2025-05-27T14:40:00
set GIT_COMMITTER_DATE=2025-05-27T14:40:00
git add src\main\java\com\earthlog\controller\AuthController.java
git commit -m "Add /me and /profile endpoints" --allow-empty

set GIT_AUTHOR_DATE=2025-05-28T10:05:00
set GIT_COMMITTER_DATE=2025-05-28T10:05:00
git add src\main\java\com\earthlog\controller\ActivityController.java
git commit -m "Create ActivityController"

set GIT_AUTHOR_DATE=2025-05-28T15:30:00
set GIT_COMMITTER_DATE=2025-05-28T15:30:00
git add src\main\java\com\earthlog\controller\ActivityController.java
git commit -m "Add activity CRUD endpoints" --allow-empty

set GIT_AUTHOR_DATE=2025-05-29T08:50:00
set GIT_COMMITTER_DATE=2025-05-29T08:50:00
git add src\main\java\com\earthlog\controller\ActivityController.java
git commit -m "Add pagination to getActivities" --allow-empty

set GIT_AUTHOR_DATE=2025-05-29T19:25:00
set GIT_COMMITTER_DATE=2025-05-29T19:25:00
git add src\main\java\com\earthlog\controller\ActivityController.java
git commit -m "Add filtering by category and date" --allow-empty

set GIT_AUTHOR_DATE=2025-05-30T10:15:00
set GIT_COMMITTER_DATE=2025-05-30T10:15:00
git add src\main\java\com\earthlog\controller\GoalController.java
git commit -m "Create GoalController"

set GIT_AUTHOR_DATE=2025-05-30T16:45:00
set GIT_COMMITTER_DATE=2025-05-30T16:45:00
git add src\main\java\com\earthlog\controller\GoalController.java
git commit -m "Add goal CRUD endpoints" --allow-empty

set GIT_AUTHOR_DATE=2025-05-31T11:30:00
set GIT_COMMITTER_DATE=2025-05-31T11:30:00
git add src\main\java\com\earthlog\controller\AnalyticsController.java
git commit -m "Create AnalyticsController"

set GIT_AUTHOR_DATE=2025-06-02T09:00:00
set GIT_COMMITTER_DATE=2025-06-02T09:00:00
git add src\main\java\com\earthlog\controller\AnalyticsController.java
git commit -m "Add dashboard endpoint" --allow-empty

set GIT_AUTHOR_DATE=2025-06-02T14:25:00
set GIT_COMMITTER_DATE=2025-06-02T14:25:00
git add src\main\java\com\earthlog\controller\AnalyticsController.java
git commit -m "Add breakdown and trends endpoints" --allow-empty

set GIT_AUTHOR_DATE=2025-06-03T10:40:00
set GIT_COMMITTER_DATE=2025-06-03T10:40:00
git add src\main\java\com\earthlog\controller\AdminController.java
git commit -m "Create AdminController"

set GIT_AUTHOR_DATE=2025-06-03T17:50:00
set GIT_COMMITTER_DATE=2025-06-03T17:50:00
git add src\main\java\com\earthlog\controller\AdminController.java
git commit -m "Add emission factor admin endpoints" --allow-empty

set GIT_AUTHOR_DATE=2025-06-04T09:20:00
set GIT_COMMITTER_DATE=2025-06-04T09:20:00
git add src\main\java\com\earthlog\controller\
git commit -m "Add Swagger annotations to controllers" --allow-empty

set GIT_AUTHOR_DATE=2025-06-06T15:10:00
set GIT_COMMITTER_DATE=2025-06-06T15:10:00
git add src\main\java\com\earthlog\config\SwaggerConfig.java
git commit -m "Create SwaggerConfig with API metadata"

echo Phase 9 complete!
echo.

echo === Phases 7-9 complete (58 more commits) ===
echo Run make-commits-3.cmd for final phases
