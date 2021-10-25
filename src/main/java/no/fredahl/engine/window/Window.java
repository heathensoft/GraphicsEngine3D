package no.fredahl.engine.window;

import no.fredahl.engine.window.events.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;


import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author Frederik Dahl
 * 21/10/2021
 */


public class Window implements GLFWindow {
    
    protected long contextThread;
    protected long mainThread;
    protected long window;
    protected long monitor;
    protected String windowTitle;
    protected Viewport viewport;
    protected Options options;
    protected GLFWErrorCallback errorCallback;
    protected GLFWVidMode monitorDefaultVidMode;
    protected GLFWVidMode vidModeBeforeWindowed;
    protected KeyInput keyInput;
    protected CharInput charInput;
    protected MouseButtons mouseButtons;
    protected MousePosition mousePosition;
    protected MouseScroll mouseScroll;
    protected WindowSize windowSize;
    protected WindowPos windowPosition;
    protected FrameBufferSize frameBufferSize;
    protected IconifiedStatus iconifiedStatus;
    protected IntBuffer tmpBuffer1;
    protected IntBuffer tmpBuffer2;
    
    protected boolean vsync;
    protected boolean cullFace;
    protected boolean windowed;
    protected boolean resizable;
    protected boolean antialiasing;
    protected boolean showTriangles;
    protected boolean lockAspectRatio;
    protected boolean compatibleProfile;
    
    protected int wwbfs; // engine.window width before full-screen
    protected int whbfs;
    
    /**
     * Creates a Window. Must be called from main thread.
     * Follow up with initialize() to create capabilities and make context current
     * @param options Window creation options
     */
    public void create(Options options) {
        
        mainThread = currentThread();
        
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        if (!glfwInit()) { // Initialize the GLFW library
            throw new IllegalStateException("Unable to initialize GLFW");
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
        glfwWindowHint(GLFW_SAMPLES,antialiasing? 4 : 0);
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
        if (monitor == NULL) {
            throw new IllegalStateException("Window: failed to locate monitor");
        }
        GLFWVidMode vidMode = getVidMode();
        System.out.println("Window: monitor default resolution: " + vidMode.width() + ":" + vidMode.height());
        System.out.println("Window: monitor default refresh rate: " + vidMode.refreshRate() + " Hz");
        monitorDefaultVidMode = vidModeBeforeWindowed = vidMode;
        
        System.out.println("Window: creating the GLFW window... ");
        
        if (windowed) {
            System.out.println("Window: creating windowed-mode window with desired resolution: " + desiredWidth + ":" + desiredHeight);
            window = glfwCreateWindow(desiredWidth,desiredHeight,windowTitle,NULL,NULL);
            if ( window == NULL ) throw new IllegalStateException("Failed to create the GLFW engine.window");
            System.out.println("Window: windowed-mode window created");
        }
        else {
            // We stick with the "go-to" resolution of the primary monitor if the monitor don't have support
            // for the desired resolution.
            //
            // If so, depending on whether the aspect ratio is locked by the launch configuration, we readjust / don't readjust
            // the aspect ratio to MATCH that resolution. Locked: The viewport will reflect the locked ratio independent of resolution
            // with either horizontal or vertical border-boxes.
            //
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
            if ( window == NULL ) throw new IllegalStateException("Failed to create the GLFW engine.window");
            vidModeBeforeWindowed = vidMode = getVidMode();
            System.out.println("Window: fullScreen window created");
            System.out.println("Window: monitor resolution: " + vidMode.width() + ":" + vidMode.height());
            System.out.println("Window: monitor refresh rate: " + vidMode.refreshRate() + " Hz");
        }
    
        tmpBuffer1 = BufferUtils.createIntBuffer(1);
        tmpBuffer2 = BufferUtils.createIntBuffer(1);
    
        getWindowSize(tmpBuffer1,tmpBuffer2);
        int windowW = tmpBuffer1.get(0);
        int windowH = tmpBuffer2.get(0);
        System.out.println("Window: window size: " + windowW + ":" + windowH);
    
        getFrameBufferSize(tmpBuffer1,tmpBuffer2);
        int frameBufferW = tmpBuffer1.get(0);
        int frameBufferH = tmpBuffer2.get(0);
        System.out.println("Window: framebuffer size: " + frameBufferW + ":" + frameBufferH);
        
        viewport.update(frameBufferW,frameBufferH); // double check
        
        windowPosition = new WindowPos();
        windowSize = new WindowSize();
        frameBufferSize = new FrameBufferSize(viewport);
        iconifiedStatus = new IconifiedStatus();
        
        glfwSetWindowPosCallback(window,windowPosition);
        glfwSetWindowSizeCallback(window,windowSize);
        glfwSetFramebufferSizeCallback(window,frameBufferSize);
        glfwSetWindowIconifyCallback(window,iconifiedStatus);
        
        mouseButtons = new MouseButtons();
        mousePosition = new MousePosition();
        mouseScroll = new MouseScroll();
        charInput = new CharInput();
        keyInput = new KeyInput();
    
        glfwSetMouseButtonCallback(windowHandle(),mouseButtons);
        glfwSetCursorPosCallback(windowHandle(),mousePosition);
        glfwSetScrollCallback(windowHandle(),mouseScroll);
        glfwSetCharCallback(windowHandle(),charInput);
        glfwSetKeyCallback(windowHandle(),keyInput);
        
        wwbfs = windowW;
        whbfs = windowH;
        
        if (windowed) centerWindow();
        glfwShowWindow(window);
    }
    
    /**
     * Make Context current in calling thread
     */
    @Override
    public void initialize() {
        long currentThread = currentThread();
        if (currentThread == mainThread) {
            contextThread = mainThread;
            System.out.println("Window: initializing on main thread...");
        }
        else {
            contextThread = currentThread;
            System.out.println("Window: initializing on separate thread...");
        }
        glfwMakeContextCurrent(window);
        glfwSwapInterval(vsync ? 1 : 0);
        GL.createCapabilities();
        glEnable(GL_BLEND);
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
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
        System.out.println("Window: terminating...");
        System.out.println("Window: destroying window");
        glfwDestroyWindow(window);
        System.out.println("Window: freeing callbacks");
        windowPosition.free();
        windowSize.free();
        iconifiedStatus.free();
        frameBufferSize.free();
        charInput.free();
        keyInput.free();
        mouseScroll.free();
        mouseButtons.free();
        mousePosition.free();
        System.out.println("Window: terminating glfw");
        glfwTerminate();
        errorCallback.free();
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
    
    // Main thread
    @Override
    public void centerWindow() {
        if (currentThread() == mainThread) {
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
        }
    }
    
    @Override
    public void windowed(int width, int height) {
    
    }
    
    @Override
    public void fullscreen(int width, int height) {
    
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
    public Viewport viewport() {
        return viewport;
    }
    
    @Override
    public Options options() {
        return options;
    }
    
    @Override
    public KeyInput keyInput() {
        return keyInput;
    }
    
    @Override
    public CharInput charInput() {
        return charInput;
    }
    
    @Override
    public MouseButtons mouseButtons() {
        return mouseButtons;
    }
    
    @Override
    public MousePosition mousePosition() {
        return mousePosition;
    }
    
    @Override
    public MouseScroll mouseScroll() {
        return mouseScroll;
    }
    
    // Main thread only
    protected boolean resolutionSupportedByMonitor(int resWidth, int resHeight) {
        ArrayList<VideoMode> videoModes = getVideoModes();
        for (VideoMode mode : videoModes) {
            int width = mode.getWidth();
            int height = mode.getHeight();
            if (width == resWidth && height == resHeight)
                return true;
        }
        return false;
    }
    // Main thread only
    protected ArrayList<VideoMode> getVideoModes() {
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
    
    // Main thread only
    protected void getFrameBufferSize(IntBuffer w, IntBuffer h) {
        glfwGetFramebufferSize(window,w,h);
    }
    
    // Main thread only
    protected void getWindowSize(IntBuffer w, IntBuffer h) {
        glfwGetWindowSize(window,w,h);
    }
    
    // Main thread only
    protected void getWindowPosition(IntBuffer x, IntBuffer y) {
        glfwGetWindowPos(window, x, y);
    }
    
    // Main thread only
    protected GLFWVidMode getVidMode() {
        return glfwGetVideoMode(monitor);
    }
    
    private synchronized long currentThread() {
        return Thread.currentThread().getId();
    }
}
