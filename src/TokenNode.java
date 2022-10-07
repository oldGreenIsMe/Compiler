public class TokenNode {
    public String tokenType;
    public String tokenName;
    public TokenNode(){

    }
    public TokenNode(String tokenType, String tokenName){
        String tmpType = new String(tokenType);
        String tmpName = new String(tokenName);
        this.tokenType = tmpType;
        this.tokenName = tmpName;
    }

}
