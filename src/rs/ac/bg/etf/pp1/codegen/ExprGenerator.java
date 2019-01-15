package rs.ac.bg.etf.pp1.codegen;

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
    public void visit(CharLiteral constant) {
        int numericValue = (int)constant.getValue().charValue();
        Code.load(new Obj(Obj.Con, "$", constant.struct, numericValue, 0));
    }

    @Override
    public void visit(EnumDesignatorFactorItem factor) {
        Obj found = null;
        for(Obj obj :factor.obj.getLocalSymbols()) {
            if(obj.getName().equals(factor.getField())) {
                found = obj;
            }
        }
        Code.load(found);
    }


    @Override
    public void visit(BoolLiteral constant) {
        int numericValue = constant.getValue().compareTo(true) == 0 ? 1 : 0;
        Code.load(new Obj(Obj.Con, "$", constant.struct, numericValue, 0));
    }

    @Override
    public void visit(AddExpr addExpr) {
        if(addExpr.getAddop().getClass() == PlusAddop.class) {
            Code.put(Code.add);
        }
        if(addExpr.getAddop().getClass() == MinusAddop.class) {
            Code.put(Code.sub);
        }
    }

    @Override
    public void visit(NegTermExpr negTermExpr) {
        Code.put(Code.neg);
    }

    @Override
    public void visit(MulExprTerm mulExprTerm) {
        if(mulExprTerm.getMulop().getClass() == MulMulop.class) {
            Code.put(Code.mul);
        }
        if(mulExprTerm.getMulop().getClass() == DivMulop.class) {
            Code.put(Code.div);
        }
        if(mulExprTerm.getMulop().getClass() == ModMulop.class) {
            Code.put(Code.rem);
        }
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
