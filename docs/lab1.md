### 实验内容

主要是阐述数据库的**基础架构**，各个组成部分的关系，包括表、页、缓冲池、字段等。实现数据库对**数据的管理**

[(159条消息) 6.830 Lab 1: SimpleDB_HearmingBear的博客-CSDN博客](https://blog.csdn.net/hjw199666/article/details/103486328)

#### Exercise 1

> - Tuple是数据库中的数据，里面的Field就是数据；TupleDesc就是数据库中的模式，里面有一组TDItems定义了每一列的类型
> - 实现骨架方法在
>   - src/java/simpledb/storage/TupleDesc.java
>   - src/java/simpledb/storage/Tuple.java
> - 实现后需要通过TupleTest（modifiyRecordId应该是fail）和TupleDescTest单元测试
> - 函数的注释以及参数写的都十分清楚，照着写即可。

![image-20221126145425774](D:\WorkSpace\Lab\MIT830\lab1.assets\image-20221126145425774.png)

TupleDesc测试结果：

![image-20221126120918190](D:\WorkSpace\Lab\MIT830\lab1.assets\image-20221126120918190.png)

Tuple测试结果：

![image-20221126120933017](D:\WorkSpace\Lab\MIT830\lab1.assets\image-20221126120933017.png)

#### Exercise 2

> - 全局Catalog是Catalog类的一个单例对象，存储着数据库的元信息。每个table与一个TupleDesc对象关联，该对象允许操作符确定表中字段类型和数量
> - 需要支持添加新表格的能力，并获得有关特定表的信息
> - 实现骨架方法在
>   - src/java/simpledb/common/Catalog.java
> - 实现后需要通过CatalogTest单元测试

#### Exercise 3

> - 缓冲池（BufferPool）负责缓存内存中最近从磁盘读取的页面。所有操作符都通过缓冲池从磁盘上的各种文件读取和写入页面。缓冲池由固定数量的页组成。
> - 只需要实现构造函数和SeqScan运算符使用的BufferPool.getPage方法，缓冲池最多应存储numPages页面。在之后的实验中会实现淘汰机制
> - 如果对不同的页面发出了多个numPages请求，则可能会抛出DbException
> - 在src/java/simpledb/storage/BufferPool.java中实现getPage方法
> - 没有为BufferPool提供单元测试，这个函数的功能将会在HeapFile中被测试

#### Exercise 4

> - 访问方法提供了硬盘读写数据的方式。通常的访问方法包括heap files(没排序的行数据文件)和B-trees，在这里，只需要实现**heap file**访问方法。
>
> - 访问方法提供了硬盘读写数据的方式。通常的访问方法包括heap files(没排序的行数据文件)和B-trees，在这里，只需要实现heap file访问方法。
>
>     HeapFile对象包含一组“物理页”，每一个页大小固定，大小由BufferPool.DEFAULT_PAGE_SIZE定义，页内存储行数据。在SimpleDB中，数据库中每一个表对应一个HeapFile对象，HeapFile中每一页包含很多个slot，每个slot是留给一行的位置。除了这些slots，每个物理页包含一个header，header是每个tuple slot的bitmap。如果bitmap中对应的某个tuple的bit是1，则这个tuple是有效的，否则无效（被删除或者没被初始化）。HeapFile对象中的物理页的类型是HeapPage，这是一种Page的接口，物理页是存储在buffer pool中，通过HeapFile类读写。
>
>     SimpleDB数据库的每个tuple需要tuple size * 8 bits 的内容大小和1 bit的header大小。因此，在一页中可以包含的tuple数量计算公式是：_tuples per page_ = floor((_page size_ * 8) / (_tuple size_ * 8 + 1))。其中，tuple size是页中单个tuple 的bytes大小。  
>
>     一旦知道了每页中能够保存的tuple数量，需要的header的物理大小是：headerBytes = ceiling(tupsPerPage/8)。
>
>     bitmap中，low bits代表了先填入的slots状态。因此，第一个headerByte的最小bit代表了第一个slot是否使用，第二小的bit代表了第二个slot是否使用。同样，最大headerByte的一些高位可能不与slot存在映射关系，只是满足headerBytes的ceiling。
>
>     提示：所有的java虚拟机都是**big-endian**
>
> - 实现骨架方法在
>
>   - src/simpledb/HeapPageId.java
>   - src/simpledb/RecordId.java
>   - src/simpledb/HeapPage.java
>
> - 实现后需要通过HeapPageIdTest、HeapPageReadTest、RecordIdTest单元测试

#### Exercise 5

> - 要从磁盘读取页面，首先需要计算文件中的正确偏移量。提示：您需要随机访问文件才能以任意偏移量读取和写入页面。从磁盘读取页时不应调用 BufferPool 方法。
> - 还需要实现HeapFile.iterator（）方法，该方法应遍历 HeapFile 中每个页面的元组。迭代器必须使用 'BufferPool.getPage方法来访问 'HeapFile' 中的页面。此方法将页面加载到缓冲池中，并最终（在以后的实验室中）用于实现基于锁定的并发控制和恢复。不要在 open（） 调用时将整个表加载到内存中 - 这将导致非常大的表出现内存不足错误。
>
> - 实现骨架方法在
>   - src/java/simpledb/storage/HeapFile.java
> - 实现后，代码应该通过 HeapFileReadTest 中的单元测试。
> - todo，写迭代器有点难，需要再继续研究

#### Exercise 6

> - 操作符负责查询计划的实际执行，它们实现关系代数的操作。在simpledb中，运算符是基于迭代器的，每个运算符实现dbiterator接口。通过将较低级别的运算符传递到较高级别的运算符的构造函数中，即通过“将它们链接在一起”，运算符连接到一个计划中。计划叶子处的特殊访问方法运算符负责从磁盘读取数据（因此它们下面没有任何运算符）。
> - 实现骨架方法在
>   - src/java/simpledb/execution/SeqScan.java
> - 网上博客写的很多都是错的。需要通过ScanTest测试

