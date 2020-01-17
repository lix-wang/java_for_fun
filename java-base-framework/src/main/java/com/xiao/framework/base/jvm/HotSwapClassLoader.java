package com.xiao.framework.base.jvm;

/**
 * 这个方法只是公开了父类的defineClass方法，这个方法把Java类的byte[]数组转变为Class对象。
 * 这个类加载器的类查找范围跟它的父类是一致的，在被调用时，会按照双亲委派模型交给父类加载。
 *
 * @author lix wang
 */
public class HotSwapClassLoader extends ClassLoader {
    public HotSwapClassLoader() {
        super(HotSwapClassLoader.class.getClassLoader());
    }

    public Class loadByte(byte[] classByte) {
        return defineClass(null, classByte, 0, classByte.length);
    }
}
