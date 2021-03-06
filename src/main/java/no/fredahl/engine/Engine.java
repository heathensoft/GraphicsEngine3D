package no.fredahl.engine;

import no.fredahl.engine.window.Options;
import no.fredahl.engine.window.Window;
import org.lwjgl.Version;

import java.util.concurrent.*;

import static java.lang.System.nanoTime;
import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Frederik Dahl
 * 22/10/2021
 */


public class Engine {
    
    private static final int TARGET_UPS = 60;
    private static final int TARGET_FPS = 60;
    private static final boolean CAP_FPS = true;
    private static final boolean SLEEP_ON_SYNC = true;
    
    private static Engine instance;
    private final ExecutorService executor;
    private final Thread GLContext;
    private final Object lock;
    public final Time time;
    public final Window window;
    private Application application;
    private boolean running;
    
    private Engine(int threadPool) {
        
        /*Todo: remove the thread pools. This has nothing to do with the engine*/
        
        threadPool = Math.max(0,threadPool);
        executor = new ThreadPoolExecutor(
                threadPool,
                threadPool * 2,
                3000,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(64)
        );
        window = new Window();
        lock = new Object();
        time = new Time();
        GLContext = new Thread(() -> {
            try {
                window.initialize();
                time.init();
                application.start(window);
                running = true;
                float alpha;
                float frameTime;
                float accumulator = 0f;
                float delta = 1f / TARGET_UPS;
                while (running) {
                    frameTime = time.frameTime();
                    accumulator += frameTime;
                    while (accumulator >= delta) {
                        if (!window.isMinimized())
                            application.input(delta);
                        application.update(delta);
                        time.incUpsCount();
                        accumulator -= delta;
                    }
                    synchronized (lock) {
                        if (running) {
                            alpha = accumulator / delta;
                            if (!window.isMinimized()) {
                                window.updateViewport(application);
                                application.render(alpha);
                                window.swapBuffers();
                            }
                        }
                    }
                    time.incFpsCount();
                    time.update();
                    if (!window.vsyncEnabled()) {
                        if (CAP_FPS) sync();
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                synchronized (lock) {
                    if (running) {
                        exit();
                    }
                }
                System.out.println("\nTERMINATE\n");
                System.out.println("Engine: exiting application...");
                application.exit();
                System.out.println("Engine: " + Thread.currentThread() + " ended");
            }
        },"GL_Context_Thread");
    }
    
    
    public void start(Application app, Options options) {
    
        String platform = System.getProperty("os.name") + ", " + System.getProperty("os.arch") + " Platform.";
        int numProcessors = Runtime.getRuntime().availableProcessors();
        int JREMemoryMb = (int)(Runtime.getRuntime().maxMemory() / 1000000L);
        String jre = System.getProperty("java.version");
    
        System.out.println("\nWelcome!");
        System.out.println("\nSYSTEM\n");
    
        System.out.println("---Running on: " + platform);
        System.out.println("---jre: " + jre);
        System.out.println("---Available processors: " + numProcessors);
        System.out.println("---Reserved memory: " + JREMemoryMb + " Mb");
    
        System.out.println("---LWJGL version: " + Version.getVersion());
        System.out.println("---GLFW version: " + glfwGetVersionString());
    
        System.out.println("\nINITIALIZE\n");
        synchronized (this) {
            try {
                application = app;
                window.create(options);
                GLContext.start();
                while (!window.shouldClose()) {
                    window.waitEvents(0.1f);
                    window.handleRequests();
                }
                synchronized (lock) {
                    running = false;
                }
                window.hide();
                GLContext.join(300);
            }
            catch (Exception e) {
                e.printStackTrace();
            }finally {
                window.terminate();
                executor.shutdown();
            }
        }
    }
    
    
    private void sync() {
        
        double lastFrame = time.lastFrame();
        double now = time.timeSeconds();
        float targetTime = 0.96f / TARGET_FPS;
        
        while (now - lastFrame < targetTime) {
            if (SLEEP_ON_SYNC) {
                Thread.yield();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            now = time.timeSeconds();
        }
    }
    
    public synchronized static Engine get() {
        return instance == null ? instance = new Engine(1) : instance;
    }
    
    public synchronized static Engine get(int threadPool) {
        return instance == null ? instance = new Engine(threadPool) : instance;
    }
    
    public void execute(Runnable runnable) {
        try {
            executor.submit(runnable);
        } catch (Exception e) {
            System.out.println("Engine: could not execute task..");
            exit();
        }
        
    }
    
    public void exit() {
        System.out.println("Engine: window signalled to close..");
        instance.window.signalToClose();
    }
    
    
    public static class Time {
        
        private double initTime;
        private double lastFrame;
        private float timeAccumulator;
        private float frameTimeLimit;
        private int fpsCount;
        private int upsCount;
        private int fps;
        private int ups;
        
        public Time() { this(0.25f); }
        
        public Time(float frameTimeLimit) { this.frameTimeLimit = frameTimeLimit; }
        
        protected void init() {
            initTime = nanoTime();
            lastFrame = timeSeconds();
        }
        
        protected float frameTime() {
            double timeSeconds = timeSeconds();
            float frameTime = (float) (timeSeconds - lastFrame);
            frameTime = Math.min(frameTime, frameTimeLimit);
            lastFrame = timeSeconds;
            timeAccumulator += frameTime;
            return frameTime;
        }
        
        protected void update() {
            
            if (timeAccumulator > 1) {
                fps = fpsCount;
                ups = upsCount;
                fpsCount = upsCount = 0;
                timeAccumulator -= 1;
            }
        }
        
        public double timeSeconds() { return nanoTime() / 1_000_000_000.0; }
        
        public double runTime() { return nanoTime() - initTime; }
    
        public double runTimeSeconds() { return (nanoTime() - initTime) / 1_000_000_000.0 ; }
        
        protected void incFpsCount() { fpsCount++; }
        
        protected void incUpsCount() { upsCount++; }
        
        public int fps() {
            return fps > 0 ? fps : fpsCount;
        }
        
        public int ups() { return ups > 0 ? ups : upsCount; }
        
        protected double lastFrame() { return lastFrame; }
        
        public float frameTimeLimit() { return frameTimeLimit; }
        
        public void setFrameTimeLimit(float limit) { frameTimeLimit = limit; }
        
    }

}
