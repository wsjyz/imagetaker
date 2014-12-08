package com.jyz.imagetaker;

/**
 * Created by dam on 2014/12/5.
 */
public class StringUtils {

    public static String  trims(String Str,String Flag) {
        if (Str == null || Str.equals("")) {
            return Str;
        } else {
            Str =   ""+Str;
            if(   Flag   ==   "l"   ||   Flag   ==   "L"   )/*trim   left   side   only*/
            {
                String RegularExp =  "^[　 ]+";
                return   Str.replaceAll(RegularExp,"");
            }
            else   if(   Flag   ==   "r"   ||   Flag   ==   "R"   )/*trim   right   side   only*/
            {
                String RegularExp =   "[　 ]+$";
                return   Str.replaceAll(RegularExp,"");
            }
            else/*defautly,   trim   both   left   and   right   side*/
            {
                String RegularExp =   "^[　 ]+|[　 ]+$";
                return   Str.replaceAll(RegularExp,"");
            }
        }
    }
}
