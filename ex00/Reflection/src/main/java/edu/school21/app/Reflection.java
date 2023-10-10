package edu.school21.app;

import edu.school21.services.ClassParser;

import java.lang.reflect.*;
import java.util.*;

public class Reflection {

    public static void main(String[] args) { 
        
        String fileJarName   = "Reflection-1.0-SNAPSHOT.jar";
        Object selectedObjct = null;
        Class<?> selectedClss;
        Map<String, String> classesMap;

        ClassParser classParser = new ClassParser();

        System.out.println("Classes:");
        classesMap = classParser.GetClass(fileJarName);
        classParser.PrintClass(classesMap);
        System.out.println("---------------------");

        Scanner in = new Scanner(System.in);
        
        System.out.println("Enter class name:");
        String className = in.nextLine();
        if(className.isEmpty()) {
            System.out.println("WARNING!!! Field can't be empty!!");
            System.exit(-1);
        }

        selectedObjct = classParser.SelectObject(className, classesMap);
        if(selectedObjct == null) {
            System.out.println("WARNING!!! Such class not found!!");
            System.exit(-1);
        }
        
        selectedClss = classParser.SelectClass(selectedObjct);         
        System.out.println("---------------------");

        Field[]  fields  = classParser.GetFields(selectedClss);
        Method[] methods = classParser.GetMethods(selectedClss);
        classParser.PrintFields(fields);
        classParser.PrintMethods(methods);
        System.out.println("---------------------");

        System.out.println("Let's create an object.");
        classParser.CreateNewObj(in, selectedClss, selectedObjct);
        System.out.println("Object created: " + selectedObjct);
        System.out.println("---------------------");
        
        System.out.println("Enter name of the field for changing:");
        classParser.UpdateObj(in, selectedClss, selectedObjct);
        System.out.println("Object updated: " + selectedObjct);
        System.out.println("---------------------");

        System.out.println("Enter name of the method for call:");
        classParser.ChangeObjByMethod(in, selectedClss, selectedObjct);

    }

}
