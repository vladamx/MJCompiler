package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;

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
	public void visit(AssignmentStmt Assignment) {
//		Code.store(Assignment.getDesignator().obj);
	}

	@Override
	public void visit(ConstDecl constDecl) {
	}

	@Override
	public void visit(NumberLiteral constant) {
//		Code.load(new Obj(Obj.Con, "$", constant.struct, constant.getValue(), 0));
	}
	
	@Override
	public void visit(NameDesignator nameDesignator) {
		SyntaxNode parent = nameDesignator.getParent();
		if (AssignmentStmt.class != parent.getClass()) {
			Code.load(nameDesignator.obj);
		}
	}

	@Override
	public void visit(PrintStmt printStmt) {
		Code.put(Code.const_1);
		Code.put(Code.print);
	}
	
	@Override
	public void visit(AddExpr addExpr) {
		Code.put(Code.add);
	}
}
