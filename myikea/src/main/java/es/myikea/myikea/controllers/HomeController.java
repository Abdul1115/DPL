package es.myikea.myikea.controllers;

import es.myikea.myikea.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String mostrarPaginaPrincipal(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Verificar si el usuario está autenticado
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String email = authentication.getName(); // Obtener el email del usuario autenticado
            model.addAttribute("authenticated", true);
            model.addAttribute("email", userService.findByUsername(authentication.getName()).getEmail());
            model.addAttribute("isAdmin", authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));
        } else {
            model.addAttribute("authenticated", false);
        }

        return "index"; // Nombre de la vista
    }
}
