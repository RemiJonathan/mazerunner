package ca.concordia.engweek.programming.amazeing.mazerunner;
import ca.concordia.engweek.programming.amazeing.mazerunner.repository.WebClientRepository;
import reactor.core.publisher.Mono;

public class MazerunnerAlgorithm {
    final char wall = '+';
    final char closedDoor = 'x';
    final char openedDoor = 'o';
    final char key = 'k';
    final char exit = '$';
    final char moved = ' ';
    int moveDir = 2; //0: north, 1: west, 2: south, 3:east
    int checkDir = 1; //0: north, 1: west, 2: south, 3:east
    int[] coord = new int[2];
    boolean keyFound = false;
    WebClientRepository webClientRepository = new WebClientRepository();

    public char goLeft(){
        return wall;
    }

    public char goRight(){
        return wall;
    }

    public char goUp(){
        return moved;
    }

    public char goDown(){
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
                if(output == wall){
                    output = move(moveDir);
                    // Check moveDir, found wall
                    if(output == wall){
                        // Change direction of movement --> go right, hug down "rotate 90 degrees" (checkdir + 1) (movedir +1)
                        moveDir += 1;
                        moveDir %= 4;
                        checkDir += 1;
                        checkDir %= 4;
                    }else if(output == key){
                        // backtrack one step (checkdir +2) AND continue in moveDir
                        // move(checkdir +2)
                        keyFound = true;
                    }else if(output == closedDoor){
                        // store location of door, keep moving in same direction
                    } else if(output == openedDoor){
                        // change movedir to the checkdir then change checkdir
                    }
                    // if we reached here, then we got ' ' successfully and moved
                    continue;
                }else if(output == key){
                    // backtrack + one move in moveDir
                    // backtrack one step (checkdir +2) AND continue in moveDir
                    // move(checkdir +2)
                }else if(output == closedDoor){
                    // store location of door, keep moving in same direction
                }else if(output == openedDoor){
                    // change movedir to the checkdir then change checkdir
                }
                // hug wall side is empty space
                // move into space, moveDir = checkDir, checkDir -= 1 (logic, if int below 0, switch to 3 --> %4)

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public char move(int dir){
        char output;
        switch(dir){
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

    public void unitCheck(char out, boolean hug){
        // checked hug-wall, found wall
        if(out == wall && hug){
            // move to expected empty space
            out = move(moveDir);
            unitCheck(out, false);
        }
        // checked moveDir, found wall
        else if(out == wall && !hug){
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
        } else if(out == openedDoor){
            // change movedir to the checkdir then change checkdir
        }
        // if we reached here, then we got ' ' successfully and moved
    }

    public void testWebClient() {
        webClientRepository.startGame();
        Character output = webClientRepository.movementRequest('d').block();
        System.out.println("the output of the webclient is " + output);
    }

}
