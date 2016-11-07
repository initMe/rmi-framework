package test.proxy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class TestClassloader {
	
	public TestClassloader() throws Exception {
		 String className = "Test"; 

        File file = new File(new File(System.getProperty("user.dir")), 
                className + ".java"); 

        StringBuffer sb = new StringBuffer(); 
        sb.append("public class " + className + " { \r\n"); 
        sb.append("     public static void main(java.lang.String[] args) throws Exception { \r\n"); 
        sb.append("         System.out.println(\"test 111.2222 "); 
        sb.append("             class: \" + " + className + ".class.newInstance());     \r\n"); 
        sb.append("     }   \r\n"); 
        sb.append("}    \r\n"); 
        
        FileOutputStream ous = new FileOutputStream(file); 
        ous.write(sb.toString().getBytes()); 
        ous.close(); 

        try {
        	String arguments =  "javac " + className + ".java" ; 
			Process p = Runtime.getRuntime().exec(arguments, null, new File(System.getProperty("user.dir")));
			p.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        URL classpath = new URL("file:/" + System.getProperty("user.dir") + "/"); 
        URLClassLoader classLoader = new URLClassLoader(new URL[] { classpath }); 
        Class testClass = classLoader.loadClass(className); 

        Method mainMethod = testClass.getMethod("main", 
                new Class[] { String[].class }); 

        mainMethod.invoke(null, new Object[] { null }); 
	}
	
	public static void main(String[] args) {
		try {
			new TestClassloader();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
