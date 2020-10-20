package com.xiao.mapper.common;

import com.xiao.model.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author wang lingxiao(lix.wang@alo7.com)
 */
public interface UserMapper2 {
    @Select("SELECT * FROM users WHERE id = #{id}")
    User getById(@Param("id") long id);
}
