import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatVerifier {
    public enum LineType {
        VARIABLE_DECLARATION,
        VARIABLE_ASSIGNMENT,
        FUNCTION_DECLARTION,
        FUNCTION_CALL,
        IF_WHILE,
        EMPTY_LINE,
        RETURN,
        CLOSE_BLOCK,
        NONE
    }

    public enum VariableType {
        INT,
        STRING,
        BOOLEAN,
        CHAR,
        FLOAT,
        DOUBLE,
        VARIABLE,
        NONE
    }


    final static String type = "(int|boolean|String|char|float|double)";
    final static String underScoreName = "_+[a-zA-Z0-9_]+";
    final static String noneUnderscoreName = "[a-zA-Z]+[a-zA-Z0-9_]*";
    final static String name = "(" + noneUnderscoreName + "|" + underScoreName + ")";
    final static String ignoreSpace = "\\s*";
    static final public String ignorLine = "((^\\s*//.*)?|(\\s*))";
    final static String space = "\\s+";
    final static String coma = ",";
    final static String validInteger = "[0-9]+";
    final static String validFloatingPoint = "[0-9]+\\.[0-9]+";
    final static String validNumber = "-?[0-9]+(\\.[0-9]+)?";
    final static String validString = "\\\".*\\\"";
    final static String validChar = "\\'.\\'";
    final static String validBoolean = "true|false|" + validNumber;
    final static String validValue = "(" + validChar + "|" + validString + "|" + validBoolean + "|" + name + ")";
    final static String equalSign = ignoreSpace + "=" + ignoreSpace;
    final static String operators = "(==|<|>|<=|>=)";
    final static String returnCommand = ignoreSpace + "return\s*;" + ignoreSpace;
    final static String andOr = "(\\|\\|" + "|" + "&&)";
    final static String openScopeSign = ignoreSpace + "\\{" + ignoreSpace;
    final static String closeScopeSign = ignoreSpace + "\\}" + ignoreSpace;
    final static String oneSideBoolean = "(" + ignoreSpace + "(" + validBoolean + "|" + name + ")" + ignoreSpace + ")";
    static final public String booleanExpression = oneSideBoolean + "(" + operators + oneSideBoolean + ")" + "?";
    static final public String ifWhile = ignoreSpace + "(" + "if" + "|" + "while" + ")" + ignoreSpace + "\\(" + "(" + ignoreSpace + "((" + name + "|" + validBoolean + ")" + ignoreSpace + andOr + "?" + ignoreSpace + ")*)" + "\\)" + openScopeSign;
    static final public String variableDeclaration = "^" + ignoreSpace + "(final" + space + ")?" + type + space + name + ignoreSpace +
            "(" + equalSign + ignoreSpace + "(" + validValue + ")" + ignoreSpace + ")?;";
    static final String inputVarDeclaration = ignoreSpace + "(final" + space + ")?" + type + space + name + ignoreSpace + coma + "?" + ignoreSpace;
    static final public String input = "(" + ignoreSpace + validValue + ignoreSpace + coma + "?" + ignoreSpace + ")*";
    static final public String variableAssignment = "^" + ignoreSpace + name + ignoreSpace + equalSign + validValue + ignoreSpace + ";" + ignoreSpace;
    static final public String functionDeclaration = "void" + space + name + "\\(" + "((" + inputVarDeclaration + ")*)" + "\\)" + openScopeSign;
    static final public String callAFunction = ignoreSpace + name + ignoreSpace + "\\(" + "(" + input + ")" + "\\)" + ";";
    final static String[] legalLineTypes = {variableDeclaration, variableAssignment, functionDeclaration, callAFunction, ignorLine, ifWhile, closeScopeSign};


    public static Boolean checkValidity(String regexCode, String textInput) {
        Pattern code = Pattern.compile(regexCode);
        Matcher text = code.matcher(textInput);
        return text.matches();
    }


    public static Matcher captureGroups(String regexCode, String textInput) {
        Pattern code = Pattern.compile(regexCode);
        Matcher text = code.matcher(textInput);
        text.matches();
        return text;
    }

    public static LineType checkLineType(String line) {
        if (checkValidity(variableDeclaration, line))
            return LineType.VARIABLE_DECLARATION;

        if (checkValidity(variableAssignment, line))
            return LineType.VARIABLE_ASSIGNMENT;

        if (checkValidity(functionDeclaration, line))
            return LineType.FUNCTION_DECLARTION;

        if (checkValidity(callAFunction, line))
            return LineType.FUNCTION_CALL;

        if (checkValidity(ifWhile, line))
            return LineType.IF_WHILE;

        if (checkValidity(returnCommand, line))
            return LineType.RETURN;

        if (checkValidity(closeScopeSign, line))
            return LineType.CLOSE_BLOCK;

        if (checkValidity(ignorLine, line))
            return LineType.EMPTY_LINE;

        return LineType.NONE;
    }

    public static VariableType checkVariableType(String type) throws JavaVerifierException {
        switch (type) {
            case "int":
                return VariableType.INT;
            case "boolean":
                return VariableType.BOOLEAN;
            case "char":
                return VariableType.CHAR;
            case "float":
                return VariableType.FLOAT;
            case "double":
                return VariableType.DOUBLE;
            case "String":
                return VariableType.STRING;
            default:
                throw new JavaVerifierException("unsupported type");


        }
    }

    public static VariableType checkValueType(String value) {
        if (checkValidity(validChar, value)) {
            return VariableType.CHAR;
        }
        if (checkValidity(validString, value)) {
            return VariableType.STRING;
        }
        if (checkValidity(validInteger, value)) {
            return VariableType.INT;
        }
        if (checkValidity(validFloatingPoint, value)) {
            return VariableType.DOUBLE;
        }
        if (checkValidity(validBoolean, value)) {
            return VariableType.BOOLEAN;
        }
        if (checkValidity(name, value)) {
            return VariableType.VARIABLE;
        }


        return VariableType.NONE;
    }
}

