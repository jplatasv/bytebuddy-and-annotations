package org.example;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Validated
@RequestMapping(
        path = "/somePrefix",
        produces = {"application/text", "application/xml"},
        consumes = {"application/text", "application/xml"},
        params = {"exampleParam1", "exampleParam2"},
        headers = {"key=val", "nokey=val"}
)
public interface ExampleInterface {

    @RequestMapping(value = {"/someEndpoint"}, method = {RequestMethod.POST}, produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<String> someEndpointMethod(String value);

}
