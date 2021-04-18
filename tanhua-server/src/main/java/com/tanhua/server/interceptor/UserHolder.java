package com.tanhua.server.interceptor;

import com.tanhua.domain.db.User;

/**
 * 作用：
 * 1、往当前线程对象上存储用户信息
 * 2、获取绑定到当前线程上的用户对象
 */
public class UserHolder {
    // 创建线程对象
    private static ThreadLocal<User> threadLocal = new ThreadLocal<>();

    // 把用户对象绑定到当前线程对象上
    public static void set(User user) {
        threadLocal.set(user);
    }

    // 从当前线程对象上获取用户
    public static User get(){
        return threadLocal.get();
    }

    // 返回用户id
    public static Long getUserId(){
        return get().getId();
    }
}
