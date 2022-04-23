
import java.util.HashSet;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

class ArgumentType {
    public String type;
    public String value;

    public ArgumentType(String type, String value) {
        this.type = type;
        this.value = value;
    }
}

public class LLVMActions extends CzajmalBaseListener {

    HashSet<String> variables = new HashSet<String>();
    HashSet<String> types = new HashSet<String>() {{
        add("int");
        add("real");
    }};
    HashSet<String> definedFunctions = new HashSet<String>() {{
        add("print");
        add("read");
    }};
    List<ArgumentType> argumentsList = new ArrayList<ArgumentType>();

    @Override
    public void exitDeclaration(CzajmalParser.DeclarationContext ctx) {
        String ID = ctx.ID().getText();
        String TYPE = ctx.type().getText();
        if (!variables.contains(ID)) {
            if (types.contains(TYPE)) {
                variables.add(ID);
                if (TYPE.equals("int")) {
                    LLVMGenerator.declareInt(ID);
                } else if (TYPE.equals("real")) {
                    LLVMGenerator.declareInt(ID);
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
                ArgumentType argument = argumentsList.get(0);
                if (argument.type.equals("ID")) {
                    String ID = argument.value;
                    if (variables.contains(ID)) {
                        LLVMGenerator.print(ID);
                    } else {
                        ctx.getStart().getLine();
                        System.err.println("Line " + ctx.getStart().getLine() + ", unknown variable: " + ID);
                    }
                }
            } else {
                ctx.getStart().getLine();
                System.err.println("Line " + ctx.getStart().getLine() + ", to many arguments in function print. Expected 1, Got: " + argumentsList.size());

            }
        } else if (FUNC_NAME.equals("read")) {
            if (argumentsList.size() == 1) {
                ArgumentType argument = argumentsList.get(0);
                if (argument.type.equals("ID")) {
                    String ID = argument.value;
                    if (variables.contains(ID)) {
                        LLVMGenerator.read(ID);
                    } else {
                        ctx.getStart().getLine();
                        System.err.println("Line " + ctx.getStart().getLine() + ", unknown variable: " + ID);
                    }
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
            argumentsList.add(new ArgumentType("ID", ctx.ID().getText()));
        }
        catch (NullPointerException e) {

        }
        try {
            argumentsList.add(new ArgumentType("ID", ctx.INT().getText()));

        } catch (NullPointerException e ) {

        }
        try {
            argumentsList.add(new ArgumentType("ID", ctx.REAL().getText()));

        } catch (NullPointerException e) {

        }
    }
}
