package com.example.talentlink.service;

import com.example.talentlink.dto.CertificateForm;
import com.example.talentlink.dto.StudentProfileForm;
import com.example.talentlink.dto.StudentSkillForm;
import com.example.talentlink.repository.CertificateRepository;
import com.example.talentlink.repository.CertificateRepository.Certificate;
import com.example.talentlink.repository.StudentProjectRepository;
import com.example.talentlink.repository.StudentProjectRepository.Project;
import com.example.talentlink.repository.StudentSkillRepository.RatedSkill;
import com.example.talentlink.service.StudentEducationService.Education;
import jakarta.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import org.springframework.stereotype.Service;

@Service
public class ProfileCompletionService {

    private final Validator validator;
    private final StudentProjectRepository projects;
    private final CertificateRepository certificates;

    public ProfileCompletionService(Validator validator, StudentProjectRepository projects,
            CertificateRepository certificates) {
        this.validator = validator;
        this.projects = projects;
        this.certificates = certificates;
    }

    public Completion forOwner(String email, StudentProfileForm profile, List<Education> education,
            List<RatedSkill> skills) {
        return calculate(profile, education, skills, projects.findOwn(email), certificates.listOwn(email));
    }

    public Completion calculate(StudentProfileForm profile, List<Education> education, List<RatedSkill> skills,
            List<Project> projects, List<Certificate> certificates) {
        List<Section> missing = new ArrayList<>();
        if (!valid(profile) || !present(profile.birthDate()) || !present(profile.phone())
                || !present(profile.summary()) || !present(profile.desiredPosition())
                || !present(profile.preferredLocation())) {
            missing.add(Section.PERSONAL);
        }
        if (!anyValid(education, item -> valid(item.form()))) {
            missing.add(Section.EDUCATION);
        }
        if (!anyValid(skills, skill -> present(skill.name()) && skill.category() != null
                && valid(new StudentSkillForm(skill.id(), skill.level())))) {
            missing.add(Section.SKILLS);
        }
        if (!anyValid(projects, project -> valid(project.form()) && project.hasGithubLink()
                && (!present(project.demoUrl()) || project.hasDemoLink()))) {
            missing.add(Section.PROJECTS);
        }
        if (!anyValid(certificates, certificate -> present(certificate.evidenceKey())
                && valid(new CertificateForm(certificate.name(), certificate.issuer(), certificate.issuedOn(), null)))) {
            missing.add(Section.CERTIFICATES);
        }
        int totalWeight = 0;
        for (Section section : Section.values()) {
            totalWeight += section.weight;
        }
        int missingWeight = missing.stream().mapToInt(section -> section.weight).sum();
        return new Completion(toPercentage(totalWeight - missingWeight, totalWeight), List.copyOf(missing));
    }

    static int toPercentage(int earnedWeight, int totalWeight) {
        return (int) Math.round(100.0 * earnedWeight / totalWeight);
    }

    private boolean valid(Object form) {
        return form != null && validator.validate(form).isEmpty();
    }

    private static boolean present(String value) {
        return value != null && !value.isBlank();
    }

    private static <T> boolean anyValid(List<T> items, Predicate<T> rule) {
        return items != null && items.stream().filter(Objects::nonNull).anyMatch(rule);
    }

    public record Completion(int percentage, List<Section> missing) {}

    public enum Section {
        PERSONAL(30, "Thông tin cá nhân", "/me/profile#personal-information"),
        EDUCATION(20, "Học vấn", "/me/profile#education"),
        SKILLS(20, "Kỹ năng", "/me/profile#skills"),
        PROJECTS(15, "Dự án", "/me/profile/projects"),
        CERTIFICATES(15, "Chứng chỉ", "/me/profile/certificates");

        private final int weight;
        private final String label;
        private final String path;

        Section(int weight, String label, String path) {
            this.weight = weight;
            this.label = label;
            this.path = path;
        }

        public int weight() {
            return weight;
        }

        public String label() {
            return label;
        }

        public String path() {
            return path;
        }
    }
}
