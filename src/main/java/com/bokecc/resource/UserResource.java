package com.bokecc.resource;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.bokecc.model.User;
import com.bokecc.param.UserParam;
import com.bokecc.service.IuserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/user")
@Api(value = "user-resource", produces = MediaType.APPLICATION_JSON)
public class UserResource {

    @Autowired
    private IuserService userService;

    @RequestMapping("/{id}")
    @ApiOperation(value = "查询user", httpMethod = "GET")
    public User getUser(@PathVariable String id){
        System.out.println(("----- selectById ------"));
        try{
            User user = userService.selectById(Long.valueOf(id));
            return user;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping()
    @ApiOperation(value = "查询user列表", httpMethod = "GET")
    public List<User> getUsers(){
        // 返回list
        System.out.println(("----- selectAll ------"));
        List<User> userList = userService.selectAll();
        userList.forEach(System.out::println);
        return userList;
    }

    @PostMapping()
    @ApiOperation(value = "新增user", httpMethod = "POST")
    public Integer addUser(@RequestBody UserParam userparam){
        System.out.println(("----- insertOne ------"));
        log.info("调度接口参数--> {}", JSON.toJSONString(userparam));
        String userid = userparam.getUserid();
        String username = userparam.getUsername();
        User user = new User();
        user.setUserId(userid);
        user.setUserName(username);
        return userService.insertOne(user);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "更新user", httpMethod = "PUT")
    public Integer updateUser(@PathVariable String id,  @RequestBody UserParam userparam){
        System.out.println(("----- updateOne ------"));
        log.info("调度接口参数--> {}", JSON.toJSONString(userparam));

        User user = new User();
        user.setUserName(userparam.getUsername());

        //设置查询条件
        EntityWrapper<User> ew = new EntityWrapper<>();
        ew.where("id", Long.valueOf(id));
        return userService.updateOne(user, ew);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除user", httpMethod = "DELETE")
    public Integer deleteUser(@PathVariable String id){
        System.out.println(("----- deleteOne ------"));
        try {
            Integer success = userService.deleteOne(Long.valueOf(id));
            return success;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
