package ru.yandex.practicum.front.config.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.ForwardedHeaderFilter;
import ru.yandex.practicum.front.service.AccountService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationProvider authenticationProvider) throws Exception {
        http
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/sign-up", "/account",
                                        "/login", "/logout", "/error", "/actuator/**").permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults())
                .authenticationProvider(authenticationProvider);
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(AccountService accountService) {
        return new RemoteUserDetailsService(accountService);
    }

    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder,
                                                         UserDetailsService userDetailsService) {
        return new RemoteAuthenticationProvider(passwordEncoder, userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {
        FilterRegistrationBean<ForwardedHeaderFilter> filterRegBean =
                new FilterRegistrationBean<>();
        filterRegBean.setFilter(new ForwardedHeaderFilter());
        filterRegBean.setOrder(0);
        return filterRegBean;
    }
}