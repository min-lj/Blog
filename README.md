## 在线地址

**项目链接：** [www.talkxj.com](https://www.talkxj.com)

**后台链接：** [www.admin.talkxj.com](https://www.admin.talkxj.com)

测试账号：test@qq.com，密码：1234567，可登入后台查看

## 目录结构

前端项目位于blog-vue下，blog为前台，admin为后台。

后端项目位于blog-springboot下。

SQL文件位于根目录下。

可直接导入该项目于本地，修改后端配置文件中的数据库连接信息，项目中使用到的关于阿里云功能和第三方授权登录等需要自行开通。

当你克隆项目到本地后可使用邮箱账号：admin@qq.com，密码：1234567进行登录，也可自行注册并将其修改为admin权限。

## 项目特点

- 前台参考"Hexo"的"Butterfly"设计，美观简洁，响应式体验好
- 后台参考"element-admin"设计，侧边栏，历史标签，面包屑自动生成。
- 采用Markdown编辑器，写法简单。
- 评论支持表情输入回复等，样式参考Valine。
- 代码遵循阿里巴巴开发规范，利于开发者学习。
- 前后端分离部署，适应当前潮流。
- 接入第三方登录，减少注册成本。
- 留言采用弹幕墙，更加炫酷。
- 支持代码高亮和复制，图片预览，深色模式等功能，提升用户体验。
- 搜索文章支持高亮分词，响应速度快

## 技术介绍

**前端：** "vue" + "vuex" + "vue-router" + "axios" + "vuetify" + "element" + "echarts"

**后端：** "SpringBoot" + "nginx" + "docker" + "SpringSecurity" + "Swagger2" + "MyBatisPlus" + "Mysql" + "Redis" + "elasticsearch" + "rabbitMQ" + "MaxWell"

**其他：** 接入QQ，微博第三方登录，接入腾讯云人机验证

## 运行环境

**服务器：** 阿里云2核4G CentOS7.2

**CDN：** 阿里云全站加速

**对象存储：** 阿里云OSS

这套搭配响应速度非常快，可以做到响应100ms以下。

## 开发工具

|开发工具|说明|
|-|-|
|IDEA|Java开发工具IDE|
|VSCode|Vue开发工具IDE|
|Navicat|MySQL远程连接工具|
|Another Redis Desktop Manager|Redis远程连接工具|
|X-shell|Linux远程连接工具|
|filezilla|Linux文件上传工具|

## 开发环境

|工具|版本|
|-|-|
|JDK|1.8|
|MySQL|8.0.20|
|Redis|6.0.5|
|Elasticsearch|6.6.0|
|RabbitMQ|3.8.5|

## 项目截图

![QQ截图20200629122244.png](https://www.static.talkxj.com/articles/1593404582248.png)

![QQ截图20200629122228.png](https://www.static.talkxj.com/articles/1593404582352.png)

![QQ截图20200703155942.png](https://www.static.talkxj.com/articles/1593763327991.png)

![QQ截图20200711095250.png](https://www.static.talkxj.com/articles/1594432395374.png)

## 项目环境安装

详见文章[Docker安装运行环境](https://www.talkxj.com/articles/2)

## 项目配置

详见文章[项目配置教程](https://www.talkxj.com/articles/3)

## docker部署项目

详见文章[项目部署教程](https://www.talkxj.com/articles/13)

## 注意事项

- 博主用户信息ID默认为1，如需修改请到 /constant/UserConst 处修改BLOGGER_ID
- 邮箱配置，第三方授权配置需要自己申请。
- ElasticSearch需要自己先创建索引，项目运行环境教程中有介绍。

## 项目总结

博客作为新手入门项目是十分不错的，项目所用的技术栈覆盖的也比较广，适合初学者学习。总的来说，项目逻辑都比较简单，最麻烦的地方在于处理评论回复和第三方登录这块。做的不好的地方请大家见谅，有问题的或者有好的建议可以私聊联系我，

## 关注&交流

![博客技术交流群聊二维码.png](https://www.static.talkxj.com/articles/1594437310326.png)





