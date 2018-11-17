package edu.sky.luncher.configuration;

import edu.sky.luncher.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)

public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;

    @Autowired
    public WebSecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/**")
                .authorizeRequests()
                    .antMatchers("/").permitAll()
                    .antMatchers("/restaurants/**").access("hasRole('ROLE_MANAGER')")
//                    .antMatchers("/administration/**").access("hasRole('ROLE_ADMIN')")
                    .anyRequest().authenticated()
                .and()
                    .csrf().disable()
//        https://stackoverflow.com/a/41568011/7667017
                    .httpBasic()
                .and()
                    .logout()
                    .permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
                .passwordEncoder(NoOpPasswordEncoder.getInstance());
    }

}
