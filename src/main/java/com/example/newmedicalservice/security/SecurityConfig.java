package com.example.newmedicalservice.security;

import com.example.newmedicalservice.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;



//@EnableWebSecurity(debug = true)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    @Autowired
    private CustomUserDetailsService userDetailsService;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/admin/**").hasRole("admin")
                .antMatchers("/customers/**").hasAnyRole("admin", "secretary")
                .antMatchers("/**").permitAll()
       //         .and().formLogin();
                .and().formLogin()
                .and()
                .logout()
                .logoutUrl("/perform_logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");




    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "https://192.168.88.90:3000/",
                        "https://192.168.88.26:8080/", "https://192.168.88.26:3000/").allowCredentials(true)
                .allowedMethods("*");
    }




}


/*
@Component
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    private String adminPassword;
    private String user1Password;
    private String user2Password;

    @Autowired
    public SecurityConfig(@Value("${password.user1}") String user1Password,
                          @Value("${password.user2}") String user2Password,
                          @Value("${password.admin}") String adminPassword) {
        this.user1Password = user1Password;
        this.user2Password = user2Password;
        this.adminPassword = adminPassword;
    }

    private final PasswordEncoder pwEncoder =
            PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Bean
    UserDetailsService authentication() {
        UserDetails secretary1 = User.builder()
                .username("user1")
                .password(pwEncoder.encode(user1Password))
                .roles("USER")
                .build();
        UserDetails secretary2 = User.builder()
                .username("user2")
                .password(pwEncoder.encode(user2Password))
                .roles("USER")
                .build();
        UserDetails admin = User.builder()
                .username("admin")
                .password(pwEncoder.encode(adminPassword))
                .roles("USER", "ADMIN")
                .build();
        return new InMemoryUserDetailsManager(secretary1, secretary2, admin);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
        .csrf().disable()
                .authorizeRequests()
              //  .antMatchers("/**").permitAll();
             //   .antMatchers("/login").not().fullyAuthenticated()
                .antMatchers(HttpMethod.GET, "/displayDocumentsTemplates/**").permitAll()
                .mvcMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin().permitAll();
              //  .defaultSuccessUrl("/");;

//             //   .loginPage("/login")
//                .loginProcessingUrl("/http://localhost:8080/login")
//                .usernameParameter("login")
//                .passwordParameter("password")

//                .loginPage("/login.html")
//                .loginProcessingUrl("/login")
//                .defaultSuccessUrl("/schedule.html", true) // TODO
//                .and()
//                .rememberMe().tokenValiditySeconds(86400);
//                .and()
//                .httpBasic();


    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000").allowCredentials(true)
                .allowedMethods("*");
    }




}
*/