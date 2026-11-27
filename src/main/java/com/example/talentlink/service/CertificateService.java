package com.example.talentlink.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

import com.example.talentlink.dto.CertificateForm;
import com.example.talentlink.repository.CertificateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class CertificateService {

    private static final Logger LOG = LoggerFactory.getLogger(CertificateService.class);
    private static final int MAX_BYTES = 5 * 1024 * 1024;
    private final CertificateRepository certificates;
    private final Path directory;

    public CertificateService(CertificateRepository certificates,
            @Value("${talentlink.certificates.directory}") Path directory) {
        this.certificates = certificates;
        this.directory = directory.toAbsolutePath().normalize();
    }

    @Transactional(rollbackFor = IOException.class)
    public void add(String email, CertificateForm form) throws IOException {
        MultipartFile upload = form.evidence();
        if (upload == null || upload.isEmpty() || upload.getSize() > MAX_BYTES) {
            throw new InvalidEvidence("Chọn một file minh chứng có dung lượng tối đa 5 MB.");
        }
        String filename = upload.getOriginalFilename();
        int dot = filename == null ? -1 : filename.lastIndexOf('.');
        String extension = dot < 0 ? "" : filename.substring(dot + 1).toLowerCase(Locale.ROOT);
        MediaType type = mediaType(extension);
        byte[] bytes = upload.getBytes();
        if (type == null || !type.toString().equalsIgnoreCase(upload.getContentType())
                || bytes.length > MAX_BYTES || !hasSignature(extension, bytes)) {
            throw new InvalidEvidence("File minh chứng phải là PDF, JPG hoặc PNG hợp lệ và tối đa 5 MB.");
        }

        String key = UUID.randomUUID() + "." + extension;
        Files.createDirectories(directory);
        Path file = directory.resolve(key);
        try (var output = Files.newOutputStream(file, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status == STATUS_ROLLED_BACK) {
                        deleteAfterCompletion(file);
                    }
                }
            });
            output.write(bytes);
        }
        if (certificates.add(email, form, key) != 1) {
            throw new ResponseStatusException(NOT_FOUND);
        }
    }

    @Transactional(rollbackFor = IOException.class)
    public void remove(String email, long id) throws IOException {
        var certificate = certificates.findOwn(email, id, true)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
        if (certificate.evidenceKey() != null) {
            Path file = directory.resolve(certificate.evidenceKey());
            Path pending = directory.resolve(UUID.randomUUID() + ".delete");
            try {
                Files.move(file, pending, StandardCopyOption.ATOMIC_MOVE);
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        if (status == STATUS_COMMITTED) {
                            deleteAfterCompletion(pending);
                        } else if (status == STATUS_ROLLED_BACK) {
                            try {
                                Files.move(pending, file, StandardCopyOption.ATOMIC_MOVE);
                            } catch (IOException error) {
                                LOG.error("Could not restore certificate evidence {}", file, error);
                            }
                        }
                    }
                });
            } catch (NoSuchFileException ignored) {
                // The evidence has already been removed from disk.
            }
        }
        certificates.remove(email, id);
    }

    public Evidence read(String key) throws IOException {
        if (key == null) {
            throw new ResponseStatusException(NOT_FOUND);
        }
        try {
            return new Evidence(key, mediaType(key.substring(key.lastIndexOf('.') + 1)),
                    Files.readAllBytes(directory.resolve(key)));
        } catch (NoSuchFileException error) {
            throw new ResponseStatusException(NOT_FOUND);
        }
    }

    private static boolean hasSignature(String extension, byte[] bytes) {
        byte[] signature = switch (extension) {
            case "pdf" -> new byte[]{'%', 'P', 'D', 'F', '-'};
            case "jpg", "jpeg" -> new byte[]{(byte) 0xff, (byte) 0xd8, (byte) 0xff};
            case "png" -> new byte[]{(byte) 0x89, 'P', 'N', 'G', 13, 10, 26, 10};
            default -> throw new IllegalArgumentException(extension);
        };
        return bytes.length >= signature.length
                && Arrays.equals(bytes, 0, signature.length, signature, 0, signature.length);
    }

    private static MediaType mediaType(String extension) {
        return switch (extension) {
            case "pdf" -> MediaType.APPLICATION_PDF;
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "png" -> MediaType.IMAGE_PNG;
            default -> null;
        };
    }

    private void deleteAfterCompletion(Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (IOException error) {
            LOG.error("Could not delete certificate evidence {}", file, error);
        }
    }

    public record Evidence(String filename, MediaType mediaType, byte[] bytes) {
    }

    public static class InvalidEvidence extends RuntimeException {
        public InvalidEvidence(String message) {
            super(message);
        }
    }
}
