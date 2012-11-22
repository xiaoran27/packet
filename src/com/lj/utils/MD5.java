package com.lj.utils;
import java.security.MessageDigest;
public class MD5 {


        public final static String setMD5(String s){
          char hexDigits[] = {
              '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
              'e', 'f'};
          try {
            byte[] strTemp = s.getBytes();
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
              byte byte0 = md[i];
              str[k++] = hexDigits[byte0 >>> 4 & 0xf];
              str[k++] = hexDigits[byte0 & 0xf];
              }
              return new String(str);
            }
            catch (Exception e){
              return null;
            }
     }
      public static void main(String[] args){

          System.out.println(MD5.setMD5("Lj!123456"));  //905a621706168f928449e1a67f11f93c
          System.out.println(MD5.setMD5("123456"));  //e10adc3949ba59abbe56e057f20f883e
          System.out.println(MD5.setMD5("Lj#123456"));  //153e2f86c85ee6c310fba3cf58f0a99e
      }
      
}
