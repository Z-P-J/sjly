package com.zpj.shouji.market.utils;

import com.github.promeg.pinyinhelper.Pinyin;
import com.zpj.shouji.market.model.InstalledAppInfo;

import java.util.Comparator;

public class PinyinComparator implements Comparator<PinyinComparator.PinyinComparable> {

    public interface PinyinComparable {
        String getName();
    }

    @Override
    public int compare(PinyinComparator.PinyinComparable info1, PinyinComparator.PinyinComparable info2) {
        String name1 = info1.getName();
        String name2 = info2.getName();
        int len1 = name1.length();
        int len2 = name2.length();
        int len = Math.min(len1, len2);

        int result = 0;
        for (int i = 0; i < len; i++) {
            result = compare(name1.charAt(i), name2.charAt(i));
            if (result != 0) {
                break;
            }
        }
        if (result == 0) {
            return Integer.compare(len1, len2);
        } else {
            return result;
        }
    }

    private int compare(char ch1, char ch2) {
        boolean isDigit1 = Character.isDigit(ch1);
        boolean isDigit2 = Character.isDigit(ch2);

        boolean isChinese1 = Pinyin.isChinese(ch1);
        boolean isChinese2 = Pinyin.isChinese(ch2);
        boolean isLetter1 = Character.isLetter(ch1);
        boolean isLetter2 = Character.isLetter(ch2);

        boolean isOther1 = !isChinese1 && !isLetter1 && !isDigit1;
        boolean isOther2 = !isChinese2 && !isLetter2 && !isDigit2;

        if (isOther1 && isOther2) {
            return Character.compare(ch1, ch2);
        } else if (isOther1) {
            return -1;
        } else if (isOther2) {
            return 1;
        }

        if (isDigit1 && isDigit2) {
            return Integer.compare(Integer.parseInt(String.valueOf(ch1)), Integer.parseInt(String.valueOf(ch2)));
        } else if (isDigit1 && !isDigit2) {
            return -1;
        } else if (!isDigit1 && isDigit2) {
            return 1;
        } else {
            if (isChinese1 && isChinese2) {
                return Pinyin.toPinyin(ch1).compareTo(Pinyin.toPinyin(ch2));
            }
            if (isLetter1 && isChinese2) {
                ch2 = Pinyin.toPinyin(ch2).charAt(0);
            } else if (isChinese1 && isLetter2) {
                ch1 = Pinyin.toPinyin(ch1).charAt(0);
            }
            isLetter1 = Character.isLetter(ch1);
            isLetter2 = Character.isLetter(ch2);
            if (isLetter1 && isLetter2) {
                String str1 = String.valueOf(ch1);
                String str2 = String.valueOf(ch2);
                if (str1.equalsIgnoreCase(str2)) {
                    return -Character.compare(ch1, ch2);
                } else {
                    ch1 = str1.toUpperCase().charAt(0);
                    ch2 = str2.toUpperCase().charAt(0);
                    return Character.compare(ch1, ch2);
                }
            } else if (isLetter1) {
                return 1;
            } else if (isLetter2) {
                return -1;
            }
            return Character.compare(ch1, ch2);
        }
    }

}
