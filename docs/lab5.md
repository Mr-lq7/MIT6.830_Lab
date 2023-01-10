### 实验内容

实现B+树索引，以进行有效的查找和范围扫描。需要实现**查询、插入、删除**等功能。

查询主要根据B+树的特性去递归查找即可；

插入要考虑节点的分裂；

删除要考虑节点内元素的重新分配，兄弟节点的合并

lab5在lab4的基础上。

[(163条消息) MIT6.830 lab5 B+ Tree Index 实验报告_跳着迪斯科学Java的博客-CSDN博客](https://blog.csdn.net/weixin_45834777/article/details/121209402)（有点问题）

[src/java/simpledb/index/BTreeFile.java · wind/SimpleDB - 码云 - 开源中国 (gitee.com)](https://gitee.com/wygandwind/simple-db/blob/master/src/java/simpledb/index/BTreeFile.java)（没有问题）

#### 实现提示：

> - 看index文件夹下的BTreeFile.java
> - 要熟悉四种不同类型的页面
>   - 叶子节点页面：BTreeLeafPage.java
>   - 内部节点页面：BTreeInternalPage.java
>   - 头部节点页面：BTreeHeaderPage.java
>   - 根节点页面：BTreeRootPtrPage.java

#### Exercise 1：搜索（Search）

> - 讲义很重要
> - 实现BTreeFile类中的findLeafPage方法
> - 实现后需要通过BTreeFileReadTest所有的单元测试以及系统测试中的BTreeScanTest.java

#### Exercise 2：插入（Insert）

> - 讲义很重要。插入有可能会伴随着分裂。
> - 插入B+树
> - 实现BTreeFile类中的splitLeafPage方法和splitInternalPage方法
> - 实现后需要通过BTreeFileInsertTest单元测试以及系统测试中的BTreeFileInsertTest.java

#### 删除（Delete）

> - 讲义很重要。删除有可能会伴随着重新分发页面以及页面之间（自己和兄弟页面tuple都比较少）的合并。
> - 重新分发页面（Redistributing Pages）
>   - 实现BTreeFile.stealFromLeafPage()、BTreeFile.stealFromLeftInternalPage()、BTreeFile.stealFromRightInternalPage()方法
> - 合并页面（Merging pages）
>   - 实现BTreeFile.mergeLeafPages()、BTreeFile.mergeInternalPages()方法
> - 实现后需要通过BTreeFileDeleteTest单元测试以及系统测试中的BTreeFileDeleteTest.java

















