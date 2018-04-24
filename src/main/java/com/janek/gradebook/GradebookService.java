package com.janek.gradebook;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

@Path("/")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class GradebookService {

    public final ArrayList<Student> students = TestModel.getStudents();
    public final ArrayList<Course> courses = TestModel.getCourseList();

    @GET
    @Path("/students")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public ArrayList<Student> getAllStudents() {
        return students;
    }

    @POST
    @Path("/students")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response addStudent(Student student) {
        student.setIndex();
        boolean exists = false;
        for(Student st : students) {
            if(st.getIndex() == student.getIndex())
                return addStudent(student);
                //exists = true;
        }
        students.add(student);
        return Response.status(Response.Status.CREATED).header("Location","/students/" + student.getIndex()).build();

    }

    @GET
    @Path("/students/{index}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Student getStudent(@PathParam("index") long index) {
        for(Student student : students) {
            if(student.getIndex() == index) {
                return student;
            }
        }
        throw new NotFoundException(new JsonError("Error", "Student " + String.valueOf(index) + " not found"));
    }

    @PUT
    @Path("/students/{index}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updateStudent(@PathParam("index") long index, Student student) {
        int matchId = 0;
        Optional<Student> match = students.stream()
                .filter(s -> s.getIndex() == index)
                .findFirst();
        if(match.isPresent()) {
            matchId = students.indexOf(match.get());
            student.setIndex(index);
            students.set(matchId,student);
            return Response.status(Response.Status.OK).build();
        } else {
            student.setIndex(index);
            students.add(student);
            return Response.status(Response.Status.CREATED).build();
        }
    }

    @DELETE
    @Path("/students/{index}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteStudent(@PathParam("index") long index) {
        Predicate<Student> studentPredicate = s -> s.getIndex() ==index;
        students.removeIf(studentPredicate);

        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("/students/{index}/grades")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public ArrayList<Grade> getStudentGrades(@PathParam("index") long index) {
        for(Student student : students) {
            if(student.getIndex() == index)
                return student.getGrades();
        }
        throw new NotFoundException(new JsonError("Error", "Student " + String.valueOf(index) + " not found"));
    }

    @POST
    @Path("students/{index}/grades")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response addGrade(@PathParam("index") long index, Grade grade) {
        for(Student student : students) {
            if(student.getIndex() == index) {
                ArrayList<Grade> studentGrades = student.getGrades();
                grade.setId(studentGrades.size()+1);
                studentGrades.add(grade);
                return Response.status(Response.Status.CREATED).build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/students/{index}/grades/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Grade getStudentGrade(@PathParam("index") long index, @PathParam("id") int id) {
        for (Student student : students) {
            if(student.getIndex() == index) {
                for (Grade grade : student.getGrades()) {
                    if(grade.getId() == id) {
                        return grade;
                    }
                }
            }
        }
        throw new NotFoundException(new JsonError("Error", "Student " + String.valueOf(index) + " not found"));
    }

    @PUT
    @Path("/students/{index}/grades/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updateStudentGrade(@PathParam("index") long index,@PathParam("id") int id, Grade grade) {
        for (Student student :students) {
            if (student.getIndex() == index) {
                if(student.getGrades() != null) {
                    int matchId = 0;
                    Optional<Grade> match = student.getGrades().stream()
                            .filter(s -> s.getId() == id)
                            .findFirst();
                    if(match.isPresent()) {
                        matchId = student.getGrades().indexOf(match.get());
                        grade.setId(id);
                        student.getGrades().set(matchId,grade);
                        return Response.status(Response.Status.OK).build();
                    } else {
                        student.getGrades().add(grade);
                        return Response.status(Response.Status.CREATED).build();
                    }
                }
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/students/{index}/grades/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteGrade(@PathParam("index") long index,@PathParam("id") int id) {
        Predicate<Grade> gradePredicate = g -> g.getId() ==id;
        for (Student student : students) {
            if( student.getIndex() == index) {
                if(student.getGrades() != null) {
                    student.getGrades().removeIf(gradePredicate);

                    return Response.status(Response.Status.OK).build();
                }
            }
        }
        return  Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/courses")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public ArrayList<Course> getAllCourses() {
        return courses;
    }

    @POST
    @Path("/courses")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response addCourse(Course course)  {
        courses.add(course);
        return Response.status(Response.Status.CREATED).build();
    }



    @GET
    @Path("/courses/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Course getCourse(@PathParam("id") int id) {
        for(Course course : courses) {
            if(course.getId() == id) {
                return course;
            }
        }
        return null;
    }

    @PUT
    @Path("/courses/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updateCourse(@PathParam("id") int id, Course course) {
        int matchId = 0;
        Optional<Course> match = courses.stream()
                .filter(s -> s.getId() == id)
                .findFirst();
        if(match.isPresent()) {
            matchId = courses.indexOf(match.get());
            course.setId(id);
            courses.set(matchId,course);
            return Response.status(Response.Status.OK).build();
        } else {
            courses.add(course);
            return Response.status(Response.Status.CREATED).build();
        }
    }

    @DELETE
    @Path("/courses/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteCourse(@PathParam("id") int id) {
        Predicate<Course> coursePredicate = c -> c.getId() == id;
        courses.removeIf(coursePredicate);

        return Response.status(Response.Status.OK).build();
    }

}
