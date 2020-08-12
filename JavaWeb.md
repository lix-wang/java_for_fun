## Java Web
* [1.Web请求过程](#1)
* [2.Java IO工作机制](#2)
* [3.Java Web 编码问题](#3)
* [4.Java编译原理](#4)
* [5.ClassLoader](#5)


<h2 id="1">1.Web请求过程</h2>
&emsp;&emsp; HTTP请求时，需要通过DNS解析出对应的IP地址，大致分为10个步骤，1.首先在浏览器缓存中查找域名对应的IP地址，如果有缓存，则解析过程结束。
2.浏览器缓存中没有，则查找操作系统缓存中是否有对应的DNS解析结果，通常在hosts文件中。3.将域名发送给LDNS(Local DNS)，即本地域名服务器。
4.如果在LDNS中没有命中，那么会直接到Root Server域名服务器请求解析。5.根域名服务器返回给本地域名服务器一个所查询域的主域名服务器(gTLD Server)地址，
gTLD是国际顶级域名服务器，如com、cn、org等。6.本地域名服务器向gTLD服务器发送请求。7.接受请求的gTLD服务器查找并返回此域名对应的Name Server域名服务器地址，
这个Name Server通常是你注册的域名服务器。8.本地域名服务器查询 Name Server域名服务器，会查询存储的域名和IP的对应关系，正常情况下返回对应IP和一个TTL值。
9。Local DNS Server会缓存这个域名和IP的对应关系，缓存的时间由TTL控制。10.Local DNS Server把解析结果返回给客户端，用户根据TTL缓存在本地系统中。

<h2 id="2">2.Java IO工作机制</h2>
### 2.1 文件访问方式
#### 2.1.1 标准访问文件的方式
&emsp;&emsp; 调用read()时，操作系统在内核的高速缓存中检查是否有需要的数据，如果已经缓存，那么直接从缓存中返回，否则从磁盘中读取，然后缓存在操作系统缓存中。
写入时调用write() 将数据从用户地址空间复制到内核地址空间的缓存中，这时对用户程序写操作已经完成，什么时候写入到磁盘中由操作系统决定，除非显式调用了sync命令。

    <p>
        read()              write()
          ^                    |
          |                    V
       应用缓存  用户空间地址  应用缓存
          ^                    |
          |                    V
      高速页缓存  内核地址空间 高速页缓存      
          ^                    |
          |                    |
          |_______物理磁盘______V
    </p>
    
#### 2.1.2 直接IO的方式
&emsp;&emsp; 直接IO的方式是应用程序直接访问磁盘数据，不经过操作系统内核数据缓冲区，这样能减少一次从内核缓存区到用户程序缓存的数据复制。
直接IO如果访问的数据不在应用程序缓存中，那么每次数据都直接从磁盘加载，这样加载会很慢，通常直接IO和异步IO结合使用性能更好。
操作系统只是简单的缓存最近一次从磁盘中读取的数据。

#### 2.1.3 同步访问文件的方式
&emsp;&emsp; 同步访问文件与标准访问文件的差别是读取写入都是同步的，只有当数据被成功写到磁盘时才返回成功标识。这种访问文件的方式性能较差。

#### 2.1.4 异步访问文件的方式
&emsp;&emsp; 异步访问文件的方式是访问数据的请求发出后，继续执行处理其他事情，当请求的数据返回后继续处理下面操作。

#### 2.1.5 内存映射的方式
&emsp;&emsp; 内存映射方式是将操作系统内存中某块区域与磁盘文件关联起来，当访问内存中的一段数据时，转换为访问文件的某一段数据。
这种方式可以减少数据从内核空间缓存到用户空间缓存的数据复制操作。

### 2.2 Java访问磁盘文件
&emsp;&emsp; 数据在磁盘中唯一最小描述是文件，文件是操作系统和磁盘驱动器交互的最小单元。Java中File不代表真实的文件对象，当指定路径描述符时，
返回代表这个路径的虚拟对象。创建FileInputStream时会创建一个FileDescriptor对象，这个对像代表一个真正存在的文件对象的描述。需要读取的时字符格式，
但是存储的时字节格式，因此StreamDecoder会将byte解码为char格式。

### 2.3 网络IO工作机制
&emsp;&emsp; 首先客户端会创建一个Socket实例，操作系统将这个Socket实例分配一个没有被使用的本地端口号，服务端在接收到请求时，会创建一个新的套接字数据结构，
关联到ServerSocket实例的一个未完成的连接数据结构列表中。这时服务端对应的Socket并未完成创建，等3次握手结束，服务端Socket实例会返回，
并将这个Socket实例对应的数据结构从未完成列表中移到已完成列表中。

<br>
&emsp;&emsp; 建立连接后，客户端服务器端都会有Socket实例，每个Socket实例都有一个InputStream和一个OutputStream，创建Socket时，会为InputStream和OutputStream
分配一定大小的缓存区，数据的读写都是通过缓冲区完成的。写入端将数据写入到OutputStream对应的SendQ队列中，队列填满时数据转移到另一端InputStream的RecvQ队列中，
如果RecvQ满了，那么OutputStream的write方法会阻塞。如果两边同时传数据，可能产生死锁。

<br>
&emsp;&emsp; 使用ByteBuffer.allocateDirector(size) 获取DirectByteBuffer，DirectByteBuffer是与底层存储空间关联的缓冲区，
每次在创建或者释放时都会调用一次System.gc()。DirectByteBuffer不需要在用户地址空间和操作系统内核地址空间复制数据。

<br>
&emsp;&emsp; NIO 提供FileChannel.transferTo、FileChannel.transferFrom 和 FileChannel.map两种比传统文件访问更好的方法。
FileChannel.transferXXX可以减少数据从内核到用户空间的复制，数据直接在内核空间中移动。FileChannel.map 将文件按照一定大小块映射为内存区域，
当程序访问内存区域时，直接操作文件数据，减少了数据从内核向用户用户空间复制的损耗，这种方式适合对大文件的只读操作。

### 2。4 网络IO优化
&emsp;&emsp; 1.减少网络交互的次数，在网络交互的两端设置缓存、合并请求等。2.减少网络传输数据量的大小，压缩数据等。3.尽量减少编码，网络IO中传输都是以字节进行的，
也就是通常要序列化，但我们发送的数据是字符形式的，从字符到字节必须编码。编码也比较耗时，因此尽量直接以字节形式发送，也就是提前将字符转换为字节，或者减少字符到字节的转换过程。

<h2 id="3">3.Java Web编码问题</h2>
&emsp;&emsp; 计算机中存储信息的单位为字节，所能表示的字符范围为0～255；人类需要表示的字符很多，无法用一个字节来表示。要解决这些矛盾，
需要有一个新的结构char，从char到byte需要编码。

### 3.1编码格式
#### 3.1.1 ASCII码
&emsp;&emsp; ASCII码总共128个，通过低7位表示。

#### 3.1.2 IOS-8859-1
&emsp;&emsp; IOS-8859-1是单字节编码，总共能表示256个字符。

#### 3.1.3 GB2312
&emsp;&emsp; 是双字节编码，编码范围是A1-F7，A1-A9是符号区，共包含682个符号，B0-F7是汉字区，包含6763个汉字。

#### 3。1.4 GBK
&emsp;&emsp; GBK是GB2312的扩展，总共有23940个码位。兼容GB2312。

#### 3.1.5 UTF-16
&emsp;&emsp; UTF-16定义了Unicode在计算机中的存取方法，UTF-16用两个字节采用定长的表示方法，定长简化了字符串的操作。Java以UTF-16作为内存的字符的存储格式。

#### 3.1.6 UTF-8
&emsp;&emsp; UTF-16缺点在于部分字符用一个字节就可以表示，现在用两个字节存储就浪费了存储空间，而且增大了网络传输流量，UTF-8采用变长技术，
规则为：1.如果是一个字节，那么最高位为0，表示这是一个ASCII字符，可见所有的ASCII都是UTF-8。2.如果是一个字节，以11开头代表是双字节UTF-8字符的首字母。
3.如果是一个字节，以10开始，表示不是首字节，需要向前查找才能得到当前字符的首字节。UTF-8中，一个英文占1字节，少数汉字占3个字节，多数占用4个字节。
UTF-8使用1-4个字节表示符号。

### 3.2 URL编解码
#### 3.2.1 HTTP Header编解码
&emsp;&emsp; header byte到char的默认编码是IOS-8859-1，所以如果Header中含有非ASCII码字符，那么解码会产生乱码。添加Header时，
需要对字符进行编码，然后在使用时，根据对应的字符集进行解码即可。

#### 3.2.2 POST表单的编解码
&emsp;&emsp; POST表单的参数传递方式是通过HTTP的BODY传递的。需要在第一次使用request.getParameter前设置request.setCharacterEncoding，
否则可能出现乱码。针对multipart/form-data类型的参数，同样使用content-type字符集编码，上传文件是通过字节流的方式传输到服务器的本地临时目录，
这个过程不涉及字符编码，真正编码是将文件内容添加到parameter中时，如果用这个不能编码，那么会使用默认编码ISO-8859-1编码。

<h2 id="4">4.Java编译原理</h2>
&emsp;&emsp; javac编译器是将符合Java语言规范的源代码转换成符合Java虚拟机规范的Java字节码。1，首先一个个字节的读取源代码，找到语法关键字，
如"if"、"while"等，这个步骤是词法分析过程。2，接着是对这些Token流进行语法分析，检查这些关键字组合在一起是不是符合Java语言规范，语法分析的
结果是形成一个符合Java语言规范的抽象语法树，抽象语法树是一个结构化的语法表达形式。3，接下来是语义分析，语义分析的结果就是将复杂的语法转换成最简单的语法，
最后形成一个注解过的抽象语法树。4，最后通过字节码生成器生成字节码，根据经过注解的抽象语法树生成字节码。

<br>
&emsp;&emsp; javac的各个模块完成了将java源代码转换成java字节码的任务，所以javac主要有4个模块：词法分析器、语法分析器、语义分析器、代码生成器。

<h2 id="5">5.ClassLoader</h2>
&emsp;&emsp; ClassLoader是用来加载Class的，负责将Class的字节码转换成内存形式的Class对象。JVM中内置了三个ClassLoder：BootstrapClassLoader、
ExtClassLoader、AppClassLoader。BootstrapClassLoader负责加载JVM运行时核心类，这些类位于$JAVA_HOME/lib/rt.jar文件中，这个ClassLoader称为根加载器。
ExtClassLoader负责加载JVM扩展类，位于$JAVA_HOME/lib/ext/*.jar中，有很多jar包。AppClassLoader是直接面向用户的加载器，会加载classpath环境变量里定义路径中的jar包和目录，
通常我们编写的代码及使用的第三方jar包都由它来加载。

<br>
&emsp;&emsp; ExtClassLoader 和 AppClassLoader都是URLClassLoader的子类，都是从本地文件系统里加载类库。ClassLoader采用双亲委派机制，
AppClassLoader在加载类时，并不会去找classpath，而是首先将这个类的名称交给ExtClassLoader，如果ExtClassLoader可以加载成功，
那么AppClassLoader就不会去搜索classpath。而ExtClassLoader加载类时，不会首先去搜索ext路径，首先会把类名称交给BootstrapClassLoader加载，
如果BootstrapClassLoader可以加载，那么Ext就不会再去加载。

<br>
&emsp;&emsp; ClassLoader有三个很重要的方法：loadClass()、findClass()、defineClass()。

        <p>
            Class<?> loadClass(String name) {
                // 判断目标class是否已经加载
                Class<?> c = findLoadedClass(name);
                if (c == null) {
                    if (parent != null) {
                        // 使用parent ClassLoader进行加载
                        c = parent.loadClass(name);
                    } else {
                        // 如果没有parent classLoader加载器，使用bootstrap classloader加载
                        c = findBootstrapClassOrNull(name)
                    }
                }
                // 如果parent classLoader无法加载
                if (c == null) {
                    // 自己加载Class
                    c = findClass(name);
                }
            }
            
            Class<?> finsClass(String name) {
                // 首先，获取目标Class的字节码，然后调用defineClass 加载Class
                return defineClass(String name, byte[] b, int off, int len)
            }
        </p>