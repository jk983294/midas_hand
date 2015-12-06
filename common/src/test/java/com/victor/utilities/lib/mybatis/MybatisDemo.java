package com.victor.utilities.lib.mybatis;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class MybatisDemo {

    public static void main(String args[]) throws IOException{

        Reader reader = Resources.getResourceAsReader("SqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        SqlSession session = sqlSessionFactory.openSession();

//        {
//            //select a particular student using id
//            Student student = session.selectOne("Student.getById", 1);
//            System.out.println("Current details of the student are" );
//            System.out.println(student.toString());
//
//            //Set new values to the mail and phone number of the student
//            student.setEmail("mohamad123@yahoo.com");
//            student.setPhone(90000000);
//
//            //Update the student record
//            session.update("Student.update",student);
//            System.out.println("Record updated successfully");
//            session.commit();
//
//            //verifying the record
//            Student std = (Student) session.selectOne("Student.getById", 1);
//            System.out.println("Details of the student after update operation" );
//            System.out.println(std.toString());
//            session.commit();
//            session.close();
//        }

        {
            //select contact all contacts
            List<Student> student = session.selectList("Student.getAll");
            for(Student st : student ){
                System.out.println(st.getId());
                System.out.println(st.getName());
                System.out.println(st.getBranch());
                System.out.println(st.getPercentage());
                System.out.println(st.getEmail());
                System.out.println(st.getPhone());
            }
            System.out.println("Records Read Successfully ");
            session.commit();
            session.close();
        }

//        {
//            //Create a new student object
//            Student student = new Student("Mohammad", "It", 80, 984803322, "Mohammad@gmail.com");
//
//            //Insert student data
//            session.insert("Student.insert", student);
//            System.out.println("record inserted successfully");
//            session.commit();
//            session.close();
//        }


    }



}
