import java.io.*;

public class IOtools {
    public static String testFileName;
    public static String outputName;
    public static File testFile;
    public static PushbackInputStream reader;
    public static FileWriter writer;
    public static int alpha;
    public static char alphabet;
    public static int lineNum = 1;
    public static int getAlpha() {
        try {
            alpha = reader.read();
            alphabet = (char) alpha;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return alpha;
    }

    public static void ungetAlpha() {
        try {
            reader.unread(alpha);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(String string) {
        try {
            writer.write(string);
            writer.write('\n');
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initIO(){
        try {
            //testFileName = new String("D:\\temporary\\编译实验\\语法分析\\Compiler\\testfile.txt");
            //outputName = new String("D:\\temporary\\编译实验\\语法分析\\Compiler\\output.txt");
            testFileName = new String("testfile.txt");
            outputName = new String("output.txt");
            testFile = new File(testFileName);
            reader = new PushbackInputStream(new FileInputStream(testFile));
            writer = new FileWriter(outputName, false);
            writer.write("");
            writer.flush();
            writer.close();
            writer = new FileWriter(outputName, true);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
