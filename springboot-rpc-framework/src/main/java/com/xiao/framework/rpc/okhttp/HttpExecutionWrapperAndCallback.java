package com.xiao.framework.rpc.okhttp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.Callback;

/**
 * Mapping model of {@link HttpExecutionWrapper} and {@link okhttp3.Callback}
 *
 * @author lix wang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HttpExecutionWrapperAndCallback {
    private HttpExecutionWrapper wrapper;
    private Callback callback;
}
