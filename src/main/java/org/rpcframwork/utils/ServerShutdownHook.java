package org.rpcframwork.utils;

import lombok.extern.slf4j.Slf4j;
import org.rpcframwork.core.registry.ServiceRegistry;
import org.rpcframwork.utils.Factory.ThreadPoolFactoryUtil;

/**
 * When the server is closed, do something such as unregister all services
 *
 * @author kmzcy
 */
@Slf4j
public class ServerShutdownHook {
    private static final ServerShutdownHook CUSTOM_SHUTDOWN_HOOK = new ServerShutdownHook();

    public static ServerShutdownHook getCustomShutdownHook() {
        return CUSTOM_SHUTDOWN_HOOK;
    }

    /**
     * Runtime.getRuntime().addShutdownHook():
     * 这是一个jvm中的关闭钩子。当程序退出时，会执行添加的shutdownHook线程。
     * 其中shutdownHook是一个已初始化但并没有启动的线程，当jvm关闭的时候，
     * 会执行系统中已经设置的所有通过方法addShutdownHook添加的钩子，当系统
     * 执行完这些钩子后，jvm才会关闭。所以，可通过这些钩子在jvm关闭的时候进行
     * 内存清理、资源回收等工作。
     * 程序退出的原因及是否触发关闭钩子:
     *   所有的线程已经执行完毕（√）
     *   调用System.exit()（√）
     *   用户输入Ctrl+C（√）
     *   遇到问题异常退出（√）
     *   kill -9 杀掉进程（×）
     * 注意: 杀进程，或者在idea里面直接stop服务是不会触发该钩子线程的
     *
     * @param serviceRegistry 提供注册服务的接口的实现
     */
    public void clearAll(ServiceRegistry serviceRegistry) {
        log.info("addShutdownHook for clearAll");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            ThreadPoolFactoryUtil.shutDownAllThreadPool();
            serviceRegistry.linkClose();
        }));
    }
}
