package gov.samhsa.c2s.fhir.audit.config;

import ca.uhn.fhir.rest.api.EncodingEnum;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties(prefix = "c2s")
@Data
public class FhirProperties {


    @NotNull
    @Valid
    private Fhir fhir;

    @Data
    public static class Fhir {

        private Publish publish;

        private Consent consent;

        private Audit audit;

        @Data
        public static class Consent {
            @NotNull
            private boolean patientReference;

            @NotBlank
            private String codeSystem;
        }

        @Data
        public static class Publish {
            @NotBlank
            private boolean enabled;
            @NotBlank
            private ServerUrl serverUrl;
            @NotBlank
            private String clientSocketTimeoutInMs;
            @NotBlank
            private boolean useCreateForUpdate = false;
            @NotNull
            private EncodingEnum encoding = EncodingEnum.JSON;
        }

        @Data
        public static class Audit {
            @NotBlank
            private boolean enable;
        }

    }

    @Data
    public static class ServerUrl {

        @NotBlank
        private String resource;

        private String patient;
    }
}
