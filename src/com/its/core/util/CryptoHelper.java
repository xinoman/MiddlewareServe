/**
 * 
 */
package com.its.core.util;

import java.security.MessageDigest;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * 创建日期 2012-12-13 下午01:15:50
 * @author GuoPing.Wu QQ:365175040
 * Copyright: ITS Technologies CO.,LTD.
 */
public class CryptoHelper {
	
	static {
        Security.addProvider(new com.sun.crypto.provider.SunJCE());
    }

    // Salt
    static byte[] salt = {
        (byte) 0xc7, (byte) 0x73, (byte) 0x21, (byte) 0x8c,
        (byte) 0x7e, (byte) 0xc8, (byte) 0xee, (byte) 0x99
    };
    
    static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    
    static String UNIHZ_CRYPTO_STRING = "PictureUnihz20090123456789012345";
    
    static int[] UNIHZ_CRYPTO_REMOVE_POSITION = new int[]{5,11,19,37,40,42,43,49,50,51,55,58};
    
    // Iteration count
    static int count = 20;

    static char[] password = {
        'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a',
        'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a',
        'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a',
        'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a',
        'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a',
        'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a',
        'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a',
        'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a',
        'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a',
        'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a',
        'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a',
        'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a',
        'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a'
    };

    /** Singleton instance */
    private CryptoHelper() {
    }

    /**
     * Decrypt a String.
     * @param input The encrypted string.
     * @return The decrypted string.
     */
    public static String doDecrypt(String input) throws Exception {

        PBEKeySpec pbeKeySpec;
        PBEParameterSpec pbeParamSpec;
        SecretKeyFactory keyFac;
        byte[] buf = input.getBytes("ISO-8859-1");

        // Create PBE parameter set
        pbeParamSpec = new PBEParameterSpec(salt, count);

        pbeKeySpec = new PBEKeySpec(password);
        keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

        // Create PBE Cipher
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");

        // Initialize PBE Cipher with key and parameters
        pbeCipher.init(Cipher.DECRYPT_MODE, pbeKey, pbeParamSpec);

        // Our cleartext
        byte[] ciphertext = buf;

        // Encrypt the cleartext
        byte[] cleartext = pbeCipher.doFinal(ciphertext);

        return new String(cleartext, "ISO-8859-1");
    }

    /**
     * Encrypt a String.
     * @param input The input string.
     * @return The encrypted string.
     */
    public static String doEncrypt(String input) throws Exception {

        PBEKeySpec pbeKeySpec;
        PBEParameterSpec pbeParamSpec;
        SecretKeyFactory keyFac;
        //byte[] buf = input.getBytes();

        // Create PBE parameter set
        pbeParamSpec = new PBEParameterSpec(salt, count);

        pbeKeySpec = new PBEKeySpec(password);
        keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

        // Create PBE Cipher
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");

        // Initialize PBE Cipher with key and parameters
        pbeCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);

        // Our cleartext
        byte[] cleartext = input.getBytes();

        // Encrypt the cleartext
        byte[] ciphertext = pbeCipher.doFinal(cleartext);

        return new String(ciphertext, "ISO-8859-1");
    }

    /**
     * Digest a String.
     * @param input The input string.
     * @return The digested string.
     */
    public static String doDigest(String input) throws Exception {
        byte[] buf = input.getBytes();
        return CryptoHelper.doDigest(buf);
    }
    
    /*
    public static String doDigest(byte[] buf) throws Exception {
        byte[] digest = null;
        StringBuffer hexString = new StringBuffer();
        MessageDigest algorithm = MessageDigest.getInstance("MD5");

        algorithm.reset();
        algorithm.update(buf);
        digest = algorithm.digest();
        int digestLen = digest.length;
        for (int i = 0; i < digestLen; i++) {
            byte b = digest[i];
            if (b >= 0 && b <= 0xF) {
            	hexString.append("0").append(Integer.toHexString(0xF & b));
            }
            else {
            	hexString.append(Integer.toHexString(0xFF & b));
            }
        }

        return hexString.toString();    	
    }
    */
    
    public static String doDigest(byte[] s) throws Exception{
        MessageDigest mdTemp = MessageDigest.getInstance("MD5");
        mdTemp.update(s);
        byte[] md = mdTemp.digest();
        int j = md.length;
        char str[] = new char[j * 2];
        int k = 0;
        //移位 输出字符串
        for (int i = 0; i < j; i++){
            str[k++] = hexDigits[md[i] >>> 4 & 0xf];
            str[k++] = hexDigits[md[i] & 0xf];
        }
        return new String(str);
    }
    

    /*
     * Converts a byte to hex digit and writes to the supplied buffer
     */
    public static void byte2hex(byte b, StringBuffer buf) {
        char[] hexChars = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        int high = ( (b & 0xf0) >> 4);
        int low = (b & 0x0f);
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
    }

    /*
     * Converts a byte array to hex string
     */
    public static String toHexString(byte[] block) {
        StringBuffer buf = new StringBuffer();

        int len = block.length;

        for (int i = 0; i < len; i++) {
            byte2hex(block[i], buf);
            if (i < len - 1) {
                buf.append(":");
            }
        }
        return buf.toString();
    }

    public static byte[] hex2byte(String input) {
        byte[] ret_val = new byte[input.length() / 2];
        for (int i = 0; i < ret_val.length; i++) {
            ret_val[i] = (byte) (hexcharvalue(input.charAt( (i + 1) * 2 - 1))
                                 +
                                 (hexcharvalue(input.charAt( (i + 1) * 2 - 2)) *
                                  16));
        }

        return ret_val;
    }

    public static int hexcharvalue(char input) {
        char[] hexChars = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        for (int i = 0; i < hexChars.length; i++) {
            if (input == hexChars[i]) {
                return i;
            }
        }

        return 0;
    }
    
    /**
     * 获取图片加密字符串
     * @param imageByte
     * @return
     * @throws Exception
     */
    public static String getUniHzImageEncryptString(byte[] imageByte) throws Exception{
    	String md5One = CryptoHelper.doDigest(imageByte);
    	int len = md5One.length();
    	int unihzCryptoStringLen = UNIHZ_CRYPTO_STRING.length();
    	StringBuffer cryptoString = new StringBuffer();
    	int i = 0;
    	for(;i<len;i++){
    		cryptoString.append(md5One.charAt(i));
    		if(i<unihzCryptoStringLen) cryptoString.append(UNIHZ_CRYPTO_STRING.charAt(i));
    	}
    	for(;i<unihzCryptoStringLen;i++){
    		cryptoString.append(UNIHZ_CRYPTO_STRING.charAt(i));
    	}
    	String md5Two = CryptoHelper.doDigest(cryptoString.toString());
    	String totalMd5 = md5One + md5Two;
    	
    	StringBuffer resultMd5 = new StringBuffer();
    	int md5Len = totalMd5.length();
    	int removeLen = UNIHZ_CRYPTO_REMOVE_POSITION.length;
    	for(i=0;i<md5Len;i++){
    		boolean removeAble = false;
    		for(int j=0;j<removeLen;j++){
    			if(i==UNIHZ_CRYPTO_REMOVE_POSITION[j]){
    				removeAble = true;
    				break;
    			}
    		}
    		if(!removeAble) resultMd5.append(totalMd5.charAt(i));
    	}
    	
    	return resultMd5.toString();
    }

    public static void main(String[] args) {
        try {           
        	byte[] fileByte1 = FileHelper.getBytes("D:/1.JPG");
        	String md5 = CryptoHelper.getUniHzImageEncryptString(fileByte1);
        	byte[] md5Byte = md5.getBytes();
        	System.out.println(md5);
        	
        	byte[] target = new byte[fileByte1.length+md5Byte.length];
        	System.arraycopy(fileByte1, 0, target, 0, fileByte1.length);
        	System.arraycopy(md5Byte, 0, target, fileByte1.length, md5Byte.length);	     
        	FileHelper.writeFile(target, "D:/1-md5.JPG");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
