#!/bin/sh
PID1=`ps -ef | grep supervisord | grep -v grep | awk '{print $2}'`
if [[ "" != "$PID1" ]]; then
    echo "killing $PID1"
    kill -9 $PID1
fi

PID2=`ps -ef | grep warm-0.0.1-SNAPSHOT.jar | grep -v grep | awk '{print $2}'`
if [[ "" != "$PID2" ]]; then
    echo "killing $PID2"
    kill -9 $PID2
fi