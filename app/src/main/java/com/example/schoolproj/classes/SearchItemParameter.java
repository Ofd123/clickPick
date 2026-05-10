package com.example.schoolproj.classes;

import androidx.annotation.NonNull;
import java.io.Serializable;

/**
 * Represents a specific search parameter or attribute for an item.
 * Stores an attribute name (e.g., "Color"), its value (e.g., "Red"), and whether it's editable.
 * Implements Serializable to allow passing between activities or fragments.
 */
public class SearchItemParameter implements Serializable
{
    /** The name of the attribute (e.g., "Brand", "Color"). */
    String Attribute;
    /** The value or setting for the attribute (e.g., "Sony", "Blue"). */
    String setting;
    /** Boolean flag indicating if this parameter can be modified by the user. */
    Boolean isEditable;

    /**
     * Parameterized constructor.
     * @param searchKey The attribute name.
     * @param searchVal The attribute value.
     * @param isEditable Whether the parameter is editable.
     */
    public SearchItemParameter(String searchKey, String searchVal, Boolean isEditable)
    {
        this.Attribute = searchKey;
        this.setting = searchVal;
        this.isEditable = isEditable;
    }

    /**
     * Constructor for non-editable parameters.
     * @param searchKey The attribute name.
     * @param searchVal The attribute value.
     */
    public SearchItemParameter(String searchKey, String searchVal)
    {
        this.Attribute = searchKey;
        this.setting = searchVal;
        this.isEditable = false;
    }

//    /**
//     * Constructor for creating an empty parameter with a specific editability.
//     * @param isEditable Whether the parameter is editable.
//     */
//    public SearchItemParameter(Boolean isEditable)
//    {
//        this.Attribute = "";
//        this.setting = "";
//        this.isEditable = isEditable;
//    }

    /** @return True if the parameter is editable. */
    public Boolean getEditable()
    {
        return isEditable;
    }

    /** @return The attribute name, or an empty string if null. */
    public String getAttribute()
    {
        if (Attribute != null) {
            return Attribute;
        } else {
            return "";
        }
    }

    /** @param searchKey The new attribute name. */
    public void setAttribute(String searchKey) {
        this.Attribute = searchKey;
    }

    /** @return The attribute setting/value, or an empty string if null. */
    public String getSetting() {
        if (setting != null) {
            return setting;
        } else {
            return "";
        }
    }

    /** @param setting The new attribute value. */
    public void setSetting(String setting) {
        this.setting = setting;
    }

//    /**
//     * Returns a string representation of the parameter.
//     * @return A string in "Attribute: setting" format.
//     */
//    @NonNull
//    @Override
//    public String toString() {
//        return Attribute + ": " + setting;
//    }
}
