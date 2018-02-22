/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package source;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Clase encargada de gestionar cifrados de la aplicacion
 *
 * @author Andres
 */
public class CryptManager {

    PrivateKey privateKey;

    private final byte[] salt = {
        (byte) 0xc7, (byte) 0x73, (byte) 0x21, (byte) 0x8c,
        (byte) 0x7e, (byte) 0xc8, (byte) 0xee, (byte) 0x99
    };

    /**
     * Cifra el texto que se le pasa con un algoritmo creado a partir de la
     * contraseña. Se almacena el texto cifrado y el iv para su posterior
     * descifrado.
     *
     * @param plano texto sin cifrar
     * @param pass contraseña introducida
     */
    public void cifrar(String plano, String pass) {
        try {
            //Generar la llave a partir de los 16 primeros bytes del hash de
            //la union de un array de bytes random, "Neron" y la contraseña
            byte[] key = (hexadecimal(salt) + "Neron" + pass).getBytes("UTF-8");
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // use only first 128 bit

            //Se consigue una SecretKeySpec a partir de la key anterior
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

            //Se cifra el texto en plano
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            AlgorithmParameters params = cipher.getParameters();

            //Se genera el iv usado para cifrar el texto en plano
            //El iv es totalmente diferente cada vez que se cifra
            byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
            byte[] ciphertext = cipher.doFinal(plano.getBytes("UTF-8"));

            guardar(ciphertext, iv);

        } catch (NoSuchAlgorithmException ex) {
            source.Logger.error("CryptManager", "cifrar()", 
                    "Algoritmo inválido", ex.getMessage());
        } catch (NoSuchPaddingException ex) {
            source.Logger.error("CryptManager", "cifrar()", 
                    "Clave de cifrado incorrecta", ex.getMessage());
        } catch (InvalidKeyException ex) {
            source.Logger.error("CryptManager", "cifrar()", 
                    "Clave de cifrado inválida", ex.getMessage());
        } catch (IllegalBlockSizeException ex) {
            source.Logger.error("CryptManager", "cifrar()", 
                    "Tamaño de bloque erróneo", ex.getMessage());
        } catch (BadPaddingException ex) {
            source.Logger.error("CryptManager", "cifrar()", 
                    "Clave de cifrado incorrecta", ex.getMessage());
        } catch (UnsupportedEncodingException ex) {
            source.Logger.error("CryptManager", "cifrar()", 
                    "Algoritmo de cifrado inválido", ex.getMessage());
        } catch (InvalidParameterSpecException ex) {
            source.Logger.error("CryptManager", "cifrar()", 
                    "Error al crear el IV", ex.getMessage());
        }
    }

    /**
     * Descifra el texto guardado mediante la reconstruccion de la clave usada
     * para cifrar el texto
     *
     * @param pass contraseña introducida
     * @return plano texto sin cifrar
     */
    public String descifrar(String pass) {
        String plano = "";
        try {
            //Generar la llave a partir de los 16 primeros bytes del hash de
            //la union de un array de bytes random, "Neron" y la contraseña
            byte[] key = (hexadecimal(salt) + "Neron" + pass).getBytes("UTF-8");
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);

            //Se consigue una SecretKeySpec a partir de la key anterior
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

            //Se recupera el iv para generar el mismo Cipher del cifrado
            byte[] iv = cargarIV();
            //Se recupera el texto cifrado guardado
            byte[] textoCifrado = cargarCiphertext();

            //Se descifra el texto con la misma clave usada para cifrarlo
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
            plano = new String(cipher.doFinal(textoCifrado), "UTF-8");

        } catch (NoSuchAlgorithmException ex) {
            source.Logger.error("CryptManager", "descifrar()", 
                    "Algoritmo inválido", ex.getMessage());
        } catch (NoSuchPaddingException ex) {
            source.Logger.error("CryptManager", "descifrar()", 
                    "Clave de cifrado incorrecta", ex.getMessage());
        } catch (InvalidKeyException ex) {
            source.Logger.error("CryptManager", "descifrar()", 
                    "Clave de cifrado inválida", ex.getMessage());
        } catch (IllegalBlockSizeException ex) {
            source.Logger.error("CryptManager", "descifrar()", 
                    "Tamaño de bloque erróneo", ex.getMessage());
        } catch (BadPaddingException ex) {
            source.Logger.error("CryptManager", "descifrar()", 
                    "Clave de cifrado incorrecta", ex.getMessage());
        } catch (UnsupportedEncodingException ex) {
            source.Logger.error("CryptManager", "descifrar()", 
                    "Algoritmo de cifrado inválido", ex.getMessage());
        } catch (InvalidAlgorithmParameterException ex) {
            source.Logger.error("CryptManager", "descifrar()", 
                    "Algoritmo de cifrado inválido", ex.getMessage());
        }
        return plano;
    }

    /**
     * Devuelve el hash de la contraseña recibida
     *
     * @param pass contraseña introducida
     * @return resumen hash de la contraseña en SHA
     */
    public String hash(String pass) {
        String resumen = "";
        try {
            byte[] textoArrayBytes = pass.getBytes();
            MessageDigest mensaje = MessageDigest.getInstance("SHA");
            mensaje.update(textoArrayBytes);
            resumen = hexadecimal(mensaje.digest());
        } catch (NoSuchAlgorithmException ex) {
            source.Logger.error("CryptManager", "hash()", 
                    "Algoritmo inválido", ex.getMessage());
        }
        return resumen;
    }

    /**
     * Genera un par de claves privada - publica y las guarda en ficheros
     *
     * @return publicKey clave publica 
     */
    public PublicKey generarClaves() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair kp = kpg.genKeyPair();
            PublicKey publicKey = kp.getPublic();
            privateKey = kp.getPrivate();
            return publicKey;
        } catch (Exception ex) {
            source.Logger.error("CryptManager", "generarClaves()", 
                    "Error al generar las claves", ex.getMessage());
        }
        return null;
    }

    /**
     * Se descifra el mensaje recibido con la clave privada
     *
     * @param textoCifrado texto cifrado
     * @return texto en plano
     */
    public String descifrarPri(byte[] textoCifrado) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            String textoPlano = new String(cipher.doFinal(textoCifrado), "UTF-8");
            return textoPlano;

        } catch (NoSuchAlgorithmException ex) {
            source.Logger.error("CryptManager", "descifrarPri()", 
                    "Algoritmo inválido", ex.getMessage());
        } catch (NoSuchPaddingException ex) {
            source.Logger.error("CryptManager", "descifrarPri()", 
                    "Clave de cifrado incorrecta", ex.getMessage());
        } catch (InvalidKeyException ex) {
            source.Logger.error("CryptManager", "descifrarPri()", 
                    "Clave de cifrado inválida", ex.getMessage());
        } catch (IllegalBlockSizeException ex) {
            source.Logger.error("CryptManager", "descifrarPri()", 
                    "Tamaño de bloque erróneo", ex.getMessage());
        } catch (BadPaddingException ex) {
            source.Logger.error("CryptManager", "descifrarPri()", 
                    "Clave de cifrado incorrecta", ex.getMessage());
        } catch (UnsupportedEncodingException ex) {
            source.Logger.error("CryptManager", "descifrarPri()", 
                    "Algoritmo de cifrado inválido", ex.getMessage());
        }
        return null;
    }

    /**
     * Convierte a String un array de bytes
     *
     * @param arrayBytes array de bytes 
     * @return string construido
     */
    public static String hexadecimal(byte[] arrayBytes) {
        String cadenaHexadecimal = "";
        String apoyo;
        for (int i = 0; i < arrayBytes.length; i++) {
            apoyo = Integer.toHexString(arrayBytes[i] & 0xFF);
            if (apoyo.length() == 1) {
                apoyo = "0" + apoyo;
            }
            cadenaHexadecimal = cadenaHexadecimal + apoyo;
        }
        return cadenaHexadecimal;
    }

    /**
     * Almacena el texto cifrado y el iv
     *
     * @param textoCifrado
     * @param iv
     */
    private void guardar(byte[] textoCifrado, byte[] iv) {
        crear("./data");
        try {
            Path ruta = Paths.get("./data/data");
            Files.write(ruta, textoCifrado);
        } catch (IOException ex) {
            source.Logger.error("CryptManager", "guardar()", 
                    "El fichero data no existe", ex.getMessage());
        }
        try {
            Path path = Paths.get("./data/iv");
            Files.write(path, iv);
        } catch (IOException ex) {
            source.Logger.error("CryptManager", "guardar()", 
                    "El fichero iv no existe", ex.getMessage());
        }
    }

    /**
     * Devuelve el iv almacenado
     *
     * @return
     */
    private byte[] cargarIV() {
        try {
            Path ruta = Paths.get("./data/iv");
            return Files.readAllBytes(ruta);
        } catch (IOException ex) {
            source.Logger.error("CryptManager", "cargarIV()", 
                    "El fichero iv no existe", ex.getMessage());
        }
        return null;
    }

    /**
     * Devuelve el texto cifrado
     *
     * @return
     */
    private byte[] cargarCiphertext() {
        try {
            Path path = Paths.get("./data/data");
            return Files.readAllBytes(path);
        } catch (IOException ex) {
            source.Logger.error("CryptManager", "cargarCiphertext()", "El fichero data no existe", ex.getMessage());
        }
        return null;
    }

    /**
     * Crea el fichero especificado creando todos los subdirectorios que
     * necesite
     *
     * @param ruta
     */
    private void crear(String ruta) {
        File f = new File(ruta);
        f.mkdirs();
    }
}
