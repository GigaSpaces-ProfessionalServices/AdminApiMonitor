#!/bin/bash

# Wrapper script for GSC cpu monitoring.

LOGDIR='/belk/WilyMonitoring/logs/';
LOGDIR='/tmp/WilyMonitoring/logs/';

[ ! -d ${LOGDIR} ] && mkdir -p ${LOGDIR} ;

NOW=$( date +"%s" )
LOGNAME='cpu.'${NOW}'.log'
LOGFILE=${LOGDIR}${LOGNAME}

[ ! -e ${LOGFILE} ] && touch ${LOGFILE} ;

echo "Writing GSC cpu stats to ${LOGFILE} ."
./gsc_cpu_monitor.sh > ${LOGFILE} &
echo "Monitoring..."
