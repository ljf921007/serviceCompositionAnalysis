package util;


import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;


public final class ExecutorUtil {

    public static ThreadLocal<Executor> TL_EXECUTOR = new ThreadLocal<Executor>();
    
    private ExecutorUtil() {}

    public static void runWithoutDeadLock(final Executor executor, final Runnable runnable) {
        if (executor == null) {
            throw new NullPointerException("executor");
        }
        if (runnable == null) {
            throw new NullPointerException("runnable");
        }

        executor.execute(new Runnable() {
            public void run() {
                TL_EXECUTOR.set(executor);
                try {
                    runnable.run();
                } finally {
                    TL_EXECUTOR.remove();
                }
            }
        });
    }
    
    public static void terminate(Executor... executors) {
        if (executors == null) {
            throw new NullPointerException("executors");
        }

        Executor[] executorsCopy = new Executor[executors.length];
        for (int i = 0; i < executors.length; i ++) {
            if (executors[i] == null) {
                throw new NullPointerException("executors[" + i + "]");
            }
            executorsCopy[i] = executors[i];
        }

        final Executor currentParent = TL_EXECUTOR.get();
        if (currentParent != null) {
            for (Executor e: executorsCopy) {
                if (e == currentParent) {
                    throw new IllegalStateException("can not shutdown from itself.");
                }
            }
        }

        boolean interrupted = false;
        for (Executor e: executorsCopy) {
            if (!(e instanceof ExecutorService)) {
                continue;
            }

            ExecutorService es = (ExecutorService) e;
            for (;;) {
                try {
                    es.shutdownNow();
                } catch (SecurityException ex) {
                    try {
                        es.shutdown();
                    } catch (SecurityException ex2) {
                        break;
                    }
                }

                try {
                    if (es.awaitTermination(100, TimeUnit.MILLISECONDS)) {
                        break;
                    }
                } catch (InterruptedException ex) {
                    interrupted = true;
                }
            }
        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }

}

