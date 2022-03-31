# EsDialerGDTools
广东天翼校园ZSM客户端通道(非网页) 工具包

没什么好说的<br>
unidbg-android\src\test\java\com\anjuke\mobile\sign\eSufing<br>
基于 [unidbg](https://github.com/zhkl0228/unidbg) 的libdaproxy.so模拟<br>
可以自己实现 ticket auth keep之类的<br>
目前防断网思路就是循环执行上面的步骤<br>
请自己实现项目已经完成了80%了<br>
立即可用版本就是这里面的apk ([下载](https://hub.fastgit.xyz/githuu5y5u/EsDialerGDTools/releases/download/1.0/_2.3.2075.21070101.apk)), 安装即用 保持进程活跃即可<br>
验证包结构可参考apk里面的东西 还有 https://github.com/claw6148/EsDialerGD
<br><br>
<br>
✅ Releases里面的 ```run.jar``` 使用方式:<br>
```java -jar run.jar <ipv4> <账号> <密码> [wlanacip] [Gateway]```<br>
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

  保活bash:
  ```bash
  #!/bin/sh
while true
do
  java -jar run.jar <ipv4> <账号> <密码> [wlanacip]
  sleep 1
  echo "restarting...."
done
  ```
