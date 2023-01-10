### 参考书籍

> Database System Concepts

### 参考课程

> CMU 15-445

### 实验仓库

>  https://github.com/MIT-DB-Class/simple-db-hw-2021.git 

### 实验工具

> - 本地配置好git、java相关环境，安装IDEA开发工具即可。
> - 因为java具有jvm，具有平台无关性，因此可以在windows系统中用Intelij IDEA进行编写与开发
> - IDEA专业版，导入simple-db-hw-2021项目文件时，IDEA会提示下载Ant构建工具，跟着提示下载即可。

![image-20221125201528196](D:\WorkSpace\Lab\MIT830\lab_pre.assets\image-20221125201528196.png)

### IDEA环境配置

> - 下载Ant并进行构建（在使用IJ打开项目的时候会自动提示），如上图所示
>
> - 指定SDK：文件->项目结构->项目，设置SDK以及语言级别
>
> - 设置输出目录，一般来说默认即可。文件->项目结构->项目，编译器输出
>
> - 设置source root，test root：右键simple-db-hw2021/src/java文件夹，选择“将源代码标记为源代码根目录”；右键simple-db-hw2021/test文件夹选择“将源代码标记为测试源代码根目录”
>
> - 将simple-db-hw-2021中的lib文件夹下的jar包加入到项目中，文件->项目结构->项目，如下图所示
>
>   ![image-20221125210234708](D:\WorkSpace\Lab\MIT830\lab_pre.assets\image-20221125210234708.png)
>
> - 在执行完上述操作后，找到SimpleDb文件，点击运行，如果出现下面提示信息，应该就算环境配置成功了

- lab1：对整个SimpleDB有大致的了解，并且实现其中数据存储相关的类，然后还有一些其他东西比如Catalog和SeqScan
- lab2：实现查询处理中的各种算子
- lab3：实现查询的优化相关的功能
- lab4：实现事务处理的相关功能
- lab5：实现B+树索引
- lab6：实现回滚和恢复等功能