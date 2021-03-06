package edu.sky.luncher.controller;

import com.fasterxml.jackson.annotation.JsonView;
import edu.sky.luncher.domain.Restaurant;
import edu.sky.luncher.domain.User;
import edu.sky.luncher.domain.Vote;
import edu.sky.luncher.domain.dto.RestaurantWithLunchMenu;
import edu.sky.luncher.service.RatingService;
import edu.sky.luncher.util.Views;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/rating")
public class RatingRestController {

    private RatingService ratingService;


    public RatingRestController(RatingService ratingService) {
        this.ratingService = ratingService;
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @JsonView(Views.Body.class)
    public ResponseEntity<List<RestaurantWithLunchMenu>> getAll(
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<RestaurantWithLunchMenu> all;
        if (date == null) {
            all = ratingService.getAllForToday();
        } else {
            all = ratingService.getAll(date);
        }
        return new ResponseEntity<>(all, HttpStatus.OK);
    }



//    https://stackoverflow.com/a/2691891/7667017
    @PutMapping(value = "/{id}")
    public ResponseEntity<Vote> vote(
            @PathVariable("id") Restaurant restaurant,
            @AuthenticationPrincipal User user
    ) {
        ratingService.vote(restaurant, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
