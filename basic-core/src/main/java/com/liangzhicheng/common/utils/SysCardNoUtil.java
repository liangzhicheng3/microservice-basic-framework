package com.liangzhicheng.common.utils;

import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @description 证件号码校验工具类
 * @author liangzhicheng
 * @since 2021-08-06
 */
public class SysCardNoUtil extends StringUtils {

    /**
     * 中国居民证件号码最小长度
     */
    public static final int CHINA_ID_MIN_LENGTH = 15;

    /**
     * 中国居民身份证号码最大长度
     */
    public static final int CHINA_ID_MAX_LENGTH = 18;

    /**
     * 省，直辖市代码表
     */
    public static final String cityCode[] = {
            "11", "12", "13", "14", "15", "21", "22", "23", "31", "32", "33", "34", "35", "36", "37", "41",
            "42", "43", "44", "45", "46", "50", "51", "52", "53", "54", "61", "62", "63", "64", "65", "71",
            "81", "82", "91"
    };

    /**
     * 每位加权因子
     */
    public static final int power[] = {
            7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2
    };

    /**
     * 第18位校检码
     */
    public static final String verifyCode[] = {
            "1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"
    };

    /**
     * 最低年限
     */
    public static final int MIN = 1930;

    /**
     * 各省
     */
    public static Map<String, String> cityCodes = new HashMap<String, String>();

    /**
     * 台湾身份首字母对应数字
     */
    public static Map<String, Integer> twFirstCode = new HashMap<String, Integer>();

    /**
     * 香港身份首字母对应数字
     */
    public static Map<String, Integer> hkFirstCode = new HashMap<String, Integer>();

    static {
        cityCodes.put("11", "北京");
        cityCodes.put("12", "天津");
        cityCodes.put("13", "河北");
        cityCodes.put("14", "山西");
        cityCodes.put("15", "内蒙古");
        cityCodes.put("21", "辽宁");
        cityCodes.put("22", "吉林");
        cityCodes.put("23", "黑龙江");
        cityCodes.put("31", "上海");
        cityCodes.put("32", "江苏");
        cityCodes.put("33", "浙江");
        cityCodes.put("34", "安徽");
        cityCodes.put("35", "福建");
        cityCodes.put("36", "江西");
        cityCodes.put("37", "山东");
        cityCodes.put("41", "河南");
        cityCodes.put("42", "湖北");
        cityCodes.put("43", "湖南");
        cityCodes.put("44", "广东");
        cityCodes.put("45", "广西");
        cityCodes.put("46", "海南");
        cityCodes.put("50", "重庆");
        cityCodes.put("51", "四川");
        cityCodes.put("52", "贵州");
        cityCodes.put("53", "云南");
        cityCodes.put("54", "西藏");
        cityCodes.put("61", "陕西");
        cityCodes.put("62", "甘肃");
        cityCodes.put("63", "青海");
        cityCodes.put("64", "宁夏");
        cityCodes.put("65", "新疆");
        cityCodes.put("71", "台湾");
        cityCodes.put("81", "香港");
        cityCodes.put("82", "澳门");
        cityCodes.put("91", "国外");

        twFirstCode.put("A", 10);
        twFirstCode.put("B", 11);
        twFirstCode.put("C", 12);
        twFirstCode.put("D", 13);
        twFirstCode.put("E", 14);
        twFirstCode.put("F", 15);
        twFirstCode.put("G", 16);
        twFirstCode.put("H", 17);
        twFirstCode.put("J", 18);
        twFirstCode.put("K", 19);
        twFirstCode.put("L", 20);
        twFirstCode.put("M", 21);
        twFirstCode.put("N", 22);
        twFirstCode.put("P", 23);
        twFirstCode.put("Q", 24);
        twFirstCode.put("R", 25);
        twFirstCode.put("S", 26);
        twFirstCode.put("T", 27);
        twFirstCode.put("U", 28);
        twFirstCode.put("V", 29);
        twFirstCode.put("X", 30);
        twFirstCode.put("Y", 31);
        twFirstCode.put("W", 32);
        twFirstCode.put("Z", 33);
        twFirstCode.put("I", 34);
        twFirstCode.put("O", 35);

        hkFirstCode.put("A", 1);
        hkFirstCode.put("B", 2);
        hkFirstCode.put("C", 3);
        hkFirstCode.put("R", 18);
        hkFirstCode.put("U", 21);
        hkFirstCode.put("Z", 26);
        hkFirstCode.put("X", 24);
        hkFirstCode.put("W", 23);
        hkFirstCode.put("O", 15);
        hkFirstCode.put("N", 14);
    }

    /**
     * @description 验证身份证是否合法
     * @param cardNo
     * @return boolean
     */
    public static boolean validateCard(String cardNo) {
        cardNo = cardNo.trim();
        if (validateCard18(cardNo)) {
            return true;
        }
        if (validateCard15(cardNo)) {
            return true;
        }
        return false;
    }

    /**
     * @description 验证18位证件号码是否合法
     * @param cardNo
     * @return boolean
     */
    public static boolean validateCard18(String cardNo) {
        boolean bTrue = false;
        if (cardNo.length() == CHINA_ID_MAX_LENGTH) {
            // 前17位
            String code17 = cardNo.substring(0, 17);
            // 第18位
            String code18 = cardNo.substring(17, CHINA_ID_MAX_LENGTH);
            if (isNum(code17)) {
                char[] cArr = code17.toCharArray();
                if (cArr != null) {
                    int[] iCard = converCharToInt(cArr);
                    int iSum17 = getPowerSum(iCard);
                    // 获取校验位
                    String val = getCheckCode18(iSum17);
                    if (val.length() > 0) {
                        if (val.equalsIgnoreCase(code18)) {
                            bTrue = true;
                        }
                    }
                }
            }
        }
        return bTrue;
    }

    /**
     * @description 验证15位证件号码是否合法
     * @param cardNo
     * @return boolean
     */
    public static boolean validateCard15(String cardNo) {
        if (cardNo.length() != CHINA_ID_MIN_LENGTH) {
            return false;
        }
        if (isNum(cardNo)) {
            String proCode = cardNo.substring(0, 2);
            if (cityCodes.get(proCode) == null) {
                return false;
            }
            String birthCode = cardNo.substring(6, 12);
            Date birthDate = null;
            try {
                birthDate = new SimpleDateFormat("yy").parse(birthCode.substring(0, 2));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar cal = Calendar.getInstance();
            if (birthDate != null)
                cal.setTime(birthDate);
            if (!valiDate(cal.get(Calendar.YEAR), Integer.valueOf(birthCode.substring(2, 4)),
                    Integer.valueOf(birthCode.substring(4, 6)))) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * @description 将15位证件号码转换为18位
     * @param cardNo
     * @return String
     */
    public static String convert15CardTo18(String cardNo) {
        String idCard18 = "";
        if (cardNo.length() != CHINA_ID_MIN_LENGTH) {
            return null;
        }
        if (isNum(cardNo)) {
            // 获取出生年月日
            String birthday = cardNo.substring(6, 12);
            Date birthDate = null;
            try {
                birthDate = new SimpleDateFormat("yyMMdd").parse(birthday);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar cal = Calendar.getInstance();
            if (birthDate != null)
                cal.setTime(birthDate);
            // 获取出生年(完全表现形式,如：2010)
            String sYear = String.valueOf(cal.get(Calendar.YEAR));
            idCard18 = cardNo.substring(0, 6) + sYear + cardNo.substring(8);
            // 转换字符数组
            char[] cArr = idCard18.toCharArray();
            if (cArr != null) {
                int[] iCard = converCharToInt(cArr);
                int iSum17 = getPowerSum(iCard);
                // 获取校验位
                String sVal = getCheckCode18(iSum17);
                if (sVal.length() > 0) {
                    idCard18 += sVal;
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }
        return idCard18;
    }

    /**
     * @description 验证台湾证件号码
     * @param cardNo
     * @return boolean
     */
    public static boolean validateTWCard(String cardNo) {
        String start = cardNo.substring(0, 1);
        String mid = cardNo.substring(1, 9);
        String end = cardNo.substring(9, 10);
        Integer iStart = twFirstCode.get(start);
        Integer sum = iStart / 10 + (iStart % 10) * 9;
        char[] chars = mid.toCharArray();
        Integer iflag = 8;
        for (char c : chars) {
            sum = sum + Integer.valueOf(c + "") * iflag;
            iflag--;
        }
        return (sum % 10 == 0 ? 0 : (10 - sum % 10)) == Integer.valueOf(end) ? true : false;
    }

    /**
     * @description 验证香港证件号码(存在Bug，部份特殊身份证无法检查)
     *              身份证前2位为英文字符，如果只出现一个英文字符则表示第一位是空格，对应数字58 前2位英文字符A-Z分别对应数字10-35
     *              最后一位校验码为0-9的数字加上字符"A"，"A"代表10
     *              将身份证号码全部转换为数字，分别对应乘9-1相加的总和，整除11则证件号码有效
     * @param cardNo
     * @return boolean
     */
    public static boolean validateHKCard(String cardNo) {
        String card = cardNo.replaceAll("[\\(|\\)]", "");
        Integer sum = 0;
        if (card.length() == 9) {
            sum = (Integer.valueOf(card.substring(0, 1).toUpperCase().toCharArray()[0]) - 55) * 9
                    + (Integer.valueOf(card.substring(1, 2).toUpperCase().toCharArray()[0]) - 55) * 8;
            card = card.substring(1, 9);
        } else {
            sum = 522 + (Integer.valueOf(card.substring(0, 1).toUpperCase().toCharArray()[0]) - 55) * 8;
        }
        String mid = card.substring(1, 7);
        String end = card.substring(7, 8);
        char[] chars = mid.toCharArray();
        Integer iflag = 7;
        for (char c : chars) {
            sum = sum + Integer.valueOf(c + "") * iflag;
            iflag--;
        }
        if (end.toUpperCase().equals("A")) {
            sum = sum + 10;
        } else {
            sum = sum + Integer.valueOf(end);
        }
        return (sum % 11 == 0) ? true : false;
    }

    /**
     * @description 将字符数组转换成数字数组
     * @param chars
     * @return int[]
     */
    public static int[] converCharToInt(char[] chars) {
        int len = chars.length;
        int[] iArr = new int[len];
        try {
            for (int i = 0; i < len; i++) {
                iArr[i] = Integer.parseInt(String.valueOf(chars[i]));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return iArr;
    }

    /**
     * @description 将身份证的每位和对应位的加权因子相乘之后，再得到和值
     * @param iArr
     * @return int
     */
    public static int getPowerSum(int[] iArr) {
        int iSum = 0;
        if (power.length == iArr.length) {
            for (int i = 0; i < iArr.length; i++) {
                for (int j = 0; j < power.length; j++) {
                    if (i == j) {
                        iSum = iSum + iArr[i] * power[j];
                    }
                }
            }
        }
        return iSum;
    }

    /**
     * @description 将power和值与11取模获得余数进行校验码判断
     * @param iSum
     * @return String
     */
    public static String getCheckCode18(int iSum) {
        String sCode = "";
        switch (iSum % 11) {
            case 10:
                sCode = "2";
                break;
            case 9:
                sCode = "3";
                break;
            case 8:
                sCode = "4";
                break;
            case 7:
                sCode = "5";
                break;
            case 6:
                sCode = "6";
                break;
            case 5:
                sCode = "7";
                break;
            case 4:
                sCode = "8";
                break;
            case 3:
                sCode = "9";
                break;
            case 2:
                sCode = "x";
                break;
            case 1:
                sCode = "0";
                break;
            case 0:
                sCode = "1";
                break;
        }
        return sCode;
    }

    /**
     * @description 根据身份编号获取年龄
     * @param cardNo
     * @return int
     */
    public static int getAgeByCardNo(String cardNo) {
        int iAge = 0;
        if (cardNo.length() == CHINA_ID_MIN_LENGTH) {
            cardNo = convert15CardTo18(cardNo);
        }
        String year = cardNo.substring(6, 10);
        Calendar cal = Calendar.getInstance();
        int iCurrYear = cal.get(Calendar.YEAR);
        iAge = iCurrYear - Integer.valueOf(year);
        return iAge;
    }

    /**
     * @description 根据身份编号获取生日
     * @param cardNo 身份编号
     * @return String
     */
    public static String getBirthByCardNo(String cardNo) {
        Integer len = cardNo.length();
        if (len < CHINA_ID_MIN_LENGTH) {
            return null;
        } else if (len == CHINA_ID_MIN_LENGTH) {
            cardNo = convert15CardTo18(cardNo);
        }
        return cardNo.substring(6, 14);
    }

    /**
     * @description 根据身份编号获取生日年
     * @param cardNo 身份编号
     * @return Short
     */
    public static Short getYearByCardNo(String cardNo) {
        Integer len = cardNo.length();
        if (len < CHINA_ID_MIN_LENGTH) {
            return null;
        } else if (len == CHINA_ID_MIN_LENGTH) {
            cardNo = convert15CardTo18(cardNo);
        }
        return Short.valueOf(cardNo.substring(6, 10));
    }

    /**
     * @description 根据身份编号获取生日月
     * @param cardNo 身份编号
     * @return Short
     */
    public static Short getMonthByCardNo(String cardNo) {
        Integer len = cardNo.length();
        if (len < CHINA_ID_MIN_LENGTH) {
            return null;
        } else if (len == CHINA_ID_MIN_LENGTH) {
            cardNo = convert15CardTo18(cardNo);
        }
        return Short.valueOf(cardNo.substring(10, 12));
    }

    /**
     * @description 根据身份编号获取生日天
     * @param cardNo 身份编号
     * @return Short
     */
    public static Short getDateByCardNo(String cardNo) {
        Integer len = cardNo.length();
        if (len < CHINA_ID_MIN_LENGTH) {
            return null;
        } else if (len == CHINA_ID_MIN_LENGTH) {
            cardNo = convert15CardTo18(cardNo);
        }
        return Short.valueOf(cardNo.substring(12, 14));
    }

    /**
     * @description 根据身份编号获取性别
     * @param cardNo 身份编号
     * @return String
     */
    public static String getGenderByCardNo(String cardNo) {
        String sGender = "N";
        if (cardNo.length() == CHINA_ID_MIN_LENGTH) {
            cardNo = convert15CardTo18(cardNo);
        }
        String sCardNum = cardNo.substring(16, 17);
        if (Integer.parseInt(sCardNum) % 2 != 0) {
            sGender = "M";
        } else {
            sGender = "F";
        }
        return sGender;
    }

    /**
     * @description 根据身份编号获取户籍省份
     * @param cardNo 身份编号
     * @return String
     */
    public static String getProvinceByCardNo(String cardNo) {
        int len = cardNo.length();
        String sProvince = null;
        String sProvinNum = "";
        if (len == CHINA_ID_MIN_LENGTH || len == CHINA_ID_MAX_LENGTH) {
            sProvinNum = cardNo.substring(0, 2);
        }
        sProvince = cityCodes.get(sProvinNum);
        return sProvince;
    }

    /**
     * @description 数字验证
     * @param val
     * @return boolean
     */
    public static boolean isNum(String val) {
        return val == null || "".equals(val) ? false : val.matches("^[0-9]*$");
    }

    /**
     * @description 验证小于当前日期 是否有效
     * @param iYear 待验证日期(年)
     * @param iMonth 待验证日期(月 1-12)
     * @param iDate 待验证日期(日)
     * @return boolean
     */
    public static boolean valiDate(int iYear, int iMonth, int iDate) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int datePerMonth;
        if (iYear < MIN || iYear >= year) {
            return false;
        }
        if (iMonth < 1 || iMonth > 12) {
            return false;
        }
        switch (iMonth) {
            case 2:
                boolean dm = ((iYear % 4 == 0 && iYear % 100 != 0) || (iYear % 400 == 0))
                        && (iYear > MIN && iYear < year);
                datePerMonth = dm ? 29 : 28;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                datePerMonth = 30;
                break;
            default:
                datePerMonth = 31;
        }
        return (iDate >= 1) && (iDate <= datePerMonth);
    }

}
