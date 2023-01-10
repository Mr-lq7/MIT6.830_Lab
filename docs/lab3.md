### 实验内容

主要是在simpledb上实现查询优化器。主要任务包括实现选择性估计框架和基于成本的优化器。（执行计划、代价估计、优化）。

通过统计直方图帮助查询优化器找到更优的执行计划。

lab3在lab2的基础上。

#### 实现提示：

> - 在tablestats类中实现方法，使其能够使用直方图（IntHistogram类提供骨架）来估计过滤器和扫描成本的选择
> - 在JoinOptimizer类中实现方法，使其能够估算加入的成本和选择性
> - 在JoinOptimizer类中实现orderJoins方法。此方法必须为一系列连接生成最佳排序（例如**Selinger 算法**），给定在前两个步骤中计算的统计信息

#### 优化器概述：

> - 基于成本的优化器的主要思想：
>   - 使用有关表的统计数据来估计不同查询计划的“成本”。通常，计划的成本与中间连接和选择（产生的元素数量）以及过滤器和加入谓词的选择性有关（解析器）
>   - 使用这些统计信息以最佳方式对连接和选择进行排序，并从多个备选方案中为连接算法选择最佳实现（优化器）

simpledb解析器和优化器的总体控制流如下所示：

![controlflow](D:\WorkSpace\Lab\MIT830\lab3.assets\controlflow.png)



[(162条消息) MIT6.830 Lab3 Query Optimization 实验报告_跳着迪斯科学Java的博客-CSDN博客_mit6.830 lab3](https://blog.csdn.net/weixin_45834777/article/details/120788433)

lab3可以大致分为两阶段：

> - 收集表的统计信息，有了统计信息才可以进行估计
> - 根据统计信息进行估计，找出最优的执行方案

#### Exercise 1：初始化直方图（IntHistogram.java）

> - outline很重要（提供了思路）
> - 实现记录表统计信息以进行选择性估计
> - 实现后需要通过IntHistogramTest单元测试

#### Exercise 2：表格统计（TableStats.java）

> - outlint很重要（提供了思路）
> - TableStats类中包含计算表中元组和页面数量的方法，并估算了该表谓词的选择性。创建的查询解析器为每张表创建了一个TableStats实例，并将这些结构传递到查询优化器中
> - 实现后需要通过TableStatsTest

#### Exercise 3：连接成本代价估计（Join Cost Estimation）

> - outline很重要，看懂计算公式
> - JoinOptimizer类包括用于对连接进行排序和计算开销的所有方法。需要编写用于估计连接的选择性和成本的方法
> - 实现后需要通过JoinOptimizerTest.java中的estimateJoinCostTest和estimateJoinCardinalityTest

一般来说是小表驱动大表。

#### Exercise 4：连接排序（Join Ordering）

> - 在实现了估算成本的方法后，将实现Selinger优化器。对于这些方法，连接表示为连接节点的列表，而不是类中描述的要连接的关系列表
> - 实现后需要通过JoinOptimizerTest单元测试，并且需要通过系统测试QueryTest



看看帆船书（数据库系统概念）















