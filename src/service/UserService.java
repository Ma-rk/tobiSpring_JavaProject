package service;

import entity.UserEntity;

public interface UserService {

	void add(UserEntity user);

	void upgradeLevelOfEveryUser();
}