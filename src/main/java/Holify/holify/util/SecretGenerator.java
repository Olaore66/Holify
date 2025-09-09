//package Holify.holify.util;
//
//import java.security.SecureRandom;
//import java.util.Base64;
//
//public class SecretGenerator {
//    public static void main(String[] args) {
//        SecureRandom secureRandom = new SecureRandom();
//        byte[] key = new byte[32]; // 256-bit key
//        secureRandom.nextBytes(key);
//        String secret = Base64.getEncoder().encodeToString(key);
//        System.out.println("JWT Secret: " + secret);
//    }
//}
