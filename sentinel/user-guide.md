

### Sentinel Dashboard本地启动说明

1. 执行gradle task 下载jar包并启动，具体task为：other/startSentinelDashboard
2. 应用程序启动时增加 vm参数：`-Dcsp.sentinel.dashboard.server=localhost:8090`, 
   或者在sentinel.properties中配置