
## 3D Graphics Engine

**A core framework for OpenGL graphics applications in the Java Environment.**

Providing everything you need to create a window, process input and
run an OpenGL application.

Modern [OpenGL](https://www.opengl.org/) through the [LWJGL 3](https://www.lwjgl.org/) library for fast GPU rendering.

Dividing window events and graphics into separate threads for seamless rendering.

I'm running it on Windows, but it should also support macOS and Linux.
_Requires OpenGL version: 4.2_

The example in the example2 package shows you how you could set it up.
The entry point is Main.java. Use the mouse to pan camera/ double click to select.
WASD to move around.
(example1 is from an older version and contains a lot of retracted classes.
The example1 package will be removed soon)

_Currently, working on lights and Uniform Buffer Objects_ (18/12/21)

From example2:

![1](https://github.com/fre-dahl/GraphicsEngine3D/blob/main/screenshots/heightmap00.png?raw=true)


### On the technical side:


#### Engine.java

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

Having window events separate from rendering avoids freezing and stuttering of
the application on window changes. You could let's say, resize the window while
having continuous updated graphics.

Implementing this is not straight forward. 
Since most glfw (Window) functionality is exclusive to the main-thread. And OpenGL
context can only be current on a single thread at a time.

The latter is not the worst. You call a line of code on the Application thread
to make the context current. But you can not have window related callbacks calling
OpenGL methods directly. And you can not have state-changes in the application
trigger window functionality directly. You could for instance not close the Window
from the application thread. So we have to manage this in some way...

#### Communication between threads

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






