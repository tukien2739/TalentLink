package com.example.talentlink.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.talentlink.config.SecurityConfig;
import com.example.talentlink.dto.EducationForm;
import com.example.talentlink.dto.SkillForm.Category;
import com.example.talentlink.repository.ProfilePreviewRepository.Candidate;
import com.example.talentlink.repository.ProfilePreviewRepository.CertificateSummary;
import com.example.talentlink.repository.StudentProjectRepository.Project;
import com.example.talentlink.repository.StudentSkillRepository.RatedSkill;
import com.example.talentlink.service.ProfilePreviewService;
import com.example.talentlink.service.ProfilePreviewService.Preview;
import com.example.talentlink.service.StudentEducationService.Education;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static java.time.LocalDate.of;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfilePreviewController.class)
@Import(SecurityConfig.class)
class ProfilePreviewMvcTest {
    private static final String PATH = "/me/profile/preview";
    private static final String OWNER = "preview-owner@example.test";

    @Autowired MockMvc mvc;
    @MockitoBean UserDetailsService users;
    @MockitoBean ProfilePreviewService previews;
    @MockitoBean(answers = org.mockito.Answers.RETURNS_DEEP_STUBS) JdbcClient jdbc;

    @Test
    void rendersAllSectionsAndUsesSessionOwnerEvenWithForgedParameters() throws Exception {
        when(previews.findOwn(OWNER)).thenReturn(new Preview(
                new Candidate(1, "Ứng viên mẫu", "Lập trình viên", "Giới thiệu", of(2003, 1, 1),
                        "Backend", "Hà Nội", true),
                List.of(new Education(1, new EducationForm("Đại học mẫu", "CNTT", "2021-01-01", null, "3.5"))),
                Map.of(Category.LANGUAGE, List.of(new RatedSkill(1, "Java", Category.LANGUAGE, 4)),
                        Category.DATABASE, List.of(new RatedSkill(2, "MySQL", Category.DATABASE, 3))),
                List.of(new Project(1, "Dự án mẫu", null, null, null, "javascript:alert(1)", "Mô tả")),
                List.of(new CertificateSummary("Chứng chỉ mẫu", "Tổ chức mẫu", of(2025, 1, 1), null))));
        mvc.perform(get(PATH).session(session("STUDENT")).param("profileId", "2")
                        .param("email", "other@example.test"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ứng viên mẫu")))
                .andExpect(content().string(containsString("/me/profile/avatar")))
                .andExpect(content().string(containsString("Đại học mẫu")))
                .andExpect(content().string(containsString("Java (4/5)")))
                .andExpect(content().string(containsString("MySQL (3/5)")))
                .andExpect(content().string(containsString("Dự án mẫu")))
                .andExpect(content().string(containsString("Chứng chỉ mẫu")))
                .andExpect(content().string(not(containsString(OWNER))))
                .andExpect(content().string(not(containsString("other@example.test"))))
                .andExpect(content().string(not(containsString("javascript:"))));
        verify(previews).findOwn(OWNER);
    }

    @Test
    void rendersEmptySections() throws Exception {
        when(previews.findOwn(OWNER)).thenReturn(new Preview(
                new Candidate(1, "Ứng viên mới", null, null, null, null, null, false),
                List.of(), Map.of(), List.of(), List.of()));
        mvc.perform(get(PATH).session(session("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Chưa cập nhật")))
                .andExpect(content().string(containsString("Chưa có học vấn.")))
                .andExpect(content().string(containsString("Chưa có kỹ năng.")))
                .andExpect(content().string(containsString("Chưa có dự án.")))
                .andExpect(content().string(containsString("Chưa có chứng chỉ.")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"PARTNER", "ADMIN"})
    void deniesOtherRoles(String role) throws Exception {
        mvc.perform(get(PATH).session(session(role))).andExpect(status().isForbidden());
        verifyNoInteractions(previews);
    }

    @Test
    void redirectsAnonymousUser() throws Exception {
        mvc.perform(get(PATH).accept(MediaType.TEXT_HTML)).andExpect(redirectedUrl("http://localhost/login"));
        verifyNoInteractions(previews);
    }

    private MockHttpSession session(String role) {
        when(jdbc.sql("SELECT NOT is_locked FROM users WHERE email = :email")
                .param("email", OWNER).query(Boolean.class).optional()).thenReturn(Optional.of(true));
        var principal = User.withUsername(OWNER).password("unused").roles(role).build();
        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
                principal, null, principal.getAuthorities()));
        var session = new MockHttpSession();
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
        return session;
    }
}
