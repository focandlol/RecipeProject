package focandlol.recipeproject.auth.config;

import focandlol.recipeproject.auth.jwt.JwtFilter;
import focandlol.recipeproject.auth.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final DefaultOAuth2UserService customOauth2UserService;
  private final SimpleUrlAuthenticationSuccessHandler customOauth2AuthenticationSuccessHandler;
  private final JwtUtil jwtUtil;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http.csrf((auth) -> auth.disable());
    http.formLogin((auth) -> auth.disable());
    http.httpBasic((auth) -> auth.disable());

    http.oauth2Login((oauth2) -> oauth2.userInfoEndpoint(
                (userInfoEndpointConfig) -> userInfoEndpointConfig.userService(customOauth2UserService))
            .successHandler(customOauth2AuthenticationSuccessHandler));

    http.addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

    http.authorizeHttpRequests((auth) -> auth.requestMatchers("/").permitAll()
            .anyRequest().authenticated());

    http.sessionManagement((session) -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
  }
}
