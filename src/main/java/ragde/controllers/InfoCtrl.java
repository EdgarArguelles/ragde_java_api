package ragde.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/info")
public class InfoCtrl {

    @Value("${api-version}")
    private String API_VERSION;

    @Autowired
    private Environment env;

    @GetMapping(value = "/version")
    public ResponseEntity version() {
        return new ResponseEntity<>(Map.of("version", API_VERSION), HttpStatus.OK);
    }

    @GetMapping(value = "/environment")
    public ResponseEntity environment() {
        // get spring active profile
        return new ResponseEntity<>(Map.of("environment", env.getActiveProfiles()), HttpStatus.OK);
    }
}