package com.bokecc.service;

import com.bokecc.Application;
import com.bokecc.model.User;
import com.bokecc.service.impl.UserServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class IuserServiceTest {

    @Autowired
    private IuserService userService;

    @Test
    public void selectAll() {
    }

    @Test
    public void selectById() {
        long id = 1;
        User user = userService.selectById(id);
        Assert.assertEquals((long) user.getId(), id);
    }

    @Test
    public  void updateOne() {
    }

    @Test
    public void insertOne() {
    }

    @Test
    public void deleteOne() {
    }

}

