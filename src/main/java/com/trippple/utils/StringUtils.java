package com.trippple.utils;

import java.util.Arrays;
import java.util.regex.Pattern;



public class StringUtils {
    static Pattern newline = Pattern.compile("\n");
    static Pattern AZwithspace = Pattern.compile("[^A-ZÆØÅa-zæøå0-9- ]");
    static Pattern AZwithoutspace = Pattern.compile("[^a-zæøåA-ZÆØÅ0-9-]");
    static Pattern capitalSplit = Pattern.compile("(?=(?<=[a-zæøå])[A-ZÆØÅ])");

    public static float wordAppearanceCount(String word, String[] words) {
        String[] wordArray = StringUtils.splitByCapitalAndMakeLower(word);
        int count = 0;
        for (String w1 : wordArray) {
            for (String w2 : words) {
                if (w2.equalsIgnoreCase(w1)) {
                    count++;
                }
            }
        }
        return count;
    }

    public static String[] splitStringToArray(String text) {
        String firstReplace = newline.matcher(text).replaceAll(" ");
        String secondReplace = AZwithspace.matcher(firstReplace).replaceAll("");
        return AZwithoutspace.split(secondReplace);

    }

    public static String[] splitByCapital(String text) {
        return capitalSplit.split(text);
    }
    public static String[] splitByCapitalAndMakeLower(String text) {
        String[] splits = StringUtils.splitByCapital(text);
        String textLower = text.toLowerCase();
        String[] lowerSplits = new String[splits.length];
        int letterCount = 0;
        for (int i = 0; i < splits.length; i++) {
            int length = splits[i].length();
            lowerSplits[i] = textLower.substring(letterCount, letterCount + length);
            letterCount += length;
        }
        return lowerSplits;
    }

    public static boolean arrayIsNullOrEmpty(String[] arr) {
        return arr == null || arr.length == 0;
    }
}