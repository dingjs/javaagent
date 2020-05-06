# Javaagent
## 概述
javaagent是一个简单优雅的java agent,利用java自带的instrument特性+javassist字节码编辑技术，实现了无侵入的方法级性能监控。相比于NewRelic或者开源的[pinpoint](https://github.com/naver/pinpoint),以及阿里的[arthas](https://github.com/alibaba/arthas),本工具主打的是简单，我们只记录每个方法的执行次数和时间，并输出到json格式的日志文件中。基于javaagent的日志，你可以使用严丽同学开发的[agent日志分析工具](https://pan.baidu.com/s/1Ma4iEWRmBonGO1TapeEF-g)进行分析查询，也可以使用[在线日志分析器](http://47.96.150.36:8080)，或者可以自己去写分析器，这样可以让你快速定位生产环境的性能瓶颈。

## 集成
java启动参数中就有jaaagent,你只需要在JAVA_OPTS中加入`-javaagent:/opt/javaagent/javaagent.jar=/opt/javaagent/agent.properties`就实现了方法级监控。其中`=`前指定的是jar包的路径，`=`后指定的是对agent的一些配置参数。

### agent.properties说明
```
# 需要监控的包，多个包用分号分隔
agent.include.package=com.XXX
# 不需要监控的包，多个包用分号分隔。 需要监控的包-不需要监控的包就是真正要监控的包
agent.exclude.package=
# 日志文件，会自动增加日期后缀
agent.log.file=d\:\\agent.log
# 日志输出周期
agent.log.interval.seconds=600
# 不需要监控的类的正则表达式
agent.exclude.class.regex=
# 是否记录平均时间
agent.log.avg.execute.time=false
# 默认不需要监控的类的正则表达式
agent.exclude.class.regex.default=.*EnhancerByCGLIB.*;.*FastClassByCGLIB.*
# 记录方法的耗时时是采用nanoTime,还是currentTimeMillis,nanoTime更准确，但是会耗时一些
agent.log.nano=true
```

## agent日志分析器使用
agent日志分析器是我同事严丽使用SpringBoot+h2数据库开发的分析工具，除了基础功能，对于有sql能力的同学，你可以直接使用[console](http://47.96.150.36:8080/console)登录后，直接写sql进行统计，你可以按自己想要的维度来进行处理。
- JDBC URL:   jdbc:h2:mem:~/h2test
- USer Name : sa
- Password : 空
登录后，你就可以尽情的写sql了。

## 欢迎关注我的公众号
![程序员阿水](https://ftp.bmp.ovh/imgs/2020/05/450fcb3d1a2fe4eb.jpg)
