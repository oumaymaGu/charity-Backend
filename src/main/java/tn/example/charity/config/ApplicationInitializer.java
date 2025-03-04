package tn.example.charity.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


import tn.example.charity.Entity.Role;
import tn.example.charity.Entity.URole;
import tn.example.charity.Entity.User;
import tn.example.charity.Repository.RoleRepository;
import tn.example.charity.Repository.UserRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ApplicationInitializer implements ApplicationRunner {

	UserRepository userRepository;
	RoleRepository roleRepository;
	PasswordEncoder passwordEncoder;


	@Override
	public void run(ApplicationArguments args) throws Exception {
		createDefaultRoles();
		createDefaultAdminAccount();



	}

	private void createDefaultRoles() {

		if (roleRepository.count() == 0) {

			URole adminRole = new URole(Role.ROLE_ADMIN);
			URole chefRole = new URole(Role.ROLE_CHEF);
			URole userRole = new URole(Role.ROLE_SIMPLE_USER);

			roleRepository.save(adminRole);
			roleRepository.save(chefRole);
			roleRepository.save(userRole);

		}
	}

	private void createDefaultAdminAccount() {

		if (userRepository.count() == 0) {

			URole adminRole = roleRepository.getByRole(Role.ROLE_ADMIN);
			User admin = new User();
			admin.setUsername("admin");
			admin.setPassword(passwordEncoder.encode("admin"));
			admin.setEmail("admin@gmail.com");
			admin.getRoles().add(adminRole);

			userRepository.save(admin);
		}
	}








}
