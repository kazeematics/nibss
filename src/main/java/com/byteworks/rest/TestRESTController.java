/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.byteworks.rest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.inject.Inject;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author SoloFoundation
 */
@Controller
@RequestMapping("")
public class TestRESTController {
    
    @ResponseBody
    @RequestMapping(value = "/netbeans-tomcat-status-test", method = RequestMethod.HEAD, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> netbeansStatusTest(@RequestBody String transaction) {
        
        Map<String, String> mapResponse = new HashMap();
        
        return mapResponse;
    }
    
    
}
