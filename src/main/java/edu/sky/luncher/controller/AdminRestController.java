package edu.sky.luncher.controller;

import com.fasterxml.jackson.annotation.JsonView;
import edu.sky.luncher.domain.LunchMenu;
import edu.sky.luncher.domain.Meal;
import edu.sky.luncher.domain.Restaurant;
import edu.sky.luncher.domain.User;
import edu.sky.luncher.repository.LunchMenuRepository;
import edu.sky.luncher.repository.MealRepository;
import edu.sky.luncher.repository.RestaurantRepository;
import edu.sky.luncher.service.UserService;
import edu.sky.luncher.util.Views;
import edu.sky.luncher.util.exception.IllegalRequestDataException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static edu.sky.luncher.util.Util.checkAccess;
import static edu.sky.luncher.util.Util.getUri;

@RestController
@RequestMapping(AdminRestController.REST_URL)
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminRestController {

    static final String REST_URL = "/administration";

    private RestaurantRepository restaurantRepository;
    private MealRepository mealRepository;
    private LunchMenuRepository lunchMenuRepository;


    public AdminRestController(RestaurantRepository restaurantRepository, UserService userService, MealRepository mealRepository, LunchMenuRepository lunchMenuRepository) {
        this.restaurantRepository = restaurantRepository;
        this.mealRepository = mealRepository;
        this.lunchMenuRepository = lunchMenuRepository;
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Restaurant getRestaurant(
            @AuthenticationPrincipal User user
    ) {
        return restaurantRepository.findByAdministratorsContains(user);
    }


    @PostMapping(
            value = "/{restaurant}/meal",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Meal> createMeal(
            @PathVariable("restaurant") Restaurant restaurant,
            @AuthenticationPrincipal User user,
            @RequestBody Meal meal
    ) {
        checkAccess(restaurant, user);
        meal.setRestaurant(restaurant);
        Meal created = mealRepository.save(meal);
        return ResponseEntity.created(getUri(created.getId(), REST_URL)).body(created);
    }


    @GetMapping(
            value = "/{restaurant}/lunchMenu/{lunchMenu}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @JsonView(Views.Body.class)
    public LunchMenu getLunchMenu(
            @PathVariable("restaurant") Restaurant restaurant,
            @PathVariable("lunchMenu") LunchMenu lunchMenu,
            @AuthenticationPrincipal User user
    ) {
        checkAccess(restaurant, user, lunchMenu);
        return lunchMenu;
    }

    @PostMapping(
            value = "/{restaurant}/lunchMenu",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @JsonView(Views.Body.class)
    public ResponseEntity<LunchMenu> createLunchMenu(
            @PathVariable("restaurant") Restaurant restaurant,
            @AuthenticationPrincipal User user,
            @RequestBody(required = false) LunchMenu lunchMenu,
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        checkAccess(restaurant, user);
        if (lunchMenu.getId() != null) {
            throw new IllegalRequestDataException("Your lunch menu must be new");
        }
        if (date == null) {
            date = LocalDate.now();
        }
        LunchMenu byDate = lunchMenuRepository.findByDateAndRestaurant(date, restaurant);
        if (byDate != null) {
            lunchMenu.setId(byDate.getId());
        }
        lunchMenu.setRestaurant(restaurant);
        lunchMenu.setDate(date);
        LunchMenu created = lunchMenuRepository.save(lunchMenu);
        return ResponseEntity.created(getUri(created.getId(), REST_URL)).body(created);
    }

    @PutMapping(
            value = "/{restaurant}/lunchMenu/{lunchMenu}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @JsonView(Views.Body.class)
    public ResponseEntity<LunchMenu> updateLunchMenu(
            @PathVariable("restaurant") Restaurant restaurant,
            @AuthenticationPrincipal User user,
            @RequestBody(required = false) LunchMenu lunchMenu,
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable("lunchMenu") LunchMenu lunchMenuFromDB) {
        checkAccess(restaurant, user);
        if (date == null) {
            date = LocalDate.now();
        }
        lunchMenuFromDB.setRestaurant(lunchMenu.getRestaurant());
        lunchMenuFromDB.setDate(date);
        lunchMenuRepository.save(lunchMenuFromDB);
        return ResponseEntity.created(getUri(lunchMenuFromDB.getId(), REST_URL)).body(lunchMenuFromDB);
    }


    @PutMapping(
            value = "/{restaurant}/lunchMenu/{lunchMenu}/{meal}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public LunchMenu addMealToLunchMenu(
            @PathVariable("restaurant") Restaurant restaurant,
            @AuthenticationPrincipal User user,
            @PathVariable("lunchMenu") LunchMenu lunchMenu,
            @PathVariable("meal") Meal meal
    ) {
        checkAccess(restaurant, user, meal);
        lunchMenu.getMenuItems().add(meal);
        return lunchMenu;
    }

    @DeleteMapping(
            value = "/{restaurant}/lunchMenu/{lunchMenu}/{meal}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeMealFromLunchMenu(
            @PathVariable("restaurant") Restaurant restaurant,
            @AuthenticationPrincipal User user,
            @PathVariable("lunchMenu") LunchMenu lunchMenu,
            @PathVariable("meal") Meal meal
    ) {
        checkAccess(restaurant, user, meal);
        if (lunchMenu.getMenuItems() != null) {
            lunchMenu.getMenuItems().remove(meal);
        }
    }




}
