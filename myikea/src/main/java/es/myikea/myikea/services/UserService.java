package es.myikea.myikea.services;

import es.myikea.myikea.models.Role;
import es.myikea.myikea.models.User;
import es.myikea.myikea.repositories.RoleRepository;
import es.myikea.myikea.repositories.UserRepository;
import org.hibernate.annotations.processing.Find;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(User user) {
        // Encriptar la contraseña
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Buscar el rol USER de forma segura
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("El rol 'USER' no existe en la base de datos"));

        // Asignar el rol al usuario y guardar
        user.setRoles(Collections.singletonList(userRole));
        userRepository.save(user);
    }


    public User findByUsername(String username) {
        // Buscar usuario por nombre de usuario de forma segura
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

    }

    public void initializeRoles() {
        // Crear roles si no existen
        if (roleRepository.findByName("USER").isEmpty()) {
            Role roleUser = new Role();
            roleUser.setName("USER");
            roleRepository.save(roleUser);
        }

        if (roleRepository.findByName("MANAGER").isEmpty()) {
            Role roleManager = new Role();
            roleManager.setName("MANAGER");
            roleRepository.save(roleManager);
        }

        if (roleRepository.findByName("ADMIN").isEmpty()) {
            Role roleAdmin = new Role();
            roleAdmin.setName("ADMIN");
            roleRepository.save(roleAdmin);
        }
    }


    public List<User> obtenerTodosLosUsuarios() {
        return userRepository.findAll();
    }

    public void eliminarUsuarioPorId(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));

//        if (isAdmin) {
//            throw new RuntimeException("No se puede eliminar un usuario con el rol ADMIN.");
//        }

        userRepository.deleteById(id);
    }


}
