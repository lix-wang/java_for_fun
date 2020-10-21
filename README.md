[读书笔记](ReadNotes.md)

## 项目简介
* [1.日志](#1)
* [2.Json序列化](#2)
* [3.数据源](#3)
* [4.环境变量](#4)
* [5.请求拦截及权限校验](#5)
* [6.Redis](#6)
* [7.RPC](#7)

<h2 id="1">1.日志</h2>
&emsp;&emsp; 使用Log4j2日志框架，使用XML文件配置了默认的日志文件输出以及日志控制台输出，而且使用Java API实现了不同情况下的定制日志输出。

    // XML日志配置
    <Appenders>
            <Console name="ConsoleAppender" target="SYSTEM_OUT" follow="true">
                <PatternLayout pattern="%d %-5p [%t] %c{1.} - %m%n%ex"/>
            </Console>
    
            <RollingRandomAccessFile name="FileAppender" fileName="logs/${sys:log.file.basename}.log"
                                     filePattern="logs/${sys:log.file.basename}.%d{yyyy-MM-dd_HH}.log">
                <PatternLayout>
                    <Pattern>%d %-5p [%t] %c{1.} - %m%n%ex</Pattern>
                </PatternLayout>
                <Policies>
                    <TimeBasedTriggeringPolicy interval="1"/>
                </Policies>
                <DefaultRolloverStrategy max="360"/>
            </RollingRandomAccessFile>
        </Appenders>
        
    // Java API实现定制化日志
    public static Logger getLogger(@NotNull LoggerTypeEnum loggerType, @NotNull ProfileType profileType) {
            switch (loggerType) {
                case DEFAULT_LOGGER:
                    return getDefaultLogger(profileType);
                default:
                    throw new RuntimeException("logger.not_supported_logger_type");
            }
        }

<h2 id="2">2.Json序列化</h2>
&emsp;&emsp; 使用SpringBoot自带的Jackson，实现了序列化及反序列化的基类，只要继承基类并注入Spring容器，就能自定义各类型的序列化操作。
其中TypeReference是以接口的形式实现的泛型类型获取方式。可以通过TypeReference来获取泛型对象的泛型类型。
只要继承BaseJsonDeserializer、BaseJsonSerializer，并使用@Component注入，那么JacksonObjectMapperConfiguration配置类，
将会自动扫描这些自定义类并进行注册。

    // 反序列化基类
    public abstract class BaseJsonDeserializer<T> extends JsonDeserializer<T> implements TypeReference {
    }
    
    // 序列化基类
    public abstract class BaseJsonSerializer<T> extends JsonSerializer<T> implements TypeReference {
        @Override
        public Class<T> handledType() {
            return getRawTypeClass();
        }
    }
    
    // joda DateTime反序列化示例
    @Component
    public class DateTimeDeserializer extends BaseJsonDeserializer<DateTime> {
        @Override
        public DateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            String str = p.getValueAsString();
            return JodaUtils.getCSTDateTime(str);
        }
    }
    
    // joda DateTime序列化示例
    @Component
    public class DateTimeSerializer extends BaseJsonSerializer<DateTime> {
        @Override
        public void serialize(DateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.toString());
        }
    }
    
<h2 id="3">3.数据源</h2>
&emsp;&emsp; 使用MyBatis ORM框架，使用MySQL数据库，使用HikariCP连接池。只需要使用LixDatabase注解，即可实现数据源相关的所有配置。
只要继承BaseTypeHandler类，并使用@Component注入，即可实现MyBatis类型自定义处理。

    @Component
    @LixDatabase(
            databaseName = CommonLixDatabaseConfig.DATABASE_NAME,
            mapperPackages = "com.xiao.mapper.common",
            mapperLocations = "classpath*:mapper/common/*.xml")
    public class CommonLixDatabaseConfig extends BaseLixDatabaseConfig {
        public static final String DATABASE_NAME = "common";
    
        @Autowired
        protected CommonLixDatabaseConfig(CommonConfig commonConfig) {
            super(commonConfig.getCommonDatabase(), commonConfig.getCommonDatabaseUserName(),
                    commonConfig.getCommonDatabasePassword());
        }
    }
    
    // MyBatis自定义类型处理示例
    @Component
    public class DateTimeTypeHandler extends BaseTypeHandler<DateTime> {
        @Override
        public void setNonNullParameter(PreparedStatement ps, int i, DateTime parameter, JdbcType jdbcType)
                throws SQLException {
            ps.setTimestamp(
                    i, 
                    Timestamp.valueOf(parameter.toLocalDateTime().toString(JodaUtils.DEFAULT_DATETIME_FORMATTER))
            );
        }
        ......
    
<h2 id="4">4.环境变量</h2>
&emsp;&emsp; 使用@EnvConfigs注解 + @EnvConfig可重复注解，来实现不同环境的不同取值。

        @EnvConfig(
                environments = {
                        ProfileType.DEV,
                        ProfileType.ALPHA,
                        ProfileType.BETA,
                        ProfileType.PROD},
                value = "jdbc:mysql://localhost:3306/spring_boot_demo")
        @Getter
        @Setter
        private String commonDatabase = "jdbc:mysql://localhost:3306/spring_boot_demo";
        
<h2 id="5">5.请求拦截及权限校验</h2>
&emsp;&emsp; 可以使用SpringBoot的Interceptor拦截，做很多前期处理，例如参数校验，权限校验等操作。

    //使用ArgumentResolver + 注解进行参数取值校验处理。
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new SelectedParamResolver());
    }
    
    @GetMapping("/getSelectedParam")
    public long getSelectedParam(
            @SelectedRequestParam(name = "paramNum", required = false, defaultValue = "5", expectedValue = {"1", "2"})
                    long paramNum) {
        return paramNum;
    }

    // 可以使用Interceptor + 注解实现权限校验处理。
    @Configuration
    public class InterceptorConfiguration implements WebMvcConfigurer {
        private final SessionService sessionService;
    
        @Autowired
        public InterceptorConfiguration(SessionService sessionService) {
            this.sessionService = sessionService;
        }
    
        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(new SessionInterceptor(sessionService));
            // This place works the same as ActuatorInterceptorConfiguration does.
            // registry.addInterceptor(new ActuatorInterceptor(actuatorHelper));
        }
    }
    
<h2 id="6">6.Redis</h2>
&emsp;&emsp; 使用Jedis，模拟了Redis主从机制，主从故障时切换，故障实例状态轮询，读写分离等功能。用以Redis学习。

    public Jedis getJedis(boolean isSlaveOp) throws NoValidJedis {
            boolean needRefreshMaster = false;
            boolean needRefreshSlaves = false;
            Jedis jedis = null;
            try {
                // have slaves.
                if (isSlaveOp && CollectionUtils.isNotEmpty(this.slaves)) {
                    jedis = this.jedisSlaveManager.getJedis();
                }
                // get jedis from master.
                if ((!isSlaveOp || jedis == null) && this.master != null) {
                    jedis = this.jedisMasterManager.getJedis();
                    needRefreshMaster = jedis == null;
                }
                needRefreshSlaves = CollectionUtils.isNotEmpty(wrongSlaves);
                if (jedis != null) {
                    return jedis;
                }
                throw JedisCustomException.noValidJedis();
            } finally {
                if (needRefreshMaster) {
                    refreshMasterManagers();
                }
                if (needRefreshSlaves) {
                    refreshSlaveManager(false);
                }
            }
        }
        
<h2 id="7">7.RPC</h2>
&emsp;&emsp; 使用okHttp + ThreadLocal实现异步请求调用。

    @GetMapping("/testHttpRequest")
    public String testHttpRequest() {
        String result;
        HttpRequestWrapper request = HttpRequestWrapper.builder().url("https://www.baidu.com").build();
        AbstractAsyncResult<Response> response;
        try {
            response = AsyncCall.callAsync(
                    () -> HttpCallFactory.get(new BaseHttpCall(), HttpCall.class).asyncCallWithOkHttp(request));
            result = response.get().body().string();
        } catch (Exception e) {
            log.error("Async request http failed " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return result;
    }