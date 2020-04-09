package com.bokecc.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.bokecc.model.User;

import java.util.List;

public interface IuserService {

    List<User> selectAll();

    int deleteOne(Long id);

    User selectById(Long id);

    User selectOne(User user);

    User selectByUserId(String userId);

    int updateOne(User user, Wrapper<User> updateWrapper);

    int insertOne(User user);

}



