package com.chenjiayao.musicplayer.utils;


import android.util.Log;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.Arrays;


/**
 * Created by chen on 2015/12/17.
 */
public class HanZi2PinYinUtils {

    public static HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

    public static String HanZi2PinYin(String input) {
        String res = null;
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (isChinese(c)) {
                try {
                    String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    sb.append(pinyin[0]);
                } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                    badHanyuPinyinOutputFormatCombination.printStackTrace();
                }
            } else {
                sb.append(c);
            }
        }
        res = sb.toString();
        return res;
    }

    public static boolean isChinese(char a) {
        int v = (int) a;
        return (v >= 19968 && v <= 171941);
    }
}
