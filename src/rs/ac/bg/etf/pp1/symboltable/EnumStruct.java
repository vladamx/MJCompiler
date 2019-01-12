package rs.ac.bg.etf.pp1.symboltable;

import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Struct;

class EnumStruct extends Struct {
    EnumStruct() {
        super(6);
    }

    @Override
    public Struct getElemType() {
        return Tab.intType;
    }
}
