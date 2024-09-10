# agent-analyzer启动教程

## 启动服务

启动 agent-analyzer 有以下两种方式，请使用提供的脚本(`run.sh`)启动程序

### 使用 `run.sh` 启动程序

请使用如下命令启动服务：`sh run.sh [port] [jmx-port]`

+ `port` 为访问 UIM API 的端口，如果不指定的话，默认为 **80**
+ `jmx-port` 为 JMX 远程监控端口。如果不指定的话，不会开启


## 验证启动是否成功

在运行 shell 脚本后，静候一小段时间，在浏览器中键入以下地址，即可查看是否启动成功：

`http://ip:port/`


## 停止服务

使用如下脚本停止服务，这样会停止所有的 `agent-analyzer` 服务。

```bash
sh stop.sh
```