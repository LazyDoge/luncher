package edu.sky.luncher.domain;


import com.fasterxml.jackson.annotation.JsonView;
import edu.sky.luncher.util.Views;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Set;

@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Restaurant extends AbstractBaseEntity {

    @JsonView(Views.Name.class)
    private String name;

    @OneToMany
    @JoinTable(
            name = "administrators",
            joinColumns = @JoinColumn(name = "restaurant_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonView(Views.Body.class)
    private Set<User> administrators;


    public Restaurant() {
    }


    public Restaurant(String name) {
        this.name = name;
    }

    public Restaurant(Long id, String name, Set<User> administrators) {
        super(id);
        this.name = name;
        this.administrators = administrators;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getAdministrators() {
        return administrators;
    }

    public void setAdministrators(Set<User> administrators) {
        this.administrators = administrators;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "name='" + name + '\'' +
                ", administrators=" + administrators +
                ", id=" + id +
                '}';
    }
}
