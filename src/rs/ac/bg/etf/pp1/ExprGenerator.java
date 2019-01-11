package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;

public class ExprGenerator extends VisitorAdaptor {
    public void visit(NameDesignator nameDesignator) {
        Code.load(nameDesignator.obj);
    }
    public void visit(NumberLiteral constant) {
        Code.load(new Obj(Obj.Con, "$", constant.struct, constant.getValue(), 0));
    }
    @Override
    public void visit(AddExpr addExpr) {
        Code.put(Code.add);
    }

    @Override
    public void visit(NameArrayDesignator designator) {
        //NOTE: put array address on stack before ArrayFieldDesignator kicks in;
        Code.load(((ArrayFieldDesignator)designator.getParent()).obj);
    }

    @Override
    public void visit(ArrayFieldDesignator designator) {
        // NOTE: element access - adr and index already on stack
        designator.obj = new Obj(Obj.Elem ,"$", designator.obj.getType().getElemType());
        Code.load(designator.obj);
    }

    @Override
    public void visit(AllocArrayFactor factor) {
        // get Size
        Code.put(Code.newarray);
        Code.put((factor.getType().struct == Tab.intType) ? 1 :0);
    }
}
