package com.xiao.marshaller;

import com.xiao.framework.biz.marshaller.MarshallerManager;
import com.xiao.model.Author;
import org.springframework.oxm.castor.CastorMarshaller;
import org.springframework.stereotype.Component;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Author marshaller and unmarshaller manager.
 *
 * @author lix wang
 */
@Component
public class AuthorMarshallerManager extends MarshallerManager {
    public static final String TARGET_FILE_NAME = "author.xml";
    private static CastorMarshaller castorMarshaller;

    public AuthorMarshallerManager() {
        super(castorMarshaller, castorMarshaller);
    }

    static {
        if (castorMarshaller == null) {
            castorMarshaller = new CastorMarshaller();
        }
    }

    /**
     * Marshal from Author to author.xml
     */
    public void marshallerAuthor(Author author) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(TARGET_FILE_NAME);
            startMarshal(author, new StreamResult(outputStream));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Unmarshal from author.xml to Author
     */
    public Author unmarshallerAuthor() {
        FileInputStream inputStream = null;
        Author author = null;
        try {
            inputStream = new FileInputStream(TARGET_FILE_NAME);
            author = (Author) startUnmarshal(new StreamSource(inputStream));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return author;
    }
}
