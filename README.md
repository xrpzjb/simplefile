<p align="center">    
    <img alt="logo" src="https://myhome999.oss-cn-hangzhou.aliyuncs.com/simplefile/logo.png" style="width:100px">    
</p>    
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">simpleFile v1.0.0</h1>    
<h4 align="center">基于SpringBoot+Vue前后端分离的网盘系统</h4>

## 项目简介

simpleFile是一个简易本地文件网盘管理系统，在不改动物理文件路径的情况下，进行路径映射进行管理，支持文件的基本操作，以及文档的预览。

- 基于[ruoyi-vue3]()二次开发,地址[GitHub - yangzongzhuan/RuoYi-Vue3: :tada: (RuoYi)官方仓库 基于SpringBoot，Spring Security，JWT，Vue3 &amp; Vite、Element Plus 的前后端分离权限管理系统](https://github.com/yangzongzhuan/RuoYi-Vue3)
- 基于ruoyi-vue3去除了redis的缓存使用，改为了本地缓存，主要基于小系统不想整的系统部署复杂化，后续也将支持使用sqlite文件数据库部署
* 前端采用Vue、Element UI。
* 后端采用Spring Boot
* word、excel、pdf预览使用vue-office
  
  

## 内置功能

- 文件映射：通过物理路径映射到虚拟路径进行管理，支持全量扫描和增量扫描

- 文件查看：查看虚拟路径下的物理路径的文件列表

- 文件操作：支持文件的基本操作，复制，移动，剪贴，删除，重命名

- 文档预览：支持word、excel、pdf、文本的在线查看，无需搭建office在线服务，（[GitHub - 501351981/vue-office: 支持word(.docx)、excel(.xlsx,.xls)、pdf、pptx等各类型office文件预览的vue组件集合，提供一站式office文件预览方案，支持vue2和3，也支持React等非Vue框架。Web-based pdf, excel, word, pptx preview library](https://github.com/501351981/vue-office)）

- 音频播放：支持MP3等音频的在线播放

- 文件夹创建：支持文件夹在线创建

- 文件搜索：支持文件名模糊搜索

- 文件上传：支持文件在线上传

- 文件下载：支持文件在线快捷下载

- 压缩包管理：支持压缩包的在线预览，以及压缩包的在线解压

- 回收站管理：支持设置回收站回收时间，支持清空回收站以及还原文件

## 捐赠

捐赠

<p >    
    <img alt="logo" src="https://myhome999.oss-cn-hangzhou.aliyuncs.com/simplefile/alipay.jpg" style="width:300px">    
</p>
