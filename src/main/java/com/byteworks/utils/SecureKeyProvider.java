/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.byteworks.utils;
//import com.google.inject.Provider;
import java.security.SecureRandom;
import org.apache.commons.codec.binary.Base64;
/**
 *
 * @author SoloFoundation
 */
public final class SecureKeyProvider {

    private final SecureRandom rng;

    public SecureKeyProvider() {
        this.rng = new SecureRandom();
    }

    public String get(int len) {

        /* SecureRandom documentation does not state if it's thread-safe,
         * therefore we do our own synchronization. see
         *
         * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6498354
         */

        synchronized (this.rng) {
            char[] chars = {'1','2','3','4','5','6','7','8','9','0'};
            
            final byte[] random = new byte[len];
            rng.nextBytes(random);
            char unique[] = new char[len];
            for (int b = 0; b < len; b++) {
                //result.Append(chars[__b % (chars.Length - )>); 
                int index = (int) random[b] % (int) (chars.length - 1);
                if(index < 0){
                    index = index * -1;
                }
                unique[b] = chars[index];
            }
            
            return new String(unique);
        }
    }

}
