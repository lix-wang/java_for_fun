# Contents

* [1.SpringBoot startup procedure analysis](#1)
* [1.1 new SpringApplication(Class<?>... primaryResources) object](#1.1)
* [1.2 call run() method of SpringApplication object](#1.2)
* [1.3 @SpringBootApplication annotation](#1.3)
* [2.Mybatis @MapperScan analysis](#2)

<h2 id = "1">SpringBoot startup procedure analysis</h2>
SpringBoot项目启动的入口为：SpringApplication.run(Class<?> primaryResource).
启动的过程中，首先创建了对象："SpringApplication(Class<?>... primaryResources)"，然后执行了该实例对象的run()方法.
<h3 id = "1.1">1.1 new SpringApplication(Class<?>... primaryResources) object</h3>
首先, 根据deduceWebApplicationType()方法判断当前服务是否为web服务。在我们的用例中，返回了WebApplicationType.NONE。
<br>
然后通过getSpringFactoriesInstances(ApplicationContextInitializer.class)方法，创建ApplicationContextInitializer型实例集合，
具体的Initializer对象类型在spring.factories中配置（org.springframework.context.ApplicationListener, 创建后设置Initializers。
<br>
通过getSpringFactoriesInstances(ApplicationListener.class)方法，创建ApplicationListener型实例集合，
具体的Initializer对象类型在spring.factories中配置（org.springframework.context.ApplicationContextInitializer，创建后设置Listeners。
<br>
实际上getSpringFactoriesInstances(Class<T> type)方法，都是去读取了spring.factories文件中的配置信息，
其中该方法不仅读取了你当前项目自定义的spring.factories文件，还读取了配置项模块的spring.factories文件。
例如，在common项目中，我们采用如下配置：
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\com.xiao.CommonAutoConfiguration
由于EnableAutoConfiguration属于spring-boot-autoConfigure项目，那么spring-boot-autoConfigure中的spring-factories文件中的配置也会被读取。
而且这些配置项并不是替换策略而是增加策略。
<br>
最后通过deduceMainApplicationClass方法来推断服务的入口类，主要是通过打印运行时异常栈轨迹，如果某个方法是"main"方法，那么该方法所在的类即为入口类。
<h3 id = "1.2>1.2 call run() method of SpringApplication object</h3>
StopWatch用以监控开发过程中的性能，忽略。
configurationHeadlessProperty()方法为配置系统的模式，默认为true，表示缺少显示设备、键盘或鼠标，该方法实质是System.setProperty("java.awt.headless", "true")。
接下来，根据SpringApplicationRunListeners和参数来确定环境。创建环境后，刷新了ApplicationContext上下文信息。
<h3 id = "1.3">1.3 @SpringBootApplication annotation</h3>
@SpringBootApplication 注解是@SpringBootConfiguration、@EnableAutoConfig
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
&emsp;&emsp;BUT, it's complicated to do so. Sometimes, we have more than one databases in our project. This means we have to repeat and repeat again to config them.
## How to simplify database configuration?
&emsp;&emsp;If you want to simplify you configuration, you must know how your configuration really works. From the above configuration, we can see the keypoints are "@Component" and "@MapperScan".
<br> &emsp;&emsp;Everyone whom use Spring knows about @Component. So, we will figure out how @MapperScan works, only when you figured out how @MapperScan works, can you simplify database configuration.
## How @MapperScan works?
I am tired of typing. If you do want to know how @MapperScan works, you can see how @LixDataBase works in my demo project. There are plenty of clear comments in my codes.