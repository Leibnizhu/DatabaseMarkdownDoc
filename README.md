仓促写的一个将数据库表结构导出成markdown文档的web服务.  
数据库地址\用户名\密码暂时写在代码里, 要用的请手动修改.  
两个类的数据库连接代码重复,应该统一管理的(见FIXME),暂时没空改.  
服务的端口也是,请手动修改.

## 手动执行
打包后执行  
```bash
 java -jar target/docbuilder-1.0-SNAPSHOT-fat.jar
```
浏览器打开 [http://localhost:8233](http://localhost:8233), 然后下拉框选择数据库, 会自动生成文档,在页面可以预览(渲染效果一般),也可以点下载按钮保存到本地在进行修改.  

## Docker执行
先保证已经安装docker,编辑build.sh,按需修改端口号,然后执行build.sh

就这样,有空再改.