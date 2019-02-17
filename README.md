# 优易（NetworkOptZ）相关功能介绍
## （1）当前服务小区、邻区基本信息获取
本APP可以运行在支持4G的安卓手机上。在手机开机、插上SIM卡并且注册到4G网络之后，该APP通过调用安卓手机Telepphony API，可以获取到目前使用的的运营商、IMSI、IMEI、手机型号、安卓版本、当前手机所在的市县街道、经度、纬度、高度，占用的小区名称、TAC、PCI、CGI、频点、Band、频率、RSRP、RSRQ、SINR，最近占用的小区记录

<img src="https://github.com/FFWeather/NetworkOptZ/blob/master/app/release/ScreenShot/%E4%B8%BB%E7%95%8C%E9%9D%A2.jpg" width="384" height="683" alt="主界面"/>

当前邻区信息

<img src="https://github.com/FFWeather/NetworkOptZ/blob/master/app/release/ScreenShot/%E9%82%BB%E5%8C%BA.jpg" width="384" height="683" alt="邻区"/>

## （2）基站数据库导入和查询
可以将指定格式的基站数据库，以文件的形式放到手机SD卡-优易目录中，APP可以识别文件中的内容并将基站信息导入到数据库中，完成基站数据库的导入。

<img src="https://github.com/FFWeather/NetworkOptZ/blob/master/app/release/ScreenShot/%E8%AF%BB%E5%8F%96%E6%96%87%E4%BB%B6%E5%9F%BA%E7%AB%99%E6%95%B0%E6%8D%AE%E5%BA%93.jpg" width="384" height="683" alt="读取文件"/>

可以将文件导入到sqlite数据库中

<img src="https://github.com/FFWeather/NetworkOptZ/blob/master/app/release/ScreenShot/%E6%96%87%E4%BB%B6%E8%AF%BB%E5%8F%96%E4%B8%AD.jpg" width="384" height="683" alt="文件导入数据库中"/>

可以通过输入小区名称、TAC、ENBID等信息查询符合条件的小区（支持模糊查询），并在界面上显示符合条件的小区的地市、CGI、小区名称、EARFCN、TAC、PCI等基本信息信息。

<img src="https://github.com/FFWeather/NetworkOptZ/blob/master/app/release/ScreenShot/%E5%9F%BA%E7%AB%99%E6%95%B0%E6%8D%AE%E5%BA%93%E6%9F%A5%E8%AF%A2.jpg" width="384" height="683" alt="查询基站数据库"/>

点击某个小区后，可以显示该小区的海拔、覆盖场景、覆盖类别、小区类别、经纬度、挂高等详情。

<img src="https://github.com/FFWeather/NetworkOptZ/blob/master/app/release/ScreenShot/%E8%AF%A6%E6%83%85.png" width="384" height="200" alt="小区详情"/>

## （3）GIS展示
能切换普通地图、卫星地图、热力图、路况显示，支持放大缩小和旋转；

<img src="https://github.com/FFWeather/NetworkOptZ/blob/master/app/release/ScreenShot/%E7%83%AD%E5%8A%9B%E5%9B%BE%E3%80%81%E8%B7%AF%E5%86%B5%E5%9B%BE.jpg" width="384" height="683" alt="地图显示"/>

可以显示用户当前所在位置及周围4G小区，并将当前占用的主服务小区用红色的线条标记出来；

<img src="https://github.com/FFWeather/NetworkOptZ/blob/master/app/release/ScreenShot/GIS%E7%95%8C%E9%9D%A2.jpg" width="384" height="683" alt="用户位置及周围小区"/>


## （4）自动更新
获取网络侧该App的最新版本

<img src="https://github.com/FFWeather/NetworkOptZ/blob/master/app/release/ScreenShot/%E5%90%8E%E5%8F%B0%E6%A3%80%E6%9F%A5%E6%98%AF%E5%90%A6%E6%9C%89%E6%96%B0%E7%89%88%E6%9C%AC.jpg" width="384" height="683" alt="检查是否有新版本"/>

如果网络侧版本比本地新，则会提示用户更新

<img src="https://github.com/FFWeather/NetworkOptZ/blob/master/app/release/ScreenShot/%E6%8F%90%E7%A4%BA%E6%9B%B4%E6%96%B0.jpg" width="384" height="683" alt="提示更新"/>

点击更新后自动下载

<img src="https://github.com/FFWeather/NetworkOptZ/blob/master/app/release/ScreenShot/%E4%B8%8B%E8%BD%BD%E6%96%B0%E7%89%88%E6%9C%AC.jpg" width="384" height="683" alt="开始下载"/>

下载完成后弹出安装界面

<img src="https://github.com/FFWeather/NetworkOptZ/blob/master/app/release/ScreenShot/%E4%B8%8B%E8%BD%BD%E5%AE%8C%E6%88%90%E5%90%8E%E8%87%AA%E5%8A%A8%E5%AE%89%E8%A3%85.jpg" width="384" height="683" alt="提示安装"/>
