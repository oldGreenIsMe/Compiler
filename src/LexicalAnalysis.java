import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;

public class LexicalAnalysis {

    public static int comment_status = 0;// 0 is normal, 1 is the single comment, 2 is the many comment
    public static String[] SELF_DEFINED_TOKEN = {"IDENFR", "INTCON", "STRCON"};
    public static HashMap<String, String> DEFINEDED_TOKEN;
    public static LinkedList<TokenNode> tokenList;
    public static void initLexical() {
        try {
            tokenList = new LinkedList<>();
            DEFINEDED_TOKEN = new HashMap<>();
            DEFINEDED_TOKEN.put("main", "MAINTK");
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addToList(String tokenType, String tokenName){
        TokenNode tokenNode = new TokenNode(tokenType, tokenName);
        tokenList.add(tokenNode);
    }

    public static void addToList(String tokenType, int num){
        TokenNode tokenNode = new TokenNode(tokenType, ""+num);
        tokenList.add(tokenNode);
    }

    public static void judgeIdentifier() {
        String tmpString = new String("");
        while (IOtools.alpha != -1 && (Character.isLetter(IOtools.alphabet) || Character.isDigit(IOtools.alphabet) || IOtools.alphabet == '_')) {
            tmpString += IOtools.alphabet;
            IOtools.getAlpha();
        }

        if (DEFINEDED_TOKEN.containsKey(tmpString)) {
            addToList(DEFINEDED_TOKEN.get(tmpString), tmpString);
        } else {
            addToList(SELF_DEFINED_TOKEN[0], tmpString);
        }
    }

    public static void judgeIntConst() {
        int tmpInt = 0;
        while (IOtools.alpha != -1 && Character.isDigit(IOtools.alphabet)) {
            tmpInt = tmpInt * 10 + (IOtools.alphabet - '0');
            IOtools.getAlpha();
        }

        addToList(SELF_DEFINED_TOKEN[1], tmpInt);
    }

    public static void judgeSimpleItem() {
        String tmpString = new String("");
        tmpString += IOtools.alphabet;
        if (IOtools.alphabet == '<' || IOtools.alphabet == '>' || IOtools.alphabet == '!' || IOtools.alphabet == '=' || IOtools.alphabet == '&' || IOtools.alphabet == '|') {
            // to see if the symbol can cooperate with the symbol behind
            IOtools.getAlpha();
            tmpString += IOtools.alphabet;
            if (DEFINEDED_TOKEN.containsKey(tmpString)) {
                addToList(DEFINEDED_TOKEN.get(tmpString),tmpString);
                IOtools.getAlpha();
            } else {
                tmpString = tmpString.substring(0, 1);
                addToList(DEFINEDED_TOKEN.get(tmpString), tmpString);
            }
        } else {
            // see the symbol that can not cooperate with the symbol behind
            addToList(DEFINEDED_TOKEN.get(tmpString), tmpString);
            IOtools.getAlpha();
        }
    }

    public static void judgeFormatString() {
        String tmpString = new String("");
        do {
            tmpString += IOtools.alphabet;
            IOtools.getAlpha();
        } while (IOtools.alpha != -1 && IOtools.alphabet != '"');
        tmpString += '"';
        addToList(SELF_DEFINED_TOKEN[2], tmpString);
        IOtools.getAlpha();
    }

    public static void analysis() {
        IOtools.getAlpha();
        int flag = 0;
        while(IOtools.alpha != -1){
            if(comment_status == 0) {
                if (IOtools.alphabet == '\t' || IOtools.alphabet == '\r' || IOtools.alphabet == '\n' || IOtools.alphabet == ' ') {
                    // back to start condition
                    if (IOtools.alphabet == '\n')
                        IOtools.lineNum++;
                    IOtools.getAlpha();
                } else if (Character.isLetter(IOtools.alphabet) || IOtools.alphabet == '_') {
                    // identifier judge head: letter or '_'
                    judgeIdentifier();
                } else if (Character.isDigit(IOtools.alphabet)) {
                    // intConst judge head: number
                    judgeIntConst();
                } else if (IOtools.alphabet == '"') {
                    // formatString judge head: "
                    judgeFormatString();
                } else if(IOtools.alphabet == '/'){
                    // judge the special symbol / to see if it is comment
                    char forwardSymbol = IOtools.alphabet;
                    IOtools.getAlpha();
                    if(IOtools.alphabet == '/'){
                        comment_status = 1;
                        IOtools.getAlpha();
                    }else if(IOtools.alphabet == '*'){
                        comment_status = 2;
                        IOtools.getAlpha();
                    }
                    else{
                        addToList(DEFINEDED_TOKEN.get("/"), ""+forwardSymbol);
                    }
                }
                else {
                    judgeSimpleItem();
                }
            }else if(comment_status == 1){
                // when see the symbol '\n',return to the normal status
                if(IOtools.alphabet=='\n'){
                    comment_status = 0;
                }
                IOtools.getAlpha();
            }else{
                // when see the symbol '*' and symbol '/'behind, return to the normal status
                if(IOtools.alphabet == '*'){
                    IOtools.getAlpha();
                    if(IOtools.alphabet == '/'){
                        comment_status = 0;
                        IOtools.getAlpha();
                    }
                }else{
                    IOtools.getAlpha();
                }
            }
        }
    }

    public static void finishReadAndWrite() {
        try {
            IOtools.reader.close();
            IOtools.writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
