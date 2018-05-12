package com.janek.gradebook;

import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Embedded
@Entity("grades")
public class Grade {

//    @InjectLinks({@InjectLink(value = "/students/{studentIndex}/grades/{id}", rel = "self"), @InjectLink(value = "/students/{studentIndex}/grades", rel = "parent")})
//    @XmlElement(name = "link")
//    @XmlElementWrapper(name = "links")
//    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
//    List<Link> links;

    @XmlTransient
    private long studentIndex;

    @Id
    @XmlJavaTypeAdapter(ObjectIdJaxbAdapter.class)
    ObjectId _id;

    private int id;
    private static int idNumber = 0;
    private float value;
    private Date date;

    @Embedded
    private Course course;


    public Grade() {
    }

    public Grade(float value, Date date, Course course) {
        this.id = idNumber++;
        this.value = value;
        this.date = date;
        this.course = course;
    }

    public Grade(Grade grade) {
        this.id = idNumber++;
        this.studentIndex  = grade.getStudentIndex();
        this.value = grade.getValue();
        this.date = grade.getDate();
        this.course = grade.getCourse();
    }

    @XmlTransient
    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public long getStudentIndex() {
        return studentIndex;
    }

    public void setStudentIndex(long studentIndex) {
        this.studentIndex = studentIndex;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        if((value%0.5 == 0)) {
            if (value >= 2.0 && value <= 5.0)
                this.value = value;
            else {
                this.value = 2.0f;
            }
        } else {
            this.value = 2.0f;
        }
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }


}
