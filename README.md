# EsDialerGDTools
广东天翼校园ZSM客户端通道(非网页) 工具包<br>

<br>unidbg-android\src\test\java\KO\eSufing<br>
基于 [unidbg](https://github.com/zhkl0228/unidbg) 的libdaproxy.so模拟<br>
不能用请自己抓包修改编译, unidbg-android 需要 JDK8 剩下的都是 JDK7(否则无法正常Hook)<br>
验证协议参考: https://github.com/claw6148/EsDialerGD
<br><br>
<br>
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
