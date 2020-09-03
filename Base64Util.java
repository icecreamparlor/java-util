import java.util.Base64;

public class Base64Util {

    public static String encode(String target) {

        byte[] targetByte = target.getBytes();

        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode(targetByte);

        return new String(encodedBytes);

    }

    public static String decode(String encoded) {

        byte[] encodedByte = encoded.getBytes();

        Base64.Decoder decoder = Base64.getDecoder();

        byte[] decodedByte = decoder.decode(encodedByte);

        return new String(decodedByte);

    }

}
