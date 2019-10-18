package com.xiao.framework.rpc.netty.demo;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 *
 * @author lix wang
 */
public class PlainOioServer {
    public void server(int port) throws IOException {
        final ServerSocket socket = new ServerSocket(port);
        for (;;) {
            final Socket clientSocket = socket.accept();
            System.out.println("Accepted connection from: " + clientSocket);
            new Thread(() -> {
                OutputStream outputStream;
                try {
                    outputStream = clientSocket.getOutputStream();
                    outputStream.write("Hi!\r\n".getBytes(Charset.forName("UTF-8")));
                    outputStream.flush();
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public static void main(String[] args) throws IOException {
        PlainOioServer plainOioServer = new PlainOioServer();
        plainOioServer.server(8899);
    }
}
