/*
=============================================
Author:		Heejae Kim
Create date: 2020-06-30
Description:	Util Methods for String
=============================================
*/

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StringUtil {

    public static boolean isEmpty(String input) {
        return input == null
                 || "".equals(input.trim())
                 || input.trim().equals("\"\"");
//                 || "undefined".equalsIgnoreCase(input.trim())
//                 || "null".equalsIgnoreCase(input.trim());
    }

    public static boolean contains(String[] strings, String text) {
        for(String string : strings) {
            if(string.equals(text)) return true;
        }
        return false;
    }

    public static String[] splitwords(String word, String key) {
        if(StringUtil.isEmpty(key)) return null;
        String[] splitArr = word.split(key);
        return splitArr;
    }


    public static String getEmailName(String email) {

        if(StringUtil.isEmpty(email))   return "";

        String[] splitArr = email.split("[@._]");
        if(splitArr.length < 2) {
            return "";
        } else {
            return splitArr[0];
        }
    }
    /*
        금액 표시에 맞는 형태로 변환
    */
    public static String toCurrency(String currency) throws Exception {
        try {
            if(currency == null || currency.length() < 1) {
                return currency;
            }
            NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
            return format.format(new Long(currency));
        } catch(IllegalArgumentException e) {
            throw e;
        }
    }

    public static boolean isValidEmail(String emailAddress) {
        String[] tokens = emailAddress.split("@");
        return tokens.length == 2 && !isEmpty(tokens[0]) && !isEmpty(tokens[1]);
    }

    public static String capitalize(String input) {
        return input != null && !input.equals("") ? input.substring(0, 1).toUpperCase()
                + input.substring(1).toLowerCase() : input;
    }

    /*
        주민번호 형식으로 변환
        1111111111111 -> 111111-1111111
    */
    public static String toCitizen(String citizen) {
        String result = "";
        if(citizen.length() != 13) {
            return citizen;
        }
        result = citizen.substring(0, 6) + "-" + citizen.substring(6, 13);
        return result;
    }

    /*
        전화번호 형식으로 변환 021234567 -> 02-123-4567
    */
    public static String toTel(String telNumber) {
        String result = "";
        if(telNumber.length() > 11) {
            return telNumber;
        }
        // 전화번호 길이에 따라 리턴
        switch(telNumber.length()) {
            case 11:
                if(telNumber.substring(0, 3).equals("050")) {
                    result = telNumber.substring(0, 4) + "-" + telNumber.substring(4, 7) + "-" + telNumber.substring(7, 11);
                } else if(telNumber.substring(0, 4).equals("0130")) {
                    result = telNumber.substring(0, 4) + "-" + telNumber.substring(4, 7) + "-" + telNumber.substring(7, 11);
                } else {
                    result = telNumber.substring(0, 3) + "-" + telNumber.substring(3, 7) + "-" + telNumber.substring(7, 11);
                }
                break;
            case 10:
                if(telNumber.substring(0, 2).equals("02")) {
                    result = telNumber.substring(0, 2) + "-" + telNumber.substring(2, 6) + "-" + telNumber.substring(6, 10);
                } else {
                    result = telNumber.substring(0, 3) + "-" + telNumber.substring(3, 6) + "-" + telNumber.substring(6, 10);
                }
                break;
            case 9:
                result = telNumber.substring(0, 2) + "-" + telNumber.substring(2, 5) + "-" + telNumber.substring(5, 9);
                break;
            case 8:
                result = telNumber.substring(0, 4) + "-" + telNumber.substring(4, 8);
                break;
            default :
                result = telNumber;
        }
        return result;
    }
    /*
        사업자등록번호 형식으로 변환 1234567890 -> 123-45-67890
    */
    public static String toBiz(String bizNumber) {
        if(bizNumber.length() != 10) {
            return bizNumber;
        }
        String result = "";
        result = bizNumber.substring(0, 3) + "-" + bizNumber.substring(3, 5) + "-" + bizNumber.substring(5, 10);

        return result;
    }

    public static String genSaveFileName(String extName) {
        // 현재 시간을 기준으로 파일 이름 생성
        String fileName = "";

        Calendar calendar = Calendar.getInstance();
        fileName += calendar.get(Calendar.YEAR);
        fileName += calendar.get(Calendar.MONTH);
        fileName += calendar.get(Calendar.DATE);
        fileName += calendar.get(Calendar.HOUR);
        fileName += calendar.get(Calendar.MINUTE);
        fileName += calendar.get(Calendar.SECOND);
        fileName += calendar.get(Calendar.MILLISECOND);
        fileName += extName;

        return fileName;
    }

    public static int calculateAge(String birthday) {

        // yyMMdd 로 들어온 결과를 바탕으로 나이 계산

        String today = "";
        int age = 0;
        int offset = 9;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        today = formatter.format(new Date()); // 시스템 날짜를 가져와서 yyMMdd 형태로 변환
        int currentYear2Digit = Integer.parseInt(today.substring(2, 4));
        int currentYear = Integer.parseInt(today.substring(0, 4));
        int birthYear = Integer.parseInt(birthday.substring(0, 2));

        if(currentYear2Digit >= birthYear + offset)      birthYear += 2000;
        else                                            birthYear += 1900;

        age = currentYear - birthYear + 1;

        return age;

    }

    public static String calculateAgeGroup(int age) {
        return age / 10 * 10 + "대";
    }

}