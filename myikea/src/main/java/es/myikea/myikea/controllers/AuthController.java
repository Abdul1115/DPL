package es.myikea.myikea.controllers;

import es.myikea.myikea.models.User;
import es.myikea.myikea.models.Role;
import es.myikea.myikea.repositories.RoleRepository;
import es.myikea.myikea.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Mostrar formulario de registro
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("user", new User());
        return "registro";
    }

    // Procesar registro
    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute("user") User user, Model model) {
        // Verificar si el correo electrónico ya está registrado
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error", "El correo electrónico ya está registrado.");
            return "registro";
        }

        // Codificar la contraseña
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Buscar el rol "USER" de forma segura
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("El rol 'USER' no existe en la base de datos"));

        // Asignar el rol al usuario
        user.setRoles(Collections.singletonList(userRole));

        // Guardar el usuario en la base de datos
        userRepository.save(user);

        return "redirect:/login";
    }

    // Mostrar formulario de login
    @GetMapping("/login")
    public String mostrarFormularioLogin() {
        return "login";
    }
}
