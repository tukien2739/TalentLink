package com.example.talentlink.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Optional;

import com.example.talentlink.service.AccountPasswordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(OutputCaptureExtension.class)
class PasswordRecoveryTest {

    @TempDir Path mailbox;

    @Test
    void recoveryRequiresExplicitLocalProfilesAndLoopback() throws Exception {
        Files.setPosixFilePermissions(mailbox, PosixFilePermissions.fromString("rwx------"));
        var runner = new ApplicationContextRunner()
                .withUserConfiguration(PasswordRecoveryController.class)
                .withBean(AccountPasswordService.class, () -> mock(AccountPasswordService.class))
                .withPropertyValues("talentlink.otp-local.directory=" + mailbox);

        runner.withPropertyValues("spring.profiles.active=dev", "server.address=127.0.0.1")
                .run(context -> assertThat(context).doesNotHaveBean(PasswordRecoveryController.class));
        runner.withPropertyValues("spring.profiles.active=dev,otp-local", "server.address=0.0.0.0")
                .run(context -> assertThat(context).doesNotHaveBean(PasswordRecoveryController.class));
        for (String profile : new String[]{"prod", "production", "staging", "shared"}) {
            runner.withPropertyValues("spring.profiles.active=dev,otp-local," + profile, "server.address=127.0.0.1")
                    .run(context -> assertThat(context).doesNotHaveBean(PasswordRecoveryController.class));
        }
        runner.withPropertyValues("spring.profiles.active=dev,otp-local", "server.address=127.0.0.1")
                .run(context -> assertThat(context).hasSingleBean(PasswordRecoveryController.class));
    }

    @Test
    void deliversCodeOnlyToPrivateMailbox(CapturedOutput output) throws Exception {
        Files.setPosixFilePermissions(mailbox, PosixFilePermissions.fromString("rwx------"));
        var passwords = mock(AccountPasswordService.class);
        String email = "otp-fixture@example.invalid";
        String code = "736294";
        when(passwords.issueResetCode(email)).thenReturn(Optional.of(code));
        var mvc = standaloneSetup(new PasswordRecoveryController(passwords, mailbox)).build();

        var result = mvc.perform(post("/forgot-password").param("email", email))
                .andExpect(redirectedUrl("/reset-password")).andReturn();

        try (var files = Files.list(mailbox)) {
            var deliveries = files.toList();
            assertThat(deliveries).hasSize(1);
            var delivery = deliveries.get(0);
            assertThat(Files.readString(delivery)).isEqualTo(code);
            assertThat(PosixFilePermissions.toString(Files.getPosixFilePermissions(delivery))).isEqualTo("rw-------");
        }
        assertThat(result.getFlashMap().toString()).doesNotContain(code);
        assertThat(output.getAll()).doesNotContain(code, email);
    }

    @Test
    void resetRedirectsOnSuccessAndShowsFieldErrorOnRejection() throws Exception {
        Files.setPosixFilePermissions(mailbox, PosixFilePermissions.fromString("rwx------"));
        var passwords = mock(AccountPasswordService.class);
        String email = "otp-fixture@example.invalid";
        String code = "736294";
        String password = "fixture-password";
        when(passwords.reset(email, code, password)).thenReturn(true, false);
        var mvc = standaloneSetup(new PasswordRecoveryController(passwords, mailbox))
                .setViewResolvers(new InternalResourceViewResolver("/views/", ".html")).build();

        mvc.perform(post("/reset-password").param("email", email).param("code", code).param("newPassword", password))
                .andExpect(redirectedUrl("/login"));
        mvc.perform(post("/reset-password").param("email", email).param("code", code).param("newPassword", password))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("passwordReset", "code"));
    }
}
