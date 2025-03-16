package sa;
import util.Type;
import util.Error;
import ts.*;

import java.util.ArrayList;
import java.util.List;

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

	public Void visit(SaExpAdd node) throws Exception
	{
		defaultIn(node);
		node.getOp1().accept(this);
		node.getOp2().accept(this);
		if(!Type.checkCompatibility(node.getOp1().getType(), Type.ENTIER) ||
				!Type.checkCompatibility(node.getOp2().getType(), Type.ENTIER))
			throw new ErrorException(Error.TYPE, "exp addition");
		defaultOut(node);
		return null;
	}

	public Void visit(SaExpInf node) throws Exception{
		defaultIn(node);
		node.getOp1().accept(this);
		node.getOp2().accept(this);
		if(!Type.checkCompatibility(node.getOp1().getType(), Type.ENTIER) ||
				!Type.checkCompatibility(node.getOp2().getType(),Type.ENTIER))
			throw new ErrorException(Error.TYPE, "exp inferieur");
		defaultOut(node);
		return null;
	}
	public Void visit(SaExpAnd node) throws Exception{
		defaultIn(node);
		node.getOp1().accept(this);
		node.getOp2().accept(this);
		if(!Type.checkCompatibility(node.getOp1().getType(), Type.BOOL) ||
				!Type.checkCompatibility(node.getOp2().getType(),Type.BOOL))
			throw new ErrorException(Error.TYPE, "exp et");
		defaultOut(node);
		return null;
	}
	public Void visit(SaExpOr node) throws Exception{
		defaultIn(node);
		node.getOp1().accept(this);
		node.getOp2().accept(this);
		if(!Type.checkCompatibility(node.getOp1().getType(), Type.BOOL) ||
				!Type.checkCompatibility(node.getOp2().getType(),Type.BOOL))
			throw new ErrorException(Error.TYPE, "exp ou");
		defaultOut(node);
		return null;
	}
	public Void visit(SaExpEqual node) throws Exception{
		defaultIn(node);
		node.getOp1().accept(this);
		node.getOp2().accept(this);
		if(Type.checkCompatibility(node.getOp1().getType(), Type.BOOL) &&
				Type.checkCompatibility(node.getOp2().getType(),Type.ENTIER))
			throw new ErrorException(Error.TYPE, "exp egal");
		else if (Type.checkCompatibility(node.getOp1().getType(), Type.ENTIER) &&
				Type.checkCompatibility(node.getOp2().getType(),Type.BOOL)) {
			throw new ErrorException(Error.TYPE, "exp egal");
		}
		defaultOut(node);
		return null;
	}
	public Void visit(SaExpSub node) throws Exception{
		defaultIn(node);
		node.getOp1().accept(this);
		node.getOp2().accept(this);
		if (!Type.checkCompatibility(node.getOp1().getType(), Type.ENTIER) ||
				!Type.checkCompatibility(node.getOp2().getType(),Type.ENTIER)) {
			throw new ErrorException(Error.TYPE, "exp soustraction");
		}
		defaultOut(node);
		return null;
	}
	public Void visit(SaExpDiv node) throws Exception{
		defaultIn(node);
		node.getOp1().accept(this);
		node.getOp2().accept(this);
		if (!Type.checkCompatibility(node.getOp1().getType(),Type.ENTIER) ||
				!Type.checkCompatibility(node.getOp2().getType(),Type.ENTIER)) {
			throw new ErrorException(Error.TYPE, "exp division");
		}
		defaultOut(node);
		return null;
	}
	public Void visit(SaExpMult node) throws Exception{
		defaultIn(node);
		node.getOp1().accept(this);
		node.getOp2().accept(this);
		if (!Type.checkCompatibility(node.getOp1().getType(),Type.ENTIER) ||
				!Type.checkCompatibility(node.getOp2().getType(),Type.ENTIER)) {
			throw new ErrorException(Error.TYPE, "exp mult");
		}
		defaultOut(node);
		return null;
	}
	public Void visit(SaInstSi node) throws Exception{
		defaultIn(node);
		node.getTest().accept(this);
		if (!Type.checkCompatibility(node.getTest().getType(),Type.BOOL)) {
			throw new ErrorException(Error.TYPE, "instruction if");
		}
		defaultOut(node);
		return null;
	}
	public Void visit(SaInstTantQue node) throws Exception{
		defaultIn(node);
		node.getTest().accept(this);
		if (!Type.checkCompatibility(node.getTest().getType(),Type.BOOL)) {
			throw new ErrorException(Error.TYPE, "instruction tant que");
		}
		defaultOut(node);
		return null;
	}
	public Void visit(SaInstRetour node) throws Exception {
		defaultIn(node);
		node.getVal().accept(this);
		if (!Type.checkCompatibility(fonctionCourante.getTypeRetour(), node.getVal().getType()))
			throw new ErrorException(Error.TYPE, "retour de fonction incorrect");
		defaultOut(node);
		return null;
	}

	public Void visit(SaVarIndicee node) throws Exception {
		defaultIn(node);
		node.getIndice().accept(this);
		if(!Type.checkCompatibility(node.getIndice().getType(),Type.ENTIER)){
			throw new ErrorException(Error.TYPE, "indice tableau");
		}
		defaultOut(node);
		return null;
	}
	public Void visit(SaExpNot node) throws Exception {
		defaultIn(node);
		node.getOp1().accept(this);
		if (!Type.checkCompatibility(node.getOp1().getType(),Type.BOOL)) {
			throw new ErrorException(Error.TYPE, "not");
		}
		defaultOut(node);
		return null;
	}

	public Void visit(SaAppel node) throws Exception {
		defaultIn(node);
		TsItemFct fonction = node.tsItem;

		List<Type> typesAttendus = new ArrayList<>();
		SaLDecVar parametres = fonction.saDecFonc.getParametres();

		List<SaExp> argsFournis = new ArrayList<>();
		SaLExp arguments = node.getArguments();
		while (parametres != null) {
			typesAttendus.add(parametres.getTete().getType());
			parametres = parametres.getQueue();
		}
		while (arguments != null) {
			argsFournis.add(arguments.getTete());
			arguments = arguments.getQueue();
		}
		for (int i = 0; i < argsFournis.size(); i++) {
			argsFournis.get(i).accept(this);
			if (!Type.checkCompatibility(argsFournis.get(i).getType(), typesAttendus.get(i))) {
				throw new ErrorException(Error.TYPE, "arguments incorrect");
			}
		}
		defaultOut(node);
		return null;
	}
	public Void visit(SaInstAffect node) throws Exception {
		defaultIn(node);
		node.getLhs().accept(this);
		node.getRhs().accept(this);
		if (!Type.checkCompatibility(node.getLhs().getTsItem().getType(),node.getRhs().getType())) {
			throw new ErrorException(Error.TYPE, "affectation incorrect");
		}

		defaultOut(node);
		return null;
	}
	public Void visit(SaDecFonc node) throws Exception {
		defaultIn(node);
		TsItemFct ancienneFonctionCourante = fonctionCourante;
		fonctionCourante = node.tsItem;
		if (node.getCorps() != null) {
			node.getCorps().accept(this);
		}
		fonctionCourante = ancienneFonctionCourante;
		defaultOut(node);
		return null;
	}
}
