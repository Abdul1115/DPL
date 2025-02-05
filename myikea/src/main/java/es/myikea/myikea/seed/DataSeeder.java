package es.myikea.myikea.seed;

import es.myikea.myikea.models.Role;
import es.myikea.myikea.models.User;
import es.myikea.myikea.repositories.RoleRepository;
import es.myikea.myikea.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }



    @Override
    public void run(String... args) {
        // Crear roles si no existen
        Role userRole = roleRepository.findByName("USER").orElseGet(() -> {
            Role role = new Role();
            role.setName("USER");
            return roleRepository.save(role);
        });

        Role managerRole = roleRepository.findByName("MANAGER").orElseGet(() -> {
            Role role = new Role();
            role.setName("MANAGER");
            return roleRepository.save(role);
        });

        Role adminRole = roleRepository.findByName("ADMIN").orElseGet(() -> {
            Role role = new Role();
            role.setName("ADMIN");
            return roleRepository.save(role);
        });

        // Crear usuarios si no existen
        if (userRepository.count() == 0) {
            User user = new User();
            user.setUsername("user");
            user.setEmail("user@myikea.com");
            user.setPassword(passwordEncoder.encode("Qwer123!"));
            user.setRoles(List.of(userRole));
            userRepository.save(user);

            User manager = new User();
            manager.setUsername("manager");
            manager.setEmail("manager@myikea.com");
            manager.setPassword(passwordEncoder.encode("Qwer123!"));
            manager.setRoles(List.of(managerRole));
            userRepository.save(manager);

            User admin1 = new User();
            admin1.setUsername("admin1");
            admin1.setEmail("admin1@myikea.com");
            admin1.setPassword(passwordEncoder.encode("Qwer123!"));
            admin1.setRoles(List.of(adminRole));
            userRepository.save(admin1);

            User admin2 = new User();
            admin2.setUsername("admin2");
            admin2.setEmail("admin2@myikea.com");
            admin2.setPassword(passwordEncoder.encode("Qwer123!"));
            admin2.setRoles(List.of(adminRole, managerRole));
            userRepository.save(admin2);
        }
    }
}
