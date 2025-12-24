package dev.stephyu.mfa.adapters.in.rest;

import dev.stephyu.mfa.adapters.in.rest.mapper.OtpMapper;
import dev.stephyu.mfa.application.usecase.GenerateOtpUseCase;
import dev.stephyu.mfa.application.usecase.ValidateOtpUseCase;
import dev.stephyu.mfa.domain.Otp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/2fa")
public class OtpController {

    private final GenerateOtpUseCase generator;
    private final ValidateOtpUseCase validator;
    private final OtpMapper mapper;

    public OtpController(GenerateOtpUseCase generator, ValidateOtpUseCase validator, OtpMapper mapper) {
        this.generator = generator;
        this.validator = validator;
        this.mapper = mapper;
    }

    @PostMapping("/request")
    public ResponseEntity<?> request(@RequestBody GenerateOtpRequestDto dto) {
        Otp otp = generator.generate(mapper.toGenerate(dto));
        return ResponseEntity.accepted().body(new GenerateOtpResponseDto(otp.userId(), "OTP generated"));
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validate(@RequestBody ValidateOtpRequestDto dto) {
        boolean ok = validator.validate(mapper.toValidate(dto));
        if (ok) return ResponseEntity.ok(new ValidateOtpResponseDto(true, "Validated"));
        return ResponseEntity.status(400).body(new ValidateOtpResponseDto(false, "Invalid or expired"));
    }

    public record GenerateOtpRequestDto(String userId) {}
    public record ValidateOtpRequestDto(String userId, String code) {}
    public record GenerateOtpResponseDto(String userId, String message) {}
    public record ValidateOtpResponseDto(boolean ok, String message) {}
}

