# Web版网络调试助手

提供B/S模式的网络调试功能，目前仅支持TCP Server，后续会逐渐增加TCP Client、UDP Server、UDP Client等功能。

Web端使用Vue+WebSocket开发。

## TCP Server

特点：
* 可限制端口范围
* 可限制最大TCP Server数量
* 可限制单个用户最大可创建的TCP Server数量
* 可限制连接至单个TCP Server的Client数量
* 支持用户侧自定义端口，或由服务器分配端口

配置参数：

* ```service.server-ip``` (默认值:```空```) 用于配置服务器的公网IP，不配置此项，则默认取服务器的本地IP
* ```service.tcp.min-port``` (默认值:```10000```) 用于配置TCP Server能够使用的最小端口号
* ```service.tcp.max-port``` (默认值:```20000```) 用于配置TCP Server能够使用的最大端口号
* ```service.tcp.max-server``` (默认值:```500```) 用于配置TCP Server的最大可创建数量
* ```service.tcp.max-server-per-session``` (默认值:```1```) 用于配置单个用户可创建的TCP Server的最大数量
* ```service.tcp.max-client-per-server``` (默认值:```10```) 用于配置连接至单个TCP Server的Client的最大数量

## 编译和部署

1. 首先需要编译Vue前端项目

```shell
cd src/main/ui
npm install
npm run build
```

2. 然后打包SpringBoot项目

```shell
mvn clean package
```

3. 部署

推荐使用systemctl将其部署为服务模式

```shell
cat <<EOF > /etc/systemd/system/net-debug.service
[Unit]
Description=net debug server
After=network.target

[Service]
Type=simple
User=nobody
Restart=always
RestartSec=5s
ExecStart=$JAVAHOME/bin/java -Dservice.server-ip=xxx.xxx.xxx.xxx -jar $PATH_TO_JAR/WebNetDebugAssistant-0.0.1-SNAPSHOT.jar

[Install]
WantedBy=multi-user.target
EOF

sudo systemctl unmask net-debug.service
sudo systemctl enable net-debug.service
sudo systemctl start net-debug.service
```

然后配置nginx转发：

```
	location ~ .* {
		proxy_pass http://127.0.0.1:9090;
		proxy_http_version 1.1;
		proxy_set_header Upgrade $http_upgrade;
		proxy_set_header Connection "Upgrade";
		proxy_set_header X-Real-IP $remote_addr;
		proxy_set_header Host $http_host;
		proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
		proxy_read_timeout 1800s;
	}
```

注意，目前由于在前/后端都并没有做WebSocket的心跳机制，所以nginx转发时设置的 ```proxy_read_timeout``` 将决定WebSocket没有数据通信时的超时断开时长。