package com.spring.form.dao;

import java.util.List;

import com.spring.form.model.User;

public interface UserDao {

	User findById(Integer id);

	List<User> findAll();
	
	User findByLoginName(String username);

	void save(User user);

	void update(User user);

	void delete(Integer id);

}