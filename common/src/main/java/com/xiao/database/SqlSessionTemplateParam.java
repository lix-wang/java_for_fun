package com.xiao.database;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Params to create a SqlSessionTemplate.
 *
 * @author lix wang
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SqlSessionTemplateParam {
    private String sqlSessionFactoryName;
}
