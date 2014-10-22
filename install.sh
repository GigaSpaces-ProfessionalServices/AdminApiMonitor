#!/bin/bash

DEFAULT_LOOKUPLOCATORS="localhost:4174"
DEFAULT_SPACENAME="mySpace"
DEFAULT_COLLECTIONINTERVAL=60000
DEFAULT_COLLECTIONDELAY=10000
DEFAULT_EMA_ALPHA=0.8
DEFAULT_LOGFILE="/tmp/GsMonitor.log"
DEFAULT_INSTALLDIR="/tmp/GsMonitor"

DEFAULT_OBLITERATE_EXISTING_INSTALL="y"

RUN_SCRIPT="run-monitor.sh"
CWD=$(pwd)

# check root

if [[ $EUID -eq 0 ]]; then
    echo "This script should not be run using sudo or as the root user."
    exit 1
fi

# Using read, ask for inputs to populate properties file.

read -p "Please enter space names (format: space1,space2,mirror-service) [${DEFAULT_SPACENAME}] : " spacename
if [ "${spacename}" == "" ]; then
    spacename="${DEFAULT_SPACENAME}"
fi
echo ${spacename}

read -p "Please enter lookup locators (format: 10.0.2.15:4174) [${DEFAULT_LOOKUPLOCATORS}] : " locators
if [ "${locators}" == "" ]; then
    locators="${DEFAULT_LOOKUPLOCATORS}"
fi
echo ${locators}

read -p "Is the space secured? (y/n) [n]: " RESP
if [ "$RESP" == "y" ]; then
  secured="true"
  read -p "Please enter admin's username : " username
  read -p "Please enter admin's password : " password
else
  secured="false"
fi
echo "${secured}"

read -p "Please enter (optional) lookup groups (format: someGroup) ['']: " lookupGroups
echo ${lookupGroups}

read -p "Please enter metric collection interval in milliseconds (format: 1000) [${DEFAULT_COLLECTIONINTERVAL}]: " interval
if [ "${interval}" == "" ]; then
    interval="${DEFAULT_COLLECTIONINTERVAL}"
fi
echo ${interval}

read -p "Please enter metric delay in milliseconds (format: 5000) [${DEFAULT_COLLECTIONDELAY}]: " delay
if [ "${delay}" == "" ]; then
    delay="${DEFAULT_COLLECTIONDELAY}"
fi
echo ${delay}

#    Using read, ask for inputs to update log file output path and logging type (choose from variants)
read -p "Please enter path to log file [${DEFAULT_LOGFILE}]: " logfile
if [ "${logfile}" == "" ]; then
    logfile="${DEFAULT_LOGFILE}"
fi
echo ${logfile}
if [ ! -f ${logfile} ]; then
    echo "mkdir -p dirname ${logfile}"
    mkdir -p dirname ${logfile}
fi

select result in Console Standard CSV Email
do
    echo ${result}
    if [ "${result}" == "Console" ]
    then
      LOGGING_CONFIG="logback-stdout.xml"
    elif [ "${result}" == "CSV" ]
    then
      LOGGING_CONFIG="logback-csv.xml"
      CSV="-c"
    elif [ "${result}" == "Standard" ]
    then
      LOGGING_CONFIG="logback-default.xml"
    elif [ "${result}" == "Email" ]
    then
      LOGGING_CONFIG="logback-email.xml"
    else
      echo "Please choose appropriate type of logging"
    fi
#    echo ${LOGGING_CONFIG}
    break
done

#    create a new source directory src/main/scripts and add the script there
#    dump the scripts directory output into the configuration directory
#    Using read, ask for installation directory

read -p "Please enter path to installation directory [${DEFAULT_INSTALLDIR}]: " installDir
if [ "${installDir}" == "" ]; then
  installDir="${DEFAULT_INSTALLDIR}"
fi
echo ${installDir}

# CONFIRM WE WANT TO PROCEED

echo "###################"
echo "## CONFIGURATION ##"
echo "###################"
echo ""
echo "space(s): ${spacename}"
echo "locator(s): ${locators}"
echo "lookup groups: '${lookupGroups}"
echo "secured: ${secured}"
echo "username: ${username}"
echo "password: *******"
echo ""
echo "collection interval (ms): ${interval}"
echo "collection delay: ${delay}"
echo ""
echo "logfile: ${logfile}"
echo "install directory: %{installDir}"
echo "output format: ${result}"
echo ""

echo ">>>>>>>>>>> <<<<<<<<<<<"

read -p "Proceed? (y/n)[n]" proceed
if [ "${proceed}" != "y" ]; then
    echo "goodbye"
    exit 666
fi

mvn clean install || echo "XXXXXXX > MAVEN BUILD FAILURE < XXXXXXXX"

if [ -e ${installDir} ]; then
    read -p "Directory already exists. Replace? (y/n) [${DEFAULT_OBLITERATE_EXISTING_INSTALL}]: " obliterate
fi
if [ "${obliterate}" == "" ]; then
    obliterate="${DEFAULT_OBLITERATE_EXISTING_INSTALL}"
fi
if [ "${obliterate}" == "y" ]; then
    rm -rf ${installDir}
fi

mkdir -p ${installDir}
#installDir=${installDir}
cp target/GsMonitor.jar ${installDir}
cp target/logback/${LOGGING_CONFIG} ${installDir}
unzip target/GsMonitor-ConfigurationFiles.zip -d ${installDir}/config
cd ${installDir}

echo "spaceMonitor.spaceName=${spacename}" > 'admin-api.properties'
echo "spaceMonitor.locators=${locators}" >> 'admin-api.properties'
echo "spaceMonitor.secured=${secured}" >> 'admin-api.properties'
echo "spaceMonitor.adminUser=${username}" >> 'admin-api.properties'
echo "spaceMonitor.adminPassword=${password}" >> 'admin-api.properties'
echo "spaceMonitor.lookupGroups=${lookupGroups}" >> 'admin-api.properties'
echo "stat.sample.alpha=${DEFAULT_EMA_ALPHA}" >> 'admin-api.properties'
echo "stat.periodic.sample.interval=${interval}" >> 'admin-api.properties'
echo "stat.periodic.sample.delay=${delay}" >> 'admin-api.properties'
echo "alerts.config=alerts-config.xml" >> 'admin-api.properties'
echo "alerts.email.reporting=false" >> 'admin-api.properties'

echo "sed -i "s/admin-api.log/${logfile}/g" ${LOGGING_CONFIG}"
sed -i '' "s#admin-api.log#${logfile}#g" ${LOGGING_CONFIG}

if [ -f ${RUN_SCRIPT} ]; then
    rm -f ${RUN_SCRIPT}
fi

SECURED_FLAG=""
if [ "${secured}" == "true" ]; then
    SECURED_FLAG = "-s"
fi

echo "java -Dproperties=${installDir}/admin-api.properties -Dlog.file=${logfile} -Dlogback.configurationFile=${installDir}/${LOGGING_CONFIG} -jar GsMonitor.jar ${SECURED_FLAG} ${CSV}" > ${RUN_SCRIPT}
chmod +x ${RUN_SCRIPT}
#./${RUN_SCRIPT}

echo "INSTALL SUCCEEDED."
echo "Run script created: [ ${installDir}/${RUN_SCRIPT} ]"

cd ${CWD} # in case of run script failure