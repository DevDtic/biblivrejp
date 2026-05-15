package biblivre.core.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaViewController {

    @GetMapping("/spa/**")
    public String spaPage(Model model, HttpServletRequest request) {
        model.addAttribute("contextPath", request.getContextPath());

        return "spa";
    }
}
