package sa;
import util.Type;
import util.Error;
import ts.*;

public class SaTypeCheck extends SaDepthFirstVisitor <Void>{
	private TsItemFct fonctionCourante;

	public SaTypeCheck(SaNode root)
	{
		try{
			root.accept(this);
		} catch(ErrorException e){
			System.err.print("ERREUR DE TYPAGE : ");
			System.err.println(e.getMessage());
			System.exit(e.getCode());
		} catch(Exception e){}
	}

	public void defaultIn(SaNode node)
	{
		//			System.out.println("<" + node.getClass().getSimpleName() + ">");
	}

	public void defaultOut(SaNode node)
	{
		//		System.out.println("</" + node.getClass().getSimpleName() + ">");
	}
	public Void visit(SaExpAdd node) throws Exception {
		node.getOp1().accept(this);
		node.getOp2().accept(this);
		if(!Type.checkCompatibility(node.getOp1().getType(), Type.ENTIER) ||
				!Type.checkCompatibility(node.getOp2().getType(), Type.ENTIER))
			throw new ErrorException(Error.TYPE, "exp addition");
		return null;
	}

	public Void visit(SaExpSub node) throws Exception {
		node.getOp1().accept(this);
		node.getOp2().accept(this);
		if (!Type.checkCompatibility(node.getOp1().getType(), Type.ENTIER) ||
				!Type.checkCompatibility(node.getOp2().getType(), Type.ENTIER)) {
			throw new ErrorException(Error.TYPE, "exp soustraction");
		}
		return null;
	}

	public Void visit(SaExpMult node) throws Exception {
		node.getOp1().accept(this);
		node.getOp2().accept(this);
		if (!Type.checkCompatibility(node.getOp1().getType(), Type.ENTIER) ||
				!Type.checkCompatibility(node.getOp2().getType(), Type.ENTIER)) {
			throw new ErrorException(Error.TYPE, "exp multiplication");
		}
		return null;
	}

	public Void visit(SaExpDiv node) throws Exception {
		node.getOp1().accept(this);
		node.getOp2().accept(this);
		if (!Type.checkCompatibility(node.getOp1().getType(), Type.ENTIER) ||
				!Type.checkCompatibility(node.getOp2().getType(), Type.ENTIER)) {
			throw new ErrorException(Error.TYPE, "exp division");
		}
		return null;
	}

	public Void visit(SaExpInf node) throws Exception {
		node.getOp1().accept(this);
		node.getOp2().accept(this);
		if (!Type.checkCompatibility(node.getOp1().getType(), Type.ENTIER) ||
				!Type.checkCompatibility(node.getOp2().getType(), Type.ENTIER)) {
			throw new ErrorException(Error.TYPE, "exp inférieur");
		}
		return null;
	}

	public Void visit(SaExpAnd node) throws Exception {
		node.getOp1().accept(this);
		node.getOp2().accept(this);
		if (!Type.checkCompatibility(node.getOp1().getType(), Type.BOOL) ||
				!Type.checkCompatibility(node.getOp2().getType(), Type.BOOL)) {
			throw new ErrorException(Error.TYPE, "exp ET");
		}
		return null;
	}

	public Void visit(SaExpOr node) throws Exception {
		node.getOp1().accept(this);
		node.getOp2().accept(this);
		if (!Type.checkCompatibility(node.getOp1().getType(), Type.BOOL) ||
				!Type.checkCompatibility(node.getOp2().getType(), Type.BOOL)) {
			throw new ErrorException(Error.TYPE, "exp OU");
		}
		return null;
	}

	public Void visit(SaExpEqual node) throws Exception {
		node.getOp1().accept(this);
		node.getOp2().accept(this);
		if (!Type.checkCompatibility(node.getOp1().getType(), node.getOp2().getType())) {
			throw new ErrorException(Error.TYPE, "exp égalité");
		}
		return null;
	}

	public Void visit(SaInstSi node) throws Exception {
		node.getTest().accept(this);
		if (!Type.checkCompatibility(node.getTest().getType(), Type.BOOL)) {
			throw new ErrorException(Error.TYPE, "instruction SI");
		}
		return null;
	}

	public Void visit(SaInstTantQue node) throws Exception {
		node.getTest().accept(this);
		if (!Type.checkCompatibility(node.getTest().getType(), Type.BOOL)) {
			throw new ErrorException(Error.TYPE, "instruction TANT QUE");
		}
		return null;
	}

//	public Void visit(SaInstAffect node) throws Exception {
//		node.getLhs().accept(this);
//		node.getRhs().accept(this);
//		if (!Type.checkCompatibility(node.getLhs().getType(), node.getRhs().getType()))
//			throw new ErrorException(Error.TYPE, "affectation incorrecte");
//		return null;
//	}

	public Void visit(SaInstRetour node) throws Exception {
		node.getVal().accept(this);
		if (fonctionCourante == null || !Type.checkCompatibility(fonctionCourante.getTypeRetour(), node.getVal().getType()))
			throw new ErrorException(Error.TYPE, "retour de fonction incorrect");
		return null;
	}

//	public Void visit(SaAppel node) throws Exception {
//		TsItemFct fct = tableSymboles.getFct(node.getNom());
//		if (fct == null || fct.getNbArgs() != (node.getArguments() != null ? node.getArguments().length() : 0))
//			throw new ErrorException(Error.TYPE, "mauvais nombre d'arguments pour " + node.getNom());
//		return null;
//	}
}
