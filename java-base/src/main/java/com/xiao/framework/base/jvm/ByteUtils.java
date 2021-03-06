package com.xiao.framework.base.jvm;

/**
 * @author lix wang
 */
public class ByteUtils {
    public static int bytes2Int(byte[] bytes, int start, int len) {
        int sum = 0;
        int end = start + len;
        for (int i = start; i < end; i++) {
            int n = ((int) bytes[i]) & 0xff;
            n <<= (--len) * 8;
            sum += n;
        }
        return sum;
    }

    public static String bytes2String(byte[] bytes, int start, int len) {
        return new String(bytes, start, len);
    }

    public static byte[] string2Bytes(String str) {
        return str.getBytes();
    }

    public static byte[] int2Bytes(int val, int len) {
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            bytes[len - i - 1] = (byte) ((val >> 8 * i) & 0xff);
        }
        return bytes;
    }

    public static byte[] bytesReplace(byte[] originBytes, int offset, int len, byte[] replaceBytes) {
        byte[] newBytes = new byte[originBytes.length + (replaceBytes.length - len)];
        System.arraycopy(originBytes, 0, newBytes, 0, offset);
        System.arraycopy(replaceBytes, 0, newBytes, offset, replaceBytes.length);
        System.arraycopy(originBytes, offset + len, newBytes, offset + replaceBytes.length,
                originBytes.length - offset - len);
        return newBytes;
    }
}
