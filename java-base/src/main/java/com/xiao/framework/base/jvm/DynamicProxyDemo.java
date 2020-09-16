package com.xiao.framework.base.jvm;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author lix wang
 */
public class DynamicProxyDemo {
    interface IHello {
        void sayHello();
    }

    private static class Hello implements IHello {
        @Override
        public void sayHello() {
            System.out.println("Hello world");
        }
    }

    private static class DynamicProxy implements InvocationHandler {
        Object originObject;

        Object bind(Object originObject) {
            this.originObject = originObject;
            return Proxy.newProxyInstance(originObject.getClass().getClassLoader(),
                    originObject.getClass().getInterfaces(), this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("Invoke method start");
            return method.invoke(originObject, args);
        }
    }

    public static void main(String[] args) {
        System.getProperties().setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
        IHello hello = (IHello) new DynamicProxy().bind(new Hello());
        hello.sayHello();
    }
}
