import java.io.*;
import java.util.HashMap;

public class LexicalAnalysis {
    public static String testFileName;
    public static String outputName;
    public static File testFile;
    public static PushbackInputStream reader;
    public static FileWriter writer;
    public static int alpha;
    public static char alphabet;
    public static int lineNum = 1;
    public static int comment_status = 0;// 0 is normal, 1 is the single comment, 2 is the many comment
    public static String[] SELF_DEFINED_TOKEN = {"IDENFR","INTCON","STRCON"};
    public static HashMap<String, String> DEFINEDED_TOKEN;
    public static int getAlpha(){
        try {
            alpha = reader.read();
            alphabet = (char)alpha;
        }catch(IOException e){
            e.printStackTrace();
        }
        return alpha;
    }
    public static void ungetsym(){
        try{
            reader.unread(alpha);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public static void write(String string){
        try{
            writer.write(string);
            writer.write('\n');
            writer.flush();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public static void initLexical(){
        try {
            testFileName = new String("testfile.txt");
            outputName = new String("output.txt");
            DEFINEDED_TOKEN = new HashMap<>();
            DEFINEDED_TOKEN.put("main","MAINTK");
            DEFINEDED_TOKEN.put("const", "CONSTTK");
            DEFINEDED_TOKEN.put("int", "INTTK");
            DEFINEDED_TOKEN.put("break", "BREAKTK");
            DEFINEDED_TOKEN.put("continue", "CONTINUETK");
            DEFINEDED_TOKEN.put("if", "IFTK");
            DEFINEDED_TOKEN.put("else", "ELSETK");
            DEFINEDED_TOKEN.put("!", "NOT");
            DEFINEDED_TOKEN.put("&&", "AND");
            DEFINEDED_TOKEN.put("||", "OR");
            DEFINEDED_TOKEN.put("while", "WHILETK");
            DEFINEDED_TOKEN.put("getint", "GETINTTK");
            DEFINEDED_TOKEN.put("printf", "PRINTFTK");
            DEFINEDED_TOKEN.put("return", "RETURNTK");
            DEFINEDED_TOKEN.put("+", "PLUS");
            DEFINEDED_TOKEN.put("-", "MINU");
            DEFINEDED_TOKEN.put("void", "VOIDTK");
            DEFINEDED_TOKEN.put("*", "MULT");
            DEFINEDED_TOKEN.put("/", "DIV");
            DEFINEDED_TOKEN.put("%", "MOD");
            DEFINEDED_TOKEN.put("<", "LSS");
            DEFINEDED_TOKEN.put("<=", "LEQ");
            DEFINEDED_TOKEN.put(">", "GRE");
            DEFINEDED_TOKEN.put(">=", "GEQ");
            DEFINEDED_TOKEN.put("==", "EQL");
            DEFINEDED_TOKEN.put("!=", "NEQ");
            DEFINEDED_TOKEN.put("=", "ASSIGN");
            DEFINEDED_TOKEN.put(";", "SEMICN");
            DEFINEDED_TOKEN.put(",", "COMMA");
            DEFINEDED_TOKEN.put("(", "LPARENT");
            DEFINEDED_TOKEN.put(")", "RPARENT");
            DEFINEDED_TOKEN.put("[", "LBRACK");
            DEFINEDED_TOKEN.put("]", "RBRACK");
            DEFINEDED_TOKEN.put("{", "LBRACE");
            DEFINEDED_TOKEN.put("}", "RBRACE");
            testFile = new File(testFileName);
            reader = new PushbackInputStream(new FileInputStream(testFile));
            writer = new FileWriter(outputName,false);
            writer.write("");
            writer.flush();
            writer.close();
            writer = new FileWriter(outputName, true);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void judgeIdentifier(){
        String tmpString = new String("");
        while(alpha != -1 && (Character.isLetter(alphabet) || Character.isDigit(alphabet) || alphabet=='_')){
            tmpString += alphabet;
            getAlpha();
        }

        if(DEFINEDED_TOKEN.containsKey(tmpString)){
            write(DEFINEDED_TOKEN.get(tmpString)+' '+tmpString);
        }else{
            write(SELF_DEFINED_TOKEN[0]+' '+tmpString);
        }
    }

    public static void judgeIntConst(){
        int tmpInt = 0;
        while(alpha !=-1 && Character.isDigit(alphabet)){
            tmpInt = tmpInt*10+(alphabet-'0');
            getAlpha();
        }
        write(SELF_DEFINED_TOKEN[1]+' '+tmpInt);
    }

    public static void judgeSimpleItem(){
        String tmpString = new String("");
        tmpString += alphabet;
        if(alphabet == '<' || alphabet == '>' || alphabet == '!' || alphabet == '=' || alphabet == '&' || alphabet == '|'){
            // to see if the symbol can cooperate with the symbol behind
            getAlpha();
            tmpString += alphabet;
            if(DEFINEDED_TOKEN.containsKey(tmpString)){
                write(DEFINEDED_TOKEN.get(tmpString)+' '+tmpString);
                getAlpha();
            }else{
                tmpString = tmpString.substring(0,1);
                write(DEFINEDED_TOKEN.get(tmpString)+' '+tmpString);
            }
        }
        else{
            // see the symbol that can not cooperate with the symbol behind
            write(DEFINEDED_TOKEN.get(tmpString)+' '+tmpString);
            getAlpha();
        }
    }
    public static void judgeFormatString(){
        String tmpString = new String("");
        do{
            tmpString += alphabet;
            getAlpha();
        }while(alpha != -1 && alphabet != '"');
        tmpString += '"';
        write(SELF_DEFINED_TOKEN[2]+' '+tmpString);
        getAlpha();
    }
    public static void analysis(){
        initLexical();
        getAlpha();
        while(alpha != -1){
            if(comment_status == 0) {
                if (alphabet == '\t' || alphabet == '\r' || alphabet == '\n' || alphabet == ' ') {
                    // back to start condition
                    if (alphabet == '\n')
                        lineNum++;
                    getAlpha();
                } else if (Character.isLetter(alphabet) || alphabet == '_') {
                    // identifier judge head: letter or '_'
                    judgeIdentifier();
                } else if (Character.isDigit(alphabet)) {
                    // intConst judge head: number
                    judgeIntConst();
                } else if (alphabet == '"') {
                    // formatString judge head: "
                    judgeFormatString();
                } else if(alphabet == '/'){
                    // judge the special symbol / to see if it is comment
                    char forwardSymbol = alphabet;
                    getAlpha();
                    if(alphabet == '/'){
                        comment_status = 1;
                        getAlpha();
                    }else if(alphabet == '*'){
                        comment_status = 2;
                        getAlpha();
                    }
                    else{
                        write(DEFINEDED_TOKEN.get("/")+' '+forwardSymbol);
                    }
                }
                else {
                    judgeSimpleItem();
                }
            }else if(comment_status == 1){
                // when see the symbol '\n',return to the normal status
                if(alphabet=='\n'){
                    comment_status = 0;
                }
                getAlpha();
            }else{
                // when see the symbol '*' and symbol '/'behind, return to the normal status
                if(alphabet == '*'){
                    getAlpha();
                    if(alphabet == '/'){
                        comment_status = 0;
                        getAlpha();
                    }
                }else{
                    getAlpha();
                }
            }
        }
        try {
            reader.close();
            writer.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
