package com.janek.gradebook;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.mongodb.morphia.annotations.*;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Entity("student")
@XmlRootElement
@XmlType(propOrder = {"index", "firstName", "lastName", "birthday", "grades", "links"})
public class Student {

    @Id
    @XmlTransient
    private ObjectId _id;

    @Indexed(name = "index", unique = true)
    private long index;

    private String firstName;
    private String lastName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "CET")
    private Date birthday;

    private List<Grade> grades;

    @InjectLinks({
            @InjectLink(value = "/students/{index}", rel = "self"),
            @InjectLink(value = "/students",rel = "parent"),
            @InjectLink(value = "/students/{index}/grades", rel = "grades")
    })
    @XmlElement(name = "link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    private List<Link> links;

    public Student() {
        this.grades = new ArrayList<>();
    }

    public Student(String firstName, String lastName, Date birthday, ArrayList<Grade> grades) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.grades = grades;
    }

    public Student(Student student) {
        this.firstName = student.getFirstName();
        this.lastName = student.getLastName();
        this.birthday = student.getBirthday();
        this.grades = student.getGrades();
    }

    @XmlTransient
    public ObjectId getId() {
        return _id;
    }

    public void setId(ObjectId id) {
        this._id = id;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
        for(Grade g : grades) {
            g.setStudentIndex(index);
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @XmlElement(name = "grade")
    @XmlElementWrapper(name = "grades")
    @JsonProperty("grades")
    public List<Grade> getGrades() {
        return grades;
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }


    public void addGrade(Grade grade) {
        getGrades().add(grade);
    }

    public Grade getGradeById(int id) {
        Optional<Grade> grade = getGrades().stream().filter(c -> c.getId() == id).findAny();
        return grade.orElse(null);
    }

    public boolean updateStudentGrade(Grade grade) {
        int idx = getGrades().indexOf(getGradeById(grade.getId()));
        if (idx != -1) {
            getGrades().set(idx, grade);
            return true;
        } else
            return false;
    }

    public boolean removeStudentGradeById(int id) {
        System.out.println("Removing: " + id);
        return getGrades().remove(getGradeById(id));
    }

}
