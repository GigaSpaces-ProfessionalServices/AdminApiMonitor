#!/bin/bash
/belk/jdk1.7.0_21/bin/java -Dproperties=/belk/admin_api.properties -Dlogback.configurationFile=/belk/belk-log-logback.xml -jar /belk/AdminApiMonitor.jar -s -c &
