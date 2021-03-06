package com.liangzhicheng.modules.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.liangzhicheng.common.basic.ResponseResult;
import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.constant.Constants;
import com.liangzhicheng.common.exception.CustomizeException;
import com.liangzhicheng.common.exception.TransactionException;
import com.liangzhicheng.common.pay.alipay.utils.AlipayRefundUtil;
import com.liangzhicheng.common.pay.wechatpay.utils.MD5Util;
import com.liangzhicheng.common.pay.wechatpay.utils.SignUtil;
import com.liangzhicheng.common.pay.wechatpay.utils.WeChatRefundUtil;
import com.liangzhicheng.common.pay.wechatpay.utils.XmlUtil;
import com.liangzhicheng.common.utils.SysBeanUtil;
import com.liangzhicheng.common.utils.SysToolUtil;
import com.liangzhicheng.modules.entity.TestOrderEntity;
import com.liangzhicheng.modules.entity.dto.TestOrderDTO;
import com.liangzhicheng.modules.entity.vo.TestOrderVO;
import com.liangzhicheng.modules.feign.ITestOrderFeignApi;
import com.liangzhicheng.modules.service.IPayService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.w3c.dom.Document;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * @description ?????????????????????
 * @author liangzhicheng
 * @since 2021-08-14
 */
@Service
public class PayServiceImpl implements IPayService {

    @Resource
    private ITestOrderFeignApi testOrderFeignApi;

    /**
     * @description ????????????????????????
     * @param orderDTO
     * @return TestOrderVO
     */
    @Override
    public TestOrderVO testInvokeGetOrder(TestOrderDTO orderDTO) {
        TestOrderEntity order = SysToolUtil.getObjectEntity(
                testOrderFeignApi.getOrder(orderDTO), TestOrderEntity.class);
        if(SysToolUtil.isNull(order)){
            throw new CustomizeException(ApiConstant.BASE_FAIL_CODE, "???????????????????????????????????????");
        }
        SysToolUtil.info("????????????????????????????????????");
        return SysBeanUtil.copyEntity(order, TestOrderVO.class);
    }

    /**
     * @description ?????????????????????
     * @param orderDTO
     * @param request
     * @return SortedMap<String, Object>
     */
    @Override
    public SortedMap<String, Object> weChatMiniPay(TestOrderDTO orderDTO, HttpServletRequest request) {
        TestOrderEntity order = getOrder(orderDTO);
        try{
            /*
             * ?????????????????????????????????
             */
            String nonceStr = MD5Util.encryptString(String.valueOf(
                    new Date().getTime() / 1000), true);
            SortedMap<String, Object> sp = new TreeMap<String, Object>();
            sp.put("appid", Constants.WECHAT_MINI_APP_ID);
            sp.put("mch_id", Constants.WECHAT_MINI_MCH_ID);
            sp.put("nonce_str", nonceStr);
            sp.put("body", "idea show");
            sp.put("out_trade_no", "order.getOrderNo()");
            //int total_fee = (int) (order.getMiniPay() * 100);
            sp.put("total_fee", 1);
            sp.put("spbill_create_ip", request.getRemoteAddr());
            sp.put("notify_url", Constants.MINI_NOTIFY_URL);
            sp.put("trade_type", "JSAPI");
            sp.put("openid", "user.getOpenId()");
            sp.put("sign", SignUtil.signMini(sp, Constants.WECHAT_MINI_APP_SECRET));
            /*
             * ???????????????xml??????
             */
            String xml = SysToolUtil.mapToXml(sp);
            SysToolUtil.info("weChat miniPay mapToXml : " + xml);
            /*
             * ????????????????????????Url
             */
            String responseStr = SysToolUtil.sendPost(Constants.WECHAT_ORDER_URL, xml);
            SysToolUtil.info("weChat miniPay : ???????????????????????????\n" + responseStr);
            Document document = XmlUtil.parseXMLDocument(responseStr);
            String return_code = document.getElementsByTagName("return_code").item(0).getFirstChild().getNodeValue();
            if(SysToolUtil.isNotBlank(return_code) && return_code.equals("SUCCESS")){
                String result_code = document.getElementsByTagName("result_code").item(0).getFirstChild().getNodeValue();
                if(SysToolUtil.isNotBlank(result_code) && result_code.equals("SUCCESS")){
                    String prepay_id = document.getElementsByTagName("prepay_id").item(0).getFirstChild().getNodeValue();
                    SysToolUtil.info("weChat miniPay : ?????????????????????id prepay_id(????????????????????????)??????" + prepay_id);
                    SortedMap<String, Object> map = new TreeMap<String, Object>();
                    map.put("appId", Constants.WECHAT_MINI_APP_ID);
                    map.put("timeStamp", System.currentTimeMillis() / 1000 + "");
//                    map.put("nonceStr", UUID.randomUUID().toString().replace("-", "").substring(10));
                    map.put("nonceStr", nonceStr);
                    map.put("package", "prepay_id=" + prepay_id);
                    map.put("signType", "MD5");
                    map.put("paySign", SignUtil.signMini(map, Constants.WECHAT_MINI_APP_SECRET).toUpperCase());
                    map.put("prepayId", prepay_id);
                    map.put("orderId", "order.getOrderId()");
                    return map;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            SysToolUtil.error("weChat miniPay error : " + e.getMessage());
            throw new CustomizeException(ApiConstant.BASE_FAIL_CODE, "????????????");
        }
        return new TreeMap<String, Object>();
    }

    /**
     * @description ??????APP??????
     * @param orderDTO
     * @param request
     * @return SortedMap<String, Object>
     */
    @Override
    public SortedMap<String, Object> weChatAppPay(TestOrderDTO orderDTO, HttpServletRequest request) {
        TestOrderEntity order = getOrder(orderDTO);
        try{
            /*
             * ?????????????????????????????????
             */
            String nonceStr = MD5Util.encryptString(String.valueOf(
                    new Date().getTime() / 1000), true);
            SortedMap<String, Object> sp = new TreeMap<String, Object>();
            sp.put("appid", Constants.WECHAT_APP_APP_ID);
            sp.put("mch_id", Constants.WECHAT_APP_MCH_ID);
            sp.put("nonce_str", nonceStr);
            sp.put("body", "idea show");
            sp.put("out_trade_no", "order.getOrderNo()");
            //int total_fee = (int) (order.getAppPay() * 100);
            sp.put("total_fee", 1);
            sp.put("spbill_create_ip", request.getRemoteAddr());
            sp.put("notify_url", Constants.APP_NOTIFY_URL);
            sp.put("trade_type", "APP");
            sp.put("sign", SignUtil.signApp(sp, Constants.WECHAT_APP_SECRET));
            /*
             * ???????????????xml??????
             */
            String xml = SysToolUtil.mapToXml(sp);
            SysToolUtil.info("weChat appPay mapToXml : " + xml);
            /*
             * ????????????????????????Url
             */
            String responseStr = SysToolUtil.sendPost(Constants.WECHAT_ORDER_URL, xml);
            SysToolUtil.info("weChat appPay : ???????????????????????????" + responseStr);
            Document document = XmlUtil.parseXMLDocument(responseStr);
            //?????????????????????
            if(document.getElementsByTagName("err_code") != null
                    && document.getElementsByTagName("err_code").item(0) != null
                    && document.getElementsByTagName("err_code").item(0).getFirstChild() != null){
                String err_code = document.getElementsByTagName("err_code").item(0).getFirstChild().getNodeValue();
                if(SysToolUtil.isNotBlank(err_code) && "ORDERPAID".equals(err_code)){
                    throw new CustomizeException(ApiConstant.BASE_FAIL_CODE, "????????????????????????????????????");
                }
            }
            //??????
            String return_code = document.getElementsByTagName("return_code").item(0).getFirstChild().getNodeValue();
            if(SysToolUtil.isNotBlank(return_code) && return_code.equals("SUCCESS")){
                String result_code = document.getElementsByTagName("result_code").item(0).getFirstChild().getNodeValue();
                if(SysToolUtil.isNotBlank(result_code) && result_code.equals("SUCCESS")){
                    String prepay_id = document.getElementsByTagName("prepay_id").item(0).getFirstChild().getNodeValue();
                    SysToolUtil.info("weChat appPay : ?????????????????????id prepay_id(????????????????????????)??????" + prepay_id);
                    //???????????????APP??????
                    SortedMap<String,Object> map = new TreeMap<String,Object>();
                    map.put("appId", Constants.WECHAT_APP_APP_ID);
                    map.put("nonceStr", nonceStr);
                    map.put("package", "Sign=WXPay");
                    map.put("partnerId", Constants.WECHAT_APP_MCH_ID);
                    map.put("prepayId", prepay_id);
                    map.put("timestamp", System.currentTimeMillis() / 1000 + "");
                    map.put("sign", SignUtil.signApp(map, Constants.WECHAT_APP_SECRET));
                    map.put("outTradeNo", sp.get("out_trade_no"));
                    map.put("total", sp.get("total_fee"));
                    return map;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            SysToolUtil.error("weChat appPay error : " + e.getMessage());
            throw new CustomizeException(ApiConstant.BASE_FAIL_CODE, "????????????");
        }
        return new TreeMap<String, Object>();
    }

    /**
     * @description ?????????APP??????
     * @param orderDTO
     * @return String
     */
    @Override
    public String alipayApp(TestOrderDTO orderDTO) {
        TestOrderEntity order = getOrder(orderDTO);
        AlipayClient alipayClient = new DefaultAlipayClient(Constants.ALIPAY_URL, Constants.ALIPAY_APP_APP_ID,
                Constants.ALIPAY_PRIVATE_KEY, Constants.INPUT_FORMAT, Constants.INPUT_CHARSET,
                Constants.ALIPAY_PUBLIC_KEY, Constants.SIGN_TYPE_APP);
        AlipayTradeAppPayRequest req = new AlipayTradeAppPayRequest();
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody("idea show");
        model.setSubject("idea show");
        //model.setOutTradeNo(orderNo);
        model.setTimeoutExpress("30m");//????????????
        //model.setTotalAmount(order.getPayAlipay() + ""); //???????????????
        model.setTotalAmount("0.01");//???????????????
        req.setBizModel(model);
        req.setNotifyUrl(Constants.ALIPAY_APP_NOTIFY_URL);
        try{
            //???????????????????????????????????????????????????sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(req);
            SysToolUtil.info("alipayApp response result : " + response.getBody());
            return response.getBody();
        }catch(AlipayApiException e){
            e.printStackTrace();
            SysToolUtil.error("alipay appPay error : " + e.getMessage());
            throw new CustomizeException(ApiConstant.BASE_FAIL_CODE, "????????????");
        }
    }

    /**
     * @description ??????????????????
     * @param orderDTO
     */
    @Override
    public void weChatRefund(TestOrderDTO orderDTO) {
        TestOrderEntity order = getOrder(orderDTO);
        String result = WeChatRefundUtil.refund(Constants.WECHAT_APP_APP_ID, Constants.WECHAT_APP_MCH_ID, /*order.getOrderNo()*/null,
                System.currentTimeMillis() + "", /*order.getAppPay()*/0, /*order.getAppPay()*/0, Constants.WECHAT_APP_SECRET, Constants.PATH_CERT);
        Document d = XmlUtil.parseXMLDocument(result);
        String return_code = d.getElementsByTagName("return_code").item(0).getFirstChild().getNodeValue();
        if(SysToolUtil.isNotBlank(return_code) && return_code.equals("SUCCESS")){
            String result_code = d.getElementsByTagName("result_code").item(0).getFirstChild().getNodeValue();
            if(SysToolUtil.isNotBlank(result_code) && result_code.equals("SUCCESS")){
                SysToolUtil.info("weChatRefund??????->???????????? ...");
                //??????????????????
                //??????????????????
                //??????????????????
            }else{
                throw new CustomizeException(ApiConstant.BASE_FAIL_CODE, "????????????");
            }
        }
    }

    /**
     * @description ?????????????????????
     * @param orderDTO
     */
    @Override
    public void alipayRefund(TestOrderDTO orderDTO) {
        TestOrderEntity order = getOrder(orderDTO);
        AlipayTradeRefundResponse refundResponse = AlipayRefundUtil.refund(Constants.ALIPAY_URL, Constants.ALIPAY_APP_APP_ID,
                Constants.ALIPAY_PRIVATE_KEY, Constants.INPUT_FORMAT, Constants.INPUT_CHARSET,
                Constants.ALIPAY_PUBLIC_KEY, Constants.SIGN_TYPE_APP/*, order*/);
        if(refundResponse.isSuccess()){
            SysToolUtil.info("alipayRefund??????->???????????? ...");
            //??????????????????
            //??????????????????
            //??????????????????
        }else{
            throw new CustomizeException(ApiConstant.BASE_FAIL_CODE, "????????????");
        }
    }

    /**
     * @description WeChat???????????????????????????
     * @param request
     * @param response
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public synchronized void weChatMiniPayNotify(HttpServletRequest request, HttpServletResponse response) {
        SysToolUtil.info("weChatMiniPayNotify : come start ...");
        try{
            Map<String, String> map = SysToolUtil.xmlToMap(request);
            //SysToolUtil.printMap(map);
            String return_code = map.get("return_code");
            SysToolUtil.info("weChatMiniPayNotify return_code : " + return_code);
            if(SysToolUtil.isNotBlank(return_code) && return_code.equals("SUCCESS")){
                String result_code = map.get("result_code");
                SysToolUtil.info("weChatMiniPayNotify result_code : " + result_code);
                if(SysToolUtil.isNotBlank(result_code) && result_code.equals("SUCCESS")){
                    String out_trade_no = map.get("out_trade_no"); //?????????
                    String transaction_id = map.get("transaction_id"); //???????????????(??????id???????????????id??????????????????id)
                    SysToolUtil.info("weChatMiniPayNotify : ???????????????\n out_trade_no : " + out_trade_no + ", transaction_id : " + transaction_id);
                    if(SysToolUtil.isNotBlank(out_trade_no)) {
                        //?????????????????????????????????????????????out_trade_no??????????????????orderNo
                    }
                }
            }
            response.setHeader("ContentType", "text/xml");
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            PrintWriter out = response.getWriter();
            out.flush();
            out.print(return_code);
            out.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @description WeChat APP??????????????????
     * @param request
     * @param response
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public synchronized void weChatAppPayNotify(HttpServletRequest request, HttpServletResponse response) {
        SysToolUtil.info("weChatAppPayNotify : come start ...");
        try{
            Map<String, String> map = SysToolUtil.xmlToMap(request);
            //SysToolUtil.printMap(map);
            String return_code = map.get("return_code");
            SysToolUtil.info("weChatAppPayNotify return_code : " + return_code);
            if(SysToolUtil.isNotBlank(return_code) && return_code.equals("SUCCESS")){
                String result_code = map.get("result_code");
                SysToolUtil.info("weChatAppPayNotify result_code : " + result_code);
                if(SysToolUtil.isNotBlank(result_code) && result_code.equals("SUCCESS")){
                    String out_trade_no = map.get("out_trade_no");		// ?????????
                    String transaction_id = map.get("transaction_id");	// ????????????????????????id???????????????id??????????????????id???
                    SysToolUtil.info("weChatAppPayNotify : ???????????????\n out_trade_no : " + out_trade_no + ", transaction_id : " + transaction_id);
                    if(SysToolUtil.isNotBlank(out_trade_no)) {
                        //?????????????????????????????????????????????out_trade_no??????????????????orderNo
                    }
                }
            }
            response.setHeader("ContentType", "text/xml");
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            PrintWriter out = response.getWriter();
            out.flush();
            out.print(return_code);
            out.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @description Alipay APP??????????????????
     * @param request
     * @param response
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public synchronized void alipayAppPayNotify(HttpServletRequest request, HttpServletResponse response) {
        SysToolUtil.info("alipayAppPayNotify come start ...");
        //???????????????POST???????????????
        Map<String,String> params = new HashMap<String,String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        SysToolUtil.info("alipayAppPayNotify Iterator params : " + JSONObject.parseObject(params.toString()));
        try {
            boolean flag = AlipaySignature.rsaCheckV1(params, Constants.ALIPAY_PUBLIC_KEY, Constants.INPUT_CHARSET, Constants.SIGN_TYPE_APP);
            if(flag) {
                SysToolUtil.info("????????????");
                String out_trade_no = params.get("out_trade_no"); //APP?????????
                String trade_no = params.get("trade_no"); //??????????????????
                SysToolUtil.info("out_trade_no : " + out_trade_no + " , trade_no : " + trade_no);
                //?????????????????????????????????????????????out_trade_no??????????????????orderNo
            } else {
                SysToolUtil.info("????????????");
            }
            response.getWriter().print(flag);
        } catch (AlipayApiException | IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @description ??????????????????
     * @param orderDTO
     * @return TestOrderEntity
     */
    private TestOrderEntity getOrder(TestOrderDTO orderDTO){
        if(SysToolUtil.isBlank(orderDTO.getId())){
            throw new TransactionException(ApiConstant.PARAM_IS_NULL);
        }
        TestOrderEntity order = SysToolUtil.getObjectEntity(
                testOrderFeignApi.getOrder(orderDTO), TestOrderEntity.class);
        if(SysToolUtil.isNull(order)){
            throw new CustomizeException(ApiConstant.BASE_FAIL_CODE, "???????????????????????????????????????");
        }
        SysToolUtil.info("??????????????????" + order.getId() + "?????????????????????");
        return order;
    }

}
