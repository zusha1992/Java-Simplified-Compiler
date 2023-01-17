import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

//Java verifier - in charge of validating each line in the file and return Error message if code is not valid.
public class JavaVerifier {
    List<Scope> scopes;
    String[] lines;

    //init function - keeps track of the number of lines and number of scopes in the current file
    JavaVerifier(String path) throws IOException {
        this.scopes = new ArrayList<>();
        this.scopes.add(new Scope(Scope.ScopeType.GLOBAL));
        List<String> linesToList = Files.readAllLines(Paths.get(path));
        this.lines = linesToList.toArray(new String[linesToList.size()]);
    }

    // verify function - iterating over the file lines and calling function to update the data structure.
//    in case of Error - return Error message
    public void verify() throws JavaVerifierException {
        for (int i = 0; i < this.lines.length; i++) {
            switch (FormatVerifier.checkLineType(this.lines[i])) {
                case VARIABLE_DECLARATION:
                    createVariable(lines[i]);
                    break;
                case VARIABLE_ASSIGNMENT:
                    assignVariable(lines[i]);
                    break;
                case FUNCTION_DECLARTION:
                    createFunction(lines[i]);
                    break;
                case FUNCTION_CALL:
                    functionCall(lines[i]);
                    break;
                case IF_WHILE:
                    createIfWhileBlock(lines[i]);
                    break;
                case RETURN:
                    handelReturn(i);
                    break;
                case CLOSE_BLOCK:
                    closeBlock(i);
                    break;
                case EMPTY_LINE:
                    break;

                default:
                    throw new JavaVerifierException("code in line " + i + " is not legal");
            }
        }
        if (this.scopes.size() > 1)
            throw new JavaVerifierException("unclosed scope!");


    }

    //create variable function - gets a text line as input, separate to regex groups, create variable from groups, update data structure
    private void createVariable(String line) throws JavaVerifierException {
        Matcher matcher = FormatVerifier.captureGroups(FormatVerifier.variableDeclaration, line);
        Variable var = createVariablesFromGroups(new int[]{1, 2, 3, 5}, matcher);
        this.scopes.get(this.scopes.size() - 1).variables.add(var);

    }

    // assign variable - gets a text line, dividing to groups, checks if variable exists, update variable value if legal
//    return Error message if needed
    private void assignVariable(String line) throws JavaVerifierException {
        Matcher matcher = FormatVerifier.captureGroups(FormatVerifier.variableAssignment, line);
        String name = matcher.group(1);
        String value = matcher.group(2);
        Variable var = searchVariableInScopes(name);
        if (var == null)
            throw new JavaVerifierException("variable does not exist in memory");
        if (!checkVariableAssignmentOrFunctionInputs(var, value, false))
            throw new JavaVerifierException("value does not correspond with type");
//        ArrayList<Variable> = this.scopes.get(this.scopes.size() -1).variables.get()

    }

    //    create function - gets text line, dividing to groups, checking for parameters,
//    updating all relevant fields in the data structure. return Error message if needed.
    private void createFunction(String line) throws JavaVerifierException {
        Matcher matcher = FormatVerifier.captureGroups(FormatVerifier.functionDeclaration, line);
        String name = matcher.group(1);
        List<Variable> inputs = new ArrayList<>();
        if (matcher.group(2) != "") {
            String paramGroup = matcher.group(2);
            String[] parameters = paramGroup.split(",");
            for (int i = 0; i < parameters.length; i++) {
                Matcher matcher2 = FormatVerifier.captureGroups(FormatVerifier.inputVarDeclaration, parameters[i]);
                Variable var = createVariablesFromGroups(new int[]{1, 2, 3}, matcher2);
                inputs.add(var);
            }
        }
        this.scopes.get(this.scopes.size() - 1).methods.add(new Method(name, inputs));
        this.scopes.add(new Scope(new ArrayList<>(inputs)));

    }

    //    gets a text line, capturing groups, check if method exists in data and if parameters are legal,
//    if not, return error.
    private void functionCall(String line) throws JavaVerifierException {
        Matcher matcher = FormatVerifier.captureGroups(FormatVerifier.callAFunction, line);
        String name = matcher.group(1);
        String paramGroup = matcher.group(2);
        String[] parameters = paramGroup != null ? paramGroup.split("\\s*,\\s*") : new String[0];
        Method method = searchMethodInScopes(name);
        if (method == null) {
            throw new JavaVerifierException("no method found with name: " + name);
        }
        if (method.inputs.size() != parameters.length) {
            throw new JavaVerifierException("inputs not correspond with method: " + name);
        }
        for (int j = 0; j < parameters.length; j++) {
            if (!checkVariableAssignmentOrFunctionInputs(method.inputs.get(j), parameters[j], true))
                throw new JavaVerifierException("input types does not correspond with function input types ");
        }

    }
//    gets text line, capturing groups, check for valid condition and updating data structure
    private void createIfWhileBlock(String line) throws JavaVerifierException {
        Matcher matcher = FormatVerifier.captureGroups(FormatVerifier.ifWhile, line);
        boolean isValidCondition = checkCondition(matcher.group(2));
        if (!isValidCondition) {
            throw new JavaVerifierException("Error! condition is not valid!!");
        }
        this.scopes.add(new Scope(Scope.ScopeType.IF_WHILE));

    }

//    gets index and updating return statment index
    private void handelReturn(int i) {
        this.scopes.get(this.scopes.size() - 1).returnIndex = i;

    }

//    gets index, check if there is a scope to close and if it is a valid close block.
//    if it is, remove the last scope from data structure, otherwise, return Error statement.
    private void closeBlock(int i) throws JavaVerifierException {
        if (this.scopes.size() <= 1)
            throw new JavaVerifierException("Error! no more scopes to close");

        Scope lastScope = this.scopes.get(this.scopes.size() - 1);
        if (lastScope.type == Scope.ScopeType.FUNCTION && (lastScope.returnIndex + 1 != i)) {
            throw new JavaVerifierException("function ended without return statement");
        }
        this.scopes.remove(this.scopes.size() - 1);
    }

    private boolean checkCondition(String value) throws JavaVerifierException {
        String[] parameters = value != null ? value.split("\\s*" + FormatVerifier.andOr + "\\s*") : new String[0];
        Variable booleanVerifier = new Variable(false, FormatVerifier.VariableType.BOOLEAN, "");
        for (int i = 0; i < parameters.length; i++) {
            if (!checkVariableAssignmentOrFunctionInputs(booleanVerifier, parameters[i], false)) {
                return false;
            }
        }
        return true;

    }


    private boolean checkVariableAssignmentOrFunctionInputs(Variable var, String value, boolean isFunctionCall) {
        boolean tempbool = var.isFinal;
        if (isFunctionCall)
            var.isFinal = false;
        if (var.isVariableLegal(value, isFunctionCall)) {
            if (isFunctionCall) {
                var.isFinal = tempbool;
            }
            return true;
        }
        Variable previousVariable = searchVariableInScopes(value);
        if (previousVariable != null) {
            if (var.isVariableLegal(previousVariable, isFunctionCall)) {
                if (isFunctionCall) {
                    var.isFinal = tempbool;
                }
                return true;
            }
        }
        if (isFunctionCall)
            var.isFinal = tempbool;
        return false;
    }

    private Variable searchVariableInScopes(String name) {
        for (int i = this.scopes.size() - 1; i >= 0; i--) {
            Variable variable = this.scopes.get(i).isVariableExist(name);
            if (variable != null) {
                return variable;
            }
        }
        return null;
    }

    private Method searchMethodInScopes(String name) {
        for (int i = this.scopes.size() - 1; i >= 0; i--) {
            Method method = this.scopes.get(i).isMethodExist(name);
            if (method != null) {
                return method;
            }
        }
        return null;
    }


    private Variable createVariablesFromGroups(int[] nums, Matcher matcher) throws JavaVerifierException {
        boolean isFinal = matcher.group(nums[0]) != null;
        FormatVerifier.VariableType type = FormatVerifier.checkVariableType(matcher.group(nums[1]));
        String varName = matcher.group(nums[2]);
        if (nums.length == 4) {
            String value = matcher.group(nums[3]);
            if (value != null)
                return new Variable(isFinal, type, varName, value);
        }
        return new Variable(isFinal, type, varName);
    }
}
