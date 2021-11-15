# AniMate

AniMate is DSL (Domain Specific Language) in the form of a Java applet made to create HTML and CSS animations seamlessly. This project was made for my CPSC410 course in a team of 4, however since it is a private repo on my student account I have decided to upload it here.


#Instructions
1. Install JDK14+ or a JRE.
2. Download the Release titled 'AniMate Executable'
3. Open input.tcts in a text editor and use the AniMate syntax described below.
4. Run AniMate.jar

Alteratively you can run `javac Main.java` and `java Main` inside src/ui

# DSL Syntax: 

Here is a small example of what you can make:
#### Input:
Canvas {color=blue}
Shape simple {rectangle, 200, 200, 0, 100, z-index=1,color=yellow} 
Animate simple {[300, 0, 0,  2, rotation=cw]}
### Output:
![Alt Text](https://i.imgur.com/b9mekEX.gif)

* Please note that all parameters should be entered in the orders as they are listed in the guide below, separated by commas between each one.
* Language is case sensitive so enter declarations as indicated 
* Required inputs won’t need the “parameter=” included, but optional inputs will.
	ie) Shape simple {rectangle, 200, 200, 0, 100, z-index = 1, color=yellow}

## Canvas 
Structure: Canvas {color=COLOR}
Canvas is an optional statement, but can be used to change the colour of the canvas. The canvas is the background of the animation. The top left corner of the canvas is the reference point (0,0). All shapes and animations will be positioned relative to the reference point. 

Required Input Parameters: 
Color: Color of the shape in a string (accepted colour names are: black, blue, gray, green, purple, red, white, yellow) or any colour in hex color code form (ie. “# _ _ _ _ _ _”). 

## Shape 
Structure: Shape NAME {SHAPE_TYPE, HEIGHT, WIDTH, POSX, POSY,  z-index=ZINDEX,  color=COLOR, angle=ANGLE}
 
The creation of a shape. When using Animate statements, a shape will only show on the canvas when its start time is indicated.
Required Input Parameters:
Name: The name you would like to give this shape(in an example below this would be ‘simple’). The name must be unique, no two shapes or groups can have the same name. All names must consist of letters only, no numbers, special characters, or spaces.
Shape Type: circle, rectangle, triangle
Height: height of the shape, some positive number. 
Width: Width of the shape, some positive number.
	Note: Triangles will be equilateral triangles. Triangles with longer height than width or vice versa will be stretched to fit the dimensions given. Circles can be also given unequal width and height to stretch it into an oval shape. 
POSX and POSY: The initial coordinates of the shape. The higher the number for POSX, the more to the right that the shape is placed. The higher the number for POSY, the more downwards that the shape is placed.
ie) Shape simple {rectangle, 200, 200, 0, 100}
 
Optional Input Parameters:
Z-index: Determines which shapes stacks on top of other shapes when they overlap. Shapes with higher POSZ are on top, lower POSZ on bottom. Think of it as layers, where shape on z-index=1 is below shape on z-index=2.
Color: Color of the shape in a string (accepted colour names are: black, blue, gray, green, purple, red, white, yellow, orange, brown) or any colour in hex color code form (ie. “# _ _ _ _ _ _”). Shapes are by default black if this parameter is not included.

For more information on hex color codes: https://www.w3schools.com/cssref/css_colors.asp 
Angle: the initial angular position of the shape. By default this is 0, and it ranges from -360 to 360 degrees. Positive angles rotate the shape or line clockwise, negative angles rotate counterclockwise.
	ie) Shape simple {rectangle, 200, 200, 0, 100, angle=-180}
 

## Line 
Structure: Line NAME {WIDTH, POSX, POSY,  z-index=ZINDEX,  color=COLOR, angle=ANGLE}
The creation of a line. When using Animate statements, a line will only show on the canvas when its start time is indicated.
Required Input Parameters:
Name: The name you would like to give this shape(in an example below this would be ‘simpleLine’). The name must be unique, no two shapes or groups can have the same name. All names must consist of letters only, no numbers, special characters, or spaces.
Width: Width of the line, some positive number.
POSX and POSY: The initial coordinates of the shape; POSX and POSY will be the left endpoint of the line. The higher the number for POSX, the more to the right that the shape is placed. The higher the number for POSY, the more downwards that the shape is placed.
Optional Input Parameters:
Z-index: Determines which shapes stacks on top of other shapes when they overlap. Shapes with higher POSZ are on top, lower POSZ on bottom. Think of it as layers, where shape on z-index=1 is below shape on z-index=2.
Color: Color of the shape in a string (accepted colour names are: black, blue, gray, green, purple, red, white, yellow, orange,  brown) or any colour in hex color code form (ie. “# _ _ _ _ _ _”). Shapes are by default black if this parameter is not included.
For more information on hex color codes: https://www.w3schools.com/cssref/css_colors.asp 
Angle: the initial angular position of the line. By default this is 0, and it ranges from -360 to 360 degrees.
	ie) Line simpleLine {200, 0, 100, angle=-180}
 

## Group
Structure: Group NAME {SHAPE_NAME_1, SHAPE_NAME_2, SHAPE_NAME_3...}
Group allows the user to group shapes together so that when Animate is called on a group name, it will animate all the shapes/lines in the group together. The group name must be unique, and cannot be the same as other previously named shapes/lines. The shapes/lines in the group must already have been created.
	Ie) Group multipleShapes {simpleSquare, simpleTriangle}
 
## Animate 
Structure: Animate NAME {[ X, Y, START_TIME, END_TIME, rotation=DIRECTION], …, loop=LOOP}
The creation of an animation. Each animation of a shape should be in a set of square brackets. The specified shape can be given multiple animations in one line, by separating the animations by a comma between each set of square brackets. The animation will NOT change the shape’s original X and Y positions, so the next animation on the same shape will start from the same initial position as before.
	Ie) Animate simple {[-50, -100, 0, 2, rotation=cw],[50, 100, 2, 4], loop=2}
Required Input Parameters:  
Name - the name of the shape or group you would like to animate; there must be a shape, line or group with the same name created.
ie) Shape simple {...}
     Animate simple {...} 
X - the x coordinate displacement of the animation. Negative values will move the shape(s) to the left, positive will move it to the right.
Y -  the y coordinate displacement of the animation. Negative values will move the shape(s) to the down, positive will move it to the up.
startTime - The starting time of the animation. Shapes/groups that have not yet appeared will appear at this time. 
endTime - The specified movement/rotation will end at this time.
Specify the existing name of the shape you want to animate and it will move from start position to end position in uniform motion from the given start time to the given end time.
Optional Input Parameters:
Rotation - the object will rotate around its center in a specified direction, either “cw” or “ccw”. Rotation must be inside a set of square brackets. The rotation, if applied to a group, will apply to each shape individually, not the group as a whole. (Circle will not really appear to be rotating, given its shape)
Loop: Determines the number of times that this set of animations will repeat itself. By default, this is 0. 

