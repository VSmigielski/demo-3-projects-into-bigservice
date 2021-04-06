package org.sg.moviecatalogservice.resources;

import org.sg.moviecatalogservice.models.CatalogItem;
import org.sg.moviecatalogservice.models.Movie;
import org.sg.moviecatalogservice.models.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {
        // Steps:
        // get all rated movieIds

//        Reactive way - asynchronous
        WebClient.Builder builder = WebClient.builder();

        // get all rated movieIds
        List<Rating> ratingsList = Arrays.asList(
                new Rating("1234", 3),
                new Rating("5678", 4)
        );

        return ratingsList.stream()
                .map(rating -> {
                    // Movie movie = restTemplate.getForObject("http://localhost:8082/movies/" + rating.getMovieId(), Movie.class);

                    // Web Client Builder Line
                    // Instance of movie
                    Movie movie = webClientBuilder.build()
                            // Http Method Get, Post etc
                            .get()
                            // URI with Web URL
                            .uri("http://localhost:8082/movies/" + rating.getMovieId())
                            // Retrieval of the info/Go do fetch
                            .retrieve()
                            // Whatever body we get back, convert to instance of this class
                            .bodyToMono(Movie.class)
                            // blocking execution until Mono container is filled (through call)
                            .block();

                    return new CatalogItem(movie.getName(), "Description", rating.getRating());
                })
                .collect(Collectors.toList());

        // Previous code
//        return Collections.singletonList(
//                new CatalogItem( "Transformers", "Test", 4)
//        );
    }

}
