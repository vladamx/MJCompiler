package rs.ac.bg.etf.pp1.codegen;

import rs.ac.bg.etf.pp1.ast.TernaryExpr;
import rs.ac.bg.etf.pp1.ast.VisitorAdaptor;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;


public class TernaryExprGenerator extends VisitorAdaptor {
    @Override
    public void visit(TernaryExpr expr) {
        expr.getExpr().traverseBottomUp(new RegularExprGenerator());
        Code.load(new Obj(Obj.Con,"$", Tab.intType, 0, 1));
        Code.putFalseJump(1,0);
        int fixupAddress = Code.pc - 2;
        expr.getExpr1().traverseBottomUp(new RegularExprGenerator());
        Code.putJump(0);
        int jumpOutAddress = Code.pc - 2;
        Code.fixup(fixupAddress);
        expr.getExpr2().traverseBottomUp(new RegularExprGenerator());
        Code.fixup(jumpOutAddress);
    }

}
