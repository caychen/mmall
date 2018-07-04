package org.com.cay.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class TokenCache {

    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);

    private static LoadingCache<String, String> localCache =
            CacheBuilder.newBuilder().
                    initialCapacity(1000).//设置缓存的初始化容量
                    maximumSize(10000).//缓存的最大容量，超过最大容量会使用LRU算法
                    expireAfterAccess(30, TimeUnit.MINUTES).//有效期
                    build(new CacheLoader<String, String>() {
                        //默认的数据加载实现，当调用get取值的时候，如果key没有对应的值，就会调用这个方法进行加载
                        @Override
                        public String load(String key) throws Exception {
                            return "null";
                        }
                    });

    public static void setKey(String key, String value) {
        localCache.put(key, value);
    }

    public static String getKey(String key) {
        String value = null;
        try {
            value = localCache.get(key);
            if ("null".equals(value)) {
                return null;
            }
            return value;
        } catch (Exception e) {
            logger.error("localcache get error", e);
        }
        return null;
    }
}
