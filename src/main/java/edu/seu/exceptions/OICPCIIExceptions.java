package edu.seu.exceptions;


import edu.seu.base.CodeEnum;

public class OICPCIIExceptions extends Exception{
    CodeEnum codeEnum;

    public CodeEnum getCodeEnum(){return codeEnum;}

    public void setCodeEnum(CodeEnum codeEnum){
        this.codeEnum = codeEnum;
    }
    public OICPCIIExceptions(){
    }
    public OICPCIIExceptions(CodeEnum codeEnum, String msg)
    {
        super(msg);
        this.codeEnum = codeEnum;
    }
    public OICPCIIExceptions(String msg)
    {
        super(msg);
    }

}
