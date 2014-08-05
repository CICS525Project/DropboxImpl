/**
 * 
 */
package authentication;

import java.io.Serializable;

/**
 * Object Class for User Information
 * @author Jitin
 *
 */
public class UserInfo implements Serializable {
	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = 1L;
private String userName;
private String password;
/**
 * @return the userName
 */
public String getUserName() {
	return userName;
}
/**
 * @param userName the userName to set
 */
public void setUserName(String userName) {
	this.userName = userName;
}
/**
 * @return the password
 */
public String getPassword() {
	return password;
}
/**
 * @param password the password to set
 */
public void setPassword(String password) {
	this.password = password;
}

}
