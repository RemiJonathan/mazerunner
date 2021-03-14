package ca.concordia.engweek.programming.amazeing.mazerunner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MazerunnerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MazerunnerApplication.class, args);
        MazerunnerAlgorithm mazerunnerAlgorithm = new MazerunnerAlgorithm();
        System.out.println("--------------------------------------------------------");
        mazerunnerAlgorithm.testWebClient();
        System.out.println("--------------------------------------------------------");
        mazerunnerAlgorithm.gameLoop();
    }
}
