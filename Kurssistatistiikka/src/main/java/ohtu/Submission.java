package ohtu;

import java.util.List;

public class Submission {
    private int week;
    private int hours;
    private String course;
    private List<Integer> exercises;

    public void setWeek(int week) {
        this.week = week;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }
    
    public void setCourse(String course) {
        this.course = course;
    }
    
    public void setExercises(List<Integer> exercises) {
        this.exercises = exercises;
    }

    public int getWeek() {
        return week;
    }
    
    public int getHours() {
        return hours;
    }
    
    public String getCourse() {
        return course;
    }
    
    public List<Integer> getExercises() {
        return exercises;
    }
    
}