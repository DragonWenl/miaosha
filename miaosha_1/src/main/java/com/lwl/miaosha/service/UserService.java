package com.lwl.miaosha.service;

import com.lwl.miaosha.dao.IUserDao;
import com.lwl.miaosha.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-16
 */
@Service
public class UserService {
    @Autowired(required = false)
    IUserDao userDao;

    public User getUserById(int id){
        return userDao.getUserById(id);
    }

    //使用事务
    @Transactional
    public boolean tx() {
        User user=new User();
        user.setId(2);
        user.setName("ljs");
        userDao.insert(user);
//        User user1=new User();
//        user1.setId(1);
//        user1.setName("ljs2");
//        userDao.insert(user1);			//这里出问题则回滚
        return true;
    }

}
