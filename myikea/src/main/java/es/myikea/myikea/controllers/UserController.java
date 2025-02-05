package es.myikea.myikea.controllers;

import es.myikea.myikea.models.User;
import es.myikea.myikea.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String listarUsuarios(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            // Obtener el usuario autenticado
            User usuarioActual = userService.findByUsername(authentication.getName());

            // Obtener todos los usuarios y filtrar el usuario autenticado
            List<User> usuarios = userService.obtenerTodosLosUsuarios().stream()
                    .filter(usuario -> !usuario.getUsername().equals(usuarioActual.getUsername()))
                    .toList();

            // Agregar atributos al modelo
            model.addAttribute("usuarios", usuarios);
            model.addAttribute("email", usuarioActual.getEmail());

            // Verificar si el usuario tiene rol ADMIN
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
            model.addAttribute("isAdmin", isAdmin);
        } else {
            model.addAttribute("email", "No autenticado");
            model.addAttribute("isAdmin", false);
        }

        return "users"; // Vista Thymeleaf
    }

    @PostMapping("/users/delete")
    public String eliminarUsuario(@RequestParam Long id, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            // Verificar si el usuario tiene rol ADMIN antes de eliminar
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));

            if (isAdmin) {
                // Eliminar usuario por ID
                userService.eliminarUsuarioPorId(id);
            } else {
                // Si el usuario no es ADMIN, redirigir sin hacer nada
                return "redirect:/users?error=No tienes permisos para eliminar usuarios";
            }
        }
        return "redirect:/users";
    }
}
