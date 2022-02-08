package com.example.ContactList.Model;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.ContactList.MyApplication;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

@Entity
public class Contact implements Parcelable
{
    public final static String LAST_UPDATED="LAST_UPDATED";
    @PrimaryKey
    @NonNull
    String phone;
    String name;
    String gender;
    Long lastUpdated= new Long(0);

    protected Contact(Parcel in) {
        phone = in.readString();
        name = in.readString();
        gender = in.readString();
        if (in.readByte() == 0) {
            lastUpdated = null;
        } else {
            lastUpdated = in.readLong();
        }
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Contact()
    {
        name=" ";
        phone=" ";
        gender=" ";
    }

    public Contact(String name, String phone, String gender)
    {
        this.gender=gender;
        this.name=name;
        this.phone=phone;
    }

    public Contact(Contact s)
    {
        this.gender=s.gender;
        this.name=s.name;
        this.phone=s.phone;
    }

    public void setLastUpdated(Long lastUpdated)
    {
        this.lastUpdated = lastUpdated;
    }

    public Long getLastUpdated()
    {
        return lastUpdated;
    }

    public Map<String,Object> toJson()
    {
        Map<String, Object> json = new HashMap<>();
        json.put("phone", getPhone());
        json.put("name", getName());
        json.put("gender", getGender());
        json.put(LAST_UPDATED, FieldValue.serverTimestamp());
        return json;
    }

    static Contact fromJson(Map<String, Object> json)
    {
        String phone=(String)json.get("phone");
        if(phone==null)
            return null;
        String name=(String)json.get("name");
        String gender=(String)json.get("gender");
        Contact contact = new Contact(name,phone,gender);
        Timestamp ts = (Timestamp)json.get(LAST_UPDATED);
        contact.setLastUpdated(new Long(ts.getSeconds()));
        return contact;
    }

    static long getLocalLastUpdated()
    {
        Long localLastUpdate = MyApplication.getAppContext().getSharedPreferences("TAG", Context.MODE_PRIVATE).getLong("STUDENTS_LAST_UPDATE", 0 );
        Log.d("TAG","localLastUpdate: " + localLastUpdate);
        return localLastUpdate;
    }

    static void setLocalLastUpdated(Long date)
    {
        SharedPreferences.Editor editor = MyApplication.getAppContext().getSharedPreferences("TAG", Context.MODE_PRIVATE).edit();
        editor.putLong("STUDENTS_LAST_UPDATE",date);
        editor.commit();
        Log.d("TAG", "new lud" + date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(phone);
        dest.writeString(name);
        dest.writeString(gender);
        if (lastUpdated == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(lastUpdated);
        }
    }
}
