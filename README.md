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
My realization of an algorithm takes as in input the directory with image puzzles and the original
image too. For each puzzle, the algorithm iterating through the specified areas of the original
image and calculate the difference between the puzzle and area of image. When the difference between
image area and puzzle gets minimal, we push the puzzle to the Map with calculated index of the puzzle
as key, and puzzle itself as value. When all puzzles added to the Map with corresponding indexes, special
method iterates through the Map and draw a new image. As the result an InputStream gets returned.

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
   1. Registration and login
   2. User profile
   3. Scoreboard for each puzzle
   4. Add categories like `new`, `popular` and `you last solve`
   5. Support more image types to be uploaded