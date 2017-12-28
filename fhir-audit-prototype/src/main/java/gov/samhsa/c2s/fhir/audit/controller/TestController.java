package gov.samhsa.c2s.fhir.audit.controller;

import gov.samhsa.c2s.fhir.audit.service.TestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestController {

    @Autowired
    TestServiceImpl testService;

    @RequestMapping(path="/testfhiraudit", method = RequestMethod.GET)
    public String publishMessage() throws Exception {

        //mySource.output().send(MessageBuilder.withPayload(payload).build());
        //mySource.output().send(MessageBuilder.withPayload(payload).build());
        try {
            testService.createFhirPatient();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Sent to queue");

        return "success";
    }
}
