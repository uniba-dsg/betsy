package betsy.executables.util


class StringUtil {

    public static String addSpaces(String string, int length){
        int paddingLength = length - string.length()
        if(paddingLength < 0 ){
            paddingLength = 0
        }
        String paddingString = " " * paddingLength
        string + paddingString
    }

}
