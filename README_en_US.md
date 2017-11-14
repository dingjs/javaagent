# Javaagent
## Overview
Javaagent a simple yet elegant java agent tool, to monitor every method's execute frequency and time consuming.
It uses jre's built-in feature Instrument and take use of javassist bytecode edit tool,monitor the mthod call and output to the log file.You can use [JQL](https://github.com/dingjs/javaagent/tree/master/resources/JQL) to analyze the agent log,find your application's performance bottlenecks.

## How to use
You can add the following options in the JAVA_OPTS
>`-javaagent:/opt/javaagent/javaagent.jar=/opt/javaagent/agent.properties`

### agent.properties
```
# The packages which you  want to monitor,seprated by semicolon.Only the packages configed in here will be monitored.
agent.include.package=
# The packages which you don't want to monitor,seprated by semicolon.
agent.exclude.package=
# The classes of regex format which you don't want to monitor 
agent.exclude.class.regex=
# The log file path,will add date string in the filename automatically
agent.log.file=/opt/logs/agent.log
# How long do you want to output the agent log to the log file,600 or 1200 is recommended.
agent.log.interval.seconds=600
# true means calculate the avg time for  every method.
agent.log.avg.execute.time=false
# Monitor pojo get set method or not,false means don't monitor get set method.
agent.pojo.monitor.open=false
```
### JQL
JQL is a python tool to analyze the agent logs, developed by my collegue jinlei. Read [JQL complete guide](https://github.com/dingjs/javaagent/tree/master/resources/JQL)for the details if you can read Chinese.

![JQL screenshot](https://github.com/dingjs/javaagent/tree/master/resources/images/JQL.png?raw=true)
