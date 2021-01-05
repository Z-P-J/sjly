package com.zpj.downloader.util.io;


import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/*
* Java序列化和反序列化时忽略serialVersionUID验证
* */
public class UnsafeObjectInputStream extends ObjectInputStream {

    public UnsafeObjectInputStream(InputStream in) throws IOException {
        super(in);
    }

    @Override
    protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
        ObjectStreamClass objInputStream = super.readClassDescriptor();
        Class<?> localClass = Class.forName(objInputStream.getName());
        ObjectStreamClass localInputStream = ObjectStreamClass.lookup(localClass);
        if (localInputStream != null) {
            final long localUID = localInputStream.getSerialVersionUID();
            final long objUID = objInputStream.getSerialVersionUID();
            if (localUID != objUID) {
                return localInputStream;
            }
        }
        return objInputStream;
    }
}
