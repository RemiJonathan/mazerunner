package ca.concordia.engweek.programming.amazeing.mazerunner;
import ca.concordia.engweek.programming.amazeing.mazerunner.repository.WebClientRepository;

public class MazerunnerAlgorithm {
    final char wall = '+';
    final char closedDoor = 'x';
    final char openedDoor = 'o';
    final char key = 'k';
    final char exit = '$';
    final char moved = ' ';
    int moveDir = 2; //0: north, 1: west, 2: south, 3:east
    int checkDir = 1; //0: north, 1: west, 2: south, 3:east
    int[] coord = new int[2]; // 0: row, 1: col
    int[] doorCoord = new int[2];
    int[] pastDoorCoord = new int[2];
    boolean keyFound = false;
    WebClientRepository webClientRepository = new WebClientRepository();

    public char goLeft(){
        coord[1] -= 1;
        return wall;
    }

    public char goRight(){
        coord[1] += 1;
        return wall;
    }

    public char goUp(){
        coord[0] -= 1;
        return moved;
    }

    public char goDown(){
        coord[0] += 1;
        return moved;
    }

    public void gameLoop(){
        char output;
        coord[0] = 1;
        coord[1] = 1;
        while (true) {
            try {
                // Check which side the wall is at (hug wall)
                output = move(checkDir);

                unitCheck(output, true);
                // hit a wall
                if (output == wall) {
                    output = move(moveDir);
                    // Check moveDir, found wall
                    if (output == wall) {
                        // Change direction of movement --> go right, hug down "rotate 90 degrees" (checkdir + 1) (movedir +1)
                        moveDir += 1;
                        moveDir %= 4;
                        checkDir += 1;
                        checkDir %= 4;
                    }
                    // Check moveDir, found key
                    else if (output == key) {
                        keyFound = true;
                    }
                    // Check moveDir, found closedDoor
                    else if (output == closedDoor) {
                        // store location of door, keep moving in same direction
                        doorCoord = foundClosedDoor(moveDir);
                    } else if (output == openedDoor) {
                        // do nothing, keep going
                        System.out.println("found open door! score");
                        // if we reached here, then we got ' ' successfully and moved in the intended direction
                        continue;
                    }
                    // Check hug wall, found key
                    else if (output == key) {
                        // 90 degree turn (clockwise)
                        moveDir -= 1;
                        moveDir %= 4;
                        checkDir -= 1;
                        checkDir %= 4;
                    /*
                    + + + + + + + + + +
                    + + + +   + + + + +
                    + + + + ^ k + + + +  chekDir = 3 (east), moveDir = 0 (north)
                    + + + +   + + + + +  checkDir = 2, movedir = 3
                    + + + + + + + + + +
                     */

                    }
                    // Check hug wall, found closed door
                    else if (output == closedDoor) {
                        // store location of door, keep moving in same direction
                        doorCoord = foundClosedDoor(checkDir);
                    } else if (output == openedDoor) {
                        if (pastDoorCoord != null && coord == pastDoorCoord) { // We have returned to a door we previously crossed
                            //backtrack and move in moveDir
                            move((checkDir + 2) % 4);
                            move(moveDir);
                        } else {
                            // change movedir to the checkdir then change checkdir
                            moveDir -= 1;
                            moveDir %= 4;
                            checkDir -= 1;
                            checkDir %= 4;
                            keyFound = false;
                        /*
                        + + + + + + + + + +
                        + + + +   + + + + +
                        + + + + ^ o       +  chekDir = 3 (east), moveDir = 0 (north)
                        + + + +   + + + + +  checkDir = 2, movedir = 3 east
                        + + + + + + + + + +
                         */
                            pastDoorCoord = coord;
                        }
                    } else if (output == key) {
                        // backtrack + one move in moveDir
                        // backtrack one step (checkdir +2) AND continue in moveDir
                        // move(checkdir +2)
                    } else if (output == closedDoor) {
                        // store location of door, keep moving in same direction
                    } else if (output == openedDoor) {
                        // change movedir to the checkdir then change checkdir
                    }
                    // hug wall side is empty space
                    // move into space, moveDir = checkDir, checkDir -= 1 (logic, if int below 0, switch to 3 --> %4)

                }
            }catch(Exception e){
                    System.out.println(e.getMessage());
            }
        }
    }

    public int[] foundClosedDoor(int dir){
        int[] res = new int[2];
        switch(dir){
            case 0: //door is north
                res[0] = coord[0] - 1;
                res[1] = coord[1];
                break;
            case 1: //door is west
                res[0] = coord[0];
                res[1] = coord[1] - 1;
                break;
            case 2: //door is south
                res[0] = coord[0] + 1;
                res[1] = coord[1];
                break;
            default: //door is east
                res[0] = coord[0];
                res[1] = coord[1] + 1;
        }
        return res;
    }

    public char move(int dir){
        char output;
        switch (dir) {
            case 0:
                output = goUp();
                break;
            case 1:
                output = goLeft();
                break;
            case 2:
                output = goDown();
                break;
            default:
                output = goRight();
        }
        return output;
    }

    public void rotateClockwise(){

    }

    public void rotateCounterClockwise(){

    }

    public void unitCheck(char out, boolean hug) {
        // checked hug-wall, found wall
        if (out == wall && hug) {
            // move to expected empty space
            out = move(moveDir);
            unitCheck(out, false);
        }
        // checked moveDir, found wall
        else if (out == wall && !hug) {
            // Change direction of movement --> go right, hug down "rotate 90 degrees" (checkdir + 1) (movedir +1)
            moveDir += 1;
            moveDir %= 4;
            checkDir += 1;
            checkDir %= 4;
        }else if(out == key){
            // backtrack one step (checkdir +2) AND continue in moveDir
            // move(checkdir +2)
            int temp = checkDir + 2;
            temp %= 4;
            move(temp);
            move(moveDir);
        }else if(out == closedDoor){
            // store location of door, keep moving in same direction
        } else if (out == openedDoor) {
            // change movedir to the checkdir then change checkdir
        }
        // if we reached here, then we got ' ' successfully and moved
    }

    public void testWebClient() {
        WebClientRepository.startGame();
        String out = WebClientRepository.movementRequest('d').block();
        System.out.println("the output of the webclient is " + out);
    }

}
