#!/bin/bash

## Adjust JAVA_HOME if necessary
## 测试环境
#JAVA_HOME=/home/installationpackage/jdk1.8.0_261
## 生产环境
JAVA_HOME=/usr/java/jdk1.8.0_331-amd64
cd `dirname $0`/..
SERVICE_NAME=virtual-account-service
PIDFILE=$SERVICE_NAME".pid"
PATH_TO_JAR=$SERVICE_NAME".jar"
PID=

## Adjust memory settings if necessary
#测试 jvm
#JAVA_OPTS="-Xmx320M -Xms320M -Xmn64M -XX:MaxMetaspaceSize=64M -XX:MetaspaceSize=64M -XX:+UseConcMarkSweepGC -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses -XX:+CMSClassUnloadingEnabled -XX:+ParallelRefProcEnabled -XX:+CMSScavengeBeforeRemark"
#生产 jvm
JAVA_OPTS="-Xmx9216M -Xms9216M -Xmn5120M -XX:MaxMetaspaceSize=512M -XX:MetaspaceSize=512M -XX:+UseConcMarkSweepGC -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses -XX:+CMSClassUnloadingEnabled -XX:+ParallelRefProcEnabled -XX:+CMSScavengeBeforeRemark"

status() {
    PID=`ps -ef|grep $SERVICE_NAME|grep -vE '(grep|kill)'|awk '{print $2}'`
	if [[ ${PID} ]]; then
		return 0
	else
        return 1
    fi
}

for i in `ls $SERVICE_NAME-*.jar 2>/dev/null`
do
    if [[ ! $i == *"-sources.jar" ]]
    then
        PATH_TO_JAR=$i
        break
    fi
done

if [[ ! -f PATH_TO_JAR && -d current ]]; then
    cd current
    for i in `ls $SERVICE_NAME-*.jar 2>/dev/null`
    do
        if [[ ! $i == *"-sources.jar" ]]
        then
            PATH_TO_JAR=$i
            break
        fi
    done
fi

if [[ -f $SERVICE_NAME".jar" ]]; then
  rm -rf $SERVICE_NAME".jar"
fi

ln $PATH_TO_JAR $SERVICE_NAME".jar"
chmod a+x $SERVICE_NAME".jar"

case "$1" in
    start)
    	status
    	RETVAL=$?
		if [ $RETVAL -eq 0 ]; then
			echo "$SERVICE_NAME($PID) exists, process is already running or crashed"
			exit 1
		fi

        echo "$(date) starting $SERVICE_NAME ..."
        ## with coverage open ##
        $JAVA_HOME/bin/java -server $JAVA_OPTS -jar $SERVICE_NAME".jar" >/dev/null &
		RETVAL=$?
		if [ $RETVAL -eq 0 ]; then
			echo "$SERVICE_NAME is started"
			echo $! > $PIDFILE
		else
			echo "Stopping $SERVICE_NAME ..."
			rm -f $PIDFILE
		fi
        ;;
    stop)
    	status
    	RETVAL=$?
		if [ $RETVAL -eq 0 ]; then
			echo "Shutting down $SERVICE_NAME ..."
			kill -9 $PID
			RETVAL=$?
			if [ $RETVAL -eq 0 ]; then
				rm -f $PIDFILE
			else
				echo "Failed to stopping $SERVICE_NAME"
			fi
		fi
		f=true
		while ${f}
        do
            status
	        RETVAL=$?
            if [ $RETVAL -eq 0 ]; then
                sleep 5
            else
                echo $SERVICE_NAME "is stopped"
		        f=false

            fi
        done
        ;;
    status)
	status
    	RETVAL=$?
		if [ $RETVAL -eq 0 ]; then
			echo "$SERVICE_NAME($PID) is running"
		else
			echo "$SERVICE_NAME is not running"
		fi
        ;;
    restart)
        cd scripts/
        ./$0 stop
        ./$0 start
        ;;
    *)
		echo "Usage: $0 {start|stop|restart|status}"
		;;
esac
