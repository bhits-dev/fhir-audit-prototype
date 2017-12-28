package gov.samhsa.c2s.fhir.audit.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.FhirValidator;
import com.esotericsoftware.minlog.Log;
import gov.samhsa.c2s.common.fhir.audit.AuditEventBuilder;
import gov.samhsa.c2s.fhir.audit.config.FhirProperties;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Patient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.dstu3.model.AuditEvent;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.r4.model.codesystems.AuditEventType;
import org.hl7.fhir.r4.model.codesystems.RestfulInteraction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.net.UnknownHostException;
import java.util.Map;

import static org.hl7.fhir.r4.model.codesystems.RestfulInteraction.CREATE;

@Service
@EnableBinding(Source.class)
@Slf4j
public class TestServiceImpl implements TestService {

    private final IGenericClient patientClient;
    private final FhirContext fhirContext;
    private final FhirValidator fhirValidator;
    private final Map<Class<? extends Resource>, IGenericClient> fhirClients;
    private final FhirProperties fhirProperties;
    private final IParser fhirJsonParser;

    @Autowired
    Source mySource;

    @Autowired
    public TestServiceImpl(FhirContext fhirContext, FhirValidator fhirValidator, Map<Class<? extends Resource>, IGenericClient> fhirClients, FhirProperties fhirProperties, IParser fhirJsonParser) {
        this.fhirContext = fhirContext;
        this.fhirValidator = fhirValidator;
        this.fhirClients = fhirClients;
        this.fhirProperties = fhirProperties;
        this.fhirJsonParser = fhirJsonParser;
        this.patientClient = fhirClients.getOrDefault(Patient.class, fhirClients.get(Resource.class));
    }

    @Override
    public void createFhirPatient() {
        Patient testPatient = buildFhirPatient();

        MethodOutcome outcome = patientClient.create()
                .resource(buildFhirPatient())
                .prettyPrint()
                .encodedXml()
                .execute();

        IdType id = (IdType) outcome.getId();
        Log.info("Resource is available at: " + id.getValue());

        Patient receivedPatient = (Patient) outcome.getResource();
        Log.info("This is what we sent up: \n" + fhirJsonParser.encodeResourceToString(testPatient));

        if (fhirProperties.getFhir().getAudit().isEnable()) {
            try {
                AuditEvent audit = AuditEventBuilder.buildAuditEvent(testPatient, outcome, AuditEventType.REST, CREATE, AuditEvent.AuditEventAction.C, "SimpleFhir.java");

                sendToAudit(audit);
            } catch (UnknownHostException e) {
                Log.warn("AuditEvent could not be created and hence not logged.");
            }
        }
    }

    private void sendToAudit(AuditEvent auditEvent) {
        mySource.output().send(MessageBuilder.withPayload(auditEvent).build());
    }

    private Patient buildFhirPatient() {
        Patient fhirPatient = new Patient();

        fhirPatient.addName().setUse(HumanName.NameUse.OFFICIAL).addPrefix("Mr").addGiven("FEI-TEST-" + System.currentTimeMillis());
        fhirPatient.addIdentifier().setSystem("http://www.feisystems.com").setValue("100");

        return fhirPatient;
    }

}
