package com.jrmf360.neteaselib.base.utils;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import android.text.TextUtils;

/**
 * Created by Administrator on 2016/3/1.
 */
public class StringUtil {

	/**
	 * 判断字符串为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (str != null && str.trim().length() > 0) {
			return false;
		}
		return true;
	}

	/**
	 * 判断字符串非空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(String str) {
		if (str != null && str.trim().length() > 0) {
			return true;
		}
		return false;
	}

	public static boolean isNotEmptyAndNull(String str) {
		if (str != null && str.trim().length() > 0 && !"null".equals(str)) {
			return true;
		}
		return false;
	}

	public static boolean isEmptyOrNull(String str) {
		if (str == null || "null".equals(str) || str.length() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 校验银行卡卡号
	 *
	 * @param cardId
	 * @return
	 */
	public static boolean checkBankCard(String cardId) {
		if (TextUtils.isEmpty(cardId)) {
			return false;
		}
		char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
		if (bit == 'N') {
			return false;
		}
		return cardId.charAt(cardId.length() - 1) == bit;
	}

	/**
	 * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
	 *
	 * @param nonCheckCodeCardId
	 * @return
	 */
	public static char getBankCardCheckCode(String nonCheckCodeCardId) {
		if (nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0 || !nonCheckCodeCardId.matches("\\d+")) {
			// 如果传的不是数据返回N
			return 'N';
		}
		char[] chs = nonCheckCodeCardId.trim().toCharArray();
		int luhmSum = 0;
		for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
			int k = chs[i] - '0';
			if (j % 2 == 0) {
				k *= 2;
				k = k / 10 + k % 10;
			}
			luhmSum += k;
		}
		return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
	}

	/**
	 * 校验身份证
	 *
	 * @param idCard
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isIDCard(String idCard) {
		if (isEmpty(idCard)) {
			return false;
		} else {
			return (idCard.length() == 15 || idCard.length() == 18);
		}
	}

	/**
	 * 校验数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {
		if (!StringUtil.isEmpty(str)) {
			return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
		}
		return false;
	}

	/**
	 * 替换手机号
	 *
	 * @return
	 */
	public static String phoneNumReplace(String phoneNum) {
		if (isEmpty(phoneNum)) {
			return "";
		}
		if (!isMobile(phoneNum)) {
			return phoneNum;
		}
		StringBuffer sb = new StringBuffer(phoneNum);
		sb.replace(3, 7, "****");
		return sb.toString();
	}

	/**
	 * 格式化金额为保留两位小数
	 * 
	 * @param money
	 * @return
	 */
	public static String formatMoney(double money) {
		BigDecimal zonggong = new BigDecimal(money);
		return zonggong.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
	}

	/**
	 * 格式化金额为保留两位小数
	 *
	 * @param money
	 * @return
	 */
	public static String formatMoney(float money) {
		String strMoney = String.valueOf(money);
		BigDecimal zonggong = new BigDecimal(strMoney);
		return zonggong.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
	}

	/**
	 * 格式化金额为两位小数
	 * 
	 * @param money
	 * @return
	 */
	public static String formatMoney(String money) {
		if (!isNotEmptyAndNull(money)) {
			return "";
		} else {
			BigDecimal amount = new BigDecimal(money.trim());
			return amount.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
		}
	}

	/**
	 * 格式化字符串为float
	 * @param money
	 * @return
	 */
	public static float formatMoneyFloat(String money) {
		if (!isNotEmptyAndNull(money)){
			return 0.00f;
		}else{
			BigDecimal zonggong = new BigDecimal(money);
			return zonggong.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		}
	}

	/**
	 * 格式化字符串为float
	 * @param money
	 * @return
	 */
	public static double formatMoneyDouble(String money) {
		if (!isNotEmptyAndNull(money)){
			return 0.00;
		}else{
			BigDecimal zonggong = new BigDecimal(money);
			return zonggong.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
	}


	/**
	 * 字符串转double
	 * 
	 * @param num
	 * @return
	 */
	public static Double string2double(String num) {
		Double d = null;
		if (isEmptyOrNull(num)) {
			return 0.00;
		}
		try {
			d = Double.valueOf(num);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return d;
	}

	/**
	 * 校验手机号
	 *
	 * @param mobile
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isMobile(String mobile) {
		String regExp = "^((13[0-9])|(17[0-9])|(15[^4,\\D])|(18[0-9])|(14[0-9]))\\d{8}$";
		return Pattern.matches(regExp, mobile);
	}

	public static String replace(String str) {
		if (str.length() >= 8) {
			int start = 4;
			int end = str.length() - 4;
			StringBuilder builder = new StringBuilder(end - start);
			for (int i = 0; i < end - start; i++) {
				builder = builder.append("*");
			}
			StringBuilder self = new StringBuilder(str);
			return self.replace(start, end, builder.toString()).toString();
		} else if (str.length() == 0) {
			return str;
		} else {
			int start = 2;
			int end = str.length() - 2;
			StringBuilder builder = new StringBuilder(end - start);
			for (int i = 0; i < end - start; i++) {
				builder = builder.append("*");
			}
			StringBuilder self = new StringBuilder(str);
			return self.replace(start, end, builder.toString()).toString();
		}
	}

	/**
	 * 根据用户名的不同长度，来进行替换 ，达到保密效果
	 *
	 * @param userName
	 *            用户名
	 * @return 替换后的用户名
	 */
	public static String nameReplace(String userName) {
		if (isEmpty(userName)) {
			userName = "";
		}

		int nameLength = userName.length();
		StringBuilder sb = new StringBuilder();

		if (nameLength <= 1) {
			sb.append("*");
		} else if (nameLength == 2) {
			sb.append("*").append(userName.charAt(nameLength - 1));
		} else if (nameLength == 3) {
			sb.append("**").append(userName.charAt(nameLength - 1));
		} else {
			sb.append("***").append(userName.charAt(nameLength - 1));
		}
		return sb.toString();
	}

	/**
	 * 替换身份证号
	 * 
	 * @param cardNum
	 * @return
	 */
	public static String idCardReplace(String cardNum) {
		if (isEmpty(cardNum)) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		if (cardNum.length() <= 15) {
			sb.append(cardNum.charAt(0)).append("*************").append(cardNum.charAt(cardNum.length() - 1));
		} else {
			sb.append(cardNum.charAt(0)).append("****************").append(cardNum.charAt(cardNum.length() - 1));
		}
		return sb.toString();
	}

	public static String phoneReplace(String phoneNum) {
		// 手机号隐藏中间四位
		if (isEmptyOrNull(phoneNum)) {
			return phoneNum;
		}
		return phoneNum.replace(phoneNum.substring(3, 7), "****");
	}

	/**
	 * 截取名字长度为10
	 *
	 * @param str
	 */
	public static String getFixUserName(String str) {
		if (!isEmpty(str) && str.length() > 10) {
			return str.substring(0, 10) + "...";
		} else {
			return str;
		}
	}

	public static String ToDBC(String text) {
		if (isEmptyOrNull(text)){
			return "";
		}
		char[] c = text.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {// 全角空格为12288，半角空格为32
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)// 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

}
