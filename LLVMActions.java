
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

    HashMap<String, String> variables = new HashMap<String, String>();
    HashSet<String> types = new HashSet<String>() {{
        add("int");
        add("real");
    }};
    HashSet<String> definedFunctions = new HashSet<String>() {{
        add("print");
        add("read");
    }};
    List<Value> argumentsList = new ArrayList<Value>();
    Stack<Value> stack = new Stack<Value>();

    @Override
    public void exitDeclarationAssignment(CzajmalParser.DeclarationAssignmentContext ctx) {
        String ID = ctx.declaration().getChild(1).getText();
        String ArrayOperation = ctx.operation().getChild(0).getText();
        if (ArrayOperation.charAt(0) != '[') {
            if (!variables.containsKey(ID)) {
                error(ctx.getStart().getLine(), "variable not declared");
            }
            Value v = stack.pop();
            if (!v.type.equals(variables.get(ID))) {
                error(ctx.getStart().getLine(), "assignment type mismatch");
            }
            if (v.type.equals("int")) {
                LLVMGenerator.assignInt(ID, v.value);
            }
            if (v.type.equals("real")) {
                LLVMGenerator.assignReal(ID, v.value);
            }
        } else {
            try {
                ID = ctx.declaration().getChild(2).getText();
                //Get array type and length
                String arrType = variables.get(ID);
                String[] split_array_type = arrType.split("\\[");
                String type = split_array_type[0];
                String len = split_array_type[1].split("\\]")[0];
                List<String> values = new ArrayList<>();

                if (argumentsList.size() > Integer.parseInt(len)) {
                    error(ctx.getStart().getLine(), "too big array");
                }
                for (Value v : argumentsList) {

                    if (v.type.equals("ID") && variables.containsKey(v.value)) {
                        if (type.equals("int")) {
                            values.add("%" + LLVMGenerator.loadInt(v.value));
                        } else if (type.equals("real")) {
                            values.add("%" + LLVMGenerator.loadReal(v.value));
                        }
                    } else if (v.type.equals("ARRAY_ID") && variables.containsKey(v.value)) {
                        String[] split_array_id = v.value.split("\\[");
                        String id = split_array_id[0];
                        String arrId = split_array_id[1].split("\\]")[0];
                        if (type.equals("int")) {
                            values.add("%" + LLVMGenerator.loadIntArrayValue(id, arrId, len));
                        } else if (type.equals("real")) {
                            values.add("%" + LLVMGenerator.loadRealArrayValue(id, arrId, len));
                        }
                    } else if (v.type.equals("int") || v.type.equals("real")) {
                        values.add(v.value);
                    }
                }
                for (int i = 0; i < values.size(); i++) {
                    if (type.equals("int")) {
                        LLVMGenerator.assignArrayIntElement(values.get(i), ID, Integer.toString(i), len);
                    } else if (type.equals("real")) {
                        LLVMGenerator.assignArrayRealElement(values.get(i), ID, Integer.toString(i), len);
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

        if (!variables.containsKey(ID)) {
            error(ctx.getStart().getLine(), "variable not declared");
        }
        String ArrayOperation = ctx.operation().getChild(0).getText();
        if (ArrayOperation.charAt(0) != '[') {
            Value v = stack.pop();
            if (!v.type.equals(variables.get(ID))) {
                error(ctx.getStart().getLine(), "assignment type mismatch");
            }
            if (v.type.equals("int")) {
                LLVMGenerator.assignInt(ID, v.value);
            }
            if (v.type.equals("real")) {
                LLVMGenerator.assignReal(ID, v.value);
            }
        } else {
            try {
                //Get array type and length
                String arrType = variables.get(ID);
                String[] split_array_type = arrType.split("\\[");
                String type = split_array_type[0];
                String len = split_array_type[1].split("\\]")[0];
                List<String> values = new ArrayList<>();
                if (argumentsList.size() != Integer.parseInt(len)) {
                    error(ctx.getStart().getLine(), "array size mismatch");
                }
                for (Value v : argumentsList) {

                    if (v.type.equals("ID") && variables.containsKey(v.value)) {
                        if (type.equals("int")) {
                            values.add("%" + LLVMGenerator.loadInt(v.value));
                        } else if (type.equals("real")) {
                            values.add("%" + LLVMGenerator.loadReal(v.value));
                        }
                    } else if (v.type.equals("ARRAY_ID") && variables.containsKey(v.value)) {
                        String[] split_array_id = v.value.split("\\[");
                        String id = split_array_id[0];
                        String arrId = split_array_id[1].split("\\]")[0];
                        if (type.equals("int")) {
                            values.add("%" + LLVMGenerator.loadIntArrayValue(id, arrId, len));
                        } else if (type.equals("real")) {
                            values.add("%" + LLVMGenerator.loadRealArrayValue(id, arrId, len));
                        }
                    } else if (v.type.equals("int") || v.type.equals("real")) {
                        values.add(v.value);
                    }
                }
                for (int i = 0; i < values.size(); i++) {
                    if (type.equals("int")) {
                        LLVMGenerator.assignArrayIntElement(values.get(i), ID, Integer.toString(i), len);
                    } else if (type.equals("real")) {
                        LLVMGenerator.assignArrayRealElement(values.get(i), ID, Integer.toString(i), len);
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
        if (!variables.containsKey(id)) {
            error(ctx.getStart().getLine(), "variable not declared");
        }
        //Get array type and length
        String arrType = variables.get(id);
        String[] split_array_type = arrType.split("\\[");
        String type = split_array_type[0];
        String len = split_array_type[1].split("\\]")[0];

        if (Integer.parseInt(arrId) >= Integer.parseInt(len) || Integer.parseInt(arrId) < 0){
            error(ctx.getStart().getLine(), "mazanie po pamieci :) (:");
        }

        Value v = stack.pop();
        if (!v.type.equals(type)) {
            error(ctx.getStart().getLine(), "arrayId assignment type mismatch");
        }
        if (v.type.equals("int")) {
            LLVMGenerator.assignArrayIntElement(v.value, id, arrId, len);
        }
        if (v.type.equals("real")) {
            LLVMGenerator.assignArrayRealElement(v.value, id, arrId, len);
        }
    }

    @Override
    public void exitDeclaration(CzajmalParser.DeclarationContext ctx) {
        String ID = ctx.ID().getText();
        String TYPE = ctx.type().getText();

        if (!variables.containsKey(ID)) {
            if (types.contains(TYPE)) {
                try {
                    String ARRAY_LEN = ctx.array_declare().getChild(1).getText();
                    variables.put(ID, TYPE + '[' + ARRAY_LEN + ']');
                    if (TYPE.equals("int")) {
                        LLVMGenerator.declareIntArray(ID, ARRAY_LEN);
                    } else if (TYPE.equals("real")) {
                        LLVMGenerator.declareRealArray(ID, ARRAY_LEN);
                    }
                } catch (NullPointerException e) {
                    variables.put(ID, TYPE);
                    if (TYPE.equals("int")) {
                        LLVMGenerator.declareInt(ID);
                    } else if (TYPE.equals("real")) {
                        LLVMGenerator.declareReal(ID);
                    }
                }
            } else {
                ctx.getStart().getLine();
                System.err.println("Line " + ctx.getStart().getLine() + ", unknown variable type: " + TYPE);
            }
        } else {
            ctx.getStart().getLine();
            System.err.println("Line " + ctx.getStart().getLine() + ", variable already defined: " + ID);
        }
    }

    @Override
    public void exitProg(CzajmalParser.ProgContext ctx) {
        System.out.println(LLVMGenerator.generate());
    }


    @Override
    public void exitCall_function(CzajmalParser.Call_functionContext ctx) {
        String FUNC_NAME = ctx.function_name().getText();
        if (FUNC_NAME.equals("print")) {
            if (argumentsList.size() == 1) {
                Value argument = argumentsList.get(0);
                String ID = argument.value;
                String type = variables.get(ID);
                if (type != null) {
                    if (type.equals("int")) {
                        LLVMGenerator.printInt(ID);
                    } else if (type.equals("real")) {
                        LLVMGenerator.printReal(ID);
                    }
                } else {
                    ctx.getStart().getLine();
                    System.err.println("Line " + ctx.getStart().getLine() + ", unknown variable: " + ID);
                }
            } else {
                ctx.getStart().getLine();
                System.err.println("Line " + ctx.getStart().getLine() + ", to many arguments in function print. Expected 1, Got: " + argumentsList.size());

            }
        } else if (FUNC_NAME.equals("read")) {
            if (argumentsList.size() == 1) {
                Value argument = argumentsList.get(0);
                String ID = argument.value;
                String type = variables.get(ID);
                if (type != null) {
                    if (type.equals("int")) {
                        LLVMGenerator.readInt(ID);
                    } else if (type.equals("real")) {
                        LLVMGenerator.readReal(ID);
                    }
                } else {
                    ctx.getStart().getLine();
                    System.err.println("Line " + ctx.getStart().getLine() + ", unknown variable: " + ID);
                }
            } else {
                ctx.getStart().getLine();
                System.err.println("Line " + ctx.getStart().getLine() + ", to many arguments in function print. Expected 1, Got: " + argumentsList.size());

            }
        }
        argumentsList.clear();
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
    public void exitId(CzajmalParser.IdContext ctx) {
        String ID = ctx.ID().getText();
        if (variables.containsKey(ID)) {
            String type = variables.get(ID);
            int reg = -1;
            if (type.equals("int")) {
                reg = LLVMGenerator.loadInt(ID);
            } else if (type.equals("real")) {
                reg = LLVMGenerator.loadReal(ID);
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
        if (variables.containsKey(id)) {
            String arrType = variables.get(id);
            String[] split_array_type = arrType.split("\\[");
            String type = split_array_type[0];
            String len = split_array_type[1].split("\\]")[0];
            int reg = -1;
            if (type.equals("int")) {
                reg = LLVMGenerator.loadIntArrayValue(id, arrId, len);
            } else if (type.equals("real")) {
                reg = LLVMGenerator.loadRealArrayValue(id, arrId, len);
            }
            stack.push(new Value(type, "%" + reg));
        } else {
            error(ctx.getStart().getLine(), "no such array");
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


    void error(int line, String msg) {
        System.err.println("Error, line " + line + ", " + msg);
        System.exit(1);
    }

}
