package referTooltest;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

public class ShowMethods {
    private static String usage = """
            usage:
            ShowMethods qualified.class.name
            To show all methods in class or:
            ShowMethods qualified.class.name word
            To search for methods involving 'word'
            """;
    //from "java.lang.Object.wait(long,int)" remove "java.lang.Object."
    private static Pattern p = Pattern.compile("\\w+\\.");

    public static void Invoke_Method(Class<?> c,Method[] methods, String methodName, Object[] MethodArgs){

        try{
            Object tar_obj = c.getConstructor().newInstance();
            for (Method method : methods) {
                if (methodName.equals(method.getName())) {
                    method.invoke(tar_obj, MethodArgs);
                }
            }
        }catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e){
            e.printStackTrace();
        }
    }

    public static Class<?>[] getParametersClass(Method[] methods, String methodName){
        List<Class<?>> paramterList = new ArrayList<>();
        for (Method method : methods) {
            if (methodName.equals(method.getName())) {
                // 直接通过method就能拿到所有的参数
                Parameter[] params = method.getParameters();
                for (Parameter parameter : params) {
                    System.out.println("parameter information: " + parameter);
                    paramterList.add(parameter.getType());
                }
            }
        }

        Class<?>[] tar_Method_args = paramterList.toArray(new Class<?>[paramterList.size()]);
        return tar_Method_args;
    }

    public void testMethod(String s){
        System.out.println(s);
    }

    public static void main(String[] args){
        if(args.length < 1) {
                System.out.println(usage);
        System.exit(9);
        }
        int lines = 0; // The number of methods
        try{
            Class<?> c = Class.forName(args[0]);
            Method[] methods = c.getMethods();
            Constructor[] constructors = c.getConstructors();

            if(args.length == 1){
                for(Method method: methods)
                    System.out.println("method: " + p.matcher(method.toString()).replaceAll(""));
                for(Constructor ctor : constructors)
                    System.out.println("Constructor: " + p.matcher(ctor.toString()).replaceAll(""));
                lines = methods.length + constructors.length;

                // 获取指定的方法的参数列表的class
                getParametersClass(methods, "getParametersClass");

                // 通过方法名和参数调用指定的方法
                Object[] MethodArgs = {"start the test_Method"};
                Invoke_Method(c, methods, "testMethod", MethodArgs);
                System.out.println("lines: " + lines);
            }
            else {
                for(Method method : methods)
                    if(method.toString().contains(args[1])) {
                        System.out.println(p.matcher(method.toString()).replaceAll(""));
                        lines++;
                    }

                for(Constructor ctor : constructors)
                    if(ctor.toString().contains(args[1])) {
                        System.out.println(p.matcher(ctor.toString()).replaceAll(""));
                        lines++;
                    }

                System.out.println("lines: " + lines);
            }
        }catch (ClassNotFoundException e){
            System.out.println("No such class: " + e);
        }
    }
}
