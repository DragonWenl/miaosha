package com.lwl.miaosha.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-16
 */
@Service
public class RedisService {
    @Autowired
    JedisPool jedisPool;

    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz) {
        Jedis jedis = null;
        //在JedisPool里面取得Jedis
        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            String str = jedis.get(realKey);
            T t = stringToBean(str, clazz);
            return t;
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    public <T> boolean set(KeyPrefix prefix, String key, T val) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String str = beanToString(val);
            if (str == null || str.length() == 0)
                return false;
            //真正的key
            String realKey = prefix.getPrefix() + key;
            int seconds = prefix.expireSeconds();
            if (seconds <= 0) { //永不失效
                jedis.set(realKey, str);
            } else {  //设置了失效时间
                jedis.setex(realKey, seconds, str);
            }
            return true;
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    //删除操作
    public <T> boolean delete(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //真正的key
            String realKey = prefix.getPrefix() + key;
            Long res = jedis.del(realKey); //视频里讲的是key
            return res > 0;
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    public  <T> String beanToString(T val) {
        if (val == null) return null;
        Class<?> clazz = val.getClass();
        if (clazz == int.class || clazz == Integer.class) {
            return val + "";
        } else if (clazz == long.class || clazz == Long.class) {
            return val + "";
        } else if (clazz == String.class) {
            return (String) val;
        } else {
            return JSON.toJSONString(val);
        }
    }

    public <T> T stringToBean(String str, Class<T> clazz) {
        if (str == null || str.length() == 0)
            return null;
        if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(str);
        } else if (clazz == long.class || clazz == Long.class) {
            return (T) Long.valueOf(str);
        } else if (clazz == String.class) {
            return (T) str;
        } else {
            return JSON.toJavaObject(JSON.parseObject(str), clazz);
        }
    }

    /**
     * 检查key是否存在
     *
     * @param prefix
     * @param key
     * @return
     */
    public <T> boolean exitsKey(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            //获取jedis
            jedis = jedisPool.getResource();
            //真正的key
            String realKey = prefix.getPrefix() + key;
            return jedis.exists(realKey);
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    /**
     * 减少值
     *
     * @param prefix
     * @param key
     * @return
     */
    public <T> Long decr(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.decr(realKey);
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    /**
     * 增加值
     *
     * @param prefix
     * @param key
     * @return
     */
    public <T> Long incr(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.incr(realKey);
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    //删除
    public boolean delete(KeyPrefix prefix) {
        if(prefix == null) {
            return false;
        }
        List<String> keys = scanKeys(prefix.getPrefix());
        if(keys==null || keys.size() <= 0) {
            return true;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(keys.toArray(new String[0]));
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    public List<String> scanKeys(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            List<String> keys = new ArrayList<String>();
            String cursor = "0";
            ScanParams sp = new ScanParams();
            sp.match("*"+key+"*");
            sp.count(100);
            do{
                ScanResult<String> ret = jedis.scan(cursor, sp);
                List<String> result = ret.getResult();
                if(result!=null && result.size() > 0){
                    keys.addAll(result);
                }
                //再处理cursor
                cursor = ret.getStringCursor();
            }while(!cursor.equals("0"));
            return keys;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
