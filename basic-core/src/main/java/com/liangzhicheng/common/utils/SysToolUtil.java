package com.liangzhicheng.common.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.liangzhicheng.common.basic.ResponseResult;
import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.constant.Constants;
import com.liangzhicheng.common.exception.TransactionException;
import com.liangzhicheng.config.http.HttpConnectionManager;
import com.liangzhicheng.config.http.HttpDeleteRequest;
import com.liangzhicheng.config.io.IOStreamReader;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.BigDecimal;
import java.net.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description 【基础模块】常用工具类
 * @author liangzhicheng
 * @since 2021-07-30
 */
public class SysToolUtil {

    /**
     * @description 生成id
     * @param
     * @return
     */
    public static String generateId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * @description 生成用户昵称
     * @return String
     */
    public static String generateNickname() {
        String nickname = "";
        Random random = new Random();
        //参数length，表示生成几位随机数
        for (int i = 0; i < 8; i++) {
            String str = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出字母还是数字
            if ("char".equalsIgnoreCase(str)) {
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                nickname += (char) (random.nextInt(26) + temp);
            } else if ("num".equalsIgnoreCase(str)) {
                nickname += String.valueOf(random.nextInt(10));
            }
        }
        return "用户_" + nickname;
    }

    /**
     * @description 获取访问地址前缀
     * @param request
     * @return String
     */
    public static String getAccessUrlPrefix(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    /**
     * @description 获取真实的ip地址
     * @param request
     * @return String
     */
    public static String getAccessUrl(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if(isNotBlank(ip)){
            //多次反向代理后会有多个IP值，第一个IP才是真实IP
            int index = ip.indexOf(",");
            if(index != -1){
                return ip.substring(0, index);
            }else{
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if(isNotBlank(ip)){
            return ip;
        }
        return request.getRemoteAddr();
    }

    /**
     * @description 文件上传
     * @param file
     * @param dir
     * @return String
     */
    public static String uploadFile(MultipartFile file, String dir){
        try{
            if(StrUtil.isBlank(dir)){
                dir = "/upload";
            }
            String saveDir = getProjectPath() + dir;
            String newFileName = file.getOriginalFilename();
            String suffix = "";
            if(file.getOriginalFilename().lastIndexOf(".") != -1){
                newFileName = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
                suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            }
            while(FileUtil.exist(saveDir + "/" + newFileName + suffix)){
                newFileName = newFileName + "-1";
            }
            File parent = new File(saveDir);
            parent.mkdirs();
            File newFile = new File(saveDir, newFileName + suffix);
            newFile.createNewFile();
            file.transferTo(newFile);
            return dir + "/" + newFileName + suffix;
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @description html转pdf
     * @param srcPath html路径，可以是硬盘上的路径，也可以是网络路径
     * @param targetPath pdf保存路径
     * @return boolean
     */
    public static boolean convert(String srcPath, String targetPath) {
        File file = new File(targetPath);
        File parent = file.getParentFile();
        //如果pdf保存路径不存在，则创建路径
        if (!parent.exists()){
            parent.mkdirs();
        }
        StringBuilder cmd = new StringBuilder();
        cmd.append(Constants.UTIL_PDF_PATH); //wkhtmltopdf在系统中的路径
        cmd.append(" ");
        cmd.append(srcPath);
        cmd.append(" ");
        cmd.append(targetPath);
        boolean result = true;
        try {
            Process proc = Runtime.getRuntime().exec(cmd.toString());
            IOStreamReader error = new IOStreamReader(proc.getErrorStream());
            IOStreamReader output = new IOStreamReader(proc.getInputStream());
            error.start();
            output.start();
            proc.waitFor();
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }

//    public static void main(String[] args) {
//        String targetPath = Constants.UTIL_PDF_PREFIX + generateId() + ".pdf";
//        convert("http://www.baidu.com/", targetPath);
//        String key = "名称" + "-" + localDateTimeToString(LocalDateTime.now(), null);
//        SysQiniuUtil.upload(targetPath, key);
//    }

    /**
     * @description 判断String参数是否为空，参数数量可变
     * @param strs
     * @return boolean
     */
    public static boolean isBlank(String ... strs){
        for(String s : strs){
            if(StringUtils.isBlank(s)){
                return true;
            }
        }
        return false;
    }

    /**
     * @description 判断多个参数是否为空，参数数量可变
     * @param strs
     * @return boolean
     */
    public static boolean isNotBlank(String ... strs){
        return !isBlank(strs);
    }

    /**
     * @description 判断对象参数是否为空
     * @param value
     * @return boolean
     */
    public static boolean isNull(Object value){
        return Objects.isNull(value);
    }

    /**
     * @description 判断对象参数是否为空
     * @param value
     * @return boolean
     */
    public static boolean isNotNull(Object value){
        return !isNull(value);
    }

    /**
     * @description 判断String参数是否为数字字符
     * @param str
     * @return boolean
     */
    public static boolean isNumber(String str){
        if(isBlank(str)){
            return false;
        }
        if(StringUtils.isNumeric(str)){
            return true;
        }
        return false;
    }

    /**
     * @description 判断String参数是否为数字字符，参数数量可变
     * @param strs
     * @return boolean
     */
    public static boolean isNumber(String ... strs){
        for(String s : strs){
            if(!isNumber(s)){
                return false;
            }
        }
        return true;
    }

    /**
     * @description 判断String参数是否为小数
     * @param str
     * @return boolean
     */
    public static boolean isDouble(String str){
        if(isBlank(str)){
            return false;
        }
        Pattern pattern = Pattern.compile("\\d+\\.\\d+$|-\\d+\\.\\d+$");
        Matcher m = pattern.matcher(str);
        return m.matches();
    }

    /**
     * @description 判断String参数是否为小数，参数数量可变
     * @param strs
     * @return boolean
     */
    public static boolean isDouble(String ... strs){
        for(String s : strs){
            if(!isDouble(s)){
                return false;
            }
        }
        return true;
    }

    /**
     * @description 判断String参数是否是一个正确的手机号码
     * @param phone
     * @return boolean
     */
    public static boolean isPhone(String phone) {
        if(isBlank(phone) || phone.length() != 11){
            return false;
        }
        Pattern p = Pattern.compile("^((13[0-9])|(14[0-9])|(17[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    /**
     * @description 判断是否邮箱格式
     * @param email
     * @return boolean
     */
    public static boolean isEmail(String email) {
        Pattern emailPattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher matcher = emailPattern.matcher(email);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    /**
     * @description 判断String参数是否中文，是返回true
     * @param str
     * @return boolean
     */
    public static boolean isChinese(String str) {
        if(isNotBlank(str)){
            char[] arr = str.toCharArray();
            if(arr != null && arr.length > 0){
                for(char c : arr){
                    Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
                    if (ub != Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                            && ub != Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                            && ub != Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                            && ub != Character.UnicodeBlock.GENERAL_PUNCTUATION
                            && ub != Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                            && ub != Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * @description 向上取整
     * @param num
     * @param size
     * @return int
     */
    public static int upCeil(int num, int size) {
        if (size == 0) {
            return 0;
        }
        int n1 = num / size;
        double n2 = num % size;
        if (n2 > 0) {
            n1 += 1;
        }
        return n1;
    }

    /**
     * @description 向下取整
     * @param num
     * @param size
     * @return int
     */
    public static int downCeil(int num, int size){
        if(size == 0){
            return 0;
        }
        int n1 = num / size;
        Math.floor(n1);
        return n1;
    }

    /**
     * @description 判断String参数是否存在可变参数中，如果不存在返回true
     * @param str
     * @param strs
     * @return boolean
     */
    public static boolean notIn(String str, String ... strs){
        for(String s : strs){
            if(s.equals(str)){
                return false;
            }
        }
        return true;
    }

    /**
     * @description 判断String参数是否存在可变参数中，如果存在返回true
     * @param str
     * @param strs
     * @return boolean
     */
    public static boolean in(String str, String ... strs){
        for(String s : strs){
            if(s.equals(str)){
                return true;
            }
        }
        return false;
    }

    /**
     * @description 判断String可变参数中是否存在，存在返回true
     * @param strs
     * @return boolean
     */
    public static boolean inOneByNotBlank(String ... strs) {
        for (String s : strs) {
            if(isNotBlank(s)){
                return true;
            }
        }
        return false;
    }

    /**
     * @description 精确计算两个数相除，v1除以v2(Integer类型)
     * @param v1
     * @param v2
     * @return Double
     */
    public static Double divide(Integer v1, Integer v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        BigDecimal bd = b1.divide(b2, 10, BigDecimal.ROUND_HALF_UP);
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    /**
     * @description 精确计算两个数相除，v1除以v2(String类型)
     * @param v1
     * @param v2
     * @return Double
     */
    public static Double divide(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        BigDecimal bd = b1.divide(b2, 10, BigDecimal.ROUND_HALF_UP);
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    /**
     * @description 精确计算两个数相乘，v1乘以v2(Double类型)
     * @param v1
     * @param v2
     * @return Double
     */
    public static Double divide(Double v1, Double v2) {
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        BigDecimal bd = b1.divide(b2, 10, BigDecimal.ROUND_HALF_UP);
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    /**
     * @description 精确计算两个数相乘，v1乘以v2(Integer类型)
     * @param v1
     * @param v2
     * @return Integer
     */
    public static Integer multiply(Integer v1, Integer v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        BigDecimal bd = b1.multiply(b2);
        return bd.intValue();
    }

    /**
     * @description 精确计算两个数相乘，v1乘以v2(String类型)
     * @param v1
     * @param v2
     * @return Integer
     */
    public static Integer multiply(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        BigDecimal bd = b1.multiply(b2);
        return bd.intValue();
    }

    /**
     * @description 精确计算两个数相乘，v1乘以v2(Double类型)
     * @param v1
     * @param v2
     * @return Double
     */
    public static Double multiply(Integer v1, Double v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        BigDecimal bd = b1.multiply(b2).setScale(2, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    /**
     * @description 精确计算两个数相乘，v1乘以v2(Double类型)
     * @param v1
     * @param v2
     * @return Double
     */
    public static Double multiply(Double v1, Double v2) {
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        BigDecimal bd = b1.multiply(b2).setScale(2, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    /**
     * @description 将转化得到的String不是科学计数法，如：4.3062319920812602E17->43062319920812602.00
     * @param d
     * @param pattern
     * @return String
     */
    public static String double2String(Double d, String pattern){
        if(d != null){
            if(isBlank(pattern)){
                pattern = "0.00";
            }
            DecimalFormat df = new DecimalFormat(pattern);
            return df.format(d);
        }
        return null;
    }

    /**
     * @description Double金额转String字符串(例如：超过万转成1.00万)
     * @param num
     * @return String
     */
    public static String double2String(Double num) {
        String str = "";
        if (num >= 10000 && num < 1000000) {
            num = num * 0.0001;
            DecimalFormat df = new DecimalFormat("######0.0");
            str = df.format(num);
            str += "万";
        } else if (num >= 1000000 && num < 10000000) {
            num = num * 0.000001;
            DecimalFormat df = new DecimalFormat("######0.0");
            str = df.format(num);
            str += "百万";
        } else if (num >= 10000000) {
            num = num * 0.0000001;
            DecimalFormat df = new DecimalFormat("######0.0");
            str = df.format(num);
            str += "千万";
        } else {
            str = Double.toString(num);
        }
        return str;
    }

    /**
     * @description 随机生成6位数的字符串
     * @param
     * @return String
     */
    public static String random(){
        int num = (int)((Math.random() * 9 + 1) * 100000);
        return String.valueOf(num);
    }

    /**
     * @description 随机生成一个16进制颜色
     * @return String
     */
    public static String makeColor(){
        String r, g, b;
        Random random = new Random();
        r = Integer.toHexString(random.nextInt(256)).toUpperCase();
        g = Integer.toHexString(random.nextInt(256)).toUpperCase();
        b = Integer.toHexString(random.nextInt(256)).toUpperCase();
        r = r.length() == 1 ? "0" + r : r ;
        g = g.length() == 1 ? "0" + g : g ;
        b = b.length() == 1 ? "0" + b : b ;
        String color = r + g + b;
        return color;
    }

    /**
     * @description 返回将number补0，长度为length位后的字符
     * @param number 要补0的数字
     * @param length 补0后的长度
     * @return String
     */
    public static String toLength(int number, int length){
        return String.format("%0" + length + "d", number);
    }

    /**
     * @description 判断字符串长度，中文为2，字母为1
     * @param value
     * @return int
     */
    public static int stringToLength(String value) {
        int valueLength = 0;
        String chinese = "[\u4e00-\u9fa5]";
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            if (temp.matches(chinese)) {
                valueLength += 2;
            } else {
                valueLength += 1;
            }
        }
        return valueLength;
    }

    /**
     * @param date
     * @param format 传null默认为 yyyy-MM-dd HH:mm:ss
     * @return String
     * @description Date日期格式化成String字符串
     */
    public static String dateToString(Date date, String format) {
        SimpleDateFormat sf = null;
        if (isBlank(format)) {
            sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        } else {
            sf = new SimpleDateFormat(format);
        }
        return sf.format(date);
    }

    /**
     * @param str
     * @param format
     * @return Date
     * @throws ParseException Date
     * @description String字符串格式化成Date日期
     */
    public static Date stringToDate(String str, String format) {
        if (isBlank(str)) {
            return null;
        }
        if (isBlank(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sf.parse(str);
        } catch (ParseException e) {
            throw new TransactionException(ApiConstant.PARAM_DATE_ERROR);
        }
        return date;
    }

    /**
     * @param date
     * @return LocalDateTime
     * @throws ParseException
     * @description Date日期格式转化成LocalDateTime格式
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        LocalDateTime localDateTime = null;
        if(date != null){
            try{
                Instant instant = date.toInstant();
                ZoneId zoneId = ZoneId.systemDefault();
                localDateTime = instant.atZone(zoneId).toLocalDateTime();
            }catch(Exception e){
                throw new TransactionException(ApiConstant.PARAM_DATE_ERROR);
            }
        }
        return localDateTime;
    }

    /**
     * @param str
     * @param format
     * @return LocalDateTime
     * @throws ParseException
     * @description String字符串格式转化成LocalDateTime格式
     */
    public static LocalDateTime stringToLocalDateTime(String str, String format) {
        if (isBlank(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        LocalDateTime localDateTime = null;
        try{
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
            localDateTime = LocalDateTime.parse(str, dtf);
        }catch(Exception e){
            throw new TransactionException(ApiConstant.PARAM_DATE_ERROR);
        }
        return localDateTime;
    }

    /**
     * @description String字符串格式转化成LocalDate格式
     * @param str
     * @return LocalDate
     */
    public static LocalDate stringToLocalDate(String str){
        DateTimeFormatter dtf = null;
        LocalDate localDate = null;
        try{
            dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            localDate = LocalDate.parse(str, dtf);
        }catch(Exception e){
            throw new TransactionException(ApiConstant.PARAM_DATE_ERROR);
        }
        return localDate;
    }

    /**
     * @description String字符串格式转化成LocalTime格式
     * @param str
     * @param format
     * @return LocalTime
     */
    public static LocalTime stringToLocalTime(String str, String format) {
        if (isBlank(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        DateTimeFormatter dtf = null;
        LocalTime localTime = null;
        try{
            dtf = DateTimeFormatter.ofPattern(format);
            localTime = LocalTime.parse(str, dtf);
        }catch(Exception e){
            throw new TransactionException(ApiConstant.PARAM_DATE_ERROR);
        }
        return localTime;
    }

    /**
     * @param localDateTime
     * @return Date
     * @throws ParseException
     * @description LocalDateTime格式转化成Date日期格式
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        Date date = null;
        try{
            ZoneId zoneId = ZoneId.systemDefault();
            ZonedDateTime zdt = localDateTime.atZone(zoneId);
            date = Date.from(zdt.toInstant());
        }catch(Exception e){
            throw new TransactionException(ApiConstant.PARAM_DATE_ERROR);
        }
        return date;
    }

    /**
     * @param localDateTime
     * @param format
     * @return String
     * @throws ParseException
     * @description LocalDateTime格式转化成String字符串格式
     */
    public static String localDateTimeToString(LocalDateTime localDateTime, String format) {
        if(isBlank(format)){
            format = "yyyy-MM-dd HH:mm:ss";
        }
        String dateStr = "";
        try{
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
            dateStr = localDateTime.format(dtf);
        }catch(Exception e){
            throw new TransactionException(ApiConstant.PARAM_DATE_ERROR);
        }
        return dateStr;
    }

    /**
     * @description 两个时间相比较，返回布尔值
     * @param time1
     * @param time2
     * @return boolean
     */
    public static boolean localDateTimeGT(LocalDateTime time1, LocalDateTime time2){
        if(time1 == null || time2 == null){
            return false;
        }
        long currentTime = time1.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        long endTime = time2.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        if(currentTime > endTime){
            return true;
        }
        return false;
    }

    /**
     * @description 返回日期参数加value天后的Date日期
     * @param date
     * @param value
     * @return Date
     */
    public static Date dateAdd(Date date, int value){
        if(date == null || value < 1){
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, value);
        return calendar.getTime();
    }

    /**
     * @description 返回日期参数加value天后的LocalDateTime日期
     * @param localDateTime
     * @param value
     * @return LocalDateTime
     */
    public static LocalDateTime localDateTimeAdd(LocalDateTime localDateTime, int value, String type){
        if (localDateTime == null || value < 1) {
            return null;
        }
        LocalDateTime time = null;
        if(in(type, "days", "months")){
            if("days".equals(type)){
//                LocalDateTime time = localDateTime.plus(value, ChronoUnit.DAYS);
                time = localDateTime.plusDays(value);
            }
            if("months".equals(type)){
//                LocalDateTime time = localDateTime.plus(value, ChronoUnit.MONTHS);
                time = localDateTime.plusMonths(value);
            }
        }
        return time;
    }

    /**
     * @description 返回日期参数减value天后的Date日期
     * @param date
     * @param value
     * @return Date
     */
    public static Date dateSub(Date date, int value){
        if(date == null || value < 1){
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, 0 - value);
        return calendar.getTime();
    }

    /**
     * @description 返回日期参数减value天后的LocalDateTime日期
     * @param localDateTime
     * @param value
     * @param type
     * @return LocalDateTime
     */
    public static LocalDateTime localDateTimeSub(LocalDateTime localDateTime, int value, String type){
        if (localDateTime == null || value < 1) {
            return null;
        }
        LocalDateTime time = null;
        if(in(type, "days", "months")){
            if("days".equals(type)){
                time = localDateTime.minusDays(value);
            }
            if("months".equals(type)){
                time = localDateTime.minusMonths(value);
            }
        }
        return time;
    }

    /**
     * @description 返回日期参数加value天后的Date日期(不要星期天和星期天一)
     * @param date
     * @param value
     * @return Date
     */
    public static Date dateAddNot7An1(Date date, int value){
        if(date == null || value < 1){
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        for(int i = value; i > 0; i--){
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            while(calendar.get(Calendar.DAY_OF_WEEK) == 7 || calendar.get(Calendar.DAY_OF_WEEK) == 1){
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
        }
        return calendar.getTime();
    }

    /**
     * @description 计算两个Date日期参数之间相差多少天
     * @param min
     * @param max
     * @return int
     */
    public static int daySubBoth(Date min, Date max){
        if(min == null || max == null || min.getTime() > max.getTime()){
            return 0;
        }
        int result = (int)((max.getTime() - min.getTime()) / 1000 / 3600 / 24);
        return result;
    }

    /**
     * @description 返回年份参数加value后的Date日期
     * @param date
     * @param value
     * @return Date
     */
    public static Date yearAdd(Date date, int value){
        if(date == null || value < 1){
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + value);
        return calendar.getTime();
    }

    /**
     * @description 返回年份参数减value后的Date日期
     * @param date
     * @param value
     * @return Date
     */
    public static Date yearSub(Date date,int value){
        if(date == null || value < 1){
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - value);
        return calendar.getTime();
    }

    /**
     * @description 返回两个日期相减，date1 - date2的年份差
     * @param date1
     * @param date2
     * @return int
     */
    public static int yearSubBoth(Date date1, Date date2){
        if(date1 != null && date2 != null){
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(date1);
            cal2.setTime(date2);
            return (cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR));
        }
        return 0;
    }

    /**
     * @description 判断两个Date日期参数是否是同一天
     * @param date1
     * @param date2
     * @return boolean
     */
    public static boolean isSameDay(Date date1, Date date2){
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        if(cal1.get(Calendar.YEAR) != cal2.get(Calendar.YEAR)){
            return false;
        }
        if(cal1.get(Calendar.MONTH) != cal2.get(Calendar.MONTH)){
            return false;
        }
        if(cal1.get(Calendar.DAY_OF_MONTH) != cal2.get(Calendar.DAY_OF_MONTH)){
            return false;
        }
        return true;
    }

    /**
     * @description 获取LocalDateTime当前时间毫秒数
     * @return long
     */
    public static long getEpochMilliByCurrentTime(){
        return LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    /**
     * @description 获取Date日期参数对应星期几参数
     * @param date
     * @return Date
     */
    public static Date getWeekByDate(Date date){
        if(date == null){
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        if(day == 1){//星期天
            return dateSub(date, 6);
        }
        if(day == 2){//星期一
            return date;
        }
        if(day == 3){//星期二
            return dateSub(date, 1);
        }
        if(day == 4){//星期三
            return dateSub(date, 2);
        }
        if(day == 5){//星期四
            return dateSub(date, 3);
        }
        if(day == 6){//星期五
            return dateSub(date, 4);
        }
        if(day == 7){//星期六
            return dateSub(date, 5);
        }
        return date;
    }

    /**
     * @description 获取Date日期格式当前月份的第一天
     * @param date
     * @return Date
     */
    public static Date getMonthFirst(Date date) {
        if(date == null){
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    /**
     * @description 获取Date日期格式当前月份的最后一天
     * @param date
     * @return Date
     */
    public static Date getMonthLast(Date date) {
        if(date == null){
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    /**
     * @description 获取Date日期格式当周星期一至星期天的日期
     * @param date
     * @return List
     */
    public static List<Date> getWeekList(Date date){
        if(date == null){
            return null;
        }
        List<Date> list = new ArrayList<Date>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        if(day == 1){//星期天
            Date start = dateSub(date, 6);
            list.add(start);
            for(int i = 0; i < 6; i++){
                start = dateAdd(start, 1);
                list.add(start);
            }
        }
        if(day == 2){//星期一
            Date start = date;
            list.add(start);
            for(int i = 0; i < 6; i++){
                start = dateAdd(start, 1);
                list.add(start);
            }
        }
        if(day == 3){//星期二
            Date start = dateSub(date, 1);
            list.add(start);
            for(int i = 0; i < 6; i++){
                start = dateAdd(start, 1);
                list.add(start);
            }
        }
        if(day == 4){//星期三
            Date start = dateSub(date, 2);
            list.add(start);
            for(int i = 0; i < 6; i++){
                start = dateAdd(start, 1);
                list.add(start);
            }
        }
        if(day == 5){//星期四
            Date start = dateSub(date, 3);
            list.add(start);
            for(int i = 0; i < 6; i++){
                start = dateAdd(start, 1);
                list.add(start);
            }
        }
        if(day == 6){//星期五
            Date start = dateSub(date, 4);
            list.add(start);
            for(int i = 0; i < 6; i++){
                start = dateAdd(start, 1);
                list.add(start);
            }
        }
        if(day == 7){//星期六
            Date start = dateSub(date, 5);
            list.add(start);
            for(int i = 0; i < 6; i++){
                start = dateAdd(start, 1);
                list.add(start);
            }
        }
        return list;
    }

    /**
     * @description 下划线字符串字段转化成驼峰字段
     * @param list
     * @return List
     */
    public static List<Map<String, Object>> formatHumpList(List<Map<String, Object>> list){
        List<Map<String, Object>> newList = new ArrayList<Map<String, Object>>();
        for(Map<String, Object> map : list){
            newList.add(formatHumpMap(map));
        }
        return newList;
    }

    public static Map<String, Object> formatHumpMap(Map<String, Object> map){
        Map<String, Object> newMap = new HashMap<String, Object>();
        Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String, Object> entry = it.next();
            String key = entry.getKey();
            String newKey = lineToHump(key);
            Object value = entry.getValue();
            if(value != null && value != ""){
                newMap.put(newKey, value);
            }else{
                newMap.put(newKey, "");
            }
        }
        return newMap;
    }

    public static String lineToHump(String colName){
        StringBuilder sb = new StringBuilder();
        String[] str = colName.toLowerCase().split("_");
        int i = 0;
        for(String s : str){
            if (s.length() == 1) {
                s = s.toUpperCase();
            }
            i++;
            if (i == 1){
                sb.append(s);
                continue;
            }
            if (s.length() > 0){
                sb.append(s.substring(0, 1).toUpperCase());
                sb.append(s.substring(1));
            }
        }
        return sb.toString();
    }

    /**
     * @description 字符串以split为分割符，转换成List
     * @param str
     * @param split
     * @return List
     */
    public static List<String> strToList(String str, String split){
        if(isBlank(str, split)){
            return null;
        }
        List<String> list = null;
        String[] strArr = str.split(split);
        if(strArr != null && strArr.length > 0){
            list = new ArrayList<String>();
            for(String s : strArr){
                if(!isBlank(s)){
                    list.add(s);
                }
            }
        }
        return list;
    }

    /**
     * @description 去掉List<String>中的重复值
     * @param oldList
     * @return List
     */
    public static List<String> trimList(List<String> oldList){
        List<String> list = new ArrayList<String>();
        if(oldList != null && oldList.size() > 0){
            for(String str : oldList){
                if(!list.contains(str)){
                    list.add(str);
                }
            }
        }
        return list;
    }

    /**
     * @description 获取两个List的交集
     * @param firstList
     * @param secondList
     * @return List<String>
     */
//    public static List<String> getListBoth(List<String> firstList, List<String> secondList) {
//        List<String> resultList = new ArrayList<String>();
//        LinkedList<String> result = new LinkedList<String>(firstList); //大集合用LinkedList
//        HashSet<String> othHash = new HashSet<String>(secondList); //小集合用HashSet
//        Iterator<String> iter = result.iterator(); //Iterator迭代器进行数据操作
//        while(iter.hasNext()) {
//            if(!othHash.contains(iter.next())) {
//                iter.remove();
//            }
//        }
//        resultList = new ArrayList<String>(result);
//        return resultList;
//    }
    public static List<String> getListBoth(List<String> firstList, List<String> secondList) {
        List<String> resultList = Lists.newArrayList();
        LinkedList<String> first = Lists.newLinkedList(firstList); //大集合用LinkedList
        HashSet<String> second = new HashSet<String>(secondList); //小集合用HashSet
        Iterator<String> iterator = first.iterator(); //Iterator迭代器进行数据操作
        while(iterator.hasNext()) {
            if(!second.contains(iterator.next())) {
                iterator.remove();
            }
        }
        resultList = Lists.newArrayList(first);
        return resultList;
    }

    /**
     * @description 判断集合是否为空并且数量大于0
     * @param collection
     * @return boolean
     */
    public static boolean listSizeGT(Collection collection) {
        if (collection == null || collection.size() < 1) {
            return false;
        }
        return true;
    }

    /**
     * @description 将request中的Xml格式转化成Map
     * @param request
     * @return Map
     */
    public static Map<String,String> xmlToMap(HttpServletRequest request){
        try{
            Map<String,String> map = new HashMap<String, String>();
            SAXReader reader = new SAXReader();
            InputStream ins = request.getInputStream();
            Document doc = reader.read(ins);
            Element root = doc.getRootElement();
            List<Element> list = root.elements();
            for(Element e : list){
                map.put(e.getName(), e.getText());
            }
            ins.close();
            return map;
        }catch(DocumentException e){
            e.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    /**
     * @description 将String的Xml格式转化成Map
     * @param xml
     * @return Map
     */
    public static Map<String,String> xmlToMap(String xml){
        try{
            Map<String,String> map = new HashMap<String, String>();
            Document doc = DocumentHelper.parseText(xml);
            Element root = doc.getRootElement();
            List<Element> list = root.elements();
            for(Element e : list){
                map.put(e.getName(), e.getText());
            }
            return map;
        }catch(DocumentException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @description 将Map转化成Xml格式
     * @param map
     * @return String
     */
    public static String mapToXml(Map map){
        if(map != null){
            Element root = DocumentHelper.createElement("xml");
            Document document = DocumentHelper.createDocument(root);
            Set<String> set = map.keySet();
            for(String key : set){
                if(map.get(key) != null){
                    root.addElement(key).addText(String.valueOf(map.get(key)));
                }
            }
            return document.asXML();
        }
        return "";
    }

    /**
     * @description 动态返回对应实体类
     * @param responseResult
     * @param clazz
     * @param <T>
     * @return T
     */
    public static<T> T getObjectEntity(ResponseResult responseResult, Class<T> clazz){
        T target = null;
        if(isNotNull(responseResult) && isNotNull(responseResult.getData())){
            target = JSONObject.parseObject(JSONObject.toJSONString(responseResult.getData()), clazz);
        }
        return target;
    }

    /**
     * @description 获取当前页码
     * @param value
     * @return int
     */
    public static int getPageNo(int value){
        if(isNull(value) || value < 1
                || !isNumber(String.valueOf(value))){
            value = 1;
        }
        return value - 1;
    }

    /**
     * @description 获取每页数量
     * @param value
     * @return int
     */
    public static int getPageSize(int value){
        if(isNull(value) || value < 1
                || !isNumber(String.valueOf(value))){
            value = 10;
        }
        return value;
    }

    /**
     * @description 分页查询结果集
     * @param page
     * @return Map<String, Object>
     */
    public static Map<String, Object> pageResult(IPage page){
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("records", page.getRecords());
        resultMap.put("total", page.getTotal());
        resultMap.put("page", page.getCurrent());
        resultMap.put("pageSize", page.getSize());
        return resultMap;
    }

    /**
     * @Description 将字符串中的emoji表情转换成可以在utf-8字符集数据库中保存的格式(表情占4个字节，需要utf8mb4字符集)
     * @param str
     * @return String
     */
    public static String emojiConvert(String str){
        if(isNotBlank(str)){
            try{
                String patternString = "([\\x{10000}-\\x{10ffff}\ud800-\udfff])";
                Pattern pattern = Pattern.compile(patternString);
                Matcher matcher = pattern.matcher(str);
                StringBuffer sb = new StringBuffer();
                while(matcher.find()) {
                    matcher.appendReplacement(sb,"[[" + URLEncoder.encode(matcher.group(1),"UTF-8") + "]]");
                }
                matcher.appendTail(sb);
                return sb.toString();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @description 还原utf-8字符集数据库中保存的含转换后emoji表情的字符串
     * @param str
     * @return String
     */
    public static String emojiRecovery(String str) {
        if(isNotBlank(str)){
            try{
                if(str == null){
                    str = "";
                }
                String patternString = "\\[\\[(.*?)\\]\\]";
                Pattern pattern = Pattern.compile(patternString);
                Matcher matcher = pattern.matcher(str);
                StringBuffer sb = new StringBuffer();
                while(matcher.find()) {
                    matcher.appendReplacement(sb, URLDecoder.decode(matcher.group(1), "UTF-8"));
                }
                matcher.appendTail(sb);
                return sb.toString();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @description 判断文件是否图片(String类型)
     * @param suffix
     * @return boolean
     */
    public static boolean isImage(String suffix) {
        return imageDispose(suffix);
    }

    /**
     * @description 判断文件是否图片(MultipartFile类型)
     * @param file
     * @return boolean
     */
    public static boolean isImage(MultipartFile file) {
        String suffix = getFileSuffix(file);
        return imageDispose(suffix);
    }

    /**
     * @description 判断文件是否图片公用方法
     * @param suffix
     * @return boolean
     */
    private static boolean imageDispose(String suffix){
        boolean ok = false;
        if(isNotBlank(suffix)) {
            String[] arr = {".png", ".jpg", ".jpeg", ".gif", ".bmp"};
            for(String s : arr) {
                if(suffix.toLowerCase().equals(s)) {
                    ok = true;
                    return ok;
                }
            }
        }
        return ok;
    }

    /**
     * @description 判断文件是否视频(String类型)
     * @param suffix
     * @return boolean
     */
    public static boolean isVideo(String suffix) {
        return videoDispose(suffix);
    }


    /**
     * @description 判断文件是否视频(MultipartFile类型)
     * @param file
     * @return boolean
     */
    public static boolean isVideo(MultipartFile file) {
        String suffix = getFileSuffix(file);
        return videoDispose(suffix);
    }

    /**
     * @description 判断文件是否视频公用方法
     * @param suffix
     * @return boolean
     */
    private static boolean videoDispose(String suffix){
        boolean ok = false;
        if(isNotBlank(suffix)) {
            String[] arr = {".flv", ".swf", ".mkv", ".avi", ".rm", ".rmvb", ".mpeg", ".mpg",".ogg", ".ogv", ".mov", ".wmv", ".mp4", ".webm", ".mp3", ".wav", ".mid"};
            for(String s : arr) {
                if(suffix.toLowerCase().equals(s)) {
                    ok = true;
                    return ok;
                }
            }
        }
        return ok;
    }

    /**
     * @description 获取文件后缀，包含.
     * @param file
     * @return String
     */
    public static String getFileSuffix(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if(!fileName.contains(".")){
            return null;
        }
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        return suffix;
    }

    /**
     * @description 向指定Url发送Post方法的请求，String方式
     * @param url
     * @param param
     * @param charset
     * @return String
     */
    public static String sendPost(String url, String param, String charset) {
        String result = "";
        if (charset == null) {
            charset = "UTF-8";
        }
        CloseableHttpClient httpClient = null;
        HttpPost httpPost = null;
        try {
            httpClient = HttpConnectionManager.getInstance().getHttpClient();
            httpPost = new HttpPost(url);
            // 设置连接超时,设置读取超时
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(10000).setSocketTimeout(10000).build();
            httpPost.setConfig(requestConfig);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
            // 设置参数
            StringEntity se = new StringEntity(param, charset);
            httpPost.setEntity(se);
            HttpResponse response = httpClient.execute(httpPost);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, charset);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * @description 向指定Url发送Post方法的请求，map方式
     * @param url
     * @param map
     * @return String
     */
    public static String sendPost(String url, Map<String, ?> map){
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        String param = "";
        Iterator<String> it = map.keySet().iterator();
        while(it.hasNext()){
            String key = it.next();
            param += key + "=" + map.get(key) + "&";
        }
        try{
            URL realUrl = new URL(url);
            //打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            //设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            //送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            //发送请求参数
            out.print(param);
            //flush输出流的缓冲
            out.flush();
            //定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{//使用finally关闭输出流、输入流
            try{
                if(out != null){
                    out.close();
                }
                if(in != null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * @description 向指定Url发送Post方法的请求，xml方式
     * @param url
     * @param xml
     * @return String
     */
    public static String sendPost(String url, String xml) {
        try {
            //发送POST请求
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setUseCaches(false);
            conn.setDoOutput(true);

            conn.setRequestProperty("Content-Length", "" + xml.length());
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            out.write(xml);
            out.flush();
            out.close();
            //获取响应状态
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                info("sendPost connect fail");
                return "";
            }
            //获取响应内容体
            String line, result = "";
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            while ((line = in.readLine()) != null) {
                result += line + "\n";
            }
            in.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        return "";
    }

    /**
     * @description 向指定Url发送Get方法的请求，String方式
     * @param url
     * @return String
     */
    public static String sendGet(String url) {
        try {
            //发送get请求
            URL getUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) getUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            //获取响应状态
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                info("sendGet connect fail");
                return "";
            }
            //获取响应内容体
            String line, result = "";
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            while ((line = in.readLine()) != null) {
                result += line + "\n";
            }
            in.close();
            info("sendGet response : " + result);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @description 向指定Url发送Get方法的请求，String请求体参数方式与Map请求头参数方式
     * @param url
     * @param map
     * @return String
     */
    public static String sendGet(String url, Map<String, Object> map) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);
        //配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(3000)
                .setConnectTimeout(3000)
                .setSocketTimeout(3000).build();
        httpGet.setConfig(requestConfig);
        httpGet.setHeader("Content-Type", map.get("contentType") + "");
        CloseableHttpResponse response = null;
        String result = null;
        try {
            response = httpClient.execute(httpGet); //发送请求
            info("StatusCode -> " + response.getStatusLine().getStatusCode());
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity,"utf-8");
            info("sendGet response : " + result);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            httpGet.releaseConnection();
        }
        return result;
    }

    /**
     * @description 向指定Url发送Put方法的请求，String请求体参数方式与Map请求头参数方式
     * @param urlPath
     * @param param json参数
     * @param map
     * @return String
     */
    public static String sendPut(String urlPath, Object param, Map<String, Object> map) {
        //创建连接
        String encode = "utf-8";
        //HttpClients.createDefault()等价于 HttpClientBuilder.create().build();
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        HttpPut httpput = new HttpPut(urlPath);
        /**header中通用属性*/
        httpput.setHeader("Accept","*/*");
        httpput.setHeader("Accept-Encoding","gzip, deflate");
        httpput.setHeader("Cache-Control","no-cache");
        httpput.setHeader("Connection", "keep-alive");
        /**业务参数*/
        httpput.setHeader("Content-Type", map.get("contentType") + "");
        //组织请求参数
        StringEntity stringEntity = new StringEntity(JSON.toJSONString(param), encode);
        httpput.setEntity(stringEntity);
        String content = null;
        CloseableHttpResponse  httpResponse = null;
        try {
            //响应信息
            httpResponse = closeableHttpClient.execute(httpput);
            HttpEntity entity = httpResponse.getEntity();
            content = EntityUtils.toString(entity, encode);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                httpResponse.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            closeableHttpClient.close();  //关闭连接、释放资源
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * @description 向指定Url发送Delete方法的请求，String请求体参数方式与Map请求头参数方式
     * @param url
     * @param param
     * @param map
     * @return String
     */
    public static String sendDelete(String url, String param, Map<String, Object> map) {
        CloseableHttpClient client = null;
        HttpDeleteRequest httpDelete = null;
        String result = null;
        try {
            client = HttpClients.createDefault();
            httpDelete = new HttpDeleteRequest(url);
            httpDelete.addHeader("Content-Type", map.get("contentType") + "");
            httpDelete.setHeader("Accept", "application/json; charset=utf-8");
            httpDelete.setEntity(new StringEntity(param));
            CloseableHttpResponse response = client.execute(httpDelete);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity);
            if (200 == response.getStatusLine().getStatusCode()) {
                info("DELETE方式请求远程调用成功.msg={" + result + "}");
            }
        } catch (Exception e) {
            error("DELETE方式请求远程调用失败,errorMsg={" + e.getMessage() + "}");
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * @description 发送手机验证码
     * @param templateId
     * @param phone
     * @param vcodes
     */
    public static void sendSMS(String templateId, String phone, String ... vcodes) {
        String content = "";
        if(isNotBlank(vcodes) && vcodes.length > 0){
            for(String str : vcodes){
                content += (str + ",");
            }
            content = content.substring(0,content.length() - 1);
        }
        JSONObject json = new JSONObject();
        json.put("sid", Constants.SMS_ACCOUNT_SID);
        json.put("token", Constants.SMS_AUTH_TOKEN);
        json.put("appid", Constants.SMS_APP_ID);
        json.put("templateid", templateId);
        json.put("mobile", phone);
        json.put("param", content);
        json.put("uid", null);
        try{
            String result = sendPost(Constants.SMS_URL, json.toJSONString(), null);
            info("sendSms success : \nphone : " + phone + ", content : " + content + " \nresult : " + result);
        }catch(Exception e){
            error("sendSms fail : " + e.getMessage());
        }
    }

    /**
     * @description 根据身份证获取出生地址
     * @param cardNo
     * @return String
     */
    public static String getBirthPlace(String cardNo) {
        Integer cardCode = Integer.parseInt(cardNo);
        String province = getNameString(cardCode / 10000);
        String city = getNameString(cardCode / 100);
        String district = getNameString(cardCode);
        String birthPlace = "";
        if (isNotBlank(province)) {
            birthPlace += province;
        }
        if (isNotBlank(city)) {
            birthPlace += city;
        }
        if (isNotBlank(district)) {
            birthPlace += district;
        }
        return birthPlace;
    }

    /**
     * @description 敏感词过滤，替换为*
     * @param content
     * @return String
     */
    public static String replaceContent(String content) {
        return SysContent1Util.getInstance().replaceSensitiveWord(content, 2, "*");
    }

    /**
     * @description 去除待带script、src的语句，转义替换后的value值
     * @param value
     * @return String
     */
    public static String replaceXSS(String value) {
        if (value != null) {
            try{
                value = value.replace("+","%2B");   //'+' replace to '%2B'
                value = URLDecoder.decode(value, "utf-8");
            }catch(UnsupportedEncodingException e){
                e.printStackTrace();
            }catch(IllegalArgumentException e){
                e.printStackTrace();
            }

            //Avoid null characters
            value = value.replaceAll("\0", "");

            //Avoid anything between script tags
            Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            //Avoid anything in a src='...' type of e­xpression
            /*scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");*/

            //Remove any lonesome </script> tag
            scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            //Remove any lonesome <script ...> tag
            scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            //Avoid eval(...) e­xpressions
            scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            //Avoid e­xpression(...) e­xpressions
            scriptPattern = Pattern.compile("e­xpression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            //Avoid javascript:... e­xpressions
            scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            //Avoid alert:... e­xpressions
            scriptPattern = Pattern.compile("alert", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            //Avoid onload= e­xpressions
            scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            scriptPattern = Pattern.compile("vbscript[\r\n| | ]*:[\r\n| | ]*", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");
        }
        return XSSFilter(value);
    }

    /**
     * @description 获取项目绝对路径(如：/opt/aa/bb.jar；返回：/opt/aa)
     * @return String
     */
    private static String getProjectPath(){
        String jarPath = FileUtil.getAbsolutePath("").replace("!/BOOT-INF/classes!/","");
        String dir = jarPath.substring(0,jarPath.lastIndexOf("/"));
//        String path = "/usr/workspace/project/";
//        String dir = path.substring(0, path.lastIndexOf("/"));
        return dir;
    }

    /**
     * @description 省市区处理
     * @param cardCode
     * @return String
     */
    private static String getNameString(Integer cardCode) {
        switch (cardCode) {
            case 11:
                return "北京市";
            case 1101:
                return "市辖区";
            case 110101:
                return "东城区";
            case 110102:
                return "西城区";
            case 110105:
                return "朝阳区";
            case 110106:
                return "丰台区";
            case 110107:
                return "石景山区";
            case 110108:
                return "海淀区";
            case 110109:
                return "门头沟区";
            case 110111:
                return "房山区";
            case 110112:
                return "通州区";
            case 110113:
                return "顺义区";
            case 110114:
                return "昌平区";
            case 110115:
                return "大兴区";
            case 110116:
                return "怀柔区";
            case 110117:
                return "平谷区";
            case 1102:
                return "县";
            case 110228:
                return "密云县";
            case 110229:
                return "延庆县";
            case 12:
                return "天津市";
            case 1201:
                return "市辖区";
            case 120101:
                return "和平区";
            case 120102:
                return "河东区";
            case 120103:
                return "河西区";
            case 120104:
                return "南开区";
            case 120105:
                return "河北区";
            case 120106:
                return "红桥区";
            case 120110:
                return "东丽区";
            case 120111:
                return "西青区";
            case 120112:
                return "津南区";
            case 120113:
                return "北辰区";
            case 120114:
                return "武清区";
            case 120115:
                return "宝坻区";
            case 120116:
                return "滨海新区";
            case 1202:
                return "县";
            case 120221:
                return "宁河县";
            case 120223:
                return "静海县";
            case 120225:
                return "蓟县";
            case 13:
                return "河北省";
            case 1301:
                return "石家庄市";
            case 130101:
                return "市辖区";
            case 130102:
                return "长安区";
            case 130104:
                return "桥西区";
            case 130105:
                return "新华区";
            case 130107:
                return "井陉矿区";
            case 130108:
                return "裕华区";
            case 130109:
                return "藁城区";
            case 130110:
                return "鹿泉区";
            case 130111:
                return "栾城区";
            case 130121:
                return "井陉县";
            case 130123:
                return "正定县";
            case 130125:
                return "行唐县";
            case 130126:
                return "灵寿县";
            case 130127:
                return "高邑县";
            case 130128:
                return "深泽县";
            case 130129:
                return "赞皇县";
            case 130130:
                return "无极县";
            case 130131:
                return "平山县";
            case 130132:
                return "元氏县";
            case 130133:
                return "赵县";
            case 130183:
                return "晋州市";
            case 130184:
                return "新乐市";
            case 1302:
                return "唐山市";
            case 130201:
                return "市辖区";
            case 130202:
                return "路南区";
            case 130203:
                return "路北区";
            case 130204:
                return "古冶区";
            case 130205:
                return "开平区";
            case 130207:
                return "丰南区";
            case 130208:
                return "丰润区";
            case 130209:
                return "曹妃甸区";
            case 130223:
                return "滦县";
            case 130224:
                return "滦南县";
            case 130225:
                return "乐亭县";
            case 130227:
                return "迁西县";
            case 130229:
                return "玉田县";
            case 130281:
                return "遵化市";
            case 130283:
                return "迁安市";
            case 1303:
                return "秦皇岛市";
            case 130301:
                return "市辖区";
            case 130302:
                return "海港区";
            case 130303:
                return "山海关区";
            case 130304:
                return "北戴河区";
            case 130321:
                return "青龙满族自治县";
            case 130322:
                return "昌黎县";
            case 130323:
                return "抚宁县";
            case 130324:
                return "卢龙县";
            case 1304:
                return "邯郸市";
            case 130401:
                return "市辖区";
            case 130402:
                return "邯山区";
            case 130403:
                return "丛台区";
            case 130404:
                return "复兴区";
            case 130406:
                return "峰峰矿区";
            case 130421:
                return "邯郸县";
            case 130423:
                return "临漳县";
            case 130424:
                return "成安县";
            case 130425:
                return "大名县";
            case 130426:
                return "涉县";
            case 130427:
                return "磁县";
            case 130428:
                return "肥乡县";
            case 130429:
                return "永年县";
            case 130430:
                return "邱县";
            case 130431:
                return "鸡泽县";
            case 130432:
                return "广平县";
            case 130433:
                return "馆陶县";
            case 130434:
                return "魏县";
            case 130435:
                return "曲周县";
            case 130481:
                return "武安市";
            case 1305:
                return "邢台市";
            case 130501:
                return "市辖区";
            case 130502:
                return "桥东区";
            case 130503:
                return "桥西区";
            case 130521:
                return "邢台县";
            case 130522:
                return "临城县";
            case 130523:
                return "内丘县";
            case 130524:
                return "柏乡县";
            case 130525:
                return "隆尧县";
            case 130526:
                return "任县";
            case 130527:
                return "南和县";
            case 130528:
                return "宁晋县";
            case 130529:
                return "巨鹿县";
            case 130530:
                return "新河县";
            case 130531:
                return "广宗县";
            case 130532:
                return "平乡县";
            case 130533:
                return "威县";
            case 130534:
                return "清河县";
            case 130535:
                return "临西县";
            case 130581:
                return "南宫市";
            case 130582:
                return "沙河市";
            case 1306:
                return "保定市";
            case 130601:
                return "市辖区";
            case 130602:
                return "新市区";
            case 130603:
                return "北市区";
            case 130604:
                return "南市区";
            case 130621:
                return "满城县";
            case 130622:
                return "清苑县";
            case 130623:
                return "涞水县";
            case 130624:
                return "阜平县";
            case 130625:
                return "徐水县";
            case 130626:
                return "定兴县";
            case 130627:
                return "唐县";
            case 130628:
                return "高阳县";
            case 130629:
                return "容城县";
            case 130630:
                return "涞源县";
            case 130631:
                return "望都县";
            case 130632:
                return "安新县";
            case 130633:
                return "易县";
            case 130634:
                return "曲阳县";
            case 130635:
                return "蠡县";
            case 130636:
                return "顺平县";
            case 130637:
                return "博野县";
            case 130638:
                return "雄县";
            case 130681:
                return "涿州市";
            case 130683:
                return "安国市";
            case 130684:
                return "高碑店市";
            case 1307:
                return "张家口市";
            case 130701:
                return "市辖区";
            case 130702:
                return "桥东区";
            case 130703:
                return "桥西区";
            case 130705:
                return "宣化区";
            case 130706:
                return "下花园区";
            case 130721:
                return "宣化县";
            case 130722:
                return "张北县";
            case 130723:
                return "康保县";
            case 130724:
                return "沽源县";
            case 130725:
                return "尚义县";
            case 130726:
                return "蔚县";
            case 130727:
                return "阳原县";
            case 130728:
                return "怀安县";
            case 130729:
                return "万全县";
            case 130730:
                return "怀来县";
            case 130731:
                return "涿鹿县";
            case 130732:
                return "赤城县";
            case 130733:
                return "崇礼县";
            case 1308:
                return "承德市";
            case 130801:
                return "市辖区";
            case 130802:
                return "双桥区";
            case 130803:
                return "双滦区";
            case 130804:
                return "鹰手营子矿区";
            case 130821:
                return "承德县";
            case 130822:
                return "兴隆县";
            case 130823:
                return "平泉县";
            case 130824:
                return "滦平县";
            case 130825:
                return "隆化县";
            case 130826:
                return "丰宁满族自治县";
            case 130827:
                return "宽城满族自治县";
            case 130828:
                return "围场满族蒙古族自治县";
            case 1309:
                return "沧州市";
            case 130901:
                return "市辖区";
            case 130902:
                return "新华区";
            case 130903:
                return "运河区";
            case 130921:
                return "沧县";
            case 130922:
                return "青县";
            case 130923:
                return "东光县";
            case 130924:
                return "海兴县";
            case 130925:
                return "盐山县";
            case 130926:
                return "肃宁县";
            case 130927:
                return "南皮县";
            case 130928:
                return "吴桥县";
            case 130929:
                return "献县";
            case 130930:
                return "孟村回族自治县";
            case 130981:
                return "泊头市";
            case 130982:
                return "任丘市";
            case 130983:
                return "黄骅市";
            case 130984:
                return "河间市";
            case 1310:
                return "廊坊市";
            case 131001:
                return "市辖区";
            case 131002:
                return "安次区";
            case 131003:
                return "广阳区";
            case 131022:
                return "固安县";
            case 131023:
                return "永清县";
            case 131024:
                return "香河县";
            case 131025:
                return "大城县";
            case 131026:
                return "文安县";
            case 131028:
                return "大厂回族自治县";
            case 131081:
                return "霸州市";
            case 131082:
                return "三河市";
            case 1311:
                return "衡水市";
            case 131101:
                return "市辖区";
            case 131102:
                return "桃城区";
            case 131121:
                return "枣强县";
            case 131122:
                return "武邑县";
            case 131123:
                return "武强县";
            case 131124:
                return "饶阳县";
            case 131125:
                return "安平县";
            case 131126:
                return "故城县";
            case 131127:
                return "景县";
            case 131128:
                return "阜城县";
            case 131181:
                return "冀州市";
            case 131182:
                return "深州市";
            case 1390:
                return "省直辖县级行政区划";
            case 139001:
                return "定州市";
            case 139002:
                return "辛集市";
            case 14:
                return "山西省";
            case 1401:
                return "太原市";
            case 140101:
                return "市辖区";
            case 140105:
                return "小店区";
            case 140106:
                return "迎泽区";
            case 140107:
                return "杏花岭区";
            case 140108:
                return "尖草坪区";
            case 140109:
                return "万柏林区";
            case 140110:
                return "晋源区";
            case 140121:
                return "清徐县";
            case 140122:
                return "阳曲县";
            case 140123:
                return "娄烦县";
            case 140181:
                return "古交市";
            case 1402:
                return "大同市";
            case 140201:
                return "市辖区";
            case 140202:
                return "城区";
            case 140203:
                return "矿区";
            case 140211:
                return "南郊区";
            case 140212:
                return "新荣区";
            case 140221:
                return "阳高县";
            case 140222:
                return "天镇县";
            case 140223:
                return "广灵县";
            case 140224:
                return "灵丘县";
            case 140225:
                return "浑源县";
            case 140226:
                return "左云县";
            case 140227:
                return "大同县";
            case 1403:
                return "阳泉市";
            case 140301:
                return "市辖区";
            case 140302:
                return "城区";
            case 140303:
                return "矿区";
            case 140311:
                return "郊区";
            case 140321:
                return "平定县";
            case 140322:
                return "盂县";
            case 1404:
                return "长治市";
            case 140401:
                return "市辖区";
            case 140402:
                return "城区";
            case 140411:
                return "郊区";
            case 140421:
                return "长治县";
            case 140423:
                return "襄垣县";
            case 140424:
                return "屯留县";
            case 140425:
                return "平顺县";
            case 140426:
                return "黎城县";
            case 140427:
                return "壶关县";
            case 140428:
                return "长子县";
            case 140429:
                return "武乡县";
            case 140430:
                return "沁县";
            case 140431:
                return "沁源县";
            case 140481:
                return "潞城市";
            case 1405:
                return "晋城市";
            case 140501:
                return "市辖区";
            case 140502:
                return "城区";
            case 140521:
                return "沁水县";
            case 140522:
                return "阳城县";
            case 140524:
                return "陵川县";
            case 140525:
                return "泽州县";
            case 140581:
                return "高平市";
            case 1406:
                return "朔州市";
            case 140601:
                return "市辖区";
            case 140602:
                return "朔城区";
            case 140603:
                return "平鲁区";
            case 140621:
                return "山阴县";
            case 140622:
                return "应县";
            case 140623:
                return "右玉县";
            case 140624:
                return "怀仁县";
            case 1407:
                return "晋中市";
            case 140701:
                return "市辖区";
            case 140702:
                return "榆次区";
            case 140721:
                return "榆社县";
            case 140722:
                return "左权县";
            case 140723:
                return "和顺县";
            case 140724:
                return "昔阳县";
            case 140725:
                return "寿阳县";
            case 140726:
                return "太谷县";
            case 140727:
                return "祁县";
            case 140728:
                return "平遥县";
            case 140729:
                return "灵石县";
            case 140781:
                return "介休市";
            case 1408:
                return "运城市";
            case 140801:
                return "市辖区";
            case 140802:
                return "盐湖区";
            case 140821:
                return "临猗县";
            case 140822:
                return "万荣县";
            case 140823:
                return "闻喜县";
            case 140824:
                return "稷山县";
            case 140825:
                return "新绛县";
            case 140826:
                return "绛县";
            case 140827:
                return "垣曲县";
            case 140828:
                return "夏县";
            case 140829:
                return "平陆县";
            case 140830:
                return "芮城县";
            case 140881:
                return "永济市";
            case 140882:
                return "河津市";
            case 1409:
                return "忻州市";
            case 140901:
                return "市辖区";
            case 140902:
                return "忻府区";
            case 140921:
                return "定襄县";
            case 140922:
                return "五台县";
            case 140923:
                return "代县";
            case 140924:
                return "繁峙县";
            case 140925:
                return "宁武县";
            case 140926:
                return "静乐县";
            case 140927:
                return "神池县";
            case 140928:
                return "五寨县";
            case 140929:
                return "岢岚县";
            case 140930:
                return "河曲县";
            case 140931:
                return "保德县";
            case 140932:
                return "偏关县";
            case 140981:
                return "原平市";
            case 1410:
                return "临汾市";
            case 141001:
                return "市辖区";
            case 141002:
                return "尧都区";
            case 141021:
                return "曲沃县";
            case 141022:
                return "翼城县";
            case 141023:
                return "襄汾县";
            case 141024:
                return "洪洞县";
            case 141025:
                return "古县";
            case 141026:
                return "安泽县";
            case 141027:
                return "浮山县";
            case 141028:
                return "吉县";
            case 141029:
                return "乡宁县";
            case 141030:
                return "大宁县";
            case 141031:
                return "隰县";
            case 141032:
                return "永和县";
            case 141033:
                return "蒲县";
            case 141034:
                return "汾西县";
            case 141081:
                return "侯马市";
            case 141082:
                return "霍州市";
            case 1411:
                return "吕梁市";
            case 141101:
                return "市辖区";
            case 141102:
                return "离石区";
            case 141121:
                return "文水县";
            case 141122:
                return "交城县";
            case 141123:
                return "兴县";
            case 141124:
                return "临县";
            case 141125:
                return "柳林县";
            case 141126:
                return "石楼县";
            case 141127:
                return "岚县";
            case 141128:
                return "方山县";
            case 141129:
                return "中阳县";
            case 141130:
                return "交口县";
            case 141181:
                return "孝义市";
            case 141182:
                return "汾阳市";
            case 15:
                return "内蒙古自治区";
            case 1501:
                return "呼和浩特市";
            case 150101:
                return "市辖区";
            case 150102:
                return "新城区";
            case 150103:
                return "回民区";
            case 150104:
                return "玉泉区";
            case 150105:
                return "赛罕区";
            case 150121:
                return "土默特左旗";
            case 150122:
                return "托克托县";
            case 150123:
                return "和林格尔县";
            case 150124:
                return "清水河县";
            case 150125:
                return "武川县";
            case 1502:
                return "包头市";
            case 150201:
                return "市辖区";
            case 150202:
                return "东河区";
            case 150203:
                return "昆都仑区";
            case 150204:
                return "青山区";
            case 150205:
                return "石拐区";
            case 150206:
                return "白云鄂博矿区";
            case 150207:
                return "九原区";
            case 150221:
                return "土默特右旗";
            case 150222:
                return "固阳县";
            case 150223:
                return "达尔罕茂明安联合旗";
            case 1503:
                return "乌海市";
            case 150301:
                return "市辖区";
            case 150302:
                return "海勃湾区";
            case 150303:
                return "海南区";
            case 150304:
                return "乌达区";
            case 1504:
                return "赤峰市";
            case 150401:
                return "市辖区";
            case 150402:
                return "红山区";
            case 150403:
                return "元宝山区";
            case 150404:
                return "松山区";
            case 150421:
                return "阿鲁科尔沁旗";
            case 150422:
                return "巴林左旗";
            case 150423:
                return "巴林右旗";
            case 150424:
                return "林西县";
            case 150425:
                return "克什克腾旗";
            case 150426:
                return "翁牛特旗";
            case 150428:
                return "喀喇沁旗";
            case 150429:
                return "宁城县";
            case 150430:
                return "敖汉旗";
            case 1505:
                return "通辽市";
            case 150501:
                return "市辖区";
            case 150502:
                return "科尔沁区";
            case 150521:
                return "科尔沁左翼中旗";
            case 150522:
                return "科尔沁左翼后旗";
            case 150523:
                return "开鲁县";
            case 150524:
                return "库伦旗";
            case 150525:
                return "奈曼旗";
            case 150526:
                return "扎鲁特旗";
            case 150581:
                return "霍林郭勒市";
            case 1506:
                return "鄂尔多斯市";
            case 150601:
                return "市辖区";
            case 150602:
                return "东胜区";
            case 150621:
                return "达拉特旗";
            case 150622:
                return "准格尔旗";
            case 150623:
                return "鄂托克前旗";
            case 150624:
                return "鄂托克旗";
            case 150625:
                return "杭锦旗";
            case 150626:
                return "乌审旗";
            case 150627:
                return "伊金霍洛旗";
            case 1507:
                return "呼伦贝尔市";
            case 150701:
                return "市辖区";
            case 150702:
                return "海拉尔区";
            case 150703:
                return "扎赉诺尔区";
            case 150721:
                return "阿荣旗";
            case 150722:
                return "莫力达瓦达斡尔族自治旗";
            case 150723:
                return "鄂伦春自治旗";
            case 150724:
                return "鄂温克族自治旗";
            case 150725:
                return "陈巴尔虎旗";
            case 150726:
                return "新巴尔虎左旗";
            case 150727:
                return "新巴尔虎右旗";
            case 150781:
                return "满洲里市";
            case 150782:
                return "牙克石市";
            case 150783:
                return "扎兰屯市";
            case 150784:
                return "额尔古纳市";
            case 150785:
                return "根河市";
            case 1508:
                return "巴彦淖尔市";
            case 150801:
                return "市辖区";
            case 150802:
                return "临河区";
            case 150821:
                return "五原县";
            case 150822:
                return "磴口县";
            case 150823:
                return "乌拉特前旗";
            case 150824:
                return "乌拉特中旗";
            case 150825:
                return "乌拉特后旗";
            case 150826:
                return "杭锦后旗";
            case 1509:
                return "乌兰察布市";
            case 150901:
                return "市辖区";
            case 150902:
                return "集宁区";
            case 150921:
                return "卓资县";
            case 150922:
                return "化德县";
            case 150923:
                return "商都县";
            case 150924:
                return "兴和县";
            case 150925:
                return "凉城县";
            case 150926:
                return "察哈尔右翼前旗";
            case 150927:
                return "察哈尔右翼中旗";
            case 150928:
                return "察哈尔右翼后旗";
            case 150929:
                return "四子王旗";
            case 150981:
                return "丰镇市";
            case 1522:
                return "兴安盟";
            case 152201:
                return "乌兰浩特市";
            case 152202:
                return "阿尔山市";
            case 152221:
                return "科尔沁右翼前旗";
            case 152222:
                return "科尔沁右翼中旗";
            case 152223:
                return "扎赉特旗";
            case 152224:
                return "突泉县";
            case 1525:
                return "锡林郭勒盟";
            case 152501:
                return "二连浩特市";
            case 152502:
                return "锡林浩特市";
            case 152522:
                return "阿巴嘎旗";
            case 152523:
                return "苏尼特左旗";
            case 152524:
                return "苏尼特右旗";
            case 152525:
                return "东乌珠穆沁旗";
            case 152526:
                return "西乌珠穆沁旗";
            case 152527:
                return "太仆寺旗";
            case 152528:
                return "镶黄旗";
            case 152529:
                return "正镶白旗";
            case 152530:
                return "正蓝旗";
            case 152531:
                return "多伦县";
            case 1529:
                return "阿拉善盟";
            case 152921:
                return "阿拉善左旗";
            case 152922:
                return "阿拉善右旗";
            case 152923:
                return "额济纳旗";
            case 21:
                return "辽宁省";
            case 2101:
                return "沈阳市";
            case 210101:
                return "市辖区";
            case 210102:
                return "和平区";
            case 210103:
                return "沈河区";
            case 210104:
                return "大东区";
            case 210105:
                return "皇姑区";
            case 210106:
                return "铁西区";
            case 210111:
                return "苏家屯区";
            case 210112:
                return "浑南区";
            case 210113:
                return "沈北新区";
            case 210114:
                return "于洪区";
            case 210122:
                return "辽中县";
            case 210123:
                return "康平县";
            case 210124:
                return "法库县";
            case 210181:
                return "新民市";
            case 2102:
                return "大连市";
            case 210201:
                return "市辖区";
            case 210202:
                return "中山区";
            case 210203:
                return "西岗区";
            case 210204:
                return "沙河口区";
            case 210211:
                return "甘井子区";
            case 210212:
                return "旅顺口区";
            case 210213:
                return "金州区";
            case 210224:
                return "长海县";
            case 210281:
                return "瓦房店市";
            case 210282:
                return "普兰店市";
            case 210283:
                return "庄河市";
            case 2103:
                return "鞍山市";
            case 210301:
                return "市辖区";
            case 210302:
                return "铁东区";
            case 210303:
                return "铁西区";
            case 210304:
                return "立山区";
            case 210311:
                return "千山区";
            case 210321:
                return "台安县";
            case 210323:
                return "岫岩满族自治县";
            case 210381:
                return "海城市";
            case 2104:
                return "抚顺市";
            case 210401:
                return "市辖区";
            case 210402:
                return "新抚区";
            case 210403:
                return "东洲区";
            case 210404:
                return "望花区";
            case 210411:
                return "顺城区";
            case 210421:
                return "抚顺县";
            case 210422:
                return "新宾满族自治县";
            case 210423:
                return "清原满族自治县";
            case 2105:
                return "本溪市";
            case 210501:
                return "市辖区";
            case 210502:
                return "平山区";
            case 210503:
                return "溪湖区";
            case 210504:
                return "明山区";
            case 210505:
                return "南芬区";
            case 210521:
                return "本溪满族自治县";
            case 210522:
                return "桓仁满族自治县";
            case 2106:
                return "丹东市";
            case 210601:
                return "市辖区";
            case 210602:
                return "元宝区";
            case 210603:
                return "振兴区";
            case 210604:
                return "振安区";
            case 210624:
                return "宽甸满族自治县";
            case 210681:
                return "东港市";
            case 210682:
                return "凤城市";
            case 2107:
                return "锦州市";
            case 210701:
                return "市辖区";
            case 210702:
                return "古塔区";
            case 210703:
                return "凌河区";
            case 210711:
                return "太和区";
            case 210726:
                return "黑山县";
            case 210727:
                return "义县";
            case 210781:
                return "凌海市";
            case 210782:
                return "北镇市";
            case 2108:
                return "营口市";
            case 210801:
                return "市辖区";
            case 210802:
                return "站前区";
            case 210803:
                return "西市区";
            case 210804:
                return "鲅鱼圈区";
            case 210811:
                return "老边区";
            case 210881:
                return "盖州市";
            case 210882:
                return "大石桥市";
            case 2109:
                return "阜新市";
            case 210901:
                return "市辖区";
            case 210902:
                return "海州区";
            case 210903:
                return "新邱区";
            case 210904:
                return "太平区";
            case 210905:
                return "清河门区";
            case 210911:
                return "细河区";
            case 210921:
                return "阜新蒙古族自治县";
            case 210922:
                return "彰武县";
            case 2110:
                return "辽阳市";
            case 211001:
                return "市辖区";
            case 211002:
                return "白塔区";
            case 211003:
                return "文圣区";
            case 211004:
                return "宏伟区";
            case 211005:
                return "弓长岭区";
            case 211011:
                return "太子河区";
            case 211021:
                return "辽阳县";
            case 211081:
                return "灯塔市";
            case 2111:
                return "盘锦市";
            case 211101:
                return "市辖区";
            case 211102:
                return "双台子区";
            case 211103:
                return "兴隆台区";
            case 211121:
                return "大洼县";
            case 211122:
                return "盘山县";
            case 2112:
                return "铁岭市";
            case 211201:
                return "市辖区";
            case 211202:
                return "银州区";
            case 211204:
                return "清河区";
            case 211221:
                return "铁岭县";
            case 211223:
                return "西丰县";
            case 211224:
                return "昌图县";
            case 211281:
                return "调兵山市";
            case 211282:
                return "开原市";
            case 2113:
                return "朝阳市";
            case 211301:
                return "市辖区";
            case 211302:
                return "双塔区";
            case 211303:
                return "龙城区";
            case 211321:
                return "朝阳县";
            case 211322:
                return "建平县";
            case 211324:
                return "喀喇沁左翼蒙古族自治县";
            case 211381:
                return "北票市";
            case 211382:
                return "凌源市";
            case 2114:
                return "葫芦岛市";
            case 211401:
                return "市辖区";
            case 211402:
                return "连山区";
            case 211403:
                return "龙港区";
            case 211404:
                return "南票区";
            case 211421:
                return "绥中县";
            case 211422:
                return "建昌县";
            case 211481:
                return "兴城市";
            case 22:
                return "吉林省";
            case 2201:
                return "长春市";
            case 220101:
                return "市辖区";
            case 220102:
                return "南关区";
            case 220103:
                return "宽城区";
            case 220104:
                return "朝阳区";
            case 220105:
                return "二道区";
            case 220106:
                return "绿园区";
            case 220112:
                return "双阳区";
            case 220113:
                return "九台区";
            case 220122:
                return "农安县";
            case 220182:
                return "榆树市";
            case 220183:
                return "德惠市";
            case 2202:
                return "吉林市";
            case 220201:
                return "市辖区";
            case 220202:
                return "昌邑区";
            case 220203:
                return "龙潭区";
            case 220204:
                return "船营区";
            case 220211:
                return "丰满区";
            case 220221:
                return "永吉县";
            case 220281:
                return "蛟河市";
            case 220282:
                return "桦甸市";
            case 220283:
                return "舒兰市";
            case 220284:
                return "磐石市";
            case 2203:
                return "四平市";
            case 220301:
                return "市辖区";
            case 220302:
                return "铁西区";
            case 220303:
                return "铁东区";
            case 220322:
                return "梨树县";
            case 220323:
                return "伊通满族自治县";
            case 220381:
                return "公主岭市";
            case 220382:
                return "双辽市";
            case 2204:
                return "辽源市";
            case 220401:
                return "市辖区";
            case 220402:
                return "龙山区";
            case 220403:
                return "西安区";
            case 220421:
                return "东丰县";
            case 220422:
                return "东辽县";
            case 2205:
                return "通化市";
            case 220501:
                return "市辖区";
            case 220502:
                return "东昌区";
            case 220503:
                return "二道江区";
            case 220521:
                return "通化县";
            case 220523:
                return "辉南县";
            case 220524:
                return "柳河县";
            case 220581:
                return "梅河口市";
            case 220582:
                return "集安市";
            case 2206:
                return "白山市";
            case 220601:
                return "市辖区";
            case 220602:
                return "浑江区";
            case 220605:
                return "江源区";
            case 220621:
                return "抚松县";
            case 220622:
                return "靖宇县";
            case 220623:
                return "长白朝鲜族自治县";
            case 220681:
                return "临江市";
            case 2207:
                return "松原市";
            case 220701:
                return "市辖区";
            case 220702:
                return "宁江区";
            case 220721:
                return "前郭尔罗斯蒙古族自治县";
            case 220722:
                return "长岭县";
            case 220723:
                return "乾安县";
            case 220781:
                return "扶余市";
            case 2208:
                return "白城市";
            case 220801:
                return "市辖区";
            case 220802:
                return "洮北区";
            case 220821:
                return "镇赉县";
            case 220822:
                return "通榆县";
            case 220881:
                return "洮南市";
            case 220882:
                return "大安市";
            case 2224:
                return "延边朝鲜族自治州";
            case 222401:
                return "延吉市";
            case 222402:
                return "图们市";
            case 222403:
                return "敦化市";
            case 222404:
                return "珲春市";
            case 222405:
                return "龙井市";
            case 222406:
                return "和龙市";
            case 222424:
                return "汪清县";
            case 222426:
                return "安图县";
            case 23:
                return "黑龙江省";
            case 2301:
                return "哈尔滨市";
            case 230101:
                return "市辖区";
            case 230102:
                return "道里区";
            case 230103:
                return "南岗区";
            case 230104:
                return "道外区";
            case 230108:
                return "平房区";
            case 230109:
                return "松北区";
            case 230110:
                return "香坊区";
            case 230111:
                return "呼兰区";
            case 230112:
                return "阿城区";
            case 230123:
                return "依兰县";
            case 230124:
                return "方正县";
            case 230125:
                return "宾县";
            case 230126:
                return "巴彦县";
            case 230127:
                return "木兰县";
            case 230128:
                return "通河县";
            case 230129:
                return "延寿县";
            case 230182:
                return "双城市";
            case 230183:
                return "尚志市";
            case 230184:
                return "五常市";
            case 2302:
                return "齐齐哈尔市";
            case 230201:
                return "市辖区";
            case 230202:
                return "龙沙区";
            case 230203:
                return "建华区";
            case 230204:
                return "铁锋区";
            case 230205:
                return "昂昂溪区";
            case 230206:
                return "富拉尔基区";
            case 230207:
                return "碾子山区";
            case 230208:
                return "梅里斯达斡尔族区";
            case 230221:
                return "龙江县";
            case 230223:
                return "依安县";
            case 230224:
                return "泰来县";
            case 230225:
                return "甘南县";
            case 230227:
                return "富裕县";
            case 230229:
                return "克山县";
            case 230230:
                return "克东县";
            case 230231:
                return "拜泉县";
            case 230281:
                return "讷河市";
            case 2303:
                return "鸡西市";
            case 230301:
                return "市辖区";
            case 230302:
                return "鸡冠区";
            case 230303:
                return "恒山区";
            case 230304:
                return "滴道区";
            case 230305:
                return "梨树区";
            case 230306:
                return "城子河区";
            case 230307:
                return "麻山区";
            case 230321:
                return "鸡东县";
            case 230381:
                return "虎林市";
            case 230382:
                return "密山市";
            case 2304:
                return "鹤岗市";
            case 230401:
                return "市辖区";
            case 230402:
                return "向阳区";
            case 230403:
                return "工农区";
            case 230404:
                return "南山区";
            case 230405:
                return "兴安区";
            case 230406:
                return "东山区";
            case 230407:
                return "兴山区";
            case 230421:
                return "萝北县";
            case 230422:
                return "绥滨县";
            case 2305:
                return "双鸭山市";
            case 230501:
                return "市辖区";
            case 230502:
                return "尖山区";
            case 230503:
                return "岭东区";
            case 230505:
                return "四方台区";
            case 230506:
                return "宝山区";
            case 230521:
                return "集贤县";
            case 230522:
                return "友谊县";
            case 230523:
                return "宝清县";
            case 230524:
                return "饶河县";
            case 2306:
                return "大庆市";
            case 230601:
                return "市辖区";
            case 230602:
                return "萨尔图区";
            case 230603:
                return "龙凤区";
            case 230604:
                return "让胡路区";
            case 230605:
                return "红岗区";
            case 230606:
                return "大同区";
            case 230621:
                return "肇州县";
            case 230622:
                return "肇源县";
            case 230623:
                return "林甸县";
            case 230624:
                return "杜尔伯特蒙古族自治县";
            case 2307:
                return "伊春市";
            case 230701:
                return "市辖区";
            case 230702:
                return "伊春区";
            case 230703:
                return "南岔区";
            case 230704:
                return "友好区";
            case 230705:
                return "西林区";
            case 230706:
                return "翠峦区";
            case 230707:
                return "新青区";
            case 230708:
                return "美溪区";
            case 230709:
                return "金山屯区";
            case 230710:
                return "五营区";
            case 230711:
                return "乌马河区";
            case 230712:
                return "汤旺河区";
            case 230713:
                return "带岭区";
            case 230714:
                return "乌伊岭区";
            case 230715:
                return "红星区";
            case 230716:
                return "上甘岭区";
            case 230722:
                return "嘉荫县";
            case 230781:
                return "铁力市";
            case 2308:
                return "佳木斯市";
            case 230801:
                return "市辖区";
            case 230803:
                return "向阳区";
            case 230804:
                return "前进区";
            case 230805:
                return "东风区";
            case 230811:
                return "郊区";
            case 230822:
                return "桦南县";
            case 230826:
                return "桦川县";
            case 230828:
                return "汤原县";
            case 230833:
                return "抚远县";
            case 230881:
                return "同江市";
            case 230882:
                return "富锦市";
            case 2309:
                return "七台河市";
            case 230901:
                return "市辖区";
            case 230902:
                return "新兴区";
            case 230903:
                return "桃山区";
            case 230904:
                return "茄子河区";
            case 230921:
                return "勃利县";
            case 2310:
                return "牡丹江市";
            case 231001:
                return "市辖区";
            case 231002:
                return "东安区";
            case 231003:
                return "阳明区";
            case 231004:
                return "爱民区";
            case 231005:
                return "西安区";
            case 231024:
                return "东宁县";
            case 231025:
                return "林口县";
            case 231081:
                return "绥芬河市";
            case 231083:
                return "海林市";
            case 231084:
                return "宁安市";
            case 231085:
                return "穆棱市";
            case 2311:
                return "黑河市";
            case 231101:
                return "市辖区";
            case 231102:
                return "爱辉区";
            case 231121:
                return "嫩江县";
            case 231123:
                return "逊克县";
            case 231124:
                return "孙吴县";
            case 231181:
                return "北安市";
            case 231182:
                return "五大连池市";
            case 2312:
                return "绥化市";
            case 231201:
                return "市辖区";
            case 231202:
                return "北林区";
            case 231221:
                return "望奎县";
            case 231222:
                return "兰西县";
            case 231223:
                return "青冈县";
            case 231224:
                return "庆安县";
            case 231225:
                return "明水县";
            case 231226:
                return "绥棱县";
            case 231281:
                return "安达市";
            case 231282:
                return "肇东市";
            case 231283:
                return "海伦市";
            case 2327:
                return "大兴安岭地区";
            case 232721:
                return "呼玛县";
            case 232722:
                return "塔河县";
            case 232723:
                return "漠河县";
            case 31:
                return "上海市";
            case 3101:
                return "市辖区";
            case 310101:
                return "黄浦区";
            case 310104:
                return "徐汇区";
            case 310105:
                return "长宁区";
            case 310106:
                return "静安区";
            case 310107:
                return "普陀区";
            case 310108:
                return "闸北区";
            case 310109:
                return "虹口区";
            case 310110:
                return "杨浦区";
            case 310112:
                return "闵行区";
            case 310113:
                return "宝山区";
            case 310114:
                return "嘉定区";
            case 310115:
                return "浦东新区";
            case 310116:
                return "金山区";
            case 310117:
                return "松江区";
            case 310118:
                return "青浦区";
            case 310120:
                return "奉贤区";
            case 3102:
                return "县";
            case 310230:
                return "崇明县";
            case 32:
                return "江苏省";
            case 3201:
                return "南京市";
            case 320101:
                return "市辖区";
            case 320102:
                return "玄武区";
            case 320104:
                return "秦淮区";
            case 320105:
                return "建邺区";
            case 320106:
                return "鼓楼区";
            case 320111:
                return "浦口区";
            case 320113:
                return "栖霞区";
            case 320114:
                return "雨花台区";
            case 320115:
                return "江宁区";
            case 320116:
                return "六合区";
            case 320117:
                return "溧水区";
            case 320118:
                return "高淳区";
            case 3202:
                return "无锡市";
            case 320201:
                return "市辖区";
            case 320202:
                return "崇安区";
            case 320203:
                return "南长区";
            case 320204:
                return "北塘区";
            case 320205:
                return "锡山区";
            case 320206:
                return "惠山区";
            case 320211:
                return "滨湖区";
            case 320281:
                return "江阴市";
            case 320282:
                return "宜兴市";
            case 3203:
                return "徐州市";
            case 320301:
                return "市辖区";
            case 320302:
                return "鼓楼区";
            case 320303:
                return "云龙区";
            case 320305:
                return "贾汪区";
            case 320311:
                return "泉山区";
            case 320312:
                return "铜山区";
            case 320321:
                return "丰县";
            case 320322:
                return "沛县";
            case 320324:
                return "睢宁县";
            case 320381:
                return "新沂市";
            case 320382:
                return "邳州市";
            case 3204:
                return "常州市";
            case 320401:
                return "市辖区";
            case 320402:
                return "天宁区";
            case 320404:
                return "钟楼区";
            case 320405:
                return "戚墅堰区";
            case 320411:
                return "新北区";
            case 320412:
                return "武进区";
            case 320481:
                return "溧阳市";
            case 320482:
                return "金坛市";
            case 3205:
                return "苏州市";
            case 320501:
                return "市辖区";
            case 320505:
                return "虎丘区";
            case 320506:
                return "吴中区";
            case 320507:
                return "相城区";
            case 320508:
                return "姑苏区";
            case 320509:
                return "吴江区";
            case 320581:
                return "常熟市";
            case 320582:
                return "张家港市";
            case 320583:
                return "昆山市";
            case 320585:
                return "太仓市";
            case 3206:
                return "南通市";
            case 320601:
                return "市辖区";
            case 320602:
                return "崇川区";
            case 320611:
                return "港闸区";
            case 320612:
                return "通州区";
            case 320621:
                return "海安县";
            case 320623:
                return "如东县";
            case 320681:
                return "启东市";
            case 320682:
                return "如皋市";
            case 320684:
                return "海门市";
            case 3207:
                return "连云港市";
            case 320701:
                return "市辖区";
            case 320703:
                return "连云区";
            case 320706:
                return "海州区";
            case 320707:
                return "赣榆区";
            case 320722:
                return "东海县";
            case 320723:
                return "灌云县";
            case 320724:
                return "灌南县";
            case 3208:
                return "淮安市";
            case 320801:
                return "市辖区";
            case 320802:
                return "清河区";
            case 320803:
                return "淮安区";
            case 320804:
                return "淮阴区";
            case 320811:
                return "清浦区";
            case 320826:
                return "涟水县";
            case 320829:
                return "洪泽县";
            case 320830:
                return "盱眙县";
            case 320831:
                return "金湖县";
            case 3209:
                return "盐城市";
            case 320901:
                return "市辖区";
            case 320902:
                return "亭湖区";
            case 320903:
                return "盐都区";
            case 320921:
                return "响水县";
            case 320922:
                return "滨海县";
            case 320923:
                return "阜宁县";
            case 320924:
                return "射阳县";
            case 320925:
                return "建湖县";
            case 320981:
                return "东台市";
            case 320982:
                return "大丰市";
            case 3210:
                return "扬州市";
            case 321001:
                return "市辖区";
            case 321002:
                return "广陵区";
            case 321003:
                return "邗江区";
            case 321012:
                return "江都区";
            case 321023:
                return "宝应县";
            case 321081:
                return "仪征市";
            case 321084:
                return "高邮市";
            case 3211:
                return "镇江市";
            case 321101:
                return "市辖区";
            case 321102:
                return "京口区";
            case 321111:
                return "润州区";
            case 321112:
                return "丹徒区";
            case 321181:
                return "丹阳市";
            case 321182:
                return "扬中市";
            case 321183:
                return "句容市";
            case 3212:
                return "泰州市";
            case 321201:
                return "市辖区";
            case 321202:
                return "海陵区";
            case 321203:
                return "高港区";
            case 321204:
                return "姜堰区";
            case 321281:
                return "兴化市";
            case 321282:
                return "靖江市";
            case 321283:
                return "泰兴市";
            case 3213:
                return "宿迁市";
            case 321301:
                return "市辖区";
            case 321302:
                return "宿城区";
            case 321311:
                return "宿豫区";
            case 321322:
                return "沭阳县";
            case 321323:
                return "泗阳县";
            case 321324:
                return "泗洪县";
            case 33:
                return "浙江省";
            case 3301:
                return "杭州市";
            case 330101:
                return "市辖区";
            case 330102:
                return "上城区";
            case 330103:
                return "下城区";
            case 330104:
                return "江干区";
            case 330105:
                return "拱墅区";
            case 330106:
                return "西湖区";
            case 330108:
                return "滨江区";
            case 330109:
                return "萧山区";
            case 330110:
                return "余杭区";
            case 330122:
                return "桐庐县";
            case 330127:
                return "淳安县";
            case 330182:
                return "建德市";
            case 330183:
                return "富阳市";
            case 330185:
                return "临安市";
            case 3302:
                return "宁波市";
            case 330201:
                return "市辖区";
            case 330203:
                return "海曙区";
            case 330204:
                return "江东区";
            case 330205:
                return "江北区";
            case 330206:
                return "北仑区";
            case 330211:
                return "镇海区";
            case 330212:
                return "鄞州区";
            case 330225:
                return "象山县";
            case 330226:
                return "宁海县";
            case 330281:
                return "余姚市";
            case 330282:
                return "慈溪市";
            case 330283:
                return "奉化市";
            case 3303:
                return "温州市";
            case 330301:
                return "市辖区";
            case 330302:
                return "鹿城区";
            case 330303:
                return "龙湾区";
            case 330304:
                return "瓯海区";
            case 330322:
                return "洞头县";
            case 330324:
                return "永嘉县";
            case 330326:
                return "平阳县";
            case 330327:
                return "苍南县";
            case 330328:
                return "文成县";
            case 330329:
                return "泰顺县";
            case 330381:
                return "瑞安市";
            case 330382:
                return "乐清市";
            case 3304:
                return "嘉兴市";
            case 330401:
                return "市辖区";
            case 330402:
                return "南湖区";
            case 330411:
                return "秀洲区";
            case 330421:
                return "嘉善县";
            case 330424:
                return "海盐县";
            case 330481:
                return "海宁市";
            case 330482:
                return "平湖市";
            case 330483:
                return "桐乡市";
            case 3305:
                return "湖州市";
            case 330501:
                return "市辖区";
            case 330502:
                return "吴兴区";
            case 330503:
                return "南浔区";
            case 330521:
                return "德清县";
            case 330522:
                return "长兴县";
            case 330523:
                return "安吉县";
            case 3306:
                return "绍兴市";
            case 330601:
                return "市辖区";
            case 330602:
                return "越城区";
            case 330603:
                return "柯桥区";
            case 330604:
                return "上虞区";
            case 330624:
                return "新昌县";
            case 330681:
                return "诸暨市";
            case 330683:
                return "嵊州市";
            case 3307:
                return "金华市";
            case 330701:
                return "市辖区";
            case 330702:
                return "婺城区";
            case 330703:
                return "金东区";
            case 330723:
                return "武义县";
            case 330726:
                return "浦江县";
            case 330727:
                return "磐安县";
            case 330781:
                return "兰溪市";
            case 330782:
                return "义乌市";
            case 330783:
                return "东阳市";
            case 330784:
                return "永康市";
            case 3308:
                return "衢州市";
            case 330801:
                return "市辖区";
            case 330802:
                return "柯城区";
            case 330803:
                return "衢江区";
            case 330822:
                return "常山县";
            case 330824:
                return "开化县";
            case 330825:
                return "龙游县";
            case 330881:
                return "江山市";
            case 3309:
                return "舟山市";
            case 330901:
                return "市辖区";
            case 330902:
                return "定海区";
            case 330903:
                return "普陀区";
            case 330921:
                return "岱山县";
            case 330922:
                return "嵊泗县";
            case 3310:
                return "台州市";
            case 331001:
                return "市辖区";
            case 331002:
                return "椒江区";
            case 331003:
                return "黄岩区";
            case 331004:
                return "路桥区";
            case 331021:
                return "玉环县";
            case 331022:
                return "三门县";
            case 331023:
                return "天台县";
            case 331024:
                return "仙居县";
            case 331081:
                return "温岭市";
            case 331082:
                return "临海市";
            case 3311:
                return "丽水市";
            case 331101:
                return "市辖区";
            case 331102:
                return "莲都区";
            case 331121:
                return "青田县";
            case 331122:
                return "缙云县";
            case 331123:
                return "遂昌县";
            case 331124:
                return "松阳县";
            case 331125:
                return "云和县";
            case 331126:
                return "庆元县";
            case 331127:
                return "景宁畲族自治县";
            case 331181:
                return "龙泉市";
            case 34:
                return "安徽省";
            case 3401:
                return "合肥市";
            case 340101:
                return "市辖区";
            case 340102:
                return "瑶海区";
            case 340103:
                return "庐阳区";
            case 340104:
                return "蜀山区";
            case 340111:
                return "包河区";
            case 340121:
                return "长丰县";
            case 340122:
                return "肥东县";
            case 340123:
                return "肥西县";
            case 340124:
                return "庐江县";
            case 340181:
                return "巢湖市";
            case 3402:
                return "芜湖市";
            case 340201:
                return "市辖区";
            case 340202:
                return "镜湖区";
            case 340203:
                return "弋江区";
            case 340207:
                return "鸠江区";
            case 340208:
                return "三山区";
            case 340221:
                return "芜湖县";
            case 340222:
                return "繁昌县";
            case 340223:
                return "南陵县";
            case 340225:
                return "无为县";
            case 3403:
                return "蚌埠市";
            case 340301:
                return "市辖区";
            case 340302:
                return "龙子湖区";
            case 340303:
                return "蚌山区";
            case 340304:
                return "禹会区";
            case 340311:
                return "淮上区";
            case 340321:
                return "怀远县";
            case 340322:
                return "五河县";
            case 340323:
                return "固镇县";
            case 3404:
                return "淮南市";
            case 340401:
                return "市辖区";
            case 340402:
                return "大通区";
            case 340403:
                return "田家庵区";
            case 340404:
                return "谢家集区";
            case 340405:
                return "八公山区";
            case 340406:
                return "潘集区";
            case 340421:
                return "凤台县";
            case 3405:
                return "马鞍山市";
            case 340501:
                return "市辖区";
            case 340503:
                return "花山区";
            case 340504:
                return "雨山区";
            case 340506:
                return "博望区";
            case 340521:
                return "当涂县";
            case 340522:
                return "含山县";
            case 340523:
                return "和县";
            case 3406:
                return "淮北市";
            case 340601:
                return "市辖区";
            case 340602:
                return "杜集区";
            case 340603:
                return "相山区";
            case 340604:
                return "烈山区";
            case 340621:
                return "濉溪县";
            case 3407:
                return "铜陵市";
            case 340701:
                return "市辖区";
            case 340702:
                return "铜官山区";
            case 340703:
                return "狮子山区";
            case 340711:
                return "郊区";
            case 340721:
                return "铜陵县";
            case 3408:
                return "安庆市";
            case 340801:
                return "市辖区";
            case 340802:
                return "迎江区";
            case 340803:
                return "大观区";
            case 340811:
                return "宜秀区";
            case 340822:
                return "怀宁县";
            case 340823:
                return "枞阳县";
            case 340824:
                return "潜山县";
            case 340825:
                return "太湖县";
            case 340826:
                return "宿松县";
            case 340827:
                return "望江县";
            case 340828:
                return "岳西县";
            case 340881:
                return "桐城市";
            case 3410:
                return "黄山市";
            case 341001:
                return "市辖区";
            case 341002:
                return "屯溪区";
            case 341003:
                return "黄山区";
            case 341004:
                return "徽州区";
            case 341021:
                return "歙县";
            case 341022:
                return "休宁县";
            case 341023:
                return "黟县";
            case 341024:
                return "祁门县";
            case 3411:
                return "滁州市";
            case 341101:
                return "市辖区";
            case 341102:
                return "琅琊区";
            case 341103:
                return "南谯区";
            case 341122:
                return "来安县";
            case 341124:
                return "全椒县";
            case 341125:
                return "定远县";
            case 341126:
                return "凤阳县";
            case 341181:
                return "天长市";
            case 341182:
                return "明光市";
            case 3412:
                return "阜阳市";
            case 341201:
                return "市辖区";
            case 341202:
                return "颍州区";
            case 341203:
                return "颍东区";
            case 341204:
                return "颍泉区";
            case 341221:
                return "临泉县";
            case 341222:
                return "太和县";
            case 341225:
                return "阜南县";
            case 341226:
                return "颍上县";
            case 341282:
                return "界首市";
            case 3413:
                return "宿州市";
            case 341301:
                return "市辖区";
            case 341302:
                return "埇桥区";
            case 341321:
                return "砀山县";
            case 341322:
                return "萧县";
            case 341323:
                return "灵璧县";
            case 341324:
                return "泗县";
            case 3415:
                return "六安市";
            case 341501:
                return "市辖区";
            case 341502:
                return "金安区";
            case 341503:
                return "裕安区";
            case 341521:
                return "寿县";
            case 341522:
                return "霍邱县";
            case 341523:
                return "舒城县";
            case 341524:
                return "金寨县";
            case 341525:
                return "霍山县";
            case 3416:
                return "亳州市";
            case 341601:
                return "市辖区";
            case 341602:
                return "谯城区";
            case 341621:
                return "涡阳县";
            case 341622:
                return "蒙城县";
            case 341623:
                return "利辛县";
            case 3417:
                return "池州市";
            case 341701:
                return "市辖区";
            case 341702:
                return "贵池区";
            case 341721:
                return "东至县";
            case 341722:
                return "石台县";
            case 341723:
                return "青阳县";
            case 3418:
                return "宣城市";
            case 341801:
                return "市辖区";
            case 341802:
                return "宣州区";
            case 341821:
                return "郎溪县";
            case 341822:
                return "广德县";
            case 341823:
                return "泾县";
            case 341824:
                return "绩溪县";
            case 341825:
                return "旌德县";
            case 341881:
                return "宁国市";
            case 35:
                return "福建省";
            case 3501:
                return "福州市";
            case 350101:
                return "市辖区";
            case 350102:
                return "鼓楼区";
            case 350103:
                return "台江区";
            case 350104:
                return "仓山区";
            case 350105:
                return "马尾区";
            case 350111:
                return "晋安区";
            case 350121:
                return "闽侯县";
            case 350122:
                return "连江县";
            case 350123:
                return "罗源县";
            case 350124:
                return "闽清县";
            case 350125:
                return "永泰县";
            case 350128:
                return "平潭县";
            case 350181:
                return "福清市";
            case 350182:
                return "长乐市";
            case 3502:
                return "厦门市";
            case 350201:
                return "市辖区";
            case 350203:
                return "思明区";
            case 350205:
                return "海沧区";
            case 350206:
                return "湖里区";
            case 350211:
                return "集美区";
            case 350212:
                return "同安区";
            case 350213:
                return "翔安区";
            case 3503:
                return "莆田市";
            case 350301:
                return "市辖区";
            case 350302:
                return "城厢区";
            case 350303:
                return "涵江区";
            case 350304:
                return "荔城区";
            case 350305:
                return "秀屿区";
            case 350322:
                return "仙游县";
            case 3504:
                return "三明市";
            case 350401:
                return "市辖区";
            case 350402:
                return "梅列区";
            case 350403:
                return "三元区";
            case 350421:
                return "明溪县";
            case 350423:
                return "清流县";
            case 350424:
                return "宁化县";
            case 350425:
                return "大田县";
            case 350426:
                return "尤溪县";
            case 350427:
                return "沙县";
            case 350428:
                return "将乐县";
            case 350429:
                return "泰宁县";
            case 350430:
                return "建宁县";
            case 350481:
                return "永安市";
            case 3505:
                return "泉州市";
            case 350501:
                return "市辖区";
            case 350502:
                return "鲤城区";
            case 350503:
                return "丰泽区";
            case 350504:
                return "洛江区";
            case 350505:
                return "泉港区";
            case 350521:
                return "惠安县";
            case 350524:
                return "安溪县";
            case 350525:
                return "永春县";
            case 350526:
                return "德化县";
            case 350527:
                return "金门县";
            case 350581:
                return "石狮市";
            case 350582:
                return "晋江市";
            case 350583:
                return "南安市";
            case 3506:
                return "漳州市";
            case 350601:
                return "市辖区";
            case 350602:
                return "芗城区";
            case 350603:
                return "龙文区";
            case 350622:
                return "云霄县";
            case 350623:
                return "漳浦县";
            case 350624:
                return "诏安县";
            case 350625:
                return "长泰县";
            case 350626:
                return "东山县";
            case 350627:
                return "南靖县";
            case 350628:
                return "平和县";
            case 350629:
                return "华安县";
            case 350681:
                return "龙海市";
            case 3507:
                return "南平市";
            case 350701:
                return "市辖区";
            case 350702:
                return "延平区";
            case 350721:
                return "顺昌县";
            case 350722:
                return "浦城县";
            case 350723:
                return "光泽县";
            case 350724:
                return "松溪县";
            case 350725:
                return "政和县";
            case 350781:
                return "邵武市";
            case 350782:
                return "武夷山市";
            case 350783:
                return "建瓯市";
            case 350784:
                return "建阳市";
            case 3508:
                return "龙岩市";
            case 350801:
                return "市辖区";
            case 350802:
                return "新罗区";
            case 350821:
                return "长汀县";
            case 350822:
                return "永定县";
            case 350823:
                return "上杭县";
            case 350824:
                return "武平县";
            case 350825:
                return "连城县";
            case 350881:
                return "漳平市";
            case 3509:
                return "宁德市";
            case 350901:
                return "市辖区";
            case 350902:
                return "蕉城区";
            case 350921:
                return "霞浦县";
            case 350922:
                return "古田县";
            case 350923:
                return "屏南县";
            case 350924:
                return "寿宁县";
            case 350925:
                return "周宁县";
            case 350926:
                return "柘荣县";
            case 350981:
                return "福安市";
            case 350982:
                return "福鼎市";
            case 36:
                return "江西省";
            case 3601:
                return "南昌市";
            case 360101:
                return "市辖区";
            case 360102:
                return "东湖区";
            case 360103:
                return "西湖区";
            case 360104:
                return "青云谱区";
            case 360105:
                return "湾里区";
            case 360111:
                return "青山湖区";
            case 360121:
                return "南昌县";
            case 360122:
                return "新建县";
            case 360123:
                return "安义县";
            case 360124:
                return "进贤县";
            case 3602:
                return "景德镇市";
            case 360201:
                return "市辖区";
            case 360202:
                return "昌江区";
            case 360203:
                return "珠山区";
            case 360222:
                return "浮梁县";
            case 360281:
                return "乐平市";
            case 3603:
                return "萍乡市";
            case 360301:
                return "市辖区";
            case 360302:
                return "安源区";
            case 360313:
                return "湘东区";
            case 360321:
                return "莲花县";
            case 360322:
                return "上栗县";
            case 360323:
                return "芦溪县";
            case 3604:
                return "九江市";
            case 360401:
                return "市辖区";
            case 360402:
                return "庐山区";
            case 360403:
                return "浔阳区";
            case 360421:
                return "九江县";
            case 360423:
                return "武宁县";
            case 360424:
                return "修水县";
            case 360425:
                return "永修县";
            case 360426:
                return "德安县";
            case 360427:
                return "星子县";
            case 360428:
                return "都昌县";
            case 360429:
                return "湖口县";
            case 360430:
                return "彭泽县";
            case 360481:
                return "瑞昌市";
            case 360482:
                return "共青城市";
            case 3605:
                return "新余市";
            case 360501:
                return "市辖区";
            case 360502:
                return "渝水区";
            case 360521:
                return "分宜县";
            case 3606:
                return "鹰潭市";
            case 360601:
                return "市辖区";
            case 360602:
                return "月湖区";
            case 360622:
                return "余江县";
            case 360681:
                return "贵溪市";
            case 3607:
                return "赣州市";
            case 360701:
                return "市辖区";
            case 360702:
                return "章贡区";
            case 360703:
                return "南康区";
            case 360721:
                return "赣县";
            case 360722:
                return "信丰县";
            case 360723:
                return "大余县";
            case 360724:
                return "上犹县";
            case 360725:
                return "崇义县";
            case 360726:
                return "安远县";
            case 360727:
                return "龙南县";
            case 360728:
                return "定南县";
            case 360729:
                return "全南县";
            case 360730:
                return "宁都县";
            case 360731:
                return "于都县";
            case 360732:
                return "兴国县";
            case 360733:
                return "会昌县";
            case 360734:
                return "寻乌县";
            case 360735:
                return "石城县";
            case 360781:
                return "瑞金市";
            case 3608:
                return "吉安市";
            case 360801:
                return "市辖区";
            case 360802:
                return "吉州区";
            case 360803:
                return "青原区";
            case 360821:
                return "吉安县";
            case 360822:
                return "吉水县";
            case 360823:
                return "峡江县";
            case 360824:
                return "新干县";
            case 360825:
                return "永丰县";
            case 360826:
                return "泰和县";
            case 360827:
                return "遂川县";
            case 360828:
                return "万安县";
            case 360829:
                return "安福县";
            case 360830:
                return "永新县";
            case 360881:
                return "井冈山市";
            case 3609:
                return "宜春市";
            case 360901:
                return "市辖区";
            case 360902:
                return "袁州区";
            case 360921:
                return "奉新县";
            case 360922:
                return "万载县";
            case 360923:
                return "上高县";
            case 360924:
                return "宜丰县";
            case 360925:
                return "靖安县";
            case 360926:
                return "铜鼓县";
            case 360981:
                return "丰城市";
            case 360982:
                return "樟树市";
            case 360983:
                return "高安市";
            case 3610:
                return "抚州市";
            case 361001:
                return "市辖区";
            case 361002:
                return "临川区";
            case 361021:
                return "南城县";
            case 361022:
                return "黎川县";
            case 361023:
                return "南丰县";
            case 361024:
                return "崇仁县";
            case 361025:
                return "乐安县";
            case 361026:
                return "宜黄县";
            case 361027:
                return "金溪县";
            case 361028:
                return "资溪县";
            case 361029:
                return "东乡县";
            case 361030:
                return "广昌县";
            case 3611:
                return "上饶市";
            case 361101:
                return "市辖区";
            case 361102:
                return "信州区";
            case 361121:
                return "上饶县";
            case 361122:
                return "广丰县";
            case 361123:
                return "玉山县";
            case 361124:
                return "铅山县";
            case 361125:
                return "横峰县";
            case 361126:
                return "弋阳县";
            case 361127:
                return "余干县";
            case 361128:
                return "鄱阳县";
            case 361129:
                return "万年县";
            case 361130:
                return "婺源县";
            case 361181:
                return "德兴市";
            case 37:
                return "山东省";
            case 3701:
                return "济南市";
            case 370101:
                return "市辖区";
            case 370102:
                return "历下区";
            case 370103:
                return "市中区";
            case 370104:
                return "槐荫区";
            case 370105:
                return "天桥区";
            case 370112:
                return "历城区";
            case 370113:
                return "长清区";
            case 370124:
                return "平阴县";
            case 370125:
                return "济阳县";
            case 370126:
                return "商河县";
            case 370181:
                return "章丘市";
            case 3702:
                return "青岛市";
            case 370201:
                return "市辖区";
            case 370202:
                return "市南区";
            case 370203:
                return "市北区";
            case 370211:
                return "黄岛区";
            case 370212:
                return "崂山区";
            case 370213:
                return "李沧区";
            case 370214:
                return "城阳区";
            case 370281:
                return "胶州市";
            case 370282:
                return "即墨市";
            case 370283:
                return "平度市";
            case 370285:
                return "莱西市";
            case 3703:
                return "淄博市";
            case 370301:
                return "市辖区";
            case 370302:
                return "淄川区";
            case 370303:
                return "张店区";
            case 370304:
                return "博山区";
            case 370305:
                return "临淄区";
            case 370306:
                return "周村区";
            case 370321:
                return "桓台县";
            case 370322:
                return "高青县";
            case 370323:
                return "沂源县";
            case 3704:
                return "枣庄市";
            case 370401:
                return "市辖区";
            case 370402:
                return "市中区";
            case 370403:
                return "薛城区";
            case 370404:
                return "峄城区";
            case 370405:
                return "台儿庄区";
            case 370406:
                return "山亭区";
            case 370481:
                return "滕州市";
            case 3705:
                return "东营市";
            case 370501:
                return "市辖区";
            case 370502:
                return "东营区";
            case 370503:
                return "河口区";
            case 370521:
                return "垦利县";
            case 370522:
                return "利津县";
            case 370523:
                return "广饶县";
            case 3706:
                return "烟台市";
            case 370601:
                return "市辖区";
            case 370602:
                return "芝罘区";
            case 370611:
                return "福山区";
            case 370612:
                return "牟平区";
            case 370613:
                return "莱山区";
            case 370634:
                return "长岛县";
            case 370681:
                return "龙口市";
            case 370682:
                return "莱阳市";
            case 370683:
                return "莱州市";
            case 370684:
                return "蓬莱市";
            case 370685:
                return "招远市";
            case 370686:
                return "栖霞市";
            case 370687:
                return "海阳市";
            case 3707:
                return "潍坊市";
            case 370701:
                return "市辖区";
            case 370702:
                return "潍城区";
            case 370703:
                return "寒亭区";
            case 370704:
                return "坊子区";
            case 370705:
                return "奎文区";
            case 370724:
                return "临朐县";
            case 370725:
                return "昌乐县";
            case 370781:
                return "青州市";
            case 370782:
                return "诸城市";
            case 370783:
                return "寿光市";
            case 370784:
                return "安丘市";
            case 370785:
                return "高密市";
            case 370786:
                return "昌邑市";
            case 3708:
                return "济宁市";
            case 370801:
                return "市辖区";
            case 370811:
                return "任城区";
            case 370812:
                return "兖州区";
            case 370826:
                return "微山县";
            case 370827:
                return "鱼台县";
            case 370828:
                return "金乡县";
            case 370829:
                return "嘉祥县";
            case 370830:
                return "汶上县";
            case 370831:
                return "泗水县";
            case 370832:
                return "梁山县";
            case 370881:
                return "曲阜市";
            case 370883:
                return "邹城市";
            case 3709:
                return "泰安市";
            case 370901:
                return "市辖区";
            case 370902:
                return "泰山区";
            case 370911:
                return "岱岳区";
            case 370921:
                return "宁阳县";
            case 370923:
                return "东平县";
            case 370982:
                return "新泰市";
            case 370983:
                return "肥城市";
            case 3710:
                return "威海市";
            case 371001:
                return "市辖区";
            case 371002:
                return "环翠区";
            case 371003:
                return "文登区";
            case 371082:
                return "荣成市";
            case 371083:
                return "乳山市";
            case 3711:
                return "日照市";
            case 371101:
                return "市辖区";
            case 371102:
                return "东港区";
            case 371103:
                return "岚山区";
            case 371121:
                return "五莲县";
            case 371122:
                return "莒县";
            case 3712:
                return "莱芜市";
            case 371201:
                return "市辖区";
            case 371202:
                return "莱城区";
            case 371203:
                return "钢城区";
            case 3713:
                return "临沂市";
            case 371301:
                return "市辖区";
            case 371302:
                return "兰山区";
            case 371311:
                return "罗庄区";
            case 371312:
                return "河东区";
            case 371321:
                return "沂南县";
            case 371322:
                return "郯城县";
            case 371323:
                return "沂水县";
            case 371324:
                return "兰陵县";
            case 371325:
                return "费县";
            case 371326:
                return "平邑县";
            case 371327:
                return "莒南县";
            case 371328:
                return "蒙阴县";
            case 371329:
                return "临沭县";
            case 3714:
                return "德州市";
            case 371401:
                return "市辖区";
            case 371402:
                return "德城区";
            case 371403:
                return "陵城区";
            case 371422:
                return "宁津县";
            case 371423:
                return "庆云县";
            case 371424:
                return "临邑县";
            case 371425:
                return "齐河县";
            case 371426:
                return "平原县";
            case 371427:
                return "夏津县";
            case 371428:
                return "武城县";
            case 371481:
                return "乐陵市";
            case 371482:
                return "禹城市";
            case 3715:
                return "聊城市";
            case 371501:
                return "市辖区";
            case 371502:
                return "东昌府区";
            case 371521:
                return "阳谷县";
            case 371522:
                return "莘县";
            case 371523:
                return "茌平县";
            case 371524:
                return "东阿县";
            case 371525:
                return "冠县";
            case 371526:
                return "高唐县";
            case 371581:
                return "临清市";
            case 3716:
                return "滨州市";
            case 371601:
                return "市辖区";
            case 371602:
                return "滨城区";
            case 371603:
                return "沾化区";
            case 371621:
                return "惠民县";
            case 371622:
                return "阳信县";
            case 371623:
                return "无棣县";
            case 371625:
                return "博兴县";
            case 371626:
                return "邹平县";
            case 3717:
                return "菏泽市";
            case 371701:
                return "市辖区";
            case 371702:
                return "牡丹区";
            case 371721:
                return "曹县";
            case 371722:
                return "单县";
            case 371723:
                return "成武县";
            case 371724:
                return "巨野县";
            case 371725:
                return "郓城县";
            case 371726:
                return "鄄城县";
            case 371727:
                return "定陶县";
            case 371728:
                return "东明县";
            case 41:
                return "河南省";
            case 4101:
                return "郑州市";
            case 410101:
                return "市辖区";
            case 410102:
                return "中原区";
            case 410103:
                return "二七区";
            case 410104:
                return "管城回族区";
            case 410105:
                return "金水区";
            case 410106:
                return "上街区";
            case 410108:
                return "惠济区";
            case 410122:
                return "中牟县";
            case 410181:
                return "巩义市";
            case 410182:
                return "荥阳市";
            case 410183:
                return "新密市";
            case 410184:
                return "新郑市";
            case 410185:
                return "登封市";
            case 4102:
                return "开封市";
            case 410201:
                return "市辖区";
            case 410202:
                return "龙亭区";
            case 410203:
                return "顺河回族区";
            case 410204:
                return "鼓楼区";
            case 410205:
                return "禹王台区";
            case 410211:
                return "金明区";
            case 410221:
                return "杞县";
            case 410222:
                return "通许县";
            case 410223:
                return "尉氏县";
            case 410224:
                return "开封县";
            case 410225:
                return "兰考县";
            case 4103:
                return "洛阳市";
            case 410301:
                return "市辖区";
            case 410302:
                return "老城区";
            case 410303:
                return "西工区";
            case 410304:
                return "瀍河回族区";
            case 410305:
                return "涧西区";
            case 410306:
                return "吉利区";
            case 410311:
                return "洛龙区";
            case 410322:
                return "孟津县";
            case 410323:
                return "新安县";
            case 410324:
                return "栾川县";
            case 410325:
                return "嵩县";
            case 410326:
                return "汝阳县";
            case 410327:
                return "宜阳县";
            case 410328:
                return "洛宁县";
            case 410329:
                return "伊川县";
            case 410381:
                return "偃师市";
            case 4104:
                return "平顶山市";
            case 410401:
                return "市辖区";
            case 410402:
                return "新华区";
            case 410403:
                return "卫东区";
            case 410404:
                return "石龙区";
            case 410411:
                return "湛河区";
            case 410421:
                return "宝丰县";
            case 410422:
                return "叶县";
            case 410423:
                return "鲁山县";
            case 410425:
                return "郏县";
            case 410481:
                return "舞钢市";
            case 410482:
                return "汝州市";
            case 4105:
                return "安阳市";
            case 410501:
                return "市辖区";
            case 410502:
                return "文峰区";
            case 410503:
                return "北关区";
            case 410505:
                return "殷都区";
            case 410506:
                return "龙安区";
            case 410522:
                return "安阳县";
            case 410523:
                return "汤阴县";
            case 410526:
                return "滑县";
            case 410527:
                return "内黄县";
            case 410581:
                return "林州市";
            case 4106:
                return "鹤壁市";
            case 410601:
                return "市辖区";
            case 410602:
                return "鹤山区";
            case 410603:
                return "山城区";
            case 410611:
                return "淇滨区";
            case 410621:
                return "浚县";
            case 410622:
                return "淇县";
            case 4107:
                return "新乡市";
            case 410701:
                return "市辖区";
            case 410702:
                return "红旗区";
            case 410703:
                return "卫滨区";
            case 410704:
                return "凤泉区";
            case 410711:
                return "牧野区";
            case 410721:
                return "新乡县";
            case 410724:
                return "获嘉县";
            case 410725:
                return "原阳县";
            case 410726:
                return "延津县";
            case 410727:
                return "封丘县";
            case 410728:
                return "长垣县";
            case 410781:
                return "卫辉市";
            case 410782:
                return "辉县市";
            case 4108:
                return "焦作市";
            case 410801:
                return "市辖区";
            case 410802:
                return "解放区";
            case 410803:
                return "中站区";
            case 410804:
                return "马村区";
            case 410811:
                return "山阳区";
            case 410821:
                return "修武县";
            case 410822:
                return "博爱县";
            case 410823:
                return "武陟县";
            case 410825:
                return "温县";
            case 410882:
                return "沁阳市";
            case 410883:
                return "孟州市";
            case 4109:
                return "濮阳市";
            case 410901:
                return "市辖区";
            case 410902:
                return "华龙区";
            case 410922:
                return "清丰县";
            case 410923:
                return "南乐县";
            case 410926:
                return "范县";
            case 410927:
                return "台前县";
            case 410928:
                return "濮阳县";
            case 4110:
                return "许昌市";
            case 411001:
                return "市辖区";
            case 411002:
                return "魏都区";
            case 411023:
                return "许昌县";
            case 411024:
                return "鄢陵县";
            case 411025:
                return "襄城县";
            case 411081:
                return "禹州市";
            case 411082:
                return "长葛市";
            case 4111:
                return "漯河市";
            case 411101:
                return "市辖区";
            case 411102:
                return "源汇区";
            case 411103:
                return "郾城区";
            case 411104:
                return "召陵区";
            case 411121:
                return "舞阳县";
            case 411122:
                return "临颍县";
            case 4112:
                return "三门峡市";
            case 411201:
                return "市辖区";
            case 411202:
                return "湖滨区";
            case 411221:
                return "渑池县";
            case 411222:
                return "陕县";
            case 411224:
                return "卢氏县";
            case 411281:
                return "义马市";
            case 411282:
                return "灵宝市";
            case 4113:
                return "南阳市";
            case 411301:
                return "市辖区";
            case 411302:
                return "宛城区";
            case 411303:
                return "卧龙区";
            case 411321:
                return "南召县";
            case 411322:
                return "方城县";
            case 411323:
                return "西峡县";
            case 411324:
                return "镇平县";
            case 411325:
                return "内乡县";
            case 411326:
                return "淅川县";
            case 411327:
                return "社旗县";
            case 411328:
                return "唐河县";
            case 411329:
                return "新野县";
            case 411330:
                return "桐柏县";
            case 411381:
                return "邓州市";
            case 4114:
                return "商丘市";
            case 411401:
                return "市辖区";
            case 411402:
                return "梁园区";
            case 411403:
                return "睢阳区";
            case 411421:
                return "民权县";
            case 411422:
                return "睢县";
            case 411423:
                return "宁陵县";
            case 411424:
                return "柘城县";
            case 411425:
                return "虞城县";
            case 411426:
                return "夏邑县";
            case 411481:
                return "永城市";
            case 4115:
                return "信阳市";
            case 411501:
                return "市辖区";
            case 411502:
                return "浉河区";
            case 411503:
                return "平桥区";
            case 411521:
                return "罗山县";
            case 411522:
                return "光山县";
            case 411523:
                return "新县";
            case 411524:
                return "商城县";
            case 411525:
                return "固始县";
            case 411526:
                return "潢川县";
            case 411527:
                return "淮滨县";
            case 411528:
                return "息县";
            case 4116:
                return "周口市";
            case 411601:
                return "市辖区";
            case 411602:
                return "川汇区";
            case 411621:
                return "扶沟县";
            case 411622:
                return "西华县";
            case 411623:
                return "商水县";
            case 411624:
                return "沈丘县";
            case 411625:
                return "郸城县";
            case 411626:
                return "淮阳县";
            case 411627:
                return "太康县";
            case 411628:
                return "鹿邑县";
            case 411681:
                return "项城市";
            case 4117:
                return "驻马店市";
            case 411701:
                return "市辖区";
            case 411702:
                return "驿城区";
            case 411721:
                return "西平县";
            case 411722:
                return "上蔡县";
            case 411723:
                return "平舆县";
            case 411724:
                return "正阳县";
            case 411725:
                return "确山县";
            case 411726:
                return "泌阳县";
            case 411727:
                return "汝南县";
            case 411728:
                return "遂平县";
            case 411729:
                return "新蔡县";
            case 4190:
                return "省直辖县级行政区划";
            case 419001:
                return "济源市";
            case 42:
                return "湖北省";
            case 4201:
                return "武汉市";
            case 420101:
                return "市辖区";
            case 420102:
                return "江岸区";
            case 420103:
                return "江汉区";
            case 420104:
                return "硚口区";
            case 420105:
                return "汉阳区";
            case 420106:
                return "武昌区";
            case 420107:
                return "青山区";
            case 420111:
                return "洪山区";
            case 420112:
                return "东西湖区";
            case 420113:
                return "汉南区";
            case 420114:
                return "蔡甸区";
            case 420115:
                return "江夏区";
            case 420116:
                return "黄陂区";
            case 420117:
                return "新洲区";
            case 4202:
                return "黄石市";
            case 420201:
                return "市辖区";
            case 420202:
                return "黄石港区";
            case 420203:
                return "西塞山区";
            case 420204:
                return "下陆区";
            case 420205:
                return "铁山区";
            case 420222:
                return "阳新县";
            case 420281:
                return "大冶市";
            case 4203:
                return "十堰市";
            case 420301:
                return "市辖区";
            case 420302:
                return "茅箭区";
            case 420303:
                return "张湾区";
            case 420304:
                return "郧阳区";
            case 420322:
                return "郧西县";
            case 420323:
                return "竹山县";
            case 420324:
                return "竹溪县";
            case 420325:
                return "房县";
            case 420381:
                return "丹江口市";
            case 4205:
                return "宜昌市";
            case 420501:
                return "市辖区";
            case 420502:
                return "西陵区";
            case 420503:
                return "伍家岗区";
            case 420504:
                return "点军区";
            case 420505:
                return "猇亭区";
            case 420506:
                return "夷陵区";
            case 420525:
                return "远安县";
            case 420526:
                return "兴山县";
            case 420527:
                return "秭归县";
            case 420528:
                return "长阳土家族自治县";
            case 420529:
                return "五峰土家族自治县";
            case 420581:
                return "宜都市";
            case 420582:
                return "当阳市";
            case 420583:
                return "枝江市";
            case 4206:
                return "襄阳市";
            case 420601:
                return "市辖区";
            case 420602:
                return "襄城区";
            case 420606:
                return "樊城区";
            case 420607:
                return "襄州区";
            case 420624:
                return "南漳县";
            case 420625:
                return "谷城县";
            case 420626:
                return "保康县";
            case 420682:
                return "老河口市";
            case 420683:
                return "枣阳市";
            case 420684:
                return "宜城市";
            case 4207:
                return "鄂州市";
            case 420701:
                return "市辖区";
            case 420702:
                return "梁子湖区";
            case 420703:
                return "华容区";
            case 420704:
                return "鄂城区";
            case 4208:
                return "荆门市";
            case 420801:
                return "市辖区";
            case 420802:
                return "东宝区";
            case 420804:
                return "掇刀区";
            case 420821:
                return "京山县";
            case 420822:
                return "沙洋县";
            case 420881:
                return "钟祥市";
            case 4209:
                return "孝感市";
            case 420901:
                return "市辖区";
            case 420902:
                return "孝南区";
            case 420921:
                return "孝昌县";
            case 420922:
                return "大悟县";
            case 420923:
                return "云梦县";
            case 420981:
                return "应城市";
            case 420982:
                return "安陆市";
            case 420984:
                return "汉川市";
            case 4210:
                return "荆州市";
            case 421001:
                return "市辖区";
            case 421002:
                return "沙市区";
            case 421003:
                return "荆州区";
            case 421022:
                return "公安县";
            case 421023:
                return "监利县";
            case 421024:
                return "江陵县";
            case 421081:
                return "石首市";
            case 421083:
                return "洪湖市";
            case 421087:
                return "松滋市";
            case 4211:
                return "黄冈市";
            case 421101:
                return "市辖区";
            case 421102:
                return "黄州区";
            case 421121:
                return "团风县";
            case 421122:
                return "红安县";
            case 421123:
                return "罗田县";
            case 421124:
                return "英山县";
            case 421125:
                return "浠水县";
            case 421126:
                return "蕲春县";
            case 421127:
                return "黄梅县";
            case 421181:
                return "麻城市";
            case 421182:
                return "武穴市";
            case 4212:
                return "咸宁市";
            case 421201:
                return "市辖区";
            case 421202:
                return "咸安区";
            case 421221:
                return "嘉鱼县";
            case 421222:
                return "通城县";
            case 421223:
                return "崇阳县";
            case 421224:
                return "通山县";
            case 421281:
                return "赤壁市";
            case 4213:
                return "随州市";
            case 421301:
                return "市辖区";
            case 421303:
                return "曾都区";
            case 421321:
                return "随县";
            case 421381:
                return "广水市";
            case 4228:
                return "恩施土家族苗族自治州";
            case 422801:
                return "恩施市";
            case 422802:
                return "利川市";
            case 422822:
                return "建始县";
            case 422823:
                return "巴东县";
            case 422825:
                return "宣恩县";
            case 422826:
                return "咸丰县";
            case 422827:
                return "来凤县";
            case 422828:
                return "鹤峰县";
            case 4290:
                return "省直辖县级行政区划";
            case 429004:
                return "仙桃市";
            case 429005:
                return "潜江市";
            case 429006:
                return "天门市";
            case 429021:
                return "神农架林区";
            case 43:
                return "湖南省";
            case 4301:
                return "长沙市";
            case 430101:
                return "市辖区";
            case 430102:
                return "芙蓉区";
            case 430103:
                return "天心区";
            case 430104:
                return "岳麓区";
            case 430105:
                return "开福区";
            case 430111:
                return "雨花区";
            case 430112:
                return "望城区";
            case 430121:
                return "长沙县";
            case 430124:
                return "宁乡县";
            case 430181:
                return "浏阳市";
            case 4302:
                return "株洲市";
            case 430201:
                return "市辖区";
            case 430202:
                return "荷塘区";
            case 430203:
                return "芦淞区";
            case 430204:
                return "石峰区";
            case 430211:
                return "天元区";
            case 430221:
                return "株洲县";
            case 430223:
                return "攸县";
            case 430224:
                return "茶陵县";
            case 430225:
                return "炎陵县";
            case 430281:
                return "醴陵市";
            case 4303:
                return "湘潭市";
            case 430301:
                return "市辖区";
            case 430302:
                return "雨湖区";
            case 430304:
                return "岳塘区";
            case 430321:
                return "湘潭县";
            case 430381:
                return "湘乡市";
            case 430382:
                return "韶山市";
            case 4304:
                return "衡阳市";
            case 430401:
                return "市辖区";
            case 430405:
                return "珠晖区";
            case 430406:
                return "雁峰区";
            case 430407:
                return "石鼓区";
            case 430408:
                return "蒸湘区";
            case 430412:
                return "南岳区";
            case 430421:
                return "衡阳县";
            case 430422:
                return "衡南县";
            case 430423:
                return "衡山县";
            case 430424:
                return "衡东县";
            case 430426:
                return "祁东县";
            case 430481:
                return "耒阳市";
            case 430482:
                return "常宁市";
            case 4305:
                return "邵阳市";
            case 430501:
                return "市辖区";
            case 430502:
                return "双清区";
            case 430503:
                return "大祥区";
            case 430511:
                return "北塔区";
            case 430521:
                return "邵东县";
            case 430522:
                return "新邵县";
            case 430523:
                return "邵阳县";
            case 430524:
                return "隆回县";
            case 430525:
                return "洞口县";
            case 430527:
                return "绥宁县";
            case 430528:
                return "新宁县";
            case 430529:
                return "城步苗族自治县";
            case 430581:
                return "武冈市";
            case 4306:
                return "岳阳市";
            case 430601:
                return "市辖区";
            case 430602:
                return "岳阳楼区";
            case 430603:
                return "云溪区";
            case 430611:
                return "君山区";
            case 430621:
                return "岳阳县";
            case 430623:
                return "华容县";
            case 430624:
                return "湘阴县";
            case 430626:
                return "平江县";
            case 430681:
                return "汨罗市";
            case 430682:
                return "临湘市";
            case 4307:
                return "常德市";
            case 430701:
                return "市辖区";
            case 430702:
                return "武陵区";
            case 430703:
                return "鼎城区";
            case 430721:
                return "安乡县";
            case 430722:
                return "汉寿县";
            case 430723:
                return "澧县";
            case 430724:
                return "临澧县";
            case 430725:
                return "桃源县";
            case 430726:
                return "石门县";
            case 430781:
                return "津市市";
            case 4308:
                return "张家界市";
            case 430801:
                return "市辖区";
            case 430802:
                return "永定区";
            case 430811:
                return "武陵源区";
            case 430821:
                return "慈利县";
            case 430822:
                return "桑植县";
            case 4309:
                return "益阳市";
            case 430901:
                return "市辖区";
            case 430902:
                return "资阳区";
            case 430903:
                return "赫山区";
            case 430921:
                return "南县";
            case 430922:
                return "桃江县";
            case 430923:
                return "安化县";
            case 430981:
                return "沅江市";
            case 4310:
                return "郴州市";
            case 431001:
                return "市辖区";
            case 431002:
                return "北湖区";
            case 431003:
                return "苏仙区";
            case 431021:
                return "桂阳县";
            case 431022:
                return "宜章县";
            case 431023:
                return "永兴县";
            case 431024:
                return "嘉禾县";
            case 431025:
                return "临武县";
            case 431026:
                return "汝城县";
            case 431027:
                return "桂东县";
            case 431028:
                return "安仁县";
            case 431081:
                return "资兴市";
            case 4311:
                return "永州市";
            case 431101:
                return "市辖区";
            case 431102:
                return "零陵区";
            case 431103:
                return "冷水滩区";
            case 431121:
                return "祁阳县";
            case 431122:
                return "东安县";
            case 431123:
                return "双牌县";
            case 431124:
                return "道县";
            case 431125:
                return "江永县";
            case 431126:
                return "宁远县";
            case 431127:
                return "蓝山县";
            case 431128:
                return "新田县";
            case 431129:
                return "江华瑶族自治县";
            case 4312:
                return "怀化市";
            case 431201:
                return "市辖区";
            case 431202:
                return "鹤城区";
            case 431221:
                return "中方县";
            case 431222:
                return "沅陵县";
            case 431223:
                return "辰溪县";
            case 431224:
                return "溆浦县";
            case 431225:
                return "会同县";
            case 431226:
                return "麻阳苗族自治县";
            case 431227:
                return "新晃侗族自治县";
            case 431228:
                return "芷江侗族自治县";
            case 431229:
                return "靖州苗族侗族自治县";
            case 431230:
                return "通道侗族自治县";
            case 431281:
                return "洪江市";
            case 4313:
                return "娄底市";
            case 431301:
                return "市辖区";
            case 431302:
                return "娄星区";
            case 431321:
                return "双峰县";
            case 431322:
                return "新化县";
            case 431381:
                return "冷水江市";
            case 431382:
                return "涟源市";
            case 4331:
                return "湘西土家族苗族自治州";
            case 433101:
                return "吉首市";
            case 433122:
                return "泸溪县";
            case 433123:
                return "凤凰县";
            case 433124:
                return "花垣县";
            case 433125:
                return "保靖县";
            case 433126:
                return "古丈县";
            case 433127:
                return "永顺县";
            case 433130:
                return "龙山县";
            case 44:
                return "广东省";
            case 4401:
                return "广州市";
            case 440101:
                return "市辖区";
            case 440103:
                return "荔湾区";
            case 440104:
                return "越秀区";
            case 440105:
                return "海珠区";
            case 440106:
                return "天河区";
            case 440111:
                return "白云区";
            case 440112:
                return "黄埔区";
            case 440113:
                return "番禺区";
            case 440114:
                return "花都区";
            case 440115:
                return "南沙区";
            case 440116:
                return "萝岗区";
            case 440117:
                return "从化区";
            case 440118:
                return "增城区";
            case 4402:
                return "韶关市";
            case 440201:
                return "市辖区";
            case 440203:
                return "武江区";
            case 440204:
                return "浈江区";
            case 440205:
                return "曲江区";
            case 440222:
                return "始兴县";
            case 440224:
                return "仁化县";
            case 440229:
                return "翁源县";
            case 440232:
                return "乳源瑶族自治县";
            case 440233:
                return "新丰县";
            case 440281:
                return "乐昌市";
            case 440282:
                return "南雄市";
            case 4403:
                return "深圳市";
            case 440301:
                return "市辖区";
            case 440303:
                return "罗湖区";
            case 440304:
                return "福田区";
            case 440305:
                return "南山区";
            case 440306:
                return "宝安区";
            case 440307:
                return "龙岗区";
            case 440308:
                return "盐田区";
            case 4404:
                return "珠海市";
            case 440401:
                return "市辖区";
            case 440402:
                return "香洲区";
            case 440403:
                return "斗门区";
            case 440404:
                return "金湾区";
            case 4405:
                return "汕头市";
            case 440501:
                return "市辖区";
            case 440507:
                return "龙湖区";
            case 440511:
                return "金平区";
            case 440512:
                return "濠江区";
            case 440513:
                return "潮阳区";
            case 440514:
                return "潮南区";
            case 440515:
                return "澄海区";
            case 440523:
                return "南澳县";
            case 4406:
                return "佛山市";
            case 440601:
                return "市辖区";
            case 440604:
                return "禅城区";
            case 440605:
                return "南海区";
            case 440606:
                return "顺德区";
            case 440607:
                return "三水区";
            case 440608:
                return "高明区";
            case 4407:
                return "江门市";
            case 440701:
                return "市辖区";
            case 440703:
                return "蓬江区";
            case 440704:
                return "江海区";
            case 440705:
                return "新会区";
            case 440781:
                return "台山市";
            case 440783:
                return "开平市";
            case 440784:
                return "鹤山市";
            case 440785:
                return "恩平市";
            case 4408:
                return "湛江市";
            case 440801:
                return "市辖区";
            case 440802:
                return "赤坎区";
            case 440803:
                return "霞山区";
            case 440804:
                return "坡头区";
            case 440811:
                return "麻章区";
            case 440823:
                return "遂溪县";
            case 440825:
                return "徐闻县";
            case 440881:
                return "廉江市";
            case 440882:
                return "雷州市";
            case 440883:
                return "吴川市";
            case 4409:
                return "茂名市";
            case 440901:
                return "市辖区";
            case 440902:
                return "茂南区";
            case 440904:
                return "电白区";
            case 440981:
                return "高州市";
            case 440982:
                return "化州市";
            case 440983:
                return "信宜市";
            case 4412:
                return "肇庆市";
            case 441201:
                return "市辖区";
            case 441202:
                return "端州区";
            case 441203:
                return "鼎湖区";
            case 441223:
                return "广宁县";
            case 441224:
                return "怀集县";
            case 441225:
                return "封开县";
            case 441226:
                return "德庆县";
            case 441283:
                return "高要市";
            case 441284:
                return "四会市";
            case 4413:
                return "惠州市";
            case 441301:
                return "市辖区";
            case 441302:
                return "惠城区";
            case 441303:
                return "惠阳区";
            case 441322:
                return "博罗县";
            case 441323:
                return "惠东县";
            case 441324:
                return "龙门县";
            case 4414:
                return "梅州市";
            case 441401:
                return "市辖区";
            case 441402:
                return "梅江区";
            case 441403:
                return "梅县区";
            case 441422:
                return "大埔县";
            case 441423:
                return "丰顺县";
            case 441424:
                return "五华县";
            case 441426:
                return "平远县";
            case 441427:
                return "蕉岭县";
            case 441481:
                return "兴宁市";
            case 4415:
                return "汕尾市";
            case 441501:
                return "市辖区";
            case 441502:
                return "城区";
            case 441521:
                return "海丰县";
            case 441523:
                return "陆河县";
            case 441581:
                return "陆丰市";
            case 4416:
                return "河源市";
            case 441601:
                return "市辖区";
            case 441602:
                return "源城区";
            case 441621:
                return "紫金县";
            case 441622:
                return "龙川县";
            case 441623:
                return "连平县";
            case 441624:
                return "和平县";
            case 441625:
                return "东源县";
            case 4417:
                return "阳江市";
            case 441701:
                return "市辖区";
            case 441702:
                return "江城区";
            case 441721:
                return "阳西县";
            case 441723:
                return "阳东县";
            case 441781:
                return "阳春市";
            case 4418:
                return "清远市";
            case 441801:
                return "市辖区";
            case 441802:
                return "清城区";
            case 441803:
                return "清新区";
            case 441821:
                return "佛冈县";
            case 441823:
                return "阳山县";
            case 441825:
                return "连山壮族瑶族自治县";
            case 441826:
                return "连南瑶族自治县";
            case 441881:
                return "英德市";
            case 441882:
                return "连州市";
            case 4419:
                return "东莞市";
            case 4420:
                return "中山市";
            case 4451:
                return "潮州市";
            case 445101:
                return "市辖区";
            case 445102:
                return "湘桥区";
            case 445103:
                return "潮安区";
            case 445122:
                return "饶平县";
            case 4452:
                return "揭阳市";
            case 445201:
                return "市辖区";
            case 445202:
                return "榕城区";
            case 445203:
                return "揭东区";
            case 445222:
                return "揭西县";
            case 445224:
                return "惠来县";
            case 445281:
                return "普宁市";
            case 4453:
                return "云浮市";
            case 445301:
                return "市辖区";
            case 445302:
                return "云城区";
            case 445303:
                return "云安区";
            case 445321:
                return "新兴县";
            case 445322:
                return "郁南县";
            case 445381:
                return "罗定市";
            case 45:
                return "广西壮族自治区";
            case 4501:
                return "南宁市";
            case 450101:
                return "市辖区";
            case 450102:
                return "兴宁区";
            case 450103:
                return "青秀区";
            case 450105:
                return "江南区";
            case 450107:
                return "西乡塘区";
            case 450108:
                return "良庆区";
            case 450109:
                return "邕宁区";
            case 450122:
                return "武鸣县";
            case 450123:
                return "隆安县";
            case 450124:
                return "马山县";
            case 450125:
                return "上林县";
            case 450126:
                return "宾阳县";
            case 450127:
                return "横县";
            case 4502:
                return "柳州市";
            case 450201:
                return "市辖区";
            case 450202:
                return "城中区";
            case 450203:
                return "鱼峰区";
            case 450204:
                return "柳南区";
            case 450205:
                return "柳北区";
            case 450221:
                return "柳江县";
            case 450222:
                return "柳城县";
            case 450223:
                return "鹿寨县";
            case 450224:
                return "融安县";
            case 450225:
                return "融水苗族自治县";
            case 450226:
                return "三江侗族自治县";
            case 4503:
                return "桂林市";
            case 450301:
                return "市辖区";
            case 450302:
                return "秀峰区";
            case 450303:
                return "叠彩区";
            case 450304:
                return "象山区";
            case 450305:
                return "七星区";
            case 450311:
                return "雁山区";
            case 450312:
                return "临桂区";
            case 450321:
                return "阳朔县";
            case 450323:
                return "灵川县";
            case 450324:
                return "全州县";
            case 450325:
                return "兴安县";
            case 450326:
                return "永福县";
            case 450327:
                return "灌阳县";
            case 450328:
                return "龙胜各族自治县";
            case 450329:
                return "资源县";
            case 450330:
                return "平乐县";
            case 450331:
                return "荔浦县";
            case 450332:
                return "恭城瑶族自治县";
            case 4504:
                return "梧州市";
            case 450401:
                return "市辖区";
            case 450403:
                return "万秀区";
            case 450405:
                return "长洲区";
            case 450406:
                return "龙圩区";
            case 450421:
                return "苍梧县";
            case 450422:
                return "藤县";
            case 450423:
                return "蒙山县";
            case 450481:
                return "岑溪市";
            case 4505:
                return "北海市";
            case 450501:
                return "市辖区";
            case 450502:
                return "海城区";
            case 450503:
                return "银海区";
            case 450512:
                return "铁山港区";
            case 450521:
                return "合浦县";
            case 4506:
                return "防城港市";
            case 450601:
                return "市辖区";
            case 450602:
                return "港口区";
            case 450603:
                return "防城区";
            case 450621:
                return "上思县";
            case 450681:
                return "东兴市";
            case 4507:
                return "钦州市";
            case 450701:
                return "市辖区";
            case 450702:
                return "钦南区";
            case 450703:
                return "钦北区";
            case 450721:
                return "灵山县";
            case 450722:
                return "浦北县";
            case 4508:
                return "贵港市";
            case 450801:
                return "市辖区";
            case 450802:
                return "港北区";
            case 450803:
                return "港南区";
            case 450804:
                return "覃塘区";
            case 450821:
                return "平南县";
            case 450881:
                return "桂平市";
            case 4509:
                return "玉林市";
            case 450901:
                return "市辖区";
            case 450902:
                return "玉州区";
            case 450903:
                return "福绵区";
            case 450921:
                return "容县";
            case 450922:
                return "陆川县";
            case 450923:
                return "博白县";
            case 450924:
                return "兴业县";
            case 450981:
                return "北流市";
            case 4510:
                return "百色市";
            case 451001:
                return "市辖区";
            case 451002:
                return "右江区";
            case 451021:
                return "田阳县";
            case 451022:
                return "田东县";
            case 451023:
                return "平果县";
            case 451024:
                return "德保县";
            case 451025:
                return "靖西县";
            case 451026:
                return "那坡县";
            case 451027:
                return "凌云县";
            case 451028:
                return "乐业县";
            case 451029:
                return "田林县";
            case 451030:
                return "西林县";
            case 451031:
                return "隆林各族自治县";
            case 4511:
                return "贺州市";
            case 451101:
                return "市辖区";
            case 451102:
                return "八步区";
            case 451121:
                return "昭平县";
            case 451122:
                return "钟山县";
            case 451123:
                return "富川瑶族自治县";
            case 4512:
                return "河池市";
            case 451201:
                return "市辖区";
            case 451202:
                return "金城江区";
            case 451221:
                return "南丹县";
            case 451222:
                return "天峨县";
            case 451223:
                return "凤山县";
            case 451224:
                return "东兰县";
            case 451225:
                return "罗城仫佬族自治县";
            case 451226:
                return "环江毛南族自治县";
            case 451227:
                return "巴马瑶族自治县";
            case 451228:
                return "都安瑶族自治县";
            case 451229:
                return "大化瑶族自治县";
            case 451281:
                return "宜州市";
            case 4513:
                return "来宾市";
            case 451301:
                return "市辖区";
            case 451302:
                return "兴宾区";
            case 451321:
                return "忻城县";
            case 451322:
                return "象州县";
            case 451323:
                return "武宣县";
            case 451324:
                return "金秀瑶族自治县";
            case 451381:
                return "合山市";
            case 4514:
                return "崇左市";
            case 451401:
                return "市辖区";
            case 451402:
                return "江州区";
            case 451421:
                return "扶绥县";
            case 451422:
                return "宁明县";
            case 451423:
                return "龙州县";
            case 451424:
                return "大新县";
            case 451425:
                return "天等县";
            case 451481:
                return "凭祥市";
            case 46:
                return "海南省";
            case 4601:
                return "海口市";
            case 460101:
                return "市辖区";
            case 460105:
                return "秀英区";
            case 460106:
                return "龙华区";
            case 460107:
                return "琼山区";
            case 460108:
                return "美兰区";
            case 4602:
                return "三亚市";
            case 460201:
                return "市辖区";
            case 460202:
                return "海棠区";
            case 460203:
                return "吉阳区";
            case 460204:
                return "天涯区";
            case 460205:
                return "崖州区";
            case 4603:
                return "三沙市";
            case 4690:
                return "省直辖县级行政区划";
            case 469001:
                return "五指山市";
            case 469002:
                return "琼海市";
            case 469003:
                return "儋州市";
            case 469005:
                return "文昌市";
            case 469006:
                return "万宁市";
            case 469007:
                return "东方市";
            case 469021:
                return "定安县";
            case 469022:
                return "屯昌县";
            case 469023:
                return "澄迈县";
            case 469024:
                return "临高县";
            case 469025:
                return "白沙黎族自治县";
            case 469026:
                return "昌江黎族自治县";
            case 469027:
                return "乐东黎族自治县";
            case 469028:
                return "陵水黎族自治县";
            case 469029:
                return "保亭黎族苗族自治县";
            case 469030:
                return "琼中黎族苗族自治县";
            case 50:
                return "重庆市";
            case 5001:
                return "市辖区";
            case 500101:
                return "万州区";
            case 500102:
                return "涪陵区";
            case 500103:
                return "渝中区";
            case 500104:
                return "大渡口区";
            case 500105:
                return "江北区";
            case 500106:
                return "沙坪坝区";
            case 500107:
                return "九龙坡区";
            case 500108:
                return "南岸区";
            case 500109:
                return "北碚区";
            case 500110:
                return "綦江区";
            case 500111:
                return "大足区";
            case 500112:
                return "渝北区";
            case 500113:
                return "巴南区";
            case 500114:
                return "黔江区";
            case 500115:
                return "长寿区";
            case 500116:
                return "江津区";
            case 500117:
                return "合川区";
            case 500118:
                return "永川区";
            case 500119:
                return "南川区";
            case 500120:
                return "璧山区";
            case 500151:
                return "铜梁区";
            case 5002:
                return "县";
            case 500223:
                return "潼南县";
            case 500226:
                return "荣昌县";
            case 500228:
                return "梁平县";
            case 500229:
                return "城口县";
            case 500230:
                return "丰都县";
            case 500231:
                return "垫江县";
            case 500232:
                return "武隆县";
            case 500233:
                return "忠县";
            case 500234:
                return "开县";
            case 500235:
                return "云阳县";
            case 500236:
                return "奉节县";
            case 500237:
                return "巫山县";
            case 500238:
                return "巫溪县";
            case 500240:
                return "石柱土家族自治县";
            case 500241:
                return "秀山土家族苗族自治县";
            case 500242:
                return "酉阳土家族苗族自治县";
            case 500243:
                return "彭水苗族土家族自治县";
            case 51:
                return "四川省";
            case 5101:
                return "成都市";
            case 510101:
                return "市辖区";
            case 510104:
                return "锦江区";
            case 510105:
                return "青羊区";
            case 510106:
                return "金牛区";
            case 510107:
                return "武侯区";
            case 510108:
                return "成华区";
            case 510112:
                return "龙泉驿区";
            case 510113:
                return "青白江区";
            case 510114:
                return "新都区";
            case 510115:
                return "温江区";
            case 510121:
                return "金堂县";
            case 510122:
                return "双流县";
            case 510124:
                return "郫县";
            case 510129:
                return "大邑县";
            case 510131:
                return "蒲江县";
            case 510132:
                return "新津县";
            case 510181:
                return "都江堰市";
            case 510182:
                return "彭州市";
            case 510183:
                return "邛崃市";
            case 510184:
                return "崇州市";
            case 5103:
                return "自贡市";
            case 510301:
                return "市辖区";
            case 510302:
                return "自流井区";
            case 510303:
                return "贡井区";
            case 510304:
                return "大安区";
            case 510311:
                return "沿滩区";
            case 510321:
                return "荣县";
            case 510322:
                return "富顺县";
            case 5104:
                return "攀枝花市";
            case 510401:
                return "市辖区";
            case 510402:
                return "东区";
            case 510403:
                return "西区";
            case 510411:
                return "仁和区";
            case 510421:
                return "米易县";
            case 510422:
                return "盐边县";
            case 5105:
                return "泸州市";
            case 510501:
                return "市辖区";
            case 510502:
                return "江阳区";
            case 510503:
                return "纳溪区";
            case 510504:
                return "龙马潭区";
            case 510521:
                return "泸县";
            case 510522:
                return "合江县";
            case 510524:
                return "叙永县";
            case 510525:
                return "古蔺县";
            case 5106:
                return "德阳市";
            case 510601:
                return "市辖区";
            case 510603:
                return "旌阳区";
            case 510623:
                return "中江县";
            case 510626:
                return "罗江县";
            case 510681:
                return "广汉市";
            case 510682:
                return "什邡市";
            case 510683:
                return "绵竹市";
            case 5107:
                return "绵阳市";
            case 510701:
                return "市辖区";
            case 510703:
                return "涪城区";
            case 510704:
                return "游仙区";
            case 510722:
                return "三台县";
            case 510723:
                return "盐亭县";
            case 510724:
                return "安县";
            case 510725:
                return "梓潼县";
            case 510726:
                return "北川羌族自治县";
            case 510727:
                return "平武县";
            case 510781:
                return "江油市";
            case 5108:
                return "广元市";
            case 510801:
                return "市辖区";
            case 510802:
                return "利州区";
            case 510811:
                return "昭化区";
            case 510812:
                return "朝天区";
            case 510821:
                return "旺苍县";
            case 510822:
                return "青川县";
            case 510823:
                return "剑阁县";
            case 510824:
                return "苍溪县";
            case 5109:
                return "遂宁市";
            case 510901:
                return "市辖区";
            case 510903:
                return "船山区";
            case 510904:
                return "安居区";
            case 510921:
                return "蓬溪县";
            case 510922:
                return "射洪县";
            case 510923:
                return "大英县";
            case 5110:
                return "内江市";
            case 511001:
                return "市辖区";
            case 511002:
                return "市中区";
            case 511011:
                return "东兴区";
            case 511024:
                return "威远县";
            case 511025:
                return "资中县";
            case 511028:
                return "隆昌县";
            case 5111:
                return "乐山市";
            case 511101:
                return "市辖区";
            case 511102:
                return "市中区";
            case 511111:
                return "沙湾区";
            case 511112:
                return "五通桥区";
            case 511113:
                return "金口河区";
            case 511123:
                return "犍为县";
            case 511124:
                return "井研县";
            case 511126:
                return "夹江县";
            case 511129:
                return "沐川县";
            case 511132:
                return "峨边彝族自治县";
            case 511133:
                return "马边彝族自治县";
            case 511181:
                return "峨眉山市";
            case 5113:
                return "南充市";
            case 511301:
                return "市辖区";
            case 511302:
                return "顺庆区";
            case 511303:
                return "高坪区";
            case 511304:
                return "嘉陵区";
            case 511321:
                return "南部县";
            case 511322:
                return "营山县";
            case 511323:
                return "蓬安县";
            case 511324:
                return "仪陇县";
            case 511325:
                return "西充县";
            case 511381:
                return "阆中市";
            case 5114:
                return "眉山市";
            case 511401:
                return "市辖区";
            case 511402:
                return "东坡区";
            case 511421:
                return "仁寿县";
            case 511422:
                return "彭山县";
            case 511423:
                return "洪雅县";
            case 511424:
                return "丹棱县";
            case 511425:
                return "青神县";
            case 5115:
                return "宜宾市";
            case 511501:
                return "市辖区";
            case 511502:
                return "翠屏区";
            case 511503:
                return "南溪区";
            case 511521:
                return "宜宾县";
            case 511523:
                return "江安县";
            case 511524:
                return "长宁县";
            case 511525:
                return "高县";
            case 511526:
                return "珙县";
            case 511527:
                return "筠连县";
            case 511528:
                return "兴文县";
            case 511529:
                return "屏山县";
            case 5116:
                return "广安市";
            case 511601:
                return "市辖区";
            case 511602:
                return "广安区";
            case 511603:
                return "前锋区";
            case 511621:
                return "岳池县";
            case 511622:
                return "武胜县";
            case 511623:
                return "邻水县";
            case 511681:
                return "华蓥市";
            case 5117:
                return "达州市";
            case 511701:
                return "市辖区";
            case 511702:
                return "通川区";
            case 511703:
                return "达川区";
            case 511722:
                return "宣汉县";
            case 511723:
                return "开江县";
            case 511724:
                return "大竹县";
            case 511725:
                return "渠县";
            case 511781:
                return "万源市";
            case 5118:
                return "雅安市";
            case 511801:
                return "市辖区";
            case 511802:
                return "雨城区";
            case 511803:
                return "名山区";
            case 511822:
                return "荥经县";
            case 511823:
                return "汉源县";
            case 511824:
                return "石棉县";
            case 511825:
                return "天全县";
            case 511826:
                return "芦山县";
            case 511827:
                return "宝兴县";
            case 5119:
                return "巴中市";
            case 511901:
                return "市辖区";
            case 511902:
                return "巴州区";
            case 511903:
                return "恩阳区";
            case 511921:
                return "通江县";
            case 511922:
                return "南江县";
            case 511923:
                return "平昌县";
            case 5120:
                return "资阳市";
            case 512001:
                return "市辖区";
            case 512002:
                return "雁江区";
            case 512021:
                return "安岳县";
            case 512022:
                return "乐至县";
            case 512081:
                return "简阳市";
            case 5132:
                return "阿坝藏族羌族自治州";
            case 513221:
                return "汶川县";
            case 513222:
                return "理县";
            case 513223:
                return "茂县";
            case 513224:
                return "松潘县";
            case 513225:
                return "九寨沟县";
            case 513226:
                return "金川县";
            case 513227:
                return "小金县";
            case 513228:
                return "黑水县";
            case 513229:
                return "马尔康县";
            case 513230:
                return "壤塘县";
            case 513231:
                return "阿坝县";
            case 513232:
                return "若尔盖县";
            case 513233:
                return "红原县";
            case 5133:
                return "甘孜藏族自治州";
            case 513321:
                return "康定县";
            case 513322:
                return "泸定县";
            case 513323:
                return "丹巴县";
            case 513324:
                return "九龙县";
            case 513325:
                return "雅江县";
            case 513326:
                return "道孚县";
            case 513327:
                return "炉霍县";
            case 513328:
                return "甘孜县";
            case 513329:
                return "新龙县";
            case 513330:
                return "德格县";
            case 513331:
                return "白玉县";
            case 513332:
                return "石渠县";
            case 513333:
                return "色达县";
            case 513334:
                return "理塘县";
            case 513335:
                return "巴塘县";
            case 513336:
                return "乡城县";
            case 513337:
                return "稻城县";
            case 513338:
                return "得荣县";
            case 5134:
                return "凉山彝族自治州";
            case 513401:
                return "西昌市";
            case 513422:
                return "木里藏族自治县";
            case 513423:
                return "盐源县";
            case 513424:
                return "德昌县";
            case 513425:
                return "会理县";
            case 513426:
                return "会东县";
            case 513427:
                return "宁南县";
            case 513428:
                return "普格县";
            case 513429:
                return "布拖县";
            case 513430:
                return "金阳县";
            case 513431:
                return "昭觉县";
            case 513432:
                return "喜德县";
            case 513433:
                return "冕宁县";
            case 513434:
                return "越西县";
            case 513435:
                return "甘洛县";
            case 513436:
                return "美姑县";
            case 513437:
                return "雷波县";
            case 52:
                return "贵州省";
            case 5201:
                return "贵阳市";
            case 520101:
                return "市辖区";
            case 520102:
                return "南明区";
            case 520103:
                return "云岩区";
            case 520111:
                return "花溪区";
            case 520112:
                return "乌当区";
            case 520113:
                return "白云区";
            case 520115:
                return "观山湖区";
            case 520121:
                return "开阳县";
            case 520122:
                return "息烽县";
            case 520123:
                return "修文县";
            case 520181:
                return "清镇市";
            case 5202:
                return "六盘水市";
            case 520201:
                return "钟山区";
            case 520203:
                return "六枝特区";
            case 520221:
                return "水城县";
            case 520222:
                return "盘县";
            case 5203:
                return "遵义市";
            case 520301:
                return "市辖区";
            case 520302:
                return "红花岗区";
            case 520303:
                return "汇川区";
            case 520321:
                return "遵义县";
            case 520322:
                return "桐梓县";
            case 520323:
                return "绥阳县";
            case 520324:
                return "正安县";
            case 520325:
                return "道真仡佬族苗族自治县";
            case 520326:
                return "务川仡佬族苗族自治县";
            case 520327:
                return "凤冈县";
            case 520328:
                return "湄潭县";
            case 520329:
                return "余庆县";
            case 520330:
                return "习水县";
            case 520381:
                return "赤水市";
            case 520382:
                return "仁怀市";
            case 5204:
                return "安顺市";
            case 520401:
                return "市辖区";
            case 520402:
                return "西秀区";
            case 520421:
                return "平坝县";
            case 520422:
                return "普定县";
            case 520423:
                return "镇宁布依族苗族自治县";
            case 520424:
                return "关岭布依族苗族自治县";
            case 520425:
                return "紫云苗族布依族自治县";
            case 5205:
                return "毕节市";
            case 520501:
                return "市辖区";
            case 520502:
                return "七星关区";
            case 520521:
                return "大方县";
            case 520522:
                return "黔西县";
            case 520523:
                return "金沙县";
            case 520524:
                return "织金县";
            case 520525:
                return "纳雍县";
            case 520526:
                return "威宁彝族回族苗族自治县";
            case 520527:
                return "赫章县";
            case 5206:
                return "铜仁市";
            case 520601:
                return "市辖区";
            case 520602:
                return "碧江区";
            case 520603:
                return "万山区";
            case 520621:
                return "江口县";
            case 520622:
                return "玉屏侗族自治县";
            case 520623:
                return "石阡县";
            case 520624:
                return "思南县";
            case 520625:
                return "印江土家族苗族自治县";
            case 520626:
                return "德江县";
            case 520627:
                return "沿河土家族自治县";
            case 520628:
                return "松桃苗族自治县";
            case 5223:
                return "黔西南布依族苗族自治州";
            case 522301:
                return "兴义市";
            case 522322:
                return "兴仁县";
            case 522323:
                return "普安县";
            case 522324:
                return "晴隆县";
            case 522325:
                return "贞丰县";
            case 522326:
                return "望谟县";
            case 522327:
                return "册亨县";
            case 522328:
                return "安龙县";
            case 5226:
                return "黔东南苗族侗族自治州";
            case 522601:
                return "凯里市";
            case 522622:
                return "黄平县";
            case 522623:
                return "施秉县";
            case 522624:
                return "三穗县";
            case 522625:
                return "镇远县";
            case 522626:
                return "岑巩县";
            case 522627:
                return "天柱县";
            case 522628:
                return "锦屏县";
            case 522629:
                return "剑河县";
            case 522630:
                return "台江县";
            case 522631:
                return "黎平县";
            case 522632:
                return "榕江县";
            case 522633:
                return "从江县";
            case 522634:
                return "雷山县";
            case 522635:
                return "麻江县";
            case 522636:
                return "丹寨县";
            case 5227:
                return "黔南布依族苗族自治州";
            case 522701:
                return "都匀市";
            case 522702:
                return "福泉市";
            case 522722:
                return "荔波县";
            case 522723:
                return "贵定县";
            case 522725:
                return "瓮安县";
            case 522726:
                return "独山县";
            case 522727:
                return "平塘县";
            case 522728:
                return "罗甸县";
            case 522729:
                return "长顺县";
            case 522730:
                return "龙里县";
            case 522731:
                return "惠水县";
            case 522732:
                return "三都水族自治县";
            case 53:
                return "云南省";
            case 5301:
                return "昆明市";
            case 530101:
                return "市辖区";
            case 530102:
                return "五华区";
            case 530103:
                return "盘龙区";
            case 530111:
                return "官渡区";
            case 530112:
                return "西山区";
            case 530113:
                return "东川区";
            case 530114:
                return "呈贡区";
            case 530122:
                return "晋宁县";
            case 530124:
                return "富民县";
            case 530125:
                return "宜良县";
            case 530126:
                return "石林彝族自治县";
            case 530127:
                return "嵩明县";
            case 530128:
                return "禄劝彝族苗族自治县";
            case 530129:
                return "寻甸回族彝族自治县";
            case 530181:
                return "安宁市";
            case 5303:
                return "曲靖市";
            case 530301:
                return "市辖区";
            case 530302:
                return "麒麟区";
            case 530321:
                return "马龙县";
            case 530322:
                return "陆良县";
            case 530323:
                return "师宗县";
            case 530324:
                return "罗平县";
            case 530325:
                return "富源县";
            case 530326:
                return "会泽县";
            case 530328:
                return "沾益县";
            case 530381:
                return "宣威市";
            case 5304:
                return "玉溪市";
            case 530401:
                return "市辖区";
            case 530402:
                return "红塔区";
            case 530421:
                return "江川县";
            case 530422:
                return "澄江县";
            case 530423:
                return "通海县";
            case 530424:
                return "华宁县";
            case 530425:
                return "易门县";
            case 530426:
                return "峨山彝族自治县";
            case 530427:
                return "新平彝族傣族自治县";
            case 530428:
                return "元江哈尼族彝族傣族自治县";
            case 5305:
                return "保山市";
            case 530501:
                return "市辖区";
            case 530502:
                return "隆阳区";
            case 530521:
                return "施甸县";
            case 530522:
                return "腾冲县";
            case 530523:
                return "龙陵县";
            case 530524:
                return "昌宁县";
            case 5306:
                return "昭通市";
            case 530601:
                return "市辖区";
            case 530602:
                return "昭阳区";
            case 530621:
                return "鲁甸县";
            case 530622:
                return "巧家县";
            case 530623:
                return "盐津县";
            case 530624:
                return "大关县";
            case 530625:
                return "永善县";
            case 530626:
                return "绥江县";
            case 530627:
                return "镇雄县";
            case 530628:
                return "彝良县";
            case 530629:
                return "威信县";
            case 530630:
                return "水富县";
            case 5307:
                return "丽江市";
            case 530701:
                return "市辖区";
            case 530702:
                return "古城区";
            case 530721:
                return "玉龙纳西族自治县";
            case 530722:
                return "永胜县";
            case 530723:
                return "华坪县";
            case 530724:
                return "宁蒗彝族自治县";
            case 5308:
                return "普洱市";
            case 530801:
                return "市辖区";
            case 530802:
                return "思茅区";
            case 530821:
                return "宁洱哈尼族彝族自治县";
            case 530822:
                return "墨江哈尼族自治县";
            case 530823:
                return "景东彝族自治县";
            case 530824:
                return "景谷傣族彝族自治县";
            case 530825:
                return "镇沅彝族哈尼族拉祜族自治县";
            case 530826:
                return "江城哈尼族彝族自治县";
            case 530827:
                return "孟连傣族拉祜族佤族自治县";
            case 530828:
                return "澜沧拉祜族自治县";
            case 530829:
                return "西盟佤族自治县";
            case 5309:
                return "临沧市";
            case 530901:
                return "市辖区";
            case 530902:
                return "临翔区";
            case 530921:
                return "凤庆县";
            case 530922:
                return "云县";
            case 530923:
                return "永德县";
            case 530924:
                return "镇康县";
            case 530925:
                return "双江拉祜族佤族布朗族傣族自治县";
            case 530926:
                return "耿马傣族佤族自治县";
            case 530927:
                return "沧源佤族自治县";
            case 5323:
                return "楚雄彝族自治州";
            case 532301:
                return "楚雄市";
            case 532322:
                return "双柏县";
            case 532323:
                return "牟定县";
            case 532324:
                return "南华县";
            case 532325:
                return "姚安县";
            case 532326:
                return "大姚县";
            case 532327:
                return "永仁县";
            case 532328:
                return "元谋县";
            case 532329:
                return "武定县";
            case 532331:
                return "禄丰县";
            case 5325:
                return "红河哈尼族彝族自治州";
            case 532501:
                return "个旧市";
            case 532502:
                return "开远市";
            case 532503:
                return "蒙自市";
            case 532504:
                return "弥勒市";
            case 532523:
                return "屏边苗族自治县";
            case 532524:
                return "建水县";
            case 532525:
                return "石屏县";
            case 532527:
                return "泸西县";
            case 532528:
                return "元阳县";
            case 532529:
                return "红河县";
            case 532530:
                return "金平苗族瑶族傣族自治县";
            case 532531:
                return "绿春县";
            case 532532:
                return "河口瑶族自治县";
            case 5326:
                return "文山壮族苗族自治州";
            case 532601:
                return "文山市";
            case 532622:
                return "砚山县";
            case 532623:
                return "西畴县";
            case 532624:
                return "麻栗坡县";
            case 532625:
                return "马关县";
            case 532626:
                return "丘北县";
            case 532627:
                return "广南县";
            case 532628:
                return "富宁县";
            case 5328:
                return "西双版纳傣族自治州";
            case 532801:
                return "景洪市";
            case 532822:
                return "勐海县";
            case 532823:
                return "勐腊县";
            case 5329:
                return "大理白族自治州";
            case 532901:
                return "大理市";
            case 532922:
                return "漾濞彝族自治县";
            case 532923:
                return "祥云县";
            case 532924:
                return "宾川县";
            case 532925:
                return "弥渡县";
            case 532926:
                return "南涧彝族自治县";
            case 532927:
                return "巍山彝族回族自治县";
            case 532928:
                return "永平县";
            case 532929:
                return "云龙县";
            case 532930:
                return "洱源县";
            case 532931:
                return "剑川县";
            case 532932:
                return "鹤庆县";
            case 5331:
                return "德宏傣族景颇族自治州";
            case 533102:
                return "瑞丽市";
            case 533103:
                return "芒市";
            case 533122:
                return "梁河县";
            case 533123:
                return "盈江县";
            case 533124:
                return "陇川县";
            case 5333:
                return "怒江傈僳族自治州";
            case 533321:
                return "泸水县";
            case 533323:
                return "福贡县";
            case 533324:
                return "贡山独龙族怒族自治县";
            case 533325:
                return "兰坪白族普米族自治县";
            case 5334:
                return "迪庆藏族自治州";
            case 533421:
                return "香格里拉县";
            case 533422:
                return "德钦县";
            case 533423:
                return "维西傈僳族自治县";
            case 54:
                return "西藏自治区";
            case 5401:
                return "拉萨市";
            case 540101:
                return "市辖区";
            case 540102:
                return "城关区";
            case 540121:
                return "林周县";
            case 540122:
                return "当雄县";
            case 540123:
                return "尼木县";
            case 540124:
                return "曲水县";
            case 540125:
                return "堆龙德庆县";
            case 540126:
                return "达孜县";
            case 540127:
                return "墨竹工卡县";
            case 5402:
                return "日喀则市";
            case 540202:
                return "桑珠孜区";
            case 540221:
                return "南木林县";
            case 540222:
                return "江孜县";
            case 540223:
                return "定日县";
            case 540224:
                return "萨迦县";
            case 540225:
                return "拉孜县";
            case 540226:
                return "昂仁县";
            case 540227:
                return "谢通门县";
            case 540228:
                return "白朗县";
            case 540229:
                return "仁布县";
            case 540230:
                return "康马县";
            case 540231:
                return "定结县";
            case 540232:
                return "仲巴县";
            case 540233:
                return "亚东县";
            case 540234:
                return "吉隆县";
            case 540235:
                return "聂拉木县";
            case 540236:
                return "萨嘎县";
            case 540237:
                return "岗巴县";
            case 5421:
                return "昌都地区";
            case 542121:
                return "昌都县";
            case 542122:
                return "江达县";
            case 542123:
                return "贡觉县";
            case 542124:
                return "类乌齐县";
            case 542125:
                return "丁青县";
            case 542126:
                return "察雅县";
            case 542127:
                return "八宿县";
            case 542128:
                return "左贡县";
            case 542129:
                return "芒康县";
            case 542132:
                return "洛隆县";
            case 542133:
                return "边坝县";
            case 5422:
                return "山南地区";
            case 542221:
                return "乃东县";
            case 542222:
                return "扎囊县";
            case 542223:
                return "贡嘎县";
            case 542224:
                return "桑日县";
            case 542225:
                return "琼结县";
            case 542226:
                return "曲松县";
            case 542227:
                return "措美县";
            case 542228:
                return "洛扎县";
            case 542229:
                return "加查县";
            case 542231:
                return "隆子县";
            case 542232:
                return "错那县";
            case 542233:
                return "浪卡子县";
            case 5424:
                return "那曲地区";
            case 542421:
                return "那曲县";
            case 542422:
                return "嘉黎县";
            case 542423:
                return "比如县";
            case 542424:
                return "聂荣县";
            case 542425:
                return "安多县";
            case 542426:
                return "申扎县";
            case 542427:
                return "索县";
            case 542428:
                return "班戈县";
            case 542429:
                return "巴青县";
            case 542430:
                return "尼玛县";
            case 542431:
                return "双湖县";
            case 5425:
                return "阿里地区";
            case 542521:
                return "普兰县";
            case 542522:
                return "札达县";
            case 542523:
                return "噶尔县";
            case 542524:
                return "日土县";
            case 542525:
                return "革吉县";
            case 542526:
                return "改则县";
            case 542527:
                return "措勤县";
            case 5426:
                return "林芝地区";
            case 542621:
                return "林芝县";
            case 542622:
                return "工布江达县";
            case 542623:
                return "米林县";
            case 542624:
                return "墨脱县";
            case 542625:
                return "波密县";
            case 542626:
                return "察隅县";
            case 542627:
                return "朗县";
            case 61:
                return "陕西省";
            case 6101:
                return "西安市";
            case 610101:
                return "市辖区";
            case 610102:
                return "新城区";
            case 610103:
                return "碑林区";
            case 610104:
                return "莲湖区";
            case 610111:
                return "灞桥区";
            case 610112:
                return "未央区";
            case 610113:
                return "雁塔区";
            case 610114:
                return "阎良区";
            case 610115:
                return "临潼区";
            case 610116:
                return "长安区";
            case 610122:
                return "蓝田县";
            case 610124:
                return "周至县";
            case 610125:
                return "户县";
            case 610126:
                return "高陵县";
            case 6102:
                return "铜川市";
            case 610201:
                return "市辖区";
            case 610202:
                return "王益区";
            case 610203:
                return "印台区";
            case 610204:
                return "耀州区";
            case 610222:
                return "宜君县";
            case 6103:
                return "宝鸡市";
            case 610301:
                return "市辖区";
            case 610302:
                return "渭滨区";
            case 610303:
                return "金台区";
            case 610304:
                return "陈仓区";
            case 610322:
                return "凤翔县";
            case 610323:
                return "岐山县";
            case 610324:
                return "扶风县";
            case 610326:
                return "眉县";
            case 610327:
                return "陇县";
            case 610328:
                return "千阳县";
            case 610329:
                return "麟游县";
            case 610330:
                return "凤县";
            case 610331:
                return "太白县";
            case 6104:
                return "咸阳市";
            case 610401:
                return "市辖区";
            case 610402:
                return "秦都区";
            case 610403:
                return "杨陵区";
            case 610404:
                return "渭城区";
            case 610422:
                return "三原县";
            case 610423:
                return "泾阳县";
            case 610424:
                return "乾县";
            case 610425:
                return "礼泉县";
            case 610426:
                return "永寿县";
            case 610427:
                return "彬县";
            case 610428:
                return "长武县";
            case 610429:
                return "旬邑县";
            case 610430:
                return "淳化县";
            case 610431:
                return "武功县";
            case 610481:
                return "兴平市";
            case 6105:
                return "渭南市";
            case 610501:
                return "市辖区";
            case 610502:
                return "临渭区";
            case 610521:
                return "华县";
            case 610522:
                return "潼关县";
            case 610523:
                return "大荔县";
            case 610524:
                return "合阳县";
            case 610525:
                return "澄城县";
            case 610526:
                return "蒲城县";
            case 610527:
                return "白水县";
            case 610528:
                return "富平县";
            case 610581:
                return "韩城市";
            case 610582:
                return "华阴市";
            case 6106:
                return "延安市";
            case 610601:
                return "市辖区";
            case 610602:
                return "宝塔区";
            case 610621:
                return "延长县";
            case 610622:
                return "延川县";
            case 610623:
                return "子长县";
            case 610624:
                return "安塞县";
            case 610625:
                return "志丹县";
            case 610626:
                return "吴起县";
            case 610627:
                return "甘泉县";
            case 610628:
                return "富县";
            case 610629:
                return "洛川县";
            case 610630:
                return "宜川县";
            case 610631:
                return "黄龙县";
            case 610632:
                return "黄陵县";
            case 6107:
                return "汉中市";
            case 610701:
                return "市辖区";
            case 610702:
                return "汉台区";
            case 610721:
                return "南郑县";
            case 610722:
                return "城固县";
            case 610723:
                return "洋县";
            case 610724:
                return "西乡县";
            case 610725:
                return "勉县";
            case 610726:
                return "宁强县";
            case 610727:
                return "略阳县";
            case 610728:
                return "镇巴县";
            case 610729:
                return "留坝县";
            case 610730:
                return "佛坪县";
            case 6108:
                return "榆林市";
            case 610801:
                return "市辖区";
            case 610802:
                return "榆阳区";
            case 610821:
                return "神木县";
            case 610822:
                return "府谷县";
            case 610823:
                return "横山县";
            case 610824:
                return "靖边县";
            case 610825:
                return "定边县";
            case 610826:
                return "绥德县";
            case 610827:
                return "米脂县";
            case 610828:
                return "佳县";
            case 610829:
                return "吴堡县";
            case 610830:
                return "清涧县";
            case 610831:
                return "子洲县";
            case 6109:
                return "安康市";
            case 610901:
                return "市辖区";
            case 610902:
                return "汉滨区";
            case 610921:
                return "汉阴县";
            case 610922:
                return "石泉县";
            case 610923:
                return "宁陕县";
            case 610924:
                return "紫阳县";
            case 610925:
                return "岚皋县";
            case 610926:
                return "平利县";
            case 610927:
                return "镇坪县";
            case 610928:
                return "旬阳县";
            case 610929:
                return "白河县";
            case 6110:
                return "商洛市";
            case 611001:
                return "市辖区";
            case 611002:
                return "商州区";
            case 611021:
                return "洛南县";
            case 611022:
                return "丹凤县";
            case 611023:
                return "商南县";
            case 611024:
                return "山阳县";
            case 611025:
                return "镇安县";
            case 611026:
                return "柞水县";
            case 62:
                return "甘肃省";
            case 6201:
                return "兰州市";
            case 620101:
                return "市辖区";
            case 620102:
                return "城关区";
            case 620103:
                return "七里河区";
            case 620104:
                return "西固区";
            case 620105:
                return "安宁区";
            case 620111:
                return "红古区";
            case 620121:
                return "永登县";
            case 620122:
                return "皋兰县";
            case 620123:
                return "榆中县";
            case 6202:
                return "嘉峪关市";
            case 620201:
                return "市辖区";
            case 6203:
                return "金昌市";
            case 620301:
                return "市辖区";
            case 620302:
                return "金川区";
            case 620321:
                return "永昌县";
            case 6204:
                return "白银市";
            case 620401:
                return "市辖区";
            case 620402:
                return "白银区";
            case 620403:
                return "平川区";
            case 620421:
                return "靖远县";
            case 620422:
                return "会宁县";
            case 620423:
                return "景泰县";
            case 6205:
                return "天水市";
            case 620501:
                return "市辖区";
            case 620502:
                return "秦州区";
            case 620503:
                return "麦积区";
            case 620521:
                return "清水县";
            case 620522:
                return "秦安县";
            case 620523:
                return "甘谷县";
            case 620524:
                return "武山县";
            case 620525:
                return "张家川回族自治县";
            case 6206:
                return "武威市";
            case 620601:
                return "市辖区";
            case 620602:
                return "凉州区";
            case 620621:
                return "民勤县";
            case 620622:
                return "古浪县";
            case 620623:
                return "天祝藏族自治县";
            case 6207:
                return "张掖市";
            case 620701:
                return "市辖区";
            case 620702:
                return "甘州区";
            case 620721:
                return "肃南裕固族自治县";
            case 620722:
                return "民乐县";
            case 620723:
                return "临泽县";
            case 620724:
                return "高台县";
            case 620725:
                return "山丹县";
            case 6208:
                return "平凉市";
            case 620801:
                return "市辖区";
            case 620802:
                return "崆峒区";
            case 620821:
                return "泾川县";
            case 620822:
                return "灵台县";
            case 620823:
                return "崇信县";
            case 620824:
                return "华亭县";
            case 620825:
                return "庄浪县";
            case 620826:
                return "静宁县";
            case 6209:
                return "酒泉市";
            case 620901:
                return "市辖区";
            case 620902:
                return "肃州区";
            case 620921:
                return "金塔县";
            case 620922:
                return "瓜州县";
            case 620923:
                return "肃北蒙古族自治县";
            case 620924:
                return "阿克塞哈萨克族自治县";
            case 620981:
                return "玉门市";
            case 620982:
                return "敦煌市";
            case 6210:
                return "庆阳市";
            case 621001:
                return "市辖区";
            case 621002:
                return "西峰区";
            case 621021:
                return "庆城县";
            case 621022:
                return "环县";
            case 621023:
                return "华池县";
            case 621024:
                return "合水县";
            case 621025:
                return "正宁县";
            case 621026:
                return "宁县";
            case 621027:
                return "镇原县";
            case 6211:
                return "定西市";
            case 621101:
                return "市辖区";
            case 621102:
                return "安定区";
            case 621121:
                return "通渭县";
            case 621122:
                return "陇西县";
            case 621123:
                return "渭源县";
            case 621124:
                return "临洮县";
            case 621125:
                return "漳县";
            case 621126:
                return "岷县";
            case 6212:
                return "陇南市";
            case 621201:
                return "市辖区";
            case 621202:
                return "武都区";
            case 621221:
                return "成县";
            case 621222:
                return "文县";
            case 621223:
                return "宕昌县";
            case 621224:
                return "康县";
            case 621225:
                return "西和县";
            case 621226:
                return "礼县";
            case 621227:
                return "徽县";
            case 621228:
                return "两当县";
            case 6229:
                return "临夏回族自治州";
            case 622901:
                return "临夏市";
            case 622921:
                return "临夏县";
            case 622922:
                return "康乐县";
            case 622923:
                return "永靖县";
            case 622924:
                return "广河县";
            case 622925:
                return "和政县";
            case 622926:
                return "东乡族自治县";
            case 622927:
                return "积石山保安族东乡族撒拉族自治县";
            case 6230:
                return "甘南藏族自治州";
            case 623001:
                return "合作市";
            case 623021:
                return "临潭县";
            case 623022:
                return "卓尼县";
            case 623023:
                return "舟曲县";
            case 623024:
                return "迭部县";
            case 623025:
                return "玛曲县";
            case 623026:
                return "碌曲县";
            case 623027:
                return "夏河县";
            case 63:
                return "青海省";
            case 6301:
                return "西宁市";
            case 630101:
                return "市辖区";
            case 630102:
                return "城东区";
            case 630103:
                return "城中区";
            case 630104:
                return "城西区";
            case 630105:
                return "城北区";
            case 630121:
                return "大通回族土族自治县";
            case 630122:
                return "湟中县";
            case 630123:
                return "湟源县";
            case 6302:
                return "海东市";
            case 630202:
                return "乐都区";
            case 630221:
                return "平安县";
            case 630222:
                return "民和回族土族自治县";
            case 630223:
                return "互助土族自治县";
            case 630224:
                return "化隆回族自治县";
            case 630225:
                return "循化撒拉族自治县";
            case 6322:
                return "海北藏族自治州";
            case 632221:
                return "门源回族自治县";
            case 632222:
                return "祁连县";
            case 632223:
                return "海晏县";
            case 632224:
                return "刚察县";
            case 6323:
                return "黄南藏族自治州";
            case 632321:
                return "同仁县";
            case 632322:
                return "尖扎县";
            case 632323:
                return "泽库县";
            case 632324:
                return "河南蒙古族自治县";
            case 6325:
                return "海南藏族自治州";
            case 632521:
                return "共和县";
            case 632522:
                return "同德县";
            case 632523:
                return "贵德县";
            case 632524:
                return "兴海县";
            case 632525:
                return "贵南县";
            case 6326:
                return "果洛藏族自治州";
            case 632621:
                return "玛沁县";
            case 632622:
                return "班玛县";
            case 632623:
                return "甘德县";
            case 632624:
                return "达日县";
            case 632625:
                return "久治县";
            case 632626:
                return "玛多县";
            case 6327:
                return "玉树藏族自治州";
            case 632701:
                return "玉树市";
            case 632722:
                return "杂多县";
            case 632723:
                return "称多县";
            case 632724:
                return "治多县";
            case 632725:
                return "囊谦县";
            case 632726:
                return "曲麻莱县";
            case 6328:
                return "海西蒙古族藏族自治州";
            case 632801:
                return "格尔木市";
            case 632802:
                return "德令哈市";
            case 632821:
                return "乌兰县";
            case 632822:
                return "都兰县";
            case 632823:
                return "天峻县";
            case 64:
                return "宁夏回族自治区";
            case 6401:
                return "银川市";
            case 640101:
                return "市辖区";
            case 640104:
                return "兴庆区";
            case 640105:
                return "西夏区";
            case 640106:
                return "金凤区";
            case 640121:
                return "永宁县";
            case 640122:
                return "贺兰县";
            case 640181:
                return "灵武市";
            case 6402:
                return "石嘴山市";
            case 640201:
                return "市辖区";
            case 640202:
                return "大武口区";
            case 640205:
                return "惠农区";
            case 640221:
                return "平罗县";
            case 6403:
                return "吴忠市";
            case 640301:
                return "市辖区";
            case 640302:
                return "利通区";
            case 640303:
                return "红寺堡区";
            case 640323:
                return "盐池县";
            case 640324:
                return "同心县";
            case 640381:
                return "青铜峡市";
            case 6404:
                return "固原市";
            case 640401:
                return "市辖区";
            case 640402:
                return "原州区";
            case 640422:
                return "西吉县";
            case 640423:
                return "隆德县";
            case 640424:
                return "泾源县";
            case 640425:
                return "彭阳县";
            case 6405:
                return "中卫市";
            case 640501:
                return "市辖区";
            case 640502:
                return "沙坡头区";
            case 640521:
                return "中宁县";
            case 640522:
                return "海原县";
            case 65:
                return "新疆维吾尔自治区";
            case 6501:
                return "乌鲁木齐市";
            case 650101:
                return "市辖区";
            case 650102:
                return "天山区";
            case 650103:
                return "沙依巴克区";
            case 650104:
                return "新市区";
            case 650105:
                return "水磨沟区";
            case 650106:
                return "头屯河区";
            case 650107:
                return "达坂城区";
            case 650109:
                return "米东区";
            case 650121:
                return "乌鲁木齐县";
            case 6502:
                return "克拉玛依市";
            case 650201:
                return "市辖区";
            case 650202:
                return "独山子区";
            case 650203:
                return "克拉玛依区";
            case 650204:
                return "白碱滩区";
            case 650205:
                return "乌尔禾区";
            case 6521:
                return "吐鲁番地区";
            case 652101:
                return "吐鲁番市";
            case 652122:
                return "鄯善县";
            case 652123:
                return "托克逊县";
            case 6522:
                return "哈密地区";
            case 652201:
                return "哈密市";
            case 652222:
                return "巴里坤哈萨克自治县";
            case 652223:
                return "伊吾县";
            case 6523:
                return "昌吉回族自治州";
            case 652301:
                return "昌吉市";
            case 652302:
                return "阜康市";
            case 652323:
                return "呼图壁县";
            case 652324:
                return "玛纳斯县";
            case 652325:
                return "奇台县";
            case 652327:
                return "吉木萨尔县";
            case 652328:
                return "木垒哈萨克自治县";
            case 6527:
                return "博尔塔拉蒙古自治州";
            case 652701:
                return "博乐市";
            case 652702:
                return "阿拉山口市";
            case 652722:
                return "精河县";
            case 652723:
                return "温泉县";
            case 6528:
                return "巴音郭楞蒙古自治州";
            case 652801:
                return "库尔勒市";
            case 652822:
                return "轮台县";
            case 652823:
                return "尉犁县";
            case 652824:
                return "若羌县";
            case 652825:
                return "且末县";
            case 652826:
                return "焉耆回族自治县";
            case 652827:
                return "和静县";
            case 652828:
                return "和硕县";
            case 652829:
                return "博湖县";
            case 6529:
                return "阿克苏地区";
            case 652901:
                return "阿克苏市";
            case 652922:
                return "温宿县";
            case 652923:
                return "库车县";
            case 652924:
                return "沙雅县";
            case 652925:
                return "新和县";
            case 652926:
                return "拜城县";
            case 652927:
                return "乌什县";
            case 652928:
                return "阿瓦提县";
            case 652929:
                return "柯坪县";
            case 6530:
                return "克孜勒苏柯尔克孜自治州";
            case 653001:
                return "阿图什市";
            case 653022:
                return "阿克陶县";
            case 653023:
                return "阿合奇县";
            case 653024:
                return "乌恰县";
            case 6531:
                return "喀什地区";
            case 653101:
                return "喀什市";
            case 653121:
                return "疏附县";
            case 653122:
                return "疏勒县";
            case 653123:
                return "英吉沙县";
            case 653124:
                return "泽普县";
            case 653125:
                return "莎车县";
            case 653126:
                return "叶城县";
            case 653127:
                return "麦盖提县";
            case 653128:
                return "岳普湖县";
            case 653129:
                return "伽师县";
            case 653130:
                return "巴楚县";
            case 653131:
                return "塔什库尔干塔吉克自治县";
            case 6532:
                return "和田地区";
            case 653201:
                return "和田市";
            case 653221:
                return "和田县";
            case 653222:
                return "墨玉县";
            case 653223:
                return "皮山县";
            case 653224:
                return "洛浦县";
            case 653225:
                return "策勒县";
            case 653226:
                return "于田县";
            case 653227:
                return "民丰县";
            case 6540:
                return "伊犁哈萨克自治州";
            case 654002:
                return "伊宁市";
            case 654003:
                return "奎屯市";
            case 654021:
                return "伊宁县";
            case 654022:
                return "察布查尔锡伯自治县";
            case 654023:
                return "霍城县";
            case 654024:
                return "巩留县";
            case 654025:
                return "新源县";
            case 654026:
                return "昭苏县";
            case 654027:
                return "特克斯县";
            case 654028:
                return "尼勒克县";
            case 6542:
                return "塔城地区";
            case 654201:
                return "塔城市";
            case 654202:
                return "乌苏市";
            case 654221:
                return "额敏县";
            case 654223:
                return "沙湾县";
            case 654224:
                return "托里县";
            case 654225:
                return "裕民县";
            case 654226:
                return "和布克赛尔蒙古自治县";
            case 6543:
                return "阿勒泰地区";
            case 654301:
                return "阿勒泰市";
            case 654321:
                return "布尔津县";
            case 654322:
                return "富蕴县";
            case 654323:
                return "福海县";
            case 654324:
                return "哈巴河县";
            case 654325:
                return "青河县";
            case 654326:
                return "吉木乃县";
            case 6590:
                return "自治区直辖县级行政区划";
            case 659001:
                return "石河子市";
            case 659002:
                return "阿拉尔市";
            case 659003:
                return "图木舒克市";
            case 659004:
                return "五家渠市";
            case 71:
                return "台湾省";
            case 81:
                return "香港特别行政区";
            case 82:
                return "澳门特别行政区";
            default:
                return null;
        }
    }

    /**
     * @description 特殊字符过滤
     * @param value
     * @return String
     */
    private static String XSSFilter(String value) {
        if (value == null) {
            return null;
        }
        StringBuffer result = new StringBuffer(value.length());
        for (int i = 0; i < value.length(); ++i) {
            switch (value.charAt(i)) {
                case '<':
                    result.append("<");
                    break;
                case '>':
                    result.append(">");
                    break;
                case '"':
                    result.append("\"");
                    break;
                case '\'':
                    result.append("'");
                    break;
                case '%':
                    result.append("%");
                    break;
                case ';':
                    result.append(";");
                    break;
                case '(':
                    result.append("(");
                    break;
                case ')':
                    result.append(")");
                    break;
                case '&':
                    result.append("&");
                    break;
                case '+':
                    result.append("+");
                    break;
                default:
                    result.append(value.charAt(i));
                    break;
            }
        }
        return result.toString();
    }

    /**
     * @description 打印日志(info级别)
     * @param info
     */
    public static void info(Object info) {
        LogManager.getLogger(SysToolUtil.class).info(info);
    }

    /**
     * @description 打印日志(warn级别)
     * @param warn
     */
    public static void warn(Object warn) {
        LogManager.getLogger(SysToolUtil.class).warn(warn);
    }

    /**
     * @description 打印日志(error级别)
     * @param error
     */
    public static void error(Object error) {
        LogManager.getLogger(SysToolUtil.class).error(error);
    }

    /**
     * @description 打印日志(info级别)
     * @param info
     * @param clazz
     */
    public static void info(Object info, Class<?> clazz) {
        LogManager.getLogger(clazz).info(info);
    }

    /**
     * @description 打印日志(warn级别)
     * @param warn
     * @param clazz
     */
    public static void warn(Object warn, Class<?> clazz) {
        LogManager.getLogger(clazz).warn(warn);
    }

    /**
     * @description 打印日志(error级别)
     * @param error
     * @param clazz
     */
    public static void error(Object error, Class<?> clazz) {
        LogManager.getLogger(clazz).error(error);
    }

}
