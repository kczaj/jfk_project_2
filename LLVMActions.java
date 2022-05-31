
import java.util.HashSet;
import java.util.HashMap;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

class Value {
    public String type;
    public String value;

    public Value(String type, String value) {
        this.type = type;
        this.value = value;
    }
}

public class LLVMActions extends CzajmalBaseListener {

    HashMap<String, String> globalVariables = new HashMap<String, String>();
    HashMap<String, String> variables = new HashMap<String, String>();
    HashSet<String> types = new HashSet<String>() {{
        add("int");
        add("real");
        add("char");
    }};
    HashSet<String> definedFunctions = new HashSet<String>() {{
        add("print");
        add("read");
    }};

    HashMap<String, ArrayList<String>> functions = new HashMap<String, ArrayList<String>>();
    HashMap<String, ArrayList<String>> structures = new HashMap<String, ArrayList<String>>();

    List<Value> argumentsList = new ArrayList<Value>();
    Stack<Value> stack = new Stack<Value>();
    Boolean global;

    @Override
    public void enterProg(CzajmalParser.ProgContext ctx) {
        global = true;
    }

    @Override
    public void exitProg(CzajmalParser.ProgContext ctx) {
        LLVMGenerator.close_main();
        System.out.println(LLVMGenerator.generate());
    }

    @Override
    public void exitDeclarationAssignment(CzajmalParser.DeclarationAssignmentContext ctx) {
        String ID = ctx.declaration().getChild(1).getText();
        String ArrayOperation = ctx.operation().getChild(0).getText();
        if (ArrayOperation.charAt(0) == '"') {
            try {
                ID = ctx.declaration().getChild(2).getText();
                //Get array type and length
                String arrType = variables.get(ID);
                if (arrType == null) {
                    arrType = globalVariables.get(ID);
                }
                String[] split_array_type = arrType.split("\\[");
                String type = split_array_type[0];
                if (!type.equals("char")) {
                    error(ctx.getStart().getLine(), "type mismatch");
                }
                String len = split_array_type[1].split("\\]")[0];
                List<String> values = new ArrayList<>();
                String trimed_string = ArrayOperation.substring(1, ArrayOperation.length() - 1);

                if (trimed_string.length() > Integer.parseInt(len)) {
                    error(ctx.getStart().getLine(), "too big array");
                }
                for (int i = 0; i < trimed_string.length(); i++) {
                    LLVMGenerator.assignArrayCharElement(Integer.toString((int) trimed_string.charAt(i)), resolveScope(ID), Integer.toString(i), len);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                error(ctx.getStart().getLine(), "variable is not an array");
            }
        } else if (ArrayOperation.charAt(0) != '[') {
            if (!variables.containsKey(ID) && !globalVariables.containsKey(ID)) {
                error(ctx.getStart().getLine(), "variable not declared");
            }
            Value v = stack.pop();
            if (variables.containsKey(ID)) {
                if (!v.type.equals(variables.get(ID))) {
                    error(ctx.getStart().getLine(), "assignment type mismatch");
                }
            } else {
                if (!v.type.equals(globalVariables.get(ID))) {
                    error(ctx.getStart().getLine(), "assignment type mismatch");
                }
            }
            if (v.type.equals("int")) {
                LLVMGenerator.assignInt(resolveScope(ID), v.value);
            }
            if (v.type.equals("real")) {
                LLVMGenerator.assignReal(resolveScope(ID), v.value);
            }
            if (v.type.equals("char")) {
                LLVMGenerator.assignChar(resolveScope(ID), v.value);
            }
        } else {
            try {
                ID = ctx.declaration().getChild(2).getText();
                //Get array type and length
                String arrType = variables.get(ID);
                if (arrType == null) {
                    arrType = globalVariables.get(ID);
                }
                String[] split_array_type = arrType.split("\\[");
                String type = split_array_type[0];
                String len = split_array_type[1].split("\\]")[0];
                List<String> values = new ArrayList<>();

                if (argumentsList.size() > Integer.parseInt(len)) {
                    error(ctx.getStart().getLine(), "too big array");
                }
                for (Value v : argumentsList) {
                    if (v.type.equals("ID") && ((variables.containsKey(v.value) && variables.get(v.value).contains(type)) || (globalVariables.containsKey(v.value) && globalVariables.get(v.value).contains(type)))) {
                        if (type.equals("int")) {
                            values.add("%" + LLVMGenerator.loadInt(resolveScope(v.value)));
                        } else if (type.equals("real")) {
                            values.add("%" + LLVMGenerator.loadReal(resolveScope(v.value)));
                        } else if (type.equals("char")) {
                            values.add("%" + LLVMGenerator.loadChar(resolveScope(v.value)));
                        }
                    } else if (v.type.equals("ARRAY_ID") && ((variables.containsKey(v.value) && variables.get(v.value).contains(type)) || (globalVariables.containsKey(v.value) && globalVariables.get(v.value).contains(type)))) {
                        String[] split_array_id = v.value.split("\\[");
                        String id = split_array_id[0];
                        String arrId = split_array_id[1].split("\\]")[0];
                        if (type.equals("int")) {
                            values.add("%" + LLVMGenerator.loadIntArrayValue(resolveScope(id), arrId, len));
                        } else if (type.equals("real")) {
                            values.add("%" + LLVMGenerator.loadRealArrayValue(resolveScope(id), arrId, len));
                        } else if (type.equals("char")) {
                            values.add("%" + LLVMGenerator.loadCharArrayValue(resolveScope(id), arrId, len));
                        }
                    } else if ((v.type.equals("int") || v.type.equals("real") || v.type.equals("char")) && v.type.contains(type)) {
                        values.add(v.value);
                    }
                }
                if (values.size() != Integer.parseInt(len)) {
                    error(ctx.getStart().getLine(), "variables is array are not the same type. Expected :" + type);
                }
                for (int i = 0; i < values.size(); i++) {
                    if (type.equals("int")) {
                        LLVMGenerator.assignArrayIntElement(values.get(i), resolveScope(ID), Integer.toString(i), len);
                    } else if (type.equals("real")) {
                        LLVMGenerator.assignArrayRealElement(values.get(i), resolveScope(ID), Integer.toString(i), len);
                    } else if (type.equals("char")) {
                        LLVMGenerator.assignArrayCharElement(values.get(i), resolveScope(ID), Integer.toString(i), len);
                    }
                }
                argumentsList.clear();
            } catch (ArrayIndexOutOfBoundsException e) {
                error(ctx.getStart().getLine(), "variable is not an array");
            }
        }

    }

    @Override
    public void exitIdAssignment(CzajmalParser.IdAssignmentContext ctx) {
        String ID = ctx.ID().getText();

        if (!variables.containsKey(ID) && !globalVariables.containsKey(ID)) {
            error(ctx.getStart().getLine(), "variable not declared");
        }
        String ArrayOperation = ctx.operation().getChild(0).getText();
        if (ArrayOperation.charAt(0) == '"') {
            // STRINGS
            try {
                String arrType = variables.get(ID);
                if (arrType == null) {
                    arrType = globalVariables.get(ID);
                }
                String[] split_array_type = arrType.split("\\[");
                String type = split_array_type[0];
                if (!type.equals("char")) {
                    error(ctx.getStart().getLine(), "type mismatch");
                }
                String len = split_array_type[1].split("\\]")[0];
                List<String> values = new ArrayList<>();
                String trimed_string = ArrayOperation.substring(1, ArrayOperation.length() - 1);

                if (trimed_string.length() > Integer.parseInt(len)) {
                    error(ctx.getStart().getLine(), "too big array");
                }
                for (int i = 0; i < trimed_string.length(); i++) {
                    LLVMGenerator.assignArrayCharElement(Integer.toString((int) trimed_string.charAt(i)), resolveScope(ID), Integer.toString(i), len);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                error(ctx.getStart().getLine(), "variable is not an array");
            }
        } else if (ArrayOperation.charAt(0) != '[') {
            // OPERATION
            Value v = stack.pop();
            if (variables.containsKey(ID)) {
                if (!v.type.equals(variables.get(ID))) {
                    error(ctx.getStart().getLine(), "assignment type mismatch");
                }
            } else {
                if (!v.type.equals(globalVariables.get(ID))) {
                    error(ctx.getStart().getLine(), "assignment type mismatch");
                }
            }
            if (v.type.equals("int")) {
                LLVMGenerator.assignInt(resolveScope(ID), v.value);
            }
            if (v.type.equals("real")) {
                LLVMGenerator.assignReal(resolveScope(ID), v.value);
            }
            if (v.type.equals("char")) {
                LLVMGenerator.assignChar(resolveScope(ID), v.value);
            }
        } else {
            try {
                //Get array type and length
                String arrType = variables.get(ID);
                if (arrType == null) {
                    arrType = globalVariables.get(ID);
                }
                String[] split_array_type = arrType.split("\\[");
                String type = split_array_type[0];
                String len = split_array_type[1].split("\\]")[0];
                List<String> values = new ArrayList<>();
                if (argumentsList.size() != Integer.parseInt(len)) {
                    error(ctx.getStart().getLine(), "array size mismatch");
                }
                for (Value v : argumentsList) {
                    if (v.type.equals("ID") && ((variables.containsKey(v.value) && variables.get(v.value).contains(type)) || (globalVariables.containsKey(v.value) && globalVariables.get(v.value).contains(type)))) {
                        if (type.equals("int")) {
                            values.add("%" + LLVMGenerator.loadInt(resolveScope(v.value)));
                        } else if (type.equals("real")) {
                            values.add("%" + LLVMGenerator.loadReal(resolveScope(v.value)));
                        } else if (type.equals("char")) {
                            values.add("%" + LLVMGenerator.loadChar(resolveScope(v.value)));
                        }
                    } else if (v.type.equals("ARRAY_ID") && ((variables.containsKey(v.value) && variables.get(v.value).contains(type)) || (globalVariables.containsKey(v.value) && globalVariables.get(v.value).contains(type)))) {
                        String[] split_array_id = v.value.split("\\[");
                        String id = split_array_id[0];
                        String arrId = split_array_id[1].split("\\]")[0];
                        if (type.equals("int")) {
                            values.add("%" + LLVMGenerator.loadIntArrayValue(resolveScope(id), arrId, len));
                        } else if (type.equals("real")) {
                            values.add("%" + LLVMGenerator.loadRealArrayValue(resolveScope(id), arrId, len));
                        } else if (type.equals("char")) {
                            values.add("%" + LLVMGenerator.loadCharArrayValue(resolveScope(id), arrId, len));
                        }
                    } else if ((v.type.equals("int") || v.type.equals("real") || v.type.equals("char")) && v.type.contains(type)) {
                        values.add(v.value);
                    }
                }
                if (values.size() != Integer.parseInt(len)) {
                    error(ctx.getStart().getLine(), "variables is array are not the same type. Expected: " + type);
                }
                for (int i = 0; i < values.size(); i++) {
                    if (type.equals("int")) {
                        LLVMGenerator.assignArrayIntElement(values.get(i), resolveScope(ID), Integer.toString(i), len);
                    } else if (type.equals("real")) {
                        LLVMGenerator.assignArrayRealElement(values.get(i), resolveScope(ID), Integer.toString(i), len);
                    } else if (type.equals("char")) {
                        LLVMGenerator.assignArrayCharElement(values.get(i), resolveScope(ID), Integer.toString(i), len);
                    }
                }
                argumentsList.clear();
            } catch (ArrayIndexOutOfBoundsException e) {
                error(ctx.getStart().getLine(), "variable is not an array");
            }
        }

    }

    @Override
    public void exitArrayIdAssignment(CzajmalParser.ArrayIdAssignmentContext ctx) {
        //Get array id and element id
        String ARRAY_ID = ctx.ARRAY_ID().getText();
        String[] split_array_id = ARRAY_ID.split("\\[");
        String id = split_array_id[0];
        String arrId = split_array_id[1].split("\\]")[0];
        if (!variables.containsKey(id) && !globalVariables.containsKey(id)) {
            error(ctx.getStart().getLine(), "variable not declared");
        }
        //Get array type and length
        String arrType = variables.get(id);
        if (arrType == null) {
            arrType = globalVariables.get(id);
        }
        String[] split_array_type = arrType.split("\\[");
        String type = split_array_type[0];
        String len = split_array_type[1].split("\\]")[0];

        if (Integer.parseInt(arrId) >= Integer.parseInt(len) || Integer.parseInt(arrId) < 0) {
            error(ctx.getStart().getLine(), "mazanie po pamieci :) (:");
        }

        Value v = stack.pop();
        if (!v.type.equals(type)) {
            error(ctx.getStart().getLine(), "arrayId assignment type mismatch");
        }
        if (v.type.equals("int")) {
            LLVMGenerator.assignArrayIntElement(v.value, resolveScope(id), arrId, len);
        }
        if (v.type.equals("real")) {
            LLVMGenerator.assignArrayRealElement(v.value, resolveScope(id), arrId, len);
        }
    }

    @Override
    public void exitStructElementsAssignment(CzajmalParser.StructElementsAssignmentContext ctx) {
        String stuctureId = ctx.STRUCT_ID().getText();
        String[] split_stuctureId = stuctureId.split("\\.");
        String id = split_stuctureId[0];
        String elementId = split_stuctureId[1];

        if (!variables.containsKey(id) && !globalVariables.containsKey(id)) {
            error(ctx.getStart().getLine(), "variable not declared");
        }

        String structType = variables.get(id);
        if (structType == null) {
            structType = globalVariables.get(id);
        }
        List<String> elementsList = structures.get(structType);
        if (elementsList == null) {
            error(ctx.getStart().getLine(), "unknown error");
        }

        try {
            String value = ctx.struct_types().getText();
            String valueType = ctx.struct_types().INT() == null ? "real" : "int";

            if (!elementsList.get(Integer.parseInt(elementId)).equals(valueType)) {
                error(ctx.getStart().getLine(), "wrong value type");
            }

            LLVMGenerator.assignStructElement(resolveScope(id), elementId, structType, valueType, value);

        } catch (IndexOutOfBoundsException e) {
            error(ctx.getStart().getLine(), "structure element out of bounds");
        }

    }

    @Override
    public void exitStringIdAssignment(CzajmalParser.StringIdAssignmentContext ctx) {
        //Get array id and element id
        String ARRAY_ID = ctx.ARRAY_ID().getText();
        String[] split_array_id = ARRAY_ID.split("\\[");
        String id = split_array_id[0];
        String arrId = split_array_id[1].split("\\]")[0];
        if (!variables.containsKey(id) && !globalVariables.containsKey(id)) {
            error(ctx.getStart().getLine(), "variable not declared");
        }
        //Get array type and length
        String arrType = variables.get(id);
        if (arrType == null) {
            arrType = globalVariables.get(id);
        }
        String[] split_array_type = arrType.split("\\[");
        String type = split_array_type[0];
        String len = split_array_type[1].split("\\]")[0];

        if (Integer.parseInt(arrId) >= Integer.parseInt(len) || Integer.parseInt(arrId) < 0) {
            error(ctx.getStart().getLine(), "mazanie po pamieci :) (:");
        }
        if (!type.equals("char")) {
            error(ctx.getStart().getLine(), "char - string found");
        }

        String c = ctx.STRING().getText();
        if (c.length() > 3) {
            error(ctx.getStart().getLine(), "char - string found");
        }
        int charNum = (int) c.charAt(1);
        LLVMGenerator.assignArrayCharElement(Integer.toString(charNum), resolveScope(id), arrId, len);
    }

    @Override
    public void exitDeclaration(CzajmalParser.DeclarationContext ctx) {
        String ID = ctx.ID().getText();
        CzajmalParser.TypeContext TYPE_ctx = ctx.type();

        if (TYPE_ctx == null) {
            String structName = ctx.structure_name().getText();
            if (structures.containsKey(structName)) {
                if ((!variables.containsKey(ID) && !global) || (!globalVariables.containsKey(ID) && global)) {
                    if (global) {
                        globalVariables.put(ID, structName);
                    } else {
                        variables.put(ID, structName);
                    }
                    LLVMGenerator.declareStructVar(ID, structName, global);
                } else {
                    error(ctx.getStart().getLine(), ", variable already defined: " + ID);
                }
            } else {
                error(ctx.getStart().getLine(), ", type not defined");
            }
        } else {
            String TYPE = ctx.type().getText();
            if ((!variables.containsKey(ID) && !global) || (!globalVariables.containsKey(ID) && global)) {
                if (types.contains(TYPE)) {
                    try {
                        String ARRAY_LEN = ctx.array_declare().getChild(1).getText();

                        if (global) {
                            globalVariables.put(ID, TYPE + '[' + ARRAY_LEN + ']');
                        } else {
                            variables.put(ID, TYPE + '[' + ARRAY_LEN + ']');
                        }
                        if (TYPE.equals("int")) {
                            LLVMGenerator.declareIntArray(ID, ARRAY_LEN, global);
                        } else if (TYPE.equals("real")) {
                            LLVMGenerator.declareRealArray(ID, ARRAY_LEN, global);
                        } else if (TYPE.equals("char")) {
                            LLVMGenerator.declareCharArray(ID, ARRAY_LEN, global);
                        }
                    } catch (NullPointerException e) {
                        if (global) {
                            globalVariables.put(ID, TYPE);
                        } else {
                            variables.put(ID, TYPE);
                        }
                        if (TYPE.equals("int")) {
                            LLVMGenerator.declareInt(ID, global);
                        } else if (TYPE.equals("real")) {
                            LLVMGenerator.declareReal(ID, global);
                        } else if (TYPE.equals("char")) {
                            LLVMGenerator.declareChar(ID, global);
                        }
                    }
                } else {
                    error(ctx.getStart().getLine(), ", unknown variable type: " + TYPE);
                }
            } else {
                error(ctx.getStart().getLine(), ", variable already defined: " + ID);
            }
        }
    }

    @Override
    public void exitCall_function(CzajmalParser.Call_functionContext ctx) {
        String FUNC_NAME = ctx.function_name().getText();
        if (FUNC_NAME.equals("print")) {
            if (argumentsList.size() == 1) {
                Value argument = argumentsList.get(0);
                String ID = argument.value;
                String type = variables.get(ID);
                if (type == null) {
                    type = globalVariables.get(ID);
                }
                if (type != null) {
                    if (type.equals("int")) {
                        LLVMGenerator.printInt(resolveScope(ID));
                    } else if (type.equals("real")) {
                        LLVMGenerator.printReal(resolveScope(ID));
                    } else if (type.equals("char")) {
                        LLVMGenerator.printChar(resolveScope(ID));
                    } else if (type.contains("char[")) {
                        String[] split_array_type = type.split("\\[");
                        String len = split_array_type[1].split("\\]")[0];
                        for (int i = 0; i < Integer.parseInt(len) - 1; i++) {
                            String id = Integer.toString(LLVMGenerator.loadCharArrayValue(resolveScope(ID), Integer.toString(i), len));
                            LLVMGenerator.printSingleChar(id);
                        }
                        String lastEl = Integer.toString(Integer.parseInt(len) - 1);
                        String id = Integer.toString(LLVMGenerator.loadCharArrayValue(resolveScope(ID), lastEl, len));
                        LLVMGenerator.printFinalChar(id);
                    }
                } else {
                    error(ctx.getStart().getLine(), ", unknown variable: " + ID);
                }
            } else {
                error(ctx.getStart().getLine(), ", to many arguments in function print. Expected 1, Got: " + argumentsList.size());
            }
        } else if (FUNC_NAME.equals("read")) {
            if (argumentsList.size() == 1) {
                Value argument = argumentsList.get(0);
                String ID = argument.value;
                String type = variables.get(ID);
                if (type == null) {
                    type = globalVariables.get(ID);
                }
                if (type != null) {
                    if (type.equals("int")) {
                        LLVMGenerator.readInt(resolveScope(ID));
                    } else if (type.equals("real")) {
                        LLVMGenerator.readReal(resolveScope(ID));
                    } else if (type.equals("char")) {
                        LLVMGenerator.readChar(resolveScope(ID));
                    }
                } else {
                    error(ctx.getStart().getLine(), ", unknown variable: " + ID);
                }
            } else {
                error(ctx.getStart().getLine(), ", to many arguments in function read. Expected 1, Got: " + argumentsList.size());
            }
        } else {
            ArrayList<String> args = functions.get(FUNC_NAME);
            if (args == null) {
                error(ctx.getStart().getLine(), ", no such function: " + FUNC_NAME);
            }
            if (argumentsList.size() != args.size() - 1) {
                error(ctx.getStart().getLine(), ", wrong number of arguments");
            }
            if (args.get(0).equals("int")) {
                LLVMGenerator.call(FUNC_NAME, "i32");
            } else if (args.get(0).equals("real")) {
                LLVMGenerator.call(FUNC_NAME, "double");
            } else {
                error(ctx.getStart().getLine(), ", invalid type");
            }
            boolean last = false;
            for (int i = 0; i < argumentsList.size(); i++) {
                if (i == argumentsList.size() - 1) {
                    last = true;
                }
                Value argument = argumentsList.get(i);
                String argType = variables.get(argument.value);
                if (argType == null) {
                    argType = globalVariables.get(argument.value);
                }
                String requiredArg = args.get(i + 1);
                if (argType.equals(requiredArg)) {
                    if (argType.equals("int")) {
                        argType = "i32";
                    } else if (argType.equals("real")) {
                        argType = "double";
                    } else {
                        error(ctx.getStart().getLine(), "wrong type");
                    }
                    LLVMGenerator.callparams(resolveScope(argument.value), argType, last);
                }
            }
        }
        argumentsList.clear();
    }

    @Override
    public void exitFunctionAssignment(CzajmalParser.FunctionAssignmentContext ctx) {
        String id = ctx.ID().getText();
        String type = variables.get(id);
        if (type == null) {
            type = globalVariables.get(id);
        }
        if (type == null) {
            error(ctx.getStart().getLine(), "variable not defined");
        }
        if (type.equals("int")) {
            LLVMGenerator.callfinal(resolveScope(id), "i32");
        } else if (type.equals("real")) {
            LLVMGenerator.callfinal(resolveScope(id), "double");
        } else {
            error(ctx.getStart().getLine(), "wrong type");
        }
    }

    @Override
    public void exitValue(CzajmalParser.ValueContext ctx) {
        try {
            argumentsList.add(new Value("ID", ctx.ID().getText()));
        } catch (NullPointerException e) {

        }
        try {
            argumentsList.add(new Value("int", ctx.INT().getText()));

        } catch (NullPointerException e) {

        }
        try {
            argumentsList.add(new Value("real", ctx.REAL().getText()));

        } catch (NullPointerException e) {

        }
        try {
            String character = ctx.STRING().getText();
            if (character.length() > 3) {
                error(ctx.getStart().getLine(), "char: string found");
            }
            character = character.split("\"")[1];
            int charNum = (int) character.charAt(0);
            argumentsList.add(new Value("char", Integer.toString(charNum)));

        } catch (NullPointerException e) {

        }

        try {
            argumentsList.add(new Value("ARRAY_ID", ctx.ARRAY_ID().getText()));
        } catch (NullPointerException e) {

        }
    }

    @Override
    public void exitInt(CzajmalParser.IntContext ctx) {
        stack.push(new Value("int", ctx.INT().getText()));
    }

    @Override
    public void exitReal(CzajmalParser.RealContext ctx) {
        stack.push(new Value("real", ctx.REAL().getText()));
    }

    @Override
    public void exitString(CzajmalParser.StringContext ctx) {
        stack.push(new Value("string", ctx.STRING().getText()));
    }

    @Override
    public void exitId(CzajmalParser.IdContext ctx) {
        String ID = ctx.ID().getText();
        if (variables.containsKey(ID) || globalVariables.containsKey(ID)) {
            String type = variables.get(ID);
            if (type == null) {
                type = globalVariables.get(ID);
            }
            int reg = -1;
            if (type.equals("int")) {
                reg = LLVMGenerator.loadInt(resolveScope(ID));
            } else if (type.equals("real")) {
                reg = LLVMGenerator.loadReal(resolveScope(ID));
            } else if (type.equals("char")) {
                reg = LLVMGenerator.loadChar(resolveScope(ID));
            }
            stack.push(new Value(type, "%" + reg));
        } else {
            error(ctx.getStart().getLine(), "no such variable");
        }
    }

    @Override
    public void exitArray_id(CzajmalParser.Array_idContext ctx) {
        String ARRAY_ID = ctx.ARRAY_ID().getText();
        String[] split_array_id = ARRAY_ID.split("\\[");
        String id = split_array_id[0];
        String arrId = split_array_id[1].split("\\]")[0];
        if (variables.containsKey(id) || globalVariables.containsKey(id)) {
            String arrType = variables.get(id);
            if (arrType == null) {
                arrType = globalVariables.get(id);
            }
            String[] split_array_type = arrType.split("\\[");
            String type = split_array_type[0];
            String len = split_array_type[1].split("\\]")[0];
            int reg = -1;
            if (type.equals("int")) {
                reg = LLVMGenerator.loadIntArrayValue(resolveScope(id), arrId, len);
            } else if (type.equals("real")) {
                reg = LLVMGenerator.loadRealArrayValue(resolveScope(id), arrId, len);
            } else if (type.equals("char")) {
                reg = LLVMGenerator.loadCharArrayValue(resolveScope(id), arrId, len);
            }
            stack.push(new Value(type, "%" + reg));
        } else {
            error(ctx.getStart().getLine(), "no such array");
        }
    }

    @Override
    public void exitStruct_id(CzajmalParser.Struct_idContext ctx) {
        String stuctureId = ctx.STRUCT_ID().getText();
        String[] split_stuctureId = stuctureId.split("\\.");
        String id = split_stuctureId[0];
        String elementId = split_stuctureId[1];

        if (!variables.containsKey(id) && !globalVariables.containsKey(id)) {
            error(ctx.getStart().getLine(), "variable not declared");
        }

        String structType = variables.get(id);
        if (structType == null) {
            structType = globalVariables.get(id);
        }
        List<String> elementsList = structures.get(structType);
        if (elementsList == null) {
            error(ctx.getStart().getLine(), "unknown error");
        }
        try {
            String type = elementsList.get(Integer.parseInt(elementId));
            int reg = -1;

            reg = LLVMGenerator.loadFromStruct(resolveScope(id), elementId, structType, type);
            stack.push(new Value(type, "%" + reg));
        } catch (IndexOutOfBoundsException e) {
            error(ctx.getStart().getLine(), "structure element out of bounds");
        }
    }

    @Override
    public void exitAdd(CzajmalParser.AddContext ctx) {
        Value v1 = stack.pop();
        Value v2 = stack.pop();
        if (v1.type.equals(v2.type)) {
            if (v1.type.equals("int")) {
                LLVMGenerator.addInt(v1.value, v2.value);
                stack.push(new Value("int", "%" + (LLVMGenerator.reg - 1)));
            }
            if (v1.type.equals("real")) {
                LLVMGenerator.addReal(v1.value, v2.value);
                stack.push(new Value("real", "%" + (LLVMGenerator.reg - 1)));
            }
        } else {
            error(ctx.getStart().getLine(), "add type mismatch");
        }
    }

    @Override
    public void exitMult(CzajmalParser.MultContext ctx) {
        Value v1 = stack.pop();
        Value v2 = stack.pop();
        if (v1.type.equals(v2.type)) {
            if (v1.type.equals("int")) {
                LLVMGenerator.multInt(v1.value, v2.value);
                stack.push(new Value("int", "%" + (LLVMGenerator.reg - 1)));
            }
            if (v1.type.equals("real")) {
                LLVMGenerator.multReal(v1.value, v2.value);
                stack.push(new Value("real", "%" + (LLVMGenerator.reg - 1)));
            }
        } else {
            error(ctx.getStart().getLine(), "multiplication type mismatch");
        }
    }

    @Override
    public void exitDel(CzajmalParser.DelContext ctx) {
        Value v1 = stack.pop();
        Value v2 = stack.pop();
        if (v1.type.equals(v2.type)) {
            if (v1.type.equals("int")) {
                LLVMGenerator.delInt(v2.value, v1.value);
                stack.push(new Value("int", "%" + (LLVMGenerator.reg - 1)));
            }
            if (v1.type.equals("real")) {
                LLVMGenerator.delReal(v2.value, v1.value);
                stack.push(new Value("real", "%" + (LLVMGenerator.reg - 1)));
            }
        } else {
            error(ctx.getStart().getLine(), "subtraction type mismatch");
        }
    }

    @Override
    public void exitDiv(CzajmalParser.DivContext ctx) {
        Value v1 = stack.pop();
        Value v2 = stack.pop();
        if (v1.type.equals(v2.type)) {
            if (v1.type.equals("int")) {
                LLVMGenerator.divInt(v2.value, v1.value);
                stack.push(new Value("int", "%" + (LLVMGenerator.reg - 1)));
            }
            if (v1.type.equals("real")) {
                LLVMGenerator.divReal(v2.value, v1.value);
                stack.push(new Value("real", "%" + (LLVMGenerator.reg - 1)));
            }
        } else {
            error(ctx.getStart().getLine(), "division type mismatch");
        }
    }

    @Override
    public void enterBlockif(CzajmalParser.BlockifContext ctx) {
        LLVMGenerator.ifstart();
    }

    @Override
    public void exitBlockif(CzajmalParser.BlockifContext ctx) {
        LLVMGenerator.ifend();
    }

    @Override
    public void exitBlockelse(CzajmalParser.BlockelseContext ctx) {
        LLVMGenerator.elseend();
    }

    @Override
    public void exitCondition(CzajmalParser.ConditionContext ctx) {
        String ID = ctx.ID().getText();
        String operation = ctx.if_operation().getText();
        String value = ctx.comparable_value().getText();

        if (value.matches("^[a-zA-Z]+$")) {
            if ((globalVariables.containsKey(ID) || variables.containsKey(ID)) && (globalVariables.containsKey(value) || variables.containsKey(value))) {
                String type1 = "";
                if (globalVariables.containsKey(ID)) {
                    type1 = globalVariables.get(ID);
                } else if (variables.containsKey(ID)) {
                    type1 = variables.get(ID);
                }
                String type2 = "";
                if (globalVariables.containsKey(value)) {
                    type2 = globalVariables.get(value);
                } else if (variables.containsKey(value)) {
                    type2 = variables.get(value);
                }
                if (type1.equals(type2)) {
                    String operation_text = "";
                    switch (operation) {
                        case "==":
                            operation_text = "eq";
                            break;
                        case "!=":
                            operation_text = "ne";
                            break;
                        case "<":
                            operation_text = "slt";
                            break;
                        case ">":
                            operation_text = "sgt";
                            break;
                        case ">=":
                            operation_text = "sge";
                            break;
                        case "<=":
                            operation_text = "sle";
                            break;
                        default:
                            operation_text = "error";
                            break;
                    }
                    if (operation_text.equals("error")) {
                        error(ctx.getStart().getLine(), "unsupported operation");
                    }
                    if (type1.equals("int")) {
                        LLVMGenerator.icmp_vars(resolveScope(ID), resolveScope(value), "i32", operation_text);
                    } else if (type1.equals("real")) {
                        LLVMGenerator.icmp_vars(resolveScope(ID), resolveScope(value), "double", operation_text);
                    } else {
                        error(ctx.getStart().getLine(), "unsupported type");
                    }
                } else {
                    error(ctx.getStart().getLine(), "varaibles have different types");
                }
            } else {
                error(ctx.getStart().getLine(), "variable not defined");
            }

        } else {

            if (globalVariables.containsKey(ID) || variables.containsKey(ID)) {
                String type = "";
                if (globalVariables.containsKey(ID)) {
                    type = globalVariables.get(ID);
                } else if (variables.containsKey(ID)) {
                    type = variables.get(ID);
                }

                if ((type.equals("int") && value.contains("\\.")) || (type.equals("real") && !value.contains("\\."))) {
                    error(ctx.getStart().getLine(), "wrong type comparison");
                }
                String operation_text = "";
                switch (operation) {
                    case "==":
                        operation_text = "eq";
                        break;
                    case "!=":
                        operation_text = "ne";
                        break;
                    case "<":
                        operation_text = "ult";
                        break;
                    case ">":
                        operation_text = "ugt";
                        break;
                    case ">=":
                        operation_text = "uge";
                        break;
                    case "<=":
                        operation_text = "ule";
                        break;
                    default:
                        operation_text = "error";
                        break;
                }
                if (operation_text.equals("error")) {
                    error(ctx.getStart().getLine(), "unsupported operation");
                }
                if (type.equals("int")) {
                    LLVMGenerator.icmp_constant(resolveScope(ID), value, "i32", operation_text);
                } else if (type.equals("real")) {
                    LLVMGenerator.icmp_constant(resolveScope(ID), value, "double", operation_text);
                } else {
                    error(ctx.getStart().getLine(), "unsupported type");
                }
            } else {
                error(ctx.getStart().getLine(), "variable not defined");
            }
        }
    }

    @Override
    public void enterLoopblock(CzajmalParser.LoopblockContext ctx) {
        String ID = ctx.condition().getChild(0).getText();
        LLVMGenerator.loopstart(resolveScope(ID));
    }

    @Override
    public void enterBlockfor(CzajmalParser.BlockforContext ctx) {
        LLVMGenerator.loopblockstart();
    }

    @Override
    public void exitBlockfor(CzajmalParser.BlockforContext ctx) {
        LLVMGenerator.loopend();
    }

    @Override
    public void exitStruct_header(CzajmalParser.Struct_headerContext ctx) {
        String ID = ctx.ID().getText();
        if (structures.containsKey(ID) || types.contains(ID)) {
            error(ctx.getStart().getLine(), "type already defined");
        } else {
            structures.put(ID, new ArrayList<String>());
            LLVMGenerator.structureHeaderExit(ID);
        }
    }

    @Override
    public void exitStructure(CzajmalParser.StructureContext ctx) {

        String structName = ctx.struct_header().ID().getText();

        CzajmalParser.StructparamsContext nctx = ctx.structparams();
        while (nctx != null) {
            CzajmalParser.StructparamsContext nnctx = nctx.structparams();
            String type = nctx.type().getText();
            if (!types.contains(type)) {
                error(ctx.getStart().getLine(), "type not supported");
            } else {
                String llvmType = "";
                if (type.equals("int")) {
                    llvmType = "i32";
                } else if (type.equals("real")) {
                    llvmType = "double";
                }
                if (llvmType.equals("")) {
                    error(ctx.getStart().getLine(), "type not supported");
                }
                structures.get(structName).add(type);
                boolean last = nnctx == null ? true : false;
                LLVMGenerator.createStructparam(llvmType, last);
            }
            if (nnctx == null) {
                LLVMGenerator.closeStruct();
                break;
            }

            nctx = nnctx;
        }
    }

    @Override
    public void enterFunction(CzajmalParser.FunctionContext ctx) {
        global = false;
        String id = ctx.ID().getText();
        String type = ctx.type().getText();
        functions.put(id, new ArrayList<String>());
        functions.get(id).add(type);
        if (type.equals("int")) {
            type = "i32";
        } else if (type.equals("real")) {
            type = "double";
        } else {
            error(ctx.getStart().getLine(), "unsupported return parameter");
        }
        LLVMGenerator.functionstart(id, type);
        CzajmalParser.FparamsContext fp = ctx.fparams();
        while (fp != null) {
            CzajmalParser.FparamsContext nfp = fp.fparams();
            String paramId = fp.ID().getText();
            String paramType = fp.type().getText();
            variables.put(paramId, paramType);
            functions.get(id).add(paramType);
            boolean last = false;
            if (nfp == null) {
                last = true;
            }
            if (paramType.equals("int")) {
                paramType = "i32";
            } else if (paramType.equals("real")) {
                paramType = "double";
            } else {
                error(ctx.getStart().getLine(), "unsupported function parameter");
            }
            LLVMGenerator.functionparams(paramId, paramType, last);
            fp = nfp;
        }
    }

    @Override
    public void exitReturnstatement(CzajmalParser.ReturnstatementContext ctx) {
        String ID = ctx.ID().getText();
        String TYPE = variables.get(ID);
        if (TYPE == null) {
            error(ctx.getStart().getLine(), "variable not defined");
        }
        if (TYPE.equals("int")) {
            LLVMGenerator.loadInt(resolveScope(ID));
            TYPE = "i32";
        } else if (TYPE.equals("real")) {
            LLVMGenerator.loadReal(resolveScope(ID));
            TYPE = "double";
        } else {
            error(ctx.getStart().getLine(), "unsupported return parameter");
        }
        LLVMGenerator.functionend(TYPE);
        variables = new HashMap<String, String>();
        global = true;
    }

    public String resolveScope(String ID) {
        String id;
        if (global) {
            id = "@" + ID;
        } else {
            if (!variables.containsKey(ID)) {
                id = "@" + ID;
            } else {
                id = "%" + ID;
            }
        }
        return id;
    }

//    public String set_variable(String ID){
//        String id;
//        if( global ){
//            if( ! globalnames.contains(ID) ) {
//                globalnames.add(ID);
//                LLVMGenerator.declare(ID, true);
//            }
//            id = "@"+ID;
//        } else {
//            if( ! localnames.contains(ID) ) {
//                localnames.add(ID);
//                LLVMGenerator.declare(ID, false);
//            }
//            id = "%"+ID;
//        }
//        return id;
//    }

    void error(int line, String msg) {
        System.err.println("Error, line " + line + ", " + msg);
        System.exit(1);
    }

}
