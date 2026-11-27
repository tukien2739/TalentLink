package com.example.talentlink.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.LockedException;
import org.springframework.security.web.WebAttributes;
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
    public String afterFailedLogin(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        var session = request.getSession(false);
        Object failure = null;
        if (session != null) {
            failure = session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
        String message = failure instanceof LockedException
                ? "Tài khoản đã bị khóa. Hãy liên hệ quản trị viên để được hỗ trợ."
                : "Không thể đăng nhập. Hãy kiểm tra email và mật khẩu.";
        redirectAttributes.addFlashAttribute("errorMessage", message);
        return "redirect:/login";
    }

    @GetMapping("/me")
    public String student(Model model) {
        model.addAttribute("workspaceTitle", "Khu vực sinh viên");
        return "workspace";
    }

    @GetMapping("/partner")
    public String partner(Model model) {
        model.addAttribute("workspaceTitle", "Khu vực đối tác tuyển dụng");
        return "workspace";
    }

    @GetMapping("/admin")
    public String admin() {
        return "redirect:/admin/users";
    }

    @GetMapping("/candidate")
    public String previousStudentHome() {
        return "redirect:/me";
    }

    @GetMapping("/recruiter")
    public String previousPartnerHome() {
        return "redirect:/partner";
    }
}
