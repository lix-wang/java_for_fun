## Spring in action 4 doc notes
* [1.上下文](#1)
* [2.装配](#2)
* [3.高级装配](#3)

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
&emsp;&emsp; Spring可以将属性定义到外部的属性文件中，使用占位符来进行插值，使用${xxx}包装属性。