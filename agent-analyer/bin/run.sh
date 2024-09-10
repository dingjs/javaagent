#!/bin/bash
DIR1="`dirname $BASH_SOURCE`"
CURRENT_PATH=`readlink -f "$DIR1"`

APP_NAME="agent-analyzer.jar"

APP_PORT=8080
if [ -z "$1" ]
then
    echo "没有指定程序端口，将使用默认端口 ${APP_PORT}"
else
    APP_PORT=$1
fi


echo "尝试获取本机 ip 地址"
IP_ADDR='127.0.0.1'
IP_ADDR=$(ip addr | awk '/^[0-9]+: / {}; /inet.*global/ {print gensub(/(.*)\/(.*)/, "\\1", "g", $2)}')
echo "成功获取本机 ip 地址: $IP_ADDR"

JAVA_OPT="${JAVA_OPT} -server -Xms512m -Xmx2g -Xmn256m"
JAVA_OPT="${JAVA_OPT} -XX:+UseG1GC"
JAVA_OPT="${JAVA_OPT} -XX:+HeapDumpOnOutOfMemoryError"
if [ -z "$2" ]
then
    echo "没有指定 JMX 远程端口，将无法进行远程监控"
else
    JAVA_OPT="${JAVA_OPT} -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=${2} -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostn
ame=${IP_ADDR}"
fi

echo "程序运行参数为：${JAVA_OPT}"
echo "程序运行端口为：${APP_PORT}"

nohup java ${JAVA_OPT} -jar ${CURRENT_PATH}/${APP_NAME} --server.port=$APP_PORT > nohup_${APP_PORT}.out 2>&1 &

