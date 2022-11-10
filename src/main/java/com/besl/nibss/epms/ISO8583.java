package com.besl.nibss.epms;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
//import java.util.logging.Level;
//import java.util.logging.Logger;
import org.apache.log4j.Logger;
import org.apache.commons.io.FileUtils;

public class ISO8583 {

    private static final Logger LOG = Logger.getLogger(ISO8583.class);
    //private static String path = "";
    // Used to load the 'native-lib' library on application startup.
    static {
        //System.out.println(System.getProperty("java.library.path"));
        //System.load("/usr/lib/java/libepms.so"); 
        //System.load("/opt/libepms.so");
        try {
            File libFile = new File("/opt/libepms.so");
            if (!libFile.exists()) {
                try {
                    InputStream in = ISO8583.class.getResourceAsStream("/libepms.so");
                    FileUtils.copyInputStreamToFile(in, libFile);
                } catch (IOException ex) {
                    LOG.info("Exception:"+ex.getMessage());
                }
            }
            else{
                LOG.info("/opt/libepms.so exist");
            }
            
            //URL url = ISO8583.class.getResource("/libepms.so");
            System.load(libFile.getAbsolutePath());
        } catch (Exception ex) {
            LOG.info("Exception:"+ex.getMessage());
        }
        
        //System.load("/Users/Shared/epms/libepms.lib");//for testing
    }

    public ISO8583(){
        
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String getVersion();
    public native String packEPMSISO8583Message(String iso8583JSONFields, String hashKey);
    public native String unpackEPMSISO8583Message(String iso8583HexData, String hashKey);

    public static void main(String[] args) {

        ISO8583 iso8583 = new ISO8583();
        String dt = iso8583.getVersion();
        System.out.println("Version=" + dt);

        //String data = iso8583.packEPMSISO8583Message("{ \"0\":\"0800\", \"3\":\"000000\", \"4\":\"000002345634\", \"25\":\"00\" }", "");
        /*String data = iso8583.packEPMSISO8583Message("{\"0\":\"0200\",\"2\":\"5399832692106192\",\"3\":\"001000\",\"4\":\"000000002154\",\"7\":\"0610074244\",\"11\":\"147207\",\"12\":\"074244\",\"13\":\"0610\",\"14\":\"2009\",\"18\":\"5999\",\"22\":\"901\",\"23\":\"001\",\"25\":\"00\",\"26\":\"12\",\"28\":\"D00000000\",\"32\":\"539983\",\"35\":\"5399832692106192D20092210015347214\",\"37\":\"777045147207\",\"40\":\"221\",\"41\":\"20390015\",\"42\":\"2044LA000017579\",\"43\":\"Office                 LA           LANG\",\"49\":\"566\",\"55\":\"9F3303E0F1C8950504000080009F370432F929539F10180110A50003020000000000000000000000FF9F26082C5CD49A2104A62D9F2701809F360202FB820239009C01009F1A0205669F1A0205669A031806109F02060000000021549F03060000000000005F2A0205669F0607A000000004101050164465626974204D6173746572436172649B02E800\",\"123\":\"510101511344001\"}", "74D60EB4787340A99FC287F6EEBA1768");
        System.out.println("packed=" + data);
        if (data != null) {
            String datax = iso8583.unpackEPMSISO8583Message(data, "74D60EB4787340A99FC287F6EEBA1768");
            System.out.println("unpacked=" + datax);
        }*/
        
        
    }

}
