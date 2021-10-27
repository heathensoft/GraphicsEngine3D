package no.fredahl.engine.window;

import no.fredahl.engine.graphics.Color;
import no.fredahl.engine.window.events.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.nio.IntBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * https://www.glfw.org/docs/latest/
 *
 * @author Frederik Dahl
 * 21/10/2021
 */


public class Window implements GLFWindow {
    
    private final long glfwThread;
    
    private long window;
    private long monitor;
    private String windowTitle;
    private Viewport viewport;
    private Options options;
    private final RequestQueue requestQueue;
    private GLFWErrorCallback errorCallback;
    private GLFWVidMode monitorDefaultVidMode;
    private GLFWVidMode vidModeBeforeWindowed;
    private KeyInput keyInput;
    private CharInput charInput;
    private MouseButtons mouseButtons;
    private MousePosition mousePosition;
    private MouseScroll mouseScroll;
    private WindowSize windowSize;
    private WindowPos windowPosition;
    private FrameBufferSize frameBufferSize;
    private IconifiedStatus iconifiedStatus;
    private final IntBuffer tmpBuffer1;
    private final IntBuffer tmpBuffer2;
    
    private boolean vsync;
    private boolean cullFace;
    private boolean windowed;
    private boolean resizable;
    private boolean antialiasing;
    private boolean showTriangles;
    private boolean lockAspectRatio;
    private boolean compatibleProfile;
    
    private int wwbfs; // window width before full-screen
    private int whbfs;
    
    
    private interface Request {
        /**
         * Requests are the means for non-main-threads to
         * call main-thread-only glfw functions.
         * Requests from main-thread are handled immediately,
         * requests from other threads are queued.
         * Queued requests are handled by the main-thread.
         * Queued requests are queried every n - millis,
         * determined by the engine.
         * Requests can be inside other requests.
         */
        void handle();
    }
    
    private static final class RequestQueue {
        
        private final Queue<Request> requests;
        private final Object lock;
        private final long glfwThread;
        
        RequestQueue(long glfwThread) {
            this.lock = new Object();
            this.glfwThread = glfwThread;
            this.requests = new ArrayDeque<>();
        }
        
        void handle() {
            synchronized (lock) {
                long current = Thread.currentThread().getId();
                if (current == glfwThread) {
                    while (!requests.isEmpty()) {
                        Request request = requests.remove();
                        request.handle();
                    }
                }
            }
        }
        
        void newRequest(Request request) {
            synchronized (lock) {
                if (request != null) {
                    long current = Thread.currentThread().getId();
                    if (current == glfwThread)
                        request.handle();
                    else requests.add(request);
                }
            }
        }
    }
    
    public Window() {
        glfwThread = Thread.currentThread().getId();
        tmpBuffer1 = BufferUtils.createIntBuffer(1);
        tmpBuffer2 = BufferUtils.createIntBuffer(1);
        requestQueue = new RequestQueue(glfwThread);
    }
    
    public void create(Options options) throws Exception {
        long current = Thread.currentThread().getId();
        if (current == glfwThread) {
            glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
            if (!glfwInit()) { // Initialize the GLFW library
                throw new Exception("Unable to initialize GLFW");
            }
            System.out.println("Window: configuring...");
            lockAspectRatio = options.lockAspectRatio();
            compatibleProfile = options.compatibleProfile();
            vsync = options.verticalSynchronization();
            showTriangles = options.showTriangles();
            antialiasing = options.antialiasing();
            resizable = options.resizableWindow();
            windowTitle = options.windowTitle();
            windowed = options.windowedMode();
            cullFace = options.cullFace();
            this.options = options;
    
            final int desiredWidth = options.desiredResolutionWidth();
            final int desiredHeight = options.desiredResolutionHeight();
            viewport = new Viewport(desiredWidth,desiredHeight);
            if (lockAspectRatio) viewport.lockAspectRatio(true);
    
            glfwDefaultWindowHints();
            glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
            glfwWindowHint(GLFW_RESIZABLE, resizable ? GLFW_TRUE : GLFW_FALSE);
            glfwWindowHint(GLFW_SAMPLES,antialiasing  ? 4 : 0);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            if (compatibleProfile) {
                glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE);
            }
            else {
                glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
                glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
            }
            System.out.println("Window: detecting primary monitor...");
            monitor = glfwGetPrimaryMonitor();
            if (monitor == NULL) throw new Exception("Window: failed to locate monitor");
            GLFWVidMode vidMode = getVidMode();
            System.out.println("Window: monitor default resolution: " + vidMode.width() + ":" + vidMode.height());
            System.out.println("Window: monitor default refresh rate: " + vidMode.refreshRate() + " Hz");
            monitorDefaultVidMode = vidModeBeforeWindowed = vidMode;
            System.out.println("Window: creating the GLFW window... ");
    
            if (windowed) {
                System.out.println("Window: creating windowed-mode window with desired resolution: " + desiredWidth + ":" + desiredHeight);
                window = glfwCreateWindow(desiredWidth,desiredHeight,windowTitle,NULL,NULL);
                if ( window == NULL ) throw new Exception("Failed to create the GLFW engine.window");
                System.out.println("Window: windowed-mode window created");
            }
            else {
        
                // We stick with the "go-to" resolution of the primary monitor if the monitor don't have support
                // for the desired resolution.
                // If so, depending on whether the aspect ratio is locked by the launch configuration, we readjust / don't readjust
                // the aspect ratio to MATCH that resolution. Locked: The viewport will reflect the locked ratio independent of resolution
                // with either horizontal or vertical border-boxes.
                // Conversely. On support for the desired resolution, the engine.window should display the view in
                // proper full-screen without border-box.
        
                int resolutionWidth, resolutionHeight;
                
                if (resolutionSupportedByMonitor(desiredWidth,desiredHeight)) {
                    System.out.println("Window: resolution supported by monitor");
                    resolutionWidth = desiredWidth;
                    resolutionHeight = desiredHeight;
                }
                else {
                    System.out.println("Window: resolution NOT supported by monitor");
                    System.out.println("Window: using default monitor resolution");
                    resolutionWidth = monitorDefaultVidMode.width();
                    resolutionHeight = monitorDefaultVidMode.height();
                }
                System.out.println("Window: creating fullScreen window with resolution: " + resolutionWidth + ":" + resolutionHeight);
                window = glfwCreateWindow(resolutionWidth,resolutionHeight,windowTitle,monitor,NULL);
                if ( window == NULL ) throw new Exception("Failed to create the GLFW engine.window");
                vidModeBeforeWindowed = vidMode = getVidMode();
                System.out.println("Window: fullScreen window created");
                System.out.println("Window: monitor resolution: " + vidMode.width() + ":" + vidMode.height());
                System.out.println("Window: monitor refresh rate: " + vidMode.refreshRate() + " Hz");
            }
            getWindowSize(tmpBuffer1,tmpBuffer2);
            int windowW = tmpBuffer1.get(0);
            int windowH = tmpBuffer2.get(0);
            System.out.println("Window: window size: " + windowW + ":" + windowH);
            getFrameBufferSize(tmpBuffer1,tmpBuffer2);
            int frameBufferW = tmpBuffer1.get(0);
            int frameBufferH = tmpBuffer2.get(0);
            System.out.println("Window: framebuffer size: " + frameBufferW + ":" + frameBufferH);
            viewport.update(frameBufferW,frameBufferH); // double check
            windowSize = new WindowSize();
            windowPosition = new WindowPos();
            iconifiedStatus = new IconifiedStatus();
            frameBufferSize = new FrameBufferSize(viewport);
            glfwSetWindowSizeCallback(window,windowSize);
            glfwSetWindowPosCallback(window,windowPosition);
            glfwSetWindowIconifyCallback(window,iconifiedStatus);
            glfwSetFramebufferSizeCallback(window,frameBufferSize);
            wwbfs = windowW;
            whbfs = windowH;
            if (windowed) centerWindow();
            glfwShowWindow(window);
        }
    }
    
    @Override
    public void initialize() {
        System.out.println("Window: initializing on separate thread...");
        glfwMakeContextCurrent(window);
        glfwSwapInterval(vsync ? 1 : 0);
        GL.createCapabilities();
        glEnable(GL_BLEND);
        setClearColor(Color.BLACK);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        if (showTriangles) glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        if (cullFace) {
            glEnable(GL_CULL_FACE);
            glCullFace(GL_BACK);
        }
        System.out.println("Window: window initialized");
    }
    
    @Override
    public void terminate() {
        long current = Thread.currentThread().getId();
        if (current == glfwThread) {
            System.out.println("Window: terminating...");
            System.out.println("Window: destroying window");
            glfwDestroyWindow(window);
            System.out.println("Window: freeing callbacks");
            if (windowPosition != null)
                windowPosition.free();
            if (windowSize != null)
                windowSize.free();
            if (iconifiedStatus != null)
                iconifiedStatus.free();
            if (frameBufferSize != null)
                frameBufferSize.free();
            if (charInput != null)
                charInput.free();
            if (keyInput != null)
                keyInput.free();
            if (mouseScroll != null)
                mouseScroll.free();
            if (mouseButtons != null)
                mouseButtons.free();
            if (mousePosition != null)
                mousePosition.free();
            System.out.println("Window: terminating glfw");
            glfwTerminate();
            if (errorCallback != null)
                errorCallback.free();
        }
    }
    
    @Override
    public void centerWindow() {
        requestQueue.newRequest(() -> {
            if (windowed){
                getWindowSize(tmpBuffer1,tmpBuffer2);
                int width = tmpBuffer1.get(0);
                int height = tmpBuffer2.get(0);
                GLFWVidMode vidMode = getVidMode();
                if (vidMode != null) {
                    int monitorResolutionWidth = vidMode.width();
                    int monitorResolutionHeight = vidMode.height();
                    glfwSetWindowPos(
                            window,
                            (monitorResolutionWidth - width) / 2,
                            (monitorResolutionHeight - height) / 2
                    );
                }
            }
        });
    }
    
    @Override
    public void windowed(int width, int height) {
        requestQueue.newRequest(() -> {
            if (windowed) glfwSetWindowSize(window,width,height);
            else glfwSetWindowMonitor(window,NULL,0,0, width,height,GLFW_DONT_CARE);
            windowed = true;
            centerWindow();
        });
    }
    
    @Override
    public void fullscreen(int width, int height) {
        requestQueue.newRequest(new Request() {
            @Override
            public void handle() {
    
                windowed = false;
            }
        });
    }
    
    public void waitEvents(float seconds) {
        long currentThread = Thread.currentThread().getId();
        if (currentThread == glfwThread)
            glfwWaitEventsTimeout(seconds);
    }
    
    @Override
    public synchronized KeyInput keyInput() {
        if (keyInput == null) {
            keyInput = new KeyInput();
            requestQueue.newRequest(() -> glfwSetKeyCallback(windowHandle(),keyInput));
        }return keyInput;
    }
    
    @Override
    public synchronized CharInput charInput() {
        if (charInput == null) {
            charInput = new CharInput();
            requestQueue.newRequest(() -> glfwSetCharCallback(windowHandle(),charInput));
        }return charInput;
    }
    
    @Override
    public synchronized MouseButtons mouseButtons() {
        if (mouseButtons == null) {
            mouseButtons = new MouseButtons();
            requestQueue.newRequest(() -> glfwSetMouseButtonCallback(windowHandle(),mouseButtons));
        }return mouseButtons;
    }
    
    @Override
    public synchronized MousePosition mousePosition() {
        if (mousePosition == null) {
            mousePosition = new MousePosition();
            requestQueue.newRequest(() -> glfwSetCursorPosCallback(windowHandle(),mousePosition));
        }return mousePosition;
    }
    
    @Override
    public synchronized MouseScroll mouseScroll() {
        if (mouseScroll == null) {
            mouseScroll = new MouseScroll();
            requestQueue.newRequest(() -> glfwSetScrollCallback(windowHandle(),mouseScroll));
        }return mouseScroll;
    }
    
    @Override
    public synchronized void setKeyInput(KeyInput callback) {
        if (keyInput == callback) return;
        if (keyInput != null)
            keyInput.free();
        keyInput = callback;
        if (keyInput != null) {
            requestQueue.newRequest(() -> glfwSetKeyCallback(windowHandle(),keyInput));
        }
    }
    
    @Override
    public synchronized void setCharInput(CharInput callback) {
        if (charInput == callback) return;
        if (charInput != null)
            charInput.free();
        charInput = callback;
        if (charInput != null) {
            requestQueue.newRequest(() -> glfwSetCharCallback(windowHandle(),charInput));
        }
    }
    
    @Override
    public synchronized void setMouseButtons(MouseButtons callback) {
        if (mouseButtons == callback) return;
        if (mouseButtons != null)
            mouseButtons.free();
        mouseButtons = callback;
        if (mouseButtons != null) {
            requestQueue.newRequest(() -> glfwSetMouseButtonCallback(windowHandle(),mouseButtons));
        }
    }
    
    @Override
    public synchronized void setMousePosition(MousePosition callback) {
        if (mousePosition == callback) return;
        if (mousePosition != null)
            mousePosition.free();
        mousePosition = callback;
        if (mousePosition != null) {
            requestQueue.newRequest(() -> glfwSetCursorPosCallback(windowHandle(),mousePosition));
        }
    }
    
    @Override
    public synchronized void setMouseScroll(MouseScroll callback) {
        if (mouseScroll == callback) return;
        if (mouseScroll != null)
            mouseScroll.free();
        mouseScroll = callback;
        if (mouseScroll != null) {
            requestQueue.newRequest(() -> glfwSetScrollCallback(windowHandle(),mouseScroll));
        }
    }
    
    public void handleRequests() {
        requestQueue.handle();
    }
    
    @Override
    public void show() {
        requestQueue.newRequest(() -> glfwShowWindow(window));
    }
    
    @Override
    public void hide() {
        requestQueue.newRequest(() -> glfwHideWindow(window));
    }
    
    @Override
    public void focus() {
        requestQueue.newRequest(() -> glfwFocusWindow(windowHandle()));
    }
    
    @Override
    public void maximize() {
        requestQueue.newRequest(() -> glfwMaximizeWindow(windowHandle()));
    }
    
    @Override
    public void minimize() {
        requestQueue.newRequest(() -> glfwIconifyWindow(windowHandle()));
    }
    
    @Override
    public void restore() {
        requestQueue.newRequest(() -> glfwRestoreWindow(windowHandle()));
    }
    
    @Override
    public void setWindowTitle(String title) {
        requestQueue.newRequest(() -> {
            glfwSetWindowTitle(window, title);
            windowTitle = title;
        });
    }
    
    @Override
    public void toggleVsync(boolean on) {
        glfwSwapInterval(on ? 1 : 0);
        vsync = on;
    }
    
    @Override
    public void lockAspectRatio(boolean lock) {
        viewport.lockAspectRatio(lock);
    }
    
    @Override
    public void updateViewport() {
        glViewport(
                viewport.x(),
                viewport.y(),
                viewport.width(),
                viewport.height()
        );
    }
    
    @Override
    public boolean isWindowed() {
        return windowed;
    }
    
    @Override
    public boolean vsyncEnabled() {
        return vsync;
    }
    
    @Override
    public boolean isMinimized() {
        return iconifiedStatus.isMinimized();
    }
    
    @Override
    public long windowHandle() {
        return window;
    }
    
    @Override
    public long monitorHandle() {
        return monitor;
    }
    
    @Override
    public int windowW() {
        return windowSize.width();
    }
    
    @Override
    public int windowH() {
        return windowSize.height();
    }
    
    @Override
    public int windowX() {
        return windowPosition.x();
    }
    
    @Override
    public int windowY() {
        return windowPosition.y();
    }
    
    @Override
    public int frameBufferW() {
        return frameBufferSize.width();
    }
    
    @Override
    public int frameBufferH() {
        return frameBufferSize.height();
    }
    
    @Override
    public int viewportW() {
        return viewport.width();
    }
    
    @Override
    public int viewportH() {
        return viewport.height();
    }
    
    @Override
    public int viewportX() {
        return viewport.x();
    }
    
    @Override
    public int viewportY() {
        return viewport.y();
    }
    
    @Override
    public float aspectRatio() {
        return viewport.aspectRatio();
    }
    
    @Override
    public float viewportInvW() {
        return viewport.inverseWidth();
    }
    
    @Override
    public float viewportInvH() {
        return viewport.inverseHeight();
    }
    
    @Override
    public Options options() {
        return options;
    }
    
    
    
    private boolean resolutionSupportedByMonitor(int resWidth, int resHeight) {
        ArrayList<VideoMode> videoModes = getVideoModes();
        for (VideoMode mode : videoModes) {
            int width = mode.getWidth();
            int height = mode.getHeight();
            if (width == resWidth && height == resHeight)
                return true;
        }
        return false;
    }
    
    private ArrayList<VideoMode> getVideoModes() {
        ArrayList<VideoMode> videoModes = new ArrayList<>();
        GLFWVidMode.Buffer modes = glfwGetVideoModes(monitor);
        if (modes != null) {
            for (int i = 0; i < modes.capacity(); i++) {
                modes.position(i);
                int width = modes.width();
                int height = modes.height();
                int redBits = modes.redBits();
                int greenBits = modes.greenBits();
                int blueBits = modes.blueBits();
                int refreshRate = modes.refreshRate();
                videoModes.add(new VideoMode(width, height, redBits, greenBits, blueBits, refreshRate));
            }
        }
        return videoModes;
    }
    
    private void getFrameBufferSize(IntBuffer w, IntBuffer h) {
        glfwGetFramebufferSize(window,w,h);
    }
    
    private void getWindowSize(IntBuffer w, IntBuffer h) {
        glfwGetWindowSize(window,w,h);
    }
    
   private void getWindowPosition(IntBuffer x, IntBuffer y) {
        glfwGetWindowPos(window, x, y);
    }
    
    private GLFWVidMode getVidMode() {
        return glfwGetVideoMode(monitor);
    }
    
}
