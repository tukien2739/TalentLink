package com.example.talentlink.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class NavigationController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping(value = "/login", params = "error")
    public String afterFailedLogin(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage",
                "Không thể đăng nhập. Hãy kiểm tra thông tin tài khoản.");
        return "redirect:/login";
    }

    @GetMapping("/candidate")
    public String candidate(Model model) {
        model.addAttribute("workspaceTitle", "Khu vực ứng viên");
        return "workspace";
    }

    @GetMapping("/recruiter")
    public String recruiter(Model model) {
        model.addAttribute("workspaceTitle", "Khu vực tuyển dụng");
        return "workspace";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("workspaceTitle", "Khu vực quản trị");
        return "workspace";
    }
}
