package ca.concordia.engweek.programming.amazeing.mazerunner.repository;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class WebClientRepository {
    //Configuring client with base uri
    static WebClient client = WebClient.create("http://51.222.158.215:5001/Player");

    /**
     * Method use to make a call to the REST Api in order to start a game
     *
     * @return the game id
     */
    public static Mono<String> startGame() {
        return client.post()
                .uri("/StartTrainingGame")
                .header("Authorization", "Bearer 28526e68a1625a2abd4ac6dfba4dd102")
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);
    }

    /**
     * Method used to make a call to the REST api and receive information about the maze
     *
     * @param direction is the input sent to the server by post request
     * @return the value that the server responds to the REST Api with
     */
    public static Mono<String> movementRequest(char direction) {
        String body = String.format("{\"MoveDirection\": \"%s\"}", direction);
        return client.post()
                .uri("/DoMove")
                .header("Authorization", "Bearer 28526e68a1625a2abd4ac6dfba4dd102")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .bodyToMono(String.class);
    }
}
