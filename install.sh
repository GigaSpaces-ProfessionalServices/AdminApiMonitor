#!/bin/bash

# check root

if [[ $EUID -ne 0 ]]; then
    echo "This script should not be run using sudo or as the root user"
    exit 1
fi


#    Using read, ask for inputs to populate properties file.


read -p "Please enter space names (format: space1,space2,mirror-service) : " spacename
echo $spacename

read -p "Please enter locators (format: 10.0.2.15:4174,10.0.2.16:4174) : " locators
echo $locators

read -p "Please enter is space secured (y/n) : " RESP
if [ "$RESP" = "y" ]; then
  secured="true"
  echo "Secured space" 
  read -p "Please enter admin's username : " username  
  read -p "Please enter admin's password : " password
else
  secured=false
  echo "space is not secured"
fi

read -p "Please enter lookup groups (format: )(optional) : " lookupGroups
echo $lookupGroups

read -p "Please enter statistics collection insterval in milliseconds (format: 10000) : " interval
echo $interval

read -p "Please enter statistics collection delay in milliseconds (format: 3000) : " delay
echo $delay

read -p "Please enter EMA alpha (format: 0.8) : " alpha
echo $alpha


#    Using read, ask for inputs to update log file output path and logging type (choose from variants)
read -p "Please enter path to log file : " logfile
echo $logfile

select result in Console Standard CSV Email
do
    echo $result
    if [ "$result" == "Console" ]
    then
      LOGGING_CONFIG="logback-stdout.xml"
    elif [ "$result" == "CSV" ]
    then
      LOGGING_CONFIG="logback-csv.xml"
      CSV="-c"
    elif [ "$result" == "Standard" ]
    then
      LOGGING_CONFIG="logback-default.xml"
    elif [ "$result" == "Email" ]
    then
      LOGGING_CONFIG="logback-email.xml"     
    else
      echo "Please choose appropriate type of logging"
    fi
    echo $LOGGING_CONFIG
    break
done

#    create a new source directory src/main/scripts and add the script there
#    dump the scripts directory output into the configuration directory
#    Using read, ask for installation directory

read -p "Please enter path to installation directory: " installDir
echo $installDir

mvn clean install

mkdir $installDir/AdminApiMonitor
installDir=$installDir/AdminApiMonitor
cp target/AdminApiMonitor.jar $installDir
cp target/logback/$LOGGING_CONFIG $installDir 
unzip target/AdminApiMonitor-ConfigurationFiles.zip -d $installDir/config
cd $installDir
echo "spaceMonitor.spaceName=$spacename" > 'admin-api.properties'
echo "spaceMonitor.locators=$locators" >> 'admin-api.properties'
echo "spaceMonitor.secured=$secured" >> 'admin-api.properties'
echo "spaceMonitor.adminUser=$username" >> 'admin-api.properties'
echo "spaceMonitor.adminPassword=$password" >> 'admin-api.properties'
echo "spaceMonitor.lookupGroups=$lookupGroups" >> 'admin-api.properties'
echo "stat.sample.alpha=$alpha" >> 'admin-api.properties'
echo "stat.periodic.sample.interval=$interval" >> 'admin-api.properties'
echo "stat.periodic.sample.delay=$delay" >> 'admin-api.properties'
echo "alerts.config=alerts-config.xml" >> 'admin-api.properties'
echo "alerts.email.reporting=false" >> 'admin-api.properties'

echo "sudo sed -i "s/admin-api.log/$logfile/g" $LOGGING_CONFIG"
sudo sed -i "s#admin-api.log#$logfile#g" $LOGGING_CONFIG

echo "java -Dproperties=$installDir/admin-api.properties -Dlog.file=$logfile -Dlogback.configurationFile=$installDir/$LOGGING_CONFIG  -jar AdminApiMonitor.jar $CSV" > run.sh
#java -Dproperties=$installDir/admin-api.properties -Dlog.file=$logfile -Dlogback.configurationFile=$installDir/$LOGGING_CONFIG  -jar AdminApiMonitor.jar $CSV
chmod +x run.sh
./run.sh



