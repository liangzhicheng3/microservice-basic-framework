package com.liangzhicheng.config.mvc.interceptor.details;

import com.alibaba.fastjson.JSONObject;
import com.liangzhicheng.common.basic.ResponseResult;
import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.utils.CacheUtil;
import com.liangzhicheng.common.utils.SysToolUtil;
import com.liangzhicheng.config.mvc.interceptor.annotation.PermissionsValidate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

/**
 * @description 权限校验拦截器，凡是方法头部加了注解@PermissionsValidate的controller，执行前都会先执行下面的preHandle()方法
 * @author liangzhicheng
 * @since 2021-08-06
 */
public class PermissionsInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler.getClass().isAssignableFrom(HandlerMethod.class)){
            SysToolUtil.info("PermissionsValidate come start ...");
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            PermissionsValidate permissionsValidate = handlerMethod.getMethodAnnotation(PermissionsValidate.class);
            if(permissionsValidate != null && permissionsValidate.validate() == true){
                String expressionStr = permissionsValidate.expression();
                String accountId = request.getHeader("accountId");
                SysToolUtil.info("request expression string : " + expressionStr);
                SysToolUtil.info("request accountId : " + accountId);
                if(SysToolUtil.isNotBlank(expressionStr, accountId)){
                    Map<String, Object> permMap = CacheUtil.getPermMap();
                    Set<String> perms = (Set<String>) permMap.get(accountId);
                    Integer totalPermNum = perms.size();
                    Integer permNum = totalPermNum;
                    String[] array = expressionStr.split(",");
                    if(SysToolUtil.isNotNull(array) && array.length > 0){
                        for (String expression : array) {
                            for(String permExpression : perms){
                                if(expression.equals(permExpression)){
                                    permNum -= 1;
                                }
                            }
                        }
                    }
                    if(totalPermNum == permNum){
                        render(response);
                        return false;
                    }
                }else{
                    render(response);
                    return false;
                }

            }
        }
        return true;
    }

    /**
     * @description 异常返回参数输出
     * @param response
     */
    private void render(HttpServletResponse response) {
        PrintWriter out = null;
        ResponseResult result = null;
        try {
            result = new ResponseResult(ApiConstant.NO_AUTHORIZATION, ApiConstant.getMessage(ApiConstant.NO_AUTHORIZATION), null);
            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            out = response.getWriter();
            out.println(JSONObject.toJSONString(result));
        } catch (Exception e) {
            SysToolUtil.error("permission JSON异常 : " + e.getMessage());
        }finally{
            if(out != null){
                out.flush();
                out.close();
            }
        }
    }

}
