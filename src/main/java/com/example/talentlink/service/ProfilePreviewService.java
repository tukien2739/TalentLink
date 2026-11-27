package com.example.talentlink.service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.talentlink.dto.SkillForm.Category;
import com.example.talentlink.exception.ResourceNotFoundException;
import com.example.talentlink.repository.ProfilePreviewRepository;
import com.example.talentlink.repository.ProfilePreviewRepository.Candidate;
import com.example.talentlink.repository.ProfilePreviewRepository.CertificateSummary;
import com.example.talentlink.repository.StudentProjectRepository;
import com.example.talentlink.repository.StudentProjectRepository.Project;
import com.example.talentlink.repository.StudentSkillRepository;
import com.example.talentlink.repository.StudentSkillRepository.RatedSkill;
import com.example.talentlink.service.StudentEducationService.Education;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfilePreviewService {

    private final ProfilePreviewRepository previews;
    private final StudentEducationService educations;
    private final StudentSkillRepository skills;
    private final StudentProjectRepository projects;

    public ProfilePreviewService(ProfilePreviewRepository previews, StudentEducationService educations,
            StudentSkillRepository skills, StudentProjectRepository projects) {
        this.previews = previews;
        this.educations = educations;
        this.skills = skills;
        this.projects = projects;
    }

    @Transactional(readOnly = true)
    public Preview findOwn(String principalName) {
        Candidate candidate = previews.findOwnCandidate(principalName)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ sinh viên."));
        Map<Category, List<RatedSkill>> skillGroups = skills.findOwn(principalName).stream()
                .collect(Collectors.groupingBy(RatedSkill::category, () -> new EnumMap<>(Category.class),
                        Collectors.toList()));
        return new Preview(candidate, educations.listOwn(principalName), skillGroups,
                projects.findOwn(principalName), previews.findCertificates(candidate.id()));
    }

    public record Preview(Candidate candidate, List<Education> educations, Map<Category, List<RatedSkill>> skillGroups,
            List<Project> projects, List<CertificateSummary> certificates) {
    }
}
