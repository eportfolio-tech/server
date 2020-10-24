package tech.eportfolio.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tech.eportfolio.server.common.constant.SecurityConstant;
import tech.eportfolio.server.security.JWTAuthenticationEntryPoint;
import tech.eportfolio.server.security.JWTAuthorizationFilter;
import tech.eportfolio.server.security.JwtAccessDeniedHandler;
import tech.eportfolio.server.service.impl.UserServiceImpl;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)

public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JWTAuthorizationFilter jwtAuthorizationFilter;
    @Autowired
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;
    @Autowired
    private JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Autowired
    private UserServiceImpl userDetailsService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().cors().and()
                // Don't keep track of session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // permit access to API doc
                .and().authorizeRequests().antMatchers(SecurityConstant.API_DOC).permitAll()
                // permit access to test endpoints
                .and().authorizeRequests().antMatchers(SecurityConstant.TEST).permitAll()
                // permit access to authentication endpoints
                .and().authorizeRequests().antMatchers(SecurityConstant.AUTHENTICATION).permitAll()
                // permit access to endpoint like GET /tags/
                .and().authorizeRequests().antMatchers(HttpMethod.GET, SecurityConstant.GET_ONLY).permitAll()
                // permit access to public POST endpoints
                .and().authorizeRequests().antMatchers(HttpMethod.POST, SecurityConstant.POST_ONLY).permitAll()
                // require authorization for other paths
                .anyRequest().authenticated().and().authorizeRequests().and().
                exceptionHandling().
                authenticationEntryPoint(jwtAuthenticationEntryPoint).
                accessDeniedHandler(jwtAccessDeniedHandler).and().
                addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
