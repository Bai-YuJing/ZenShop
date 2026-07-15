package cn.zenshop.common.context;

public class ThreadLocalContext {

    private static final ThreadLocal<Long> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 存入当前线程的用户ID
     */
    public static void set(Long id) {
        THREAD_LOCAL.set(id);
    }

    /**
     * 获取当前线程的用户ID
     */
    public static Long get() {
        return THREAD_LOCAL.get();
    }

    /**
     * 移除当前线程的上下文（防止内存泄漏）
     */
    public static void remove() {
        THREAD_LOCAL.remove();
    }
}
