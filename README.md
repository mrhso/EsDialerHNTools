# EsDialerGDTools
广东天翼校园ZSM客户端通道(非网页) 工具包<br>

<br>unidbg-android\src\test\java\KO\eSufing<br>
基于 [unidbg](https://github.com/zhkl0228/unidbg) 的libdaproxy.so模拟<br>
不能用请自己抓包修改编译, unidbg-android 需要 JDK8 剩下的都是 JDK7(否则无法正常Hook)<br>
由于本项目可能随时失效，故不提供文档<br>
验证协议参考: https://github.com/claw6148/EsDialerGD

注意!    
由于目前客户端版本的更新以及本项目协议部分没有完全遵循原规范, 本项目部分失效, 表现为keep后迅速断网    
解决方案就暂不放出了, 还是比较好解决的    
有想法的同学可以自行使用 3.0.2081.23032701 版本客户端    
使用GG修改器的某脚本对复制到SDCARD的DEX文件进行分析   
另外如果是通过路由器使用的话建议修改MTU到某个小一点的值    
原因就不透露了    

✅ ([Releases](https://hub.fastgit.xyz/githuu5y5u/EsDialerGDTools/releases/)) 里面 Build.zip 的 ```run.jar``` 使用方式:<br>
```java -jar run.jar <ipv4> <账号> <密码> [wlanacip] [Gateway]```<br><br>
(wlanacip 是 弹出网页验证界面连接后面的参数 -> xxxx/qs/index.jsp?wlanacip=<> , GateWay 是 DHCP 网关IP)<br><br>
💡 termux 安装:<br>
  ```diff
  1. 解压Build.zip到/sdcard
  2. https://mirrors.tuna.tsinghua.edu.cn/help/termux/ 换源
  #> apt update
  #> pkg install proot-distro
  #> termux-setup-storage
  #> termux-wake-lock
  #> proot-distro install ubuntu
  >  proot-distro login ubuntu
  >  sudo apt update
  >  sudo apt install openjdk-11-jdk
  >  cd /sdcard
  >  java -jar run.jar <ipv4> <账号> <密码> [wlanacip] [Gateway]
  !下一次开termux从绿色部分开始执行
  !原生 Ubuntu 直接从绿色下一行开始执行
  建议关闭doze mode
  adb shell dumpsys deviceidle disable
  adb shell dumpsys deviceidle whitelist +com.termux
  ```
  <br>

  保活 shell 脚本:
  ```bash
  #!/bin/sh
while true
do
  java -jar run.jar <ipv4> <账号> <密码> [wlanacip]
  sleep 1
  echo "restarting...."
done
  ```
