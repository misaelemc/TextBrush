# Text Brush Challenge Resolution with Jetpack Compose

The Text Brush Challenge is a test of your attention to detail and craftsmanship, as it requires to create a drawing tool that allows users to draw text along a custom path. This README will guide you through the resolution of the challenge using Jetpack Compose.

## Challenge Overview

In this challenge, I created a drawing tool that enables users to draw text along a custom path. Unlike the typical draw tool, it will be drawing text characters. The primary goals were to:

Create a visually appealing and functional end product.
Calculate the position and rotation of each individual letter as it is drawn on the screen.
Achieve even spacing between the letters.
Ensure that the letters are correctly oriented to the direction of the drawn path.

## Approach

To successfully complete the Text Brush Challenge in Jetpack Compose, I considered the following steps and aspects:

1. Define a Custom Path
Create a custom path based on user input. This path was defined by a series of points representing the path's shape. Using the Path class to create and manipulate the path based on the input points.
2. Calculate Spacing
To ensure even spacing between the letters, calculate the total length of the custom path. I Divided this length by the number of characters to display. This gives the spacing between each character along the path.
3. Character Position and Rotation
For each character that I draw along the path, calculate its position and rotation:
Calculate the position by finding the point on the path based on the spacing and the character's position in the sequence.
Use mathematical calculations to determine the rotation angle of each character, aligning it with the tangent of the path at that point.
4. Drawing Text
Use Canvas to draw the text characters on the screen.
For each character, I calculated its position and rotation and draw it on the canvas.
5. Text Input Dialog
AlertDialog to allow users to type the text they want to display when drawn

## Features

1. User can draw a line where the entered text will be drawn.
2. User can undo and redo the changes drawn.
3. User can randomly change the typeface of the text.
4. User can reset entire canvas

