package com.liangzhicheng.modules.service.impl;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.constant.Constants;
import com.liangzhicheng.common.exception.TransactionException;
import com.liangzhicheng.common.utils.*;
import com.liangzhicheng.modules.controller.client.LoginClientController;
import com.liangzhicheng.modules.dao.ITestUserDao;
import com.liangzhicheng.modules.entity.TestUserEntity;
import com.liangzhicheng.modules.entity.dto.TestAreaDTO;
import com.liangzhicheng.modules.entity.dto.TestLoginPhoneDTO;
import com.liangzhicheng.modules.entity.dto.TestLoginWeChatDTO;
import com.liangzhicheng.modules.entity.dto.TestVcodeDTO;
import com.liangzhicheng.modules.entity.vo.TestUserVO;
import com.liangzhicheng.modules.feign.ITestAreaFeignApi;
import com.liangzhicheng.modules.service.ITestUserService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description 用户服务实现类
 * @author liangzhicheng
 * @since 2021-08-09
 */
@Service
public class TestUserServiceImpl extends ServiceImpl<ITestUserDao, TestUserEntity> implements ITestUserService {

    @Resource
    private ITestAreaFeignApi testAreaFeignApi;

    /**
     * @description 获取短信验证码
     * @param userLoginDTO
     */
    @Override
    public void sendSMS(TestVcodeDTO userLoginDTO) {
        String phone = userLoginDTO.getPhone();
        if(SysToolUtil.isBlank(phone)){
            throw new TransactionException(ApiConstant.PARAM_IS_NULL);
        }
        if(!SysToolUtil.isPhone(phone)){
            throw new TransactionException(ApiConstant.PARAM_PHONE_ERROR);
        }
        String vcode = SysToolUtil.random();
        SysToolUtil.sendSMS(Constants.SMS_TEMPLATE_ID, phone, vcode);
        SysCacheUtil.set(phone, vcode, 5 * 60);
        //测试展示到缓存Map
        SysCacheUtil.hset("SMS_TEST_MAP", phone, vcode);
        SysCacheUtil.expire("SMS_TEST_MAP", 5 * 60);
    }

    /**
     * @description APP手机号码登录
     * @param loginDTO
     * @return TestUserVO
     */
    @Override
    public TestUserVO loginPhone(TestLoginPhoneDTO loginDTO) {
        String phone = loginDTO.getPhone();
        String vcode = loginDTO.getVcode();
        if(SysToolUtil.isBlank(phone, vcode)){
            throw new TransactionException(ApiConstant.PARAM_IS_NULL);
        }
        if(!SysToolUtil.isPhone(phone)){
            throw new TransactionException(ApiConstant.PARAM_PHONE_ERROR);
        }
        String existVcode = (String) SysCacheUtil.get(phone);
        if(SysToolUtil.isBlank(existVcode) || !existVcode.equals(vcode)){
            throw new TransactionException(ApiConstant.PARAM_VCODE_ERROR);
        }
        TestUserEntity user = baseMapper.selectOne(
                new LambdaQueryWrapper<TestUserEntity>().eq(TestUserEntity::getPhone, phone));
        if(SysToolUtil.isNull(user)){
            user = new TestUserEntity();
        }
        user.setPhone(phone);
        saveOrUpdate(user);
        TestUserVO userVO = SysBeanUtil.copyEntity(user, TestUserVO.class);
        //同一台设备登录推送下线通知 (deviceNo:'设备号 必传',appType:'1IOS,ANDROID 必传')
//        XingePush.updateTokenByLogout(user.getUserId(), loginDTO.getDeviceNo(), loginDTO.getAppType());
        //生成json web token
        Date expireTime = SysToolUtil.dateAdd(new Date(), 15);
        String token = SysTokenUtil.createTokenAPP("6688", expireTime);
        SysTokenUtil.updateTokenAPP("6688", token);
        userVO.setTokenAPP(token);
        return userVO;
    }

    /**
     * @description APP授权登录
     * @param loginDTO
     * @return TestUserVO
     */
    @Override
    public TestUserVO loginCodeAPP(TestLoginWeChatDTO loginDTO) {
        String code = loginDTO.getCode();
        /**
         * 1.根据code获取用户信息
         * 2.根据openId判断用户是否存在，存在直接返回用户信息，不存在新增用户信息
         */
        String result = SysWeChatUtil.getUserInfoByCode(code);
        JSONObject userInfo = JSONObject.parseObject(result);
        String openId = userInfo.getString("openid");
        TestUserEntity user = baseMapper.selectOne(
                new LambdaQueryWrapper<TestUserEntity>().eq(TestUserEntity::getOpenId, openId));
        if(SysToolUtil.isNull(user)){
            user = new TestUserEntity();
            user.setOpenId(openId);
        }
        saveOrUpdate(user);
        TestUserVO userVO = SysBeanUtil.copyEntity(user, TestUserVO.class);
        //同一台设备登录推送下线通知(deviceNo:'设备号 必传',appType:'1IOS,2ANDROID 必传')
//        XingePush.updateTokenByLogout(user.getUserId(), loginDTO.getDeviceNo(), loginDTO.getAppType());
        //生成json web token
        Date expireTime = SysToolUtil.dateAdd(new Date(), 1);
        String token = SysTokenUtil.createTokenAPP("6688", expireTime);
        SysTokenUtil.updateTokenAPP("6688", token);
        userVO.setTokenAPP(token);
        return userVO;
    }

    /**
     * @description 小程序授权登录
     * @param loginDTO
     * @return TestUserVO
     */
    @Override
    public TestUserVO loginMINI(TestLoginWeChatDTO loginDTO) {
        String code = loginDTO.getCode();
        String encryptedData = loginDTO.getEncryptedData();
        String iv = loginDTO.getIv();
        if(SysToolUtil.isBlank(code, encryptedData, iv)){
            throw new TransactionException(ApiConstant.PARAM_IS_NULL);
        }
        Map<String, Object> miniMap = Maps.newHashMap();
        miniMap.put("appid", Constants.WECHAT_MINI_APP_ID);
        miniMap.put("secret", Constants.WECHAT_MINI_APP_SECRET);
        miniMap.put("js_code", code);
        miniMap.put("grant_type", Constants.WECHAT_MINI_GRANT_TYPE);
        JSONObject json = JSONObject.parseObject(SysToolUtil.sendPost(Constants.WECHAT_MINI_URL, miniMap));
        String sessionKey = json.getString("session_key");
        JSONObject userInfo = getUserInfo(sessionKey, encryptedData, iv);
        String avatar = userInfo.getString("avatarUrl");
        String nickName = userInfo.getString("nickName");
        String gender = userInfo.getString("gender");
        String country = userInfo.getString("country");
        String province = userInfo.getString("province");
        String city = userInfo.getString("city");
        /**
         * 保存用户信息 save(user);
         * 1.根据微信授权后返回用户信息获取openId
         * 2.根据openId查询用户信息记录是否存在，不存在则新增
         */
        String openId = json.getString("openid");
        TestUserEntity user = baseMapper.selectOne(
                new LambdaQueryWrapper<TestUserEntity>().eq(TestUserEntity::getOpenId, openId));
        if(SysToolUtil.isNull(user)){
            user = new TestUserEntity();
            user.setOpenId(openId);
        }
        //地区处理
        TestAreaDTO areaDTO = new TestAreaDTO(country, province, city);
        List<Map<String, Object>> resultList = testAreaFeignApi.getArea(areaDTO);
        String areaName = "";
        String areaCode = "";
        if(SysToolUtil.listSizeGT(resultList)){
            areaName = (String) resultList.get(0).get("areaName");
            areaCode = ((String) resultList.get(0).get("areaCode")).substring(5);
            user.setCountryName(areaName);
            user.setCountryId(areaCode);
            if(resultList.size() > 1){
                areaName = (String) resultList.get(1).get("areaName");
                areaCode = ((String) resultList.get(1).get("areaCode")).substring(5);
                user.setProvinceName(areaName);
                user.setProvinceId(areaCode);
            }
            if(resultList.size() > 2){
                areaName = (String) resultList.get(2).get("areaName");
                areaCode = ((String) resultList.get(2).get("areaCode")).substring(5);
                user.setCityName(areaName);
                user.setCityId(areaCode);
            }
        }
        saveOrUpdate(user);
        TestUserVO userVO = SysBeanUtil.copyEntity(user, TestUserVO.class);
        //生成json web token
        Date expireTime = SysToolUtil.dateAdd(new Date(), 15);
        String token = SysTokenUtil.createTokenMINI("6688", expireTime);
        SysTokenUtil.updateTokenMINI("6688", token);
        userVO.setTokenMINI(token);
        return userVO;
    }

    /**
     * @description APP退出登录
     * @param request
     */
    @Override
    public void logOutAPP(HttpServletRequest request) {
        String userId = request.getHeader("userId");
        //判断用户是否存在
        // TODO user
        //清除缓存中账号及设备登录类型
        //XingePush.clearTokenByLogout(userId);
        //XingePush.clearTypeByLogout(userId);
        SysTokenUtil.clearTokenAPP(userId);
    }

    /**
     * @description 根据会话密钥、加密数据获取用户信息
     * @param sessionKey
     * @param encryptedData
     * @param iv
     * @return JSONObject
     */
    private JSONObject getUserInfo(String sessionKey, String encryptedData, String iv) {
        //加密秘钥
        byte[] keyB = Base64.decode(sessionKey);
        //被加密的数据
        byte[] dataB = Base64.decode(encryptedData);
        //加密算法初始向量
        byte[] ivB = Base64.decode(iv);
        try {
            int base = 16;//密钥不足16位，补足
            if(keyB.length % base != 0){
                int groups = keyB.length / base + (keyB.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyB, 0, temp, 0, keyB.length);
                keyB = temp;
            }
            //初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding","BC");
            SecretKeySpec spec = new SecretKeySpec(keyB, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivB));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);
            byte[] resultByte = cipher.doFinal(dataB);
            if(resultByte != null && resultByte.length > 0){
                String result = new String(resultByte, "UTF-8");
                return JSON.parseObject(result);
            }
        } catch (NoSuchAlgorithmException e) {
            SysToolUtil.info("NoSuchAlgorithmException : " + e.getMessage(), LoginClientController.class);
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            SysToolUtil.info("NoSuchPaddingException : " + e.getMessage(), LoginClientController.class);
            e.printStackTrace();
        } catch (InvalidParameterSpecException e) {
            SysToolUtil.info("InvalidParameterSpecException : " + e.getMessage(), LoginClientController.class);
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            SysToolUtil.info("IllegalBlockSizeException : " + e.getMessage(), LoginClientController.class);
            e.printStackTrace();
        } catch (BadPaddingException e) {
            SysToolUtil.info("BadPaddingException : " + e.getMessage(), LoginClientController.class);
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            SysToolUtil.info("UnsupportedEncodingException : " + e.getMessage(), LoginClientController.class);
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            SysToolUtil.info("InvalidKeyException : " + e.getMessage(), LoginClientController.class);
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            SysToolUtil.info("InvalidAlgorithmParameterException : " + e.getMessage(), LoginClientController.class);
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            SysToolUtil.info("NoSuchProviderException : " + e.getMessage(), LoginClientController.class);
            e.printStackTrace();
        }
        return null;
    }

}
