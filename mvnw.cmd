@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.2.0
@REM ----------------------------------------------------------------------------

@ECHO OFF
SETLOCAL

SET MAVEN_PROJECTBASEDIR=%~dp0
SET WRAPPER_JAR=%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.jar

@REM Find java.exe
IF NOT "%JAVA_HOME%"=="" (
    SET "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
) ELSE (
    SET "JAVA_EXE=java"
)

@REM Download maven-wrapper.jar if it doesn't exist
IF NOT EXIST "%WRAPPER_JAR%" (
    ECHO Downloading Maven Wrapper...
    powershell -Command "(New-Object Net.WebClient).DownloadFile('https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar', '%WRAPPER_JAR%')"
)

@REM Execute Maven
"%JAVA_EXE%" ^
    %MAVEN_OPTS% ^
    -classpath "%WRAPPER_JAR%" ^
    "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" ^
    org.apache.maven.wrapper.MavenWrapperMain %*

ENDLOCAL
