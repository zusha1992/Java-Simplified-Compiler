import java.util.ArrayList;
import java.util.List;

public class Scope {

    enum ScopeType{
        GLOBAL,
        FUNCTION,
        IF_WHILE
    }
    List<Variable> variables;
    List<Method>  methods;
    ScopeType type;
    public int returnIndex = -1;

    Scope(ScopeType type){
        this.variables = new ArrayList<>();
        this.methods = new ArrayList<>();
        this.type = type;

    }
    Scope(List<Variable> variables){
        this.variables = variables;
        this.methods = new ArrayList<>();
        this.type = ScopeType.FUNCTION;

    }

    public Variable isVariableExist(String name){
        //use arrayList Function for search
        for (Variable var:this.variables) {
            if (var.name.equals(name)){
                return var;
            }
        }
        return  null;

    }

    public Method isMethodExist(String name){
        //use arrayList Function for search
        for (Method method :this.methods) {
            if (method.name.equals(name)){
                return method;
            }
        }
        return  null;

    }

    public ScopeType getScopeType(){
        return this.type;
    }
}

//add property scopeType(global, functionDeclaration, ifWhile)
//
