package no.fredahl.engine;

import no.fredahl.engine.window.Options;
import no.fredahl.engine.window.Window;
import org.lwjgl.Version;

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
    private final Thread GLContext;
    private final Time frames;
    private final Object lock;
    public final Window window;
    public Application application;
    private boolean running;
    
    private Engine() {
        window = new Window();
        lock = new Object();
        frames = new Time();
        GLContext = new Thread(() -> {
            try {
                window.initialize();
                frames.init();
                application.start(window);
                running = true;
                float alpha;
                float frameTime;
                float accumulator = 0f;
                float delta = 1f / TARGET_UPS;
                while (running) {
                    frameTime = frames.frameTime();
                    accumulator += frameTime;
                    while (accumulator >= delta) {
                        if (!window.isMinimized())
                            application.input(delta);
                        application.update(delta);
                        frames.incUpsCount();
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
                    frames.incFpsCount();
                    frames.update();
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
            }
        }
    }
    
    
    private void sync() {
        
        double lastFrame = frames.lastFrame();
        double now = frames.timeSeconds();
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
            now = frames.timeSeconds();
        }
    }
    
    
    public synchronized static Engine get() {
        return instance == null ? instance = new Engine() : instance;
    }
    
    public synchronized void requestTask(Runnable runnable) {
        
        // new Thread().start();
    }
    
    public void exit() {
        System.out.println("Engine: window signalled to close..");
        instance.window.signalToClose();
    }
    
    
    private static class Time {
        
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
        
        public void init() {
            initTime = nanoTime();
            lastFrame = timeSeconds();
        }
        
        public float frameTime() {
            double timeSeconds = timeSeconds();
            float frameTime = (float) (timeSeconds - lastFrame);
            frameTime = Math.min(frameTime, frameTimeLimit);
            lastFrame = timeSeconds;
            timeAccumulator += frameTime;
            return frameTime;
        }
        
        public void update() {
            
            if (timeAccumulator > 1) {
                fps = fpsCount;
                ups = upsCount;
                fpsCount = upsCount = 0;
                timeAccumulator -= 1;
            }
        }
        
        public double timeSeconds() { return nanoTime() / 1_000_000_000.0; }
        
        public double runTime() { return nanoTime() - initTime; }
        
        public void incFpsCount() { fpsCount++; }
        
        public void incUpsCount() { upsCount++; }
        
        public int fps() {
            return fps > 0 ? fps : fpsCount;
        }
        
        public int ups() { return ups > 0 ? ups : upsCount; }
        
        public double lastFrame() { return lastFrame; }
        
        public float frameTimeLimit() { return frameTimeLimit; }
        
        public void setFrameTimeLimit(float limit) { frameTimeLimit = limit; }
        
    }

}
