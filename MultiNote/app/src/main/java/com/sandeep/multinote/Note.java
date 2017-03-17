package com.sandeep.multinote;

/**
 * Created by Sandeep on 15-02-2017.
 */

public class Note {

    private String dateAndTime;
    private String notes;
    private String title;

    public String getDateAndTime() { return dateAndTime; }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Note{" +
                "dateAndTime='" + dateAndTime + '\'' +
                ", notes='" + notes + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
