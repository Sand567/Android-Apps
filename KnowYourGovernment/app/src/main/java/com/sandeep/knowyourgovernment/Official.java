package com.sandeep.knowyourgovernment;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sandeep on 02-04-2017.
 */

public class Official {

    private String office;
    private String name;
    private String party;
    private String officeAddress;
    private String phone;
    private String emailaddress;
    private String website;
    private String facebookLink;
    private String twitterLink;
    private String googlePlusLink;
    private String youtubeLink;
    private String image;

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(office);
//        dest.writeString(name);
//        dest.writeString(party);
//        dest.writeString(officeAddress);
//        dest.writeString(phone);
//        dest.writeString(emailaddress);
//        dest.writeString(website);
//        dest.writeString(facebookLink);
//        dest.writeString(twitterLink);
//        dest.writeString(googlePlusLink);
//        dest.writeString(youtubeLink);
//        dest.writeString(image);
//    }
//
//    public static final Parcelable.Creator<Official> CREATOR
//            = new Parcelable.Creator<Official>() {
//        public Official createFromParcel(Parcel in) {
//            return new Official(in);
//        }
//
//        public Official[] newArray(int size) {
//            return new Official[size];
//        }
//    };
//
//    private Official(Parcel in) {
//        office = in.readString();
//        name = in.readString();
//        party = in.readString();
//        officeAddress = in.readString();
//        phone = in.readString();
//        emailaddress = in.readString();
//        website = in.readString();
//        facebookLink = in.readString();
//        twitterLink = in.readString();
//        googlePlusLink = in.readString();
//        youtubeLink = in.readString();
//        image = in.readString();
//    }



    public Official() {

    }

    public Official(String office, String name, String party, String officeAddress, String phone, String emailaddress, String website, String facebookLink, String twitterLink, String googlePlusLink, String youtubeLink, String image) {
        this.office = office;
        this.name = name;
        this.party = party;
        this.officeAddress = officeAddress;
        this.phone = phone;
        this.emailaddress = emailaddress;
        this.website = website;
        this.facebookLink = facebookLink;
        this.twitterLink = twitterLink;
        this.googlePlusLink = googlePlusLink;
        this.youtubeLink = youtubeLink;
        this.image = image;

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getOfficeAddress() {
        return officeAddress;
    }

    public void setOfficeAddress(String officeAddress) {
        this.officeAddress = officeAddress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmailaddress() {
        return emailaddress;
    }

    public void setEmailaddress(String emailaddress) {
        this.emailaddress = emailaddress;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getFacebookLink() {
        return facebookLink;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }

    public String getTwitterLink() {
        return twitterLink;
    }

    public void setTwitterLink(String twitterLink) {
        this.twitterLink = twitterLink;
    }

    public String getGooglePlusLink() {
        return googlePlusLink;
    }

    public void setGooglePlusLink(String googlePlusLink) {
        this.googlePlusLink = googlePlusLink;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public void setYoutubeLink(String youtubeLink) {
        this.youtubeLink = youtubeLink;
    }
}
