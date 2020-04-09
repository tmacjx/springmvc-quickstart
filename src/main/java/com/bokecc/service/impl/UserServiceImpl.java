package com.bokecc.service.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.bokecc.mapper.UserMapper;
import com.bokecc.model.User;
import com.bokecc.service.IuserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements IuserService {

    @Autowired
    private UserMapper mapper;

    @Override
    public List<User> selectAll() {
        return mapper.selectList(null);
    }

    @Override
    public User selectById(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public User selectOne(User user){
        return mapper.selectOne(user);
    }

    @Override
    public User selectByUserId(String userid){
        return mapper.selectByUserId(userid);
    }

    @Override
    public int updateOne(User user, Wrapper<User> updateWrapper) {
        return mapper.update(user, updateWrapper);
    }

    @Override
    public int insertOne(User user) {
        return mapper.insert(user);
    }

    @Override
    public int deleteOne(Long id) {
        return mapper.deleteById(id);
    }

}
