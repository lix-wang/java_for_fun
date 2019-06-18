package com.xiao.biz.environment;

import lombok.Data;

/**
 *
 * @author lix wang
 */
@Data
public class CmdLineConfig {
    private ProfileType profile = ProfileType.DEV;
    private String hostName = "localhost";
    private String serverName = "unspecific";
}
