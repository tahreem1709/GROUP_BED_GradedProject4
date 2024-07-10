package org.ems.employee_management_system.configs;

import jakarta.annotation.PostConstruct;
import org.ems.employee_management_system.dto.UserRegistrationDto;
import org.ems.employee_management_system.filters.LoginFilter;
import org.ems.employee_management_system.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @PostConstruct
    public void insertInitialUserData() {
        UserRegistrationDto registrationDtoAdmin = new UserRegistrationDto();
        registrationDtoAdmin.setEmail("admin@ems.com");
        registrationDtoAdmin.setPassword("AdminUser@123");
        registrationDtoAdmin.setFirstName("EMS");
        registrationDtoAdmin.setLastName("Admin");
        registrationDtoAdmin.setRole("ROLE_ADMIN");

        UserRegistrationDto registrationDtoUser = new UserRegistrationDto();
        registrationDtoUser.setEmail("user@ems.com");
        registrationDtoUser.setPassword("NormalUser@456");
        registrationDtoUser.setFirstName("EMS");
        registrationDtoUser.setLastName("User");
        registrationDtoUser.setRole("ROLE_USER");

        userService.save(registrationDtoAdmin);
        userService.save(registrationDtoUser);
        System.out.println("Added default users!");
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(new AntPathRequestMatcher("/h2-console/**"));
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.addFilterBefore(new LoginFilter(), DefaultLoginPageGeneratingFilter.class);
        return http.authorizeHttpRequests(auth -> auth
                                    .requestMatchers("/js/**", "/css/**", "/img/**")
                                    .permitAll()
                                    .anyRequest()
                                    .authenticated()
                                )
                .formLogin(login -> login.loginPage("/login").defaultSuccessUrl("/employees/", true)
                        .permitAll()
                )
                .logout(logout -> logout.logoutSuccessUrl("/login?logout").invalidateHttpSession(true).deleteCookies("JSESSIONID").clearAuthentication(true).logoutRequestMatcher(new AntPathRequestMatcher("/logout")).permitAll())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)).build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
}