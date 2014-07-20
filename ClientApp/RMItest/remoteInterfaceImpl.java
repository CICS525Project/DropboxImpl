package javaRMI;

import java.rmi.*;
import java.rmi.server.*;
public class remoteInterfaceImpl extends UnicastRemoteObject implements remoteInterface{


	public remoteInterfaceImpl (String msg) throws RemoteException{
		super();
	}
	@Override
	public String say() throws RemoteException {
		// TODO Auto-generated method stub
		remoteEnity h = new remoteEnity();
		h.setMsg("hello world");
		return h.getMsg();
	}

}
