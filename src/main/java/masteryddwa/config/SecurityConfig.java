/*
Created by: Margaret Donin
Date created:
Date revised:
 */
package masteryddwa.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsService userDetails;

    @Autowired
    public void configureGlobalInDB(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetails).passwordEncoder(bCryptPasswordEncoder());
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/usersAll", "/userEditRoles", "/userEnableToggle", "/userDelete").hasRole("ADMIN")
                .antMatchers("/hashtagsAll", "/hashtagAdd", "/hashtagDelete").hasRole("ADMIN")
                .antMatchers("/postDeleteExpired", "/postDelete").hasRole("ADMIN")
                .antMatchers("/postsAll", "/postEdit", "/postAdd").hasAnyRole("EMPLOYEE")
                .antMatchers("/user", "/userEdit", "/commentAdd", "/editPassword").hasAnyRole("USER")
                .antMatchers("/", "/home", "/post", "/userAdd", "/postsByHashtag", "/postsByCreator").permitAll()
                .antMatchers("/css/**", "/js/**").permitAll()
                .anyRequest().hasRole("ADMIN, EMPLOYEE, USER")
            .and()
                .formLogin()
                .loginPage("/login")
                .failureUrl("/login?login_error=1")
                .permitAll()
            .and()
                .logout()
                .logoutSuccessUrl("/")
                .permitAll();
    }
}
