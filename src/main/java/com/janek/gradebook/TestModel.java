/*
package com.janek.gradebook;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

public class TestModel {

    private static ArrayList<Student> students = new ArrayList<>();
    private static ArrayList<Course> courseList = new ArrayList<>();
    private static final AtomicLong gradeIdCounter = new AtomicLong();
    private static final AtomicLong studentIdCounter = new AtomicLong();
    private static final AtomicLong courseIdCounter = new AtomicLong();

    static {

        */
/**
         *  Setup courses
         *//*

        Course course1 = addCourse((int)courseIdCounter.getAndIncrement(),"Marcin Szelag","ZWiWO");
        courseList.add(course1);
        Course course2 = addCourse((int) courseIdCounter.getAndIncrement(),"Andrzej Golota", "Boxing");
        courseList.add(course2);
        Databasemodel.getDatastore().save(courseList);
        */
/**
         * Setup grades - student1
         *//*

        ArrayList<Grade> gradesStudent1 = new ArrayList<>();
        Grade grade1Student1 = addGrade(4.0f,course1,new Date("2018/10/14"),(int) gradeIdCounter.getAndIncrement());
        gradesStudent1.add(grade1Student1);
        Grade grade2Student1 = addGrade(3.5f, course2,new Date("2018/08/14"),(int)gradeIdCounter.getAndIncrement());
        gradesStudent1.add(grade2Student1);

        Student student1 = addStudent("Jan", "Nowak", new Date("2011/6/17"),studentIdCounter.getAndIncrement(),gradesStudent1);
        students.add(student1);

        //Setup grades - student2
        ArrayList<Grade> gradesStudent2 = new ArrayList<>();
        Grade grade1Student2 = addGrade(2.0f,course1,new Date("2018/11/14"),(int) gradeIdCounter.getAndIncrement());
        gradesStudent2.add(grade1Student2);

        Grade grade2Student2 = addGrade(4.5f,course2, new Date("2018/10/23"),(int) gradeIdCounter.getAndIncrement());
        gradesStudent2.add(grade2Student2);

        Grade grade3Student2 = addGrade(3.5f,course2,new Date("2018/08/12"),(int) gradeIdCounter.getAndIncrement());
        gradesStudent2.add(grade3Student2);

        Student student2 = addStudent("Stefan", "Kowalski",new Date("1995/04/26"),(int) studentIdCounter.getAndIncrement(),gradesStudent2);
        students.add(student2);

        Databasemodel.getDatastore().save(students);
    }

    private TestModel() {

    }

    public static ArrayList<Student> getStudents() {
        return  students;
    }

    public static ArrayList<Course> getCourseList() {
        return courseList;
    }

    public static Course addCourse(int id, String lecturer, String name) {
        Course course = new Course();
        course.setId(id);
        course.setLecturer(lecturer);
        course.setName(name);

        return course;
    }

    public static Grade addGrade(float value, Course course, Date date, int id) {
        Grade grade = new Grade();
        grade.setCourse(course);
        grade.setDate(date);
        grade.setId(id);
        grade.setValue(value);

        return grade;
    }

    public static Student addStudent(String firstName, String lastName, Date birthdate, long index, ArrayList<Grade> grades) {
        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setBirthday(birthdate);
        student.setIndex(index);
        student.setGrades(grades);

        return student;
    }
}
*/
