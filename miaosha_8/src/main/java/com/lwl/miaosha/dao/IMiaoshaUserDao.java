package com.lwl.miaosha.dao;

import com.lwl.miaosha.domain.MiaoshaUser;
import org.apache.ibatis.annotations.*;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-17
 */
@Mapper
public interface IMiaoshaUserDao {
    @Select("select * from miaosha_user where id = #{id}")  //这里#{id}通过后面参数来为其赋值
    MiaoshaUser getById(@Param("id") long id);

    @Update("update miaosha_user set password = #{password} where id = #{id}")
    public void update(MiaoshaUser newUser);

    @Insert("insert into miaosha_user(id, nickname, register_date, salt, password, login_count)values(#{id},#{nickname},#{registerDate},#{salt},#{password},#{loginCount})")
    public void insertUser(MiaoshaUser miaoshaUser);
}
