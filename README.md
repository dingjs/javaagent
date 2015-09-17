# simpleAgent
It's a simple java agent to monitor every method's execute frequency and time consuming.
As a developer,you probably konw your code's performance roughly,but you never know it for sure.How many times your code was 
invoked during a day,how much time do your code take just for one time running.
SimpleAgent is a tool that can do the monitor work for you,it can monitor every method as you want to,and cost almost nothing
to your system's performance,so you can use it not only during testing,but after release.

you can use simpleAgent by config the java options like this： 
-javaagent:D:/javaagent-1.0.0.jar=D:/agent.properties
the agent.properties look like as follows:
   
    #the default packages that you don't want to monitor,multi config separated by semicolon 
    # such as tomcat,jre's method
    agent.exclude.package.default=
    #the  packages that you don't want to monitor,multi config separated by semicolon 
    agent.exclude.package=
    #the packages you want to monitor,the agent only monitor the packages and subpackages thar configed in here,
    #multi config separated by semicolon 
    agent.include.package=com.thunisoft
    #the log file name,it will add current date automatically
    agent.log.file=d\:\\agent.log
    #how much seconds per time do the log output
    agent.log.interval.seconds=600
    #the class name't regex pattern that you don't want to monitor
    agent.exclude.class.regex=
    #do you want to calculate the avg execute time
    agent.log.avg.execute.time=false
    #the default class name't regex pattern that you don't want to monitor
    agent.exclude.class.regex.default=.*EnhancerByCGLIB.*;.*FastClassByCGLIB.*
   
   
