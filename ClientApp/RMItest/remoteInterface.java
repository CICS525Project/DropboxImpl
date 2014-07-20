package javaRMI;

import java.rmi.*;
public interface remoteInterface extends Remote{
	public String say() throws RemoteException;
}
