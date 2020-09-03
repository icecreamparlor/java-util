import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;

public class CryptoUtil {

    private static final byte[] key = { (byte)0xEF, (byte)0xCD, (byte)0xAB, (byte)0x90, (byte)0x78, (byte)0x56, (byte)0x34, (byte)0x12 };
    private static final byte[] iv  = { (byte)0xEF, (byte)0xCD, (byte)0xAB, (byte)0x90, (byte)0x78, (byte)0x56, (byte)0x34, (byte)0x12 };

    private static final Logger logger = LoggerFactory.getLogger(CryptoUtil.class);

    public static String encryptToBase64(byte[] byteValue) {

        return Base64Util.encode(encrypt(byteValue));

    }

    public static byte[] encrypt(byte[] byteValue) {
        ByteArrayOutputStream ms = new ByteArrayOutputStream();

        Cipher cipher = null;

        try {

            cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            SecretKeySpec key = new SecretKeySpec(getKey(), "DES");
            IvParameterSpec iv = new IvParameterSpec(getIv());
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);

            ms.write(cipher.doFinal(byteValue));
            byteValue = ms.toByteArray();
            ms.close();

        } catch (Exception e) {

            logger.error("Exception Occured While Encrypt : ", e);

        }

        return byteValue;
    }

    public static byte[] decryptFromBase64(String encodedValue) throws Exception {

        byte[] decodedBytes = Base64Util.decode(encodedValue);
        return decrypt(decodedBytes);

    }

    public static byte[] decrypt(byte[] byteValue) throws Exception {

        try {

            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            SecretKeySpec key = new SecretKeySpec(getKey(), "DES");
            IvParameterSpec iv = new IvParameterSpec(getIv());
            cipher.init(Cipher.DECRYPT_MODE, key, iv);

            return cipher.doFinal(byteValue, 0, byteValue.length);

        } catch(Exception e) {

            throw new Exception("Exception Occured While decrypt : " + e.getMessage());

        }
    }

    public static byte[] getKey() {
        return key;
    }

    public static byte[] getIv() {
        return iv;
    }

}
