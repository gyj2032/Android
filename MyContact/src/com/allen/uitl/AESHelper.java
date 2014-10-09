package com.allen.uitl;
/***
 * 关于加解密JAVA一般使用的JCE，关于C++可以实现AES加解密的开源项目就多的数不胜数的。
 理论上上算法一样，对称密钥一样就能够互相识别了。
 相信很多人开始想法都同我一样，起初我JAVA用JCE，C++使用openssl。
 结果发现加密出的密文完全不相同。
 出现问题就要解决
 了解了一下JCE：
 JCE中AES支持五中模式：CBC，CFB，ECB，OFB，PCBC；支持三种填充：NoPadding，PKCS5Padding，ISO10126Padding。
 不支持SSL3Padding。不支持“NONE”模式。
 好原来有模式和填充一说。
 在OPENSSL中直接有一个CBC加解密函数。填充没找到，试过后发现C++加密出的内容比JAVA的要长出一截，前面的内容是完全一样的。
 这应该就出现在填充上。
 本来以为找到问题关键就应该很容易解决的。结果发现OPENSSL的填充是固定实现的，而我所需要解密的java端代码不能改动。
 一条路走不通咱就换条路。最后发现有一个开源项目Botan什么都有而且同JCE十分相似并且满足我要求垮平台，好就是它了。
 附：
 算法/模式/填充 16字节加密后数据长度 不满16字节加密后长度
 AES/CBC/NoPadding 16 不支持
 AES/CBC/PKCS5Padding 32 16
 AES/CBC/ISO10126Padding 32 16
 AES/CFB/NoPadding 16 原始数据长度
 AES/CFB/PKCS5Padding 32 16
 AES/CFB/ISO10126Padding 32 16
 AES/ECB/NoPadding 16 不支持
 AES/ECB/PKCS5Padding 32 16
 AES/ECB/ISO10126Padding 32 16
 AES/OFB/NoPadding 16 原始数据长度
 AES/OFB/PKCS5Padding 32 16
 AES/OFB/ISO10126Padding 32 16
 AES/PCBC/NoPadding 16 不支持
 AES/PCBC/PKCS5Padding 32 16
 AES/PCBC/ISO10126Padding 32 16
 可以看到，在原始数据长度为16的整数倍时，
 假如原始数据长度等于16*n，
 则使用NoPadding时加密后数据长度等于16*n
 ，其它情况下加密数据长度等于16*(n+1)。
 在不足16的整数倍的情况下，假如原始数据长度等于16*n+m[其中m小于16]
 ，除了NoPadding填充之外的任何方式，加密数据长度都等于16*(n+1)
 ；NoPadding填充情况下，CBC、ECB和PCBC三种模式是不支持的，CFB、OFB两种模式下则加密数据长度等于原始数据长度。
 */
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
/**
 * AES加密解密类
 *
 * @author xuxiong
 */
public class AESHelper {
    /** 算法/模式/填充 **/
    private static final String CipherMode = "AES/ECB/NoPadding";
    
    /**
     * 加密(结果为16进制字符串)
     *
     * @param content
     *            要加密的字符串
     * @param password
     *            密钥
     * @return 加密后的16进制字符串
     */
    public static String encrypt(String content, String password) {
    	if (Util.isNull(content) || Util.isNull(password)) {
    		return null;
    	}
    	content = checkText(content);
        byte[] data = null;
        try {
            data = content.getBytes("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        data = encrypt(data, password);
        String result = byte2hex(data);
        return result;
    }
    /** 解密16进制的字符串为字符串 **/
    public static String decrypt(String content, String password) {
    	if (Util.isNull(content) || Util.isNull(password)) {
    		return null;
    	}
        byte[] data = null;
        try {
            data = hex2byte(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        data = decrypt(data, password);
        if (data == null)
            return null;
        String result = null;
        try {
            result = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result.trim();
    }
    
    /**
     * 创建密钥
     *
     * @param password
     *            例如："0123456701234567" 128位 16*8 所有密钥长度不能超过16字符中文占两个。192 24；
     *            256 32
     * @return SecretKeySpec 实例
     */
    private static SecretKeySpec createKey(String password) {
        byte[] data = null;
        if (password == null) {
            password = "";
        }
        StringBuffer sb = new StringBuffer(16);
        sb.append(password);
        while (sb.length() < 16) {
            sb.append("0");
        }
        if (sb.length() > 16) {
            sb.setLength(16);
        }
        try {
            data = sb.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new SecretKeySpec(data, "AES");
    }
    /**
     * 加密字节数据
     *
     * @param content
     *            需要加密的字节数组
     * @param password
     *            密钥 128 <16个字节 192 <24,256 <32个字节
     * @return 加密完后的字节数组
     */
    private static byte[] encrypt(byte[] content, String password) {
        try {
            SecretKeySpec key = createKey(password);
            Cipher cipher = Cipher.getInstance(CipherMode);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /** 解密字节数组 **/
    private static byte[] decrypt(byte[] content, String password) {
        try {
            SecretKeySpec key = createKey(password);
            Cipher cipher = Cipher.getInstance(CipherMode);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private static String checkText(String text) {
    	int length = text.getBytes().length%16;
    	if (length == 0) {
    		return text;
    	}
    	
    	StringBuffer sb = new StringBuffer(text);
    	length = 16 - length;
    	for (int i = 0; i < length; i++) {
    		sb.append(" ");
		}
    	return sb.toString();
    }

    /**
     * 字节数组转成16进制字符串
     *
     * @param b
     * @return 16进制字符串
     */
    public static String byte2hex(byte[] b) { // 一个字节的数，
        StringBuffer sb = new StringBuffer(b.length * 2);
        String tmp = "";
        for (int n = 0; n < b.length; n++) {
            // 整数转成十六进制表示
            tmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (tmp.length() == 1) {
                sb.append("0");
            }
            sb.append(tmp);
        }
        return sb.toString().toUpperCase(); // 转成大写
    }
    /**
     * 将hex字符串转换成字节数组 *
     *
     * @param 16进制的字符串
     * @return 字节数组
     */
    private static byte[] hex2byte(String inputString) {
        if (inputString == null || inputString.length() < 2) {
            return new byte[0];
        }
        inputString = inputString.toLowerCase();
        int l = inputString.length() / 2;
        byte[] result = new byte[l];
        for (int i = 0; i < l; ++i) {
            String tmp = inputString.substring(2 * i, 2 * i + 2);
            result[i] = (byte) (Integer.parseInt(tmp, 16) & 0xFF);
        }
        return result;
    }
    
    public static void encrypt(String filePath, String outPath, String password) {
		try {
			FileInputStream fis = new FileInputStream(filePath);
			// This stream write the encrypted text. This stream will be wrapped by
			// another stream.
			FileOutputStream fos = new FileOutputStream(outPath);
			// Length is 16 byte
			SecretKeySpec sks = new SecretKeySpec(password.getBytes(), "AES");
			// Create cipher
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, sks);
			// Wrap the output stream
			CipherOutputStream cos = new CipherOutputStream(fos, cipher);
			// Write bytes
			int b;
			byte[] d = new byte[1024];
			while ((b = fis.read(d)) != -1) {
				cos.write(d, 0, b);
			}
			// Flush and close streams.
			cos.flush();
			cos.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void decrypt(String filePath, String outPath, String password) {
		try {
			FileInputStream fis = new FileInputStream(filePath);
			FileOutputStream fos = new FileOutputStream(outPath);
			SecretKeySpec sks = new SecretKeySpec(password.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, sks);
			CipherInputStream cis = new CipherInputStream(fis, cipher);
			int b;
			byte[] d = new byte[1024];
			while ((b = cis.read(d)) != -1) {
				fos.write(d, 0, b);
			}
			fos.flush();
			fos.close();
			cis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String updatePassword(String password) {
		String add = "";
		if (password.length() > 16) {
			password = password.substring(0, 16);
		} else if (password.length() < 16) {
			for(int i=password.length(); i<16; i++) {
				add += "0";
			}
		}
		return password+add;
	}
}