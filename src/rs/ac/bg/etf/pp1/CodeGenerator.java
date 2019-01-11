package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;

public class CodeGenerator extends VisitorAdaptor {
	
	private int mainPc;

	public int getMainPc() {
		return mainPc;
	}
	
	@Override
	public void visit(MethodTypeName methodTypeName) {
		if ("main".equalsIgnoreCase(methodTypeName.getName())) {
			mainPc = Code.pc;
		}
		methodTypeName.obj.setAdr(Code.pc);
		
		SyntaxNode methodNode = methodTypeName.getParent();
		CounterVisitor.VarCounter varCounter = new CounterVisitor.VarCounter();
		methodNode.traverseTopDown(varCounter);

		Code.put(Code.enter);
		Code.put(0);
		Code.put(varCounter.getCount());
	}
	

	@Override
	public void visit(MethodDecl MethodDecl) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	@Override
	public void visit(AssignmentStmt assignment) {
		assignment.getExpr().traverseBottomUp(new ExprGenerator());
		// vrednost izraza na esteku
		Code.store(assignment.getDesignator().obj);
	}

	@Override
	public void visit(DecrementStmt stmt) {
	}

	@Override
	public void visit(IncrementStmt stmt) {
	}

	@Override
	public void visit(ReadStmt readStmt) {
		readStmt.getDesignator().traverseBottomUp(new VisitorAdaptor() {
			@Override
			public void visit(NameArrayDesignator designator) {
				//NOTE: put array address on stack before ArrayFieldDesignator kicks in;
				Code.load(((ArrayFieldDesignator)designator.getParent()).obj);
			}
			@Override
			public void visit(ArrayFieldDesignator designator) {
				//NOTE: Here we know that we want array field. Address is already here.
				designator.obj = new Obj(Obj.Elem ,"$", designator.obj.getType().getElemType());
				// i
				designator.getExpr().traverseBottomUp(new ExprGenerator());
			}
		});
		Code.put(Code.read); // vrednost zavrsila na expr stacku
		// adr
		Code.store(readStmt.getDesignator().obj);
	}

	@Override
	public void visit(PrintStmt printStmt) {
		printStmt.getExpr().traverseBottomUp(new ExprGenerator());
		Code.put(Code.const_1);
		Code.put(Code.print);
	}

	@Override
	public void visit(PrintStmtOptional printStmt) {
		printStmt.getExpr().traverseBottomUp(new ExprGenerator());
		Code.load(new Obj(Obj.Con, "$", Tab.intType, printStmt.getNumber(), 0));
		Code.put(Code.print);
	}
}
