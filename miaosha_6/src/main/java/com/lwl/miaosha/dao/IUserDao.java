package com.lwl.miaosha.dao;

import com.lwl.miaosha.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-16
 */
//表明这是持久层
@Mapper
public interface IUserDao {
    @Select("select * from user where id = #{id}")
    public User getUserById(@Param("id") int id);
    @Insert("insert into user(id,name) values(#{id},#{name})")  //id为自增的，所以可以不用设置id
    public void insert(User user);
}
