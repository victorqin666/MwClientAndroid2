package com.work.util;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Created by tangyx on 16/8/10.
 */
public class EncryptUtils {
    /**
     * MD5加密
     *
     * @param data 明文字符串
     * @return 密文
     */
    public static String getMD5(String data) {
        return getMD5(data.getBytes());
    }
    /**
     * MD5加密
     *
     * @param data 明文字符串
     * @param salt 盐
     * @return 密文
     */
    public static String getMD5(String data, String salt) {
        return bytes2Hex(encryptMD5((data + salt).getBytes()));
    }
    /**
     * MD5加密
     *
     * @param data 明文字节数组
     * @return 密文
     */
    public static String getMD5(byte[] data) {
        return bytes2Hex(encryptMD5(data));
    }
    /**
     * MD5加密
     *
     * @param data 明文字节数组
     * @param salt 盐字节数组
     * @return 密文
     */
    public static String getMD5(byte[] data, byte[] salt) {
        byte[] dataSalt = new byte[data.length + salt.length];
        System.arraycopy(data, 0, dataSalt, 0, data.length);
        System.arraycopy(salt, 0, dataSalt, data.length, salt.length);
        return bytes2Hex(encryptMD5(dataSalt));
    }
    /**
     * MD5加密
     *
     * @param data 明文字节数组
     * @return 密文字节数组
     */
    public static byte[] encryptMD5(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
    /**
     * 获取文件的MD5校验码
     *
     * @param filePath 文件路径
     * @return 文件的MD5校验码
     */
    public static String getMD5File(String filePath) {
        return getMD5File(new File(filePath));
    }
    /**
     * 获取文件的MD5校验码
     *
     * @param file 文件
     * @return 文件的MD5校验码
     */
    public static String getMD5File(File file) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            FileChannel channel = in.getChannel();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(buffer);
            return bytes2Hex(md.digest());
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
        }
        return "";
    }
    /**
     * SHA加密
     *
     * @param data 明文字符串
     * @return 密文
     */
    public static String getSHA(String data) {
        return getSHA(data.getBytes());
    }

    /**
     * SHA加密
     *
     * @param data 明文字节数组
     * @return 密文
     */
    public static String getSHA(byte[] data) {
        return bytes2Hex(encryptSHA(data));
    }
    /**
     * SHA加密
     *
     * @param data 明文字节数组
     * @return 密文字节数组
     */
    public static byte[] encryptSHA(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(data);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
    /**
     * 一个byte转为2个hex字符
     *
     * @param src byte数组
     * @return 16进制大写字符串
     */
    public static String bytes2Hex(byte[] src) {
        char[] res = new char[src.length << 1];
        final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        for (int i = 0, j = 0; i < src.length; i++) {
            res[j++] = hexDigits[src[i] >>> 4 & 0x0f];
            res[j++] = hexDigits[src[i] & 0x0f];
        }
        return new String(res);
    }

    private static String DES = "DES";
    //des 加密
    public static String encryptByteDES(byte[] byteD, String strKey) throws Exception {
        return encode(doEncrypt(byteD,getKey(strKey),DES));
    }
    /**
     * 执行加密操作
     *
     * @param data 待操作数据
     * @param key  Key
     * @param type 算法 RSA or DES
     * @return
     * @throws Exception
     */
    public static byte[] doEncrypt(byte[] data, Key key, String type) throws Exception {
        Cipher cipher = Cipher.getInstance(type);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }
    public static SecretKey getKey(String strKey) throws Exception {
        DESKeySpec desKeySpec = new DESKeySpec(strKey.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey sk = keyFactory.generateSecret(desKeySpec);
        return sk;
    }

    //base64

    private static final char[] legalChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();


    public static String encode(byte[] data) {
        int start = 0;
        int len = data.length;
        StringBuffer buf = new StringBuffer(data.length * 3 / 2);
        int end = len - 3;
        int i = start;
        int n = 0;

        int d;
        while(i <= end) {
            d = (data[i] & 255) << 16 | (data[i + 1] & 255) << 8 | data[i + 2] & 255;
            buf.append(legalChars[d >> 18 & 63]);
            buf.append(legalChars[d >> 12 & 63]);
            buf.append(legalChars[d >> 6 & 63]);
            buf.append(legalChars[d & 63]);
            i += 3;
            if(n++ >= 14) {
                n = 0;
                buf.append(" ");
            }
        }

        if(i == start + len - 2) {
            d = (data[i] & 255) << 16 | (data[i + 1] & 255) << 8;
            buf.append(legalChars[d >> 18 & 63]);
            buf.append(legalChars[d >> 12 & 63]);
            buf.append(legalChars[d >> 6 & 63]);
            buf.append("=");
        } else if(i == start + len - 1) {
            d = (data[i] & 255) << 16;
            buf.append(legalChars[d >> 18 & 63]);
            buf.append(legalChars[d >> 12 & 63]);
            buf.append("==");
        }

        return buf.toString();
    }

    private static int decode(char c) {
        if(c >= 65 && c <= 90) {
            return c - 65;
        } else if(c >= 97 && c <= 122) {
            return c - 97 + 26;
        } else if(c >= 48 && c <= 57) {
            return c - 48 + 26 + 26;
        } else {
            switch(c) {
                case '+':
                    return 62;
                case '/':
                    return 63;
                case '=':
                    return 0;
                default:
                    throw new RuntimeException("unexpected code: " + c);
            }
        }
    }

    public static byte[] decode(String s) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            decode(s, bos);
        } catch (IOException var5) {
            throw new RuntimeException();
        }

        byte[] decodedBytes = bos.toByteArray();

        try {
            bos.close();
            bos = null;
        } catch (IOException var4) {
            System.err.println("Error while decoding BASE64: " + var4.toString());
        }

        return decodedBytes;
    }

    private static void decode(String s, OutputStream os) throws IOException {
        int i = 0;
        int len = s.length();

        while(true) {
            while(i < len && s.charAt(i) <= 32) {
                ++i;
            }

            if(i == len) {
                break;
            }

            int tri = (decode(s.charAt(i)) << 18) + (decode(s.charAt(i + 1)) << 12) + (decode(s.charAt(i + 2)) << 6) + decode(s.charAt(i + 3));
            os.write(tri >> 16 & 255);
            if(s.charAt(i + 2) == 61) {
                break;
            }

            os.write(tri >> 8 & 255);
            if(s.charAt(i + 3) == 61) {
                break;
            }

            os.write(tri & 255);
            i += 4;
        }

    }
}
