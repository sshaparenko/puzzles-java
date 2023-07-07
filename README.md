# Puzzle Application
This project is a program realization of a basic puzzle game

## Before the start
The only thing that you should do before the start, is set the static-locations field at the application.yml file to absolute path with pattern file:/your/path/here/src/main/resources/static
</br>That's it, you are ready to go!

## Web Application
Web application could be accessed from http://localhost:8080/
</br>Actions that you can perform on the page:
- Choose file to upload
- Load puzzle
- Move puzzles and try to solve
- Check the correction

### Puzzle creation
In order to get a puzzle from an image you should do the next steps:
1. By clicking on `Choose File`, choose your image. **Only jpg images could be accepted! The maximum image size is 10 MB!**
2. Click on `Upload` button to upload an image
3. To load puzzle, you should choose the same file again
4. Then click on `Load Puzzle` button to load
5. Now try to solve it
6. To check if final image is correct, click on `Submit`

### Puzzle auto-solve
The objective of the auto-solve function was to create an algorithm that will take as an input
a directory with image puzzles (puzzles are not numbered or marked) and return the solved image.
About the original image the algorithm should know nothing accept its width and height.

#### Description of an algorithm
##### Old Version
My realization of an algorithm takes as in input the directory with image puzzles and the original
image too. For each puzzle, the algorithm iterating through the specified areas of the original
image and calculate the difference between the puzzle and area of image. When the difference between
image area and puzzle gets minimal, we push the puzzle to the Map with calculated index of the puzzle
as key, and puzzle itself as value. When all puzzles added to the Map with corresponding indexes, special
method iterates through the Map and draw a new image. As the result an InputStream gets returned.

##### New version
The solve() method of the PuzzleAutoSolve class takes the name of the image whose puzzles the algorithm should assemble. The method writes the puzzle files to the LinkedList. It is important to note that the names of the image files will be of the type 0.jpg, 1.jpg, n.jpg, this was done for the correct operation of the web part of the application, the automatic assembly algorithm does not take file names into account. This can be checked by simply changing the names of the puzzle images. Next, the method calculates the width and height of the puzzles and the number of rows and columns of the entire puzzle. Next, each image is contained in a Puzzle type object.

The Puzzle class has 6 fields. An imagePuzzle field for storing an image of type BufferedImage. Top, right, bottom, left fields for storing links to neighboring puzzles in the form of Optional<Puzzle>. The index field contains the index of the puzzle in the overall image. The class has an isCorner() method that checks whether a puzzle is a corner of the image based on the references it has to neighboring puzzles.

Next, the setAdjacentPuzzle() method takes a List<Puzzle>. The method traverses the sheet and for each puzzle, looks for the one next to it and records it in the fields top, right, bottom, left.

Next, the findCorner() method that accepts a List<Puzzle> searches for a puzzle that is a corner of the image and calculates its index.

After that, the dfsTraversal method accepts an object of type Optional<Puzzle>, which was defined as the corner of the image, and makes a detour through all the top, right, bottom, left values, and at each step calculates the index of neighboring puzzles based on the index of the corner puzzle.

The search for a neighboring puzzle is carried out using the methods getTopDifferance, getRightDifferance, getBottomDifferance, getLeftDifferance. They calculate the difference between pixels at the edges of two images. If the calculated difference is acceptable, such puzzles will be considered adjacent.

These methods of calculating the difference have a significant limitation, since the threshold and maxDiff parameters are the criteria for the permissible difference between the edges of the images. Different images will have different values of these parameters, in order to successfully find the edge puzzle, so when sending a request, it is possible to specify query parameters threshold and maxDiff in order to try to find the necessary combination of values. The algorithm best assembles 2X2 and 3X3 puzzles. With an increased number of puzzles, problems may arise.

##### Demonstration of the new Auto-solve algorithm 
[![Watch the video](https://imgur.com/fLuj2EV.png)](https://youtu.be/G-OSbPe0MRw)

#### How to use
**Note!** Before using the auto-solve function you should upload your image on the web page via Upload button and get puzzles via Load Puzzle button.

</br>Puzzle auto-solve function can be accessed via `/api/v1/auto-solve`. The GET request should contain query parameter `imageName`.

</br>Here are the demonstrations of the web application and auto-solve algorithm work:

[![Watch the video](https://i.imgur.com/o3hrpZ2.png)](https://youtu.be/u7f6kud-jw0)

[![Watch the video](https://i.imgur.com/Z3keIS5.png)](https://www.youtube.com/watch?v=Tah_eFB5Mpc)

### Tests
To run all the tests, execute the `mvn verify` command
</br>For testing purposes I use `Mockito` and `AssertJ` libs

### Known Bags and Issues
1. When you upload the image and trying to load puzzles, you should choose the file once more to actually load it
2. In case you upload an image with size greater than 10 MB, it will lead to an error which is for now is not handled properly
3. Loaded puzzles are corrupted. To solve it,  click on the Load Puzzles several times
4. Not all exceptions are handled properly

### Plans
1. Fix all the bags that exist for now
2. Move the front-end to the React framework, so we can divide the back Spring API
from front-end and perform calls to API from React
3. Scale application to maintain multiple users 
4. Add registration and login 
5. Add user profile 
6. Add scoreboard for each puzzle 
7. Add categories like `new`, `popular` and `you last solve`
8. Support more image types to be uploaded