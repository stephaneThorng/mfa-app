package dev.stephyu.mfa.config;

import dev.stephyu.mfa.adapters.out.LoggingOtpEventPublisher;
import dev.stephyu.mfa.application.usecase.GenerateOtpUseCase;
import dev.stephyu.mfa.application.usecase.ValidateOtpUseCase;
import dev.stephyu.mfa.ports.out.OtpEventPublisher;
import dev.stephyu.mfa.ports.out.OtpStore;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class AppConfig {

    @Bean
    public OtpEventPublisher otpEventPublisher() {
        return new LoggingOtpEventPublisher();
    }

    @Bean
    public GenerateOtpUseCase generateOtpUseCase(OtpStore store, OtpEventPublisher publisher) {
        return new GenerateOtpUseCase(store, publisher, Clock.systemUTC(), 120, 3);
    }

    @Bean
    public ValidateOtpUseCase validateOtpUseCase(OtpStore store) {
        return new ValidateOtpUseCase(store);
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MFA API")
                        .version("1.0.0")
                        .description("Two-factor authentication API"));
    }
}

