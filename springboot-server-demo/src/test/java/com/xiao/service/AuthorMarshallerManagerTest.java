package com.xiao.service;

import com.xiao.SpringDemoServer;
import com.xiao.marshaller.Author;
import com.xiao.marshaller.AuthorMarshallerHook;
import com.xiao.marshaller.AuthorMarshallerManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test for {@link com.xiao.marshaller.AuthorMarshallerManager}
 *
 * @author lix wang
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringDemoServer.class)
public class AuthorMarshallerManagerTest {
    @Autowired
    AuthorMarshallerManager authorMarshallerManager;
    @Autowired
    AuthorMarshallerHook authorMarshallerHook;

    @Test
    public void testAuthorMarshal() {
        Author author = Author.builder().author("lix.wang").authorAmount(1).version("Demo 1.0").build();
        authorMarshallerManager.setMarshallerHook(authorMarshallerHook);
        authorMarshallerManager.marshallerAuthor(author);
        Author unmarshallerAuthor = authorMarshallerManager.unmarshallerAuthor();
        System.out.println(unmarshallerAuthor.toString());
    }
}
