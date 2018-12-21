import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.util.LinkedHashMap;
import java.util.Map;

public class FirstTraverse extends MiniJavaBaseListener {
    Scope globalScope, currentScope;
    ParseTreeProperty<Scope> scopes = new ParseTreeProperty<>();
    Map<ClassSymbol, Scope> classScopes = new LinkedHashMap<>();


    @Override
    public void enterBraceStatement(MiniJavaParser.BraceStatementContext ctx) {
        currentScope = new Scope(currentScope);
        saveScope(ctx, currentScope);
    }

    @Override
    public void exitBraceStatement(MiniJavaParser.BraceStatementContext ctx) {
        currentScope = currentScope.getOuterScope();
    }
    public ParseTreeProperty<Scope> getScopes() {
        return scopes;
    }

    public Scope getGlobalScope() {
        return globalScope;
    }

    public Map<ClassSymbol, Scope> getClassScopes() {
        return classScopes;
    }

    @Override
    public void enterProg(MiniJavaParser.ProgContext ctx) {
        globalScope = new Scope(null);
        currentScope = globalScope;
        saveScope(ctx, currentScope);
    }

    @Override
    public void exitProg(MiniJavaParser.ProgContext ctx) {
        currentScope = currentScope.getOuterScope();
    }

    @Override
    public void enterMainClass(MiniJavaParser.MainClassContext ctx) {

        String className = ctx.ID(0).getText();
        ClassSymbol newClass = new ClassSymbol(className);
        currentScope.define(newClass);

        currentScope = new Scope(currentScope, newClass);
        String argsName = ctx.ID(1).getText();
        currentScope.define(new VarSymbol(argsName, VarType.typeStringArray));
        saveScope(ctx, currentScope);
        classScopes.put(newClass, currentScope);
    }

    @Override
    public void exitMainClass(MiniJavaParser.MainClassContext ctx) {
        currentScope = currentScope.getOuterScope();
    }

    @Override
    public void enterClassDeclaration(MiniJavaParser.ClassDeclarationContext ctx) {
        String className = ctx.ID(0).getText();
        ClassSymbol newClass = new ClassSymbol(className);
        currentScope.define(newClass);
        currentScope = new Scope(currentScope, newClass);
        saveScope(ctx, currentScope);
        classScopes.put(newClass, currentScope);
    }

    @Override
    public void exitClassDeclaration(MiniJavaParser.ClassDeclarationContext ctx) {
        currentScope = currentScope.getOuterScope();
    }

    @Override
    public void enterMethodDeclaration(MiniJavaParser.MethodDeclarationContext ctx) {
        String returnTypeName = ctx.type(0).getText();
        String methodName = ctx.ID(0).getText();
        VarType returnType = getTypeFromTypeName(returnTypeName);
        currentScope.define(new MethodSymbol(methodName, returnType));
        currentScope = new Scope(currentScope);
        saveScope(ctx, currentScope);
    }

    @Override
    public void exitMethodDeclaration(MiniJavaParser.MethodDeclarationContext ctx) {
        currentScope = currentScope.getOuterScope();
    }


    void saveScope(ParserRuleContext ctx, Scope s) {
        scopes.put(ctx, s);
    }

    static VarType getTypeFromTypeName(String typeName) {
        if (typeName.equals("int[]")) {
            return VarType.typeIntArray;
        }
        if (typeName.equals("boolean")) {
            return VarType.typeBoolean;
        }
        if (typeName.equals("int")) {
            return VarType.typeInt;
        }
        return VarType.typeClass;
    }

    static void printError(Token t, String msg) {
        System.err.printf("line %d: %s \n", t.getLine(), msg);
    }
}
