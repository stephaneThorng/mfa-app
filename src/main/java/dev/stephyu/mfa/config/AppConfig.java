package dev.stephyu.mfa.config;

import dev.stephyu.mfa.adapters.out.LoggingOtpEventPublisherAdapter;
import dev.stephyu.mfa.application.usecase.GenerateOtpUseCase;
import dev.stephyu.mfa.application.usecase.ValidateOtpUseCase;
import dev.stephyu.mfa.ports.out.OtpEventPublisherPort;
import dev.stephyu.mfa.ports.out.OtpStorePort;
import dev.stephyu.mfa.ports.out.UserRepositoryPort;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class AppConfig {

    @Bean
    public OtpEventPublisherPort otpEventPublisher() {
        return new LoggingOtpEventPublisherAdapter();
    }

    @Bean
    public GenerateOtpUseCase generateOtpUseCase(UserRepositoryPort userRepositoryPort, OtpStorePort store, OtpEventPublisherPort publisher) {
        return new GenerateOtpUseCase(userRepositoryPort, store, publisher, Clock.systemUTC(), 120, 3);
    }

    @Bean
    public ValidateOtpUseCase validateOtpUseCase(OtpStorePort store) {
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

