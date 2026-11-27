package com.example.talentlink.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.Locale;

import com.example.talentlink.dto.StudentProfileForm;
import com.example.talentlink.service.StudentEducationService;
import com.example.talentlink.service.StudentProfileService;
import com.example.talentlink.service.StudentAvatarService;

import jakarta.validation.Valid;

import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StudentProfileController {

    private final StudentProfileService profiles;
    private final StudentEducationService educations;

    private final StudentAvatarService avatars;

    public StudentProfileController(StudentProfileService profiles, StudentAvatarService avatars, StudentEducationService educations) {
        this.profiles = profiles;
        this.avatars = avatars;
        this.educations = educations;
    }

    @GetMapping("/me/profile/avatar")
    public ResponseEntity<byte[]> avatar(Principal principal) {
        return avatars.find(principal.getName())
                .map(avatar -> ResponseEntity.ok().cacheControl(CacheControl.noStore())
                        .contentType(MediaType.parseMediaType(avatar.contentType())).body(avatar.data()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/me/profile/avatar")
    public String replaceAvatar(@RequestParam("avatar") MultipartFile avatar, Principal principal,
            RedirectAttributes redirect) {
        try {
            avatars.replace(principal.getName(), avatar);
            redirect.addFlashAttribute("successMessage", "Đã lưu ảnh đại diện.");
        } catch (IllegalArgumentException exception) {
            redirect.addFlashAttribute("avatarError", exception.getMessage());
        } catch (IOException exception) {
            redirect.addFlashAttribute("avatarError", "Không thể đọc tệp ảnh. Vui lòng chọn lại.");
        }
        return "redirect:/me/profile#avatar";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String oversizedAvatar(RedirectAttributes redirect) {
        redirect.addFlashAttribute("avatarError", "Ảnh đại diện phải có dung lượng tối đa 2 MB.");
        return "redirect:/me/profile#avatar";
    }

    private void populateAvatar(Principal principal, Model model, String fullName) {
        String name = fullName.strip();
        model.addAttribute("hasAvatar", avatars.hasAvatar(principal.getName()));
        model.addAttribute("avatarInitial", name.isEmpty() ? "?"
                : new String(Character.toChars(name.codePointAt(0))).toUpperCase(Locale.ROOT));
