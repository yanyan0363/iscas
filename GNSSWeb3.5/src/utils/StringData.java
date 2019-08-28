package utils;

public class StringData {
    private static String JsonMark = "\"";
    private static String JsonSeperate = ",";
    private static String JsonStart = "{";
    private static String JsonEnd = "}";
 
 
    private String originData;
 
    public StringData(String originData)
    {
        this.originData = originData;
    }
 
    public String getOriginData()
    {
        return originData;
    }
 
    public void setOriginData(String originData)
    {
        this.originData = originData;
    }
 
    public String getString(String attname)
    {
        attname = JsonMark + attname + JsonMark;
        int nameIndex = originData.indexOf(attname);
        if (nameIndex < 0)
            return null;
        String tail = originData.substring(nameIndex + attname.length() + 2);
        int endIndex = tail.indexOf(JsonMark);
        return tail.substring(0, endIndex);
    }
 
    public Double getNumber(String attname)
    {
        attname = JsonMark + attname + JsonMark;
        int nameIndex = originData.indexOf(attname);
        if (nameIndex < 0)
            return null;
        String tail = originData.substring(nameIndex + attname.length() + 1);
        int endIndex = tail.indexOf(JsonEnd);
        int endIndex1 = tail.indexOf(JsonSeperate);
        if (endIndex1 != -1 && endIndex1 < endIndex)
            endIndex = endIndex1;
        return new Double(tail.substring(0, endIndex));
    }
 
    public Long getTime(String attname)
    {
        attname = JsonMark + attname + JsonMark;
        int nameIndex = originData.indexOf(attname);
        if (nameIndex < 0)
            return null;
        String tail = originData.substring(nameIndex + attname.length() + 1);
        int endIndex = tail.indexOf(JsonEnd);
        int endIndex1 = tail.indexOf(JsonSeperate);
        if (endIndex1 != -1 && endIndex1 < endIndex)
            endIndex = endIndex1;
        return new Long(tail.substring(0, endIndex));
    }
 
}

