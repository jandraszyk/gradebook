package com.janek.gradebook;

import jersey.repackaged.com.google.common.collect.Lists;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Path("/")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class GradebookService {



    @GET
    @Path("/students")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getAllStudents(@QueryParam("firstName") String firstName,
                                   @QueryParam("lastName") String lastName,
                                   @QueryParam("birthday") Date birthday,
                                   @QueryParam("dateRelation") String dateRelation) {

        Query<Student> studentQuery = Databasemodel.getDatastore().createQuery(Student.class);
        if(firstName != null) {
            studentQuery.field("firstName").containsIgnoreCase(firstName);
        }
        if(lastName != null) {
            studentQuery.field("lastName").containsIgnoreCase(lastName);
        }
        if(birthday != null && dateRelation == null) {
            studentQuery.field("birthday").equal(birthday);
        }

        if(birthday != null && dateRelation !=null) {
            switch (dateRelation.toLowerCase()) {
                case "equal":
                    studentQuery.field("birthday").equal(birthday);
                    break;
                case "after":
                    studentQuery.field("birthday").greaterThan(birthday);
                    break;
                case "before":
                    studentQuery.field("birthday").lessThan(birthday);
                    break;
            }
        }

        List<Student> students = studentQuery.asList();
        if (students == null || students.size() == 0) {
            return Response.status(Response.Status.NOT_FOUND).entity("No students").build();
        }

        GenericEntity<List<Student>> entity = new GenericEntity<List<Student>>(Lists.newArrayList(students)) {};
        return Response.status(Response.Status.OK).entity(entity).build();
    }

    @POST
    @Path("/students")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response addStudent(Student student) {
        if (student != null) {
            Course course;
            for (Grade grade : student.getGrades()) {
                course = Databasemodel.getDatastore().createQuery(Course.class).field("name").equal(grade.getCourse().getName())
                        .field("lecturer").equal(grade.getCourse().getLecturer()).get();
                if (course ==null) {
                    return Response.status(Response.Status.NOT_FOUND).entity("Course not found").build();
                }
            }
            long id = 0;
            Query<IdGenerator> query = Databasemodel.getDatastore().find(IdGenerator.class);
            if(query.countAll() == 0) {
                Databasemodel.getDatastore().save(new IdGenerator(0,0,0));
            } else {
                long newStudentIndex = query.get().getStudentId() + 1;
                UpdateOperations<IdGenerator> updateOperations = Databasemodel.getDatastore().createUpdateOperations(IdGenerator.class).set("studentId", newStudentIndex);
                Databasemodel.getDatastore().findAndModify(query, updateOperations);
                id = newStudentIndex;
            }
            long newId = id;
            student.setIndex(newId);
            Databasemodel.getDatastore().save(student);
            return Response.status(Response.Status.CREATED).header("Location","/students/" + student.getIndex()).entity("Student " + student + " added\n").build();
        }
        return Response.status(Response.Status.NO_CONTENT).entity("Specify the student").build();
    }

    @GET
    @Path("/students/{index}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getStudent(@PathParam("index") long index) {
        Student student = Databasemodel.getDatastore().createQuery(Student.class).field("index").equal(index).get();

        if(student == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Student not found").build();
        }
        return Response.status(Response.Status.OK).entity(student).build();
    }

    @PUT
    @Path("/students/{index}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updateStudent(@PathParam("index") long index, Student student) {

        Student searchedStudent = Databasemodel.getDatastore().createQuery(Student.class).field("index").equal(index).get();

        if(searchedStudent == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Student not found").build();
        }

        Course course;
        for (Grade grade : student.getGrades()) {
            course = Databasemodel.getDatastore().createQuery(Course.class).field("name").equal(grade.getCourse().getName())
                    .field("lecturer").equal(grade.getCourse().getLecturer()).get();
            if (course ==null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Course not found").build();
            }
        }

        Query<Student> studentUpdate = Databasemodel.getDatastore().createQuery(Student.class).field("index").equal(index);
        UpdateOperations<Student> studentUpdateOperations = Databasemodel.getDatastore().createUpdateOperations(Student.class)
                .set("firstName", student.getFirstName())
                .set("lastName", student.getLastName())
                .set("birthday", student.getBirthday());
        if(student.getGrades() != null && !student.getGrades().isEmpty()) {
            studentUpdateOperations.set("grades", student.getGrades());
        }
        Databasemodel.getDatastore().update(studentUpdate, studentUpdateOperations);
        return Response.status(Response.Status.OK).entity("Student " + student + " was updated").build();

        }

    @DELETE
    @Path("/students/{index}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteStudent(@PathParam("index") long index) {

        Student searchedStudent = Databasemodel.getDatastore().createQuery(Student.class).field("index").equal(index).get();
        if(searchedStudent == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Student not found").build();
        }

        Databasemodel.getDatastore().delete(Databasemodel.getDatastore().createQuery(Student.class).field("index").equal(index).get());
        if (Databasemodel.getDatastore().createQuery(Student.class).field("index").equal(index).get() == null) {
            return Response.status(Response.Status.OK).entity("Student "  + searchedStudent + " was deleted").build();
        } else {
            return Response.status(Response.Status.CONFLICT).entity("Something went wrong! Not deleted").build();
        }

    }


    @GET
    @Path("/students/{index}/grades")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getStudentGrades(@PathParam("index") long index,
                                     @QueryParam("courseName") String courseName,
                                     @QueryParam("value") String value,
                                     @QueryParam("valueRelation") String valueRelation) {

        Student searchedStudent = Databasemodel.getDatastore().createQuery(Student.class).field("index").equal(index).get();

        if(searchedStudent == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Student not found").build();
        }

        List<Grade> grades = searchedStudent.getGrades();
        //List<Grade> grades = gradeQuery.asList();
        if(grades == null || grades.size() == 0) {
            return Response.status(Response.Status.NOT_FOUND).entity("Grades not found").build();
        }

        if (courseName != null) {
            grades = grades.stream().filter(gr -> gr.getCourse().getName().equals(courseName)).collect(Collectors.toList());
        }

        // filtering by grade's value
        if (value != null && valueRelation != null) {
            switch (valueRelation.toLowerCase()) {
                case "greater":
                    grades = grades.stream().filter(gr -> gr.getValue() > Float.valueOf(value).floatValue()).collect(Collectors.toList());
                    break;
                case "lower":
                    grades = grades.stream().filter(gr -> gr.getValue() < Float.valueOf(value).floatValue()).collect(Collectors.toList());
                    break;
            }
        }

        GenericEntity<List<Grade>> entity = new GenericEntity<List<Grade>>(Lists.newArrayList(grades)) {};

        return Response.status(Response.Status.OK).entity(entity).build();
    }

    @POST
    @Path("students/{index}/grades")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response addGrade(@PathParam("index") long index, Grade grade) {

        if(grade != null) {
            Student searchedStudent = Databasemodel.getDatastore().createQuery(Student.class).field("index").equal(index).get();

            if(searchedStudent == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Student not found").build();
            }

            //Course course = Databasemodel.getDatastore().createQuery(Course.class).field("name").equal(grade.getCourse().getName())
            //        .field("lecturer").equal(grade.getCourse().getLecturer()).get();
            Course course = Databasemodel.getDatastore().createQuery(Course.class).field("id").equal(grade.getCourse().getId()).get();
            if (course ==null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Course not found").build();
            }

            int id = 0;
            Query<IdGenerator> query = Databasemodel.getDatastore().find(IdGenerator.class);
            if(query.countAll() == 0) {
                Databasemodel.getDatastore().save(new IdGenerator(0,0,0));
            } else {
                int newGradeIndex = query.get().getGradeId() + 1;
                UpdateOperations<IdGenerator> updateOperations = Databasemodel.getDatastore().createUpdateOperations(IdGenerator.class).set("gradeId", newGradeIndex);
                Databasemodel.getDatastore().findAndModify(query, updateOperations);
                id = newGradeIndex;
            }
                grade.setId(id);
                grade.setValue(grade.getValue());
                grade.setStudentIndex(searchedStudent.getIndex());
                grade.setCourse(course);
                searchedStudent.addGrade(grade);

                Query<Student> studentUpdate = Databasemodel.getDatastore().createQuery(Student.class).field("index").equal(searchedStudent.getIndex());
                UpdateOperations<Student> studentUpdateOperations = Databasemodel.getDatastore().createUpdateOperations(Student.class)
                        .set("firstName", searchedStudent.getFirstName())
                        .set("lastName", searchedStudent.getLastName())
                        .set("birthday", searchedStudent.getBirthday());
                if (searchedStudent.getGrades() != null && !searchedStudent.getGrades().isEmpty()) {
                    studentUpdateOperations.set("grades", searchedStudent.getGrades());
                }
            Databasemodel.getDatastore().update(studentUpdate, studentUpdateOperations);
                return Response.status(Response.Status.CREATED).header("Location","students/"+ searchedStudent.getIndex()+"/grades/"+grade.getId()).entity("Grade " + grade + " was added\n").build();
        } else {
            return Response.status(Response.Status.NO_CONTENT).entity("Specify the grade").build();
        }

    }

    @GET
    @Path("/students/{index}/grades/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getStudentGrade(@PathParam("index") long index, @PathParam("id") int id) {

        Student searchedStudent = Databasemodel.getDatastore().createQuery(Student.class).field("index").equal(index).get();

        if(searchedStudent == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Student not found").build();
        }

        Grade searchedGrade = searchedStudent.getGradeById(id);
        if(searchedGrade == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Grade not found").build();
        }

        return Response.status(Response.Status.OK).entity(searchedGrade).build();
    }

    @PUT
    @Path("/students/{index}/grades/{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updateStudentGrade(@PathParam("index") long index,@PathParam("id") int id, Grade grade) {

        if(grade != null) {
            Student searchedStudent = Databasemodel.getDatastore().createQuery(Student.class).field("index").equal(index).get();

            if(searchedStudent == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Student not found").build();
            }

            Grade searchedGrade = searchedStudent.getGradeById(id);
            if(searchedGrade == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Grade not found").build();
            }

            Course course = Databasemodel.getDatastore().createQuery(Course.class).field("id").equal(grade.getCourse().getId()).get();
                    /*.field("lecturer").equal(grade.getCourse().getLecturer()).get();*/
            if (course ==null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Course not found").build();
            }

            course.setId(grade.getCourse().getId());
            grade.setCourse(course);
            grade.setId(id);
            grade.setStudentIndex(searchedStudent.getIndex());
            searchedStudent.updateStudentGrade(grade);
            Query<Student> studentUpdate = Databasemodel.getDatastore().createQuery(Student.class).field("index").equal(searchedStudent.getIndex());
            UpdateOperations<Student> studentUpdateOperations = Databasemodel.getDatastore().createUpdateOperations(Student.class)
                    .set("firstName", searchedStudent.getFirstName())
                    .set("lastName", searchedStudent.getLastName())
                    .set("birthday", searchedStudent.getBirthday());
            if (searchedStudent.getGrades() != null && !searchedStudent.getGrades().isEmpty()) {
                studentUpdateOperations.set("grades", searchedStudent.getGrades());
            }
            Databasemodel.getDatastore().update(studentUpdate, studentUpdateOperations);
            return Response.status(Response.Status.CREATED).entity("Grade " + grade + " was updated").build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Specify grade").build();
        }
    }

    @DELETE
    @Path("/students/{index}/grades/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteGrade(@PathParam("index") long index,@PathParam("id") int id) {

        Student searchedStudent = Databasemodel.getDatastore().createQuery(Student.class).field("index").equal(index).get();

        if(searchedStudent == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Student not found").build();
        }

        Grade searchedGrade = searchedStudent.getGradeById(id);
        if(searchedGrade == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Grade not found").build();
        }

        searchedStudent.removeStudentGradeById(id);
        Query<Student> studentUpdate = Databasemodel.getDatastore().createQuery(Student.class).field("index").equal(searchedStudent.getIndex());
        UpdateOperations<Student> studentUpdateOperations = Databasemodel.getDatastore().createUpdateOperations(Student.class)
                .set("firstName", searchedStudent.getFirstName())
                .set("lastName", searchedStudent.getLastName())
                .set("birthday", searchedStudent.getBirthday());
        if (searchedStudent.getGrades() != null && !searchedStudent.getGrades().isEmpty()) {
            studentUpdateOperations.set("grades", searchedStudent.getGrades());
        }
        Databasemodel.getDatastore().update(studentUpdate, studentUpdateOperations);
        return Response.status(Response.Status.OK).entity("Grade " + searchedGrade + " was deleted").build();


    }

    @GET
    @Path("/courses")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getAllCourses(@QueryParam("lecturer") String lecturer) {
        Query<Course> courseQuery = Databasemodel.getDatastore().createQuery(Course.class);

        if(lecturer != null) {
            courseQuery.field("lecturer").containsIgnoreCase(lecturer);
        }

        List<Course> courses = courseQuery.asList();

        if(courses == null || courses.size() == 0) {
            return Response.status(Response.Status.NOT_FOUND).entity("No courses").build();
        }
        GenericEntity<List<Course>> entity = new GenericEntity<List<Course>>(Lists.newArrayList(courses)) {};
        return Response.status(Response.Status.OK).entity(entity).build();
    }

    @POST
    @Path("/courses")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response addCourse(Course course) {
        if (course != null) {
            int id = 0;
            Query<IdGenerator> query = Databasemodel.getDatastore().find(IdGenerator.class);
            if (query.countAll() == 0) {
                Databasemodel.getDatastore().save(new IdGenerator(0, 0, 0));
            } else {
                int newCourseId = query.get().getCourseId() + 1;
                UpdateOperations<IdGenerator> updateOperations = Databasemodel.getDatastore().createUpdateOperations(IdGenerator.class).set("courseId", newCourseId);
                Databasemodel.getDatastore().findAndModify(query, updateOperations);
                id = newCourseId;
            }
            int newId = id;
            course.setId(newId);
            Databasemodel.getDatastore().save(course);
            return Response.status(Response.Status.CREATED).header("Location", "/courses/" + course.getId()).entity("Course " + course + " was added").build();
        } else {
            return Response.status(Response.Status.NO_CONTENT).entity("Specify the course").build();
        }


    }



    @GET
    @Path("/courses/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getCourse(@PathParam("id") int id) {
        Course course = Databasemodel.getDatastore().createQuery(Course.class).field("id").equal(id).get();

        if(course == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Course not found").build();
        }
        return Response.status(Response.Status.OK).entity(course).build();
    }

    @PUT
    @Path("/courses/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updateCourse(@PathParam("id") int id, Course course) {
        Course searchedCourse = Databasemodel.getDatastore().createQuery(Course.class).field("id").equal(id).get();

        if(searchedCourse == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Course not found").build();
        }

        course.setId(id);
        Query<Course> courseToUpdate = Databasemodel.getDatastore().createQuery(Course.class).field("id").equal(course.getId());
        UpdateOperations<Course> courseUpdateOperations = Databasemodel.getDatastore().createUpdateOperations(Course.class)
                .set("name", course.getName())
                .set("lecturer", course.getLecturer());
        Databasemodel.getDatastore().update(courseToUpdate,courseUpdateOperations);
        return Response.status(Response.Status.OK).entity("Course " + course + " was updated").build();
    }

    @DELETE
    @Path("/courses/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteCourse(@PathParam("id") int id) {
        Course searchedCourse = Databasemodel.getDatastore().createQuery(Course.class).field("id").equal(id).get();

        if(searchedCourse == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Course not found").build();
        }

        Query<Student> studentQuery = Databasemodel.getDatastore().createQuery(Student.class);
        List<Student> students = studentQuery.asList();

        if(students != null && !students.isEmpty()) {
            for (Student student : students) {
                for (int i = 0; i<student.getGrades().size(); i++) {
                    if(student.getGrades().get(i).getCourse().getId() == id) {
                        student.removeStudentGradeById(student.getGrades().get(i).getId());
                    }
                }
                Query<Student> studentUpdate = Databasemodel.getDatastore().createQuery(Student.class).field("index").equal(student.getIndex());
                UpdateOperations<Student> studentUpdateOperations = Databasemodel.getDatastore().createUpdateOperations(Student.class)
                        .set("firstName", student.getFirstName())
                        .set("lastName", student.getLastName())
                        .set("birthday", student.getBirthday())
                        .set("grades", student.getGrades());
                Databasemodel.getDatastore().update(studentUpdate, studentUpdateOperations);
            }
        }
        Databasemodel.getDatastore().delete(Databasemodel.getDatastore().createQuery(Course.class).field("id").equal(id).get());
        if(Databasemodel.getDatastore().createQuery(Course.class).field("id").equal(id).get() == null) {
            return Response.status(Response.Status.OK).entity("Course " + searchedCourse + " was deleted").build();
        } else {
            return Response.status(Response.Status.CONFLICT).entity("Something went wrong! Not deleted").build();
        }
    }

}
