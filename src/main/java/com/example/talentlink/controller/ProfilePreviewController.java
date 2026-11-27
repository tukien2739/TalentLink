package com.example.talentlink.controller;

import java.security.Principal;

import com.example.talentlink.service.ProfilePreviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfilePreviewController {

    private final ProfilePreviewService previews;

    public ProfilePreviewController(ProfilePreviewService previews) {
        this.previews = previews;
    }

    @GetMapping("/me/profile/preview")
    public String preview(Principal principal, Model model) {
        model.addAttribute("hideAccountName", true);
        model.addAttribute("preview", previews.findOwn(principal.getName()));
        return "student/profile-preview";
    }
}
