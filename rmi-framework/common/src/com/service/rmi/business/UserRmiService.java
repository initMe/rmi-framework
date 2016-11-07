package com.service.rmi.business;

import java.rmi.RemoteException;

import com.bean.business.User;
import com.service.rmi.tools.RemoteBaseService;

public interface UserRmiService extends RemoteBaseService {
	public User getUserById(Long userId) throws RemoteException;
}
