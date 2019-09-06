package com.ran.demo.dubboproducer;

import com.alibaba.dubbo.config.annotation.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.stereotype.Component;

@Service //这里是dubbo的注解, 将接口暴露在外, 可以供调用, 这里就是放在了注册中心
//@org.springframework.stereotype.Service   spring自己的注解, 为了区分, 使用了@Component
@Component //让spring扫描该类
public class UserServiceImpl implements UserService{

    private static Map<Integer, User> userMap = new HashMap<>();

    /**
     * 模拟从数据库查询数据
     */
    static {
        User u1 = new User();
        u1.setId(1);
        u1.setName("u1");
        u1.setAge(10);
        userMap.put(u1.getId(), u1);

        User u2 = new User();
        u2.setId(2);
        u2.setName("u2");
        u2.setAge(20);
        userMap.put(u2.getId(), u2);

        User u3 = new User();
        u3.setId(3);
        u3.setName("u3");
        u3.setAge(30);
        userMap.put(u3.getId(), u3);
    }

    @Override
    public User getUserById(int id) {
        User u = userMap.get(id);
        return u;
    }

    @Override
    public User getUserByUser(User user) {
        User u = null;
        for (Entry<Integer, User> en: userMap.entrySet()) {
            User tmp = en.getValue();
            if (user.getName().equals(tmp.getName())) {
                u = tmp;
                break;
            }
        }
        return u;
    }
}
