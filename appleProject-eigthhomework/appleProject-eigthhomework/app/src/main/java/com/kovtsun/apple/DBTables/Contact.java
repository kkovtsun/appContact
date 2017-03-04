package com.kovtsun.apple.DBTables;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class Contact implements Serializable{

    @DatabaseField(generatedId = true, columnName = "contact_id")
    public int contactId;

    @DatabaseField(columnName = "contact_name")
    public String contactName = "";

    @DatabaseField(columnName = "contact_number")
    public String contactNumber = "";

    @Override
    public String toString() {
        return "Contact{" + "id=" + contactId + ", name=" + contactName + ", number=" + contactNumber + '}';
    }

    public Contact() {}

    public int getContactId() {
        return contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public Contact(final String contactName, final String contactNumber) {
        this.contactName = contactName;
        this.contactNumber = contactNumber;
    }

    public Contact(int contactId, String contactName, String contactNumber) {
        this.contactId = contactId;
        this.contactName = contactName;
        this.contactNumber = contactNumber;
    }
}
