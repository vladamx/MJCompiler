package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.codegen.ExprGenerator;
import rs.ac.bg.etf.pp1.semantics.CounterVisitor;
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
		// vrednost izraza na esteku
		if(assignment.getDesignator().getClass() == ArrayFieldDesignator.class) {
			assignment.getDesignator().traverseBottomUp(new VisitorAdaptor() {
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
					designator.getExpr1().traverseBottomUp(new ExprGenerator());
				}
			});
		}
		assignment.getExpr1().traverseBottomUp(new ExprGenerator());
		Code.store(assignment.getDesignator().obj);
	}

	@Override
	public void visit(DecrementStmt stmt) {
		stmt.getDesignator().traverseBottomUp(new VisitorAdaptor() {
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
				designator.getExpr1().traverseBottomUp(new ExprGenerator());
			}
		});
		Code.load(stmt.getDesignator().obj);
		Code.load(new Obj(Obj.Con, "$", Tab.intType, 1, 0));
		Code.put(Code.sub);
		Code.store(stmt.getDesignator().obj);
	}

	@Override
	public void visit(IncrementStmt stmt) {
		stmt.getDesignator().traverseBottomUp(new VisitorAdaptor() {
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
				designator.getExpr1().traverseBottomUp(new ExprGenerator());
			}
		});
		Code.load(stmt.getDesignator().obj);
		Code.load(new Obj(Obj.Con, "$", Tab.intType, 1, 0));
		Code.put(Code.add);
		Code.store(stmt.getDesignator().obj);
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
				designator.getExpr1().traverseBottomUp(new ExprGenerator());
			}
		});
		// vrednost zavrsila na expr stacku
		if(readStmt.getDesignator().obj.getType() == Tab.charType) {
			Code.put(Code.bread);
		} else {
			Code.put(Code.read);
		}
		// adr
		Code.store(readStmt.getDesignator().obj);
	}

	@Override
	public void visit(PrintStmt printStmt) {
		printStmt.getExpr1().traverseBottomUp(new ExprGenerator());
		Code.put(Code.const_1);
		if(printStmt.getExpr1().struct == Tab.charType) {
			Code.put(Code.bprint);
		} else {
			Code.put(Code.print);
		}
	}

	@Override
	public void visit(PrintStmtOptional printStmt) {
		printStmt.getExpr1().traverseBottomUp(new ExprGenerator());
		Code.load(new Obj(Obj.Con, "$", Tab.intType, printStmt.getNumber(), 0));
		if(printStmt.getExpr1().struct == Tab.charType) {
			Code.put(Code.bprint);
		} else {
			Code.put(Code.print);
		}
	}
}
