package com.example.talentlink.service;

import java.io.IOException;
import java.sql.Types;
import java.util.Locale;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;

import com.example.talentlink.exception.ResourceNotFoundException;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StudentAvatarService {

    private static final long MAX_BYTES = 2 * 1024 * 1024;
    private static final String OWN_PROFILE = """
            FROM profiles p JOIN users u ON u.id = p.user_id
            WHERE u.email = :email AND u.role = 'CANDIDATE'
            """;

    private final JdbcClient jdbc;

    public StudentAvatarService(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public boolean hasAvatar(String email) {
        return jdbc.sql("SELECT p.avatar_data IS NOT NULL " + OWN_PROFILE)
                .param("email", email).query(Boolean.class).optional().orElse(false);
    }

    public Optional<Avatar> find(String email) {
        return jdbc.sql("SELECT p.avatar_data, p.avatar_content_type " + OWN_PROFILE
                + " AND p.avatar_data IS NOT NULL")
                .param("email", email)
                .query((row, index) -> new Avatar(row.getBytes(1), row.getString(2)))
                .optional();
    }

    public void replace(String email, MultipartFile file) throws IOException {
        if (file.isEmpty() || file.getSize() > MAX_BYTES) {
            throw new IllegalArgumentException("Chọn ảnh JPG hoặc PNG có dung lượng tối đa 2 MB.");
        }

        String contentType;
        try (var input = new MemoryCacheImageInputStream(file.getInputStream())) {
            var readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) {
                throw invalidImage();
            }
            ImageReader reader = readers.next();
            try {
                contentType = switch (reader.getFormatName().toLowerCase(Locale.ROOT)) {
                    case "jpeg" -> "image/jpeg";
                    case "png" -> "image/png";
                    default -> throw invalidImage();
                };
                reader.setInput(input, true, true);
                var sample = reader.getDefaultReadParam();
                sample.setSourceSubsampling(Math.max(1, reader.getWidth(0) / 64),
                        Math.max(1, reader.getHeight(0) / 64), 0, 0);
                reader.read(0, sample);
            } catch (IOException exception) {
                throw invalidImage();
            } finally {
                reader.dispose();
            }
        }

        // Replacing this row's binary value removes the previous avatar atomically.
        int updated = jdbc.sql("""
                UPDATE profiles p JOIN users u ON u.id = p.user_id
                SET p.avatar_data = :data, p.avatar_content_type = :contentType
                WHERE u.email = :email AND u.role = 'CANDIDATE'
                """)
                .param("data", file.getBytes(), Types.BLOB)
                .param("contentType", contentType)
                .param("email", email)
                .update();
        if (updated == 0) {
            throw new ResourceNotFoundException("Student profile does not exist");
        }
    }

    private IllegalArgumentException invalidImage() {
        return new IllegalArgumentException("Tệp phải là ảnh JPG hoặc PNG hợp lệ.");
    }

    public record Avatar(byte[] data, String contentType) {
    }
}
