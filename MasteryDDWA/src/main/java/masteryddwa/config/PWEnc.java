/*
Created by: Margaret Donin
Date created: 10/13/20
Date revised:
*/

package masteryddwa.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PWEnc {
    public static void main(String[] args) {
        String clearTxtPw = "password";
        // BCrypt
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPw = encoder.encode(clearTxtPw);
        System.out.println(hashedPw);
    }
}
