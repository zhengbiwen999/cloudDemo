package com.zbw.controller;
import com.alibaba.fastjson.JSONObject;
import com.zbw.mapper.AreaEntityMapper;
import com.zbw.model.AreaEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
@RestController
@RefreshScope
public class DbTestController {
    @Autowired
    AreaEntityMapper areaEntityMapper;
    @RequestMapping("/getData")
    public String getData(HttpServletRequest request,
                          @RequestParam(value = "xxx", required = false) String xxx) {
        String usernam = request.getParameter("xxx");

        Object content = request.getServletContext().getAttribute("content");
        System.out.println("content:"+content.toString());
        System.out.println("xxx:" + xxx);
        System.out.println("username:" + usernam);
        List<AreaEntity> areaEntities = areaEntityMapper.selectAll();
        System.out.println(areaEntities.size());
        return "success";
    }
    public static void main(String[] args) {
        String str = "{\"p1\":\"111\",\"p2\":\"222\",\"p3\":\"333\"}";
        //{"p1":"111","p2":"222","p3":"333"}
        JSONObject jsonObject = JSONObject.parseObject(str);

        for(String key : jsonObject.keySet()){
            System.out.println("value值是："+jsonObject.getString(key));
        }

        Map<String, String> map = new HashMap<>();
        map.put("a", "1");
        map.put("b", "2");
        map.put("c", "3");
        for (String key : map.keySet()) {
            String value = map.get(key);
            System.out.println("key:" + key + " value:" + value);
        }
    }
}


