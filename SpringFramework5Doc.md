## Spring framework 5 doc notes
* [1.模块](#1)
* [2.IOC和Beans](#2)
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
### @Configuration && @Bean
&emsp;&emsp; 被@Configuration注解的类通过简单的在调用同一个类中其他的@Bean方法来定义bean之间的依赖关系。当@Bean在没有使用@Configuration注解的类中声明时，
被以"lite"方式处理，用@Component修饰的类或普通类中都是以"lite"模式。lite @Bean 方法不能简单的在类内部定义依赖关系。在lite模式下，@Bean方法不应该调用其他的@Bean方法。
<br>
&emsp;&emsp; 在@Configuration注解中使用@Bean采用"full"模式，可以防止同样的@Bean方法被意外调用多次。

###AnnotationConfigApplicationContext
&emsp;&emsp; 这个通用的ApplicationContext可以接受@Configuration注解也可以接受JSR-330元数据注解的简单类和@Component。
可以通过register(Class<?>...) 也可以通过scan(String...)组件扫描，此时相当于@Configuration和@ComponentScan。

###AnnotationConfigWebApplicationContext
&emsp;&emsp; 这是AnnotationConfigApplicationContext 在WebApplication中的变体。配置ContextLoaderListener、Servlet监听器、DispatchServlet的时候，
可以用这个来实现。