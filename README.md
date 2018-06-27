仓促写的一个将数据库表结构导出成markdown文档的web服务.  
## TODO
- 预览样式优化
就这样,有空再改.

## 配置文件
可以放在任何位置，任何文件名，内容参考`src/main/resources/config.json`。  

## 手动执行
打包后执行  
```bash
 java -jar target/docbuilder-1.0-SNAPSHOT-fat.jar /path/to/配置文件
```
浏览器打开 [http://localhost:8233](http://localhost:8233), 然后下拉框选择数据库, 会自动生成文档,在页面可以预览(渲染效果一般),也可以点下载按钮保存到本地在进行修改.  

## Docker执行
先保证已经安装`docker`,编辑`build.sh`,按需修改端口号,然后执行
```bash
./build.sh
```
