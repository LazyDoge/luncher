package edu.sky.luncher.domain;

import com.fasterxml.jackson.annotation.JsonView;
import edu.sky.luncher.util.Views;

import javax.persistence.*;


@Entity
@Table
public class Meal extends AbstractBaseEntity {


    @JsonView(Views.Name.class)
    private String name;

    @JsonView(Views.Body.class)
    private Long price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;


    public Meal() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}
