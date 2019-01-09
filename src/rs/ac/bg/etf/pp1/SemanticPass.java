package rs.ac.bg.etf.pp1;
import org.apache.log4j.Logger;
import rs.ac.bg.etf.pp1.ast.*;
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
				report_info("Deklarisana promenljiva " + varDeclItem.getVarName(),varDeclItem);
				Tab.insert(Obj.Var, varDeclItem.getVarName(), type.struct);
			}
			@Override
			public void visit(ArrayVarDeclItem varDeclItem) {
				report_info("Deklarisan niz " + varDeclItem.getVarName(),varDeclItem);
				Tab.insert(Obj.Var, varDeclItem.getVarName(), new Struct(Struct.Array, type.struct));
			}
		});
	}

	public void visit(ConstDecl constDecl) {
		final Type type = constDecl.getType();
		constDecl.traverseBottomUp(new VisitorAdaptor() {
			private Integer value;
			@Override
			public void visit(ConstDeclItem constDeclItem) {
				report_info("Deklarisana konstanta " + constDeclItem.getName(),constDeclItem);
				Obj insert = Tab.insert(Obj.Con, constDeclItem.getName(), type.struct);
				insert.setAdr(this.value);
				constDeclItem.obj = insert;
			}
			@Override
			public void visit(NumberLiteral numberLiteral) {
				this.value = numberLiteral.getValue();
			}
		});
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
		Obj currentMethod = Tab.insert(Obj.Meth, methodTypeName.getName(), Tab.noType);
		methodTypeName.obj = currentMethod;
		Tab.openScope();
		report_info("Obradjuje se funkcija " + methodTypeName.getName(), methodTypeName);
	}

	public void visit(AssignmentStmt assignment) {
//		if (!assignment.getExpr().struct.assignableTo(assignment.getDesignator().obj.getType()))
//			report_error("Greska na liniji " + assignment.getLine() + " : " + " nekompatibilni tipovi u dodeli vrednosti ", null);
	}

	public void visit(PrintStmt printStmt){

	}


	public void visit(AddExpr addExpr) {
		Struct te = addExpr.getExpr().struct;
		Struct t = addExpr.getTerm().struct;
		if (te.equals(t) && te == Tab.intType) {
			addExpr.struct = te;
		}
		else {
			report_error("Greska na liniji "+ addExpr.getLine()+" : nekompatibilni tipovi u izrazu za sabiranje.", null);
			addExpr.struct = Tab.noType;
		}
	}

	public void visit(TermExpr termExpr) {
		termExpr.struct = termExpr.getTerm().struct;
	}

	public void visit(FactorTerm term) {
		term.struct = term.getFactor().struct;
	}

	public void visit(NumberLiteral cnst){
//		cnst.struct = Tab.intType;
	}

	public void visit(NameDesignator designator){
		Obj obj = Tab.find(designator.getName());
		if (obj == Tab.noObj) {
			report_error("Greska na liniji " + designator.getLine()+ " : ime "+designator.getName()+" nije deklarisano! ", null);
		}
		designator.obj = obj;
		((DesignatorFactor)designator.getParent()).struct = obj.getType();
	}
	
	public boolean passed() {
		return !errorDetected;
	}
	
}

