# 阿里云定时解析服务
定时将公网IP解析到阿里云DNS中，服务会自动检查当前IP与阿里云中解析记录是否相同，如果相同则不进行更新，反之更新。
## 使用方式
1. 将代码拉取到本地，进行`mvn package`操作。
2. 使用需要系统中有Java运行环境，版本为1.8，切换到当前目录后，运行命令为:
```
java -jar alidns-1.0.jar accessKey:阿里云的accessKey accessSecret:阿里云的secret rr:需要解析的域名前缀
```

> 例：假如有A级域名为xxx.com，首先在阿里云域名解析中添加一个解析记录，解析记录的值为`test`，在启动此项目的时候rr参数配置为test
