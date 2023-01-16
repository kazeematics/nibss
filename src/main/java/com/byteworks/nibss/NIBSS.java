package com.byteworks.nibss;


import com.google.gson.*;
import com.byteworks.utils.Crypto;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import com.besl.nibss.epms.ISO8583;
import com.byteworks.model.epms.Epms;
import com.byteworks.model.epms.EpmsJDBCTemplate;
//import com.byteworks.model.remoteserver.RemoteServer;
//import com.byteworks.model.remoteserver.RemoteServerJDBCTemplate;
//import com.byteworks.model.remoteserverkey.RemoteServerKey;
//import com.byteworks.model.remoteserverkey.RemoteServerKeyJDBCTemplate;
import com.byteworks.model.terminal.Terminal;
import com.byteworks.model.terminal.TerminalJDBCTemplate;
import com.byteworks.model.transaction.Transaction;
import com.byteworks.model.transaction.TransactionJDBCTemplate;
import com.byteworks.rest.EPMSRESTController;
import com.byteworks.utils.Constants;
import com.byteworks.utils.Crypto;
import com.byteworks.utils.ResponseCodes;
import com.byteworks.utils.SecureKeyProvider;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.crypto.SecretKey;
//import org.byteworks.main.WicketApplication;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
//import static org.byteworks.main.WicketApplication.context;
//import static org.byteworks.main.WicketApplication.epmsJDBCTemplate;
//import static org.byteworks.main.WicketApplication.remoteServerKeyJDBCTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class NIBSS {

    private static final String TAG = "Initiate";
    
    private static final Logger LOG = Logger.getLogger(NIBSS.class);

    /*private static String date1 = com.byteworks.utils.DateUtil.getDate(new Date(), "yyMMdd");//YYMMDD
    private static String dateF13 = com.byteworks.utils.DateUtil.getDate(new Date(), "MMdd");//MMDD

    private static String timeF12 = com.byteworks.utils.DateUtil.getDate(new Date(), "HHmmss");//HHMMSS
    private static String dateF7 = com.byteworks.utils.DateUtil.getDate(new Date(), "MMddHHmmss");//MMDDhhmmss*/
    
    public static ApplicationContext context = new ClassPathXmlApplicationContext("applicationBeanContext.xml");
    public static TransactionJDBCTemplate transactionJDBCTemplate = (TransactionJDBCTemplate) context.getBean("transactionJDBCTemplate");
    public static TerminalJDBCTemplate terminalJDBCTemplate = (TerminalJDBCTemplate) context.getBean("terminalJDBCTemplate");
    public static EpmsJDBCTemplate epmsJDBCTemplate = (EpmsJDBCTemplate) context.getBean("epmsJDBCTemplate");

    /**
     * Remove any diacritical marks (accents like ç, ñ, é, etc) from the given
     * string (so that it returns plain c, n, e, etc).
     *
     * @param string The string to remove diacritical marks from.
     * @return The string with removed diacritical marks, if any.
     */
    public static String removeDiacriticalMarks(String string) {
        return Normalizer.normalize(string, Form.NFD).replaceAll("[^\\p{ASCII}]", "");//replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
    //private static byte[]  responseData = null;

    public static String masterKeyDownload(String host, String port, String protocol, String stan, String terminalid, String posSerial){

        String date1 = com.byteworks.utils.DateUtil.getDate(new Date(), "yyMMdd");//YYMMDD
        String dateF13 = com.byteworks.utils.DateUtil.getDate(new Date(), "MMdd");//MMDD

        String timeF12 = com.byteworks.utils.DateUtil.getDate(new Date(), "HHmmss");//HHMMSS
        String dateF7 = com.byteworks.utils.DateUtil.getDate(new Date(), "MMddHHmmss");//MMDDhhmmss
        
        ISO8583 iso8583 = new ISO8583();
        //iso8583.getVersion();
        String unpacked = null;

        String F62 = String.format("01%03d%s", posSerial.length(), posSerial);

        JsonObject innerObject = new JsonObject();
        innerObject.addProperty("0", "0800");
        innerObject.addProperty("3", "9A0000");
        innerObject.addProperty("7", dateF7);
        innerObject.addProperty("11", stan);
        innerObject.addProperty("12", timeF12);
        innerObject.addProperty("13", dateF13);
        innerObject.addProperty("41", terminalid);
        innerObject.addProperty("62", F62);

        LOG.info( "request.masterKey : " + innerObject.toString());

        try {
            String packed = iso8583.packEPMSISO8583Message(innerObject.toString(), "");
            LOG.info("packedISO : " + packed);
            LOG.info("packedFields : " + iso8583.unpackEPMSISO8583Message(packed, ""));
            if (packed != null) {
                LOG.info("IP:PORT : " + host + ":" + port);
                byte[] response = sendAndReceiveDataFromNIBSS(host, Integer.parseInt(port), Integer.parseInt(protocol), hexStringToBytes(packed));
                if (response != null) {
                    LOG.info("response : " + hex(response));
                    if (!hex(response).isEmpty()) {
                        unpacked = iso8583.unpackEPMSISO8583Message(hex(response), "");
                        LOG.info("unpackedISO : " + unpacked);
                    } else {
                        return null;
                    }
                }
            } else {
                return null;
            }
        } catch (Exception ex) {
            LOG.info("Exception : " + ex.getMessage());
            return null;
        }

        return unpacked;
    }

    public static String sessionKeyDownload(String host, String port, String protocol, String stan, String terminalid, String posSerial){

        String date1 = com.byteworks.utils.DateUtil.getDate(new Date(), "yyMMdd");//YYMMDD
        String dateF13 = com.byteworks.utils.DateUtil.getDate(new Date(), "MMdd");//MMDD

        String timeF12 = com.byteworks.utils.DateUtil.getDate(new Date(), "HHmmss");//HHMMSS
        String dateF7 = com.byteworks.utils.DateUtil.getDate(new Date(), "MMddHHmmss");//MMDDhhmmss
        
        ISO8583 iso8583 = new ISO8583();
        //iso8583.getVersion();
        String unpacked = null;

        String F62 = String.format("01%03d%s", posSerial.length(), posSerial);

        JsonObject innerObject = new JsonObject();
        innerObject.addProperty("0", "0800");
        innerObject.addProperty("3", "9B0000");
        innerObject.addProperty("7", dateF7);
        innerObject.addProperty("11", stan);
        innerObject.addProperty("12", timeF12);
        innerObject.addProperty("13", dateF13);
        innerObject.addProperty("41", terminalid);
        innerObject.addProperty("62", F62);

        LOG.info( "sessionKey : " + innerObject.toString());

        try {
            String packed = iso8583.packEPMSISO8583Message(innerObject.toString(), "");
            LOG.info("packedISO : " + packed);
            LOG.info("packedFields : " + iso8583.unpackEPMSISO8583Message(packed, ""));
            if (packed != null) {
                LOG.info("IP:PORT : " + host + ":" + port);
                byte[] response = sendAndReceiveDataFromNIBSS(host, Integer.parseInt(port), Integer.parseInt(protocol), hexStringToBytes(packed));
                if (response != null) {
                    LOG.info("response : " + hex(response));
                    if (!hex(response).isEmpty()) {
                        unpacked = iso8583.unpackEPMSISO8583Message(hex(response), "");
                        LOG.info("unpackedISO : " + unpacked);
                    } else {
                        return null;
                    }
                }
            } else {
                return null;
            }
        } catch (Exception ex) {
            LOG.info("Exception : " + ex.getMessage());
            return null;
        }

        return unpacked;
    }

    public static String pinKeyDownload(String host, String port, String protocol, String stan, String terminalid, String posSerial){

        String date1 = com.byteworks.utils.DateUtil.getDate(new Date(), "yyMMdd");//YYMMDD
        String dateF13 = com.byteworks.utils.DateUtil.getDate(new Date(), "MMdd");//MMDD

        String timeF12 = com.byteworks.utils.DateUtil.getDate(new Date(), "HHmmss");//HHMMSS
        String dateF7 = com.byteworks.utils.DateUtil.getDate(new Date(), "MMddHHmmss");//MMDDhhmmss
        
        ISO8583 iso8583 = new ISO8583();
        //iso8583.getVersion();
        String unpacked = null;

        String F62 = String.format("01%03d%s", posSerial.length(), posSerial);

        JsonObject innerObject = new JsonObject();
        innerObject.addProperty("0", "0800");
        innerObject.addProperty("3", "9G0000");
        innerObject.addProperty("7", dateF7);
        innerObject.addProperty("11", stan);
        innerObject.addProperty("12", timeF12);
        innerObject.addProperty("13", dateF13);
        innerObject.addProperty("41", terminalid);
        innerObject.addProperty("62", F62);

        LOG.info( "pinKey : " + innerObject.toString());

        try {
            String packed = iso8583.packEPMSISO8583Message(innerObject.toString(), "");
            LOG.info("packedISO : " + packed);
            LOG.info("packedFields : " + iso8583.unpackEPMSISO8583Message(packed, ""));
            if (packed != null) {
                LOG.info("IP:PORT : " + host + ":" + port);
                byte[] response = sendAndReceiveDataFromNIBSS(host, Integer.parseInt(port), Integer.parseInt(protocol), hexStringToBytes(packed));
                if (response != null) {
                    LOG.info("response : " + hex(response));
                    if (!hex(response).isEmpty()) {
                        unpacked = iso8583.unpackEPMSISO8583Message(hex(response), "");
                        LOG.info("unpackedISO : " + unpacked);
                    } else {
                        return null;
                    }
                }
            } else {
                return null;
            }
        } catch (Exception ex) {
            LOG.info("Exception : " + ex.getMessage());
            return null;
        }

        return unpacked;
    }

    public static String parametersDownload(String host, String port, String protocol, String stan, String terminalid, String posSerial, String key){

        String date1 = com.byteworks.utils.DateUtil.getDate(new Date(), "yyMMdd");//YYMMDD
        String dateF13 = com.byteworks.utils.DateUtil.getDate(new Date(), "MMdd");//MMDD

        String timeF12 = com.byteworks.utils.DateUtil.getDate(new Date(), "HHmmss");//HHMMSS
        String dateF7 = com.byteworks.utils.DateUtil.getDate(new Date(), "MMddHHmmss");//MMDDhhmmss
        
        ISO8583 iso8583 = new ISO8583();
        //iso8583.getVersion();
        String unpacked = null;

        String F62 = String.format("01%03d%s", posSerial.length(), posSerial);

        JsonObject innerObject = new JsonObject();
        innerObject.addProperty("0", "0800");
        innerObject.addProperty("3", "9C0000");
        innerObject.addProperty("7", dateF7);
        innerObject.addProperty("11", stan);
        innerObject.addProperty("12", timeF12);
        innerObject.addProperty("13", dateF13);
        innerObject.addProperty("41", terminalid);
        innerObject.addProperty("62", F62);

        LOG.info( "parameters : " + innerObject.toString());

        try {
            String packed = iso8583.packEPMSISO8583Message(innerObject.toString(), key);
            LOG.info("packedISO : " + packed);
            LOG.info("packedFields : " + iso8583.unpackEPMSISO8583Message(packed, key));
            if (packed != null) {
                LOG.info("IP:PORT : " + host + ":" + port);
                byte[] response = sendAndReceiveDataFromNIBSS(host, Integer.parseInt(port), Integer.parseInt(protocol), hexStringToBytes(packed));
                if (response != null) {
                    LOG.info("response : " + hex(response));
                    if (!hex(response).isEmpty()) {
                        unpacked = iso8583.unpackEPMSISO8583Message(hex(response), key);
                        LOG.info("unpackedISO : " + unpacked);
                    } else {
                        return null;
                    }
                }
            } else {
                return null;
            }
        } catch (Exception ex) {
            LOG.info("Exception : " + ex.getMessage());
            return null;
        }

        return unpacked;
    }

    public static String callhome(String host, String port, String protocol, String terminalid, String posSerial, String key){

        String date1 = com.byteworks.utils.DateUtil.getDate(new Date(), "yyMMdd");//YYMMDD
        String dateF13 = com.byteworks.utils.DateUtil.getDate(new Date(), "MMdd");//MMDD

        String timeF12 = com.byteworks.utils.DateUtil.getDate(new Date(), "HHmmss");//HHMMSS
        String dateF7 = com.byteworks.utils.DateUtil.getDate(new Date(), "MMddHHmmss");//MMDDhhmmss
        
        ISO8583 iso8583 = new ISO8583();
        //iso8583.getVersion();
        String unpacked = null;
        
        SecureKeyProvider sp = new SecureKeyProvider();
        String stan = sp.get(6);

        String F62 = String.format("01%03d%s", posSerial.length(), posSerial);

        JsonObject innerObject = new JsonObject();
        innerObject.addProperty("0", "0800");
        innerObject.addProperty("3", "9D0000");
        innerObject.addProperty("7", dateF7);
        innerObject.addProperty("11", stan);
        innerObject.addProperty("12", timeF12);
        innerObject.addProperty("13", dateF13);
        innerObject.addProperty("41", terminalid);
        innerObject.addProperty("62", F62);

        LOG.info( "callhome : " + innerObject.toString());

        try {
            String packed = iso8583.packEPMSISO8583Message(innerObject.toString(), key);
            LOG.info("packedFields : " + iso8583.unpackEPMSISO8583Message(packed, key));
            LOG.info("packedISO : " + packed);
            if (packed != null) {
                LOG.info("IP:PORT : " + host + ":" + port);
                byte[] response = sendAndReceiveDataFromNIBSS(host, Integer.parseInt(port), Integer.parseInt(protocol), hexStringToBytes(packed));
                if (response != null) {
                    LOG.info("response : " + hex(response));
                    if (!hex(response).isEmpty()) {
                        unpacked = iso8583.unpackEPMSISO8583Message(hex(response), key);
                        LOG.info("unpackedISO : " + unpacked);
                    } else {
                        return null;
                    }
                }
            } else {
                return null;
            }
        } catch (Exception ex) {
            LOG.info("Exception : " + ex.getMessage());
            return null;
        }

        return unpacked;
    }

    public static String transaction(String host, String port, String protocol, String terminalid, String marchantid, int transtype, String rrn, String amount, String cashback, String mcc, String iccdata, String panseqno, String track2, String marchantNameAndAddress, String currencycode, String pinblock, int ac, String key){

        String date1 = com.byteworks.utils.DateUtil.getDate(new Date(), "yyMMdd");//YYMMDD
        String dateF13 = com.byteworks.utils.DateUtil.getDate(new Date(), "MMdd");//MMDD

        String timeF12 = com.byteworks.utils.DateUtil.getDate(new Date(), "HHmmss");//HHMMSS
        String dateF7 = com.byteworks.utils.DateUtil.getDate(new Date(), "MMddHHmmss");//MMDDhhmmss
        
        ISO8583 iso8583 = new ISO8583();
        String pan = "";
        String expiry = "";
        String servicecode = "";
        String stan = "";
        String refno = "";
        String mti = "";
        String responsecode = "";
        String authid = "";
        //iso8583.getVersion();
        String unpacked = null;
        
        Transaction transaction = null;

        JsonObject innerObject = new JsonObject();
        
        if(track2.contains("D")){
             String []part = track2.split("D", -1);
             pan = part[0];
             expiry = part[1].substring(0, 4);
             servicecode = part[1].substring(4, 7);
         }
         else{
             String []part = track2.split("=", -1);
             pan = part[0];
             expiry = part[1].substring(0, 4);
             servicecode = part[1].substring(4, 7);
         }
        
        SecureKeyProvider sp = new SecureKeyProvider();
        stan = sp.get(6);
        if(rrn.isEmpty()){
            refno = sp.get(12);
        }
        else{
            refno = rrn;
        }
        
        int fromac = ac;//account Type set to default
        int toac = 0;
        
        if ((transtype == Constants.TRAN_TYPE_REVERSAL) || (transtype == Constants.TRAN_TYPE_REVERSAL2) || (transtype == Constants.TRAN_TYPE_REFUND) || (transtype == Constants.TRAN_TYPE_PREAUTH_LIFECYCLE)) {
            refno = rrn;
            try {
                transaction = transactionJDBCTemplate.getTransaction(rrn);
                if (transaction == null) {
                    return null;
                }
            } catch (Exception ex) {
                return null;
            }
        }

        /* set ISO message fields */
        if(transtype == Constants.TRAN_TYPE_PURCHASE){
            String F3 = String.format("00%02d%02d", fromac, toac);
            mti = "0200";
            innerObject.addProperty("0", mti);
            innerObject.addProperty("2", pan); //PAN
            innerObject.addProperty("3", F3);
        }
        else if(transtype == Constants.TRAN_TYPE_BALANCE){
            String F3 = String.format("31%02d%02d", fromac, toac);
            mti = "0100";
            innerObject.addProperty("0", mti);
            innerObject.addProperty("2", pan); //PAN
            innerObject.addProperty("3", F3);
        }
        else if(transtype == Constants.TRAN_TYPE_REFUND){
            String F3 = String.format("20%02d%02d", fromac, toac);
            mti = "0200";
            innerObject.addProperty("0", mti);
            innerObject.addProperty("2", pan); //PAN
            innerObject.addProperty("3", F3);
        }
        else if(transtype == Constants.TRAN_TYPE_CASHADVANCE){
            String F3 = String.format("01%02d%02d", fromac, toac);
            mti = "0200";
            innerObject.addProperty("0", mti);
            innerObject.addProperty("2", pan); //PAN
            innerObject.addProperty("3", F3);
        }
        else if(transtype == Constants.TRAN_TYPE_CASHBACK){
            String F3 = String.format("09%02d%02d", fromac, toac);
            mti = "0200";
            innerObject.addProperty("0", mti);
            innerObject.addProperty("2", pan); //PAN
            innerObject.addProperty("3", F3);
        }
        else if(transtype == Constants.TRAN_TYPE_DEPOSITS){
            String F3 = String.format("21%02d%02d", fromac, toac);
            mti = "0200";
            innerObject.addProperty("0", mti);
            innerObject.addProperty("2", pan); //PAN
            innerObject.addProperty("3", F3);
        }
        else if(transtype == Constants.TRAN_TYPE_REVERSAL){
            String F3 = String.format("00%02d%02d", fromac, toac);
            mti = "0420";
            innerObject.addProperty("0", mti);
            innerObject.addProperty("2", pan); //PAN
            innerObject.addProperty("3", F3);
        }
        else if(transtype == Constants.TRAN_TYPE_REVERSAL2){
            String F3 = String.format("00%02d%02d", fromac, toac);
            mti = "0421";
            innerObject.addProperty("0", mti);
            innerObject.addProperty("2", pan); //PAN
            innerObject.addProperty("3", F3);
        }
        else if(transtype == Constants.TRAN_TYPE_LINK_ACCOUNT_ENQUIRY){
            String F3 = String.format("30%02d%02d", fromac, toac);
            mti = "0100";
            innerObject.addProperty("0", mti);
            innerObject.addProperty("2", pan); //PAN
            innerObject.addProperty("3", F3);
        }
        else if(transtype == Constants.TRAN_TYPE_PREAUTH_PURCHASE){
            String F3 = String.format("60%02d%02d", fromac, toac);
            mti = "0100";
            innerObject.addProperty("0", mti);
            innerObject.addProperty("2", pan); //PAN
            innerObject.addProperty("3", F3);
        }
        else if(transtype == Constants.TRAN_TYPE_PREAUTH_LIFECYCLE){
            String F3 = String.format("61%02d%02d", fromac, toac);
            mti = "0200";
            innerObject.addProperty("0", mti);
            innerObject.addProperty("2", pan); //PAN
            innerObject.addProperty("3", F3);
        }
        else if(transtype == Constants.TRAN_TYPE_CHANGEPIN){
            String F3 = String.format("90%02d%02d", fromac, toac);
            mti = "0100";
            innerObject.addProperty("0", mti);
            innerObject.addProperty("2", pan); //PAN
            innerObject.addProperty("3", F3);
        }

        innerObject.addProperty("4", amount); //Amount

        LOG.info( "amount: " +amount);

        innerObject.addProperty("7", dateF7);
        if ((transtype == Constants.TRAN_TYPE_REVERSAL) || (transtype == Constants.TRAN_TYPE_REVERSAL2) || (transtype == Constants.TRAN_TYPE_REFUND)) {
            innerObject.addProperty("11", transaction.getStan());
            innerObject.addProperty("12", transaction.getTimeF12());
            innerObject.addProperty("13", transaction.getDateF13());
        } else {
            innerObject.addProperty("11", stan);
            innerObject.addProperty("12", timeF12);
            innerObject.addProperty("13", dateF13);
        }
        
        innerObject.addProperty("14", expiry);
        innerObject.addProperty("18", mcc);

        if(iccdata == null || iccdata.isEmpty()){//magstripe
            innerObject.addProperty("22", "901"); //POS Entry Mode
        }
        else{//chip
            innerObject.addProperty("22", "051"); //POS Entry Mode
        }

        innerObject.addProperty("23", panseqno);//Card Sequence Number
        innerObject.addProperty("25", "00");
        innerObject.addProperty("26", "12");
        innerObject.addProperty("28", "D00000000");
        innerObject.addProperty("32", track2.substring(0, 6)); //BIN
        innerObject.addProperty("35", track2);//Track 2 Data

        if ((transtype == Constants.TRAN_TYPE_REVERSAL) || (transtype == Constants.TRAN_TYPE_REVERSAL2) || (transtype == Constants.TRAN_TYPE_REFUND) || (transtype == Constants.TRAN_TYPE_PREAUTH_LIFECYCLE)) {
            innerObject.addProperty("37", rrn); //Retrieval Reference Number
        } else {
            innerObject.addProperty("37", refno); //Retrieval Reference Number
        }

        innerObject.addProperty("40", servicecode); //Service Restriction Code (Card Service Code)
        innerObject.addProperty("41", terminalid);
        innerObject.addProperty("42", marchantid);

        innerObject.addProperty("43", marchantNameAndAddress);
        innerObject.addProperty("49", currencycode);

        if(pinblock != null) {
            pinblock = pinblock.trim();
        }

        if(pinblock != null && !pinblock.isEmpty()){
            innerObject.addProperty("52", pinblock);//PIN Data
        }

        if(transtype == Constants.TRAN_TYPE_CASHBACK){
            String F54 = String.format("%02d%02d%s%s%012d", fromac,5, currencycode, "D", Long.parseLong(cashback));
            innerObject.addProperty("54", F54);//cashback amount
        }

        if(iccdata != null && !iccdata.isEmpty()){
            innerObject.addProperty("55", iccdata);//Integrated Circuit Card System Related Data
        }

        if((transtype == Constants.TRAN_TYPE_REVERSAL) || (transtype == Constants.TRAN_TYPE_REVERSAL2) || (transtype == Constants.TRAN_TYPE_PREAUTH_LIFECYCLE)){
            String F90 = String.format("%s%s%s%022d", transaction.getMti(), transaction.getStan(), transaction.getDateF7(), 0);
            innerObject.addProperty("90", F90);//Original Data Elements
        }

        if((transtype == Constants.TRAN_TYPE_REVERSAL) || (transtype == Constants.TRAN_TYPE_REVERSAL2) || (transtype == Constants.TRAN_TYPE_REFUND) || (transtype == Constants.TRAN_TYPE_PREAUTH_LIFECYCLE)){
            //String F95 = String.format("%012d%012dD%08dD%08d", Long.parseLong(amount), Long.parseLong(settlementAmount), Long.parseLong(transactionFee), Long.parseLong(settlementFee));
            String F95 = String.format("%012d%012dD%08dD%08d", Long.parseLong(amount), Long.parseLong("0"), Long.parseLong("0"), Long.parseLong("0"));
            innerObject.addProperty("95", F95);// Amount, Replacement
        }

        innerObject.addProperty("123", "510101511344001");//POS Data Code

        LOG.info( "transaction : " + innerObject.toString());

        try {
            String packed = iso8583.packEPMSISO8583Message(innerObject.toString(), key);
            LOG.info("packedFields : " + iso8583.unpackEPMSISO8583Message(packed, key));
            LOG.info("packedISO : " + packed);
            if (packed != null) {
                LOG.info("IP:PORT : " + host + ":" + port);
                byte[] response = sendAndReceiveDataFromNIBSS(host, Integer.parseInt(port), Integer.parseInt(protocol), hexStringToBytes(packed));
                if (response != null) {
                    LOG.info("response : " + hex(response));
                    if (response.length > 12) {
                        String rmti = new String(hexStringToBytes(hex(response).substring(4, 12)));
                        if (rmti.equals("0210") || rmti.equals("0110") || rmti.equals("0130") || rmti.equals("0230") || rmti.equals("0430")) {
                            if (!hex(response).isEmpty()) {
                                unpacked = iso8583.unpackEPMSISO8583Message(hex(response), "");
                                LOG.info("packedFields : " + unpacked);
                            } else {
                                return null;
                            }

                            JsonObject jobject = null;
                            try {
                                JsonElement jsonElement = new JsonParser().parse(unpacked);
                                jobject = jsonElement.getAsJsonObject();

                                if (jobject == null) {

                                } else {

                                    try {
                                        responsecode = jobject.getAsJsonPrimitive("39").getAsString();
                                    } catch (Exception ex) {
                                    }

                                    try {
                                        authid = jobject.getAsJsonPrimitive("38").getAsString();
                                    } catch (Exception ex) {
                                    }

                                }
                            } catch (Exception ex) {

                            }
                        } else {
                            return null;
                        }
                    }
                    else{
                        return null;
                    }
                }
            } else {
                return null;
            }
        } catch (Exception ex) {
            LOG.info("Exception : " + ex.getMessage());
            return null;
        }
        
        transactionJDBCTemplate.insertTransaction(terminalid, marchantid, fromac, toac, transtype, stan, refno, amount, cashback, pan, expiry, servicecode, mti, dateF7, timeF12, dateF13, responsecode, authid);

        return unpacked;
    }

    private static byte[] sendAndReceiveDataFromNIBSS(String host, int port, int ssl, byte[] requestData) {

        byte[] responseData = null;

        try {

            if (ssl == 1) {
                // Create a trust manager that does not validate certificate chains
                TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {

                            @Override
                            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                                // Trust always
                            }

                            @Override
                            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                                // Trust always
                            }

                            @Override
                            public X509Certificate[] getAcceptedIssuers() {
                                //return null;
                                return new X509Certificate[0];
                            }
                        }
                };

                // Install the all-trusting trust manager, TLS is the last SSL protocol and is used by all the CA
                SSLContext sc = SSLContext.getInstance("TLS");
                //SSLContext sc = SSLContext.getInstance("TLSv1.2");
                // Create empty HostnameVerifier
                HostnameVerifier hv = new HostnameVerifier() {
                    public boolean verify(String arg0, SSLSession arg1) {
                        return true;
                    }
                };

                //sc.init(null, trustAllCerts, new SecureRandom());
                sc.init(null, trustAllCerts, null);
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(hv);

                LOG.info( "Connecting via SSL...");

                SSLSocketFactory ssf = sc.getSocketFactory();
                //SSLSocket socket = (SSLSocket) ssf.createSocket(host, 5043);
                SSLSocket socket = (SSLSocket) ssf.createSocket();
                socket.setSoTimeout(60 * 1000);//receive timeout
                socket.connect(new InetSocketAddress(host, port), 30 * 1000);
                socket.startHandshake();

                /*Certificate[] serverCerts = socket.getSession().getPeerCertificates();
                System.out.println("Retreived Server's Certificate Chain");

                System.out.println(serverCerts.length + "Certifcates Foundnnn");
                for (int i = 0; i < serverCerts.length; i++) {
                    Certificate myCert = serverCerts[i];
                    System.out.println("====Certificate:" + (i + 1) + "====");
                    System.out.println("-Public Key-n" + myCert.getPublicKey());
                    System.out.println("-Certificate Type-n " + myCert.getType());

                    System.out.println();
                }*/

                // write data
                LOG.info( "Sending...");
                OutputStream socketOutputStream = socket.getOutputStream();
                socketOutputStream.write(requestData);

                // read data
                InputStream socketInputStream = socket.getInputStream();

                LOG.info( "Receiving...");
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[1024];
                while ((nRead = socketInputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                buffer.flush();
                responseData = buffer.toByteArray();

                socketOutputStream.close();
                socketInputStream.close();
                socket.close();

            } else {

                //Socket socket = new Socket(host, port);
                Socket socket = new Socket();
                socket.setSoTimeout(60 * 1000);//receive timeout
                socket.connect(new InetSocketAddress(host, port), 30 * 1000);

                LOG.info( "Sending...");
                // write data
                OutputStream socketOutputStream = socket.getOutputStream();
                socketOutputStream.write(requestData);

                LOG.info( "Receiving...");
                // read data
                InputStream socketInputStream = socket.getInputStream();

                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[1024];
                while ((nRead = socketInputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                buffer.flush();
                responseData = buffer.toByteArray();

                socketOutputStream.close();
                socketInputStream.close();
                socket.close();

            }

        } catch (Exception e) {
            LOG.info( "Error:"+ e.getMessage());
        }

        return responseData;

    }
    
    /*public static Boolean prep(String terminalid, String host, String port, String protocol, String key1, String key2, String serialno)
    {
        //String name = "VAS";
        //String terminalid = "";
        String merchantid = "";
        String mkey = ""; 
        String skey = "";
        String pkey = "";
        String datetime = "";
        String timeout = "";
        String currencycode = "";
        String countrycode = "";
        String callhome = ""; 
        String namelocation = "";
        String mcc = "";
        SecureKeyProvider sp = new SecureKeyProvider();
        //String stan = sp.get(6);
        String stan = String.format("%06d",1);
        
    
        try {
            
            LOG.info( "Preping...");   
            
            //Terminal terminal = terminalJDBCTemplate.getTerminalWithDeviceID(3);//VAS
            
            //terminalid = terminal.getTerminal_id();
            
            LOG.info( "Preping Terminal ID : "+terminalid);
            
            LOG.info( "Host : "+host);
            LOG.info( "Port : "+port);
            LOG.info( "SSl : "+protocol);
           
            LOG.info( "key1:"+key1);
            LOG.info( "key2"+key2);

            //master key doawload
            LOG.info( "master key download");
            String response = NIBSS.masterKeyDownload(host, port, protocol, stan,terminalid, serialno);
            LOG.info( "response : "+response);

            if(response == null){
                return false;
            }

            JsonElement jsonElement = new JsonParser().parse(response);
            JsonObject jobject = jsonElement.getAsJsonObject();
            String f39 = jobject.getAsJsonPrimitive("39").getAsString();
            LOG.info( "f39 : "+f39);

            if(f39 != null && f39.equals("00")){
                String f53 = jobject.getAsJsonPrimitive("53").getAsString();
                String emk = f53.substring(0,32);
                String kcv = f53.substring(32, 38);
                LOG.info( "f53 : "+f53);
                LOG.info( "emk : "+emk);
                LOG.info( "kcv : "+kcv);

                byte[] keyB1 = Crypto.hexToByte(key1 + key1.substring(0, 16));
                byte[] keyB2 = Crypto.hexToByte(key2 + key2.substring(0, 16));
                byte[] keyB3 = new byte[keyB1.length];

                for (int i = 0; i < keyB1.length; i++) {
                    keyB3[i]=(byte)(((byte)(keyB1[i] ^ keyB2[i])));
                }

                SecretKey key = Crypto.read3DESKey(keyB3);
                String dmk = Crypto.Decrypt3DES(key, emk);
                LOG.info( "dmk : "+dmk);

                mkey = dmk;
               

            }
            else{
                return false;
            }

            //session key download
            LOG.info( "session key download");
            response = NIBSS.sessionKeyDownload(host, port, protocol, stan,terminalid, serialno);
            LOG.info( "response : "+response);

            if(response == null){
                return false;
            }

            jsonElement = new JsonParser().parse(response);
            jobject = jsonElement.getAsJsonObject();
            f39 = jobject.getAsJsonPrimitive("39").getAsString();
            LOG.info( "f39 : "+f39);

            if(f39 != null && f39.equals("00")){
                String f53 = jobject.getAsJsonPrimitive("53").getAsString();
                String esk = f53.substring(0,32);
                String kcv = f53.substring(32, 38);
                LOG.info( "f53 : "+f53);
                LOG.info( "esk : "+esk);
                LOG.info( "kcv : "+kcv);

                //String mkey = settings.getString("mkey", "");
                byte[] keyB1 = Crypto.hexToByte(mkey + mkey.substring(0, 16));

                SecretKey key = Crypto.read3DESKey(keyB1);
                String dsk = Crypto.Decrypt3DES(key, esk);
                LOG.info( "dsk : "+dsk);

                skey = dsk;
               

            }
            else{
                return false;
            }

            //pin key downlaod
            LOG.info( "pin key downlaod");
            response = NIBSS.pinKeyDownload(host, port, protocol, stan,terminalid, serialno);
            LOG.info( "response : "+response);

            if(response == null){
                return false;
            }

            jsonElement = new JsonParser().parse(response);
            jobject = jsonElement.getAsJsonObject();
            f39 = jobject.getAsJsonPrimitive("39").getAsString();
            LOG.info( "f39 : "+f39);

            if(f39 != null && f39.equals("00")){
                String f53 = jobject.getAsJsonPrimitive("53").getAsString();
                String epk = f53.substring(0,32);
                String kcv = f53.substring(32, 38);
                LOG.info( "f53 : "+f53);
                LOG.info( "epk : "+epk);
                LOG.info( "kcv : "+kcv);

                //String mkey = settings.getString("mkey", "");
                byte[] keyB1 = Crypto.hexToByte(mkey + mkey.substring(0, 16));

                SecretKey key = Crypto.read3DESKey(keyB1);
                String dpk = Crypto.Decrypt3DES(key, epk);
                LOG.info( "dsk : "+dpk);
                
                pkey = dpk;

              

                //System.out.println("The check value: " + Crypto.Encrypt3DES(key, "0000000000000000"));
            }
            else{
                return false;
            }

            //parameter download
            LOG.info( "parameter download");
            //String skey = settings.getString("skey", "");
            LOG.info( "parameter download:skey:"+skey);
            //byte[] keyB1 = Crypto.hexToByte(mkey + mkey.substring(0, 16));

            response = NIBSS.parametersDownload(host, port, protocol, stan,terminalid, serialno,skey);
            LOG.info( "response : "+response);

            if(response == null){
                return false;
            }

            jsonElement = new JsonParser().parse(response);
            jobject = jsonElement.getAsJsonObject();
            f39 = jobject.getAsJsonPrimitive("39").getAsString();
            LOG.info( "f39 : "+f39);

            if(f39 != null && f39.equals("00")){
                String f62 = jobject.getAsJsonPrimitive("62").getAsString();
                LOG.info( "f62 : "+f62);

                for (;;) {

                    if(f62.isEmpty() || (!f62.startsWith("02") && !f62.startsWith("03") && !f62.startsWith("04") && !f62.startsWith("05") && !f62.startsWith("06") && !f62.startsWith("07") && !f62.startsWith("08") && !f62.startsWith("52"))){
                        break;
                    }

                    if (f62.startsWith("02")) {//CTMS Date and Time : YYYYMMDDhhmmss | 20180714093530
                        int index = f62.indexOf("02");
                        String len = f62.substring(index + 2, index + 2 + 3);
                        //System.out.println("tdlen: " + tdlen);
                        String value = f62.substring(index + 2 + 3, index + 2 + 3 + Integer.parseInt(len));
                        LOG.info( "CTMS Date and Time : " + value);
                        
                        datetime = value;

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                        try {
                            Date date = sdf.parse(value);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            LOG.info( "Set Date and Time : " + calendar.getTime());
                        } catch (Exception ex) {
                        }

                        //remove this from this string
                        f62 = f62.replace("02" + len + value, "");
                    }

                    //System.out.println("f62: " + f62);

                    if (f62.startsWith("03")) {//Card Acceptor Identification Code
                        int index = f62.indexOf("03");
                        String len = f62.substring(index + 2, index + 2 + 3);
                        //System.out.println("tdlen: " + tdlen);
                        String value = f62.substring(index + 2 + 3, index + 2 + 3 + Integer.parseInt(len));
                        LOG.info( "Card Acceptor Identification Code : " + value);

                        merchantid = value;
                        
                        

                        //remove this from this string
                        f62 = f62.replace("03" + len + value, "");
                    }

                    //System.out.println("f62: " + f62);

                    if (f62.startsWith("04")) {//Timeout (maximum time interval to wait for response – in seconds)
                        int index = f62.indexOf("04");
                        String len = f62.substring(index + 2, index + 2 + 3);
                        //System.out.println("tdlen: " + tdlen);
                        String value = f62.substring(index + 2 + 3, index + 2 + 3 + Integer.parseInt(len));
                        LOG.info( "Receive Timeout : " +value);

                        timeout = value;
                        
                        

                        //remove this from this string
                        f62 = f62.replace("04" + len + value, "");
                    }

                    //System.out.println("f62: " + f62);

                    if (f62.startsWith("05")) {//Currency Code
                        int index = f62.indexOf("05");
                        String len = f62.substring(index + 2, index + 2 + 3);
                        //System.out.println("tdlen: " + tdlen);
                        String value = f62.substring(index + 2 + 3, index + 2 + 3 + Integer.parseInt(len));
                        LOG.info( "Currency Code : " + value);
                        
                        currencycode = value;

                       

                        //remove this from this string
                        f62 = f62.replace("05" + len + value, "");
                    }

                    //System.out.println("f62: " + f62);

                    if (f62.startsWith("06")) {//Country Code
                        int index = f62.indexOf("06");
                        String len = f62.substring(index + 2, index + 2 + 3);
                        //System.out.println("tdlen: " + tdlen);
                        String value = f62.substring(index + 2 + 3, index + 2 + 3 + Integer.parseInt(len));
                        LOG.info( "Country Code : " + value);

                        countrycode = value;
                        
                        

                        //remove this from this string
                        f62 = f62.replace("06" + len + value, "");
                    }

                    //System.out.println("f62: " + f62);

                    if (f62.startsWith("07")) {//Call home time (maximum time interval idleness for which a call – home must be done – in hours)
                        int index = f62.indexOf("07");
                        String len = f62.substring(index + 2, index + 2 + 3);
                        //System.out.println("tdlen: " + tdlen);
                        String value = f62.substring(index + 2 + 3, index + 2 + 3 + Integer.parseInt(len));
                        LOG.info( "Callhome Timer : " + value);

                        callhome = value;
                        
                        

                        //remove this from this string
                        f62 = f62.replace("07" + len + value, "");
                    }

                    //System.out.println("f62: " + f62);

                    if (f62.startsWith("52")) {//Merchant Name and Location
                        int index = f62.indexOf("52");
                        String len = f62.substring(index + 2, index + 2 + 3);
                        //System.out.println("tdlen: " + tdlen);
                        String value = f62.substring(index + 2 + 3, index + 2 + 3 + Integer.parseInt(len));
                        LOG.info( "Merchant Name and Location : " + value);

                        namelocation = value;
                        
                        

                        //remove this from this string
                        f62 = f62.replace("52" + len + value, "");
                    }

                    //System.out.println("f62: " + f62);

                    if (f62.startsWith("08")) {//Merchant Category Code
                        int index = f62.indexOf("08");
                        String len = f62.substring(index + 2, index + 2 + 3);
                        //System.out.println("tdlen: " + tdlen);
                        String value = f62.substring(index + 2 + 3, index+ 2 + 3 + Integer.parseInt(len));
                        LOG.info( "Merchant Category Code : " + value);

                        mcc = value;
                        
                       

                        //remove this from this string
                        f62 = f62.replace("08" + len + value, "");
                    }

                    //System.out.println("f62: " + f62);

                }

            }
            else{
                return false;
            }

            //Epms epms = epmsJDBCTemplate.getEpms(terminalid);
            //if (epms == null) {
                epmsJDBCTemplate.deleteEpms(terminalid);
                epmsJDBCTemplate.insertEpms(terminalid, terminalid, merchantid, mkey, skey, pkey, datetime, timeout, currencycode, countrycode, callhome, namelocation, mcc);
            //}
            
            return true;
        } catch (Exception e) {
            LOG.info( e.getMessage());
        }

        return false;
    }*/
    
    public static String prep(String terminalid, String host, String port, String protocol, String key1, String key2, String serialno)
    {
        //String name = "VAS";
        //String terminalid = "";
        String merchantid = "";
        String mkey = ""; 
        String skey = "";
        String pkey = "";
        String epkey = "";
        String datetime = "";
        String timeout = "";
        String currencycode = "";
        String countrycode = "";
        String callhome = ""; 
        String namelocation = "";
        String mcc = "";
        SecureKeyProvider sp = new SecureKeyProvider();
        //String stan = sp.get(6);
        String stan = String.format("%06d",1);
        
    
        try {
            
            LOG.info( "Preping...");   
            
            //Terminal terminal = terminalJDBCTemplate.getTerminalWithDeviceID(3);//VAS
            
            //terminalid = terminal.getTerminal_id();
            
            LOG.info( "Preping Terminal ID : "+terminalid);
            
            LOG.info( "Host : "+host);
            LOG.info( "Port : "+port);
            LOG.info( "SSl : "+protocol);
           
            LOG.info( "key1:"+key1);
            LOG.info( "key2"+key2);

            //master key doawload
            LOG.info( "master key download");
            String response = NIBSS.masterKeyDownload(host, port, protocol, stan,terminalid, serialno);
            LOG.info( "response : "+response);

            if(response == null){
                JSONObject obj = new JSONObject();
                obj.put("response", "05");
                obj.put("description", "Masterkey Download Failed");
                return obj.toJSONString();
            }

            JsonElement jsonElement = new JsonParser().parse(response);
            JsonObject jobject = jsonElement.getAsJsonObject();
            String f39 = jobject.getAsJsonPrimitive("39").getAsString();
            LOG.info( "f39 : "+f39);

            if(f39 != null && f39.equals("00")){
                String f53 = jobject.getAsJsonPrimitive("53").getAsString();
                String emk = f53.substring(0,32);
                String kcv = f53.substring(32, 38);
                LOG.info( "f53 : "+f53);
                LOG.info( "emk : "+emk);
                LOG.info( "kcv : "+kcv);

                byte[] keyB1 = Crypto.hexToByte(key1 + key1.substring(0, 16));
                byte[] keyB2 = Crypto.hexToByte(key2 + key2.substring(0, 16));
                byte[] keyB3 = new byte[keyB1.length];

                for (int i = 0; i < keyB1.length; i++) {
                    keyB3[i]=(byte)(((byte)(keyB1[i] ^ keyB2[i])));
                }

                SecretKey key = Crypto.read3DESKey(keyB3);
                String dmk = Crypto.Decrypt3DES(key, emk);
                LOG.info( "dmk : "+dmk);

                mkey = dmk;
                
                

            }
            else{
                JSONObject obj = new JSONObject();
                obj.put("response", f39);
                obj.put("description", ResponseCodes.responseCodeErrorMessage(Integer.parseInt(f39)));
                return obj.toJSONString();
            }

            //session key download
            LOG.info( "session key download");
            response = NIBSS.sessionKeyDownload(host, port, protocol, stan,terminalid, serialno);
            LOG.info( "response : "+response);

            if(response == null){
                JSONObject obj = new JSONObject();
                obj.put("response", "05");
                obj.put("description", "Sessionkey Download Failed");
                return obj.toJSONString();
            }

            jsonElement = new JsonParser().parse(response);
            jobject = jsonElement.getAsJsonObject();
            f39 = jobject.getAsJsonPrimitive("39").getAsString();
            LOG.info( "f39 : "+f39);

            if(f39 != null && f39.equals("00")){
                String f53 = jobject.getAsJsonPrimitive("53").getAsString();
                String esk = f53.substring(0,32);
                String kcv = f53.substring(32, 38);
                LOG.info( "f53 : "+f53);
                LOG.info( "esk : "+esk);
                LOG.info( "kcv : "+kcv);

                //String mkey = settings.getString("mkey", "");
                byte[] keyB1 = Crypto.hexToByte(mkey + mkey.substring(0, 16));

                SecretKey key = Crypto.read3DESKey(keyB1);
                String dsk = Crypto.Decrypt3DES(key, esk);
                LOG.info( "dsk : "+dsk);

                skey = dsk;
                
                

            }
            else{
                JSONObject obj = new JSONObject();
                obj.put("response", f39);
                obj.put("description", ResponseCodes.responseCodeErrorMessage(Integer.parseInt(f39)));
                return obj.toJSONString();
            }

            //pin key downlaod
            LOG.info( "pin key downlaod");
            response = NIBSS.pinKeyDownload(host, port, protocol, stan,terminalid, serialno);
            LOG.info( "response : "+response);

            if(response == null){
                JSONObject obj = new JSONObject();
                obj.put("response", "05");
                obj.put("description", "PINkey Download Failed");
                return obj.toJSONString();
            }

            jsonElement = new JsonParser().parse(response);
            jobject = jsonElement.getAsJsonObject();
            f39 = jobject.getAsJsonPrimitive("39").getAsString();
            LOG.info( "f39 : "+f39);

            if(f39 != null && f39.equals("00")){
                String f53 = jobject.getAsJsonPrimitive("53").getAsString();
                String epk = f53.substring(0,32);
                String kcv = f53.substring(32, 38);
                LOG.info( "f53 : "+f53);
                LOG.info( "epk : "+epk);
                LOG.info( "kcv : "+kcv);
                
                epkey = epk;

                //String mkey = settings.getString("mkey", "");
                byte[] keyB1 = Crypto.hexToByte(mkey + mkey.substring(0, 16));

                SecretKey key = Crypto.read3DESKey(keyB1);
                String dpk = Crypto.Decrypt3DES(key, epk);
                LOG.info( "dsk : "+dpk);
                
                pkey = dpk;

               

                //System.out.println("The check value: " + Crypto.Encrypt3DES(key, "0000000000000000"));
            }
            else{
                JSONObject obj = new JSONObject();
                obj.put("response", f39);
                obj.put("description", ResponseCodes.responseCodeErrorMessage(Integer.parseInt(f39)));
                return obj.toJSONString();
            }

            //parameter download
            LOG.info( "parameter download");
            //String skey = settings.getString("skey", "");
            LOG.info( "parameter download:skey:"+skey);
            //byte[] keyB1 = Crypto.hexToByte(mkey + mkey.substring(0, 16));

            response = NIBSS.parametersDownload(host, port, protocol, stan,terminalid, serialno,skey);
            LOG.info( "response : "+response);

            if(response == null){
                JSONObject obj = new JSONObject();
                obj.put("response", "05");
                obj.put("description", "Parameter Download Failed");
                return obj.toJSONString();
            }

            jsonElement = new JsonParser().parse(response);
            jobject = jsonElement.getAsJsonObject();
            f39 = jobject.getAsJsonPrimitive("39").getAsString();
            LOG.info( "f39 : "+f39);

            if(f39 != null && f39.equals("00")){
                String f62 = jobject.getAsJsonPrimitive("62").getAsString();
                LOG.info( "f62 : "+f62);

                for (;;) {

                    if(f62.isEmpty() || (!f62.startsWith("02") && !f62.startsWith("03") && !f62.startsWith("04") && !f62.startsWith("05") && !f62.startsWith("06") && !f62.startsWith("07") && !f62.startsWith("08") && !f62.startsWith("52"))){
                        break;
                    }

                    if (f62.startsWith("02")) {//CTMS Date and Time : YYYYMMDDhhmmss | 20180714093530
                        int index = f62.indexOf("02");
                        String len = f62.substring(index + 2, index + 2 + 3);
                        //System.out.println("tdlen: " + tdlen);
                        String value = f62.substring(index + 2 + 3, index + 2 + 3 + Integer.parseInt(len));
                        LOG.info( "CTMS Date and Time : " + value);
                        
                        datetime = value;

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                        try {
                            Date date = sdf.parse(value);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            LOG.info( "Set Date and Time : " + calendar.getTime());
                        } catch (Exception ex) {
                        }

                        //remove this from this string
                        f62 = f62.replace("02" + len + value, "");
                    }

                    //System.out.println("f62: " + f62);

                    if (f62.startsWith("03")) {//Card Acceptor Identification Code
                        int index = f62.indexOf("03");
                        String len = f62.substring(index + 2, index + 2 + 3);
                        //System.out.println("tdlen: " + tdlen);
                        String value = f62.substring(index + 2 + 3, index + 2 + 3 + Integer.parseInt(len));
                        LOG.info( "Card Acceptor Identification Code : " + value);

                        merchantid = value;
                        
                        

                        //remove this from this string
                        f62 = f62.replace("03" + len + value, "");
                    }

                    //System.out.println("f62: " + f62);

                    if (f62.startsWith("04")) {//Timeout (maximum time interval to wait for response – in seconds)
                        int index = f62.indexOf("04");
                        String len = f62.substring(index + 2, index + 2 + 3);
                        //System.out.println("tdlen: " + tdlen);
                        String value = f62.substring(index + 2 + 3, index + 2 + 3 + Integer.parseInt(len));
                        LOG.info( "Receive Timeout : " +value);

                        timeout = value;
                        
                        

                        //remove this from this string
                        f62 = f62.replace("04" + len + value, "");
                    }

                    //System.out.println("f62: " + f62);

                    if (f62.startsWith("05")) {//Currency Code
                        int index = f62.indexOf("05");
                        String len = f62.substring(index + 2, index + 2 + 3);
                        //System.out.println("tdlen: " + tdlen);
                        String value = f62.substring(index + 2 + 3, index + 2 + 3 + Integer.parseInt(len));
                        LOG.info( "Currency Code : " + value);
                        
                        currencycode = value;

                        

                        //remove this from this string
                        f62 = f62.replace("05" + len + value, "");
                    }

                    //System.out.println("f62: " + f62);

                    if (f62.startsWith("06")) {//Country Code
                        int index = f62.indexOf("06");
                        String len = f62.substring(index + 2, index + 2 + 3);
                        //System.out.println("tdlen: " + tdlen);
                        String value = f62.substring(index + 2 + 3, index + 2 + 3 + Integer.parseInt(len));
                        LOG.info( "Country Code : " + value);

                        countrycode = value;
                        
                        

                        //remove this from this string
                        f62 = f62.replace("06" + len + value, "");
                    }

                    //System.out.println("f62: " + f62);

                    if (f62.startsWith("07")) {//Call home time (maximum time interval idleness for which a call – home must be done – in hours)
                        int index = f62.indexOf("07");
                        String len = f62.substring(index + 2, index + 2 + 3);
                        //System.out.println("tdlen: " + tdlen);
                        String value = f62.substring(index + 2 + 3, index + 2 + 3 + Integer.parseInt(len));
                        LOG.info( "Callhome Timer : " + value);

                        callhome = value;
                        
                        

                        //remove this from this string
                        f62 = f62.replace("07" + len + value, "");
                    }

                    //System.out.println("f62: " + f62);

                    if (f62.startsWith("52")) {//Merchant Name and Location
                        int index = f62.indexOf("52");
                        String len = f62.substring(index + 2, index + 2 + 3);
                        //System.out.println("tdlen: " + tdlen);
                        String value = f62.substring(index + 2 + 3, index + 2 + 3 + Integer.parseInt(len));
                        LOG.info( "Merchant Name and Location : " + value);

                        namelocation = value;
                        
                        

                        //remove this from this string
                        f62 = f62.replace("52" + len + value, "");
                    }

                    //System.out.println("f62: " + f62);

                    if (f62.startsWith("08")) {//Merchant Category Code
                        int index = f62.indexOf("08");
                        String len = f62.substring(index + 2, index + 2 + 3);
                        //System.out.println("tdlen: " + tdlen);
                        String value = f62.substring(index + 2 + 3, index+ 2 + 3 + Integer.parseInt(len));
                        LOG.info( "Merchant Category Code : " + value);

                        mcc = value;
                        

                        //remove this from this string
                        f62 = f62.replace("08" + len + value, "");
                    }

                    //System.out.println("f62: " + f62);

                }

            }
            else{
                JSONObject obj = new JSONObject();
                obj.put("response", f39);
                obj.put("description", ResponseCodes.responseCodeErrorMessage(Integer.parseInt(f39)));
                return obj.toJSONString();
            }
            
            //Epms epms = epmsJDBCTemplate.getEpms(terminalid);
            //if (epms == null) {
                epmsJDBCTemplate.deleteEpms(terminalid);
                epmsJDBCTemplate.insertEpms(terminalid, terminalid, merchantid, mkey, skey, pkey, datetime, timeout, currencycode, countrycode, callhome, namelocation, mcc);
            //}

            JSONObject obj = new JSONObject();
            obj.put("response", "00");
            obj.put("description", "Prep Successful");
            obj.put("merchantid", merchantid);
            obj.put("terminalid", terminalid);
            obj.put("country_code", countrycode);
            obj.put("currency_code", currencycode);
            obj.put("merchant_category_code", mcc);
            obj.put("merchant_address", namelocation);
            obj.put("datetime", datetime);
            obj.put("masterkey", mkey);
            obj.put("pin_key", pkey);
            obj.put("epin_key", epkey);
            obj.put("callhome", callhome);
            obj.put("timeout", timeout);
            //obj.put("session_key", skey);
            return obj.toJSONString();
            
            
            //return true;
        } catch (Exception e) {
            LOG.info( e.getMessage());
        }

        JSONObject obj = new JSONObject();
        obj.put("response", "05");
        obj.put("description", "Prep Failed");
        return obj.toJSONString();
    }

    public static byte[] hexStringToBytes(String s) {

        int iLength = s.length();
        int iBuff = iLength / 2;

        byte[] buff = new byte[iBuff];

        int j = 0;
        for (int i = 0; i < iLength; i += 2) {

            try {

                String s1 = s.substring(i, i + 2);
                buff[j++] = (byte) Integer.parseInt(s1, 16);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return buff;

    }

    public static String hex(byte[] data) {

        StringBuilder sb = new StringBuilder();

        for (byte b : data) {

            sb.append(Character.forDigit((b & 240) >> 4, 16));
            sb.append(Character.forDigit((b & 15), 16));
        }

        return sb.toString().toUpperCase();
    }
   

}
