package co.za.kroutled.fixme.core;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5hash {
    public static String idHash(String id)
    {
        String myHash = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(id.getBytes());
            byte[] digest = md.digest();
            myHash = DatatypeConverter.printHexBinary(digest).toLowerCase();
        }catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return myHash;
    }
}
