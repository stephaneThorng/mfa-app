package dev.stephyu.mfa.adapters.in.rest.mapper;

import dev.stephyu.mfa.adapters.in.rest.OtpController;
import dev.stephyu.mfa.adapters.in.rest.OtpController.GenerateOtpRequestDto;
import dev.stephyu.mfa.adapters.in.rest.OtpController.ValidateOtpRequestDto;
import dev.stephyu.mfa.application.dto.GenerateOtpRequest;
import dev.stephyu.mfa.application.dto.ValidateOtpRequest;
import dev.stephyu.mfa.domain.Otp;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OtpMapper {

    GenerateOtpRequest toGenerate( GenerateOtpRequestDto dto );

    ValidateOtpRequest toValidate( ValidateOtpRequestDto dto );

    default OtpController.GenerateOtpResponseDto fromOtp(Otp otp) {
        return new OtpController.GenerateOtpResponseDto(otp.userId(), "OTP generated");
    }

    default OtpController.ValidateOtpResponseDto fromValidation(boolean ok) {
        return new OtpController.ValidateOtpResponseDto(ok, ok ? "Validated" : "Invalid or expired");
    }
}