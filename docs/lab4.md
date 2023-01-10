### 实验内容：事务、锁、并发控制

> - 实现一个基于锁的事务系统。需要在适当的位置加锁和解锁。
>
> - lab4在lab3的基础上进行。
>
> - 需要知道数据库的**两段锁**
> - 共享锁（读锁）或者排他锁（写锁）
> - ACID
>
> - 该实验内容就是战德臣慕课的最后部分
>
> - 恢复和缓冲管理：No steal + Force
>   - force：表示事务在committed后必须将所有更新立即持久化到磁盘
>   - no steal：磁盘上不会存在uncommitted的数据
>   - 现在的DBMS策略一般都是steal+no force

[(162条消息) MIT 6.830 Lab 4 实验笔记 两段锁以及事物_马走日_ZouR-Ma的博客-CSDN博客](https://blog.csdn.net/Fitzzzz/article/details/118768879)

[(162条消息) MIT6.830 lab4 SimpleDB Transactions 实验报告_跳着迪斯科学Java的博客-CSDN博客_6.830 lab4](https://blog.csdn.net/weixin_45834777/article/details/120999300)

[(163条消息) 6.830 Lab 4: SimpleDB Transactions_HearmingBear的博客-CSDN博客_simpledb transaction](https://blog.csdn.net/hjw199666/article/details/103674301)

#### Exercise 1：Granting Locks

> - 编写在缓冲池(BufferPool.java)中获取和释放锁的方法。假设使用的是页面级锁定，则需要完成以下操作：
>   - 修改getPage（）
>   - 实现unsafeReleasePage（）
>   - 实现holdsLock（）
> - 在通过LockingTest之前需要实现下一个测试

#### Exercise 2：Lock Lifetime

> - 在读取任何页面或者元组之前，需要获取共享锁
> - 在编写任何页面或者元组时，需要获取排他锁
> - 确保simpledb能够获取和释放锁
> - 在SeqScan期间从页面上读取元组（如果在BufferPool.getPage()中实现了锁定，只要您的HeapFile.iterator()使用BufferPool.getPage()就可以正常工作）
> - 通过 BufferPool 和 HeapFile 方法插入和删除元组（如果您在 BufferPool.getPage（） 中实现了锁定，只要 HeapFile.insertTuple（） 和 HeapFile.deleteTuple（） 使用 BufferPool.getPage（） 就可以正常工作。
> - 在以下情况下，您还需要特别考虑获取和释放锁：
>   - 将新页面添加到堆文件。何时将页面物理写入磁盘？与 HeapFile 级别（在其他线程上）的其他事务是否存在可能需要特别注意的争用条件，而不考虑页面级锁定？
>   - 寻找一个可以插入元组的空插槽。大多数实现会扫描页面以查找空插槽，并且需要READ_ONLY锁才能执行此操作。然而，令人惊讶的是，如果事务 t 在页面 p 上找不到空闲插槽，t 可能会立即释放 p 上的锁。虽然这显然与两阶段锁定的规则相矛盾，但没关系，因为 t 没有使用页面中的任何数据，因此更新 p 的并发事务 t' 不可能影响 t 的答案或结果。
> - 实现后，可以通过LockingTest

#### Exercise 3：Implementing NO STEAL

> - 事务仅在提交后写入磁盘。这意味着我们可以通过丢弃脏页并从磁盘重新读取它们来中止事务。因此，我们绝不能驱逐脏页。此策略称为NO STEAL。（脏页就是内存的数据和磁盘的数据不一致）
> - 您需要修改BufferPool的evictPage。特别是，它绝不能逐出脏页面。如果您的逐出策略更喜欢脏页面进行逐出，则必须找到一种方法来逐出替代页面。如果缓冲池中的所有页面都是脏的，则应抛出 DbException。如果逐出策略逐出干净的页面，请注意任何锁事务可能已经保留到逐出页面，并在实现中适当地处理它们。
> - 不丢弃脏页，如果该页被一个未提交事务所lock。当事务提交后，应该把脏页刷进磁盘。刷回磁盘后的页面的锁需要释放
> - 修改BufferPool的evictPage方法：
>   - 将脏页刷进磁盘
>   - 不允许驱逐脏页

#### Exercise 4：Transactions

> - 在 SimpleDB 中，在每个查询的开头创建一个 TransactionId 对象。此对象将传递给查询中涉及的每个运算符。查询完成后，将调用BufferPool类中的transactionComplete方法
> - 调用此方法会提交或中止事务，由参数标志 commit 指定。在执行过程中的任何时候，操作员都可能引发 TransactionAbortedException 异常，这表示发生了内部错误或死锁。我们为您提供的测试用例创建适当的 TransactionId 对象，以适当的方式将它们传递给运算符，并在查询完成后调用 transactionComplete。我们还实现了 TransactionId。
> - 简言之就是：
>   - commit时，将脏页刷进磁盘。释放状态，释放锁
>   - abort时，应该将页面恢复到磁盘状态。释放状态，释放锁
> - 实现后需要通过TransactionTest、系统测试的AbortEvictionTest

#### Exercise 5：DeadLocks and Aborts

> - SimpleDB中的transactions是有可能死锁的，需要实现对死锁的检测并抛出一个TransactionAbortedException异常。
> -  有很多种名方式检测死锁。比如，可以实现一个简单的**超时策略**，如果在既定时间内一个transaction没有完成，就抛弃这个transaction。另外，也可以依靠dependency graph data structure实现一个cycle-detection，可以随时在授予锁的时候检查这个图数据结构中**是否有环**，如果有就抛弃。
> - 在检测到死锁存在之后，需要考虑如何改善。如果在 transaction t等待一个锁释放时检测到死锁，如果你比较抓狂，可以抛弃掉t等待的所有transactions，这可能导致很大一部分工作没能完成，但能够保证t能够继续往下执行。相反的，你也可以选择抛弃t ，是的其他transactions有机会继续往下执行，这意味着在之后需要重新尝试执行transaction t
> - 在BufferPool.java中实现死锁预防，具体在getPage中加入超时检测代码
> - 实现后需要通过DeadlockTest、系统测试的TransactionTest



















