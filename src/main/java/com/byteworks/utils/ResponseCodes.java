/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.byteworks.utils;

/**
 *
 * @author SoloFoundation
 */
public class ResponseCodes {
    
    public static String responseCodeErrorMessage(int responseCode){

        String message = "";

        switch(responseCode){

            case 0:
                message = "Approved";
                break;
            case 1:
                message = "Refer to card issuer";
                break;

            case 2:
                message = "Refer to card issuer, special condition 03 Invalid merchant";
                break;

            case 3:
                message = "Invalid merchant";
                break;

            case 4:
                message = "Pick-up card";
                break;

            case 5:
                message = "Do not honor";
                break;

            case 6:
                message = "Error";
                break;

            case 7:
                message = "Pick-up card, special condition";
                break;

            case 8:
                message = "Honor with identification";
                break;

            case 9:
                message = "Request in progress";
                break;

            case 10:
                message = "Approved, partial";
                break;

            case 11:
                message = "Approved, VIP";
                break;

            case 12:
                message = "Invalid transaction";
                break;

            case 13:
                message = "Invalid amount";
                break;

            case 14:
                message = "Invalid card number";
                break;

            case 15:
                message = "No such issuer";
                break;

            case 16:
                message = "Approved, update track 3";
                break;

            case 17:
                message = "Customer cancellation";
                break;

            case 18:
                message =  "Customer dispute";
                break;

            case 19:
                message = "Re-enter transaction";
                break;

            case 20:
                message = "Invalid response";
                break;

            case 21:
                message = "No action taken";
                break;

            case 22:
                message = "Suspected malfunction";
                break;

            case 23:
                message = "Unacceptable transaction fee";
                break;

            case 24:
                message = "File update not supported";
                break;

            case 25:
                message = "Unable to locate record";
                break;

            case 26:
                message = "Duplicate record";
                break;

            case 27:
                message = "File update edit error";
                break;

            case 28:
                message = "File update file locked";
                break;

            case 29:
                message = "File update failed";
                break;

            case 30:
                message = "Format error";
                break;

            case 31:
                message = "Bank not supported";
                break;

            case 32:
                message = "Completed partially";
                break;

            case 33:
                message = "Expired card, pick-up";
                break;

            case 34:
                message = "Suspected fraud, pick-up";
                break;

            case 35:
                message = "Contact acquirer, pick-up";
                break;

            case 36:
                message = "Restricted card, pick-up";
                break;

            case 37:
                message = "Call acquirer security, pick-up";
                break;

            case 38:
                message = "PIN tries exceeded, pick-up";
                break;

            case 39:
                message = "No credit account";
                break;

            case 40:
                message = "Function not supported";
                break;

            case 41:
                message = "Lost card";
                break;

            case 42:
                message = "No universal account";
                break;

            case 43:
                message = "Stolen card";
                break;

            case 44:
                message = "No investment account";
                break;

            case 51:
                message = "Insufficient funds";
                break;

            case 52:
                message = "No check account";
                break;

            case 53:
                message = "No savings account";
                break;

            case 54:
                message = "Expired card";
                break;

            case 55:
                message = "Incorrect PIN";
                break;

            case 56:
                message = "No card record";
                break;

            case 57:
                message = "Transaction not permitted to cardholder";
                break;

            case 58:
                message = "Transaction not permitted on terminal";
                break;

            case 59:
                message = "Suspected fraud";
                break;

            case 60:
                message = "Contact acquirer";
                break;

            case 61:
                message = "Exceeds withdrawal limit";
                break;

            case 62:
                message = "Restricted card";
                break;

            case 63:
                message = "Security violation";
                break;

            case 64:
                message = "Original amount incorrect";
                break;

            case 65:
                message = "Exceeds withdrawal frequency";
                break;

            case 66:
                message = "Call acquirer security";
                break;

            case 67:
                message = "Hard capture";
                break;

            case 68:
                message = "Response received too late";
                break;

            case 75:
                message = "PIN tries exceeded";
                break;

            case 77:
                message = "Intervene, bank approval required";
                break;

            case 78:
                message = "Intervene, bank approval required for partial amount";
                break;

            case 79:
                message = "Cut-off in progress";
                break;

            case 90:
                message = "Cut-off in progress";
                break;

            case 91:
                message = "Issuer or switch inoperative";
                break;

            case 92:
                message = "Routing error";
                break;

            case 93:
                message = "Violation of law";
                break;

            case 94:
                message = "Duplicate transaction";
                break;

            case 95:
                message = "Reconcile error";
                break;

            case 96:
                message = "System malfunction";
                break;

            case 97:
                message = "Exceeds cash limit";
                break;

            case 99:
                message =  "No Response From Acquirer";
                break;

        }

        return message;

    }
    
}
