public class Compiler {
    public static void main(String[] args){
        IOtools.initIO();
        LexicalAnalysis.initLexical();
        LexicalAnalysis.analysis();
        Syntactic.analysis();
        LexicalAnalysis.finishReadAndWrite();
    }
}
