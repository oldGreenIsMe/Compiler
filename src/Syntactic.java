public class Syntactic {
    public static int index = -1;
    public static String nowSym(){
        return LexicalAnalysis.tokenList.get(index).tokenType;
    }

    public static String nextSym(){
        return LexicalAnalysis.tokenList.get(index+1).tokenType;
    }

    public static String getsymbol(){
        if(index < LexicalAnalysis.tokenList.size()-1){
            if(index>=0)
                IOtools.write(LexicalAnalysis.tokenList.get(index).tokenType+" "+LexicalAnalysis.tokenList.get(index).tokenName);
            index++;
        }else{
            IOtools.write(LexicalAnalysis.tokenList.get(index).tokenType+" "+LexicalAnalysis.tokenList.get(index).tokenName);
        }
        return LexicalAnalysis.tokenList.get(index).tokenType;
    }

    public static Boolean judgeAssigh(int index){
        int i = index;
        while(!LexicalAnalysis.tokenList.get(i).tokenType.equals("SEMICN")){
            if(LexicalAnalysis.tokenList.get(i).tokenType.equals("ASSIGN")){
                return true;
            }
            i++;
        }
        return false;
    }

    public static Boolean judgeDecl(int index){
        if(LexicalAnalysis.tokenList.get(index+2).tokenType.equals("LPARENT"))
            return false;
        return true;
    }

    public static void compUnit(){
        while(judgeDecl(index)){
            decl();
        }
        while(!nextSym().equals("MAINTK")){
            funcDef();
        }
        mainFuncDef();
        IOtools.write("<CompUnit>");
    }

    public static void decl(){
        if(nowSym().equals("CONSTTK")){
            constDecl();
        }else{
            varDecl();
        }
    }

    public static void constDecl(){
        if(nowSym().equals("CONSTTK")){
            getsymbol();                // pass 'const'
            bType();
            constDef();
            while(nowSym().equals("COMMA")){
                getsymbol();            // pass ','
                constDef();
            }
            getsymbol();                // pass ';'
            IOtools.write("<ConstDecl>");
        }
    }

    public static void bType(){
        if(nowSym().equals("INTTK")) {
            getsymbol();                // pass 'int'
        }
    }

    public static void constDef(){
        getsymbol();                        // pass identifier
        while(nowSym().equals("LBRACK")){
            getsymbol();                    // pass '['
            constExp();
            getsymbol();    // pass ']'
        }
        getsymbol();            // pass '='
        constInitVal();
        IOtools.write("<ConstDef>");
    }

    public static void constInitVal(){
        if(nowSym().equals("LBRACE")){
            getsymbol();                       // pass '{'
            if(!nowSym().equals("RBRACE")){
                constInitVal();
                while(nowSym().equals("COMMA")){
                    getsymbol();                // pass ','
                    constInitVal();
                }
            }
            getsymbol();                        // pass '}'
        }else{
            constExp();
        }
        IOtools.write("<ConstInitVal>");
    }

    public static void varDecl(){
        bType();
        varDef();
        while(nowSym().equals("COMMA")){
            getsymbol();                            // pass ','
            varDef();
        }
        getsymbol();                                // pass ';'
        IOtools.write("<VarDecl>");
    }

    public static void varDef(){
        getsymbol();                                // pass identifier
        while(nowSym().equals("LBRACK")){
            getsymbol();                            // pass '['
            constExp();
            getsymbol();                            // pass ']'
        }
        if(nowSym().equals("ASSIGN")){
            getsymbol();                            // pass '='
            initVal();
        }
        IOtools.write("<VarDef>");
    }

    public static void initVal(){
        if(nowSym().equals("LBRACE")){
            getsymbol();                            // pass '{'
            if(!nowSym().equals("RBRACE")){
                initVal();
                while(nowSym().equals("COMMA")){
                    getsymbol();                    // pass ','
                    initVal();
                }
            }
            getsymbol();                            //  pass '}'
        }else{
            exp();
        }
        IOtools.write("<InitVal>");
    }

    public static void funcDef(){
        funcType();
        getsymbol();                                    // pass identifer
        getsymbol();    // pass '('
        if(!nowSym().equals("RPARENT")){
            funcFParams();
        }
        getsymbol();    // pass ')'
        block();
        IOtools.write("<FuncDef>");
    }

    public static void mainFuncDef(){
        if(nowSym().equals("INTTK")){
            getsymbol();    // pass 'int'
            getsymbol();    // pass 'main'
            getsymbol();    // pass '('
            getsymbol();    // pass ')'
            block();
            IOtools.write("<MainFuncDef>");
        }
    }

    public static void funcType(){
        getsymbol();    // pass 'int' or 'void'
        IOtools.write("<FuncType>");
    }

    public static void funcFParams(){
        funcFParam();
        while(nowSym().equals("COMMA")){
            getsymbol();                        // pass ','
            funcFParam();
        }
        IOtools.write("<FuncFParams>");
    }

    public static void funcFParam(){
        bType();
        getsymbol();                                    // pass identifier
        if(nowSym().equals("LBRACK")){
            getsymbol();    // pass '['
            getsymbol();    // pass ']'
            while(nowSym().equals("LBRACK")){
                getsymbol();    // pass '['
                constExp();
                getsymbol();    // pass ']'
            }
        }
        IOtools.write("<FuncFParam>");
    }

    public static void block(){
        getsymbol();    // pass '{'
        while(!nowSym().equals("RBRACE")){
            blockItem();
        }
        getsymbol();    // pass '}'
        IOtools.write("<Block>");
    }

    public static void blockItem(){
        if(nowSym().equals("INTTK") || nowSym().equals("CONSTTK")){
            decl();
        }else{
            stmt();
        }
    }

    public static void stmt(){
        if(nowSym().equals("PRINTFTK")){
            getsymbol();                        // pass 'printf'
            getsymbol();                        // pass '('
            getsymbol();                        // pass formatString
            while(nowSym().equals("COMMA")){
                getsymbol();                    // pass ','
                exp();
            }
            getsymbol();                        // pass ')'
            getsymbol();                        // pass ';'
        }else if(nowSym().equals("RETURNTK")){
            getsymbol();                        // pass 'return'
            if(!nowSym().equals("SEMICN")){
                exp();
            }
            getsymbol();                        // pass ';'
        }else if(nowSym().equals("BREAKTK") || nowSym().equals("CONTINUETK")){
            getsymbol();                        // pass 'break' or 'continue'
            getsymbol();                        // pass ';'
        }else if(nowSym().equals("WHILETK")){
            getsymbol();                        // pass 'while'
            getsymbol();                        // pass '('
            cond();
            getsymbol();                        // pass ')'
            stmt();
        }else if(nowSym().equals("IFTK")){
            getsymbol();                        // pass 'if'
            getsymbol();                        // pass '('
            cond();                             // pass 'cond'
            getsymbol();                        // pass ')'
            stmt();
            if(nowSym().equals("ELSETK")){
                getsymbol();                    // pass 'else'
                stmt();
            }
        }else if(nowSym().equals("LBRACE")){
            block();
        }else if(judgeAssigh(index)){
            lVal();
            getsymbol();                         // pass '='
            if(nowSym().equals("GETINTTK")){
                getsymbol();                     // pass 'getint'
                getsymbol();                    // pass '('
                getsymbol();                    // pass ')'
                getsymbol();                    // pass ';'
            }else{
                exp();
                getsymbol();                      // pass ';'
            }
        }else{
            if(nowSym().equals("SEMICN")){
                getsymbol();                        // pass ';'
            }else{
                exp();
                getsymbol();                        // pass ';'
            }
        }
        IOtools.write("<Stmt>");
    }

    public static void exp(){
        addExp();
        IOtools.write("<Exp>");
    }

    public static void cond(){
        lOrExp();
        IOtools.write("<Cond>");
    }

    public static void lVal(){
        getsymbol();                                // pass identifier
        while(nowSym().equals("LBRACK")){
            getsymbol();                            // pass '['
            exp();
            getsymbol();                            // pass ']'
        }
        IOtools.write("<LVal>");
    }

    public static void primaryExp(){
        if(nowSym().equals("LPARENT")){
            getsymbol();                            // pass '('
            exp();
            getsymbol();                            // pass ')'
        }else if(nowSym().equals("INTCON")){
            number();
        }else{
            lVal();
        }
        IOtools.write("<PrimaryExp>");
    }

    public static void number(){
        getsymbol();                                 // pass intconst
        IOtools.write("<Number>");
    }

    public static void unaryExp(){
        if(nowSym().equals("PLUS") || nowSym().equals("MINU") || nowSym().equals("NOT")){
            unaryOp();
            unaryExp();
        }else if(nowSym().equals("IDENFR") && nextSym().equals("LPARENT")){
            getsymbol();                        // pass identifier
            getsymbol();                        // pass '('
            if(!nowSym().equals("RPARENT")){
                funcRParams();
            }
            getsymbol();                        // pass ')'
        }else{
            primaryExp();
        }
        IOtools.write("<UnaryExp>");
    }

    public static void unaryOp(){
        getsymbol();                            // pass '+' or '-' or '!'
        IOtools.write("<UnaryOp>");
    }

    public static void funcRParams(){
        exp();
        while(nowSym().equals("COMMA")){
            getsymbol();                        // pass ','
            exp();
        }
        IOtools.write("<FuncRParams>");
    }

    public static void mulExp(){
        unaryExp();
        while(nowSym().equals("MULT") || nowSym().equals("DIV") || nowSym().equals("MOD")){
            IOtools.write("<MulExp>");
            getsymbol();                        // pass '*' or '/' or '%'
            unaryExp();
        }
        IOtools.write("<MulExp>");
    }

    public static void addExp(){
        mulExp();
        while(nowSym().equals("PLUS") || nowSym().equals("MINU")){
            IOtools.write("<AddExp>");
            getsymbol();                        // pass '+' or '-'
            mulExp();
        }
        IOtools.write("<AddExp>");
    }

    public static void relExp(){
        addExp();
        while(nowSym().equals("LSS") || nowSym().equals("GRE") || nowSym().equals("LEQ") || nowSym().equals("GEQ")){
            IOtools.write("<RelExp>");
            getsymbol();                        // pass '<' or '>' or '<=' or >='
            addExp();
        }
        IOtools.write("<RelExp>");
    }

    public static void eqExp(){
        relExp();
        while(nowSym().equals("EQL") || nowSym().equals("NEQ")){
            IOtools.write("<EqExp>");
            getsymbol();                        // pass '==' or '!='
            relExp();
        }
        IOtools.write("<EqExp>");
    }

    public static void lAndExp(){
        eqExp();
        while(nowSym().equals("AND")){
            IOtools.write("<LAndExp>");
            getsymbol();                        // pass '&&'
            eqExp();
        }
        IOtools.write("<LAndExp>");
    }

    public static void lOrExp(){
        lAndExp();
        while(nowSym().equals("OR")){
            IOtools.write("<LOrExp>");
            getsymbol();                        // pass '||'
            lAndExp();
        }
        IOtools.write("<LOrExp>");
    }

    public static void constExp(){
        addExp();
        IOtools.write("<ConstExp>");
    }

    public static void analysis(){
        getsymbol();
        compUnit();
    }
}
