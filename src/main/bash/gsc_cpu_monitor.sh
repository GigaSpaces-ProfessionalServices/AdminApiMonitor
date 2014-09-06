#!/bin/bash

PERIOD=30

#
# Prints a quadruple {timestamp, PID, PSR, CPU} every PERIOD seconds,
#
# where:
#   PID is the process id for a GSC (java) process,
#   PSR is the machine processor id to which pid is assinged,
#   CPU is the percentage of cpu on that processor at time that 'ps' was run
#

echo "CPU stats collected on $(hostname)"
echo "timestamp, PID, PSR, %CPU"
while true; do
   for i in `ps aux | grep GSC | grep -v grep| grep -v ' start '| awk '{print $2}'`; do cur_date=`date +"%m-%d-%Y-%T"`; PID=${i}; ps -p $i -L -o pid,psr,pcpu | grep -v PID | awk -vcur_date=${cur_date} -vPID=${PID} '{arr[$2]+=$3} END {for (i in arr) {print cur_date,PID,i,arr[i]}}';done
   sleep ${PERIOD}
done
