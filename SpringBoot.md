## SpringBoot In Action notes
* [1.自动配置](#1)
* [2.测试](#2)
* [3.Actuator](#3)

<h2 id = "1">1.自动配置</h2>
&emsp;&emsp; 自动配置时使用的条件化注解：
1.@ConditionalOnBean: 配置了某个特定的Bean。
2.@ConditionalOnMissingBean：没有配置特定的Bean。
3.@ConditionalOnClass：Classpath里有特定的类。
4.ConditionalOnMissingClass：Classpath里缺少指定的类。
5.@ConditionalOnExpression：给定的Spring Expression Language 表达式计算结果为true。
6.@ConditionalOnJava：java的版本匹配特定值或者一个范围值。
7.@ConditionalOnJndi：参数中给定的JNDI位置必须存在一个，如果没有给参数，则要有JNDI InitialContext。
8.@ConditionalOnProperty：指定的配置属性要有一个明确的值。
9.@ConditionalOnResource：Classpath里有指定的资源。
10.@ConditionalOnWebApplication：这是一个Web应用程序。
11.@ConditionalOnNotWebApplication：这不是一个Web应用程序。
<br>
&emsp;&emsp; SpringBoot的设计是加载应用级配置，随后考虑自动配置类。SpringBoot应用程序有多种设置途径，能够从多种属性资源获得属性，包括：
1.命令行参数。
2.java:comp/env里的JNDI属性。
3.JVM系统属性。
4.操作系统环境变量。
5.随机生成的带random.*前缀的属性(在设置其他属性时，可以引用它们，比如：${random.long})。
6.应用程序以外的application.properties或者application.yml文件。
7.打包在应用程序内的application.properties或者application.yml。
8.通过@PropertySource标注的属性源。
9.默认属性。
<br>
&emsp;&emsp; 这个列表按照优先级排序，也就是说任何在高优先级设置的属性都会覆盖低优先级设置的属性。
<br>
&emsp;&emsp; application.properties和application.yml文件能够放在以下位置：
1.外置，在相对于应用程序运行目录的/config子目录里。
2.外置，在应用程序运行的目录里。
3.内置，在config包内。
4.内置，在Classpath根目录。
<br>
&emsp;&emsp; /config子目录里的application.properties会覆盖应用程序Classpath里的application.properties中的相同属性。
此外如果在同一优先级位置有application.properties和application.yml，那么application.yml里的属性会覆盖application.properties里的属性。
<br>
&emsp;&emsp; 部分配置项:
1.server.port=8888 配置服务器的端口。
2.默认情况下，SpringBoot会使用Logback来记录日志，gradle里可以使用configurations { all*.exclude group: 'org.springframework.boot', module: 'spring-boot-starter-logging' }
&emsp;&emsp; 来排除默认日志的起步依赖。然后可以引入其他的日志：compile("org.springframework.boot:spring-boot-starter-log4j2")
3.可以使用logging.config=classpath:log-config-name.xml来引入日志配置。
4.配置数据源，spring.datasource.url(username、password、driver-class-name)=jdbc:mysql://localhost/xxx。
&emsp;&emsp; 通常SpringBoot会根据数据库URL识别出需要的驱动，但是如果识别出问题，可以设置driver-class-name属性。
&emsp;&emsp; 在自动配置DataSource Bean时，如果Classpath里有Tomcat的连接池，那么会使用它，否则会在Classpath里查找以下连接池：
&emsp;&emsp; HikariCP、Commons DBCP、Commons DBCP2。可以通过spring.datasource.jndi-name=xxx来设置连接池，
&emsp;&emsp; 一旦设置，其他的连接池就会被忽略。
5.可以通过@ConfigurationProperties进行属性注入。SpringBoot的属性解析器能够自动把驼峰规则的属性和使用连字符或下划线的同名属性关联起来，
&emsp;&emsp; a.userId 和 a.user_id 以及 a.user-id都是等价的。
6.使用Profile进行多环境配置。spring.profile.active属性能激活Profile。可以创建特定的属性文件，遵循application-{profile}.properties这种命名格式。
&emsp;&emsp; 特定环境的属性可以放在特定的配置文件中，通用的属性可以放在application.properties文件。

<h2 id="2">2.测试</h2>
&emsp;&emsp; @RunWith参数为SpringJUnit4ClassRunner.class，开启了Spring集成测试支持，@ContextConfiguration指定了如何加载应用上下文。
SpringJUnit4ClassRunner能通过自动织入从应用程序上下文里向测试本身注入Bean。@ContextConfiguration没能加载完整的SpringBoot，
SpringBoot最终由SpringApplication加载的，它不仅会加载应用程序上下文，还会开启日志、加载外部属性(application.properties)以及其他SpringBoot特性。
如果需要这些特性，需要使用@SpringApplicationConfiguration，它加载Spring应用程序上下文的方式同SpringApplication相同。
<br>
&emsp;&emsp; @WebIntegrationTest声明不仅SpringBoot会在测试时创建上下文，而且会启动一个嵌入式的Servlet容器。
可以用@WebIntegrationTest(randomPort=true)来启动随机端口，请求时使用@Value("${local.server.port}")来获取启动的那个端口。

<h2 id="3">3.Actuator</h2>
&emsp;&emsp; SpringBoot Actuator特性是在应用程序里提供众多的Web端点，通过它们来了解应用程序运行时的内部状况。Actuator端点如下：
1.GET /actoconfig 提供了一份自动配置报告，记录哪些自动配置条件通过了，哪些没有通过。
2.GET /configprops 描述配置属性(包含默认值)如何注入Bean。
3.GET /beans 描述应用程序上下文里所有的Bean，以及它们的关系。
4.GET /dump 获取线程活动的快照。
5.GET /env 获取全部环境属性。
6.GET /env/{name} 根据名称获取特定的环境变量。
7.GET /health 报告应用程序的健康指标，这些值由HealthIndicator的实现类提供。
8.GET /info 获取应用程序的定制信息，这些信息由info打头的属性提供。
9.GET /mappings 描述全部的URI路径，以及它们和控制器(包括Actuator端点)的映射关系。
10.GET /metrics 报告各种应用程序度量信息，比如内存用量和HTTP请求计数。
11.GET /metrics/{name} 报告指定名称的应用程序度量值。
12.POST /shutdown 关闭应用程序，要求endpoint.shutdown.enabled设置为true。
13.GET /trace 提供基本的HTTP请求跟踪信息(时间戳、HTTP头等)。
<br>
要启用Actuator端点，只需要在项目中引入Actuator起步依赖即可。compile 'org.springframework.boot:spring-boot-starter-actuator'
<br>
&emsp;&emsp; /beans 返回的所有Bean条目都有五类信息：
1.bean：Spring应用程序上下文中的Bean名称或ID。
2.resource: .class文件的物理位置，通常是一个URL，指向构建出的JAR文件。这会随着应用程序的构建和运行方式发生变化。
3.dependencies: 当前Bean注入的Bean ID列表。
4.scope: Bean的作用域(通常是单例，这也是默认作用域)。
5.type：Bean的Java类型。
<br>
&emsp;&emsp; /autoconfig 返回自动配置报告，包括为什么会有这个Bean以及为什么没有这个Bean。
<br>
&emsp;&emsp; /env 查看配置属性，会生成应用程序可用的所有环境属性的列表。包括环境变量、JVM属性、命令行参数、以及application.properties属性。
所有名为password、secret、key(或者名字中最后一段是这个)的属性在/env里都会加上"*"。环境变量可以通过@ConfigurationProperties注解使用，
/configprops端点会生成一个报告，说明如何进行配置。
<br>
&emsp;&emsp; /mappings 提供了一个列表，用来显示控制器到端点的映射关系。
<br>
&emsp;&emsp; /metrics 提供了应用程序运行时数据列表，包括：
1.垃圾收集器 gc.* 已经发生的垃圾收集次数，以及垃圾收集所耗费的时间，适用于标记-清理 垃圾收集器和并行垃圾收集器。
2.内存 mem.* 分配给应用程序的内存数量和空闲的内存数量。
3.堆 heap.* 当前内存用量。
4.类加载器 classes.* JVM类加载器加载与卸载的类的数量。
5.系统 processors、uptimeinstance.uptime、systemload.average 系统信息，处理器数量、运行时间、平均负载。
6.线程池 thraed.* 线程、守护线程的数量，以及JVM启动后的线程数量峰值。
7.数据源 datasource.* 数据源连接池的数量。
8.Tomcat会话 httpsessions.* tomcat活跃会话数和最大会话数。
9.HTTP counter.status.*、gauge.response.* 多种应用程序服务HTTP请求的度量值与计数器。
<br>
&emsp;&emsp; /trace 能报告所有Web请求的详细信息。包括请求方法、路径、时间戳以及请求和响应的头信息。/trace能显示最近100个请求的信息，
包含对/trace自己的请求。
<br>
&emsp;&emsp; /dump 会生成当前线程活动的快照。包含线程的很多特定信息，以及线程相关的阻塞和锁状态。
<br>
&emsp;&emsp; /health 会显示当前应用程序是否在健康运行。如果没有授权的请求只会返回健康状态，经过身份验证的会提供更多的信息。
可用磁盘空间以及应用程序正在使用的数据库的状态都可以看到。这些健康指示器会按需自动配置。健康指示器如下：
1.ApplicationHealthIndicator 永远为up。
2.DataSourceHealthIndicator key: db 如果数据库能连上，则内容是UP和数据库类型，否则为DOWN。
3.DiskSpaceHealthIndicator key: diskSpace 如果可用空间大于阀值，则内容为UP和可用磁盘空间，如果不足则为DOWN。
4.JmsHealthIndicator key: jms 如果能连上消息代理，则内容为UP和JMS提供方的名称，否则为DOWN。
5.MailHealthIndicator key: mail 如果能连上邮件服务器，则内容为UP和邮件服务器主机端口号，否则为DOWN。
6.MongoHealthIndicator key: mongo 如果能连上MongoDB服务器，则内容为UP和MongoDB服务器版本，否则为DOWN。
7.RabbitHealthIndicator key: rabbit 如果能连上RabbitMQ，则内容为UP和版本号，否则为DOWN。
8.RedisHealthIndicator key: redis 如果能连上服务器，则内容为UP和Redis服务器版本号，否则为DOWN。
9.SolarHealthIndicator key: solr 如果能连上Solr服务器，则内容为UP，否则为DOWN。
<br>
&emsp;&emsp; 除了REST端点和远程shell，Actuator可以把它的端点以MBean的方式发布出来，可以通过JMX来查看和管理。Actuator有多种定制方式：
1.重命名端点。
2.启用和禁用端点。
3.自定义度量信息。
4.创建自定义仓库来存储跟中数据。
5.插入自定义的健康指示器。
<br>
&emsp;&emsp; 默认情况下除了/shutdown 其他的端点都启用，endpoints.enabled=false 可以禁用所有的端点。自动配置允许Actuator创建CounterService实例，
并将其注册为Spring的应用程序上下文中的Bean，CounterService接口定义了三个方法，分别用来增加、减少、重置特定名称的度量值：
<p>

    public interface CounterService {
        void increment(String metricName);
        void decrement(String metricName);
        void reset(String metricName);
    }
</p>
&emsp;&emsp; Actuator还有GaugeService类型的Bean，能够将某个值记录到特定名称的度量值里。
<p>

    public interface GaugeService {
        void submit(String metricName, double value);
    }
</p>
&emsp;&emsp; 我们只需要注入这两个Bean，然后调用方法，更新度量值。对于难于通过增加计数器或记录指标值来捕获的度量值，
可以采用实现PublicMetric接口的方式，并在容器中注册实现类为Bean。
<p>
    
    public interface PublicMetrics {
        Collection<Metric<?>> metrics();
    }
</p>
<br>
&emsp;&emsp; 默认情况下，/trace端点报告的跟踪信息都存储在内存仓库里，100个条目封顶。一旦仓库满了就会移除老的条目。
可以通过配置类创建InMemoryTraceRepository Bean 来调整条目容量，也可以将条目存储在其他地方，只需要实现TraceRepository接口即可。
<br>
&emsp;&emsp; 可以通过实现一个HealthIndicator来自定义一个健康指示器。
