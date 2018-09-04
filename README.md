# Javaagent
## 概述
javaagent是一个简单优雅的java agent,利用java自带的instrument特性+javassist字节码编辑技术，实现了无侵入的方法级性能监控。相比于NewRelic或者开源的[pinpoint](https://github.com/naver/pinpoint),本工具主打的是简单，我们只记录每个方法的执行次数和时间，并输出到日志。基于javaagent的日志，你可以使用[JQL](https://github.com/dingjs/javaagent/tree/master/resources/JQL)工具进行分析查询，也可以自己去写分析器，这样可以让你快速定位生产环境的性能瓶颈。

## 集成
java启动参数中就有jaaagent,你只需要在JAVA_OPTS中加入`-javaagent:/opt/javaagent/javaagent.jar=/opt/javaagent/agent.properties`就实现了方法级监控。其中`=`前指定的是jar包的路径，`=`后指定的是对agent的一些配置参数。

### agent.properties说明
```
# 你想监控哪些包，多个包用分号分隔，凡是不在该配置里的包中类都不会监控，所以不会去默认监控各种第三方包以及jre自带的类
agent.include.package=com.xxx
# 你不想监控的包，多个包用分号分隔，include.package减去exclude.package就是你监控的包
agent.exclude.package=
# 你不想监控的包的正则
agent.exclude.class.regex=
# 日志文件路径，会自动带上日期
agent.log.file=/opt/logs/agent.log
# 你希望多长时间输出一次日志，以秒为单位，建议生产环境配置600或者1200
agent.log.interval.seconds=600
# 你是否需要计算平均时间，对于生产环境，建议配成false,通过JQL查询时会进行计算
agent.log.avg.execute.time=false
# 是否监控pojo的get set方法，如果不开启的话，则不监控，避免出现大量pojo相关的日志。
agent.pojo.monitor.open=false
```
### JQL
JQL是用金雷同学用python写的一个agent日志分析工具，你可以用这个工具做查询、排序、limit等。详细用法，请参考[JQL完全指南](https://github.com/dingjs/javaagent/tree/master/resources/JQL)。

![JQL截图](https://github.com/dingjs/javaagent/blob/master/resources/images/JQL.png?raw=true)
   
   
