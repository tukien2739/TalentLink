package com.example.talentlink.controller;

import java.io.IOException;
import java.security.Principal;

import com.example.talentlink.dto.CertificateForm;
import com.example.talentlink.repository.CertificateRepository;
import com.example.talentlink.service.CertificateService;
import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
public class CertificateController {

    private static final String STUDENT = "/me/profile/certificates";
    private static final String ADMIN = "/admin/certificates";
    private static final String LIST = "redirect:" + STUDENT;

    private final CertificateRepository certificates;
    private final CertificateService evidence;

    public CertificateController(CertificateRepository certificates, CertificateService evidence) {
        this.certificates = certificates;
        this.evidence = evidence;
    }

    @GetMapping(STUDENT)
    public String list(Principal principal, Model model) {
        model.addAttribute("certificateForm", new CertificateForm(null, null, null, null));
        return studentPage(principal, model);
    }

    @GetMapping(ADMIN)
    public String adminList(Model model) {
        model.addAttribute("admin", true);
        model.addAttribute("certificates", certificates.listEvidence());
        return "student/certificates";
    }

    @PostMapping(STUDENT)
    public String add(@Valid @ModelAttribute("certificateForm") CertificateForm form, BindingResult errors,
            Principal principal, Model model, RedirectAttributes redirect) throws IOException {
        if (!errors.hasErrors()) {
            try {
                evidence.add(principal.getName(), form);
                redirect.addFlashAttribute("successMessage", "Đã thêm chứng chỉ.");
                return LIST;
            } catch (CertificateService.InvalidEvidence error) {
                errors.rejectValue("evidence", "invalidEvidence", error.getMessage());
            }
        }
        return studentPage(principal, model);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String oversized(RedirectAttributes redirect) {
        redirect.addFlashAttribute("evidenceError", "File minh chứng vượt quá dung lượng tối đa 5 MB.");
        return LIST;
    }

    @PostMapping(STUDENT + "/{id}/remove")
    public String remove(@PathVariable long id, Principal principal, RedirectAttributes redirect) throws IOException {
        evidence.remove(principal.getName(), id);
        redirect.addFlashAttribute("successMessage", "Đã xoá chứng chỉ.");
        return LIST;
    }

    @GetMapping(STUDENT + "/{id}/evidence")
    public ResponseEntity<byte[]> ownEvidence(@PathVariable long id, Principal principal,
            @RequestParam(defaultValue = "false") boolean inline) throws IOException {
        return download(certificates.findOwn(principal.getName(), id, false)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND)), inline);
    }

    @GetMapping(ADMIN + "/{id}/evidence")
    public ResponseEntity<byte[]> adminEvidence(@PathVariable long id,
            @RequestParam(defaultValue = "false") boolean inline) throws IOException {
        return download(certificates.find(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND)), inline);
    }

    private String studentPage(Principal principal, Model model) {
        model.addAttribute("certificates", certificates.listOwn(principal.getName()));
        return "student/certificates";
    }

    private ResponseEntity<byte[]> download(CertificateRepository.Certificate certificate, boolean inline)
            throws IOException {
        var file = evidence.read(certificate.evidenceKey());
        var disposition = inline ? ContentDisposition.inline() : ContentDisposition.attachment();
        return ResponseEntity.ok().contentType(file.mediaType()).cacheControl(CacheControl.noStore())
                .header("Content-Disposition", disposition.filename(file.filename()).build().toString())
                .header("Content-Security-Policy", "sandbox")
                .header("X-Content-Type-Options", "nosniff")
                .body(file.bytes());
    }
}
