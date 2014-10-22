@ECHO OFF

:: check root

::  Using read, ask for inputs to populate properties file.


SET /p SPACES="Please enter space names (format: space1,space2,mirror-service) : " %=%
ECHO %SPACES%

SET /p LOCATORS="Please enter locators, e.g. ("format: 10.0.2.15:4174,10.0.2.16:4174"): " %=%
ECHO %LOCATORS%

SET SECURED=false
SET /p ANSWER="Please enter is space secured (y/n) : " %=%
if /i {%ANSWER%}=={y} (
SET SECURED=true
SET /p NAME="Please enter admin's username : " %=%
ECHO %NAME%
SET /p PASSWORD="Please enter admin's password : " %=%
ECHO %PASSWORD%
)

SET /p LOOKUP_GROUPS="Please enter lookup groups (format: )(optional) : "
echo %LOOKUP_GROUPS%

SET /p INTERVAL="Please enter statistics collection interval in milliseconds (format: 10000) : " %=%
ECHO %INTERVAL%

SET /p DELAY="Please enter statistics collection delay in milliseconds (format: 10000) : " %=%
ECHO %DELAY%

SET /p CONST="Please enter EMA alpha (format: 0.8) : " %=%
ECHO %CONST%

::    Using read, ask for inputs to update log file output path and logging type (choose from variants)
SET /p LOGFILE="Please enter path to log file : " %=%
ECHO %LOGFILE%

:choice
ECHO 1) Console
ECHO 2) Standard
ECHO 3) CSV
ECHO 4) Email
SET /p LOGGING_TYPE="Please enter the number of logging type"
ECHO %LOGGING_TYPE%

IF %LOGGING_TYPE% EQU 1 (
SET LOGGING_CONFIG=logback-stdout.xml
SET CSV=
) ELSE IF %LOGGING_TYPE% EQU 2 (
SET LOGGING_CONFIG=logback-default.xml
SET CSV=
) ELSE IF %LOGGING_TYPE% EQU 3 (
SET LOGGING_CONFIG=logback-csv.xml
SET CSV=-c
) ELSE IF %LOGGING_TYPE% EQU 3 (
SET LOGGING_CONFIG=logback-email.xml
SET CSV=
) ELSE (
ECHO "Incorrect choice"
goto :choice
)

IF %LOGGING_TYPE% EQU 3 (
SET CSV=-c)

ECHO %LOGGING_CONFIG%
ECHO CSV=%CSV%

SET /p INSTALL_DIR="Please enter path to installation directory:" %=%
ECHO %INSTALL_DIR%

call mvn clean install

md %INSTALL_DIR%\GsMonitor
SET INSTALL_DIR=%INSTALL_DIR%\GsMonitor
xcopy target\GsMonitor.jar %INSTALL_DIR%
xcopy target\logback\%LOGGING_CONFIG% %INSTALL_DIR%
::unzip target/GsMonitor-ConfigurationFiles.zip -d $installDir/config
cd %INSTALL_DIR%

ECHO %CONST%

echo spaceMonitor.spaceName=%SPACES%>admin-api.properties
echo spaceMonitor.locators=%LOCATORS%>>admin-api.properties
echo spaceMonitor.secured=%SECURED%>>admin-api.properties
echo spaceMonitor.adminUser=%NAME%>>admin-api.properties
echo spaceMonitor.adminPassword=%PASSWORD%>>admin-api.properties
echo spaceMonitor.lookupGroups=%LOOKUP_GROUPS%>>admin-api.properties
echo stat.periodic.sample.interval=%INTERVAL%>>admin-api.properties
echo stat.periodic.sample.delay=%DELAY%>>admin-api.properties
echo stat.sample.alpha=%CONST% >>admin-api.properties
echo alerts.config=alerts-config.xml>>admin-api.properties
echo alerts.email.reporting=false>>admin-api.properties

::SED EQUIVALENT START
@echo off
setlocal enabledelayedexpansion
set INTEXTFILE=%LOGGING_CONFIG%
set OUTTEXTFILE=test_out.txt
set SEARCHTEXT=admin-api.log
set REPLACETEXT=%LOGFILE%
set OUTPUTLINE=

for /f "tokens=1,* delims=�" %%A in ( '"type %INTEXTFILE%"') do (
SET string=%%A
SET modified=!string:%SEARCHTEXT%=%REPLACETEXT%!

echo !modified! >> %OUTTEXTFILE%
)
del %INTEXTFILE%
rename %OUTTEXTFILE% %INTEXTFILE%
::SED EQUIVALENT END

::ECHO java -Dproperties=%INSTALL_DIR%\admin-api.properties -Dlog.file=%LOGFILE% -Dlogback.configurationFile=%INSTALL_DIR%\%LOGGING_CONFIG%  -jar GsMonitor.jar %CSV%
java -Dproperties=%INSTALL_DIR%\admin-api.properties -Dlog.file=%LOGFILE% -Dlogback.configurationFile=%INSTALL_DIR%\%LOGGING_CONFIG%  -jar GsMonitor.jar %CSV%
::chmod +x run.sh
::./run.sh



