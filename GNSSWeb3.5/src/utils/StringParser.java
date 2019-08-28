package utils;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class StringParser {
   //如果单独搜索attname的子串，可能会出现在其他json的值中，所以前后加上双引号搜索。
   private static String JsonMark = "\"";
   private static String JsonSeperate = ",";
   private static String JsonStart = "{";
   private static String JsonEnd = "}";
   private static String JsonArrayStart = "[";
   private static String JsonArrayEnd = "]";

   public static String getJSONValue(String JsonString, String attname){
    attname = JsonMark + attname + JsonMark;
//System.out.println("JsonString = " + JsonString);
int nameIndex = JsonString.indexOf(attname);
    if (nameIndex < 0)
    return null;
    String tail = JsonString.substring(nameIndex+attname.length() + 1);
    int endIndex = 0;
    if(tail.startsWith(JsonMark)){
    tail=tail.substring(1);
    endIndex=tail.indexOf(JsonMark);
    return tail.substring(0, endIndex);
    
    }else
    //以{和[开头的json内容要考虑到复合括号的情况
    if(tail.startsWith(JsonStart)){
    int count = 1;
    for(int i = 1; i<tail.length(); i++){
    if(tail.charAt(i) == JsonEnd.charAt(0)){
    count -- ;
    if( count == 0)
    endIndex = i;
    }
    
    if(tail.charAt(i) == JsonStart.charAt(0)){
    count ++ ;
    }
    }
    //endIndex=tail.indexOf(JsonEnd);
    return tail.substring(0,endIndex+1);
    }else if(tail.startsWith(JsonArrayStart)){
    int count = 1;
    for(int i = 1; i<tail.length(); i++){
    if(tail.charAt(i) == JsonArrayEnd.charAt(0)){
    count -- ;
    if( count == 0)
    endIndex = i;
    }
    
    if(tail.charAt(i) == JsonArrayStart.charAt(0)){
    count ++ ;
    }
    }
    
    //endIndex=tail.indexOf(JsonArrayEnd);
    return tail.substring(0,endIndex+1);
    }else{
    //这个地方可能出现数值等
    endIndex=tail.indexOf(JsonEnd);
    int endIndex1=tail.indexOf(JsonSeperate);
    if(endIndex1!=-1&&endIndex1<endIndex)
    endIndex=endIndex1;
    return tail.substring(0, endIndex);     
    }
}

   public static String setJSONValue(String JsonString, String attname, String value){
    
    //采用跟getJSONValue相同的方法，找到首尾节点后进行拼接
    attname = JsonMark + attname + JsonMark;
    int nameIndex = JsonString.indexOf(attname);
    if (nameIndex < 0)
    return null;
    int startIndex = nameIndex+attname.length() + 1;
    String tail = JsonString.substring(startIndex);
    
    int endIndex = 0;
    if(tail.startsWith(JsonMark)){
    startIndex++;
    tail=tail.substring(1);
    endIndex=tail.indexOf(JsonMark);
    
    }else
    //以{和[开头的json内容要考虑到复合括号的情况
    if(tail.startsWith(JsonStart)){
    int count = 1;
    for(int i = 1; i<tail.length(); i++){
    if(tail.charAt(i) == JsonEnd.charAt(0)){
    count -- ;
    if( count == 0)
    endIndex = i;
    }
    
    if(tail.charAt(i) == JsonStart.charAt(0)){
    count ++ ;
    }
    }
    endIndex ++;
    }else if(tail.startsWith(JsonArrayStart)){
    int count = 1;
    for(int i = 1; i<tail.length(); i++){
    if(tail.charAt(i) == JsonArrayEnd.charAt(0)){
    count -- ;
    if( count == 0)
    endIndex = i;
    }
    
    if(tail.charAt(i) == JsonArrayStart.charAt(0)){
    count ++ ;
    }
    }
    
    endIndex ++;
    }else{
    //这个地方可能出现数值等
    endIndex=tail.indexOf(JsonEnd);
    int endIndex1=tail.indexOf(JsonSeperate);
    if(endIndex1!=-1&&endIndex1<endIndex)
    endIndex=endIndex1; 
    }
    

    return JsonString.substring(0, startIndex)+value+tail.substring(endIndex);
   }
   
   
   public static String getSubString(String originString, String attname, String endMark)
   {
       attname = JsonMark + attname + JsonMark;
       int nameIndex = originString.indexOf(attname);
       if (nameIndex < 0)
           return null;
       String tail = originString.substring(nameIndex);
       int indexEnd = tail.indexOf(endMark);
       return tail.substring(0, indexEnd);
   }

   public static String getStringContent(String originString, String startMark, String endMark)
   {
       int startIndex = originString.indexOf(startMark);
       String tail = originString.substring(startIndex + 1);
       int endIndex = tail.indexOf(endMark);
       return tail.substring(0, endIndex);
   }

   //默认情况下以Json串分割
   public static String[] getStringArray(String originString)
   {
       originString = originString.substring(1, originString.length() - 1);
       if(!originString.contains(JsonEnd))
    return new String[0];
       String[] stringArray = originString.split(JsonEnd);
       for (int i = 0; i < stringArray.length; i++) {
           if (stringArray[i].startsWith(JsonSeperate))
               stringArray[i] = stringArray[i].substring(1) + JsonEnd;
           else
               stringArray[i] = stringArray[i] + JsonEnd;
           stringArray[i]=stringArray[i].trim();
       }
       return stringArray;
   }

   //有mark的情况下以mark分割
   public static String[] getStringArray(String originString, String mark)
   {
       return originString.split(mark);
   }

   public static String insertString(String originString, String insertContent, int position)
   {
       return originString.substring(0, position) + insertContent + originString.substring(position, originString.length());
   }
   
   //数值型数据的过滤
   //originList 是原始字符串列表
   //attname 是要过滤的字段
   //operator 分别为equal moreThan lessThan
   //equal =value  moreThan >value lessThan < value moreEqual >=value lessEqual <= value
   public static List<String> numberFilter(List<String> originList, String attname, String operator, Double value){
    Double num;
    StringData str;
    switch(operator){
    case "equal":
    for(int i=originList.size()-1; i>=0; i--){
        str = new StringData(originList.get(i));
        num = str.getNumber(attname);
        if (!num.equals(value))
        originList.remove(i);
        }
    break;
    case "moreThan":
    for(int i=originList.size()-1; i>=0; i--){
        str = new StringData(originList.get(i));
        num = str.getNumber(attname);
        if (Double.compare(num, value)<=0)
        originList.remove(i);
        }
    break;
    case "moreEqual":
    for(int i=originList.size()-1; i>=0; i--){
        str = new StringData(originList.get(i));
        num = str.getNumber(attname);
        if (Double.compare(num, value)<0)
        originList.remove(i);
        }
    break;
    case "lessThan":
    for(int i=originList.size()-1; i>=0; i--){
        str = new StringData(originList.get(i));
        num = str.getNumber(attname);
        if (Double.compare(num, value)>=0)
        originList.remove(i);
        }
    break;
    case "lessEqual":
    for(int i=originList.size()-1; i>=0; i--){
        str = new StringData(originList.get(i));
        num = str.getNumber(attname);
        if (Double.compare(num, value)>0)
        originList.remove(i);
        }
    break;
    default:    
    }
  
    return originList;
   }
   
   public static List<String> numberFilter(List<String> originList, String attname, String operator, Long value){
    Long num;
    StringData str;
    switch(operator){
    case "equal":
    for(int i=originList.size()-1; i>=0; i--){
        str = new StringData(originList.get(i));
        num = str.getTime(attname);
        if (!num.equals(value))
        originList.remove(i);
        }
    break;
    case "moreThan":
    for(int i=originList.size()-1; i>=0; i--){
        str = new StringData(originList.get(i));
        num = str.getTime(attname);
        if (Long.compare(num, value)<=0)
        originList.remove(i);
        }
    break;
    case "moreEqual":
    for(int i=originList.size()-1; i>=0; i--){
        str = new StringData(originList.get(i));
        num = str.getTime(attname);
        if (Long.compare(num, value)<0)
        originList.remove(i);
        }
    break;
    case "lessThan":
    for(int i=originList.size()-1; i>=0; i--){
        str = new StringData(originList.get(i));
        num = str.getTime(attname);
        if (Long.compare(num, value)>=0)
        originList.remove(i);
        }
    break;
    case "lessEqual":
    for(int i=originList.size()-1; i>=0; i--){
        str = new StringData(originList.get(i));
        num = str.getTime(attname);
        if (Long.compare(num, value)>0)
        originList.remove(i);
        }
    break;
    default:    
    }
  
    return originList;
   }
   

   public static String encode(String originString) throws UnsupportedEncodingException
   {
       byte a = 97;
       byte bys[] = originString.getBytes("UTF-8");
       int n = bys.length;
       byte[] codedbys = new byte[2 * n];
       int k = 0;
       for (int i = 0; i < n; i++) {
           byte b = bys[i];
           //取低四位
           codedbys[k] = (byte) ((b & 15) + a);
           k++;
           //取高四位,右移四位高位补0
           codedbys[k] = (byte) (((b >>> 4) & 15) + a);
           k++;
       }
       String codedString = new String(codedbys, "UTF-8");
       return codedString;
   }

   public static String decode(String codedString) throws UnsupportedEncodingException
   {
       byte a = 97;
       byte codedbys[] = codedString.getBytes("UTF-8");
       int n = codedbys.length;
       byte bys[] = new byte[n / 2];

       int k = 0;
       //upper计算高位，lower计算低位
       byte upper, lower;
       for (int i = 0; i < n / 2; i++) {
           lower = (byte) (codedbys[k] - a);
           k++;
           upper = (byte) (codedbys[k] - a);
           k++;
           bys[i] = (byte) ((upper << 4) | lower);
       }
       String originString = new String(bys, "UTF-8");
       return originString;
   }
}
