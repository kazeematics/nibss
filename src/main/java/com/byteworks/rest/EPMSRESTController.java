/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.byteworks.rest;

import com.byteworks.model.epms.Epms;
import com.byteworks.model.epms.EpmsJDBCTemplate;
import com.byteworks.model.host.HostJDBCTemplate;
import com.byteworks.model.terminal.Terminal;
import com.byteworks.model.terminal.TerminalJDBCTemplate;
import com.byteworks.model.transaction.TransactionJDBCTemplate;
import com.byteworks.nibss.NIBSS;

import static com.byteworks.utils.Constants.LIVE_HOST;
import static com.byteworks.utils.Constants.LIVE_KEY1;
import static com.byteworks.utils.Constants.LIVE_KEY2;
import static com.byteworks.utils.Constants.LIVE_PORT;
import static com.byteworks.utils.Constants.LIVE_PROTOCOL;
import static com.byteworks.utils.Constants.POSVAS_LIVE_HOST;
import static com.byteworks.utils.Constants.POSVAS_LIVE_KEY1;
import static com.byteworks.utils.Constants.POSVAS_LIVE_KEY2;
import static com.byteworks.utils.Constants.POSVAS_LIVE_PORT;
import static com.byteworks.utils.Constants.POSVAS_LIVE_PROTOCOL;
import static com.byteworks.utils.Constants.POSVAS_TEST_HOST;
import static com.byteworks.utils.Constants.POSVAS_TEST_KEY1;
import static com.byteworks.utils.Constants.POSVAS_TEST_KEY2;
import static com.byteworks.utils.Constants.POSVAS_TEST_PORT;
import static com.byteworks.utils.Constants.POSVAS_TEST_PROTOCOL;

import static com.byteworks.utils.Constants.TEST_HOST;
import static com.byteworks.utils.Constants.TEST_KEY1;
import static com.byteworks.utils.Constants.TEST_KEY2;
import static com.byteworks.utils.Constants.TEST_PORT;
import static com.byteworks.utils.Constants.TEST_PROTOCOL;

import static com.byteworks.utils.Constants.TRAN_TYPE_BALANCE;
import com.byteworks.utils.ResponseCodes;
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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.inject.Inject;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author SoloFoundation
 */
@Controller
@RequestMapping("api")
public class EPMSRESTController {
    
    private static final Logger LOG = Logger.getLogger(EPMSRESTController.class);
    public static ApplicationContext context = new ClassPathXmlApplicationContext("applicationBeanContext.xml");
    public static TransactionJDBCTemplate transactionJDBCTemplate = (TransactionJDBCTemplate) context.getBean("transactionJDBCTemplate");
    public static TerminalJDBCTemplate terminalJDBCTemplate = (TerminalJDBCTemplate) context.getBean("terminalJDBCTemplate");
    public static EpmsJDBCTemplate epmsJDBCTemplate = (EpmsJDBCTemplate) context.getBean("epmsJDBCTemplate");
    //public static HostJDBCTemplate hostJDBCTemplate = (HostJDBCTemplate) context.getBean("hostJDBCTemplate");
    
    @ResponseBody
    @RequestMapping(value = "/addTerminal", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String addTerminal(@RequestBody String transaction) {
        
        String response = "";
        
        LOG.info("Request => " + transaction);
        
        try {
            
            JSONObject jsonObject = (JSONObject) JSONValue.parse(transaction);    
            
            String terminalid = (String) jsonObject.get("terminalid"); 
            String name = (String) jsonObject.get("name");
            
            String result = terminalJDBCTemplate.insertTerminal(name, terminalid);
            if (result == null) {
                JSONObject obj = new JSONObject();
                obj.put("response", "00");
                obj.put("description", "Terminal " + terminalid + " Added Successfully");
                response = obj.toJSONString();
            } else {
                JSONObject obj = new JSONObject();
                obj.put("response", "05");
                obj.put("description", result);
                response = obj.toJSONString();
            }
            
        } catch (Exception ex) {
            LOG.error("Exception = " + ex.getMessage());
            JSONObject obj = new JSONObject();
            obj.put("response", "96");
            obj.put("description", ex.getMessage());
            response = obj.toJSONString();
        }
        
        LOG.info("Response => " + response);

        return response;
    }
    
    @ResponseBody
    @RequestMapping(value = "/deleteTerminal", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String deleteTerminal(@RequestBody String transaction) {
        
        String response = "";
        
        LOG.info("Request => " + transaction);
        
        try {
            
            JSONObject jsonObject = (JSONObject) JSONValue.parse(transaction);    
            
            String terminalid = (String) jsonObject.get("terminalid"); 
            
            String result = terminalJDBCTemplate.deleteTerminal(terminalid);
            if (result == null) {
                JSONObject obj = new JSONObject();
                obj.put("response", "00");
                obj.put("description", "Terminal " + terminalid + " Deleted Successfully");
                response = obj.toJSONString();
            } else {
                JSONObject obj = new JSONObject();
                obj.put("response", "05");
                obj.put("description", result);
                response = obj.toJSONString();
            }
            
            
        } catch (Exception ex) {
            LOG.error("Exception = " + ex.getMessage());
            JSONObject obj = new JSONObject();
            obj.put("response", "96");
            obj.put("description", ex.getMessage());
            response = obj.toJSONString();
        }
        
        LOG.info("Response => " + response);

        return response;
    }
    
    
    @ResponseBody
    @RequestMapping(value = "/prep", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String prepTreminal(@RequestBody String transaction) {
        
        String response = "";
        
        LOG.info("Request => " + transaction);
        
        try {
            
            JSONObject jsonObject = (JSONObject) JSONValue.parse(transaction);    
            
            String terminalid = (String) jsonObject.get("terminalid"); 
            String serialno = (String) jsonObject.get("serialno");
            if(serialno != null){
                serialno = serialno.replaceAll("[^a-zA-Z0-9]", "");
            }
            
            try {
                Terminal terminal = terminalJDBCTemplate.getTerminal(terminalid);
                if (terminal == null) {
                    JSONObject obj = new JSONObject();
                    obj.put("response", "05");
                    obj.put("description", "Terminal not allowed");
                    response = obj.toJSONString();
                    LOG.info("Response => " + response);
                    return response;
                }
            } catch (Exception ex) {
                JSONObject obj = new JSONObject();
                obj.put("response", "05");
                obj.put("description", "Terminal not allowed:"+ex.getMessage());
                response = obj.toJSONString();
                LOG.info("Response => " + response);
                return response;
            }
            
            response = NIBSS.prep(terminalid, LIVE_HOST, LIVE_PORT, LIVE_PROTOCOL, LIVE_KEY1, LIVE_KEY2, serialno);
            
            /*boolean status = NIBSS.prep(terminalid, LIVE_HOST, LIVE_PORT, LIVE_PROTOCOL, LIVE_KEY1, LIVE_KEY2, serialno);
            
            if(status == false){
                JSONObject obj = new JSONObject();
                obj.put("response", "05");
                obj.put("description", "Prep Failed");
                response = obj.toJSONString();
            }
            else{
                Epms epms = epmsJDBCTemplate.getEpms(terminalid);
                JSONObject obj = new JSONObject();
                obj.put("response", "00");
                obj.put("description", "Prep Successful");
                obj.put("merchantid", epms.getMerchantid());
                obj.put("terminalid", epms.getTerminalid());
                obj.put("country_code", epms.getCountrycode());
                obj.put("currency_code", epms.getCurrencycode());
                obj.put("merchant_category_code", epms.getMcc());
                obj.put("merchant_address", epms.getNamelocation());
                obj.put("datetime", epms.getDatetime());
                obj.put("pin_key", epms.getPkey());
                obj.put("callhome", epms.getCallhome());
                obj.put("timeout", epms.getTimeout());
                response = obj.toJSONString();
            }*/
            
        } catch (Exception ex) {
            LOG.error("Exception = " + ex.getMessage());
            JSONObject obj = new JSONObject();
            obj.put("response", "96");
            obj.put("description", ex.getMessage());
            response = obj.toJSONString();
        }
        
        LOG.info("Response => " + response);

        return response;
    }
    
    @ResponseBody
    @RequestMapping(value = "/transaction", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String performTransaction(@RequestBody String transaction) {
        
        String response = "";
        
        LOG.info("Request => " + transaction);
        
        try {
            
            JSONObject jsonObject = (JSONObject) JSONValue.parse(transaction);    
            
            String terminalid = (String) jsonObject.get("terminalid"); 
            Epms epms = null;
            try {
                epms = epmsJDBCTemplate.getEpms(terminalid);
                if (epms == null) {
                    JSONObject obj = new JSONObject();
                    obj.put("response", "05");
                    obj.put("description", "Terminal not prepped");
                    response = obj.toJSONString();
                    LOG.info("Response => " + response);
                    return response;
                }
            } catch (Exception ex) {
                JSONObject obj = new JSONObject();
                obj.put("response", "05");
                obj.put("description", "Terminal not prepped:"+ex.getMessage());
                response = obj.toJSONString();
                LOG.info("Response => " + response);
                return response;
            }
            
            String merchantid = (String) jsonObject.get("merchantid");
            String amount = (String) jsonObject.get("amount");
            amount = String.format("%012d", Integer.parseInt(amount));
            String cashback = (String) jsonObject.get("cashback");
            cashback = String.format("%012d", Integer.parseInt(cashback));
            //String fromac = (String) jsonObject.get("fromac");
            int ac = 0;
            if(jsonObject.get("fromac") == null){
                ac = 0;
            }
            else{
                try{
                   String fromac = (String) jsonObject.get("fromac");
                   ac = Integer.parseInt(fromac);
                }
                catch(Exception ex){
                    
                }
            }
            //String toac = (String) jsonObject.get("toac");
            String transtype = (String) jsonObject.get("transtype");
            String mcc = (String) jsonObject.get("merchant_category_code");
            String iccdata = (String) jsonObject.get("iccdata");
            String panseqno = (String) jsonObject.get("panseqno");
            panseqno = String.format("%03d", Integer.parseInt(panseqno));
            String track2 = (String) jsonObject.get("track2");
            String merchant_address = (String) jsonObject.get("merchant_address");
            String currencycode = (String) jsonObject.get("currency_code");
            String pinblock = (jsonObject.get("pinblock") == null) ? "" : (String)jsonObject.get("pinblock");
            String rrn = (jsonObject.get("rrn") == null) ? "" : (String)jsonObject.get("rrn");
           
            String status = NIBSS.transaction(LIVE_HOST, LIVE_PORT, LIVE_PROTOCOL, terminalid, merchantid, Integer.parseInt(transtype), rrn, amount, cashback, mcc, iccdata, panseqno, track2, merchant_address, currencycode, pinblock, ac, epms.getSkey());
            
            if (status == null || status.isEmpty()) {
                JSONObject obj = new JSONObject();
                obj.put("response", "99");
                obj.put("description", "No response from server");
                response = obj.toJSONString();
            } else {
                //TODO:check if its a correct JSON string here
                JsonObject jobject = null;
                try {
                    JsonElement jsonElement = new JsonParser().parse(status);
                    jobject = jsonElement.getAsJsonObject();

                    if (jobject == null) {
                        JSONObject obj = new JSONObject();
                        obj.put("response", "99");
                        obj.put("description", "Parser Error");
                        response = obj.toJSONString();
                    } else {
                        String f39 = "";
                        try {
                            f39 = jobject.getAsJsonPrimitive("39").getAsString();
                        } catch (Exception ex) {
                        }
                        
                        String datetime = "";
                        try {
                            datetime = jobject.getAsJsonPrimitive("7").getAsString();
                        } catch (Exception ex) {
                        }

                        String refno = "";
                        try {
                            refno = jobject.getAsJsonPrimitive("37").getAsString();
                        } catch (Exception ex) {
                        }
                        
                        String stanx = "";
                        try {
                            stanx = jobject.getAsJsonPrimitive("11").getAsString();
                        } catch (Exception ex) {
                        }

                        String authid = "";
                        try {
                            authid = jobject.getAsJsonPrimitive("38").getAsString();
                        } catch (Exception ex) {
                        }

                        String balance = "";
                        if (Integer.parseInt(transtype) == TRAN_TYPE_BALANCE) {
                            try {
                                balance = jobject.getAsJsonPrimitive("54").getAsString();
                                if (balance != null && !balance.isEmpty()) {
                                    if (balance.length() >= 20) {//FIELD54=0002566C 000042795475 0001566C 000042795598
                                        balance = balance.substring(8, 20);
                                    }
                                }
                            } catch (Exception ex) {
                            }
                        }

                        String iccresponse = "";
                        try {
                            iccresponse = jobject.getAsJsonPrimitive("55").getAsString();
                        } catch (Exception ex) {
                        }

                        if (f39 != null && f39.equals("00")) {
                            JSONObject obj = new JSONObject();
                            obj.put("response", "00");
                            obj.put("description", "APPROVED");
                            obj.put("rrn", refno);
                            obj.put("stan", stanx);
                            obj.put("authid", authid);
                            obj.put("datetime", datetime);
                            if(!iccresponse.isEmpty()){
                                obj.put("iccresponse", iccresponse);
                            }
                            if(!balance.isEmpty()){
                                obj.put("balance", balance);
                            }
                            response = obj.toJSONString();
                        } else {
                            JSONObject obj = new JSONObject();
                            obj.put("response", f39);
                            obj.put("rrn", refno);
                            obj.put("stan", stanx);
                            obj.put("datetime", datetime);
                            if(!iccresponse.isEmpty()){
                                obj.put("iccresponse", iccresponse);
                            }
                            obj.put("description", ResponseCodes.responseCodeErrorMessage(Integer.parseInt(f39)));
                            response = obj.toJSONString();
                        }
                    }
                } catch (Exception ex) {
                    LOG.error("Exception = " + ex.getMessage());
                    JSONObject obj = new JSONObject();
                    obj.put("response", "96");
                    obj.put("description", "Exception:"+ex.getMessage());
                    response = obj.toJSONString();
                }

            }
            
        } catch (Exception ex) {
            LOG.error("Exception = " + ex.getMessage());
            JSONObject obj = new JSONObject();
            obj.put("response", "96");
            obj.put("description", "Exception:"+ex.getMessage());
            response = obj.toJSONString();
        }
        
        LOG.info("Response => " + response);

        return response;
    }
    
    @ResponseBody
    @RequestMapping(value = "/callhome", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String performCallhome(@RequestBody String transaction) {
        
        String response = "";
        
        LOG.info("Request => " + transaction);
        
        try {
            
            JSONObject jsonObject = (JSONObject) JSONValue.parse(transaction);    
            
            String terminalid = (String) jsonObject.get("terminalid"); 
            Epms epms = null;
            try {
                epms = epmsJDBCTemplate.getEpms(terminalid);
                if (epms == null) {
                    JSONObject obj = new JSONObject();
                    obj.put("response", "05");
                    obj.put("description", "Terminal not prepped");
                    response = obj.toJSONString();
                    LOG.info("Response => " + response);
                    return response;
                }
            } catch (Exception ex) {
                JSONObject obj = new JSONObject();
                obj.put("response", "05");
                obj.put("description", "Terminal not prepped:"+ex.getMessage());
                response = obj.toJSONString();
                LOG.info("Response => " + response);
                return response;
            }
            
            String serialno = (String) jsonObject.get("serialno");
            if(serialno != null){
                serialno = serialno.replaceAll("[^a-zA-Z0-9]", "");
            }
           
            String status = NIBSS.callhome(LIVE_HOST, LIVE_PORT, LIVE_PROTOCOL, terminalid, serialno, epms.getSkey());
            
            if (status == null || status.isEmpty()) {
                JSONObject obj = new JSONObject();
                obj.put("response", "99");
                obj.put("description", "No response from server");
                response = obj.toJSONString();
            } else {
                //TODO:check if its a correct JSON string here
                JsonObject jobject = null;
                try {
                    JsonElement jsonElement = new JsonParser().parse(status);
                    jobject = jsonElement.getAsJsonObject();

                    if (jobject == null) {
                        JSONObject obj = new JSONObject();
                        obj.put("response", "99");
                        obj.put("description", "Parser Error");
                        response = obj.toJSONString();
                    } else {
                        String f39 = "";
                        try {
                            f39 = jobject.getAsJsonPrimitive("39").getAsString();
                        } catch (Exception ex) {
                        }
                       
                        if (f39 != null && f39.equals("00")) {
                            JSONObject obj = new JSONObject();
                            obj.put("response", "00");
                            obj.put("description", "Callhome OK");
                            response = obj.toJSONString();
                        } else {
                            JSONObject obj = new JSONObject();
                            obj.put("response", f39);
                            obj.put("description", "Callhome Failed");
                            response = obj.toJSONString();
                        }
                    }
                } catch (Exception ex) {
                    LOG.error("Exception = " + ex.getMessage());
                    JSONObject obj = new JSONObject();
                    obj.put("response", "96");
                    obj.put("description", "Exception:"+ex.getMessage());
                    response = obj.toJSONString();
                }

            }
            
        } catch (Exception ex) {
            LOG.error("Exception = " + ex.getMessage());
            JSONObject obj = new JSONObject();
            obj.put("response", "96");
            obj.put("description", "Exception:"+ex.getMessage());
            response = obj.toJSONString();
        }
        
        LOG.info("Response => " + response);

        return response;
    }
    
    @ResponseBody
    @RequestMapping(value = "/prep_test", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String prepTreminalTest(@RequestBody String transaction) {
        
        String response = "";
        
        LOG.info("Request => " + transaction);
        
        try {
            
            JSONObject jsonObject = (JSONObject) JSONValue.parse(transaction);    
            
            String terminalid = (String) jsonObject.get("terminalid"); 
            String serialno = (String) jsonObject.get("serialno");
            if(serialno != null){
                serialno = serialno.replaceAll("[^a-zA-Z0-9]", "");
            }
            
            try {
                Terminal terminal = terminalJDBCTemplate.getTerminal(terminalid);
                if (terminal == null) {
                    JSONObject obj = new JSONObject();
                    obj.put("response", "05");
                    obj.put("description", "Terminal not allowed");
                    response = obj.toJSONString();
                    LOG.info("Response => " + response);
                    return response;
                }
            } catch (Exception ex) {
                JSONObject obj = new JSONObject();
                obj.put("response", "05");
                obj.put("description", "Terminal not allowed:"+ex.getMessage());
                response = obj.toJSONString();
                LOG.info("Response => " + response);
                return response;
            }
            
            response = NIBSS.prep(terminalid, TEST_HOST, TEST_PORT, TEST_PROTOCOL, TEST_KEY1, TEST_KEY2, serialno);
            
            /*boolean status = NIBSS.prep(terminalid, TEST_HOST, TEST_PORT, TEST_PROTOCOL, TEST_KEY1, TEST_KEY2, serialno);
            
            if(status == false){
                JSONObject obj = new JSONObject();
                obj.put("response", "05");
                obj.put("description", "Prep Failed");
                response = obj.toJSONString();
            }
            else{
                Epms epms = epmsJDBCTemplate.getEpms(terminalid);
                JSONObject obj = new JSONObject();
                obj.put("response", "00");
                obj.put("description", "Prep Successful");
                obj.put("merchantid", epms.getMerchantid());
                obj.put("terminalid", epms.getTerminalid());
                obj.put("country_code", epms.getCountrycode());
                obj.put("currency_code", epms.getCurrencycode());
                obj.put("merchant_category_code", epms.getMcc());
                obj.put("merchant_address", epms.getNamelocation());
                obj.put("datetime", epms.getDatetime());
                obj.put("pin_key", epms.getPkey());
                obj.put("callhome", epms.getCallhome());
                obj.put("timeout", epms.getTimeout());
                response = obj.toJSONString();
            }*/
            
        } catch (Exception ex) {
            LOG.error("Exception = " + ex.getMessage());
            JSONObject obj = new JSONObject();
            obj.put("response", "96");
            obj.put("description", ex.getMessage());
            response = obj.toJSONString();
        }
        
        LOG.info("Response => " + response);

        return response;
    }
    
    @ResponseBody
    @RequestMapping(value = "/transaction_test", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String performTransactionTest(@RequestBody String transaction) {
        
        String response = "";
        
        LOG.info("Request => " + transaction);
        
        try {
            
            JSONObject jsonObject = (JSONObject) JSONValue.parse(transaction);    
            
            String terminalid = (String) jsonObject.get("terminalid"); 
            Epms epms = null;
            try {
                epms = epmsJDBCTemplate.getEpms(terminalid);
                if (epms == null) {
                    JSONObject obj = new JSONObject();
                    obj.put("response", "05");
                    obj.put("description", "Terminal not prepped");
                    response = obj.toJSONString();
                    LOG.info("Response => " + response);
                    return response;
                }
            } catch (Exception ex) {
                JSONObject obj = new JSONObject();
                obj.put("response", "05");
                obj.put("description", "Terminal not prepped:"+ex.getMessage());
                response = obj.toJSONString();
                LOG.info("Response => " + response);
                return response;
            }
            
            String merchantid = (String) jsonObject.get("merchantid");
            String amount = (String) jsonObject.get("amount");
            amount = String.format("%012d", Integer.parseInt(amount));
            String cashback = (String) jsonObject.get("cashback");
            cashback = String.format("%012d", Integer.parseInt(cashback));
            //String fromac = (String) jsonObject.get("fromac");
            int ac = 0;
            if(jsonObject.get("fromac") == null){
                ac = 0;
            }
            else{
                try{
                   String fromac = (String) jsonObject.get("fromac");
                   ac = Integer.parseInt(fromac);
                }
                catch(Exception ex){
                    
                }
            }
            //String toac = (String) jsonObject.get("toac");
            String transtype = (String) jsonObject.get("transtype");
            String mcc = (String) jsonObject.get("merchant_category_code");
            String iccdata = (String) jsonObject.get("iccdata");
            String panseqno = (String) jsonObject.get("panseqno");
            panseqno = String.format("%03d", Integer.parseInt(panseqno));
            String track2 = (String) jsonObject.get("track2");
            String merchant_address = (String) jsonObject.get("merchant_address");
            String currencycode = (String) jsonObject.get("currency_code");
            String pinblock = (jsonObject.get("pinblock") == null) ? "" : (String)jsonObject.get("pinblock");
            String rrn = (jsonObject.get("rrn") == null) ? "" : (String)jsonObject.get("rrn");
           
            String status = NIBSS.transaction(TEST_HOST, TEST_PORT, TEST_PROTOCOL, terminalid, merchantid, Integer.parseInt(transtype), rrn, amount, cashback, mcc, iccdata, panseqno, track2, merchant_address, currencycode, pinblock, ac, epms.getSkey());
            
            if (status == null || status.isEmpty()) {
                JSONObject obj = new JSONObject();
                obj.put("response", "99");
                obj.put("description", "No response from server");
                response = obj.toJSONString();
            } else {
                //TODO:check if its a correct JSON string here
                JsonObject jobject = null;
                try {
                    JsonElement jsonElement = new JsonParser().parse(status);
                    jobject = jsonElement.getAsJsonObject();

                    if (jobject == null) {
                        JSONObject obj = new JSONObject();
                        obj.put("response", "99");
                        obj.put("description", "Parser Error");
                        response = obj.toJSONString();
                    } else {
                        String f39 = "";
                        try {
                            f39 = jobject.getAsJsonPrimitive("39").getAsString();
                        } catch (Exception ex) {
                        }
                        
                        String datetime = "";
                        try {
                            datetime = jobject.getAsJsonPrimitive("7").getAsString();
                        } catch (Exception ex) {
                        }

                        String refno = "";
                        try {
                            refno = jobject.getAsJsonPrimitive("37").getAsString();
                        } catch (Exception ex) {
                        }
                        
                        String stanx = "";
                        try {
                            stanx = jobject.getAsJsonPrimitive("11").getAsString();
                        } catch (Exception ex) {
                        }

                        String authid = "";
                        try {
                            authid = jobject.getAsJsonPrimitive("38").getAsString();
                        } catch (Exception ex) {
                        }

                        String balance = "";
                        if (Integer.parseInt(transtype) == TRAN_TYPE_BALANCE) {
                            try {
                                balance = jobject.getAsJsonPrimitive("54").getAsString();
                                if (balance != null && !balance.isEmpty()) {
                                    if (balance.length() >= 20) {//FIELD54=0002566C 000042795475 0001566C 000042795598
                                        balance = balance.substring(8, 20);
                                    }
                                }
                            } catch (Exception ex) {
                            }
                        }

                        String iccresponse = "";
                        try {
                            iccresponse = jobject.getAsJsonPrimitive("55").getAsString();
                        } catch (Exception ex) {
                        }

                        if (f39 != null && f39.equals("00")) {
                            JSONObject obj = new JSONObject();
                            obj.put("response", "00");
                            obj.put("description", "APPROVED");
                            obj.put("rrn", refno);
                            obj.put("stan", stanx);
                            obj.put("authid", authid);
                            if(!iccresponse.isEmpty()){
                                obj.put("iccresponse", iccresponse);
                            }
                            if(!balance.isEmpty()){
                                obj.put("balance", balance);
                            }
                            response = obj.toJSONString();
                        } else {
                            JSONObject obj = new JSONObject();
                            obj.put("response", f39);
                            obj.put("rrn", refno);
                            obj.put("stan", stanx);
                            if(!iccresponse.isEmpty()){
                                obj.put("iccresponse", iccresponse);
                            }
                            obj.put("description", ResponseCodes.responseCodeErrorMessage(Integer.parseInt(f39)));
                            response = obj.toJSONString();
                        }
                    }
                } catch (Exception ex) {
                    LOG.error("Exception = " + ex.getMessage());
                    JSONObject obj = new JSONObject();
                    obj.put("response", "96");
                    obj.put("description", "Exception:"+ex.getMessage());
                    response = obj.toJSONString();
                }

            }
            
        } catch (Exception ex) {
            LOG.error("Exception = " + ex.getMessage());
            JSONObject obj = new JSONObject();
            obj.put("response", "96");
            obj.put("description", "Exception:"+ex.getMessage());
            response = obj.toJSONString();
        }
        
        LOG.info("Response => " + response);

        return response;
    }
    
    @ResponseBody
    @RequestMapping(value = "/callhome_test", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String performCallhomeTest(@RequestBody String transaction) {
        
        String response = "";
        
        LOG.info("Request => " + transaction);
        
        try {
            
            JSONObject jsonObject = (JSONObject) JSONValue.parse(transaction);    
            
            String terminalid = (String) jsonObject.get("terminalid"); 
            Epms epms = null;
            try {
                epms = epmsJDBCTemplate.getEpms(terminalid);
                if (epms == null) {
                    JSONObject obj = new JSONObject();
                    obj.put("response", "05");
                    obj.put("description", "Terminal not prepped");
                    response = obj.toJSONString();
                    LOG.info("Response => " + response);
                    return response;
                }
            } catch (Exception ex) {
                JSONObject obj = new JSONObject();
                obj.put("response", "05");
                obj.put("description", "Terminal not prepped:"+ex.getMessage());
                response = obj.toJSONString();
                LOG.info("Response => " + response);
                return response;
            }
            
            String serialno = (String) jsonObject.get("serialno");
            if(serialno != null){
                serialno = serialno.replaceAll("[^a-zA-Z0-9]", "");
            }
           
            String status = NIBSS.callhome(TEST_HOST, TEST_PORT, TEST_PROTOCOL, terminalid, serialno, epms.getSkey());
            
            if (status == null || status.isEmpty()) {
                JSONObject obj = new JSONObject();
                obj.put("response", "99");
                obj.put("description", "No response from server");
                response = obj.toJSONString();
            } else {
                //TODO:check if its a correct JSON string here
                JsonObject jobject = null;
                try {
                    JsonElement jsonElement = new JsonParser().parse(status);
                    jobject = jsonElement.getAsJsonObject();

                    if (jobject == null) {
                        JSONObject obj = new JSONObject();
                        obj.put("response", "99");
                        obj.put("description", "Parser Error");
                        response = obj.toJSONString();
                    } else {
                        String f39 = "";
                        try {
                            f39 = jobject.getAsJsonPrimitive("39").getAsString();
                        } catch (Exception ex) {
                        }
                       

                        if (f39 != null && f39.equals("00")) {
                            JSONObject obj = new JSONObject();
                            obj.put("response", "00");
                            obj.put("description", "Callhome OK");
                            response = obj.toJSONString();
                        } else {
                            JSONObject obj = new JSONObject();
                            obj.put("response", f39);
                            obj.put("description", "Callhome Failed");
                            response = obj.toJSONString();
                        }
                    }
                } catch (Exception ex) {
                    LOG.error("Exception = " + ex.getMessage());
                    JSONObject obj = new JSONObject();
                    obj.put("response", "96");
                    obj.put("description", "Exception:"+ex.getMessage());
                    response = obj.toJSONString();
                }

            }
            
        } catch (Exception ex) {
            LOG.error("Exception = " + ex.getMessage());
            JSONObject obj = new JSONObject();
            obj.put("response", "96");
            obj.put("description", "Exception:"+ex.getMessage());
            response = obj.toJSONString();
        }
        
        LOG.info("Response => " + response);

        return response;
    }
    
    
    @ResponseBody
    @RequestMapping(value = "/prep_posvas", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String prepPOSVASTreminal(@RequestBody String transaction) {
        
        String response = "";
        
        LOG.info("Request => " + transaction);
        
        try {
            
            JSONObject jsonObject = (JSONObject) JSONValue.parse(transaction);    
            
            String terminalid = (String) jsonObject.get("terminalid"); 
            String serialno = (String) jsonObject.get("serialno");
            if(serialno != null){
                serialno = serialno.replaceAll("[^a-zA-Z0-9]", "");
            }
            
            try {
                Terminal terminal = terminalJDBCTemplate.getTerminal(terminalid);
                if (terminal == null) {
                    JSONObject obj = new JSONObject();
                    obj.put("response", "05");
                    obj.put("description", "Terminal not allowed");
                    response = obj.toJSONString();
                    LOG.info("Response => " + response);
                    return response;
                }
            } catch (Exception ex) {
                JSONObject obj = new JSONObject();
                obj.put("response", "05");
                obj.put("description", "Terminal not allowed:"+ex.getMessage());
                response = obj.toJSONString();
                LOG.info("Response => " + response);
                return response;
            }
            
            response = NIBSS.prep(terminalid, POSVAS_LIVE_HOST, POSVAS_LIVE_PORT, POSVAS_LIVE_PROTOCOL, POSVAS_LIVE_KEY1, POSVAS_LIVE_KEY2, serialno);
            
            /*boolean status = NIBSS.prep(terminalid, POSVAS_LIVE_HOST, POSVAS_LIVE_PORT, POSVAS_LIVE_PROTOCOL, POSVAS_LIVE_KEY1, POSVAS_LIVE_KEY2, serialno);
            
            if(status == false){
                JSONObject obj = new JSONObject();
                obj.put("response", "05");
                obj.put("description", "Prep Failed");
                response = obj.toJSONString();
            }
            else{
                Epms epms = epmsJDBCTemplate.getEpms(terminalid);
                JSONObject obj = new JSONObject();
                obj.put("response", "00");
                obj.put("description", "Prep Successful");
                obj.put("merchantid", epms.getMerchantid());
                obj.put("terminalid", epms.getTerminalid());
                obj.put("country_code", epms.getCountrycode());
                obj.put("currency_code", epms.getCurrencycode());
                obj.put("merchant_category_code", epms.getMcc());
                obj.put("merchant_address", epms.getNamelocation());
                obj.put("datetime", epms.getDatetime());
                obj.put("pin_key", epms.getPkey());
                obj.put("callhome", epms.getCallhome());
                obj.put("timeout", epms.getTimeout());
                response = obj.toJSONString();
            }*/
            
        } catch (Exception ex) {
            LOG.error("Exception = " + ex.getMessage());
            JSONObject obj = new JSONObject();
            obj.put("response", "96");
            obj.put("description", ex.getMessage());
            response = obj.toJSONString();
        }
        
        LOG.info("Response => " + response);

        return response;
    }
    
    @ResponseBody
    @RequestMapping(value = "/transaction_posvas", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String performPOSVASTransaction(@RequestBody String transaction) {
        
        String response = "";
        
        LOG.info("Request => " + transaction);
        
        try {
            
            JSONObject jsonObject = (JSONObject) JSONValue.parse(transaction);    
            
            String terminalid = (String) jsonObject.get("terminalid"); 
            Epms epms = null;
            try {
                epms = epmsJDBCTemplate.getEpms(terminalid);
                if (epms == null) {
                    JSONObject obj = new JSONObject();
                    obj.put("response", "05");
                    obj.put("description", "Terminal not prepped");
                    response = obj.toJSONString();
                    LOG.info("Response => " + response);
                    return response;
                }
            } catch (Exception ex) {
                JSONObject obj = new JSONObject();
                obj.put("response", "05");
                obj.put("description", "Terminal not prepped:"+ex.getMessage());
                response = obj.toJSONString();
                LOG.info("Response => " + response);
                return response;
            }
            
            String merchantid = (String) jsonObject.get("merchantid");
            String amount = (String) jsonObject.get("amount");
            amount = String.format("%012d", Integer.parseInt(amount));
            String cashback = (String) jsonObject.get("cashback");
            cashback = String.format("%012d", Integer.parseInt(cashback));
            //String fromac = (String) jsonObject.get("fromac");
            int ac = 0;
            if(jsonObject.get("fromac") == null){
                ac = 0;
            }
            else{
                try{
                   String fromac = (String) jsonObject.get("fromac");
                   ac = Integer.parseInt(fromac);
                }
                catch(Exception ex){
                    
                }
            }
            //String toac = (String) jsonObject.get("toac");
            String transtype = (String) jsonObject.get("transtype");
            String mcc = (String) jsonObject.get("merchant_category_code");
            String iccdata = (String) jsonObject.get("iccdata");
            String panseqno = (String) jsonObject.get("panseqno");
            panseqno = String.format("%03d", Integer.parseInt(panseqno));
            String track2 = (String) jsonObject.get("track2");
            String merchant_address = (String) jsonObject.get("merchant_address");
            String currencycode = (String) jsonObject.get("currency_code");
            String pinblock = (jsonObject.get("pinblock") == null) ? "" : (String)jsonObject.get("pinblock");
            String rrn = (jsonObject.get("rrn") == null) ? "" : (String)jsonObject.get("rrn");
           
            String status = NIBSS.transaction(POSVAS_LIVE_HOST, POSVAS_LIVE_PORT, POSVAS_LIVE_PROTOCOL, terminalid, merchantid, Integer.parseInt(transtype), rrn, amount, cashback, mcc, iccdata, panseqno, track2, merchant_address, currencycode, pinblock, ac, epms.getSkey());
            
            if (status == null || status.isEmpty()) {
                JSONObject obj = new JSONObject();
                obj.put("response", "99");
                obj.put("description", "No response from server");
                response = obj.toJSONString();
            } else {
                //TODO:check if its a correct JSON string here
                JsonObject jobject = null;
                try {
                    JsonElement jsonElement = new JsonParser().parse(status);
                    jobject = jsonElement.getAsJsonObject();

                    if (jobject == null) {
                        JSONObject obj = new JSONObject();
                        obj.put("response", "99");
                        obj.put("description", "Parser Error");
                        response = obj.toJSONString();
                    } else {
                        String f39 = "";
                        try {
                            f39 = jobject.getAsJsonPrimitive("39").getAsString();
                        } catch (Exception ex) {
                        }
                        
                        String datetime = "";
                        try {
                            datetime = jobject.getAsJsonPrimitive("7").getAsString();
                        } catch (Exception ex) {
                        }

                        String refno = "";
                        try {
                            refno = jobject.getAsJsonPrimitive("37").getAsString();
                        } catch (Exception ex) {
                        }
                        
                        String stanx = "";
                        try {
                            stanx = jobject.getAsJsonPrimitive("11").getAsString();
                        } catch (Exception ex) {
                        }

                        String authid = "";
                        try {
                            authid = jobject.getAsJsonPrimitive("38").getAsString();
                        } catch (Exception ex) {
                        }

                        String balance = "";
                        if (Integer.parseInt(transtype) == TRAN_TYPE_BALANCE) {
                            try {
                                balance = jobject.getAsJsonPrimitive("54").getAsString();
                                if (balance != null && !balance.isEmpty()) {
                                    if (balance.length() >= 20) {//FIELD54=0002566C 000042795475 0001566C 000042795598
                                        balance = balance.substring(8, 20);
                                    }
                                }
                            } catch (Exception ex) {
                            }
                        }

                        String iccresponse = "";
                        try {
                            iccresponse = jobject.getAsJsonPrimitive("55").getAsString();
                        } catch (Exception ex) {
                        }

                        if (f39 != null && f39.equals("00")) {
                            JSONObject obj = new JSONObject();
                            obj.put("response", "00");
                            obj.put("description", "APPROVED");
                            obj.put("rrn", refno);
                            obj.put("stan", stanx);
                            obj.put("authid", authid);
                            obj.put("datetime", datetime);
                            if(!iccresponse.isEmpty()){
                                obj.put("iccresponse", iccresponse);
                            }
                            if(!balance.isEmpty()){
                                obj.put("balance", balance);
                            }
                            response = obj.toJSONString();
                        } else {
                            JSONObject obj = new JSONObject();
                            obj.put("response", f39);
                            obj.put("rrn", refno);
                            obj.put("stan", stanx);
                            obj.put("datetime", datetime);
                            if(!iccresponse.isEmpty()){
                                obj.put("iccresponse", iccresponse);
                            }
                            obj.put("description", ResponseCodes.responseCodeErrorMessage(Integer.parseInt(f39)));
                            response = obj.toJSONString();
                        }
                    }
                } catch (Exception ex) {
                    LOG.error("Exception = " + ex.getMessage());
                    JSONObject obj = new JSONObject();
                    obj.put("response", "96");
                    obj.put("description", "Exception:"+ex.getMessage());
                    response = obj.toJSONString();
                }

            }
            
        } catch (Exception ex) {
            LOG.error("Exception = " + ex.getMessage());
            JSONObject obj = new JSONObject();
            obj.put("response", "96");
            obj.put("description", "Exception:"+ex.getMessage());
            response = obj.toJSONString();
        }
        
        LOG.info("Response => " + response);

        return response;
    }
    
    @ResponseBody
    @RequestMapping(value = "/callhome_posvas", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String performPOSVASCallhome(@RequestBody String transaction) {
        
        String response = "";
        
        LOG.info("Request => " + transaction);
        
        try {
            
            JSONObject jsonObject = (JSONObject) JSONValue.parse(transaction);    
            
            String terminalid = (String) jsonObject.get("terminalid"); 
            Epms epms = null;
            try {
                epms = epmsJDBCTemplate.getEpms(terminalid);
                if (epms == null) {
                    JSONObject obj = new JSONObject();
                    obj.put("response", "05");
                    obj.put("description", "Terminal not prepped");
                    response = obj.toJSONString();
                    LOG.info("Response => " + response);
                    return response;
                }
            } catch (Exception ex) {
                JSONObject obj = new JSONObject();
                obj.put("response", "05");
                obj.put("description", "Terminal not prepped:"+ex.getMessage());
                response = obj.toJSONString();
                LOG.info("Response => " + response);
                return response;
            }
            
            String serialno = (String) jsonObject.get("serialno");
            if(serialno != null){
                serialno = serialno.replaceAll("[^a-zA-Z0-9]", "");
            }
           
            String status = NIBSS.callhome(POSVAS_LIVE_HOST, POSVAS_LIVE_PORT, POSVAS_LIVE_PROTOCOL, terminalid, serialno, epms.getSkey());
            
            if (status == null || status.isEmpty()) {
                JSONObject obj = new JSONObject();
                obj.put("response", "99");
                obj.put("description", "No response from server");
                response = obj.toJSONString();
            } else {
                //TODO:check if its a correct JSON string here
                JsonObject jobject = null;
                try {
                    JsonElement jsonElement = new JsonParser().parse(status);
                    jobject = jsonElement.getAsJsonObject();

                    if (jobject == null) {
                        JSONObject obj = new JSONObject();
                        obj.put("response", "99");
                        obj.put("description", "Parser Error");
                        response = obj.toJSONString();
                    } else {
                        String f39 = "";
                        try {
                            f39 = jobject.getAsJsonPrimitive("39").getAsString();
                        } catch (Exception ex) {
                        }
                       
                        if (f39 != null && f39.equals("00")) {
                            JSONObject obj = new JSONObject();
                            obj.put("response", "00");
                            obj.put("description", "Callhome OK");
                            response = obj.toJSONString();
                        } else {
                            JSONObject obj = new JSONObject();
                            obj.put("response", f39);
                            obj.put("description", "Callhome Failed");
                            response = obj.toJSONString();
                        }
                    }
                } catch (Exception ex) {
                    LOG.error("Exception = " + ex.getMessage());
                    JSONObject obj = new JSONObject();
                    obj.put("response", "96");
                    obj.put("description", "Exception:"+ex.getMessage());
                    response = obj.toJSONString();
                }

            }
            
        } catch (Exception ex) {
            LOG.error("Exception = " + ex.getMessage());
            JSONObject obj = new JSONObject();
            obj.put("response", "96");
            obj.put("description", "Exception:"+ex.getMessage());
            response = obj.toJSONString();
        }
        
        LOG.info("Response => " + response);

        return response;
    }
    
    @ResponseBody
    @RequestMapping(value = "/prep_posvas_test", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String prepPOSVASTESTTreminal(@RequestBody String transaction) {
        
        String response = "";
        
        LOG.info("Request => " + transaction);
        
        try {
            
            JSONObject jsonObject = (JSONObject) JSONValue.parse(transaction);    
            
            String terminalid = (String) jsonObject.get("terminalid"); 
            String serialno = (String) jsonObject.get("serialno");
            if(serialno != null){
                serialno = serialno.replaceAll("[^a-zA-Z0-9]", "");
            }
            
            try {
                Terminal terminal = terminalJDBCTemplate.getTerminal(terminalid);
                if (terminal == null) {
                    JSONObject obj = new JSONObject();
                    obj.put("response", "05");
                    obj.put("description", "Terminal not allowed");
                    response = obj.toJSONString();
                    LOG.info("Response => " + response);
                    return response;
                }
            } catch (Exception ex) {
                JSONObject obj = new JSONObject();
                obj.put("response", "05");
                obj.put("description", "Terminal not allowed:"+ex.getMessage());
                response = obj.toJSONString();
                LOG.info("Response => " + response);
                return response;
            }
            
            response = NIBSS.prep(terminalid, POSVAS_TEST_HOST, POSVAS_TEST_PORT, POSVAS_TEST_PROTOCOL, POSVAS_TEST_KEY1, POSVAS_TEST_KEY2, serialno);
            
            /*boolean status = NIBSS.prep(terminalid, POSVAS_TEST_HOST, POSVAS_TEST_PORT, POSVAS_TEST_PROTOCOL, POSVAS_TEST_KEY1, POSVAS_TEST_KEY2, serialno);
            
            if(status == false){
                JSONObject obj = new JSONObject();
                obj.put("response", "05");
                obj.put("description", "Prep Failed");
                response = obj.toJSONString();
            }
            else{
                Epms epms = epmsJDBCTemplate.getEpms(terminalid);
                JSONObject obj = new JSONObject();
                obj.put("response", "00");
                obj.put("description", "Prep Successful");
                obj.put("merchantid", epms.getMerchantid());
                obj.put("terminalid", epms.getTerminalid());
                obj.put("country_code", epms.getCountrycode());
                obj.put("currency_code", epms.getCurrencycode());
                obj.put("merchant_category_code", epms.getMcc());
                obj.put("merchant_address", epms.getNamelocation());
                obj.put("datetime", epms.getDatetime());
                obj.put("pin_key", epms.getPkey());
                obj.put("callhome", epms.getCallhome());
                obj.put("timeout", epms.getTimeout());
                response = obj.toJSONString();
            }*/
            
        } catch (Exception ex) {
            LOG.error("Exception = " + ex.getMessage());
            JSONObject obj = new JSONObject();
            obj.put("response", "96");
            obj.put("description", ex.getMessage());
            response = obj.toJSONString();
        }
        
        LOG.info("Response => " + response);

        return response;
    }
    
    @ResponseBody
    @RequestMapping(value = "/transaction_posvas_test", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String performPOSVASTESTTransaction(@RequestBody String transaction) {
        
        String response = "";
        
        LOG.info("Request => " + transaction);
        
        try {
            
            JSONObject jsonObject = (JSONObject) JSONValue.parse(transaction);    
            
            String terminalid = (String) jsonObject.get("terminalid"); 
            Epms epms = null;
            try {
                epms = epmsJDBCTemplate.getEpms(terminalid);
                if (epms == null) {
                    JSONObject obj = new JSONObject();
                    obj.put("response", "05");
                    obj.put("description", "Terminal not prepped");
                    response = obj.toJSONString();
                    LOG.info("Response => " + response);
                    return response;
                }
            } catch (Exception ex) {
                JSONObject obj = new JSONObject();
                obj.put("response", "05");
                obj.put("description", "Terminal not prepped:"+ex.getMessage());
                response = obj.toJSONString();
                LOG.info("Response => " + response);
                return response;
            }
            
            String merchantid = (String) jsonObject.get("merchantid");
            String amount = (String) jsonObject.get("amount");
            amount = String.format("%012d", Integer.parseInt(amount));
            String cashback = (String) jsonObject.get("cashback");
            cashback = String.format("%012d", Integer.parseInt(cashback));
            //String fromac = (String) jsonObject.get("fromac");
            int ac = 0;
            if(jsonObject.get("fromac") == null){
                ac = 0;
            }
            else{
                try{
                   String fromac = (String) jsonObject.get("fromac");
                   ac = Integer.parseInt(fromac);
                }
                catch(Exception ex){
                    
                }
            }
            //String toac = (String) jsonObject.get("toac");
            String transtype = (String) jsonObject.get("transtype");
            String mcc = (String) jsonObject.get("merchant_category_code");
            String iccdata = (String) jsonObject.get("iccdata");
            String panseqno = (String) jsonObject.get("panseqno");
            panseqno = String.format("%03d", Integer.parseInt(panseqno));
            String track2 = (String) jsonObject.get("track2");
            String merchant_address = (String) jsonObject.get("merchant_address");
            String currencycode = (String) jsonObject.get("currency_code");
            String pinblock = (jsonObject.get("pinblock") == null) ? "" : (String)jsonObject.get("pinblock");
            String rrn = (jsonObject.get("rrn") == null) ? "" : (String)jsonObject.get("rrn");
           
            String status = NIBSS.transaction(POSVAS_TEST_HOST, POSVAS_TEST_PORT, POSVAS_TEST_PROTOCOL, terminalid, merchantid, Integer.parseInt(transtype), rrn, amount, cashback, mcc, iccdata, panseqno, track2, merchant_address, currencycode, pinblock, ac, epms.getSkey());
            
            if (status == null || status.isEmpty()) {
                JSONObject obj = new JSONObject();
                obj.put("response", "99");
                obj.put("description", "No response from server");
                response = obj.toJSONString();
            } else {
                //TODO:check if its a correct JSON string here
                JsonObject jobject = null;
                try {
                    JsonElement jsonElement = new JsonParser().parse(status);
                    jobject = jsonElement.getAsJsonObject();

                    if (jobject == null) {
                        JSONObject obj = new JSONObject();
                        obj.put("response", "99");
                        obj.put("description", "Parser Error");
                        response = obj.toJSONString();
                    } else {
                        String f39 = "";
                        try {
                            f39 = jobject.getAsJsonPrimitive("39").getAsString();
                        } catch (Exception ex) {
                        }
                        
                        String datetime = "";
                        try {
                            datetime = jobject.getAsJsonPrimitive("7").getAsString();
                        } catch (Exception ex) {
                        }

                        String refno = "";
                        try {
                            refno = jobject.getAsJsonPrimitive("37").getAsString();
                        } catch (Exception ex) {
                        }
                        
                        String stanx = "";
                        try {
                            stanx = jobject.getAsJsonPrimitive("11").getAsString();
                        } catch (Exception ex) {
                        }

                        String authid = "";
                        try {
                            authid = jobject.getAsJsonPrimitive("38").getAsString();
                        } catch (Exception ex) {
                        }

                        String balance = "";
                        if (Integer.parseInt(transtype) == TRAN_TYPE_BALANCE) {
                            try {
                                balance = jobject.getAsJsonPrimitive("54").getAsString();
                                if (balance != null && !balance.isEmpty()) {
                                    if (balance.length() >= 20) {//FIELD54=0002566C 000042795475 0001566C 000042795598
                                        balance = balance.substring(8, 20);
                                    }
                                }
                            } catch (Exception ex) {
                            }
                        }

                        String iccresponse = "";
                        try {
                            iccresponse = jobject.getAsJsonPrimitive("55").getAsString();
                        } catch (Exception ex) {
                        }

                        if (f39 != null && f39.equals("00")) {
                            JSONObject obj = new JSONObject();
                            obj.put("response", "00");
                            obj.put("description", "APPROVED");
                            obj.put("rrn", refno);
                            obj.put("stan", stanx);
                            obj.put("authid", authid);
                            obj.put("datetime", datetime);
                            if(!iccresponse.isEmpty()){
                                obj.put("iccresponse", iccresponse);
                            }
                            if(!balance.isEmpty()){
                                obj.put("balance", balance);
                            }
                            response = obj.toJSONString();
                        } else {
                            JSONObject obj = new JSONObject();
                            obj.put("response", f39);
                            obj.put("rrn", refno);
                            obj.put("stan", stanx);
                            obj.put("datetime", datetime);
                            if(!iccresponse.isEmpty()){
                                obj.put("iccresponse", iccresponse);
                            }
                            obj.put("description", ResponseCodes.responseCodeErrorMessage(Integer.parseInt(f39)));
                            response = obj.toJSONString();
                        }
                    }
                } catch (Exception ex) {
                    LOG.error("Exception = " + ex.getMessage());
                    JSONObject obj = new JSONObject();
                    obj.put("response", "96");
                    obj.put("description", "Exception:"+ex.getMessage());
                    response = obj.toJSONString();
                }

            }
            
        } catch (Exception ex) {
            LOG.error("Exception = " + ex.getMessage());
            JSONObject obj = new JSONObject();
            obj.put("response", "96");
            obj.put("description", "Exception:"+ex.getMessage());
            response = obj.toJSONString();
        }
        
        LOG.info("Response => " + response);

        return response;
    }
    
    @ResponseBody
    @RequestMapping(value = "/callhome_posvas_test", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String performPOSVASTESTCallhome(@RequestBody String transaction) {
        
        String response = "";
        
        LOG.info("Request => " + transaction);
        
        try {
            
            JSONObject jsonObject = (JSONObject) JSONValue.parse(transaction);    
            
            String terminalid = (String) jsonObject.get("terminalid"); 
            Epms epms = null;
            try {
                epms = epmsJDBCTemplate.getEpms(terminalid);
                if (epms == null) {
                    JSONObject obj = new JSONObject();
                    obj.put("response", "05");
                    obj.put("description", "Terminal not prepped");
                    response = obj.toJSONString();
                    LOG.info("Response => " + response);
                    return response;
                }
            } catch (Exception ex) {
                JSONObject obj = new JSONObject();
                obj.put("response", "05");
                obj.put("description", "Terminal not prepped:"+ex.getMessage());
                response = obj.toJSONString();
                LOG.info("Response => " + response);
                return response;
            }
            
            String serialno = (String) jsonObject.get("serialno");
            if(serialno != null){
                serialno = serialno.replaceAll("[^a-zA-Z0-9]", "");
            }
           
            String status = NIBSS.callhome(POSVAS_TEST_HOST, POSVAS_TEST_PORT, POSVAS_TEST_PROTOCOL, terminalid, serialno, epms.getSkey());
            
            if (status == null || status.isEmpty()) {
                JSONObject obj = new JSONObject();
                obj.put("response", "99");
                obj.put("description", "No response from server");
                response = obj.toJSONString();
            } else {
                //TODO:check if its a correct JSON string here
                JsonObject jobject = null;
                try {
                    JsonElement jsonElement = new JsonParser().parse(status);
                    jobject = jsonElement.getAsJsonObject();

                    if (jobject == null) {
                        JSONObject obj = new JSONObject();
                        obj.put("response", "99");
                        obj.put("description", "Parser Error");
                        response = obj.toJSONString();
                    } else {
                        String f39 = "";
                        try {
                            f39 = jobject.getAsJsonPrimitive("39").getAsString();
                        } catch (Exception ex) {
                        }
                       
                        if (f39 != null && f39.equals("00")) {
                            JSONObject obj = new JSONObject();
                            obj.put("response", "00");
                            obj.put("description", "Callhome OK");
                            response = obj.toJSONString();
                        } else {
                            JSONObject obj = new JSONObject();
                            obj.put("response", f39);
                            obj.put("description", "Callhome Failed");
                            response = obj.toJSONString();
                        }
                    }
                } catch (Exception ex) {
                    LOG.error("Exception = " + ex.getMessage());
                    JSONObject obj = new JSONObject();
                    obj.put("response", "96");
                    obj.put("description", "Exception:"+ex.getMessage());
                    response = obj.toJSONString();
                }

            }
            
        } catch (Exception ex) {
            LOG.error("Exception = " + ex.getMessage());
            JSONObject obj = new JSONObject();
            obj.put("response", "96");
            obj.put("description", "Exception:"+ex.getMessage());
            response = obj.toJSONString();
        }
        
        LOG.info("Response => " + response);

        return response;
    }
    
}
