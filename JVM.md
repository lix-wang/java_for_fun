## JVM
* [1.JVM Classloading Mechanism](#1)
* [1.1 Loading](#1.1)
* [1.2 Verification](#1.2)
* [1.3 Preparation](#1.3)
* [1.4 Resolution](#1.4)

<h2 id = "1">1.JVM Classloading Mechanism</h2>
&emsp;&emsp; 类的整个生命周期包括7个阶段：加载(Loading)、验证(Verification)、准备(Preparation)、解析(Resolution)、
初始化(Initialization)、使用(Using)、卸载(Unloading)。其中验证(Verification)、准备(Preparation)、解析(Resolution)，
3个阶段统称为连接(Linking)。
<br>
&emsp;&emsp; 其中加载(Loading)、验证(Verification)、准备(Preparation)、初始化(Initialization)、卸载(Unloading)，
这5个阶段顺序是确定的，因为可能采用动态绑定。遇到new、getstatic、publicstatic、invokestatic，如果类没有初始化，那么要先触发初始化。
场景为：使用new实例化对象、读取或设置一个类的静态字段（被final修饰，在编译期已经把结果放入常量池的静态字段除外）、调用一个类的静态方法、
使用java.lang.reflect包的方法对类进行反射调用的时候、当初始化一个类的时候，其父类还没有初始化、虚拟机启动时，
会先初始化主类（包含main()的类）。这些行为被称作对一个类的主动引用。
<br>
&emsp;&emsp; 接口也会初始化，接口中不能使用静态语句块，但仍会有<clinit>()类构造器，接口在初始化时，并不要求父接口全部都完成了初始化，
只有在真正使用到父接口是，才会初始化。

<h3 id = "1.1">1.1 Loading</h3>
&emsp;&emsp; 加载阶段完成以下3件事：1.通过一个类的全限定名来获取定义该类的二进制字节流。
2.将字节流所代表的静态存储结构转化为方法区的运行时数据结构。3.在内存中生成一个代表这个类的Class对象，作为方法区这个类的各种数据入口。

<h3 id = "1.2">1.2 Verification</h3>
&emsp;&emsp; 验证是连接阶段的第一步，为了确保Class文件的字节流中包含的信息符合当前虚拟机的要求，不会危害虚拟机自身的安全。
会完成以下4个阶段的检验动作：文件格式验证、元数据验证、字节码验证、符号引用验证。
<br>
&emsp;&emsp; 文件格式验证是基于二进制字节流进行的，只有通过这个阶段的验证后，字节流才会进入内存的方法区中进行存储。后面的阶段都是基于方法区中的存储结构进行的，
不会再直接操作字节流。
<br>
&emsp;&emsp; 元数据验证，主要对类的元数据信息进行校验，确保不存在不符合Java语言规范的元数据信息。
<br>
&emsp;&emsp; 字节码验证，通过数据流和控制流分析，确保程序语义是合法的、符合逻辑的。将对类的方法体进行校验，保证不会作出危害虚拟机的事件。
<br>
&emsp;&emsp; 符号引用验证，判断引用的类、方法、字段等的引用合法性，发生在虚拟机将符号引用转化为直接引用时，在解析阶段发生。

<h3 id = "1.3">1.3 Preparation</h3>
&emsp;&emsp; 准备阶段是正式为类变量分配内存并设置类变量初始值的阶段，这些变量使用的内存都在方法区中进行分配。
例如：public static int value = 123; 这个类变量在准备阶段之后，值为0，而不是123。因为这时候尚未开始执行任何Java方法，
真正赋值是在程序被编译后，执行类构造器<clinit>()方法时完成赋值。赋值动作将会在初始化阶段才会执行。
如果是常量，那么在准备阶段会被初始化为指定的值，如：public static final int value = 123; 此时准备阶段，会赋真值。

<h3 id = "1.4">1.4 Resolution</h3>