import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

public class SeamCarver {
    private Picture mainPic; // represents the main picture
    private int picWidth; // number of pixels in the horizontal direction
    private int picHeight; // number of pixels in the vertical direction
    private boolean isTransposed; // whether or not the picture is currently
    // transposed


    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        // check to make sure picture is not null
        if (picture == null) {
            throw new IllegalArgumentException("Picture can't be null");
        }
        // initialize instance variables
        mainPic = new Picture(picture);
        picWidth = mainPic.width();
        picHeight = mainPic.height();
        isTransposed = false;
    }

    // current picture
    public Picture picture() {
        // create new picture object so that original picture object is not
        // mutated when calling picture()
        Picture tempPicture = new Picture(mainPic);
        return tempPicture;
    }

    // width of current picture
    public int width() {
        return picWidth;
    }

    // height of current picture
    public int height() {
        return picHeight;
    }

    // returns energy of pixel at column x and row y
    // y*picWidth + x is the index
    public double energy(int x, int y) {
        // needed for border cases, if not border, these values will stay the
        // same
        int xPlusOne = x + 1;
        int xMinusOne = x - 1;
        int yPlusOne = y + 1;
        int yMinusOne = y - 1;
        // border cases, since energy treats picture as a continuous loop, once
        // we get to the end of the picture or are at the start of the picture,
        // the next and previous x and y values are the endpoints or starting
        // points depending on which side of picture you are on
        if (x == 0) xMinusOne = picWidth - 1;
        if (x == picWidth - 1) xPlusOne = 0;
        if (y == 0) yMinusOne = picHeight - 1;
        if (y == picHeight - 1) yPlusOne = 0;
        // create variables for these values so that we don't call .getRGB()
        // too many times
        int getRGBofXplusOneY = mainPic.getRGB(xPlusOne, y);
        int getRGBofXminusOneY = mainPic.getRGB(xMinusOne, y);
        // calculate delta for each color X values
        double redDifferenceX = (getRGBofXplusOneY >> 16 & 0xFF) - (
                getRGBofXminusOneY >> 16 & 0xFF);
        double blueDifferenceX = (getRGBofXplusOneY >> 0 & 0xFF) - (
                getRGBofXminusOneY >> 0 & 0xFF);
        double greenDifferenceX = (getRGBofXplusOneY >> 8 &
                0xFF) - (getRGBofXminusOneY >> 8 & 0xFF);
        // create variables for these values so that we don't call .getRGB()
        // too many times
        int getRBGofXYPlusOne = mainPic.getRGB(x, yPlusOne);
        int getRGBofXYminusOne = mainPic.getRGB(x, yMinusOne);
        // calculate delta for each color Y values
        double redDifferenceY = (getRBGofXYPlusOne >> 16 & 0xFF) - (
                getRGBofXYminusOne >> 16 & 0xFF);
        double blueDifferenceY = (getRBGofXYPlusOne >> 0 & 0xFF) - (
                getRGBofXYminusOne >> 0 & 0xFF);
        double greenDifferenceY = (getRBGofXYPlusOne >> 8 & 0xFF)
                - (getRGBofXYminusOne >> 8 & 0xFF);
        // calculate X and Y gradients
        double xGradient = redDifferenceX * redDifferenceX
                + blueDifferenceX * blueDifferenceX
                + greenDifferenceX * greenDifferenceX;
        double yGradient = redDifferenceY * redDifferenceY
                + blueDifferenceY * blueDifferenceY
                + greenDifferenceY * greenDifferenceY;
        // return energy = xGradient + yGradient
        return Math.sqrt(xGradient + yGradient);
    }

    // private energy method which takes in an array called energyCalculated
    // that stores energy values we have already calculated so that we don't
    // have to calculate energies we have already calculated
    private double energyPrivate(int x, int y, double[][] energyCalculated) {
        // check for exceptions
        if (x >= picWidth || x < 0 || y < 0 || y >= picHeight) {
            throw new IllegalArgumentException("Pixel is not valid");
        }
        // if energy has not been calculated (value stored = 0), then calculate
        // energy and store it to the energyCalculated array and return that
        // energy value
        if (energyCalculated[x][y] == 0) {
            double result = energy(x, y);
            energyCalculated[x][y] = result;
            return result;
        }
        // if energy has already been calculated (value stored is not zero),
        // then return that energy value
        else {
            return energyCalculated[x][y];
        }
    }


    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        // new array representing seam we will return
        int[] result;
        // transpose picture because our findSeam() method finds a vertical
        // seam
        transpose(mainPic);
        // find the seam
        result = findSeam();
        // transpose picture back to normal
        transpose(mainPic);
        // return the seam
        return result;
    }

    // private helper method that transposes given Picture object
    private void transpose(Picture preTranspose) {
        // switch width and height for new picture because that's what happens
        // when picture is transposed
        int newWidth = preTranspose.height();
        int newHeight = preTranspose.width();
        // create new picture object using these new dimensions
        Picture postTranspose = new Picture(newWidth, newHeight);
        // transpose all the RGB values for each pixel to the new pixel location
        for (int y = 0; y < newWidth; y++) {
            for (int x = 0; x < newHeight; x++) {
                postTranspose.setRGB(y, x, mainPic.getRGB(x, y));
            }
        }
        // set picWidth and picHeight to the newWidth and newHeight in the
        // picture
        picWidth = newWidth;
        picHeight = newHeight;
        // set the mainPic instance variable to the new picture object created
        // with the new dimensions and transposed RGB values
        mainPic = postTranspose;
        // switch boolean variable because picture has been transposed
        isTransposed = !isTransposed;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        // findSeam() method find a vertical seam
        return findSeam();
    }

    // find vertical seam regardless of picture orientation
    private int[] findSeam() {
        // create an array of doubles that we store all the energies we have
        // calculated so that we don't recalculate energies we have already
        // calculated
        double[][] energyCalculated = new double[picWidth][picHeight];
        // initialize all values in the energyCalculated array to zero to start
        for (int i = 0; i < energyCalculated.length; i++) {
            for (int j = 0; j < energyCalculated[0].length; j++) {
                energyCalculated[i][j] = 0;
            }
        }
        // disTo matrix represents distance to each pixel in terms of
        // how much energy per pixel representing distance
        double[][] distTo = new double[picWidth][picHeight];
        // edgeTo represents edges of the seams being analyzed
        int[][] edgeTo = new int[picWidth][picHeight];
        // initialize above matrixes
        for (int col = 0; col < picWidth; col++) {
            for (int row = 0; row < picHeight; row++) {
                if (row == 0) {
                    // first line, source of paths, distance to each starting
                    // point is just the energy of that point
                    distTo[col][row] =
                            energyPrivate(col, row, energyCalculated);
                }
                else {
                    // since we are trying to minimize energy, set every other
                    // disTo to positive infinity
                    distTo[col][row] = Double.POSITIVE_INFINITY;
                }
                // set edgeTo to -1 for every value since there are no edges
                // created yet
                edgeTo[col][row] = -1;
            }
        }
        // topological order
        for (int row = 0; row < picHeight - 1; row++) {
            for (int col = 0; col < picWidth; col++) {
                // relax each edge (x, y)
                for (int i = -1; i <= 1; i++) {
                    if (col + i < 0 || col + i >= picWidth)
                        continue;
                    else if (distTo[col + i][row + 1] > distTo[col][row] +
                            energyPrivate(col + i, row + 1, energyCalculated)) {
                        distTo[col + i][row + 1] = distTo[col][row] +
                                energyPrivate(col + i, row + 1,
                                              energyCalculated);
                        edgeTo[col + i][row + 1] = col;
                    }
                }
            }
        }
        // represents final pixel
        int finalPix = 0;
        // loop through all final pixels and find one with smallest disTo
        for (int col = 1; col < picWidth; col++) {
            if (distTo[col][picHeight - 1] < distTo[finalPix][picHeight - 1]) {
                finalPix = col;
            }
        }
        // array of ints which will be the seam we return
        int[] seam = new int[picHeight];
        // add final pixel
        seam[picHeight - 1] = finalPix;
        // add all other pixels of the seam into the seam array
        for (int row = picHeight - 2; row >= 0; row--) {
            seam[row] = edgeTo[finalPix][row + 1];
            finalPix = seam[row];
        }
        // return the seam
        return seam;
    }

    // private method to remove the seam, removes vertical seam
    private void removeSeam(int[] seam) {
        int previous = seam[0]; // previous value in seam, start at 0 position
        // new picture with a width of width-1 because we are removing a
        // vertical seam
        Picture newpic = new Picture(picWidth - 1, picHeight);
        // loop through seam and remove pixels
        for (int y = 0; y < picHeight; y++) {
            int current = seam[y]; // current pixel being removed
            // illegal argument exception for invalid seams
            if (current < 0 || current >= picWidth || current - previous > 1
                    || current - previous < -1)
                throw new IllegalArgumentException("");
            // set current pixel to the pixel next to it to remove it
            for (int x = 0; x < picWidth - 1; x++) {
                int possX = x; // pixel next to current pixel
                if (x >= current)
                    possX += 1;
                // set current pixel's colors to colors of the pixel next to it
                // to remove current pixel
                newpic.setRGB(x, y, mainPic.getRGB(possX, y));
            }
            // set previous to current and go to next pixel in the seam
            previous = current;
        }
        // set new picture with removed seam to mainPic instance variable
        mainPic = newpic;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        // boolean represents if picture is transposed or not
        boolean transp = false;
        // check for illegal arguments
        if (seam == null) {
            throw new IllegalArgumentException();
        }
        if (seam.length != picWidth) {
            throw new IllegalArgumentException();
        }
        // transpose the picture if it is not already transposed because
        // removeSeam method removes a vertical seam so to remove a horizontal
        // seam we need to transpose the picture first
        if (!isTransposed) {
            transpose(mainPic);
            transp = true; // set transp to true because picture is transposed
        }
        // another illegal argument exception if picHeight is less than 1
        if (picHeight < 1) {
            throw new IllegalArgumentException();
        }
        // remove the seam and reduce picture height by 1
        removeSeam(seam);
        picHeight--;
        // transpose image back to normal
        if (transp)
            transpose(mainPic);
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        // check exceptions and illegal arguments
        if (seam == null)
            throw new IllegalArgumentException();

        if (seam.length != picHeight)
            throw new IllegalArgumentException();
        // transpose the picture if it is already transposed because removeSeam
        // method automatically removes a vertical seam
        if (isTransposed) {
            transpose(mainPic);
        }
        // another illegal argument exception if width is less than 1
        if (picWidth < 1)
            throw new IllegalArgumentException();
        // remove the seam and decrease the width of the picture by 1
        removeSeam(seam);
        picWidth--;
    }

    //  unit testing (required)
    public static void main(String[] args) {

        // create new picture object
        Picture newPicture = new Picture("3x4.png");
        // create new SeamCarver object using picture object
        SeamCarver test1 = new SeamCarver(newPicture);
        // test picture() method
        Picture picture2 = test1.picture();
        // test height and width methods
        StdOut.println("Height of 3x4.png: " + test1.height());
        StdOut.println("Width of 3x4.png: " + test1.width());
        // transpose second picture we made and show the picture to see if
        // it transposed properly
        test1.transpose(picture2);
        picture2.show();
        // print out energy of point (1, 2)
        StdOut.println("Energy of point (1, 2): " + test1.energy(1, 2));
        // find vertical and horizontal seams and store these seams
        int[] horizontalSeam = test1.findHorizontalSeam();
        int[] verticalSeam = test1.findVerticalSeam();
        // remove both seams found
        test1.removeHorizontalSeam(horizontalSeam);
        test1.removeVerticalSeam(verticalSeam);
        // show picture with seams removed
        test1.picture().show();

        // read me testing
        /*
        Stopwatch timer = new Stopwatch();
        Picture timeTestPicture = new Picture("stadium2000-by-250.png");
        SeamCarver timeTest = new SeamCarver(timeTestPicture);
        timeTest.removeVerticalSeam(timeTest.findVerticalSeam());
        timeTest.removeHorizontalSeam(timeTest.findHorizontalSeam());
        double elapsedTime = timer.elapsedTime();
        StdOut.println(elapsedTime);

         */

    }

}
