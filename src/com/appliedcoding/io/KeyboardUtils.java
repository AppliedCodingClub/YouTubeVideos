package com.appliedcoding.io;

import java.io.IOException;

public class KeyboardUtils {

    public static final String A_KEY = "" + (int) 'a';
    public static final String C_LOWER_KEY = "" + (int) 'c';
    public static final String ESC_KEY = "27";
    public static final String EQUALS_KEY = "" + (int) '=';
    public static final String F_KEY = "" + (int) 'f';
    public static final String L_KEY = "" + (int) 'l';
    public static final String MINUS_KEY = "" + (int) '-';
    public static final String M_LOWER_KEY = "" + (int) 'm';
    public static final String M_UPPER_KEY = "" + (int) 'M';
    public static final String O_KEY = "" + (int) 'o';
    public static final String P_KEY = "" + (int) 'p';
    public static final String PLUS_KEY = "" + (int) '+';
    public static final String R_LOWER_KEY = "" + (int) 'r';
    public static final String R_UPPER_KEY = "" + (int) 'R';
    public static final String SPACE_KEY = "32";
    public static final String T_KEY = "" + (int) 't';

    private static String nextKey = "";

    public static String readKeyPress() throws IOException {
        String result = readOneKey();
        if (result.isEmpty()) { // no key from System.in
            if (!nextKey.isEmpty()) { // we have nextKey from previous time?
                result = nextKey;
                nextKey = "";
            }
        } else { // debounce
            boolean isLoop = true;
            while (isLoop) {
                String k = readOneKey();
                if (!k.equals(result)) { // new key after debounce?
                    nextKey = k;
                    isLoop = false;
                    while (System.in.available() > 0) {
                        System.in.read();
                    }
                }
            }
        }

        return result;
    }

    private static String readOneKey() throws IOException {
        String result = "";
        if (System.in.available() > 0) {
            result += System.in.read();
            if (result.equals("27") && System.in.available() >= 2) { // is arrow key?
                result += System.in.read();
                result += System.in.read();
            }
        }

        return result;
    }
}
