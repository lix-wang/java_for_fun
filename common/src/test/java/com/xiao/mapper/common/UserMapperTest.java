package com.xiao.mapper.common;

import com.xiao.CommonAutoConfiguration;
import com.xiao.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author lix wang
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CommonAutoConfiguration.class)
public class UserMapperTest {
    @Autowired
    UserMapper userMapper;

    @Test
    public void testGetById() {
        User user = userMapper.getById(2);
        Assert.assertEquals("name 2", user.getName());
    }
}
