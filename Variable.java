public class Variable {
    FormatVerifier.VariableType type;
    String name;
    Boolean isFinal;
    String value;
// initializing variable object with all properties
    Variable(Boolean isFinal, FormatVerifier.VariableType type, String name, String value) throws JavaVerifierException {
        this.isFinal = isFinal;
        this.type = type;
        this.name = name;
        if (this.verifyValueType(value))
            this.value = value;
        else {
            throw new JavaVerifierException("value does not correspond with type");
        }
    }

    //initializing variable object without value
    Variable(Boolean isFinal, FormatVerifier.VariableType type, String name) {
        this.value = null;
        this.isFinal = isFinal;
        this.type = type;
        this.name = name;

    }

//    function gets a String val, check with regex value type and return a boolean
    public boolean verifyValueType(String val) {
//        if (this.type == FormatVerifier.VariableType.BOOLEAN){
//            return FormatVerifier.checkValidity(FormatVerifier.validBoolean,val);
//        }
        return this.type == FormatVerifier.checkValueType(val);

    }

//
//
//    returns is final variable state
    public boolean verifyFinal() {
        return this.isFinal;
    }
// check if variable is final or not. if not check validity and update variable value.
//    return true if variable legal, else - false
    public boolean isVariableLegal(String value, boolean isFunctionCall) {
        if (!this.verifyFinal()) {
            if (this.verifyValueType(value)) {
                if (!isFunctionCall) {
                    this.value = value;
                    return true;
                }
                return true;
            }
        }
        return false;
    }
//
    public boolean isVariableLegal(Variable var, boolean isFunctionCall) {
        if (!this.verifyFinal()) {
            if (this.type == var.type || (this.type == FormatVerifier.VariableType.BOOLEAN &&(var.type == FormatVerifier.VariableType.INT || var.type == FormatVerifier.VariableType.DOUBLE))) {
                if (!isFunctionCall) {
                    this.value = var.value;
                    return true;
                }
                return true;
            }
        }
        return false;
    }


}
