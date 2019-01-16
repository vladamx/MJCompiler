package rs.ac.bg.etf.pp1.symboltable;

import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Struct;

public class EnumStruct extends Struct {
    EnumStruct() {
        super(6);
    }

    @Override
    public Struct getElemType() {
        return Tab.intType;
    }

    @Override
    public boolean assignableTo(Struct dest) {
        if(dest == Tab.intType || dest.getKind() == 6) {
            return  true;
        } else {
            return  false;
        }
    }

    @Override
    public boolean equals(Object o) {
        Struct struct = (Struct) o;
        if(struct == Tab.intType || struct.getKind() == 6) {
            return true;
        } else {
            return  false;
        }
    }
}
