package danil.blog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import danil.blog.services.PersonDetailsService;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	private final PersonDetailsService personDetailsService;
	
	@Autowired
	public SecurityConfig(PersonDetailsService personDetailsService) {
		this.personDetailsService = personDetailsService;
	}
	
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
	{
		http.authorizeHttpRequests(authz -> authz
				.requestMatchers("/auth/login", "/auth/registration").permitAll()
				.requestMatchers("/main/admin").hasAnyRole("ADMIN", "KING_ADMIN")
				.anyRequest().hasAnyRole("USER", "ADMIN", "KING_ADMIN")
				
			)
			.formLogin(form -> form
					.loginPage("/auth/login")
					.loginProcessingUrl("/login_process")
					.defaultSuccessUrl("/main", true)
					.failureUrl("/auth/login?error")
					.permitAll()
			)
			.logout(logout -> logout
					.logoutUrl("/logout")
					.logoutSuccessUrl("/auth/login")
			);
		return http.build();
	}
	
	@Bean
	public PasswordEncoder getPasswordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
	
}
