
package com.whitecape.flayes.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.whitecape.flayes.Security.AuthEntryPointJwt;
import com.whitecape.flayes.Security.AuthTokenFilter;
import com.whitecape.flayes.services.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(

		prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;

	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}

	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}

	@Override
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {

		// authenticationManagerBuilder.inMemoryAuthentication().withUser("achref_hamzaoui")
		// .password(passwordEncoder().encode("achref_hamzaoui")).roles("ADMIN").authorities("XXXX","YYYYY");
		authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable().exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
				.antMatchers("/api/auth/**").permitAll().antMatchers("/**/**/**")
				.hasAnyRole("ADMIN", "FREELANCER", "CLIENT").antMatchers("/**/**").permitAll().antMatchers("/domain/**")
				.permitAll().antMatchers("/skill/**/**").hasRole("ADMIN").antMatchers("/announcement/**/**").permitAll()
				.antMatchers("/subdomain/**/**").hasRole("ADMIN").antMatchers("/FreelancerProfil/**/**")
				.hasRole("ADMIN").antMatchers("/Education/**/**").hasRole("ADMIN").antMatchers("/Experience/**/**")
				.hasRole("ADMIN").antMatchers("/**/**/**").permitAll()
				
				// .antMatchers("/skill/add").hasAuthority("XXXX")
				// .antMatchers("/skill/add").hasAuthority("YYYY")
				.anyRequest().authenticated();
		http.sessionManagement()
        .maximumSessions(1) // Configurez le nombre maximal de sessions par utilisateur selon vos besoins
        .sessionRegistry(sessionRegistry()); // Assurez-vous de référencer le bean SessionRegistry dans votre configuration


		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
	}
}