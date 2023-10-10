package edu.school21.services;

import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassParser {

    public Map<String, String> GetClass(String fileJarName) {
        String appName = fileJarName;
        Map<String, String> classList = new HashMap<>();
        String packName = this.getClass().getPackage().getName();
        String packNameStart = packName.split("\\.")[0];
        String packNameEnd = ".class";
        String packNameStartReverse = ReverseStr(packNameStart);
        String packNameEndReverse = ReverseStr(packNameEnd);
        try {
            InputStream stream = getClass().getClassLoader().getResourceAsStream(appName);
            if(stream != null) {
                byte[] bytes = new byte[stream.available()];
                DataInputStream dataInputStream = new DataInputStream(stream);
                dataInputStream.readFully(bytes);
                bytes = ReverseArray(bytes);
                String dataFromFile = new String(bytes, StandardCharsets.UTF_8);
                stream.close();

                Pattern pattern = Pattern.compile(packNameEndReverse + "+/" + packNameStartReverse);
                Matcher matcher = pattern.matcher(dataFromFile);
                while (matcher.find()) {
                    String[] tmp = dataFromFile.substring(matcher.start(), matcher.end()).split(packNameStartReverse);
                    if (tmp[0].length() > 0 && !tmp[0].contains("�")) {
                        byte[] b = ReverseArray(tmp[0].getBytes());
                        String str = new String(b, StandardCharsets.UTF_8);
                        String[] tmpValue = str.split(packNameEnd);
                        String[] tmpName = str.split("/");
                        String[] tmpKey = tmpName[tmpName.length - 1].split(packNameEnd);
                        classList.put(tmpKey[0], packNameStart + tmpValue[0].replace('/', '.'));
                    }
                }
            }
        } catch (Throwable ex) {
            System.err.println("WARNING!!! Something wrong: " + ex);
            return null;
        }
        return classList;
    }

    public byte[] ReverseArray(byte[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            byte temp = array[i];
            array[i] = array[array.length - 1 - i];
            array[array.length - 1 - i] = temp;
        }
        return array;
    }

    public String ReverseStr(String str) {
        return new StringBuilder(str).reverse().toString();
    }

    public Object SelectObject(String className, Map<String, String> classesMap) {
        Object selectedObjct = null;
        try {
            selectedObjct = Class.forName(classesMap.get(className)).newInstance();
        } catch (NullPointerException | InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
            System.err.println("WARNING!!! Something wrong: " + ex);
            return null;
        }
        return selectedObjct;
    }

    public Class <?> SelectClass(Object selectedObjct) {
        try {
            return selectedObjct.getClass();
        } catch (NullPointerException ex) {
            System.err.println("WARNING!!! Something wrong: " + ex);
            return null;
        }
    }
    
    public Method[] GetMethods(Class<?> selectedClss) {
        return selectedClss.getDeclaredMethods();
    }

    public Field[] GetFields(Class<?> selectedClss) {
        return selectedClss.getDeclaredFields();
    } 
    
    public void CreateNewObj(Scanner in, Class<?> selectedClss, Object selectedObjct)  {
        Field[] fields = selectedClss.getDeclaredFields();
        for (Field field : fields) {
            System.out.println(field.getName() + ":");
            String newValue = in.nextLine();
            field.setAccessible(true);
            try {
                field.set(selectedObjct, parseValue(newValue, field.getType().toString()));
            } catch (IllegalAccessException ex) {
                System.err.println("WARNING!!! Something wrong: " + ex);
            } catch (NumberFormatException ex) {
                System.err.println("WARNING!!! Invalid data format entered, default data will be used! (Errore: " + ex + ")");
            }
        }
    }

    public void UpdateObj(Scanner in, Class<?> selectedClss, Object selectedObjct) {
        String selectedField = in.nextLine();
        Field[] fields = selectedClss.getDeclaredFields();
        for (Field field : fields) {
            if(field.getName().equals(selectedField)) {
                System.out.print("Enter ");
                System.out.print(field.getType().getSimpleName());
                System.out.println(" value:");
                String newValue = in.nextLine();
                field.setAccessible(true);
                try {
                    field.set(selectedObjct, parseValue(newValue, field.getType().toString()));
                } catch (IllegalAccessException ex) {
                    System.err.println("WARNING!!! " + ex);
                } catch (NumberFormatException ex) {
                    System.err.println("WARNING!!! Invalid data format entered, default data will be used! (Errore: " + ex + ")");
                }
                return;
            }
        }
        System.out.println("WARNING!!! No matches found for this field name!!)");
    }

    public void ChangeObjByMethod(Scanner in, Class<?> selectedClss, Object selectedObjct) {
        String inTmp = in.nextLine();
        if(inTmp.isEmpty()) {
            return;
        }
        String[] inSplit = inTmp.split("\\(");
        String selected = inSplit[0];

        if(inSplit.length < 2) {
            System.out.println("WARNING!!! No such method found!!");
            return;
        }
        String[] inValueTmp = inSplit[1].split("\\)");
        String[] selectedValue = inValueTmp[0].split(", ");

        Method[] methods = selectedClss.getDeclaredMethods();
        List<Object> paramsObj = new ArrayList<>();
        for (Method method : methods) {
            if(method.getName().equals(selected)) {
                Parameter[] parameters = method.getParameters();
                if(selectedValue.length != parameters.length) {
                    System.out.println("WARNING!! Parameters do not match!!!");
                    break;
                }
                int i = 0;
                for (Parameter parameter : parameters) {
                    String parameterType = parameter.getType().getSimpleName();
                    if(selectedValue[i++] != parameterType) {
                        System.out.println("WARNING!! Parameters do not match!!!");
                        break;
                    }
                }
                for (Parameter parameter : parameters) {
                    String parameterType = parameter.getType().getSimpleName();
                    System.out.print("Enter ");
                    System.out.print(parameterType);
                    System.out.println(" value:");
                    String newValue = in.nextLine();
                    paramsObj.add(parseValue(newValue, parameterType));
                }
                try {
                    Object resTmp = method.invoke(selectedObjct, paramsObj.toArray());
                    if(!method.getReturnType().getSimpleName().equals("void")) {
                        System.out.println("Method returned:");
                        System.out.println(resTmp);
                    }
                } catch (IllegalAccessException | InvocationTargetException ex) {
                    System.err.println("Something wrong: " + ex);
                }
            }
        }
    }

    private Object parseValue(String value, String type) {
        Object result;
        switch (type) {
            case "short":
                result = Short.valueOf(value);
                break;
            case "int":
                result = Integer.valueOf(value);
                break;
            case "double":
                result = Double.valueOf(value);
                break;
            case "float":
                result = Float.valueOf(value);
                break;
            case "long":
                result = Long.valueOf(value);
                break;
            case "boolean":
                result = Boolean.valueOf(value);
                break;
            case "byte":
                result = Byte.valueOf(value);
                break;
            case "char":
                result = value.toCharArray()[0];
                break;
            default:
                result = value;
                break;
        }
        return result;
    }

    public void PrintClass(Map<String, String> listClass) {
        for (Map.Entry<String, String> entry : listClass.entrySet()) {
            System.out.println(" - " + entry.getKey());
        }
    }

    public void PrintMethods(Method[] methods) {
        System.out.println("methods:");
        for (Method method : methods) {
            System.out.print("\t" + method.getReturnType().getSimpleName());
            System.out.print(" ");
            System.out.print(method.getName());
            System.out.print("(");
            Parameter[] parameters = method.getParameters();
            int i = 0;
            for (Parameter parameter : parameters) {
                if(i++ > 0) System.out.print(", ");
                System.out.print(parameter.getType().getSimpleName());
            }
            System.out.println(")");
        }
    }
    
    public void PrintFields(Field[] fields) {
        System.out.println("fields:");
        for (Field field : fields) {
            System.out.print("\t" + field.getType().getSimpleName());
            System.out.print(" ");
            System.out.println(field.getName());
        }
    }

}
