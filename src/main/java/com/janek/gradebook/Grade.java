package com.janek.gradebook;

import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@XmlRootElement(name = "grade")
@XmlType(propOrder = {"id", "course", "value", "date"})
public class Grade {

    private int id;
    private static AtomicLong idCount = new AtomicLong();
    private float value;
    private Date date;
    private Course course;

    /*@InjectLinks({
            @InjectLink(value = "students/{index}/grades/{id}", rel = "self"),
            @InjectLink(value = "students/grades",rel = "parent"),
    })
    @XmlElement(name = "link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    private List<Link> links;*/

    public Grade() {
    }

    @XmlAttribute
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setId() {
        this.id = (int) idCount.getAndIncrement();
    }

    @XmlElement
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

    @XmlElement
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @XmlElement
    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }


}
