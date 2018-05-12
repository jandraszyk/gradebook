package com.janek.gradebook;

import org.mongodb.morphia.annotations.Entity;

@Entity("ids")
public class IdGenerator {

    private long studentId;
    private int courseId;
    private int gradeId;

    public IdGenerator() {

    }

    public IdGenerator(int studentId, int courseId, int gradeId) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.gradeId = gradeId;
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getGradeId() {
        return gradeId;
    }

    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
    }

    @Override
    public String toString() {
        return "IdGenerator{" + "studentId=" + studentId + ", courseId=" + courseId + ", gradeId=" + gradeId + '}';
    }
}
