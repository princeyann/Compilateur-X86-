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

    public Void visit(SaExpAdd node) throws Exception
    {
		defaultIn(node);
		node.getOp1().accept(this);
		node.getOp2().accept(this);
		if(!Type.checkCompatibility(node.getOp1().getType(), Type.ENTIER) ||
				!Type.checkCompatibility(node.getOp2().getType(),Type.ENTIER))
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
		if (!Type.checkCompatibility(node.getTest().getType(),Type.BOOL)) {
			throw new ErrorException(Error.TYPE, "instruction if");
		}
		defaultOut(node);
		return null;
	}
	public Void visit(SaInstTantQue node) throws Exception{
		defaultIn(node);
		if (!Type.checkCompatibility(node.getTest().getType(),Type.BOOL)) {
			throw new ErrorException(Error.TYPE, "instruction tant que");
		}
		defaultOut(node);
		return null;
	}



}
