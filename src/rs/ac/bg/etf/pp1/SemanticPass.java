package rs.ac.bg.etf.pp1;
import org.apache.log4j.Logger;
import rs.ac.bg.etf.pp1.ast.*;
import rs.ac.bg.etf.pp1.symboltable.Bool;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class SemanticPass extends VisitorAdaptor {

	boolean errorDetected = false;
	int nVars;

	Logger log = Logger.getLogger(getClass());

	public void report_error(String message, SyntaxNode info) {
		errorDetected = true;
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0: info.getLine();
		if (line != 0)
			msg.append (" na liniji ").append(line);
		log.error(msg.toString());
	}

	public void report_info(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder(message); 
		int line = (info == null) ? 0: info.getLine();
		if (line != 0)
			msg.append (" na liniji ").append(line);
		log.info(msg.toString());
	}
	
	public void visit(Program program) {		
		nVars = Tab.currentScope.getnVars();
		Tab.chainLocalSymbols(program.getProgName().obj);
		Tab.closeScope();
	}

	public void visit(ProgName progName) {
		progName.obj = Tab.insert(Obj.Prog, progName.getPName(), Tab.noType);
		Tab.openScope();     	
	}

	public void visit(VarDecl varDecl) {
		final Type type = varDecl.getType();
		varDecl.traverseTopDown(new VisitorAdaptor() {
			@Override
			public void visit(SimpleVarDeclItem varDeclItem) {
				Obj obj = Tab.currentScope().findSymbol(varDeclItem.getVarName());
				if(obj == null) {
					report_info("Deklarisana promenljiva " + varDeclItem.getVarName(), varDeclItem);
					Tab.insert(Obj.Var, varDeclItem.getVarName(), type.struct);
				} else {
					report_error("Promenljiva " + varDeclItem.getVarName() + " je vec deklarisana" , varDeclItem);
				}
			}
			@Override
			public void visit(ArrayVarDeclItem varDeclItem) {
				Obj obj = Tab.currentScope().findSymbol(varDeclItem.getVarName());
				if(obj == null) {
					report_info("Deklarisan niz " + varDeclItem.getVarName(),varDeclItem);
					Tab.insert(Obj.Var, varDeclItem.getVarName(), new Struct(Struct.Array, type.struct));
				} else {
					report_error("Promenljiva " + varDeclItem.getVarName() + " je vec deklarisana" , varDeclItem);
				}
			}
		});
	}

	public void visit(ConstDecl constDecl) {
		final Type type = constDecl.getType();
		constDecl.traverseBottomUp(new VisitorAdaptor() {
			private Integer value;
			@Override
			public void visit(ConstDeclItem constDeclItem) {
				if(type.struct == constDeclItem.getTypeLiteral().struct) {
					Obj obj = Tab.currentScope().findSymbol(constDeclItem.getName());
					if(obj == null) {
						report_info("Deklarisana konstanta " + constDeclItem.getName(), constDeclItem);
						Obj insert = Tab.insert(Obj.Con, constDeclItem.getName(), type.struct);
						insert.setAdr(this.value);
						constDeclItem.obj = insert;
					} else {
						report_error("Konstanta " + constDeclItem.getName() + " je vec deklarisana" , constDeclItem);
					}
				} else {
					report_error("Tip literala i tip konstante se ne slazu", constDeclItem);
				}
			}
			@Override
			public void visit(NumberLiteral numberLiteral) {
				this.value = numberLiteral.getValue();
			}
			public void visit(CharLiteral cnst){
				this.value = Character.getNumericValue(cnst.getValue());
			}

		});
	}

	@Override
	public void visit(EnumDecl enumDecl) {
		// Todo: en
		Obj obj = Tab.currentScope().findSymbol(enumDecl.getName());
		if(obj == null) {
			report_info("Deklarisan niz " + enumDecl.getName(),enumDecl);
			// TODO: enum type does not exist in tab
//			Tab.insert(Obj., enumDecl.getName(), new Struct(Struct.Array, type.struct));
		} else {
			report_error("Promenljiva " + enumDecl.getName() + " je vec deklarisana" , enumDecl);
		}
	}

	public void visit(Type type) {
		Obj typeNode = Tab.find(type.getTypeName());
		if (typeNode == Tab.noObj) {
			report_error("Nije pronadjen tip " + type.getTypeName() + " u tabeli simbola", null);
			type.struct = Tab.noType;
		} 
		else {
			if (Obj.Type == typeNode.getKind()) {
				type.struct = typeNode.getType();
			} 
			else {
				report_error("Greska: Ime " + type.getTypeName() + " ne predstavlja tip ", type);
				type.struct = Tab.noType;
			}
		}  
	}

	public void visit(MethodDecl methodDecl) {
		Tab.chainLocalSymbols(methodDecl.getMethodTypeName().obj);
		Tab.closeScope();
	}

	public void visit(MethodTypeName methodTypeName) {
		if(!methodTypeName.getName().equals("main")) {
			report_error("Ime metode mora biti 'main' ", methodTypeName);
		}
		Obj currentMethod = Tab.insert(Obj.Meth, methodTypeName.getName(), Tab.noType);
		methodTypeName.obj = currentMethod;
		Tab.openScope();
		report_info("Obradjuje se funkcija " + methodTypeName.getName(), methodTypeName);
	}

	public void visit(AssignmentStmt assignment) {
		Designator designator = assignment.getDesignator();
		Struct targetType = designator.obj.getType();
		if(designator.getClass() == ArrayFieldDesignator.class) {
			targetType = designator.obj.getType().getElemType();
		}

		if (!assignment.getExpr().struct.assignableTo(targetType))
		report_error("Greska na liniji " + assignment.getLine() + " : " + " nekompatibilni tipovi u dodeli vrednosti ", null);
	}

	@Override
	public void visit(IncrementStmt incrementStmt) {
		if(incrementStmt.getDesignator().obj.getKind() == Obj.Con) {
			report_error("Greska na liniji " + incrementStmt.getLine() + " : " + " nekompatibilni tipovi u dodeli vrednosti ", null);
		}
	}

	@Override
	public void visit(DecrementStmt decrementStmt) {;
		if(decrementStmt.getDesignator().obj.getKind() == Obj.Con) {
		report_error("Greska na liniji " + decrementStmt.getLine() + " : " + " nekompatibilni tipovi u dodeli vrednosti ", null);
		}
	}

	public void visit(PrintStmt printStmt){

	}
    public void visit(MulExprTerm expr) {
        Struct te = expr.getTerm().struct;
        Struct t = expr.getFactor().struct;
        if (te.equals(t) && te == Tab.intType) {
            expr.struct = te;
        }
        else {
            report_error("Greska na liniji "+ expr.getLine()+" : nekompatibilni tipovi u izrazu za mnozenje.", null);
            expr.struct = Tab.noType;
        }
    }
	public void visit(AddExpr addExpr) {
		Struct te = addExpr.getExpr().struct;
		Struct t = addExpr.getTerm().struct;
		if (te.equals(t)) {
			if(te == Tab.intType) {
				addExpr.struct = te;
			} else {
				report_error("Greska na liniji "+ addExpr.getLine()+" : moguce je sabirati samo int tipove.", null);
			}
		}
		else {
			report_error("Greska na liniji "+ addExpr.getLine()+" : nekompatibilni tipovi u izrazu za sabiranje.", null);
			addExpr.struct = Tab.noType;
		}
	}

	public void visit(TermExpr termExpr) {
		termExpr.struct = termExpr.getTerm().struct;
	}

	public void visit(LiteralFactor term) {
		term.struct = term.getTypeLiteral().struct;
	}

	@Override
	public void visit(BracketExprFactor factor) {
		factor.struct = factor.getExpr().struct;
	}

	@Override
	public void visit(AllocArrayFactor factor) {
		//TODO: ne znam da li je ovo ispravno?
		factor.struct = new Struct(Struct.Array, factor.getType().struct);
	}

	@Override
	public void visit(NegTermExpr negTermExpr) {
		if(negTermExpr.getTerm().struct != Tab.intType) {
			report_error("Greska na liniji "+ negTermExpr.getLine()+" : nekompatibilni tip u izrazu za negaciju.", null);
		}
		negTermExpr.struct = negTermExpr.getTerm().struct;
	}

	public void visit(FactorTerm term) {
		term.struct = term.getFactor().struct;
	}

	public void visit(NumberLiteral cnst){
		cnst.struct = Tab.intType;
	}

	public void visit(CharLiteral cnst){
		cnst.struct = Tab.charType;
	}

	public void visit(BoolLiteral cnst){
		cnst.struct = Bool.struct;
	}

	@Override
	public void visit(NameDesignator designator) {
		Obj obj = Tab.find(designator.getName());
		if (obj == Tab.noObj) {
			report_error("Greska na liniji " + designator.getLine()+ " : ime "+designator.getName()+" nije deklarisano! ", null);
		}
		designator.obj = obj;
		if(designator.getParent().getClass() == DesignatorFactor.class) {
			((DesignatorFactor)designator.getParent()).struct = obj.getType();
		}
	}

	@Override
	public void visit(ArrayFieldDesignator designator) {
		String name = ((NameArrayDesignator) designator.getArrayDesignator()).getName();
		Obj obj = Tab.find(name);
		if (obj == Tab.noObj) {
			report_error("Greska na liniji " + designator.getLine()+ " : ime "+name+" nije deklarisano! ", null);
		}
//		if (obj.getType().getKind() != Struct.Array) {
//			report_error("Greska na liniji " + designator.getLine() + " : ime " + designator.getName() + " nije niz! ", null);
//		}
		designator.obj = obj;
		if(designator.getParent().getClass() == DesignatorFactor.class) {
			((DesignatorFactor)designator.getParent()).struct = designator.obj.getType().getElemType();
		}
	}

	public boolean passed() {
		return !errorDetected;
	}
	
}

