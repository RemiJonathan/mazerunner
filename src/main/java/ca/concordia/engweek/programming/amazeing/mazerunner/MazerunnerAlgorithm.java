package ca.concordia.engweek.programming.amazeing.mazerunner;
import ca.concordia.engweek.programming.amazeing.mazerunner.repository.WebClientRepository;
import netscape.javascript.JSObject;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.io.StringReader;
import java.util.Arrays;

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
    boolean inMaze = true;

    char[][] visualMap = new char[100][100];


    public char testDecode(String out){
        char end;
        String val = out.substring(32, out.lastIndexOf("\""));
        if(val.equals("\\u002B")){
            end = '+';
        }else if(val.equals("\\u0024")){
            end = ' ';
        }
        else {
            end = val.charAt(0);
        }
//        System.out.println("end : " + end);
        return end;
    }

    public char goLeft(){

        String out = WebClientRepository.movementRequest('l').block();
        char decodedChar = testDecode(out);
        visualMap[coord[0]][coord[1]] = decodedChar;
        if(decodedChar != wall && decodedChar != closedDoor){
            coord[1] -= 1;
        }
        return decodedChar;
    }

    public char goRight(){
        String out = WebClientRepository.movementRequest('r').block();
        char decodedChar = testDecode(out);
        visualMap[coord[0]][coord[1]] = decodedChar;
        if(decodedChar != wall && decodedChar != closedDoor){
            coord[1] += 1;
        }
        return decodedChar;

    }

    public char goUp(){
        String out = WebClientRepository.movementRequest('u').block();
        char decodedChar = testDecode(out);
        visualMap[coord[0]][coord[1]] = decodedChar;
        if(decodedChar != wall && decodedChar != closedDoor){
            coord[0] -= 1;
        }
        return decodedChar;

    }

    public char goDown(){
        String out = WebClientRepository.movementRequest('d').block();
        char decodedChar = testDecode(out);
        visualMap[coord[0]][coord[1]] = decodedChar;
        if(decodedChar != wall && decodedChar != closedDoor){
            coord[0] += 1;
        }
        return decodedChar;
    }

    public void gameLoop(){
        System.out.println("---------------------" + coord);
        WebClientRepository.startGame().block();
        char output;
        coord[0] = 1;
        coord[1] = 1;
        //empty out the array
        for(int i = 0; i<100; i++) Arrays.fill(visualMap[i], ' ');

        while (inMaze) {
            //printVisualMap();
            try {

                System.out.println("---------------------------------------" + coord[0] + ", " + coord[1]);
//                System.out.println("--------------------------------------- checkDir" + checkDir + ", moveDir: " + moveDir);
                // Check which side the wall is at (hug wall)
                output = move(checkDir);

                // Check hug wall, found wall
                if (output == wall || output == closedDoor) {
                    if(output == closedDoor){
                        doorCoord = foundClosedDoor(checkDir);
//                        System.out.println("check hug wall, found closed door! store and keep moving");
                    }else {
//                        System.out.println("check hug wall, found a wall so we move along");
                    }
                    output = move(moveDir);
                    // Check moveDir, found wall
                    if (output == wall) {
                        // Change direction of movement --> go right, hug down "rotate 90 degrees" (checkdir + 1) (movedir +1
                        rotateCounterClockwise();
//                        System.out.println("move dir found wall --> rotate counter clockwise");
                    }
                    // Check moveDir, found key
                    else if (output == key) {
                        keyFound = true;
//                        System.out.println("move dir found key --> keep moving");
                    }
                    // Check moveDir, found closedDoor
                    else if (output == closedDoor) {
                        // store location of door, keep moving in same direction
                        doorCoord = foundClosedDoor(moveDir);
                        rotateCounterClockwise();
//                        System.out.println("move dir found closed door @ coord " + doorCoord);
                    }
                    // Check moveDir found openedDoor
                    else if (output == openedDoor) {
                        // do nothing, keep going
//                        System.out.println("found open door! score");
                    }
                    else if (output == exit){
                        System.out.println("-------------------------------------------------------------");
                        System.out.println("----------------------the end---------------------------------------");
                        System.out.println("-------------------------------------------------------------");
                        inMaze = false;
                    }
                    else if(output == moved){
//                        System.out.println("move dir found empty space --> keep moving");
                        continue; //do nothing
                    }
                // Check hug wall, found key
                }else if (output == key) {
                    // 90 degree turn (clockwise)
//                    System.out.println("check hug wall found key --> rotate clockwise");
                    rotateClockwise();
                }
                // Check hug wall, found closed door
                else if (output == closedDoor) {
                    // store location of door, keep moving in same direction
                    doorCoord = foundClosedDoor(checkDir);
//                    System.out.println("check hug wall, found closed door! store and keep moving");
                    output = move(moveDir);
                }
                else if (output == exit){
                    System.out.println("-------------------------------------------------------------");
                    System.out.println("----------------------the end---------------------------------------");
                    System.out.println("-------------------------------------------------------------");
                    inMaze = false;
                }
                // Check hug wall, found opened door
                else if (output == openedDoor) {
                    if (pastDoorCoord != null && coord == pastDoorCoord) { // We have returned to a door we previously crossed
                        //backtrack and move in moveDir
                        move((checkDir + 2) % 4);
                        move(moveDir);
//                        System.out.println("check hug wall, found open door we already went through! go back!");
                    } else {
                        // change movedir to the checkdir then change checkdir
                        rotateClockwise();
                        keyFound = false;
                        pastDoorCoord = coord;
//                        System.out.println("check hug wall, found open door! go through");
                        output = move(moveDir);

                    }
                }
                // Check hug wall, found empty space
                else if(output == moved){
                    System.out.println("check hug wall, found empty space. rotate clockwise");
                    rotateClockwise();
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
        moveDir -= 1;
        if(moveDir < 0){
            moveDir = 3;
        }
//        moveDir %= 4;
        checkDir -= 1;
        if(checkDir < 0){
            checkDir = 3;
        }
//        checkDir %= 4;
        System.out.println("---clockwise------------------------------------ checkDir" + checkDir + ", moveDir: " + moveDir);


        /*
        + + + + + + + + + +
        + + + +   + + + + +
        + + + + ^ k + + + +  chekDir = 3 (east), moveDir = 0 (north)
        + + + +   + + + + +  checkDir = 2, movedir = 3
        + + + + + + + + + +
         */

    }

    public void rotateCounterClockwise(){
        moveDir += 1;
        moveDir %= 4;
        checkDir += 1;
        checkDir %= 4;
        System.out.println("-------changed-------------------------------- checkDir" + checkDir + ", moveDir: " + moveDir);

    }

    public void printVisualMap(){
        for (int i = 0; i<100; i++){
            for (int j = 0; j<100;j++){
                System.out.print(visualMap[i][j]);
            }
            System.out.println();
        }
    }

    public char testWebClient() {
        WebClientRepository.startGame().block();
        String out = WebClientRepository.movementRequest('d').block();

        char val = 'e';
        try{
            val = out.charAt(32);
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("the output of the webclient is " + out);
        System.out.println("the output of the webclient is " + val);
        return val;
    }

}
