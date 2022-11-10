/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.byteworks.utils;

import java.util.Calendar;

/**
 *
 * @author SoloFoundation
 */
public class Constants {
    
    //public final static String VERSION = new Version().getVersion();//"v3.0.0";
    
    Calendar now = Calendar.getInstance();   // Gets the current date and time
    int year = now.get(Calendar.YEAR); 
    
    //public final static String LOGINPAGEFOOTERTEXT = "Pay Intersystem Limited © "+year+" All Rights Reserved. Privacy and Terms. "+VERSION;
    //public final static String HOMEPAGEFOOTERTEXT = "Terminal Management System "+VERSION+" - © "+year+" All Right Reserved by <a href=\"http://xpresspayments.com\">Xpress Payments</a>";
    //public final static String HOMEPAGEFOOTERTEXT = "Catamaran. "+VERSION+" - © "+year+" All Right Reserved by <a href=\"https://www.payintersystemslimited.com/\">Pay Intersystem Limited</a>";
    
    
    //public final static String LOGINPAGEFOOTERTEXT = "Xpress Payment Solutions Limited © "+year+" All Rights Reserved. Privacy and Terms. "+VERSION;
    //public final static String HOMEPAGEFOOTERTEXT = "Catamaran. "+VERSION+" - © "+year+" All Right Reserved by <a href=\"http://xpresspayments.com\">Xpress Payment Solutions Limited</a>";
    
    //public final static String LOGINPAGEFOOTERTEXT = "Guaranty Trust Bank Liberia © "+year+" All Rights Reserved. Privacy and Terms. "+VERSION;
    //public final static String HOMEPAGEFOOTERTEXT = "Terminal Management System. "+VERSION+" - © "+year+" All Right Reserved by <a href=\"http://www.gtbanklr.com\">Guaranty Trust Bank Liberia</a>";
    
    //public final static String LOGINPAGEFOOTERTEXT = "Byteworks Embedded System Ltd © "+year+" All Rights Reserved. Privacy and Terms. "+VERSION;
    //public final static String HOMEPAGEFOOTERTEXT = "Terminal Management System. "+VERSION+" - © "+year+" All Right Reserved by <a href=\"http://www.byteworksgroup.com\">Byteworks Embedded System Ltd</a>";
    
    //public final static String LOGINPAGEFOOTERTEXT = "MTECH Communications Ltd © "+year+" All Rights Reserved. Privacy and Terms. "+VERSION;
    //public final static String HOMEPAGEFOOTERTEXT = "ECR. "+VERSION+" - © "+year+" All Right Reserved by <a href=\"http://www.byteworksgroup.com\">Byteworks Embedded System Ltd</a>";
    
    public static final String XPRESS_PAY_HTTP_URL = "http://172.21.7.5:9000/EwalletFund/pos.asmx";
    //public static final String XPRESSPAY_URL = "http://172.21.7.75:8080/TMS/bridge";
    
    //public static final String XPRESS_HOST_IPADDRESS = "197.253.21.235";
    //public static final String XPRESS_HOST_PORT = "8080";
    
    //live parameter
    /*public static final String HOST = "196.6.103.73";
    public static final String PORT = "5043";
    public static final String KEY1 = "BF862532E057460B0823F1FD944C08D5";
    public static final String KEY2 = "0D3B01DAD0A26E58EAC4EF8662ADD6F2";
    public static final String PROTOCOL = "1";*/
    
    //test parameter
    public static final String TEST_HOST = "196.6.103.72";
    public static final String TEST_PORT = "5042";
    public static final String TEST_KEY1 = "5D25072F04832A2329D93E4F91BA23A2";
    public static final String TEST_KEY2 = "86CBCDE3B0A22354853E04521686863D";
    public static final String TEST_PROTOCOL = "0";
    
    public static final String LIVE_HOST = "196.6.103.73";
    public static final String LIVE_PORT = "5043";
    public static final String LIVE_KEY1 = "BF862532E057460B0823F1FD944C08D5";
    public static final String LIVE_KEY2 = "0D3B01DAD0A26E58EAC4EF8662ADD6F2";
    public static final String LIVE_PROTOCOL = "1";
    
    public static final String POSVAS_LIVE_HOST = "196.6.103.18";
    public static final String POSVAS_LIVE_PORT = "5012";
    public static final String POSVAS_LIVE_KEY1 = "3D4A38FE319D252C0B83839173B094E3";
    public static final String POSVAS_LIVE_KEY2 = "AE1026402F1934075BA261E916FBADA8";
    public static final String POSVAS_LIVE_PROTOCOL = "1";
    
    public static final String POSVAS_TEST_HOST = "196.6.103.10";
    public static final String POSVAS_TEST_PORT = "55533";
    public static final String POSVAS_TEST_KEY1 = "5D25072F04832A2329D93E4F91BA23A2";
    public static final String POSVAS_TEST_KEY2 = "86CBCDE3B0A22354853E04521686863D";
    public static final String POSVAS_TEST_PROTOCOL = "1";
    
    public static final int DB_MAXROW = 100;
    public static final int DB_FETCH_SIZE = 1000;
            
    public static final char HSM_OK = '0';
    public static final char HSM_ERROR = '1';
    
    public static final int TMS = 1;
    public static final int POS = 2;
    
    public static final int ALL = 2;
    public static final int APPROVED = 1;
    public static final int DECLINED = 0;
    
    public static final int TRAN_TYPE_EWALLET = 300;
    public static final int TRAN_TYPE_EWALLETPULLCREDITEOFFLINE = 301;
    public static final int TRAN_TYPE_EWALLETBALANCE = 302;
    public static final int TRAN_TYPE_EWALLETPUSHCREDITONLINE = 303;
    public static final int TRAN_TYPE_EWALLETPUSHALLTRANSACTIONSLINE = 304;
    public static final int TRAN_TYPE_EWALLETOFFLINE = 305;
    public static final int TRAN_TYPE_GETREVENUEDETAIL = 306;

    public static final int TRAN_TYPE_PURCHASE = 1;
    public static final int TRAN_TYPE_PURCASH = 2;
    public static final int TRAN_TYPE_CASHBACK = 3;
    public static final int TRAN_TYPE_REVERSAL = 4;
    public static final int TRAN_TYPE_REFUND = 5;
    public static final int TRAN_TYPE_AUTH = 6;
    public static final int TRAN_TYPE_BALANCE = 7;
    public static final int TRAN_TYPE_PINCHANGE = 8;
    public static final int TRAN_TYPE_MINISTATE = 9;
    public static final int TRAN_TYPE_TRANSFER = 10;
    public static final int TRAN_TYPE_DEPOSIT = 11;
    public static final int TRAN_TYPE_ROLLBACK = 12;

    public static final int TRAN_TYPE_EPIN = 13;
    //public static final int TRAN_TYPE_VALUEADD_VOUCHER=	13;
    public static final int TRAN_TYPE_PAYMENT = 14;
    //public static final int TRAN_TYPE_VALUEADD_RECHARGE=	15;
    public static final int TRAN_TYPE_AUTH_COMPLETION = 15;
    public static final int TRAN_TYPE_CLOSE_BATCH = 16;

    public static final int TRAN_TYPE_LINKDEPOSIT = 23;
    public static final int TRAN_TYPE_LINKACCOUNT = 22;
    public static final int TRAN_TYPE_SERVICEPAYMENT = 25;
    public static final int TRAN_TYPE_AGENTDEPOSIT = 30;
    public static final int TRAN_TYPE_AGENT_LINKDEPOSIT = 31;

    public static final int TRAN_TYPE_AGENTREVERSAL = 36;
    public static final int TRAN_TYPE_TAMSACCT_TRANSFER = 37;
    public static final int TRAN_TYPE_CUSTOMERREVERSAL = 90;

    public static final int TXN_PENDING = 0;
    public static final int TXN_APPROVED = 1;
    public static final int TXN_DECLINED = 2;
    public static final int TXN_RECONSILED = 3;
    public static final int TXN_ROLLBACK = 4;
    
    //Trantypes
    //public static final int TRAN_TYPE_PURCHASE = 1;
    public static final int TRAN_TYPE_PURCHASECASH = 2;
    //public static final int TRAN_TYPE_CASHBACK = 3;
    //public static final int TRAN_TYPE_REVERSAL = 4;
    //public static final int TRAN_TYPE_REFUND = 5;
    public static final int TRAN_TYPE_AUTHONLY = 6;
    //public static final int TRAN_TYPE_BALANCE = 7;
    public static final int TRAN_TYPE_CHANGEPIN = 8;
    public static final int TRAN_TYPE_MINISTAT = 9;
    public static final int TRAN_TYPE_TRANSFERS = 10;
    public static final int TRAN_TYPE_DEPOSITS = 11;
    //public static final int TRAN_TYPE_ROLLBACK = 12;
    //11 Roleback
    public static final int TRAN_TYPE_VALUEADDVOUCHER = 13;
    //14 Pan Check
    public static final int TRAN_TYPE_VALUEADDRECHARGE = 15;
    public static final int TRAN_TYPE_FUELFLEET = 16;
    public static final int TRAN_TYPE_FUEL = 17;

    public static final int TRAN_TYPE_CHURCH = 18;
    public static final int TRAN_TYPE_BREWERY	= 19;
    public static final int TRAN_TYPE_VOUCHERPURCHASE	= 20;
    public static final int TRAN_TYPE_TICKETPURCHASE = 21;

    public static final int TRAN_TYPE_EMBASSY = 26;
    public static final int TRAN_TYPE_PREAUTH = 27;

    public static final int TRAN_TYPE_SURVEY = 28;

    public static final int TRAN_TYPE_PHCN = 29;

    public static final int TRAN_TYPE_PREAUTH_PURCHASE = 33;
    public static final int TRAN_TYPE_PREAUTH_LIFECYCLE = 34;
    public static final int TRAN_TYPE_PREAUTH_ADJUSTMENT = 35;

    public static final int TRAN_TYPE_BILL_PAYMENT = 39;

    public static final int TRAN_TYPE_ONLINE_VOUCHER = 40;

    public static final int TRAN_TYPE_MBL = 41;

    public static final int TRAN_TYPE_FMC = 43;

    public static final int TRAN_TYPE_CASHADVANCE = 45;

    public static final int TRAN_TYPE_WITHDRAWAL = 46;

    public static final int TRAN_TYPE_PIN_SELECTION = 47;

    public static final int TRAN_TYPE_SELECT = 99;

    public static final int TRAN_TYPE_AUTO_REVERSAL = 100;

    public static final int TRAN_TYPE_PREPAID = 101;

    public static final int TRAN_TYPE_LINK_ACCOUNT_ENQUIRY = 102;

    public static final int TRAN_TYPE_REVERSAL2 = 103;
    
    public static final int ADMIN_TERMINALID = 99;
    public static final int ADMIN_NIBSSCONFIG = 100;
    public static final int ADMIN_TMSCONFIG = 101;
    public static final int ADMIN_GPRS = 102;
    public static final int ADMIN_ETHERNET = 103;
    public static final int ADMIN_WIFI = 104;
    public static final int ADMINPREPTERMINAL = 105;
    public static final int ADMIN_PARAMETERDOWNLOAD = 106;
    public static final int ADMIN_REMOTEDOWNLOAD = 107;
    public static final int ADMIN_CALLHOMETIMER = 108;
    public static final int ADMIN_PULLOFFLINEBALANCE = 109;
    public static final int ADMIN_PUSHOFFLINEBALANCE = 110;
    public static final int ADMIN_PUSHTRANSACTIONOFFLINE = 111;
    public static final int ADMIN_DOWNLOADMENU = 112;

    public final static int STATUS_OK = 0;
    public final static int STATUS_INVALIDTERMINAL = 300;
    public final static int STATUS_INVALIDBATCH = 301;
    public final static int STATUS_TRANNOTFOUND = 302;
    public final static int STATUS_TXNEXIST = 303;
    public final static int STATUS_NOSWITCH = 304;
    public final static int STATUS_INVALIDMERCHANT = 305;
    public final static int STATUS_INVALIDAUTHCODE = 306;
    public final static int STATUS_SYSUNAVAIL = 307;
    public final static int STATUS_VALIDATION = 308;
    public final static int STATUS_INVALIDAMOUNT = 309;
    public final static int STATUS_INVALIDCARD = 310;
    public final static int STATUS_PICKUP = 311;
    public final static int STATUS_WRONGCARDTYPE = 312;
    public final static int STATUS_INVALIDACCOUNT = 313;
    public final static int STATUS_INSUFFIENCTFUNDS = 314;
    public final static int STATUS_EXPIRED = 315;
    public final static int STATUS_EXCEEDSLIMIT = 316;
    public final static int STATUS_PINFAULT = 317;
    public final static int STATUS_TXNMISMATCH = 318;
    public final static int STATUS_HOTCARD = 319;
    public final static int STATUS_MANUALNOTALLOWED = 320;
    public final static int STATUS_REFUNDNOTALLOWED = 321;
    public final static int STATUS_REVERSALNOTALLOWED = 322;
    public final static int STATUS_BUDGETNOTALLOWED = 323;
    public final static int STATUS_CASHBACKNOTALLOWED = 324;
    public final static int STATUS_AUTHOVERRIDENOTALLOWED = 325;
    public final static int STATUS_AUTHONLYNOTALLOWED = 326;
    public final static int STATUS_PINCHANGE_FAILED = 327;
    public final static int STATUS_ALREADY_REVERSED = 328;
    public final static int STATUS_NO_OPEN_BATCH = 329;
    public final static int STATUS_CURRENCY_ERROR = 330;
    public final static int STATUS_HSM_ERROR = 331;
    public final static int STATUS_DUKPT_SERIAL_ERROR = 333;
    //334,335,336,337,338,339,340
    public final static int STATUS_NO_ROUTE = 341;
    public final static int STATUS_UNKNOWN = 399;
    public final static int STATUS_SYSTEM_MALFUNCTION = 400;
    
    public static final int ACC_SAVINGS = 1;
    public static final int ACC_CHEQUE = 2;
    public static final int ACC_CREDIT = 3;
    public static final int ACC_DEFAULT = 4;
    
}
