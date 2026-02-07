package com.example.schoolproj.classes;

public class SearchItemParameter
{
    String Attribute , setting;


    public SearchItemParameter(String searchKey, String searchVal)
    {
        this.Attribute = searchKey;
        this.setting = searchVal;
    }

    public String getAttribute() {
        return Attribute;
    }

    public void setAttribute(String searchKey) {
        this.Attribute = searchKey;
    }

    public String getSetting() {
        return setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }
}
