### 实验内容

主要是在simpledb上实现simpledb的日志系统，支持回滚和崩溃恢复。这些内容也是对应战得臣慕课靠后的部分的

lab6在lab5的基础上。

lab4的bufferpool实现的是no steal+force，但是现在的dbms普遍是steal+no force



#### 实现提示：

> - steal/no-steal: 是否允许一个uncommitted的事务将修改更新到磁盘，如果是steal策略，那么此时磁盘上就可能包含uncommitted的数据，因此系统需要记录undo log，以防事务abort时进行回滚（roll-back）。如果是no steal策略，就表示磁盘上不会存在uncommitted数据，因此无需回滚操作，也就无需记录undo log。
> - force/no-force:force策略表示事务在committed之后必须将所有更新立刻持久化到磁盘，这样会导致磁盘发生很多小的写操作（更可能是随机写）。no-force表示事务在committed之后可以不立即持久化到磁盘， 这样可以缓存很多的更新批量持久化到磁盘，这样可以降低磁盘操作次数（提升顺序写），但是如果committed之后发生crash，那么此时已经committed的事务数据将会丢失（因为还没有持久化到磁盘），因此系统需要记录redo log，在系统重启时候进行前滚（roll-forward）操作。
> - 为了支持steal/no-force策略，即我们可以将未提交事务的数据更新到磁盘，也不必在事务提交时就一定将修改的数据刷入磁盘，我们需要用日志来记录一些修改的行为。在simpledb中，日志不区分redo log和undo log，格式较为简单，也不会记录事务执行过程中对记录的具体修改行为。
> - 日志格式和checkpoint

lab6可以大致分为两大阶段：

> - Rollback（回滚）：是undo log需要做的事情
>   - 在回滚时将上一个版本的数据写回磁盘
> - Recovery（崩溃恢复）：是redo log需要做的事情
>   - 对于未提交的事务：使用before-image进行恢复
>   - 对于已提交的事务：使用after-image进行恢复

#### Exercise 1：LogFile.rollback()

> - 实现LogFile.rollback()
> - 实现后需要通过系统测试LogTest的TestAbort和TestAbortCommitInterleaved子测试

#### Exercise 2：LogFile.recover()

> - outlint很重要（提供了思路）
> - 实现后需要通过系统测试LogTest

指导书很重要。



原来lab5中的heapfile和bufferpool写的可能有点问题，导致过不去。

参考代码：

> - [1345414527/MIT6.830: MIT6.830要我们基于java语言实现一个数据库系统，一共6个lab。 (github.com)](https://github.com/1345414527/MIT6.830)  的bufferpool和heapflile。因为其采用的是lru，并且在bufferpool里用到了锁机制，因此，还新加了LRUCache类、Lock类、LockManager类
> - [(163条消息) MIT6.830 lab6 Rollback and Recovery 实验报告_跳着迪斯科学Java的博客-CSDN博客_mit6实验报告](https://blog.csdn.net/weixin_45834777/article/details/121306173)  rollback和recover











