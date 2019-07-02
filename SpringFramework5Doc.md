## Spring framework 5 doc notes
* [1.模块](#1)
* [2.IOC和Beans](#2)
* [3.环境抽象](#3)
* [4.ApplicationContext](#4)
* [5.资源](#5)
* [6.验证、数据绑定和类型转换](#6)
* [7.O/X映射器](#7)



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

<h2 id="4">4.ApplicationContext</h2>
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
&emsp;&emsp; 自定义的事件可以参考springboot-common-framework模块中的Actuator事件用法。也可以通过在Bean方法上加@EventListener来处理事件，
具体用法可以参考AnnotatedActuatorNotifier。如果希望异步处理事件，可以在事件处理方法上添加@Async注解。可以通过@Order注解来设置监听的顺序。

<h3>BeanFactory</h3>
&emsp;&emsp; BeanFactory仅仅被用于和第三方框架的集成，目的就是为了让大量的第三方框架和Spring集成时保持向后兼容。ApplicationContext包括了BeanFactory所有的功能。

<h2 id="5">5.资源</h2>
<h3>Resource接口</h3>
&emsp;&emsp; Spring Resource接口getInputStream()：定位并打开当前资源，返回当前资源的InputStream。每次调用都返回一个新的InputStream。
exists() 判断当前资源是否存在。isOpen() 判断当前资源是否是一个已经打开的输入流，如果为true，返回的InputStream不能多次读写，
只能一次读取后关闭InputStream，防止内存泄露。除了InputStreamResource，其他的常用Resource都会返回false。
getDescription() 返回当前资源的描述，当处理资源出错时，用以错误信息的输出。资源的描述是一个完全限定的文件名称或者当前资源的真实url。
<br>
&emsp;&emsp; Spring内置了很多开箱即用的Resource实现：
1.URLResource 封装了一个java.net.URL对象，用来访问URL可以正常访问的任意对象。可以显式的使用UrlResource构造函数创建UrlResource对象。
也可以通过一个代表路径的String参数隐式的创建UrlResource。对于后者会由PropertyEditor决定创建UrlResource的类型，如果路径包含classpath前缀等，
会根据前缀创建合适的Resource，如果无法识别前缀，则会当作标准URL创建UrlResource。
2.ClassPathResource 可以从类路径上加载资源。可以使用线程上下文加载器、指定加载器或者指定的class类型来加载资源。资源存在于文件系统时，
ClassPathResource以java.io.File的形式访问，当类路径上资源尚未解压，处于jar包中，不支持java.io.File访问，Spring中各Resource都支持java.net.URL。
3.FileSystemResource 针对java.io.File提供的Resource实现。使用FileSystemResource的getFile() 获取File对象，通过getURL()获取URL对象。
4.ServletContextResource 为了获取web根路径的ServletContext资源而提供的resource实现。
5.InputStreamResource 针对InputStream提供的Resource实现。尽量使用ByteArrayResource或其他基于文件的Resource实现来代替。
除了在需要获取资源描述符或者需要从输入流多次读取时，都不要使用InputStreamResource来读取资源。
6.ByteArrayResource 针对字节组提供的Resource实现，当需要从字节数组加载内容时，可以使用。

<h3>ResourceLoader接口</h3>
&emsp;&emsp; 用来加载Resource对象，当一个对象需要获取Resource实例时，选择ResourceLoader接口。Spring所有应用上下文都实现了ResourceLoader接口，
所以所有的应用上下文都可以通过getResource()来获取Resource实例。可以通过路径加载资源：
Resource resource = ctx.getResource("classpath:a/b/c.txt") 从类路径加载 file:///a/b/c.txt 以URL形式从文件系统加载 http://a.com/b/c.txt 以URL形式加载。

<h3>ResourceLoaderAware接口</h3>
&emsp;&emsp; 是标记接口，用来标记提供ResourceLoader引用的对象。应用上下文会识别ResourceLoaderAware并将自身作为参数来调用setResourceLoader()，
因为所有应用上下文都实现了ResourceLoader接口。也可以实现ApplicationContextAware接口，直接使用应用上下文来加载资源。最好使用专用的ResourceLoader接口，
这样代码只会与接口耦合，不会与整个ApplicationContext耦合。

<h3>应用上下文和资源路径</h3>
&emsp;&emsp; classpath*: 通配符会匹配类路径下的所有符合条件的资源，如：classpath*:META-INF/*-beans.xml。
如果使用classpath*:*.xml这种pattern无法从根目录的jar文件中获取资源，只能从根目录的扩展目录中获取资源。
当FileSystemApplicationContext是一个ResourceLoader实例时，不管FileSystemResource实例的位置是否以/开头，
FileSystemApplicationContext都将其作为相对路径来处理。当需要处理绝对路径时，使用file:的UrlResource。

<h2 id="6">6.验证、数据绑定和类型转换</h2>
&emsp;&emsp; BeanWrapper 和 BeanWrapperImpl提供了设置和获取属性值、获取属性描述符以及查询属性确认是否可读可写的功能。
Spring通过PropertyEditor来实现Object和String之间的转换。Spring中内置的PropertyEditor如下：
1.ByteArrayPropertyEditor 针对字节数组的编辑器，字符串会简单的转换成相应的字节表示，默认情况下由BeanWrapperImpl注册。
2.ClassEditor 将类的字符串表示形式解析成实际的类形式，并且也能返回实际类的字符串表示形式。
3.CustomBooleanEditor 针对Boolean属性的可制定的属性编辑器。
4.CustomCollectionEditor 针对集合的属性编辑器，可以将原始的Collection转换成给定的目标Collection类型。
5.CustomDateEditor 针对java.util.Date的可定制属性编辑器，支持自定义的时间格式。
6.CustomNumberEditor 针对Number子类的可定制属性编辑器。
7.FileEditor 能将字符串解析成java.io.File对象。
8.InputStreamEditor 一次性的属性编辑器，能够读取文本字符串并生成一个InputStream对象。
9.LocaleEditor 将字符串解析成Locale对象。
10.PatternEditor 将字符串解析成java.util.regex.Pattern对象。
11.PropertiesEditor 将字符串解析成Properties对象。
12.StringTrimmerEditor 用于缩减字符串的属性编辑器。
13.URLEditor 将一个URL的字符串表示解析成实际的URL对象。

<h3>Spring类型转换</h3>
&emsp;&emsp; Converter 将外部Bean的属性值字符串转换成需要的属性类型。实现Converter接口可以自定义转换器。

<h2 id="7">7.O/X映射器</h2>
&emsp;&emsp; 一个编组器负责将一个将一个对象序列化为XML，一个反编组器将XML反序列化成一个对象。Spring将编组操作抽象成了org.springframework.oxm.Marshaller中的方法。
    
    <p>
    public interface Marshaller {
       /**
        * 将对象编组并存放在Result中。
        */
        void marshal(Object graph, Result result) throws XmlMappingException, IOException;
    }
<br>
&emsp;&emsp; 不同的Result封装不同的XML表现形式。DOMResult 封装org.w3c.dom.Node SAXResult 封装 org.xml.sax.ContentHandler 
StreamResult 封装 java.io.File, java.io.OutputStream, java.io.Writer。
<br>
&emsp;&emsp; org.springframework.oxm.Unmarshaller用来处理将XML反序列化为一个对象。
    
    <p>
    public interface Unmarshaller {
       /**
        * 将XML 反编组成一个对象。
        */
        Object unmarshal(Source source) throws XmlMappingException, IOException;
     }
<br>
&emsp;&emsp; 每种Source封装了一种XML表现形式。DOMSource 封装 org.w3c.dom.Node SAXSource 封装 org.xml.sax.InputSource, org.xml.sax.XMLReader
StreamSource 封装 java.io.File, java.io.OutputStream, java.io.Writer

<br>
&emsp;&emsp; Spring对底层O／X映射异常进行了转换。以XmlMappingException的方式使之成为Spring自身异常继承体系的一部分。O／X 异常层次为：
    
    <p>
                               XmlMappingException
                                   |           |
                                   |           |                    
                      MarshallingException   ValidationFailureException  
                          |           |
                          |           |
    MarshallingFailureException    UnmarshallingFailureException
    </p>
