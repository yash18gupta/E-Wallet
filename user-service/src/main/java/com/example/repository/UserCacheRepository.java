package com.example.repository;

import com.example.Utils.Constants;
import com.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class UserCacheRepository {

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    public void set(User user){
        if(user.getId()==0){
            return;
        }
        String key = getKey(user.getId());

        redisTemplate.opsForValue().set(key,user,Constants.USER_REDIS_EXPIRE, TimeUnit.SECONDS);
    }

    public User get(int id){
        if(id==0){
            return null;
        }

        String key = getKey(id);

        return (User)redisTemplate.opsForValue().get(key);
    }

    private String getKey(int id){
        return Constants.USER_REDIS_KEY_PREFIX+id;
    }
}
