package com.liangzhicheng.common.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * @description bean拷贝工具类
 * @author liangzhicheng
 * @since 2021-07-30
 */
public class SysBeanUtil {

    /**
     * @description 拷贝单个entity
     * @param entity
     * @param cls
     * @param <T>
     * @return
     */
    public static<T> T copyEntity(Object entity, Class<T> cls){
        T target = null;
        if(SysToolUtil.isNotNull(entity)){
            target = ReflectUtil.newInstance(cls);
            BeanUtil.copyProperties(entity, target);
        }
        return target;
    }

    /**
     * @description 拷贝整个list，由于hutool只有单个bean的拷贝，没有整个List的拷贝，需要封装一个list的拷贝
     * @param source
     * @param cls
     * @param <T>
     * @return
     */
    public static<T> List<T> copyList(List<?> source, Class<T> cls) {
        if(!SysToolUtil.listSizeGT(source)){
            return Lists.newArrayList();
        }
        List<T> targetList = new ArrayList<>(source.size());
        T target = null;
        for (Object obj : source) {
            if(SysToolUtil.isNotNull(obj)){
                target = ReflectUtil.newInstance(cls);
                BeanUtil.copyProperties(obj, target);
            }
            targetList.add(target);
        }
        return targetList;
    }

}
