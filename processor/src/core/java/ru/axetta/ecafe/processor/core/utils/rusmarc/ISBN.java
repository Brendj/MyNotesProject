/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils.rusmarc;

import java.util.ArrayList;
import java.util.List;

public class ISBN {

    public enum StateEnum {Empty, Wrong, Normal}

    private StateEnum state;

    private String parsedIsbn;

    public ISBN() {
        parsedIsbn = "";
        state = StateEnum.Empty;
    }

    public ISBN(String isbnField) {
        this(isbnField, false);
    }

    public ISBN(String isbnField, boolean markedWrong) {
        String isbnLetters = "0123456789X ";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < isbnField.length(); ++i) {
            char c = isbnField.charAt(i);
            char ch = c == 'Х' ? 'X' : c;
            if (isbnLetters.indexOf(ch) != -1)
                sb.append(ch);
        }
        if (sb.length() == 0) {
            state = StateEnum.Empty;
            return;
        }
        String[] strs = split(' ', sb.toString());
        if (strs.length == 0) {
            state = StateEnum.Empty;
            return;
        }
        parsedIsbn = strs[0];
        if (markedWrong || !TryValidate(parsedIsbn))
            state = StateEnum.Wrong;
        else
            state = StateEnum.Normal;
    }

    private static String[] split(char separator, String str) {
        List<String> l = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            if (c == separator) {
                if (sb.length() > 0) {
                    l.add(sb.toString());
                    sb.delete(0, sb.length());
                }
                continue;
            }
            sb.append(c);
        }
        if (sb.length() > 0) {
            l.add(sb.toString());
        }
        String[] res = new String[l.size()];
        for (int i = 0; i < res.length; ++i)
            res[i] = l.get(i);
        return res;
    }

    /**
     * This method will validate a ISBN 10 or ISBN 13 code.
     *
     * @param isbn code to validate
     * @return true, if valid, otherwise false
     */
    public static boolean TryValidate(String isbn) {
        boolean result = false;

        if (isbn != null && isbn.length() > 0) {
            if (isbn.indexOf("-") != -1) isbn = isbn.replace("-", "");

            switch (isbn.length()) {
                case 10:
                    result = IsValidIsbn10(isbn);
                    break;
                case 13:
                    result = IsValidIsbn13(isbn);
                    break;
            }
        }

        return result;
    }

    /**
     * Validates ISBN10 codes
     *
     * @param isbn10 code to validate
     * @return true if valid
     */
    private static boolean IsValidIsbn10(String isbn10) {
        boolean result = false;
        if (isbn10 != null && isbn10.length() > 0) {
            if (isbn10.indexOf("-") != -1) isbn10 = isbn10.replace("-", "");

            // Length must be 10 and only the last character could be a char('X') or a numeric value,
            // otherwise it's not valid.
            if (isbn10.length() != 10)
                return false;
            try {
                Long.parseLong(isbn10.substring(0, isbn10.length() - 2));
            } catch (NumberFormatException ignored) {
                return false;
            }

            char lastChar = isbn10.charAt(isbn10.length() - 1);

            // Using the alternative way of calculation
            int sum = 0;
            for (int i = 0; i < 9; i++)
                sum += Integer.parseInt(String.valueOf(isbn10.charAt(i))) * (i + 1);

            // Getting the remainder or the checkdigit
            int remainder = sum % 11;

            // If the last character is 'X', then we should check if the checkdigit is equal to 10
            if (lastChar == 'X') {
                result = (remainder == 10);
            }
            // Otherwise check if the lastChar is numeric
            // Note: I'm passing sum to the TryParse method to not create a new variable again
            try {
                sum = Integer.parseInt(String.valueOf(lastChar));
                // lastChar is numeric, so let's compare it to remainder
                result = (remainder == Integer.parseInt(String.valueOf(lastChar)));
            } catch (NumberFormatException ignored) {
            }
        }

        return result;
    }

    /**
     * Validates ISBN13 codes
     *
     * @param isbn13 code to validate
     * @return true, if valid
     */
    private static boolean IsValidIsbn13(String isbn13) {
        boolean result = false;

        if (isbn13 != null && isbn13.length() > 0) {
            if (isbn13.indexOf("-") != -1) isbn13 = isbn13.replace("-", "");

            // If the length is not 13 or if it contains any non numeric chars, return false
            if (isbn13.length() != 13)
                return false;
            try {
                Long.parseLong(isbn13);
            } catch (NumberFormatException ignored) {
                return false;
            }

            // Comment Source: Wikipedia
            // The calculation of an ISBN-13 check digit begins with the first
            // 12 digits of the thirteen-digit ISBN (thus excluding the check digit itself).
            // Each digit, from left to right, is alternately multiplied by 1 or 3,
            // then those products are summed modulo 10 to give a value ranging from 0 to 9.
            // Subtracted from 10, that leaves a result from 1 to 10. A zero (0) replaces a
            // ten (10), so, in all cases, a single check digit results.
            int sum = 0;
            for (int i = 0; i < 12; i++) {
                sum += Integer.parseInt(String.valueOf(isbn13.charAt(i))) * (i % 2 == 1 ? 3 : 1);
            }

            int remainder = sum % 10;
            int checkDigit = 10 - remainder;
            if (checkDigit == 10) checkDigit = 0;

            result = (checkDigit == Integer.parseInt(String.valueOf(isbn13.charAt(12))));
        }

        return result;
    }


    public StateEnum getState() {
        return state;
    }

    @Override
    public String toString() {
        return parsedIsbn;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ISBN))
            return false;
        ISBN isbn = (ISBN) obj;
        return isbn.state == state && isbn.parsedIsbn.equals(parsedIsbn);
    }

    @Override
    public int hashCode() {
        return parsedIsbn.hashCode();
    }
}
