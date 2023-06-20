package net.kuisec.r8c.Bean;

import net.kuisec.r8c.ipc.PluginManager;

public class ServiceBean {
    public final String Package;
    public final String ServiceClassName;
    public final PluginManager manager;

    public ServiceBean(String aPackage, String serviceClassName, PluginManager manager) {
        Package = aPackage;
        ServiceClassName = aPackage + "." + serviceClassName;
        this.manager = manager;
    }
}
