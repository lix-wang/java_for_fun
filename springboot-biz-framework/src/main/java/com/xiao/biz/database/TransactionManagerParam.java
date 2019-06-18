package com.xiao.biz.database;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Params to create TransactionManager.
 *
 * @author lix wang
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionManagerParam {
    private String dataSourceName;
}
