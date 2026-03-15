package com.example.schoolproj.classes;

import androidx.annotation.NonNull;
import java.io.Serializable;

public class SearchItemParameter implements Serializable
{
    String Attribute , setting;
    Boolean isEditable;

    public SearchItemParameter(String searchKey, String searchVal,Boolean isEditable)
    {
        this.Attribute = searchKey;
        this.setting = searchVal;
        this.isEditable = isEditable;
    }
    public SearchItemParameter(String searchKey, String searchVal)
    {
        this.Attribute = searchKey;
        this.setting = searchVal;
        this.isEditable = false;
    }
    public SearchItemParameter(Boolean isEditable)
    {
        this.Attribute = "enter attribute";
        this.setting = "enter settings";
        this.isEditable = isEditable;
    }

    public Boolean getEditable()
    {
        return isEditable;
    }



    public String getAttribute()
    {
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

    @NonNull
    @Override
    public String toString() {
        return Attribute + ": " + setting;
    }
}
