package de.linkshade.security;

import de.linkshade.security.oauth.OAuth2UserService;
import de.linkshade.security.oauth.OAuth2LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final String[] WHITE_LIST = {"/", "/short-urls", "/s/**", "/login/**", "/error",
            "/webjars/**", "/css/**", "/js/**", "/images/**"};
    private final OAuth2UserService oAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //basic config
                .csrf(Customizer.withDefaults())
                // authorization rules
                .authorizeHttpRequests(req -> req
                        .requestMatchers(WHITE_LIST).permitAll()
                        .requestMatchers("/my-urls").authenticated()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2login -> oauth2login
                        .loginPage("/login")
                        .userInfoEndpoint(info -> info.userService(oAuth2UserService))
                        .successHandler(oAuth2LoginSuccessHandler)
                )
                //logout config
                .logout(logout -> logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/")
                );
        return http.build();
    }
}
