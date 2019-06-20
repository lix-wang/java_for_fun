## Spring framework 5 doc notes
* [1.模块](#1)
* [2.IOC和Beans](#2)
* [3.环境抽象](#3)
<h2 id="1">1.模块</h2>
&emsp;&emsp; Spring 核心容器由spring-core、spring-beans、spring-context、spring-context-support、spring-expression组成。
spring-core和spring-beans模块提供了框架的基础功能，包括ioc和依赖注入。BeanFactory是一个成熟的工厂模式的实现，可以把依赖关系的配置和描述从程序中解耦。
spring-context建立在core和beans基础上。提供一个框架式的对象访问方式。spring-expression提供了表达式语言支持查询和操作运行时对象图。
spring-aop提供了面向方面的编程实现，允许自定义方法拦截器和切入点。
数据访问／集成模块由JDBC、ORM、OXM、JMS和实物模块组成。spring-jdbc提供了JDBC抽象层，消除了JDBC编码和数据库厂商特有的错误代码解析。
spring-tx支持用以实现特殊接口和所有POJO类的编程和声明式事务管理。spring-orm为对象关系映射API提供集成层。spring-oxm提供支持对象／XML映射实现的抽象层。
spring-jms包含用以生产和消费信息的功能。web层由spring-web、spring-webmvc和spring-websocket组成。spring-web提供基本的面向web的集成功能，
以及初始化了一个使用Servlet侦听器和面向Web的应用程序上下文ioc容器，还包括一个HTTP客户端和spring的远程支持的web相关部分。
spring-webmvc提供了web应用程序的MVC和REST实现。

<h2 id="2">2.IOC和Beans</h2>
&emsp;&emsp; 日志是Spring唯一的强制性外部依赖关系。IOC描述了对象的定义和依赖的一个过程，依赖的对象通过构造参数、工厂方法参数或者一个属性注入，
当对象实例化后依赖的对象才被创建，当创建bean后，容器注入这些依赖对象。
<br>
&emsp;&emsp; ApplicationContext接口代表了IOC容器，负责实例化、配置、组装beans。Spring提供几个开箱即用的ApplicationContext接口的实现类，
通常创建一个ClassPathXmlApplicationContext或FileSystemXmlApplicationContext实例对象。
<br>
<h3>@Configuration && @Bean</h3>
&emsp;&emsp; 被@Configuration注解的类通过简单的在调用同一个类中其他的@Bean方法来定义bean之间的依赖关系。当@Bean在没有使用@Configuration注解的类中声明时，
被以"lite"方式处理，用@Component修饰的类或普通类中都是以"lite"模式。lite @Bean 方法不能简单的在类内部定义依赖关系。在lite模式下，@Bean方法不应该调用其他的@Bean方法。
在@Configuration注解中使用@Bean采用"full"模式，可以防止同样的@Bean方法被意外调用多次。
<br>
&emsp;&emsp; @Configuration是@Component的一个元注解，对于component的扫描，@Configuration注解类会自动成为候选者。


<h3>AnnotationConfigApplicationContext</h3>
&emsp;&emsp; 这个通用的ApplicationContext可以接受@Configuration注解也可以接受JSR-330元数据注解的简单类和@Component。
可以通过register(Class<?>...) 也可以通过scan(String...)组件扫描，此时相当于@Configuration和@ComponentScan。

<h3>AnnotationConfigWebApplicationContext</h3>
&emsp;&emsp; 这是AnnotationConfigApplicationContext 在WebApplication中的变体。配置ContextLoaderListener、Servlet监听器、DispatchServlet的时候，
可以用这个来实现。

<h3>@Bean</h3>
&emsp;&emsp; @Bean支持init-method、destroy-method、autowiring和name。默认情况下bean名称与方法名称相同。@Bean方法可以具有描述构建bean所需依赖关系的任意数量的参数。
就是说@Bean方法可以有依赖关系参数，这种参数的注入与构造函数依赖注入相同。@Bean支持@Scope注解，默认的作用域是单例。@Bean可以通过name接受String数组声明别名。
可以利用@Description注解对Bean添加描述。
<br>
&emsp;&emsp; @Bean支持方法查找注入，通过在@Bean创建时指明抽象方法的具体实现。可以参考我项目中 ActuatorInterceptorConfiguration 校验Actuator访问权限的拦截器配置。
@Configuration和@Bean使用CGLIB的方式进行子类实例化，如果想移除依赖注入限制，可以采用@Component + @Bean。

<h3>@Import</h3>
&emsp;&emsp; @Import注解允许从另一个配置类加载@Bean定义。小心通过@Bean的BeanPostProcessor、BeanFactotyPostProcessor定义，
应该被声明为static的@Bean方法，不会触发包含它们的配置类的实例化，否则，@Autowired和@Value将在配置类上不生效。
<br>
&emsp;&emsp; @Configuration 和@Bean可以定义在interface method 或 abstract class method中，用以抽象@Bean的实例化，解耦。
可以在@Bean上使用@Profile注解，@Profile基于@Conditional注解实现，根据matches()方法判断是否具备创建该@Bean的条件。
以java为中心，使用AnnotationConfigurationApplicationContext 和 @ImportResource注解来引入所需的XML。

<h2 id="3">3.环境抽象</h2>
&emsp;&emsp; @Profile可以声明在类上，也可以声明在方法上，根据不同的环境，创建不同的Bean。profile可以使用default，
看作是对一个或者多个bean提供了一种默认的定义方式，如果启用任何profile，那么默认的profile都不会被应用。
判断当前环境中是否有某个配置，需要对一组PropertySource进行搜索，Spring标准环境是由两个PropertySource配置的，一个表示一系列的JVM系统属性(System.getProperties())，
一个表示一系列的环境变量(System.getenv())。这些默认的属性资源，存在于StandardEnvironment。boolean Environment.containsProperty("foo")。
系统属性优于环境变量。搜索时按照层级结构，属性值不会被合并而是被之前的值覆盖，优先级如下：
1.ServletConfig参数（例如DispatcherServlet上下文环境）。
2.ServletContext参数（Web.xml中的context-param）。
3.JNDI环境变量（"java:comp/env/"）。
4.JVM系统属性（"-D命令行参数"）。
5.JVM系统环境变量（操作系统环境变量）。
<br>
&emsp;&emsp; @PropertySource注解对添加一个PropertySource到Spring环境中提供了一个便捷和声明式的机制。
可以发现，最开始我通过System.setProperty("management.endpoints.web.exposure.include", "*")的方式来开启Actuator，
现在可以通过BizAutoConfiguration中设置@PropertySource注解的方式来开始Actuator。