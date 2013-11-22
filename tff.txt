[33mcommit 11a7c1c016ec61107163389f19129996e081227c[m
Author: Lenox <Lenox@206.87.120.212>
Date:   Wed Oct 23 12:22:27 2013 -0700

    JSFML Window Second Attempt

[1mdiff --git a/CS410 Project/src/main/Main.java b/CS410 Project/src/main/Main.java[m
[1mnew file mode 100644[m
[1mindex 0000000..907fb4d[m
[1m--- /dev/null[m
[1m+++ b/CS410 Project/src/main/Main.java[m	
[36m@@ -0,0 +1,41 @@[m
[32m+[m[32mpackage main;[m[41m[m
[32m+[m[41m[m
[32m+[m[41m[m
[32m+[m[32mimport org.jsfml.graphics.RenderWindow;[m[41m[m
[32m+[m[32mimport org.jsfml.window.VideoMode;[m[41m[m
[32m+[m[32mimport org.jsfml.window.event.Event;[m[41m[m
[32m+[m[41m[m
[32m+[m[32mpublic class Main {[m[41m[m
[32m+[m[41m[m
[32m+[m	[32m/**[m[41m[m
[32m+[m	[32m * @param args[m[41m[m
[32m+[m	[32m */[m[41m[m
[32m+[m	[32mpublic static void main(String[] args) {[m[41m[m
[32m+[m		[32mRenderWindow window = new RenderWindow();[m[41m[m
[32m+[m[41m		[m
[32m+[m		[32mVideoMode mode = new VideoMode(800, 600);[m[41m[m
[32m+[m[41m		[m
[32m+[m		[32mwindow.create(mode, "Test");[m[41m[m
[32m+[m[41m		[m
[32m+[m		[32mwindow.setFramerateLimit(60);[m[41m[m
[32m+[m[41m		[m
[32m+[m		[32mboolean RUNNING = true;[m[41m[m
[32m+[m[41m		[m
[32m+[m		[32mwhile (RUNNING) {[m[41m[m
[32m+[m[41m			[m
[32m+[m			[32mfor (Event event : window.pollEvents()) {[m[41m[m
[32m+[m[41m				[m
[32m+[m				[32mif (event.type == Event.Type.CLOSED) {[m[41m[m
[32m+[m					[32mRUNNING = false;[m[41m[m
[32m+[m				[32m}[m[41m[m
[32m+[m			[32m}[m[41m[m
[32m+[m[41m			[m
[32m+[m			[32mwindow.clear();[m[41m[m
[32m+[m			[32mwindow.display();[m[41m[m
[32m+[m		[32m}[m[41m[m
[32m+[m[41m		[m
[32m+[m		[32mwindow.close();[m[41m[m
[32m+[m[41m[m
[32m+[m	[32m}[m[41m[m
[32m+[m[41m[m
[32m+[m[32m}[m[41m[m
