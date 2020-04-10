package com.bokecc.resource;

import com.bokecc.Application;
import com.bokecc.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.apache.http.Header;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.HeaderResultMatchers;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import javax.rmi.PortableRemoteObject;
import java.util.List;

//SpringMVC测试Resource
@RunWith(SpringRunner.class) //表示在测试环境中运行
@SpringBootTest(classes = Application.class)
public class UserResourceTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mvc;
    // private MockHttpSession session;
    @Autowired
    ObjectMapper mapper;

    @Before
    public void setupMockMvc(){
        //初始化MockMvc对象
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void getUser() throws Exception{
        int id = 1;
        String uri = "/api/user/" + id;
        RequestBuilder request = MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void getUsers() throws Exception {
        String uri = "/api/user";
        RequestBuilder request = MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    public void addUser() throws Exception{
        String uri = "/api/user";

        User user = new User();
        user.setUserName("test");
        user.setUserId("1213");
        // jackjson序列化
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE);
        String json = mapper.writeValueAsString(user);

        RequestBuilder request = MockMvcRequestBuilders.post(uri)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json.getBytes());

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());;
    }

    @Test
    @Transactional
    public void updateUser() throws Exception {
        String uri = "/api/user/1";

        User user = new User();
        user.setUserName("sss");
        user.setUserId("1213");
        // jackjson序列化
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE);

        String json = mapper.writeValueAsString(user);

        RequestBuilder request = MockMvcRequestBuilders.put(uri)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());;
    }

    @Test
    @Transactional
    public void deleteUser() throws Exception{
        String uri = "/api/user/1";

        RequestBuilder request = MockMvcRequestBuilders.delete(uri)
                .accept(MediaType.APPLICATION_JSON_UTF8);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());;
    }
}
