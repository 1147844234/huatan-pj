package com.tanhua.server.interceptor;

import com.tanhua.domain.db.User;
import com.tanhua.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 1. 统一处理token的拦截器
 * 2. @Component 创建对象加入容器
 */
@Component
@Slf4j
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    /**
     * 控制器处理请求之前，先进行身份验证，校验token
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("拦截器中进行统一身份认证---->" + request.getRemoteAddr());

        //1. 获取请求头
        String token = request.getHeader("Authorization");
        //2. 判断
        if (StringUtils.isEmpty(token)) {
            // 401 未授权 错误状态码
            response.setStatus(401);
            return false;
        }
        //3. 根据token获取用户对象
        User user = userService.findUserByToken(token);
        if (user == null) {
            response.setStatus(401);
            log.info("身份验证失败");
            return false;
        }
        //4. 把认证后的用户对象绑定到当前线程对象上
        UserHolder.set(user);

        // 返回true表示放行，会进入控制器方法
        // 返回false表示不放行，不会进入控制器方法
        return true;
    }
}















