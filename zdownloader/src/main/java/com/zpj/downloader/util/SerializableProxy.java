package com.zpj.downloader.util;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;

public class SerializableProxy implements Serializable {

    private final Proxy.Type type;
    private final SocketAddress sa;

    public static SerializableProxy with(Proxy proxy) {
        return new SerializableProxy(proxy.type(), proxy.address());
    }

    private SerializableProxy() {
        type = Proxy.Type.DIRECT;
        sa = null;
    }

    public SerializableProxy(Proxy.Type type, SocketAddress sa) {
        if ((type == Proxy.Type.DIRECT) || !(sa instanceof InetSocketAddress))
            throw new IllegalArgumentException("type " + type + " is not compatible with address " + sa);
        this.type = type;
        this.sa = sa;
    }

    public Proxy proxy() {
        return new Proxy(type, sa);
    }

    public Proxy.Type type() {
        return type;
    }

    public SocketAddress address() {
        return sa;
    }

    public String toString() {
        if (type() == Proxy.Type.DIRECT)
            return "DIRECT";
        return type() + " @ " + address();
    }

    public final boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Proxy))
            return false;
        Proxy p = (Proxy) obj;
        if (p.type() == type()) {
            if (address() == null) {
                return (p.address() == null);
            } else
                return address().equals(p.address());
        }
        return false;
    }

    public final int hashCode() {
        if (address() == null)
            return type().hashCode();
        return type().hashCode() + address().hashCode();
    }

}
