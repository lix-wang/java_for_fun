package com.xiao.framework.demo;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 *
 * @author lix wang
 */
public class ReadThread extends Thread {
    private final Socket socket;
    private final InputStream inputStream;

    public ReadThread(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = socket.getInputStream();
    }

    /**
     * 改写interrupt方法，无论线程是否在某个可中断的阻塞方法中阻塞，都可以被中断并停止执行当前的工作。
     */
    public void interrupt() {
        try {
            socket.close();
        } catch (IOException ex) {
            // do nothing.
        } finally {
            super.interrupt();
        }
    }

    public void run() {
        try {
            byte[] buf = new byte[1024];
            while (true) {
                int count = inputStream.read(buf);
                if (count < 0) {
                    break;
                } else if (count > 0) {
                    // process buf.
                }
            }
        } catch (IOException ex) {
            // let thread shutdown.
        }
    }
}
