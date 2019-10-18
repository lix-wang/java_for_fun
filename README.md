[Java虚拟机笔记](JVM.md)
<br>
[《SpringBoot In Action》笔记](SpringBootInAction.md)
<br>
[《Spring-framework-5-doc》笔记](SpringFramework5Doc.md)
<br>
[OkHttp 笔记](okhttp.md)
<br>
[《Spring In Action 4》笔记](SpringInAction4.md)
<br>
[Redis 笔记](Redis.md)
<br>
[《Java Concurrency In Practice》笔记](Java_Concurrency_In_Practice.md)
<br>
[Undertow 笔记](Undertow.md)
<br>
[ThreadPoolExecutor分析](ThreadPoolExecutor.md)
<br>
[Netty 笔记](Netty.md)
<br>
[NIO 笔记](NIO.md)

## SpringBoot
* [1.SpringBoot startup procedure analysis](#1)
* [1.1 new SpringApplication(Class<?>... primaryResources) object](#1.1)
* [1.2 call run() method of SpringApplication object](#1.2)
* [1.3 Prepare environment](#1.3)
* [1.4 Handle ApplicationContext](#1.4)
* [1.5 Refresh ApplicationContext](#1.5)
* [2.Mybatis @MapperScan analysis](#2)
* [3.@SpringBootApplication annotation analysis](#3)
* [3.1@EnableAutoConfiguration workflow](#3.1)
* [3.2 BeanDefinitionRegistryPostProcessor](#3.2)
* [4.SpringBoot Summary](#4)

<h2 id = "1">1.SpringBoot startup procedure analysis</h2>
&emsp;&emsp; SpringBoot项目启动的入口为：SpringApplication.run(Class<?> primaryResource).
启动的过程中，首先创建了对象："SpringApplication(Class<?>... primaryResources)"，然后执行了该实例对象的run()方法.

<h3 id = "1.1">1.1 new SpringApplication(Class<?>... primaryResources) object</h3>
&emsp;&emsp; 首先, 根据deduceWebApplicationType()方法判断当前服务是否为web服务。在我们的用例中，返回了WebApplicationType.NONE。
<br>
&emsp;&emsp; 然后通过getSpringFactoriesInstances(ApplicationContextInitializer.class)方法，创建ApplicationContextInitializer型实例集合，
具体的Initializer对象类型在spring.factories中配置（org.springframework.context.ApplicationListener, 创建后设置Initializers。
<br>
&emsp;&emsp; 通过getSpringFactoriesInstances(ApplicationListener.class)方法，创建ApplicationListener型实例集合，
具体的Initializer对象类型在spring.factories中配置（org.springframework.context.ApplicationContextInitializer，创建后设置Listeners。
<br>
&emsp;&emsp; 实际上getSpringFactoriesInstances(Class<T> type)方法，都是去读取了spring.factories文件中的配置信息，
其中该方法不仅读取了你当前项目自定义的spring.factories文件，还读取了配置项模块的spring.factories文件。
例如，在common项目中，我们采用如下配置：
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\com.xiao.CommonAutoConfiguration
由于EnableAutoConfiguration属于spring-boot-autoConfigure项目，那么spring-boot-autoConfigure中的spring-factories文件中的配置也会被读取。
而且这些配置项并不是替换策略而是增加策略。
<br>
&emsp;&emsp; 最后通过deduceMainApplicationClass方法来推断服务的入口类，主要是通过打印运行时异常栈轨迹，如果某个方法是"main"方法，那么该方法所在的类即为入口类。

<h3 id = "1.2">1.2 call run() method of SpringApplication object</h3>
&emsp;&emsp; StopWatch用以监控开发过程中的性能，忽略。
configurationHeadlessProperty()方法为配置系统的模式，默认为true，表示缺少显示设备、键盘或鼠标，该方法实质是System.setProperty("java.awt.headless", "true")。
接下来，根据SpringApplicationRunListeners和参数来确定环境。创建环境后，刷新了ApplicationContext上下文信息。

<h3 id = "1.3">1.3 Prepare environment</h3>
&emsp;&emsp; 在启动过程中，首先做了环境准备工作。先执行了 new StandardEnvironment(),创建了标准环境，这一步扫描出了systemProperties和systemEnvironment。
然后调用configureEnvironment(environment, args)方法来对获取到的ConfigurableEnvironment进行了配置，根据上文可知，我们并没有传入run()方法参数，
这一步主要调用了两个方法：configurePropertySource(ConfigurableEnvironment environment, String[] args) 和 configureProfiles(environment, args);
configurePropertySource(ConfigurableEnvironment environment, String[] args) 方法主要是设置args参数，如果没有，那这一步不会执行任何逻辑。
configureProfiles(environment, args) 方法先会去找"spring.profiles.active" 配置，由于我并没有采用这种常规的方式进行多环境配置，因此这一步并不会找到对应的配置。
也就是说configureEnvironment(environment, args) 这一步并没有执行实质性的逻辑。
<br>
&emsp;&emsp; 接下来调用了SpringApplicationRunListeners.environmentPrepared(environment)方法，通过ConfigFileApplicationListener.postProcessEnvironment()方法，获取了"spring.profiles.active"配置。
然后把获取到的环境与SpringApplication进行绑定。

<h3 id = "1.4">1.4 Handle ApplicationContext</h3>
&emsp;&emsp; 准备好环境后，开始进行ApplicationContext的处理。首先使用createApplicationContext()方法创建ApplicationContext。
由于当前的SpringApplication webApplicationType 为 WebApplicationType.NONE，
因此我们获取到的contextClass为 org.springframework.context.annotation.AnnotationConfigApplicationContext。
采用BeanUtils.instantiateClass(Class<T> clazz) 方式来创建了AnnotationConfigApplicationContext实例，并强转为ConfigurableApplicationContext。
创建完ConfigurableApplicationContext后，调用prepareContext()方法来填充applicationContext，在这一步中，首先将前面准备好的environment传入到context中，
然后使用前面扫描出来的Initializers逐个对context进行initialize(). 实际上这里调用的是EventPublishingRunListener.contextPrepared(context)空方法。
context 准备好后，把"springApplicationArguments"注册为单例Bean，这里的arguements是由 new DefaultApplicationArguments(String[] args) 得到。
然后createBeanDefinitionLoader(BeanDefinitionRegistry registry, Object[] sources)创建BeanDefinitionLoader，该loader会根据入口类来进行对应的加载。
首先会判断该入口类是否被Component注解注释，如果是，则会采用AnnotatedBeanDefinitionReader.register(Class<?> source)方法来注册该入口类。

<h3 id = "1.5">1.5 Refresh ApplicationContext</h3>
&emsp;&emsp; prepareContext() 结束后，执行了refreshContext(context)，该方法会刷新context，会把所有的BeanDefinition都扫描出来。

<h2 id ="2">2.Mybatis @MapperScan analysis</h2>

## Demo configuration
&emsp;&emsp;Sometimes, we config datasource like the following does. 
<p>
	
    @Component
    @MapperScan(basePackages = "com.xiao.mapper.common", sqlSessionTemplateRef = "commonSqlSessionTemplate")
    public class CommonDatabaseConfig {
        private static final String MAPPER_XML_PATH = "classpath:com/xiao/mapper/common/*.xml";
        private static final String KEY_DATABASE_ANME = "common";
    
    
        private final CommonConfig commonConfig;
        private final MysqlDatabaseService mysqlDatabaseService;
    
        @Autowired
        public CommonDatabaseConfig(CommonConfig commonConfig, MysqlDatabaseService mysqlDatabaseService) {
            this.commonConfig = commonConfig;
            this.mysqlDatabaseService = mysqlDatabaseService;
        }
    
        @Bean(name = KEY_DATABASE_ANME + Constants.KEY_DATA_SOURCE)
        @Primary
        public DataSource getDataSource() {
            return mysqlDatabaseService.createDataSource(commonConfig.getCommonDatabase(),
                    commonConfig.getCommonDatabaseUserName(), commonConfig.getCommonDatabasePassword());
        }
    
        @Bean(name = KEY_DATABASE_ANME + Constants.KEY_SQL_SESSION_FACTORY)
        @Primary
        public SqlSessionFactory getSqlSessionFactory(@Qualifier(KEY_DATABASE_ANME + Constants.KEY_DATA_SOURCE)
                DataSource dataSource) throws Exception {
            PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
            return mysqlDatabaseService.createSqlSessionFactory(dataSource, patternResolver.getResources(MAPPER_XML_PATH));
        }
    
        @Bean(name = KEY_DATABASE_ANME + Constants.KEY_TRANSACTION_MANAGER)
        @Primary
        public DataSourceTransactionManager getTransactionManager(@Qualifier(KEY_DATABASE_ANME + Constants.KEY_DATA_SOURCE)
                DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }
    
        @Bean(name = KEY_DATABASE_ANME + Constants.KEY_SQL_SESSION_TEMPLATE)
        public SqlSessionTemplate getSqlSessionTemplate(@Qualifier(KEY_DATABASE_ANME + Constants.KEY_SQL_SESSION_FACTORY)
                SqlSessionFactory sqlSessionFactory) {
            return new SqlSessionTemplate(sqlSessionFactory);
        }
    }

</p>

#### It does works!!! 
&emsp;&emsp; BUT, it's complicated to do so. Sometimes, we have more than one databases in our project. This means we have to repeat and repeat again to config them.
## How to simplify database configuration?
&emsp;&emsp; If you want to simplify you configuration, you must know how your configuration really works. From the above configuration, we can see the keypoints are "@Component" and "@MapperScan".
<br> 
&emsp;&emsp; Everyone whom use Spring knows about @Component. So, we will figure out how @MapperScan works, only when you figured out how @MapperScan works, can you simplify database configuration.
## How @MapperScan works?
&emsp;&emsp; I am tired of typing. If you do want to know how @MapperScan works, you can see how @LixDataBase works in my demo project. There are plenty of clear comments in my codes.

<h2 id = "3">3.@SpringBootApplication annotation analysis</h2>
&emsp;&emsp; 在Refresh ApplicationContext 这一步之后，项目中的BeanDefinition都已经加载完成。我们会发现@SpringBootApplication注解由另外几个注解注解。
主要有@Inherited、@SpringBootConfiguration、@EnableAutoConfiguration、@ComponentScan这四个注解。
首先@Inherited注解表明被该元注解注解的注解(The Annotation which annotated by @Inherited annotation), 将具有继承性，在这里我们关注的重点不在这个注解，
如果感兴趣可以参考我"springboot-demo" module 中SpringDemoServer的用法，可以看到SpringDemoServer类上并没有被@SpringBootApplication注解，
但实际上，这个类已经做到了相应的功能。
<br>
&emsp;&emsp; @SpringBootConfiguration 是被@Configuration注解所注解，本质上讲是起到了扫描的功能，把被注解的类扫描为BeanDefinition。
<br>
&emsp;&emsp; @ComponentScan 作用是用来定义目标扫描包的位置，被该注解注解的类所在包会被扫描，从而扫描出所有的BeanDefinitions，
用法可以参考"common" module下的CommonAutoConfiguration类，在这里先忽略该注解相关内容。
<br>
&emsp;&emsp; @SpringBootApplication注解起主要作用的是@EnableAutoConfiguration注解，接下来我们会分析@EnableAutoConfiguration注解到底做了什么事情。

<h3 id = "3.1">3.1@EnableAutoConfiguration workflow</h3>
&emsp;&emsp; 首先，我们需要知道，@EnableAutoConfiguration注解使用的场景。
<br>
&emsp;&emsp; 1.在AutoConfigurationExcludeFilter.getAutoConfiguration() 方法中，
SpringFactoriesLoader.loadFactoryNames(EnableAutoConfiguration.class, this.beanClassLoader)来获取所有的AutoConfiguration BeanDefinitions.
<br>
&emsp;&emsp; 调用链如下：SpringApplication.refreshContext(ConfigurableApplicationContext) --> SpringApplication.refresh(ApplicationContext) 
-->... --> ComponentScanAnnotationParser.parse(AnnotationAttributes, declaringClass) 此时declaringClass为"SpringDemoServer",
然后执行了ClassPathBeanDefinitionScanner.doScan(String... basePackages)方法，--> findCandidateComponents(String basePackage).
<br>
&emsp;&emsp; 这里在判断isCandidateComponent(MetadataReader) 的时候，TypeFilter 使用的是 AutoConfigurationExcludeFilter，
因为在@SpringBootApplication.@ComponentScan 就是设置的该TypeFilter。
<br>
&emsp;&emsp; 2.在@EnableAutoConfiguration注解中使用了@Import(AutoConfigurationImportSelector.class)。
<br>
&emsp;&emsp; 该类的调用链为：SpringApplication.refreshContext(ConfigurableApplicationContext) --> SpringApplication.refresh(ApplicationContext) 
-->... --> ConfigurationClassParser.parse(Set<BeanDefinitionHolder>) 此时BeanDefinitionHolder 名称为springDemoServer, 
--> ConfigurationClassParser.getImports() --> AutoConfigurationImportSelector.process(AnnotationMetadata, DeferredImportSelector)
--> AutoConfigurationImportSelector.selectImports(AnnotationMetadata) 在这个方法中，将会处理 exclude，
而且filter(List<String>, AutoConfigurationMetadata)将会调用OnClassCondition.match(String[], AutoConfigurationMetadata)方法，
该方法会筛选出不符合要求的BeanDefinition，例如一个AutoConfiguration使用了@ConditionalOnClass(xxx.class) 而 xxx.class 并不存在，
那么该AutoConfiguration 就会被筛选出去。在AutoConfigurationImportSelector.selectImports(AnnotationMetadata)方法中，
我们可以知道哪些AutoConfigurations被自动加载了，对于不需要的AutoConfigurations，我们可以在@SpringBootApplication注解中exclude掉。

<h3 id = "3.2">3.2 BeanDefinitionRegistryPostProcessor</h3>
&emsp;&emsp; 在我的项目中，我用到了BeanDefinitionRegistryPostProcessor，那么这个interface的作用是什么？
首先在AbstractApplicationContext.invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory) 中，
执行了invokeBeanFactoryPostProcessors方法，该方法就是用来处理实现了BeanDefinitionRegistryPostProcessor 接口的BeanDefinition，
首先执行了BeanDefinitionRegistryPostProcessor.postProcessBeanDefinitionRegistry(registry)方法，
在这一步所有常规的BeanDefinitions都已经加载了，但都还没初始化，因此这一步，我们可以添加额外的BeanDefinition，
或者修改BeanDefinition定义，就像我项目中的EnvConfigPostProcessor，修改了BeanDefinition的beanClass。
在执行完这一步后，会执行postProcessBeanFactory(ConfigurableListableBeanFactory) 方法，这个方法是在BeanDefinition加载完成后，
但还没有初始化阶段使用，这个方法允许在初始化前做各种处理。
<br>
&emsp;&emsp; postProcessBeanDefinitionRegistry(registry) 这个方法是在Bean创建前执行，
postProcessBeanFactory(ConfigurableListableBeanFactory) 这个方法是在Bean创建之后初始化之前执行。

<h2 id = "4">4.SpringBoot Summary</h2>
&emsp;&emsp; 首先，SpringBoot 会准备环境，然后加载AutoConfiguration，然后会扫描出BeanDefinitionRegistryPostProcessor，
然后执行BeanDefinitionRegistryPostProcessor.postProcessBeanDefinitionRegistry(BeanDefinitionRegistry)，
具体用法可以参考我项目中的EnvConfigPostProcessor 类，上面都是加载BeanDefinitions，并没有执行Bean的实例化操作，
在AbstractApplicationContext.refresh() 中，执行完finishBeanFactoryInitialization(ConfigurableListableBeanFactory)，
这些BeanDefinitions才真正的被实例化。