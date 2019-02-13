//package com.csye6225.spring2019.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import javax.servlet.http.HttpServletResponse;
//
//@Configuration
//public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//
////    @Autowired
////    private AuthenticationEntryPoint authenticationEntryPoint;
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .antMatchers("/user/register").permitAll()
//                .antMatchers(("/note")).authenticated()
//                .antMatchers(("/login")).authenticated()
//                .antMatchers(("/note/*")).authenticated()
//                .antMatchers("/").authenticated()
//                .and()
//                .exceptionHandling().authenticationEntryPoint((request, response, exception) -> {
//            response.setContentType("application/json");
//            response.getOutputStream().print("{\"error\":\"Unauthorized. Please login.\"}");
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        })
//                .and()
//                .httpBasic()
//                .and()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .csrf().disable();
//    }
//
////    @Bean
////    public PasswordEncoder passwordEncoder() {
////        return new BCryptPasswordEncoder();
////    }
//
//     @Autowired
//     public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//          auth.inMemoryAuthentication().withUser("jinansi").password("{noop}password").roles("USER");
////          auth.userDetailsService()
//     }
//
//}
