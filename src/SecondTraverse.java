import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;
import java.util.Map;

public class SecondTraverse extends MiniJavaBaseListener {
    Scope globalScope,currentScope;
    ParseTreeProperty<Scope> scopes = new ParseTreeProperty<>();
    Map<ClassSymbol, Scope> classScopes;
    ParseTreeProperty<VarType> ExpressionType = new ParseTreeProperty<>();
    ParseTreeProperty<ClassSymbol> ExpressionClassSymbol = new ParseTreeProperty<>();

    SecondTraverse(Scope globalScope, ParseTreeProperty<Scope> scopes, Map<ClassSymbol, Scope> classScopes) {
        this.globalScope = globalScope;
        this.scopes = scopes;
        this.classScopes = classScopes;
    }
    static VarType getTypeFromTypeName(String typeName) {
        if (typeName.equals("int[]")) {
            return VarType.typeIntArray;
        }
        if (typeName.equals("boolean")) {
            return VarType.typeInt;//bool=int
        }
        if (typeName.equals("int")) {
            return VarType.typeInt;
        }
        return VarType.typeClass;
    }

    @Override
    public void exitAndExpression(MiniJavaParser.AndExpressionContext ctx) { ExpressionType.put(ctx, VarType.typeBoolean); }

    @Override
    public void exitLessExpression(MiniJavaParser.LessExpressionContext ctx) { ExpressionType.put(ctx, VarType.typeBoolean); }

    @Override
    public void exitPlusMinusExpression(MiniJavaParser.PlusMinusExpressionContext ctx) { ExpressionType.put(ctx, VarType.typeInt); }

    @Override
    public void exitMultiplyExpression(MiniJavaParser.MultiplyExpressionContext ctx) { ExpressionType.put(ctx, VarType.typeInt); }


    @Override
    public void enterProg(MiniJavaParser.ProgContext ctx) {
        currentScope = globalScope;
    }

    @Override
    public void exitProg(MiniJavaParser.ProgContext ctx) {
        currentScope = currentScope.getOuterScope();
    }

    @Override
    public void enterMainClass(MiniJavaParser.MainClassContext ctx) {
        currentScope = scopes.get(ctx);
    }

    @Override
    public void exitMainClass(MiniJavaParser.MainClassContext ctx) {
        currentScope = currentScope.getOuterScope();
    }

    @Override
    public void enterClassDeclaration(MiniJavaParser.ClassDeclarationContext ctx) {
        try {
            currentScope = scopes.get(ctx);
            List<TerminalNode> IDs = ctx.ID();
            if (IDs.size() > 1) {
                // Check the inherited class
                String inheritedClassName = ctx.ID(1).getText();
                if (inheritedClassName != null && currentScope.lookup(inheritedClassName) == null) {
                    printError(ctx.ID(1).getSymbol(), "class '" + inheritedClassName + "' is not'defined.");
                }
            }
        } catch (Exception e) {
            printError(ctx.getStart(), "Unexpected error!");
        }
    }

    @Override
    public void exitClassDeclaration(MiniJavaParser.ClassDeclarationContext ctx) {
        currentScope = currentScope.getOuterScope();
    }

    @Override
    public void enterVarDeclaration(MiniJavaParser.VarDeclarationContext ctx) {
        try {
            String varName = ctx.ID().getText();
            String typeName = ctx.type().getText();
            VarType type = getTypeFromTypeName(typeName);
            if (!type.equals(VarType.typeClass)) {
                currentScope.define(new VarSymbol(varName, type));
            } else {
                Symbol classSymbol = currentScope.lookup(typeName);
                if (classSymbol == null) {
                    // Class Symbol doesn't exist.
                    printError(ctx.type().getStart(), "The class " + typeName + " doesn't exist.");
                } else if (classSymbol.getSymbolType() != SymbolType.classSymbol) {
                    // Type is not a class symbol.
                    printError(ctx.type().getStart(), typeName + " is not a class symbol.");
                } else {
                    currentScope.define(new VarSymbol(varName, (ClassSymbol) classSymbol));
                }
            }
        } catch (Exception e) {
            printError(ctx.getStart(), "Unexpected error!");
        }
    }

    @Override
    public void enterMethodDeclaration(MiniJavaParser.MethodDeclarationContext ctx) {
        try {
            currentScope = scopes.get(ctx);
            List<MiniJavaParser.TypeContext> parameterTypes = ctx.type();
            List<TerminalNode> parameterIDs = ctx.ID();
            for (int i = 1; i < parameterTypes.size(); ++i) {
                TerminalNode parameterID = parameterIDs.get(i);
                MiniJavaParser.TypeContext parameterType = parameterTypes.get(i);
                VarType parameterVarType = getTypeFromTypeName(parameterType.getText());
                if (!parameterVarType.equals(VarType.typeClass)) {
                    currentScope.define(new VarSymbol(parameterID.getText(), parameterVarType));
                } else {
                    Symbol classSymbol = currentScope.lookup(parameterType.getText());
                    if (classSymbol == null) {
                        printError(parameterType.getStart(), "The class " + parameterType.getText() + " doesn't exist.");
                    } else if (classSymbol.getSymbolType() != SymbolType.classSymbol) {
                        printError(parameterType.getStart(), parameterType.getText() + " is not a class symbol.");
                    } else {
                        currentScope.define(new VarSymbol(parameterID.getText(), (ClassSymbol) classSymbol));
                    }
                }
            }
        } catch (Exception e) {
            printError(ctx.getStart(), "Unexpected error!");
        }
    }

    @Override
    public void exitMethodDeclaration(MiniJavaParser.MethodDeclarationContext ctx) {
        currentScope = currentScope.getOuterScope();
    }

    @Override
    public void enterBraceStatement(MiniJavaParser.BraceStatementContext ctx) {
        currentScope = scopes.get(ctx);
    }

    @Override
    public void exitBraceStatement(MiniJavaParser.BraceStatementContext ctx) {
        currentScope = currentScope.getOuterScope();
    }


    @Override
    public void exitAssignStatement(MiniJavaParser.AssignStatementContext ctx) {
        try {
            String ID = ctx.ID().getText();
            Symbol IDSymbol = currentScope.lookup(ID);
            if (IDSymbol == null) {
                printError(ctx.ID().getSymbol(), "Symbol '" + ID + "' is not defined.");
            } else if (IDSymbol.getSymbolType() != SymbolType.varSymbol) {
                printError(ctx.ID().getSymbol(), "Symbol " + ID + " is not an variable symbol.");
            } else if (((VarSymbol) IDSymbol).getVarType() != ExpressionType.get(ctx.Expression()) ) {
                printError(ctx.ID().getSymbol(), " '" + ID + "' is not matched with other variables.");
            }
        } catch (Exception e) {
            printError(ctx.getStart(), "Unexpected error!");
        }
    }
  
    

    @Override
    public void exitCallExpression(MiniJavaParser.CallExpressionContext ctx) {
        try {
            String methodName = ctx.ID().getText();
            MiniJavaParser.ExpressionContext classCtx = ctx.Expression(0);
            ClassSymbol classSymbol = ExpressionClassSymbol.get(classCtx);
            if (classSymbol == null) {
                ExpressionType.put(ctx, VarType.typeInt);
            } else if (classSymbol.getSymbolType() != SymbolType.classSymbol) {
                ExpressionType.put(ctx, VarType.typeInt);
            } else {
                    ExpressionType.put(ctx, VarType.typeInt);
                } else if (ExpressionType.get(classCtx) != VarType.typeClass) {
                    ExpressionType.put(ctx, VarType.typeInt);
                } else {
                    ExpressionType.put(ctx, ((MethodSymbol) calledMethod).getReturnType());
                    if (((MethodSymbol) calledMethod).getReturnType() == VarType.typeClass) {
                        ExpressionClassSymbol.put(ctx, ((MethodSymbol) calledMethod).getReturnClassSymbol());
                    }
                }
            }
        } catch (Exception e) {
            printError(ctx.getStart(), "Unexpected error!");

        }
    }

    @Override
    public void exitIntExpression(MiniJavaParser.IntExpressionContext ctx) {
        ExpressionType.put(ctx, VarType.typeInt);
    }

    @Override
    public void exitTrueExpression(MiniJavaParser.TrueExpressionContext ctx) {
        ExpressionType.put(ctx, VarType.typeInt);
    }

    @Override
    public void exitFalseExpression(MiniJavaParser.FalseExpressionContext ctx) {
        ExpressionType.put(ctx, VarType.typeInt);
    }

    @Override
    public void exitClassExpression(MiniJavaParser.ClassExpressionContext ctx) {
        try {
            Symbol symbol = currentScope.lookup(ctx.getText());
                switch (symbol.getSymbolType()) {
                    case classSymbol:
                        printError(ctx.getStart(), "Expected to get instance but class symbol " + ctx.getText() + ".");
                        break;
                    case methodSymbol:
                        printError(ctx.getStart(), "Expected to get class but method symbol " + ctx.getText() + ".");
                        break;
                    case varSymbol:
                        VarSymbol varSymbol = (VarSymbol) symbol;
                        switch (varSymbol.getVarType()) {
                            case typeClass:
                                ExpressionType.put(ctx, varSymbol.getVarType());
                                ExpressionClassSymbol.put(ctx, varSymbol.getClassType());
                                break;
                            default:
                                ExpressionType.put(ctx, varSymbol.getVarType());
                        }
                }
            }
        } catch (Exception e) {
            printError(ctx.getStart(), "Unexpected error!");
        }
    }

    @Override
    public void exitThisExpression(MiniJavaParser.ThisExpressionContext ctx) {
        ExpressionType.put(ctx, VarType.typeClass);
        ExpressionClassSymbol.put(ctx, currentScope.getOuterClass());
    }
    @Override
    public void exitNewClassInstanceExpression(MiniJavaParser.NewClassInstanceExpressionContext ctx) {
        try {
            Symbol newInstance = currentScope.lookup(ctx.ID().getText());
            if (newInstance == null) {
                printError(ctx.ID().getSymbol(), "The symbol " + ctx.ID().getText() + " doesn't exist.");
            } else {
                ExpressionType.put(ctx, VarType.typeClass);
                ExpressionClassSymbol.put(ctx, (ClassSymbol) newInstance);
            }
        } catch (Exception e) {
            printError(ctx.getStart(), "Unexpected error!");
        }
    }

//()
    @Override
    public void exitParenthesisExpression(MiniJavaParser.ParenthesisExpressionContext ctx) {
        try {
            ExpressionType.put(ctx, ExpressionType.get(ctx.Expression()));
            if (ExpressionType.get(ctx.Expression()) == VarType.typeClass) {
                ExpressionClassSymbol.put(ctx, ExpressionClassSymbol.get(ctx.Expression()));
            }
        } catch (Exception e) {
            printError(ctx.getStart(), "Unexpected error!");
        }
    }

    static void printError(Token t, String msg) {
        System.err.printf("line %d: %s \n", t.getLine(),  msg);
    }
}
