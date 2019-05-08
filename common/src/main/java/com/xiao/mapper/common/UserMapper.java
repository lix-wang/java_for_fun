package com.xiao.mapper.common;

import com.xiao.model.User;
import org.apache.ibatis.annotations.Param;

/**
 *
 * @author lix wang
 */
public interface UserMapper {
    User getById(@Param("id") long id);
}
