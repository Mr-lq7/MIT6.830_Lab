### 实验内容

主要是实现数据库的**操作符**，包括insert、delete、select、join、group by、order by等。此外，设计一项替换策略从缓冲池中冲洗陈旧页面。

lab2在lab1的基础上

#### Exercise 1：过滤和连接（filter and join）

> - 回想下simpledb实施关系代数的操作。需要实现filter和join两个操作
> - filter：该操作仅返回满足谓词的元素，该谓词被指定为构造函数的一部分，过滤所有与谓词不匹配的元组
> - join：该操作把两个子级连接元组作为构造函数的一部分
> - 实现骨架方法在
>   - src/java/simpledb/execution/Predicate.java
>   - src/java/simpledb/execution/JoinPredicate.java
>   - src/java/simpledb/execution/Filter.java
>   - src/java/simpledb/execution/Join.java
> - 实现后需要通过PredicateTest，JoinPredicateTest，FilterTest，JoinTest四个单元测试。进一步地，需要通过系统测试：FilterTest和JoinTest

[(161条消息) Mit 6.830：SimpleDB Lab2_DespairC的博客-CSDN博客](https://blog.csdn.net/DespairC/article/details/124253674) + happier

#### Exercise 2：聚合（Aggregates）

> - 实现五个SQL聚合（计数、总和、AVG、MIN、MAX），并且要支持分组。支持在一个字段上支持聚合，然后通过单个字段进行分组。
> - 实现骨架方法在
>   - src/java/simpledb/execution/IntegerAggregator.java
>   - src/java/simpledb/execution/StringAggregator.java
>   - src/java/simpledb/execution/Aggregate.java
> - 实现后需要通过IntegerAggregatorTest、StringAggregatorTest、AggregatorTest三个单元测试。进一步地，需要通过系统测试：AggregateTest

#### Exercise 3：堆文件可变性（HeapFile Mutability）

> - 插入元组（insertTuple）：将元组添加到HeapFile中。需要找到一个带有空插槽的页面，如果HeapFile中没有此类页面，需要创建一个新页面并将其添加到磁盘上的物理文件，然后需要确保正确更新元组中的RecordID。
> - 删除元组（deleteTuple）：元组包含RecordIDs，可以根据该ID找到元组所处的页面，然后进行删除
> - 实现骨架方法在
>   - src/java/simpledb/storage/HeapPage.java
>   - src/java/simpledb/storage/HeapFile.java(Note that you do not necessarily need to implement writePage at this point).
>   - 需要注意的是还需要在BufferPool.java中做改动：insertTuple()、deleteTuple()。并且增加元组和删除元组与替换策略有关系
> - 实现后需要通过HeapPageWriteTest、HeapFileWriteTest、BufferPoolWriteTest（这个在实现Exercise 5后能通过）

#### Exercise 4：插入和删除（Insertion and deletion）

> - 需要调用BufferPool.java中的插入和删除元组方法
> - 实现骨架方法在：
>   - src/java/simpledb/execution/Insert.java
>   - src/java/simpledb/execution/Delete.java
> - 实现后需要通过InsertTest。Delete的测试暂未提供
> - 系统测试的InsertTest、DeleteTest

#### Exercise 5：页替换（Page eviction）

> - 这里需要改动挺多的，并且会影响到Exercise 3和Exercise 4的单元测试
> - 需要实现LRU替换策略（自己定义一个双向链表），这里的话Lab2_DespairC写的有问题，挺坑的
> - 实现骨架方法在
>   - src/java/simpledb/storage/BufferPool.java
> - 实现后需要通过系统测试的EvictionTest system test

[MIT 6.830 Lab 2 实验笔记 - 代码先锋网 (codeleading.com)](https://www.codeleading.com/article/98535799544/) + happier

















