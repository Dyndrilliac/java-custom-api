*******************************************************************

* Title:  Custom Java API
* Author: [Matthew Boyette](mailto:Dyndrilliac@gmail.com)
* Date:   2012-2016

*******************************************************************

This project represents an effort to recycle more of my Java code and promote modular design practices. Basically this is a collection of classes, methods, and interfaces that I have found useful or interesting. This code is a constant work in progress and updates happen frequently. I also use the the [standard Java library](http://introcs.cs.princeton.edu/java/stdlib/) from the text _Introduction to Computer Science in Java_ and the [sample algorithms](http://algs4.cs.princeton.edu/code/) from the text _Algorithms, 4th Edition_ both of which are written by __Robert Sedgewick__ and __Kevin Wayne__. These libraries are not included within this repository. You can dwnload them from the provided links and include them in your build path.

# Package/Class Listing

1. ## api.gui.draw

	* Car: 
	* Clock: creates simple clocks suitable for presentation inside an application's GUI window.
	* Fan: 
	* Fans: 
	* HealthGrid: provides a graphical display to the user showing a simulation of an initial infection vector spreading through a population operating under certain fundamental rules.
	* Parabola: 

2. ## api.gui.swing

	* ApplicationWindow: represents a GUI window for an application.
	* CarGUI: 
	* RichTextPane: creates text panes for applications with rich formatting capabilities and built-in file I/O.

3. ## api.util

	* EventHandler: simple mechanism for providing event hooks to objects.
	* Games: contains utility methods useful for games, animated applications, and applications accompanying games.
	* Lexer: contains the ground work for a very basic RegEx lexical analyzer to split data up into tokens which can then be further processed.
	* Mathematics: contains utility methods for performing mathematical tasks such as identifying whether an integer is prime and more.
	* ObjectCloner: contains a method to make a deep copy or clone of an object using Java's built-in serialization mechanism.
	* Support: contains utility methods for validating data, simple I/O, displaying miscellaneous GUI components, executing shell commands, and more.

5. ## api.util.datastructures

	* TODO

6. ## api.util.games

	* TODO

7. ## api.util.mathematics

	* TODO

8. ## api.util.networking

	* RMI: contains utility classes/interfaces and methods for providing simple RMI-based TCP network communication.
	* Sockets: contains utility classes/interfaces and methods for providing simple socket-based TCP network communication.
	* SQL: contains utility classes/interfaces and methods for providing simple Oracle SQL execution using JDBC.

9. ## api.util.sicxe

	* TODO

# Using My Custom Java API

These instructions are going to apply primarily to the Eclipse IDE, because that is what I use. For other IDEs, you will need to consult the official documentation for your particular software.

1. The first step to putting the API into action in your project is to clone the Git repository for the API.
2. Next, import it into Eclipse. This can be accomplished by creating a new project (I call mine "API Framework"). Next, right-click on the new project you just created and go down to "Properties". Select "Java Build Path" from the pane on the left-hand side of the screen. Make sure you are on the "Source" tab. First, remove any folders that are currently in the list by selecting them and clicking "Remove". Next, click on "Link Source". A new window will open. Click "Browse" and find the directory where you cloned the Git repository and select it. Make sure to click on the radio button to update exclusion filters to resolve any possible nesting problems. Then click "Finish".
3. Right-click on the project in the package explorer that you want to use with the API. Click on "Properties". Select "Java Build Path" from the pane on the left-hand side of the screen just as you did earlier. Click on the "Projects" tab, just to the right of "Source", then click on "Add". This will bring up another screen where you can make your selections. The screen will contain all currently open projects (so if the API project isn't open, you won't see it in the list!). Check the box next to each project that you want to add to the build path. Make sure to click the "OK" buttons to get back to the main screen.

You should now be able to invoke any class or method from the API as if the source files had been copied into the project.
