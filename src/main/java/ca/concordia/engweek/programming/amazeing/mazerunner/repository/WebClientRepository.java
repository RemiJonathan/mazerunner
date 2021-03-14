package ca.concordia.engweek.programming.amazeing.mazerunner.repository;

import org.springframework.web.reactive.function.client.WebClient;

public class WebClientRepository {
    WebClient client = WebClient.create("https://51.222.158.215:5001/Player/");

    public char movementRequest(char direction){



        return 'x';
    }
}
