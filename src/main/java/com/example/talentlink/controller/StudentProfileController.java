package com.example.talentlink.controller;

import com.example.talentlink.dto.StudentProfileForm;
import com.example.talentlink.dto.StudentSkillForm;
import com.example.talentlink.repository.SkillRepository;
import com.example.talentlink.repository.StudentSkillRepository.RatedSkill;
import com.example.talentlink.repository.StudentSkillRepository;
import com.example.talentlink.service.StudentAvatarService;
import com.example.talentlink.service.StudentEducationService;
import com.example.talentlink.service.StudentProfileService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StudentProfileController {

    private final StudentProfileService profiles;

    private final SkillRepository catalog;
    private final StudentSkillRepository skills;

    private final StudentAvatarService avatars;
    private final StudentEducationService educations;

    public StudentProfileController(StudentProfileService profiles, SkillRepository catalog,
            StudentSkillRepository skills, StudentAvatarService avatars,
            StudentEducationService educations) {
        this.profiles = profiles;
        this.catalog = catalog;
        this.skills = skills;
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
    }

    private String showProfile(Principal principal, Model model, String fullName) {
        populateAvatar(principal, model, fullName);
        model.addAttribute("educations", educations.listOwn(principal.getName()));
        model.addAttribute("skillGroups", skills.findOwn(principal.getName()).stream()
                .collect(Collectors.groupingBy(RatedSkill::category, LinkedHashMap::new, Collectors.toList())));
        model.addAttribute("skillCatalog", catalog.findVisible());
        model.addAttribute("skillLevels", List.of(
                "Mới làm quen, cần hướng dẫn từng bước",
                "Thực hiện được tác vụ cơ bản khi có hướng dẫn",
                "Tự thực hiện được các tác vụ thông thường",
                "Xử lý được tình huống phức tạp và hỗ trợ người khác",
                "Thành thạo, có thể hướng dẫn và cải tiến cách áp dụng"));
        return "student/profile";
    }

    @GetMapping("/me/profile")
    public String edit(Principal principal, Model model) {
        StudentProfileForm profile = profiles.findOwnProfile(principal.getName());
        model.addAttribute("profileForm", profile);
        model.addAttribute("skillForm", new StudentSkillForm(null, null));
        return showProfile(principal, model, profile.fullName());
    }

    @PostMapping("/me/profile")
    public String update(@Valid @ModelAttribute("profileForm") StudentProfileForm form, BindingResult errors,
            Principal principal, Model model, RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            model.addAttribute("skillForm", new StudentSkillForm(null, null));
            return showProfile(principal, model, profiles.findOwnProfile(principal.getName()).fullName());
        }
        profiles.updateOwnProfile(principal.getName(), form);
        redirect.addFlashAttribute("successMessage", "Đã lưu hồ sơ của bạn.");
        return "redirect:/me/profile";
    }

    @PostMapping("/me/profile/skills")
    public String addSkill(@Valid @ModelAttribute("skillForm") StudentSkillForm form, BindingResult errors,
            Principal principal, Model model, RedirectAttributes redirect) {
        if (!errors.hasErrors()) {
            try {
                if (skills.add(principal.getName(), form) == 1) {
                    redirect.addFlashAttribute("successMessage", "Đã thêm kỹ năng.");
                    return "redirect:/me/profile#skills";
                }
                errors.rejectValue("skillId", "unavailable", "Kỹ năng này không còn trong danh mục.");
            } catch (DuplicateKeyException exception) {
                errors.rejectValue("skillId", "duplicate", "Bạn đã chọn kỹ năng này.");
            }
        }
        StudentProfileForm profile = profiles.findOwnProfile(principal.getName());
        model.addAttribute("profileForm", profile);
        return showProfile(principal, model, profile.fullName());
    }

    @PostMapping("/me/profile/skills/{skillId}/remove")
    public String removeSkill(@PathVariable long skillId, Principal principal, RedirectAttributes redirect) {
        if (skills.remove(principal.getName(), skillId) == 1) {
            redirect.addFlashAttribute("successMessage", "Đã bỏ kỹ năng.");
        }
        return "redirect:/me/profile#skills";
    }
}
