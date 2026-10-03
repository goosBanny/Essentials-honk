package com.earth2me.essentials.utils;

import com.earth2me.essentials.Essentials;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskUtil {

    private static Essentials plugin;
    private static volatile Thread taskThread;

    public TaskUtil(Essentials plugin) {
        TaskUtil.plugin = plugin;
    }

    public static final ExecutorService THREAD = Executors.newSingleThreadExecutor(r -> {
        taskThread = new Thread(r, "Essentials Async Processor Thread (x1)");
        return taskThread;
    });

    public static void runAsync(Runnable task) {
        if (Thread.currentThread() == taskThread) {
            try {
                task.run();
            } catch (final Throwable t) {
                if (plugin != null) {
                    plugin.getLogger().log(java.util.logging.Level.WARNING, "Error executing inline async task", t);
                }
            }
        } else {
            THREAD.execute(task);
        }
    }

    public static void runAsyncBukkit(Runnable task) {
        plugin.runTaskAsynchronously(task);
    }
}