package com.yywh.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.log4j.Logger;

/**
 * 
 * @author you 2015年10月30日
 *
 */
public class CodeUtil {
	private static Logger logs = Logger.getLogger(CodeUtil.class);
	private static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/**
	 * 对传入参数进行 md5编码。
	 * 
	 * @param str
	 * @return
	 */
	/*
	 * public static String enCodeMd5_UTF8(String str) { String sResult = "";
	 * try { MessageDigest md = MessageDigest.getInstance("MD5");
	 * 
	 * md.update(str.getBytes("UTF-8"));
	 * 
	 * byte cResult[] = md.digest();
	 * 
	 * for (int i = 0; i < cResult.length; i++) { if (cResult[i] < 0) {
	 * cResult[i] += 128; } String sTemp = byte2Hex(cResult[i]); sResult +=
	 * sTemp; } } catch (Exception e) { sResult = ""; }
	 * 
	 * return sResult;
	 * 
	 * }
	 * 
	 * private static String byte2Hex(byte b) { return ("" +
	 * "0123456789ABCDEF".charAt(b >> 4 & 0xf) + "0123456789ABCDEF" .charAt(b &
	 * 0xf)); }
	 */
	/**
	 * 用于微信生成md5
	 * 
	 * @param input
	 * @return
	 */
	public static String enCodeMd5_UTF8(String input) {
		try {
			byte[] btInput = input.getBytes("UTF-8");
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将系统默认编码 转为utf-8
	 * 
	 * @param input
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String codeUTF8(String input)
			throws UnsupportedEncodingException {
		if (input != null) {
			return new String(input.getBytes(), "UTF-8");
		} else {
			return null;
		}
	}

	/**
	 * 用uuid生成32位随 机码。
	 * 
	 * @return
	 */
	public static String getRandomCode32() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString().replaceAll("-", "");
	}

	public static String enCodeSHA1(String decript) {
		try {
			MessageDigest digest = java.security.MessageDigest
					.getInstance("SHA-1");
			digest.update(decript.getBytes());
			byte messageDigest[] = digest.digest();
			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			// 字节数组转换为 十六进制 数
			for (int i = 0; i < messageDigest.length; i++) {
				String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexString.append(0);
				}
				hexString.append(shaHex);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 微信请求生成 签名前
	 * 排序key=value。
	 * 如：body=test&device_info=1000&mch_id=10000100&
	 * @param object
	 * @param key API密钥
	 * @return
	 */
	public static String getSign(Object object) {
		Class clazz = object.getClass();
		Field[] fields = clazz.getDeclaredFields();
		Map<String, Object> map = new TreeMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		boolean accessible = false;
		try {
			for (Field field : fields) {
				accessible = field.isAccessible();
				field.setAccessible(true);
				if (field.get(object) != null) {
					map.put(field.getName(), field.get(object));
				}
				field.setAccessible(accessible);
			}
		} catch (IllegalArgumentException e) {
			logs.error(e);
		} catch (IllegalAccessException e) {
			logs.error(e);
		}
		//优化后代码
		Iterator<Entry<String, Object>> it = map.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, Object> entry = (Entry<String, Object>) it.next();
			sb.append(entry.getKey());
			sb.append("=");
			//过滤掉多余的换行符。
			Object mapValue = entry.getValue();
			if(mapValue instanceof String){
				mapValue = ((String)mapValue).replaceAll("\r|\n|\t", "");
			}
			sb.append(mapValue);
			sb.append("&");
		}
		sb.append("key=");
		//sb.append(WXConfig.KEY);
		return enCodeMd5_UTF8(sb.toString());
	}

	/**
	 * 
	 * @param prepay_id 统一下单接口返回的prepay_id参数值
	 * @return json字符串
	 * 直接拼接到支付网页中,如下？号的位置：<br>
	 * function onBridgeReady(){<br>
	 * WeixinJSBridge.invoke(<br>
	 *  'getBrandWCPayRequest',？,<br>
	 *  function(res){<br>
	 *  if(res.err_msg == "get_brand_wcpay_request：ok" ) {}     // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。<br>
	 *  }<br>
	 */
	public static String getJSAPIPay(String prepay_id){
		TreeMap<String, String> sortMap = new TreeMap<String, String>();
		StringBuilder sb = new StringBuilder();
		StringBuilder jsonsb = new StringBuilder();
		//sortMap.put("appId", WXConfig.APPID);
		sortMap.put("timeStamp", System.currentTimeMillis()/1000+"");
		sortMap.put("nonceStr", getRandomCode32());
		sortMap.put("package", "prepay_id="+prepay_id);
		sortMap.put("signType", "MD5");
		//调优后代码
		Iterator it = sortMap.entrySet().iterator();
		jsonsb.append("{");
		while(it.hasNext()){
			Entry<String, Object> entry = (Entry<String, Object>) it.next();
			sb.append(entry.getKey());
			jsonsb.append("\"");
			jsonsb.append(entry.getKey());
			jsonsb.append("\"");
			sb.append("=");
			jsonsb.append(":");
			sb.append(entry.getValue());
			jsonsb.append("\"");
			jsonsb.append(entry.getValue());
			jsonsb.append("\"");
			sb.append("&");
			jsonsb.append(",");
		}
		
		sb.append("key=");
		//sb.append(WXConfig.KEY);
		jsonsb.append("\"paySign\":\"");
		jsonsb.append(enCodeMd5_UTF8(sb.toString()));
		jsonsb.append("\"}");
		return jsonsb.toString();
	}
	
	public static void main(String[] args){
		//Unifiedorder unifiedorder = new Unifiedorder();
		/*unifiedorder.setTotal_fee(9);*/
//		unifiedorder.setAppid("dddd");
//		unifiedorder.setNotify_url("ewwrfds");
//		System.out.println(getSign(unifiedorder));
//		System.out.println(getJSAPIPay("123456"));
	}
}
