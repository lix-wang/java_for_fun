package com.xiao.biz.database;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;

/**
 * Params of sqlSessionFactory.
 * If you want to know the meaning of mapperLocations, PLZ see:
 *  {@link org.mybatis.spring.SqlSessionFactoryBean#setMapperLocations(Resource[])}
 *
 * @author lix wang
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SqlSessionFactoryParam {
    private String dataSourceName;
    private String[] mapperLocations;
    private DatabaseEnum database;
}
