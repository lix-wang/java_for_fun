package com.xiao.database;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author lix wang
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataSourceParam {
    private DatabaseEnum database;
}
