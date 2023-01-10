super注意点：

1. super调用父类的构造方法，必须在构造方法的第一个。this也必须在构造方法的第一个
2. super必须只能出现在子类的方法或者构造方法中
3. super和this不能同时调用构造方法



重写：需要有继承关系，子类重写父类的方法！

1. 方法名必须相同
2. 参数列表必须相同
3. 修饰符：范围可以扩大：public>protected>Default>private
4. 抛出的异常：范围，可以被缩小，但不能扩大：ClassNotFoundException（小） --> Exception（大）

重写：子类的方法和父类必须要一致，方法体不同



Java的三大特性：封装、继承、多态

属性和方法都没有重写和多态

java多态：Person（父类）、Student（子类）

多态的条件：继承、重写

不能多态的情况：

1. static方法属于类，它不属于实例
2. final常量
3. private方法



进行对象之间的强制类型转换时可以先用instanceof来比较这两个对象是否有链式继承关系

object > Person > Teacher

object > String

X instanceof Y是否编译通过取决于X、Y之间是否有父子关系



static在多线程中使用



类是单继承

接口可以多继承



抽象类可以存在普通方法

抽象类有构造方法



接口是比抽象类还抽象的东西

接口中的方法都是public abstract

接口中的变量都是public static final



接口没有构造方法



成员内部类：可以访问外部类的私有属性和私有方法

静态内部类

局部内部类，在方法里定义的类

匿名内部类





多线程

lambda表达式

注解

反射

异常的最高类型是throwable

