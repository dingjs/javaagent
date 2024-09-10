#!/bin/bash
# add comment
if [ -z "$1" ]
then
    pid=`ps ax |grep -i 'agent-analyzer.jar' |grep java | grep -v grep |  awk '{print $1}'`
else
    pid=`ps ax |grep -i 'agent-analyzer.jar' |grep java | grep -i 'server.port='''${1}''''| grep -v grep |  awk '{print $1}'`
fi

if [ -z "$pid" ] ; then
        echo "agent-analyzer is not running."
        exit 0;
fi
echo "agent-analyzer(${pid}) is running."
echo "Send shutdown request to agent-analyzer(${pid})....."
kill -9 ${pid}
echo "Shutdown agent-analyzer(${pid}) success."
 