package core.tools;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

public class RSAGenerator {

    public static void main(String... args){
        try {
            KeyFactory ondemand_factory = KeyFactory.getInstance("RSA");
            KeyPairGenerator ondemand_keygen = KeyPairGenerator.getInstance("RSA");
            ondemand_keygen.initialize(1024);
            KeyPair ondemand_keypair = ondemand_keygen.genKeyPair();

            KeyFactory login_factory = KeyFactory.getInstance("RSA");
            KeyPairGenerator login_keygen = KeyPairGenerator.getInstance("RSA");
            login_keygen.initialize(1024);
            KeyPair login_keypair = login_keygen.genKeyPair();

            RSAPrivateKeySpec ondemand_privateSpec = ondemand_factory.getKeySpec(ondemand_keypair.getPrivate(),
                    RSAPrivateKeySpec.class);

            RSAPublicKeySpec ondemand_publicSpec = ondemand_factory.getKeySpec(ondemand_keypair.getPublic(),
                    RSAPublicKeySpec.class);

            RSAPrivateKeySpec login_privateSpec = login_factory.getKeySpec(login_keypair.getPrivate(),
                    RSAPrivateKeySpec.class);

            RSAPublicKeySpec login_publicSpec = login_factory.getKeySpec(login_keypair.getPublic(),
                    RSAPublicKeySpec.class);

            writeKey(0, ondemand_privateSpec.getModulus(), ondemand_privateSpec.getPrivateExponent(),
                    login_privateSpec.getModulus(), login_privateSpec.getPrivateExponent());

            writeKey(1, ondemand_publicSpec.getModulus(), ondemand_publicSpec.getPublicExponent(),
                    login_publicSpec.getModulus(), login_publicSpec.getPublicExponent());

        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static void writeKey(int type, @NotNull BigInteger ondemand_modulus, @NotNull BigInteger ondemand_exponent,
                                 @NotNull BigInteger login_modulus, @NotNull BigInteger login_exponent){
        System.out.println("===============--- Chain Keys ---===============");
        String prefix = type == 0 ? "private" : "public";
        System.out.println(prefix + "_ondemand_modulus = " + ondemand_modulus);
        System.out.println(prefix + "_ondemand_exponent = " + ondemand_exponent);
        System.out.println(prefix + "_login_modulus = " + login_modulus);
        System.out.println(prefix + "_login_exponent = " + login_exponent);
        System.out.println("===============--- End Keys ---===============");
    }
}
