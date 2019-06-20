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

<h3>@PropertySource</h3>
&emsp;&emsp; @PropertySource注解对添加一个PropertySource到Spring环境中提供了一个便捷和声明式的机制。
可以发现，最开始我通过System.setProperty("management.endpoints.web.exposure.include", "*")的方式来开启Actuator，
现在可以通过BizAutoConfiguration中设置@PropertySource注解的方式来开始Actuator。出现在@propertySource中的资源位置占位符，
都会被注册在环境变量中的资源解析。

<h3>加载时编织器LoadTimeWeaver</h3>
&emsp;&emsp; 在类被加载进JVM时Spring使用LoadTimeWeaver进行动态转换。为了使加载时编织器可用，需要在@Configuration类上添加@EnableLoadTimeWeaving。

<h3>ApplicationContext</h3>
&emsp;&emsp; org.springframework.beans.factory包提供基本的功能来管理和操作bean。org.springframework.context包增加了ApplicationContext接口，
继承了BeanFactory接口。ApplicationContext提供了以下功能：
1.通过MessageSource接口访问i18n风格的消息。
2.通过ResourceLoader接口访问类似URL和文件资源。
3.通过ApplicationEventPublisher接口，即bean实现ApplicationListener接口进行事件发布。
4.通过HierarchicalBeanFactory接口实现加载多个上下文，允许每个上下文只关注特定的层，例如web层。
<br>
&emsp;&emsp; 当ApplicationContext被载入的时候，会自动的在上下文中去搜索名称为messageSource的MessageSource Bean。
如果找到，则方法的调用都会委托给消息源。如果没有，ApplicationContext会尝试找一个同名的父消息源，如果没找到任何消息源，
那一个空的DelegatingMessageSource将被实例化。

<h3>事件</h3>
&emsp;&emsp; ApplicationEvent和ApplicationListener接口提供了ApplicationContext中的事件处理。如果一个Bean实现了ApplicationListener接口，
并被部署到上下文中，那么每次ApplicationEvent发布到ApplicationContext中时，bean都会收到通知，是观察者模型。
<br>
&emsp;&emsp; 内置的事件如下：
1.ContextRefreshedEvent 当ApplicationContext被初始化或者刷新的时候发布。
2.ContextStartedEvent 当ApplicationContext启动时发布，在ConfigurableApplicationContext接口上调用start()方法。
3.ContextStoppedEvent 当ApplicationContext停止发布，在ConfigurableApplicationContext接口上调用stop()方法。
4.ContextClosedEvent 当ApplicationContext关闭发布，在ConfigurableApplicationContext接口上调用close()方法。
5.RequestHandledEvent 接受一个HTTP请求时，一个特定的web时间会通知所有的bean，仅适用适用Spring的DispatcherServlet的Web应用程序。
<br>
&emsp;&emsp; 自定义的事件可以参考springboot-common-framework模块中的Actuator事件用法。