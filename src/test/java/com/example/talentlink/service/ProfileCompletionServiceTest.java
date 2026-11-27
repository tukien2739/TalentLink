package com.example.talentlink.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.example.talentlink.dto.EducationForm;
import com.example.talentlink.dto.SkillForm.Category;
import com.example.talentlink.dto.StudentProfileForm;
import com.example.talentlink.repository.CertificateRepository;
import com.example.talentlink.repository.CertificateRepository.Certificate;
import com.example.talentlink.repository.StudentProjectRepository;
import com.example.talentlink.repository.StudentProjectRepository.Project;
import com.example.talentlink.repository.StudentSkillRepository.RatedSkill;
import com.example.talentlink.service.ProfileCompletionService.Section;
import com.example.talentlink.service.StudentEducationService.Education;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ProfileCompletionServiceTest {

    private static final ValidatorFactory VALIDATION = Validation.buildDefaultValidatorFactory();
    private final ProfileCompletionService service = new ProfileCompletionService(
            VALIDATION.getValidator(), mock(StudentProjectRepository.class), mock(CertificateRepository.class));

    private final StudentProfileForm profile = new StudentProfileForm(
            "Nguyễn An", "2000-01-01", "0912345678", "Giới thiệu", "Lập trình viên", "Hà Nội");
    private final Education education = new Education(1, new EducationForm("Đại học", "CNTT",
            "2020-09-01", null, "3.5"));
    private final RatedSkill skill = new RatedSkill(1, "Java", Category.values()[0], 3);
    private final Project project = new Project(1, "Dự án", "Lập trình", "Java",
            "https://github.com/example/project", null, "Mô tả");
    private final Certificate certificate = new Certificate(1, "Chứng chỉ", "Đơn vị cấp",
            LocalDate.of(2024, 1, 1), "fixture.pdf", "student@example.test");

    @AfterAll
    static void closeValidatorFactory() {
        VALIDATION.close();
    }

    @Test
    void nullEmptyAndInvalidDataEarnNothing() {
        assertThat(service.calculate(null, null, null, null, null).missing())
                .containsExactly(Section.values());
        var result = service.calculate(new StudentProfileForm("An", null, null, null, null, " "),
                Arrays.asList(null, new Education(1, new EducationForm("Đại học", "CNTT",
                        "2020-09-01", "2019-09-01", null))),
                List.of(new RatedSkill(1, "Java", Category.values()[0], 6)),
                List.of(new Project(1, "Dự án", "Lập trình", "Java", "https://example.org", null, "Mô tả")),
                List.of(new Certificate(1, "Chứng chỉ", " ", null, null, "student@example.test")));
        assertThat(result.percentage()).isZero();
        assertThat(result.missing()).containsExactly(Section.values());
        assertThat(service.calculate(null, List.of(), List.of(), List.of(), List.of()).percentage()).isZero();
    }

    @ParameterizedTest
    @CsvSource({"PERSONAL,30", "EDUCATION,20", "SKILLS,20", "PROJECTS,15", "CERTIFICATES,15"})
    void eachSectionEarnsItsWeightAndDisappearsFromMissing(Section completed, int weight) {
        var result = service.calculate(completed == Section.PERSONAL ? profile : null,
                completed == Section.EDUCATION ? List.of(education) : List.of(),
                completed == Section.SKILLS ? List.of(skill) : List.of(),
                completed == Section.PROJECTS ? List.of(project) : List.of(),
                completed == Section.CERTIFICATES ? List.of(certificate) : List.of());
        assertThat(result.percentage()).isEqualTo(weight);
        assertThat(result.missing()).containsExactlyElementsOf(
                Arrays.stream(Section.values()).filter(section -> section != completed).toList());
    }

    @Test
    void fullProfileEarnsOneHundredEvenWithMultipleRecordsAndDeletionLowersScore() {
        assertThat(Arrays.stream(Section.values()).mapToInt(Section::weight).sum()).isEqualTo(100);
        var full = service.calculate(profile, List.of(education, education), List.of(skill, skill),
                List.of(project, project), List.of(certificate, certificate));
        assertThat(full.percentage()).isEqualTo(100);
        assertThat(full.missing()).isEmpty();

        var afterDeletion = service.calculate(profile, List.of(education), List.of(skill),
                List.of(), List.of(certificate));
        assertThat(afterDeletion.percentage()).isEqualTo(85);
        assertThat(afterDeletion.missing()).containsExactly(Section.PROJECTS);
    }

    @ParameterizedTest
    @CsvSource({"1,3,33", "1,6,17", "1,8,13", "0,100,0", "100,100,100"})
    void roundsToNearestIntegerWithHalfRoundedUp(int earned, int total, int percentage) {
        assertThat(ProfileCompletionService.toPercentage(earned, total)).isEqualTo(percentage);
    }
}
