## Spring in action 4 doc notes
* [1.上下文](#1)
* [2.装配](#2)
* [3.高级装配](#3)
* [4.面向切面](#4)
* [5.SpringMVC](#5)
* [6.JDBC](#6)

<h2 id="1">1.上下文</h2>
&emsp;&emsp; Spring 采用4中策略降低Java开发的复杂性：1，基于POJO的轻量级和最小侵入性编程。2，通过依赖注入和面向接口实现松耦合。
3，基于切面和惯例进行声明式编程。4，通过切面和模版减少样板式代码。

&emsp;&emsp; Spring 自带了很多类型的上下文。AnnotationConfigApplicationContext：从一个或多个基于Java的配置类中加载Spring应用上下文。
AnnotationConfigWebApplicationContext：从一个或者多个基于Java的配置类中加载Spring Web应用上下文。
ClassPathXmlApplicationContext：从类路径下的一个或多个XML配置文件中加载上下文定义，把应用上下文的定义文件作为类资源。
FileSystemXmlApplicationContext：从文件系统下的一个或多个XML配置文件中加载上下文定义。
XmlWebApplicationContext：从Web应用下的一个或多个XML配置文件中加载上下文定义。
<br>
&emsp;&emsp; Bean 装载到Spring应用上下文中生命周期过程如下：实例化 -> 填充属性 -> 调用BeanNameAware的setBeanName()方法 
-> 调用BeanFactoryAware的setBeanFactory()方法 ->  调用ApplicationContextAware的setApplicationContext()方法
-> 调用BeanPostProcessor的预初始化方法 -> 调用InitializingBean的afterPropertiesSet() 方法 -> 调用自定义的初始化方法
-> 调用BeanPostProcessor的初始化后方法 -> bean创建完成 -> (容器关闭)调用DisposableBean的destroy()方法 -> 调用自定义的销毁方法。

<br>
&emsp;&emsp; 详细描述为：1，Spring对bean进行初始化。2，Spring将值和bean的引用注入到bean对应的属性中。
3，如果bean实现了BeanNameAware接口，Spring将bean的ID传递给setBeanName()方法。
4，如果bean实现了BeanFactoryAware接口，Spring将调用setBeanFactory()方法，将BeanFactory容器实例传入。
5，如果bean实现了ApplicationContextAware接口，Spring将调用setApplicationContext()方法，将bean所在的应用上下文引用引入进来。
6，如果bean实现了BeanPostProcessor接口，Spring将调用它们的postProcessBeforeInitialization()方法。
7，如果bean实现了InitializingBean接口，Spring将调用它们的afterPropertiesSet()方法，类似的，如果bean使用init-method声明了初始化方法，该方法也会被调用。
8，如果bean实现了BeanPostProcessor接口，Spring将调用它们的postProcessAfterInitialization()方法。
9，此时bean已经准备就绪，可以被应用程序使用，它们将一直存在应用上下文中，直到该应用上下文被销毁。
10，如果bean实现了DisposableBean接口，Spring将调用它的destroy()接口方法，同样如果bean使用destroy-method声明了销毁方法，那么该方法也会被调用。

<br>
&emsp;&emsp; Spring从两个角度实现自动化装配：组件扫描，Spring会自动发现应用上下文中所创建的bean。自动装配，Spring自动满足bean之间的依赖。

<h2 id="2">2.装配</h2>
&emsp;&emsp; 使用@Autowired 进行装配，如果没有匹配的bean会抛异常，为了防止抛异常，可以将@Autowired的required设置为false，
这时候没有找到匹配的bean的话，该依赖将为null，可能引起空指针的问题。@Inject和@Autowired可以互换，@Named和@Component可以互换。
可以使用@Import的方式将两个@Configuration组合在一起，可以使用@ImportResource注解引入XML配置。

<br>
&emsp;&emsp; Spring框架核心是Spring容器，容器负责管理应用中组件的生命周期，会创建这些组件并保证它们的依赖能得到满足。
装配bean可以采用三种方式：自动化配置，基于Java的显式配置，基于XML的显式配置。

<h2 id="3">3.高级装配</h2>
&emsp;&emsp; 可以使用@Profile注解指定bean属于哪个Profile。可以在类和方法上使用该注解。只有当规定的Profile激活的时候，相应的bean才会被创建，
没有指定Profile的bean始终会被创建。Spring在确定哪个Profile处于激活状态时，需要依赖spring.profiles.active和spring.profiles.default。
如果设置了spring.profiles.active属性的话，那么就会确定哪个Profile被激活，如果没有设置那么将会查找spring.profiles.default，如果均没有，
那么就没有激活的Profile，此时只会加载没有定义Profile的bean。

<br>
&emsp;&emsp; 可以使用@Conditional注解进行条件化配置，设置给@Conditional类的可以是任意实现了Condition接口的类型，只需要提供matches()接口即可。
Condition实现的考量因素很多，matches()方法会得到ConditionContext和AnnotatedTypeMetadata对象做出决策。
ConditionContext接口，可以通过getRegistry()返回BeanDefinitionRegistry检查bean定义。借助getBeanFactory()返回的ConfigurableListableBeanFactory
检查bean是否存在，甚至探查bean的属性。借助getEnvironment()返回的Environment检查环境变量是否存在以及值。通过getResourceLoader()返回的ResourceLoader所加载的资源。
借助getClassLoader()返回的ClassLoader加载并检查类是否存在。

<br>
&emsp;&emsp; AnnotatedTypeMetadata能够检查带有@Bean注解的方法上还有什么其他的注解。isAnnotated()方法判断带有@Bean注解的方法是不是还有其他特定的注解。

<br>
&emsp;&emsp; Spring在自动装配的时候，如果发现多个同样类型的Bean，这时候Spring会遇到装配歧义性问题，此时会抛出NoUniqueBeanDefinitionException，
解决该异常可以采用bean中某一设为Primary，或者使用限定符Qualifier来消除歧义。@Qualifier传入的值就是bean ID。

<br>
&emsp;&emsp; 在Spring中，默认所有bean都是以单例模式创建的。有时候对象是易变的，会保持一些状态。
Spring提供了多种作用域：单例(Singleton)，在整个应用中只创建bean的一个实例。原型(Prototype)，每次注入或者通过Spring上下文获取时，都创建一个新的实例。
会话(Session)，在Web应用中，为每个会话创建一个实例。请求(Request)，在Web应用中，为每个请求创建一个实例。ConfigurableBeanFactory.SCOPE_PROTOTYPE。

<br>
&emsp;&emsp; Spring可以将属性定义到外部的属性文件中，使用占位符来进行插值，使用${xxx}包装属性。可以使用Spring表达式语言进行装配，
SpEL具有以下特性：使用bean的ID来引用bean。调用方法和访问对象的属性。对值进行算术、关系和逻辑运算。正则表达式匹配。集合操作。
SpEL表达式要放到#{xxx}中。@Value中可以放置占位符，也可以放置SpEL。SpEL可以将一个bean装配到另一个bean的属性中，此时使用bean ID作为SpEL表达式。
可以使用类型安全的运算符#{user.getAge()?.toUpperCase()} 如果"?"前的值为null，那么就直接返回null，不执行后续方法，这个类似于ruby中的"&"符号。
可以使用T()来访问目标类型的静态方法和常量。#{T(System).currentTimeMillis()}。SpEL提供了查询运算符(.?[])，#{music.songs.?[artist eq 'Jay']}。
(.^[])表示查询第一个匹配项，(.$[])表示查询最后一个匹配项。(.![])投影运算符，#{music.songs.![title]} 表示将所有的title投影到一个新的集合中。
#{music.songs.?[artist eq 'Jay'].![title]}

<h2 id="4">4.面向切面</h2>
&emsp;&emsp; 通知(Advice)：Spring切面可以应用5中通知：前置通知(Before)：在目标方法被调用之前调用通知功能。后置通知(After)：在目标方法完成之后调用通知。
返回通知(After-returning)：在目标方法成功执行之后调用通知。异常通知(After-throwing)：在目标方法抛出异常后调用通知。
环绕通知(Around)：通知包裹了被通知的方法，在被通知的方法调用之前和调用之后执行自定义的行为。

<br>
&emsp;&emsp; 连接点(Join point)：连接点是在应用执行过程中能够插入切面的一个点。

<br>
&emsp;&emsp; 切点(Poincut)：切点的定义会匹配通知所要织入的一个或多个连接点。

<br>
&emsp;&emsp; 切面(Aspect)：切面是通知和切点的结合，通知和切点定义了切面的全部内容。

<br>
&emsp;&emsp; 织入(Weaving)：织入是把切面应用到目标对象并创建新的代理对象的过程。切面在指定的连接点被织入到目标对象中。
在目标对象的生命周期里有多个点可以进行织入：切面在编译期被织入，需要特殊的编译器，AspectJ的织入编译器就是这样织入切面的。
切面在类加载期织入，切面在目标类加载到JVM时织入，这种方式需要特殊的类加载器，在目标类被引入应用之前增强该目标类的字节码。AspectJ的加载时织入就支持。

### Spring aop
&emsp;&emsp; 首先通过ProxyFactoryBean设置代理的interface、target以及Advice列表，然后通过ProxyFactoryBean.getObject()获取当前target的代理对象。
如果是基于接口代理的，那么会创建JdkDynamicAopProxy，否则使用ObjenesisCglibAopProxy。

    // Spring aop使用
    Advice before = new StoreBeforeAdvice();
    Advice after = new StoreAfterReturningAdvice();
    Advice around = new StoreAroundAdvice();
    Advice throwsAdvice = new StoreThrowsAdvice();

    StoreService storeService = new StoreServiceImpl();

    ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
    proxyFactoryBean.setInterfaces(StoreService.class);
    proxyFactoryBean.setTarget(storeService);

    proxyFactoryBean.addAdvice(after);
    proxyFactoryBean.addAdvice(around);
    proxyFactoryBean.addAdvice(throwsAdvice);
    proxyFactoryBean.addAdvice(before);

    StoreService storeService1 = (StoreService) proxyFactoryBean.getObject();
    storeService1.buy();

<br>
&emsp;&emsp; 对于JdkDynamicAopProxy对象，在方法调用时，首先获取当前方法的拦截链(interception chain)，在获取方法链时，
先通过hasMatchingIntroductions(Advised, Class)方法，先判断是否有IntroductionAdvisor。接着循环当前ProxyFactoryBean中存在的Advisors。
最终返回interceptor集合，interceptor是根据Advisor获取的，Advisor分为：PointcutAdvisor、IntroductionAdvisor、以及其他Advisor，
通常Advisor支持三种Advice：AfterReturningAdvice、MethodBeforeAdvice、ThrowsAdvice。在使用registry.getInterceptors(Advisor)获取MethodInterceptor[]时，
最终返回的Interceptor数组可能包含四种类型：MethodInterceptor、AfterReturningAdviceInterceptor、MethodBeforeAdviceInterceptor、ThrowsAdviceInterceptor。

    对于上面的storeService1.buy();
        1，先根据method获取List<MethodInterceptor> 执行链。包括AfterReturningAdviceInterceptor、MethodInterceptor、
        ThrowsAdviceInterceptor、MethodBeforeAdviceInterceptor。
    然后创建一个ReflectiveMethodInvocation对象，使用ReflectiveMethodInvocation.proceed()方法来调用执行方法链。
    
&emsp;&emsp; 对于方法链的执行顺序。

    MethodBeforeAdviceInterceptor 在真正方法的调用前执行。
    AfterReturningAdviceInterceptor 在真正方法调用后执行。
    ThrowsAdviceInterceptor 使用try catch 包裹真正的方法调用。
    MethodInterceptor 自定义执行。
    
    对于上面的例子，如果使用下面添加advice的顺序。
        proxyFactoryBean.addAdvice(after);
        proxyFactoryBean.addAdvice(around);
        proxyFactoryBean.addAdvice(throwsAdvice);
        proxyFactoryBean.addAdvice(before);
    结果为：
        .....around advice begin.....
        .....before advice.....
        .....customer buy.....
        .....around advice end.....
        .....after returning advice.....
        
    如果使用下面添加advice的顺序。
        proxyFactoryBean.addAdvice(after);
        proxyFactoryBean.addAdvice(around);
        proxyFactoryBean.addAdvice(throwsAdvice);
        proxyFactoryBean.addAdvice(before);
    结果为：
        .....before advice.....
        .....around advice begin.....
        .....customer buy.....
        .....around advice end.....
        .....after returning advice.....

<h2 id="5">5.SpringMVC</h2>
&emsp;&emsp; 请求时，首先经过DispatcherServlet，这一步单实例的Servlet将请求委托给应用程序的其他组件执行实际的处理。
DispatcherServlet将请求发送给Spring MVC控制器(Controller)，DispatcherServlet会查询处理器映射(Handler Mapping)，确定下一站。
控制器将业务逻辑委托给一个或多个服务对象进行处理。控制器在逻辑处理完后，会将模型数据打包，接下来将请求连同模型和试图名发送回DispatcherServlet。
传递给DispatcherServlet的并不是直接的视图，而是逻辑名称，DispatcherServlet会通过名称和试图解析器(View resolver)匹配对应的视图。

<br>
&emsp;&emsp; 扩展AbstractAnnotationConfigDispatcherServletInitializer的任意类都会自动的配置DispatcherServlet和Spring应用上下文。
Spring应用上下文会位于应用的Servlet上下文中。容器会在类路径中查找实现了ServletContainerInitializer接口的类，如果发现了就会用来配置Servlet容器，
Spring提供了这个类的实现SpringServletContainerInitializer，这个类会去查找实现了WebApplicationInitializer的类，并把配置的任务交给它。
Spring引入了一个基础实现，就是AbstractAnnotationConfigDispatcherServletInitializer，因此拓展该类就可以用来配置Servlet上下文。

<br>
&emsp;&emsp; getServletMappings() 会将一个或者多个路径映射到DispatcherServlet上，"/"表示是应用默认的Servlet，处理所有进入应用的请求。
当DispatcherServlet启动的时候，会创建Spring应用上下文，加载配置文件和配置类中声明的bean。getServletConfigClasses()方法要求加载应用上下文时，使用定义在这些Class中的配置类。
在Spring Web应用中，通常还有另一个应用上下文，这个上下文由ContextLoaderListener创建。DispatcherServlet加载包含Web组件的bean，如控制器，视图解析器，处理映射器，
ContextLoaderListener加载其他的bean。getServletConfigClasses()方法，返回带有@Configuration注解的类用以定义DispatcherServlet上下文中的bean。
getRootConfigClasses()返回带有@Configuration注解的类，配置ContextLoaderListener创建的应用上下文的bean。

<br>
&emsp;&emsp; 控制器通知是任意带有@ControllerAdvice注解的类。这个类会包含一个或多个如下类型的方法：@Exceptionhandler注解标注的方法。
@InitBinder注解标注的方法。@ModelAttribute注解标注的方法。在带有@ControllerAdvice注解的类上，以上的方法会运用到所有控制器中带有@RequestMapping注解的方法上。


<h2 id="6">6.JDBC</h2>
&emsp;&emsp; Spring 将数据访问过程中固定的和可变的部分划分为两个不同的类：模板(template)和回调(callback)。

<br>
&emsp;&emsp; 基于JPA的应用程序需要使用EntityManagerFactory的实现类来获取EntityManager实例，
JPA定义了两种类型的实体管理器：应用程序管理类型(Application-managed)，当应用程序向实体管理工厂直接请求实体管理器时，工厂会创建一个实体管理器。
这种模式下，程序要负责打开关闭实体管理器，并在事务中进行控制。容器管理类型(Container-managed)，容器负责配置实体管理器工厂。