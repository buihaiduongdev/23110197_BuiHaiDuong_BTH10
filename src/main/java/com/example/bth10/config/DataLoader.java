package com.example.bth10.config;

import com.example.bth10.entity.Category;
import com.example.bth10.entity.Product;
import com.example.bth10.entity.User;
import com.example.bth10.repository.CategoryRepository;
import com.example.bth10.repository.ProductRepository;
import com.example.bth10.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepo;
    private final CategoryRepository categoryRepo;
    private final ProductRepository productRepo;

    public DataLoader(UserRepository userRepo, CategoryRepository categoryRepo, ProductRepository productRepo) {
        this.userRepo = userRepo;
        this.categoryRepo = categoryRepo;
        this.productRepo = productRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepo.count() == 0) {
            User admin = User.builder().username("admin").password("123456")
                    .fullName("Nguyen Van Admin").email("admin@mail.com").phone("0123456789").role(User.Role.admin).build();
            userRepo.save(admin);

            User manager = User.builder().username("manager").password("123456")
                    .fullName("Nguyen Van Manager").email("manager@mail.com").phone("0987654321").role(User.Role.manager).build();
            userRepo.save(manager);

            User user = User.builder().username("user").password("123456")
                    .fullName("Nguyen Van User").email("user@mail.com").phone("0111222333").role(User.Role.user).build();
            userRepo.save(user);

            if (categoryRepo.count() == 0) {
                Category electronics = categoryRepo.save(Category.builder().name("Electronics").description("Electronic items").image(null).status(true).creationDate(LocalDate.now()).quantity(100).createdBy(admin).build());
                Category books = categoryRepo.save(Category.builder().name("Books").description("Books and magazines").image(null).status(true).creationDate(LocalDate.now()).quantity(250).createdBy(admin).build());
                Category clothes = categoryRepo.save(Category.builder().name("Clothes").description("Men and Women Clothes").image(null).status(false).creationDate(LocalDate.now().minusDays(10)).quantity(0).createdBy(manager).build());

                if (productRepo.count() == 0) {
                    productRepo.save(Product.builder().name("Laptop").price(1200.00).description("A powerful laptop").image(null).quantity(10).category(electronics).createdBy(admin).build());
                    productRepo.save(Product.builder().name("Smartphone").price(800.00).description("A smart phone").image(null).quantity(50).category(electronics).createdBy(admin).build());
                    productRepo.save(Product.builder().name("The Lord of the Rings").price(25.00).description("A fantasy book").image(null).quantity(100).category(books).createdBy(admin).build());
                }
            }
        }
    }
}
