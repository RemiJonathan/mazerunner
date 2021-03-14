package ca.concordia.engweek.programming.amazeing.mazerunner.repository;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class WebClientRepository {
    //Configuring client with base uri
    WebClient client = WebClient.create("https://51.222.158.215:5001/Player");

    /**
     * Method used to make a call to the REST api and receive information about the maze
     *
     * @param direction is the input sent to the server by post request
     * @return the value that the server responds to the REST Api with
     */
    public Mono<Character> movementRequest(char direction) {
        return client.post()
                .uri("/DoMove").header("Authorization", "Bearer 28526e68a1625a2abd4ac6dfba4dd102")
                .body(Mono.just(direction), char.class)
                .retrieve()
                .bodyToMono(char.class);
    }
}
