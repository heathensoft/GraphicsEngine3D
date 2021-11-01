
## 3D Graphics Engine

**The core of a OpenGL graphics application for the Java Environment.**

Modern [OpenGL](https://www.opengl.org/) through the [LWJGL 3](https://www.lwjgl.org/) library for fast GPU rendering.

Dividing window events and graphics into separate threads for seamless rendering.


(I'm running it on Windows x64, but it should also support macOS x64 and Linux x64)


### Engine.java

is the core class. The point of entry for the Graphics Engine.
It creates the Window, runs the Application and manages both.
The Application is run on a separate thread. And the applications' update
and render loops on separately tuned time intervals.

Running an application is done with the line:

```
Engine.get().start(Application app, Options options);
```
This call (Must be made from the main thread) will start and run
an object implementing the Application Interface with the given window options.
The Application will run until the window is closed.


The Engine manages two primary threads:

1. The thread it runs on (Main thread / GLFW thread) and
2. The Application thread (GL_Context_Thread).

The main thread manages the window, and the application thread runs the graphics.

Having window events seperate from rendering avoids freezing and stuttering of
the application on window changes. You could let's say, resize the window while
having continuous updated graphics.

Implementing this is not straight forward. 
Since most glfw (Window) functionallity is exclusive to the main-thread. And OpenGL
context can only be current on a single thread at a time.

The latter is not the worst. You call a line of code on the Application thread
to make the context current. But you can not have window related callbacks calling
OpenGL methods directly. And you can not have state-changes in the application
trigger window functionality directly. You could for instance not close the Window
from the application thread. So we have to manage this in some way...

### Communication between threads

Is divided into two concepts: Events and Requests.

Events are passed from the Main-Thread to the Application Thread.
Requests are passed from Any thread to the Main-Thread.

GLFW Thread | --> EVENT   -->  | Application Thread |
------------ | ------------- | ------------- |
GLFW Thread | <-- REQUEST <--| Any Thread

(Events and Requests are hidden and handled internally by the engine)


EVENTS

An event is really a series of triggers from
the OS layer to the the application input-processors.

Since the application cannot query the window directly for input events,
and the application runs independently. Events are queued for the application to collect.

Input Event handling layers:


OS | GLFW  | Engine | Engine | Application
------------ | ------------- | ------------- | ------------- | -------------
Event --> | Polling -->|Callback -->|Event -->|Prosessor


This layering is somewhat blurred, but the event-chain is more or less:


The Engine provides callbacks that GLFW will trigger on OS events.
(Callbacks are implementations of GLFW interfaces).
GLFW polls OS input.
The callbacks trigger engine-internal events.
The event is stripped to only contain useful data.
The event is then sent to subscribing threads via. a queue.
The Application is typically the listening thread. The Application
then collects and prosess those events through Keyboard, Mouse, Controller or TextProsessor objects.

(Some Events like Mouse movement are updated automatically.
And the Mouse only needs to query the latest values)

REQUESTS

All main-thread-only glfw methods are wrapped inside of the functional Interface Request.

* Requests are the means for non-main-threads to
* call main-thread-only glfw functions.
* Requests from main-thread are handled immediately,
* requests from other threads are queued.
* Queued requests are handled by the main-thread.
* Queued requests are queried every n - millis,
* determined by the engine.
* Requests can safely contain other requests.




1. Initializing the glfw-window.
2. Starting the Application on a seperate thread.
3. Making the OpenGL API current on the Application-thread.
Giving GPU access to the Application.

After initialization, the main thread will loop until signalled to exit:

4. Waits for window input events. When an event has occured,
it will trigger the appropriate callback i.e. window resizing, mouse hover etc.
Events like a key-press will get queued. Window related events will be handled
immediately by the main thread.

5. On a regular interval determined by the engine, it polls for Requests made
by other threads. Since (most) glfw functionallity is exclusive to the main-thread,
GLFW calls are wrapped inside Requests. Requests made by the Main-thread are handled 
immideately, requests by other threads are queud and executed when polled.
For instance. The Application can request for a new GamePad listener. Or it
could request for the window to get maximized etc.



