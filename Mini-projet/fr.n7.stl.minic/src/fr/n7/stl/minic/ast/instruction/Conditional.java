/**
 * 
 */
package fr.n7.stl.minic.ast.instruction;

import java.util.Optional;

import fr.n7.stl.minic.ast.Block;
import fr.n7.stl.minic.ast.SemanticsUndefinedException;
import fr.n7.stl.minic.ast.expression.Expression;
import fr.n7.stl.minic.ast.instruction.declaration.FunctionDeclaration;
import fr.n7.stl.minic.ast.scope.Declaration;
import fr.n7.stl.minic.ast.scope.HierarchicalScope;
import fr.n7.stl.minic.ast.type.AtomicType;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;

/**
 * Implementation of the Abstract Syntax Tree node for a conditional instruction.
 * @author Marc Pantel
 *
 */
public class Conditional implements Instruction {

	protected Expression condition;
	protected Block thenBranch;
	protected Block elseBranch;

	public Conditional(Expression _condition, Block _then, Block _else) {
		this.condition = _condition;
		this.thenBranch = _then;
		this.elseBranch = _else;
	}

	public Conditional(Expression _condition, Block _then) {
		this.condition = _condition;
		this.thenBranch = _then;
		this.elseBranch = null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "if (" + this.condition + " )" + this.thenBranch + ((this.elseBranch != null)?(" else " + this.elseBranch):"");
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collectAndPartialResolve(HierarchicalScope<Declaration> _scope) {
		boolean ok = true;
		ok = ok && this.condition.collectAndPartialResolve(_scope);
		ok = ok && this.thenBranch.collectAndPartialResolve(_scope);
		if(this.elseBranch != null){
			ok = ok && this.elseBranch.collectAndPartialResolve(_scope);
		}
		return ok;
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collectAndPartialResolve(HierarchicalScope<Declaration> _scope, FunctionDeclaration _container) {
		return this.collectAndPartialResolve(_scope);
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean completeResolve(HierarchicalScope<Declaration> _scope) {
		boolean ok = true;
		ok = ok && this.condition.completeResolve(_scope);
		ok = ok && this.thenBranch.completeResolve(_scope);
		if(this.elseBranch != null){
			ok = ok && this.elseBranch.completeResolve(_scope);
		}
		return ok;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#checkType()
	 */
	@Override
	public boolean checkType() {
		boolean ok = true;
		ok = ok && this.condition.getType().equalsTo(AtomicType.BooleanType);
		ok = ok && this.thenBranch.checkType();
		if(this.elseBranch != null){
			ok = ok && this.elseBranch.checkType();
		}
		return ok;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#allocateMemory(fr.n7.stl.tam.ast.Register, int)
	 */
	@Override
	public int allocateMemory(Register _register, int _offset) {
		return 0;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		 Fragment frag = _factory.createFragment();
    
		 String labelCondition = "label_condition_" + _factory.createLabelNumber();
		 String labelElse = "label_else_" + _factory.createLabelNumber();
		 String labelEnd = "label_end_" + _factory.createLabelNumber();
	 
		 frag.addPrefix(labelCondition);
		 
		 frag.append(this.condition.getCode(_factory));
		 
		 frag.add(_factory.createJumpIf(labelElse, 1));  
		 
		 frag.append(this.thenBranch.getCode(_factory));
		 
		 frag.add(_factory.createJump(labelEnd));
	 
		 frag.addSuffix(labelElse);
	 
		 if (this.elseBranch != null) {
			 frag.append(this.elseBranch.getCode(_factory));
		 }
	 
		 frag.addSuffix(labelEnd);
	 
		 return frag;
	 }
	
}
