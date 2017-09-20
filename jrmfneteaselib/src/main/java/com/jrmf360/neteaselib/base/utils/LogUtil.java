package com.jrmf360.neteaselib.base.utils;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

/**
 * 日志打印
 */
public class LogUtil {

	private static boolean		IS_SHOW_LOG		= false;

	private static final String	DEFAULT_MESSAGE	= "execute";

	private static final String	LINE_SEPARATOR	= System.getProperty("line.separator");

	private static final int	JSON_INDENT		= 4;

	private static final int	V				= 0x1;

	private static final int	D				= 0x2;

	private static final int	I				= 0x3;

	private static final int	W				= 0x4;

	private static final int	E				= 0x5;

	private static final int	A				= 0x6;

	private static final int	JSON			= 0x7;

	private static final int	FILE			= 0x8;

	public static void init(boolean isShowLog) {
		IS_SHOW_LOG = isShowLog;
	}

	public static void v() {
		printLog(V, null, DEFAULT_MESSAGE);
	}

	public static void v(Object msg) {
		printLog(V, null, msg);
	}

	public static void v(String tag, String msg) {
		printLog(V, tag, msg);
	}

	public static void d() {
		printLog(D, null, DEFAULT_MESSAGE);
	}

	public static void d(Object msg) {
		printLog(D, null, msg);
	}

	public static void d(String tag, Object msg) {
		printLog(D, tag, msg);
	}

	public static void i() {
		printLog(I, null, DEFAULT_MESSAGE);
	}

	public static void i(Object msg) {
		printLog(I, null, msg);
	}

	public static void i(String tag, Object msg) {
		printLog(I, tag, msg);
	}

	public static void w() {
		printLog(W, null, DEFAULT_MESSAGE);
	}

	public static void w(Object msg) {
		printLog(W, null, msg);
	}

	public static void w(String tag, Object msg) {
		printLog(W, tag, msg);
	}

	public static void e() {
		printLog(E, null, DEFAULT_MESSAGE);
	}

	public static void e(Object msg) {
		printLog(E, null, msg);
	}

	public static void e(String tag, Object msg) {
		printLog(E, tag, msg);
	}

	public static void a() {
		printLog(A, null, DEFAULT_MESSAGE);
	}

	public static void a(Object msg) {
		printLog(A, null, msg);
	}

	public static void a(String tag, Object msg) {
		printLog(A, tag, msg);
	}

	public static void json(String jsonFormat) {
		printLog(JSON, null, jsonFormat);
	}

	public static void json(String tag, String jsonFormat) {
		printLog(JSON, tag, jsonFormat);
	}

	public static void file(File targetDirectory, Object msg) {
		printFile(null, targetDirectory, null, msg);
	}

	public static void file(String tag, File targetDirectory, Object msg) {
		printFile(tag, targetDirectory, null, msg);
	}

	public static void file(String tag, File targetDirectory, String fileName, Object msg) {
		printFile(tag, targetDirectory, fileName, msg);
	}

	private static void printLog(int type, String tagStr, Object objectMsg) {

		if (!IS_SHOW_LOG) {
			return;
		}

		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		int index = 4;
		String className = stackTrace[index].getFileName();
		String methodName = stackTrace[index].getMethodName();
		int lineNumber = stackTrace[index].getLineNumber();

		String tag = (tagStr == null ? className : tagStr);

		String methodNameShort = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("[ (").append(className).append(":").append(lineNumber).append(")#").append(methodNameShort).append(" ] ");
		String msg = (objectMsg == null) ? "Log with null Object" : objectMsg.toString();

		if (msg != null && type != JSON) {
			stringBuilder.append(msg);
		}

		String logStr = stringBuilder.toString();

		switch (type) {
			case V:
			case D:
			case I:
			case W:
			case E:
			case A:
				segmentPrintLog(type,tag,logStr);
				break;
			case JSON: {
				if (TextUtils.isEmpty(msg)) {
					Log.e(tag, "Empty or Null json content");
					return;
				}
				printJson(tag, msg, logStr);
			}
				break;
		}

	}

	private static void printLog(int type, String tag, String logStr) {
		switch (type) {
			case V:
				Log.v(tag, logStr);
				break;
			case D:
				Log.d(tag, logStr);
				break;
			case I:
				Log.i(tag, logStr);
				break;
			case W:
				Log.w(tag, logStr);
				break;
			case E:
				Log.e(tag, logStr);
				break;
			case A:
				Log.wtf(tag, logStr);
				break;
		}
	}

	private static void printJson(String tag, String msg, String logStr) {

		String message = null;

		try {
			if (msg.startsWith("{")) {
				JSONObject jsonObject = new JSONObject(msg);
				message = jsonObject.toString(JSON_INDENT);
			} else if (msg.startsWith("[")) {
				JSONArray jsonArray = new JSONArray(msg);
				message = jsonArray.toString(JSON_INDENT);
			}
		} catch (JSONException e) {
			e(tag, e.getCause().getMessage() + "\n" + msg);
			return;
		}

		printLine(tag, true);
		message = logStr + LINE_SEPARATOR + message;
		String[] lines = message.split(LINE_SEPARATOR);
		StringBuilder jsonContent = new StringBuilder();
		for (String line : lines) {
			jsonContent.append("║ ").append(line).append(LINE_SEPARATOR);
		}
		Log.d(tag, jsonContent.toString());
		printLine(tag, false);
	}

	private static void printFile(String tag, File targetDirectory, String fileName, Object objectMsg) {

		if (!IS_SHOW_LOG) {
			return;
		}

		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

		int index = 4;
		String className = stackTrace[index].getFileName();
		String methodName = stackTrace[index].getMethodName();
		int lineNumber = stackTrace[index].getLineNumber();

		tag = (tag == null ? className : tag);

		String methodNameShort = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("[ (").append(className).append(":").append(lineNumber).append(")#").append(methodNameShort).append(" ] ");
		String msg = (objectMsg == null) ? "Log with null Object" : objectMsg.toString();

		String headString = stringBuilder.toString();

		if (msg != null) {
			msg = headString + msg;
		}

		fileName = (fileName == null) ? FileHelper.getFileName() : fileName;
		if (FileHelper.save(targetDirectory, fileName, msg)) {
			Log.d(tag, headString + " save log jrmf_b_success ! location is >>>" + targetDirectory.getAbsolutePath() + "/" + fileName);
		} else {
			Log.e(tag, headString + "save log fails !");
		}
	}

	private static void printLine(String tag, boolean isTop) {
		if (isTop) {
			Log.d(tag, "╔═══════════════════════════════════════════════════════════════════════════════════════");
		} else {
			Log.d(tag, "╚═══════════════════════════════════════════════════════════════════════════════════════");
		}
	}

	/**
	 * 分段输出日志
	 *
	 * @param msg
	 */
	private static void segmentPrintLog(int type,String tag, String msg) {
		if (StringUtil.isEmpty(tag) || StringUtil.isEmpty(msg)){
			return;
		}
		int segmentSize = 3 * 1024;
		long length = msg.length();
		if (length <= segmentSize) {// 长度小于等于限制直接打印
			printLog(type, tag, msg);

		} else {
			while (msg.length() > segmentSize) {// 循环分段打印日志
				String logContent = msg.substring(0, segmentSize);
				msg = msg.replace(logContent, "");
				printLog(type, tag, logContent);
			}
			if (StringUtil.isNotEmpty(msg)){
				printLog(type, tag, msg);// 打印剩余日志
			}
		}
	}
}
