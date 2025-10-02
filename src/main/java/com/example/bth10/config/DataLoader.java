package com.example.bth10.config;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.bth10.entity.Category;
import com.example.bth10.entity.User;
import com.example.bth10.entity.User.Role;
import com.example.bth10.repository.CategoryRepository;
import com.example.bth10.repository.UserRepository;

@Component
public class DataLoader implements CommandLineRunner {

	private final UserRepository userRepo;
	private final CategoryRepository categoryRepo;

	public DataLoader(UserRepository userRepo, CategoryRepository categoryRepo) {
		this.userRepo = userRepo;
		this.categoryRepo = categoryRepo;
	}

	@Override
	public void run(String... args) throws Exception {
		if (userRepo.count() == 0) {
			User admin = User.builder().username("admin").password("123456").fullName("Nguyen Van Admin")
					.email("admin@mail.com").phone("0123456789").role(Role.admin).build();

			User manager = User.builder().username("manager").password("123456").fullName("Nguyen Van Manager")
					.email("manager@mail.com").phone("0987654321").role(Role.manager).build();

			User user = User.builder().username("user").password("123456").fullName("Nguyen Van User")
					.email("user@mail.com").phone("0111222333").role(Role.user).build();

			userRepo.save(admin);
			userRepo.save(manager);
			userRepo.save(user);

			if (categoryRepo.count() == 0) {
				categoryRepo.save(Category.builder().name("Electronics").description("Electronic items").image(null)
						.status(true).creationDate(LocalDate.now()).quantity(100).createdBy(admin).build());

				categoryRepo.save(Category.builder().name("Books").description("Books and magazines").image(null)
						.status(true).creationDate(LocalDate.now()).quantity(250).createdBy(admin).build());

				categoryRepo.save(Category.builder().name("Clothes").description("Men and Women Clothes").image(null)
						.status(false).creationDate(LocalDate.now().minusDays(10)).quantity(0).createdBy(manager)
						.build());
			}
		}
	}
}
