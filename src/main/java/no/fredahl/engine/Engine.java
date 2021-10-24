package no.fredahl.engine;

import no.fredahl.engine.window.Options;
import no.fredahl.engine.window.Window;
import org.lwjgl.Version;

import static java.lang.System.nanoTime;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

/**
 * @author Frederik Dahl
 * 22/10/2021
 */


public class Engine {
    
    
    private static final int TARGET_UPS = 30;
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
            window.initialize();
            frames.init();
            application.start(window);
            running = true;
            float alpha;
            float frameTime;
            float accumulator = 0f;
            float delta = 1f / TARGET_UPS;
            while (running) {
                glClear(GL_COLOR_BUFFER_BIT);
                frameTime = frames.frameTime();
                accumulator += frameTime;
                while (accumulator >= delta) {
                    application.update(delta);
                    frames.incUpsCount();
                    accumulator -= delta;
                }
                alpha = accumulator / delta;
                window.updateViewport();
                application.render(alpha);
                synchronized (lock) {
                    if (running) {
                        window.swapBuffers();
                    }
                }
                frames.incFpsCount();
                frames.update();
                if (!window.vsyncEnabled()) {
                    if (CAP_FPS) {
                        sync(TARGET_FPS);
                    }
                }
            }
            application.exit();
        });
    }
    
    
    public void start(Application app, Options options) {
    
        String platform = System.getProperty("os.name") + ", " + System.getProperty("os.arch") + " Platform.";
        int numProcessors = Runtime.getRuntime().availableProcessors();
        int JREMemoryMb = (int)(Runtime.getRuntime().maxMemory() / 1000000L);
        String jre = System.getProperty("java.version");
    
        System.out.println("\nWelcome!\n");
        System.out.println("SYSTEM INFO\n");
    
        System.out.println("---Running on: " + platform);
        System.out.println("---jre: " + jre);
        System.out.println("---Available processors: " + numProcessors);
        System.out.println("---Reserved memory: " + JREMemoryMb + " Mb");
    
        System.out.println("---LWJGL version: " + Version.getVersion());
        System.out.println("---GLFW version: " + glfwGetVersionString() + "\n");
        
        synchronized (this) {
            try {
                application = app;
                window.create(options);
                GLContext.start();
                while (!window.shouldClose()) {
                    // Main-Thread sleeps, awaiting
                    // input from the OS
                    window.waitEvents();
                }
                synchronized (lock) {
                    running = false;
                }
                window.hide();
                GLContext.join(300);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                window.terminate();
            }
        }
    }
    
    
    private void sync(int targetFPS) {
        
        double lastFrame = frames.lastFrame();
        double now = frames.timeSeconds();
        float targetTime = 0.96f / targetFPS;
        
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
    
    public void exit() {
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
