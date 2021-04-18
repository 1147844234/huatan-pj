package com.tanhua.server.test.cache;

import com.tanhua.domain.db.UserInfo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserInfoTestService {

    /**
     * 缓存测试方法,测试流程：
     * 1、方法上不加注解执行2次，发现没有都从数据库查询（模拟）
     * 2、加上注解 @Cacheable(value = "users")
     * 第一次执行：从数据库获取数据
     * 第二次执行：从缓存获取数据
     */
    @Cacheable(value = "users")
    public List<UserInfo> findAll(){
        System.out.println("从数据库中查询数据：");
        List<UserInfo> list = new ArrayList<>();
        for (int i = 1; i <=5 ; i++) {
            UserInfo userInfo = new UserInfo();
            userInfo.setNickname("葫芦娃兄弟"+i);
            userInfo.setCity("gz");
            list.add(userInfo);
        }
        return list;
    }
    /**
     * 保存数据时候，需要删除缓存中数据
     * @CacheEvict
     *   allEntries: true表示清除缓存中的所有元素
     *   value 指定要清除的缓存的key
     */
    @CacheEvict(value = "users",allEntries = true)
    public void update(){
        System.out.println("更新数据!");
    }

    /**
     * @Cacheable(value = "user",key = "#p0")
     *   value 指定缓存目录名称
     *   key   指定缓存目录下的key, 支持spel表达式
     *         #userId 中的userId对应方法形参名称
     *         #p0 对应方法的第一个参数
     *
     */
    @Cacheable(value = "user",key = "#userId")
    public UserInfo findById(Long userId){
        System.out.println(" 查询数据库，根据id查询：" );
        UserInfo userInfo = new UserInfo();
        userInfo.setNickname("葫芦娃兄弟");
        userInfo.setCity("gz");
        return userInfo;
    }

    @CacheEvict(value = "user",key = "#userInfo.id")
    public void save(UserInfo userInfo){
        System.out.println(" save() 清空缓存数据....");
    }
}
