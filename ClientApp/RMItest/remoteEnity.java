package javaRMI;
import java.io.Serializable;
public class remoteEnity implements Serializable{
	private String msg;
	
	public void setMsg(String m){
		this.msg = m;
	}
	public String getMsg(){
		return this.msg;
	}
}
