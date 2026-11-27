package com.example.talentlink.service;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.Map;
import java.util.Optional;

import com.example.talentlink.dto.StudentProfileForm;
import com.example.talentlink.dto.StudentRegistrationForm;
import com.example.talentlink.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SprintServiceTest {
    private static final String EMAIL = "student@example.com";
    private static final long USER_ID = 17L;

    @Mock PasswordEncoder encoder;
    private final JdbcClient jdbc = mock(JdbcClient.class, RETURNS_DEEP_STUBS);

    @Test
    void registrationStoresEncodedPasswordAndCreatesPrivateProfile() {
        when(encoder.encode("password123")).thenReturn("encoded");
        var insertUser = userInsert();
        when(insertUser.update(any(KeyHolder.class), eq("id"))).thenAnswer(invocation -> {
            invocation.<KeyHolder>getArgument(0).getKeyList().add(Map.of("id", USER_ID));
            return 1;
        });
        var insertProfile = jdbc.sql(argThat(sql -> sql != null && sql.contains("INSERT INTO profiles") && sql.contains("'PRIVATE'"))).param("userId", USER_ID)
                .param("fullName", "Student Name");

        new StudentRegistrationService(jdbc, encoder).register(registrationForm());

        verify(insertUser).update(any(KeyHolder.class), eq("id"));
        verify(insertProfile).update();
    }

    @Test
    void duplicateEmailPreventsProfileCreation() {
        when(encoder.encode("password123")).thenReturn("encoded");
        when(userInsert().update(any(KeyHolder.class), eq("id")))
                .thenThrow(new DuplicateKeyException("Duplicate email"));
        clearInvocations(jdbc);

        assertThrows(StudentRegistrationService.EmailAlreadyUsedException.class,
                () -> new StudentRegistrationService(jdbc, encoder).register(registrationForm()));
        verify(jdbc, never()).sql(contains("INSERT INTO profiles"));
    }

    @Test
    void passwordChangeStoresNewHashAndClearsResetCode() throws Exception {
        stubAccount();
        when(encoder.matches("old-password", "old-hash")).thenReturn(true);
        when(encoder.encode("new-password")).thenReturn("new-hash");
        var update = jdbc.sql(contains("UPDATE users SET password_hash"))
                .param("id", USER_ID).param("hash", "new-hash");
        var delete = jdbc.sql(contains("DELETE FROM password_reset_codes")).param("id", USER_ID);

        assertTrue(new AccountPasswordService(jdbc, encoder).change(EMAIL, "old-password", "new-password"));

        verify(update).update();
        verify(delete).update();
    }

    @Test
    void incorrectCurrentPasswordPreventsUpdate() throws Exception {
        stubAccount();
        when(encoder.matches("wrong-password", "old-hash")).thenReturn(false);

        assertFalse(new AccountPasswordService(jdbc, encoder).change(EMAIL, "wrong-password", "new-password"));
        verify(encoder, never()).encode(anyString());
        verify(jdbc, never()).sql(contains("UPDATE users"));
    }

    @Test
    void missingAccountPreventsPasswordChange() {
        var lookup = jdbc.sql(contains("FOR UPDATE")).param("email", EMAIL)
                .query(org.mockito.ArgumentMatchers.<RowMapper<Object>>any());
        when(lookup.optional()).thenReturn(Optional.empty());

        assertFalse(new AccountPasswordService(jdbc, encoder).change(EMAIL, "old-password", "new-password"));
        verifyNoInteractions(encoder);
    }

    @Test
    void resetConsumesCodeAndRejectsReuse() throws Exception {
        stubAccount();
        var codeLookup = resetCodeLookup();
        when(codeLookup.optional()).thenReturn(Optional.of("otp-hash"), Optional.empty());
        when(encoder.matches("736294", "otp-hash")).thenReturn(true);
        when(encoder.encode("fixture-password")).thenReturn("new-hash");
        var update = jdbc.sql(contains("UPDATE users SET password_hash"))
                .param("id", USER_ID).param("hash", "new-hash");
        var delete = jdbc.sql(contains("DELETE FROM password_reset_codes")).param("id", USER_ID);
        var passwords = new AccountPasswordService(jdbc, encoder);

        assertTrue(passwords.reset(EMAIL, "736294", "fixture-password"));
        assertFalse(passwords.reset(EMAIL, "736294", "fixture-password"));

        verify(update).update();
        verify(delete).update();
    }

    @Test
    void wrongCodeIncrementsAttemptsWithoutChangingPassword() throws Exception {
        stubAccount();
        when(resetCodeLookup().optional()).thenReturn(Optional.of("otp-hash"));
        var attempts = jdbc.sql(contains("failed_attempts = failed_attempts + 1")).param("id", USER_ID);

        assertFalse(new AccountPasswordService(jdbc, encoder).reset(EMAIL, "111111", "fixture-password"));

        verify(attempts).update();
        verify(jdbc, never()).sql(contains("UPDATE users"));
    }

    @Test
    void expiredCodeIsExcludedAndLeavesPasswordUnchanged() throws Exception {
        stubAccount();
        when(resetCodeLookup().optional()).thenReturn(Optional.empty());

        assertFalse(new AccountPasswordService(jdbc, encoder).reset(EMAIL, "736294", "fixture-password"));

        verifyNoInteractions(encoder);
        verify(jdbc, never()).sql(contains("UPDATE users"));
    }

    private JdbcClient.MappedQuerySpec<String> resetCodeLookup() {
        return jdbc.sql(argThat(sql -> sql != null && sql.contains("expires_at > UTC_TIMESTAMP(6)")
                        && sql.contains("failed_attempts < :maxAttempts")))
                .param("id", USER_ID).param("maxAttempts", 5).query(String.class);
    }

    @Test
    void updatingMissingProfileRaisesNotFound() {
        var form = new StudentProfileForm("Student Name", null, null, null, null, null);
        var update = jdbc.sql(contains("UPDATE profiles")).param("email", EMAIL)
                .param("fullName", "Student Name").param("birthDate", null, Types.DATE)
                .param("phone", null, Types.VARCHAR).param("summary", null, Types.VARCHAR)
                .param("desiredPosition", null, Types.VARCHAR).param("preferredLocation", null, Types.VARCHAR);
        when(update.update()).thenReturn(0);

        assertThrows(ResourceNotFoundException.class, () -> new StudentProfileService(jdbc).updateOwnProfile(EMAIL, form));
        verify(update).update();
    }

    private JdbcClient.StatementSpec userInsert() {
        return jdbc.sql(contains("INSERT INTO users")).param("email", EMAIL).param("passwordHash", "encoded");
    }

    private StudentRegistrationForm registrationForm() {
        return new StudentRegistrationForm("Student Name", EMAIL, "password123", "password123");
    }

    private void stubAccount() throws Exception {
        ResultSet row = mock(ResultSet.class);
        when(row.getLong("id")).thenReturn(USER_ID);
        when(row.getString("password_hash")).thenReturn("old-hash");
        JdbcClient.MappedQuerySpec<Object> result = mock();
        when(jdbc.sql(contains("FOR UPDATE")).param("email", EMAIL)
                .query(org.mockito.ArgumentMatchers.<RowMapper<Object>>any())).thenAnswer(invocation -> {
            RowMapper<Object> mapper = invocation.getArgument(0);
            Object account = mapper.mapRow(row, 0);
            when(result.optional()).thenReturn(Optional.of(account));
            return result;
        });
    }
}
