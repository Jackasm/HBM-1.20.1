package com.hbm.main;

public class ProxyFactory {

    public static CommonProxy create() {
        boolean isClient = net.minecraftforge.fml.loading.FMLEnvironment.dist.isClient();
        String className = isClient ? "com.hbm.client.ClientProxy" : "com.hbm.main.ServerProxy";
        try {
            Class<?> clazz = Class.forName(className);
            return (CommonProxy) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create proxy: " + className, e);
        }
    }
}