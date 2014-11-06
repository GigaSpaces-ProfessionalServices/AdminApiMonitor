#!/bin/bash
/belk/jdk1.7.0_21/bin/java -Dproperties=/belk/admin_api.properties -Dlogback.configurationFile=/belk/belk-csv-logback.xml -jar /belk/gs-monitor.jar -s -c &
