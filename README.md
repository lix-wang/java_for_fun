# Contents

* [1.SpringBoot start procedure analysis](#1)
* [2.Mybatis @MapperScan analysis](#2)

<h2 id = "1">1.SpringBoot start procedures</h2>
loading...
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