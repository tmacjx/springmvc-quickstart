package com.bokecc.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.bokecc.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<User> {

    User selectByUserId(String userid);

//    @Override
//    List<User> selectList(Wrapper<User> wrapper);
//
//    @Override
//    User selectById(Serializable serializable);
//
//    @Override
//    Integer updateById(User user);
//
//    @Override
//    Integer update(User user, Wrapper<User> updateWrapper);
//
//    @Override
//    Integer deleteById(Serializable id);
//
//    @Override
//    Integer insert(User user);

//
//    @Override
//    List<User> selectPage(RowBounds rowBounds, Wrapper<User> wrapper);

}
