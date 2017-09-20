package com.jrmf360.neteaselib.base.utils;

/**
 * Created by mafei on 15/12/8.
 */
public class NoDoubleClickUtils {

	private static long			lastClickTime;

	private final static int	SPACE_TIME	= 800;

	public static void initLastClickTime() {
		lastClickTime = 0;
	}

	public synchronized static boolean isDoubleClick() {
		long currentTime = System.currentTimeMillis();
		boolean isClick2;
		if (currentTime - lastClickTime > SPACE_TIME) {
			isClick2 = false;
		} else {
			isClick2 = true;
		}
		lastClickTime = currentTime;
		return isClick2;
	}
}
