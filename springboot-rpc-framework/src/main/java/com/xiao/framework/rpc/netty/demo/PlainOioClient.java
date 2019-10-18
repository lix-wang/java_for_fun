package com.xiao.framework.rpc.netty.demo;

import javax.validation.constraints.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author lix wang
 */
public class PlainOioClient {
    public void client(@NotNull String address, int port) throws IOException {
        for (int i = 0; i < 10; i++) {
            final Socket socket = new Socket(address, port);
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(("I am message: " + i).getBytes());
            outputStream.flush();
            InputStream inputStream = socket.getInputStream();
            byte[] buffer = new byte[4096];
            StringBuilder stringBuilder = new StringBuilder();
            while (inputStream.read(buffer) > 0) {
                stringBuilder.append(buffer);
            }
            System.out.println("I got message from server: " + new String(buffer));
            inputStream.close();
            socket.close();
        }
    }

    public static void main(String[] args) throws IOException {
        PlainOioClient plainOioClient = new PlainOioClient();
        plainOioClient.client("127.0.0.1", 8899);
    }
}
