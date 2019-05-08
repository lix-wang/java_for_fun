# Mybatis @MapperScan Analysis
## Demo configuration
&emsp;&emsp;&emsp;&emsp;Sometimes, we config datasource like the following does. 
<p>
	
	@Component
	@MapperScan(basePackages = "com.xiao.mapper.common", sqlSessionTemplateRef = "commonSqlSessionTemplate")
	public class CommonDataBaseConfig {
    private static final String MAPPER_XML_PATH = "classpath:com/xiao/mapper/common/*.xml";
    private static final String KEY_DATABASE_ANME = "common";


    private final CommonConfig commonConfig;

    @Autowired
    public CommonDataBaseConfig(CommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }

    @Bean(name = KEY_DATABASE_ANME + Constants.KEY_DATA_SOURCE)
    @Primary
    public DataSource getDataSource() {
        return DatabaseHelper.createDataSource(commonConfig.getCommonDatabase(),
                commonConfig.getCommonDatabaseUserName(), commonConfig.getCommonDatabasePassword());
    }

    @Bean(name = KEY_DATABASE_ANME + Constants.KEY_SQL_SESSION_FACTORY)
    @Primary
    public SqlSessionFactory getSqlSessionFactory(@Qualifier(KEY_DATABASE_ANME + Constants.KEY_DATA_SOURCE)
            DataSource dataSource) throws Exception {
        return DatabaseHelper.createSqlSessionFactory(dataSource, MAPPER_XML_PATH);
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
&emsp;&emsp;&emsp;&emsp;BUT, it's complicated to do so. Sometimes, we have more than one database in our project. This means we have to repeat and repeat again to config them.
## How to simplify database configuration?
&emsp;&emsp;&emsp;&emsp;If you want to simplify you configuration, you must know how your configuration really works. 
<br>&emsp;&emsp;&emsp;&emsp;From the above configuration, we can see the keypoints are "@Component" and "@MapperScan".
<br> &emsp;&emsp;&emsp;&emsp;Everyone whom use Spring knows about @Component. So, we will figure out how @MapperScan works, only when you figured out how @MapperScan works, can you simplify you database configuration.
## How @MapperScan works?
LOADING~~~