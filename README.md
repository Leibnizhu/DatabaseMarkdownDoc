仓促写的一个将数据库表结构导出成markdown文档的web服务.  
## TODO
- 预览样式优化
就这样,有空再改.

## 配置文件
请修改`src/main/resources/config.json`。  
由于端口号涉及到Docker的启动配置,所以使用Docker执行时,不建议修改该配置文件的端口号配置.  

## 手动执行
打包后执行  
```bash
 java -jar target/docbuilder-1.0-SNAPSHOT-fat.jar /path/to/配置文件
```
浏览器打开 [http://localhost:8233](http://localhost:8233), 然后下拉框选择数据库, 会自动生成文档,在页面可以预览(渲染效果一般),也可以点下载按钮保存到本地在进行修改.  

## Docker执行
先保证已经安装`docker`和`mvn`,并修改好配置文件`src/main/resources/config.json`,然后编辑`build.sh`,按需修改端口号映射,然后执行:  
```bash
./build.sh
```
