package com.whitecape.flayes.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.whitecape.flayes.dto.SubDomainData;
import com.whitecape.flayes.entities.Domain;
import com.whitecape.flayes.entities.ERole;
import com.whitecape.flayes.entities.Role;
import com.whitecape.flayes.entities.Skill;
import com.whitecape.flayes.entities.SubDomain;
import com.whitecape.flayes.entities.User;
import com.whitecape.flayes.repositories.DomainRepository;
import com.whitecape.flayes.repositories.RoleRepository;
import com.whitecape.flayes.repositories.SkillRepository;
import com.whitecape.flayes.repositories.SubDomainRepository;
import com.whitecape.flayes.repositories.UserRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ApplicationInitializer implements ApplicationRunner {

	UserRepository userRepository;
	SkillRepository skillRepository;
	RoleRepository roleRepository;
	PasswordEncoder passwordEncoder;
	SubDomainRepository subDomainRepository;
	DomainRepository domainRepository;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		createDefaultRoles();
		createDefaultAdminAccount();
		createDefaultSkills();

		createDefaultSubDomains();

	}

	private void createDefaultRoles() {

		if (roleRepository.count() == 0) {

			Role adminRole = new Role(ERole.ROLE_ADMIN);
			Role freelancerRole = new Role(ERole.ROLE_FREELANCER);
			Role clientRole = new Role(ERole.ROLE_CLIENT);

			roleRepository.save(adminRole);
			roleRepository.save(freelancerRole);
			roleRepository.save(clientRole);

		}
	}

	private void createDefaultAdminAccount() {

		if (userRepository.count() == 0) {

			Role adminRole = roleRepository.getByName(ERole.ROLE_ADMIN);
			User admin = new User();
			admin.setUsername("admin");
			admin.setPassword(passwordEncoder.encode("adminA1@"));
			admin.setEmail("mohamedachref.hamzaoui@esprit.tn");
			admin.getRoles().add(adminRole);

			userRepository.save(admin);
		}
	}

	private void createDefaultSkills() {
		List<String> skillKeys = Arrays.asList("communication", "francais",
				"anglais" /* autres compétences par défaut */);

		for (String skillKey : skillKeys) {
			if (!skillRepository.existsBySkillKey(skillKey)) {
				Skill skill = new Skill();
				skill.setSkillKey(skillKey);

				skillRepository.save(skill);
			}
		}
	}

	private void createDefaultSubDomains() {
		Map<String, Domain> map = new HashMap<>();
		List<SubDomainData> subDomainsData = Arrays.asList(new SubDomainData("java", "Programming"),
				new SubDomainData("springboot", "Programming"),new SubDomainData("Laravel", "Programming"), new SubDomainData("Journalisme", "Lettres"),
				new SubDomainData("traduction", "Lettres"), new SubDomainData("angular", "Programming"),
				new SubDomainData("marketing", "Business")

		);

		for (SubDomainData subDomainData : subDomainsData) {

			Domain existingDomain = map.get(subDomainData.getDomain());

			if (existingDomain == null && !domainRepository.existsByCategorie(subDomainData.getDomain())) {
				Domain domain = new Domain();
				domain.setCategorie(subDomainData.getDomain());
				domainRepository.save(domain);
				map.put(subDomainData.getDomain(), domain);
				existingDomain = domain;
			}

			if (!subDomainRepository.existsByName(subDomainData.getName())) {
				SubDomain subDomain = new SubDomain();
				subDomain.setName(subDomainData.getName());
				subDomain.setDomain(existingDomain);
				subDomainRepository.save(subDomain);
			}
		}
	}

}
