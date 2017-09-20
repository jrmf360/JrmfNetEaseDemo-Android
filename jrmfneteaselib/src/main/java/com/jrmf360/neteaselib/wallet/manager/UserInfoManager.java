package com.jrmf360.neteaselib.wallet.manager;

/**
 * @创建人 honglin
 * @创建时间 2017/9/5 上午11:37
 * @类描述 用户信息管理
 */
public class UserInfoManager {

	private static UserInfoManager	instance	= null;

	private String					userId, thirdToken,userName,userIcon;

	private int						authentication = -1;

	private boolean					hasPwd;

    private UserInfoManager() {}

	public static UserInfoManager getInstance() {
		synchronized (UserInfoManager.class) {
			if (instance == null) {
				instance = new UserInfoManager();
			}
		}

		return instance;
	}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getThirdToken() {
        return thirdToken;
    }

    public void setThirdToken(String thirdToken) {
        this.thirdToken = thirdToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public int getAuthentication() {
        return authentication;
    }

    public void setAuthentication(int authentication) {
        if (this.authentication != authentication){
            this.authentication = authentication;
        }
    }

    public boolean isHasPwd() {
        return hasPwd;
    }

    public void setHasPwd(boolean hasPwd) {
        this.hasPwd = hasPwd;
    }

    public void clear(){
        authentication = -1;
        hasPwd = false;
    }
}













