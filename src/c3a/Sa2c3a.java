package c3a;
import ts.*;
import sa.*;
import util.Error;

public class Sa2c3a extends SaDepthFirstVisitor <C3aOperand> {
    private C3a c3a;
    int indentation;

    public C3a getC3a() {
        return this.c3a;
    }

    public Sa2c3a(SaNode root, Ts tableGlobale) {
        c3a = new C3a();
        C3aTemp result = c3a.newTemp();
        C3aFunction fct = new C3aFunction(tableGlobale.getFct("main"));
        c3a.ajouteInst(new C3aInstCall(fct, result, ""));
        c3a.ajouteInst(new C3aInstStop(result, ""));
        indentation = 0;

    }

    public void defaultIn(SaNode node) {
        //for(int i = 0; i < indentation; i++){System.out.print(" ");}
        //indentation++;
        //System.out.println("<" + node.getClass().getSimpleName() + ">");
    }

    public void defaultOut(SaNode node) {
        //indentation--;
        //	for(int i = 0; i < indentation; i++){System.out.print(" ");}
        //	System.out.println("</" + node.getClass().getSimpleName() + ">");
    }


    public C3aOperand visit(SaExpAdd node) throws Exception {
        defaultIn(node);

        C3aOperand op1 = node.getOp1().accept(this);
        C3aOperand op2 = node.getOp2().accept(this);
        C3aOperand result = c3a.newTemp();

        c3a.ajouteInst(new C3aInstAdd(op1, op2, result, ""));
        defaultOut(node);
        return result;
    }

    public C3aOperand visit(SaExpSub node) throws Exception {
        defaultIn(node);
        C3aOperand op1 = node.getOp1().accept(this);
        C3aOperand op2 = node.getOp2().accept(this);
        C3aOperand result = c3a.newTemp();

        c3a.ajouteInst(new C3aInstSub(op1, op2, result, ""));
        defaultOut(node);
        return result;
    }

    public C3aOperand visit(SaExpMult node) throws Exception {
        defaultIn(node);
        C3aOperand op1 = node.getOp1().accept(this);
        C3aOperand op2 = node.getOp2().accept(this);
        C3aOperand result = c3a.newTemp();
        c3a.ajouteInst(new C3aInstMult(op1, op2, result, ""));
        defaultOut(node);
        return result;
    }

    public C3aOperand visit(SaExpDiv node) throws Exception {
        defaultIn(node);
        C3aOperand op1 = node.getOp1().accept(this);
        C3aOperand op2 = node.getOp2().accept(this);
        C3aOperand result = c3a.newTemp();
        c3a.ajouteInst(new C3aInstDiv(op1, op2, result, ""));
        defaultOut(node);
        return result;
    }

    public C3aOperand visit(SaInstAffect node) throws Exception {
        defaultIn(node);
        C3aOperand var = node.getLhs().accept(this);
        C3aOperand expr = node.getRhs().accept(this);
        c3a.ajouteInst(new C3aInstAffect(expr, var, ""));
        defaultOut(node);
        return null;
    }

    public C3aOperand visit(SaInstRetour node) throws Exception {
        C3aOperand valeur = node.getVal().accept(this);
        c3a.ajouteInst(new C3aInstReturn(valeur, ""));
        return null;
    }

    public C3aOperand visit(SaExpInt node) throws Exception {
        return new C3aConstant(node.getVal());
    }

    public C3aOperand visit(SaExpVrai node) throws Exception {


            return new C3aConstant(1);

    }

    public C3aOperand visit(SaExpFaux node) throws Exception {

            return new C3aConstant(0);

    }

    public C3aOperand visit(SaInstEcriture node) throws Exception {
        defaultIn(node);
        C3aOperand op = node.getArg().accept(this);
        c3a.ajouteInst(new C3aInstWrite(op, ""));

        defaultOut(node);
        return op;
    }

    public C3aOperand visit(SaDecFonc node) throws Exception {
        defaultIn(node);

        TsItemFct fonction = node.tsItem;
        c3a.ajouteInst(new C3aInstFBegin(fonction, "entree fonction"));
        if (node.getParametres() != null) {
            node.getParametres().accept(this);
        }
        if (node.getCorps() != null) {
            node.getCorps().accept(this);
        }
        if (node.getVariable() != null) {
            node.getVariable().accept(this);
        }
        c3a.ajouteInst(new C3aInstFEnd(""));
        defaultOut(node);
        return null;
    }

    public C3aOperand visit(SaAppel node) throws Exception {
        C3aFunction func = new C3aFunction(node.tsItem);
        C3aOperand result = c3a.newTemp();

        SaLExp args = node.getArguments();
        while (args != null) {
            C3aOperand arg = node.getArguments().getTete().accept(this);
            c3a.ajouteInst(new C3aInstParam(arg, ""));
            args = args.getQueue();
        }
        c3a.ajouteInst(new C3aInstCall(func, result, ""));
        return result;
    }

    public C3aOperand visit(SaVarSimple node) throws Exception {
        defaultIn(node);
        TsItemVarSimple varSimple = node.getTsItem();
        C3aOperand result = new C3aVar(varSimple, null);
        defaultOut(node);
        return result;
    }
    public C3aOperand visit(SaInstSi node) throws Exception {
        defaultIn(node);
        C3aOperand op = node.getTest().accept(this);
        C3aLabel etiquette0 = c3a.newAutoLabel();
        C3aLabel etiquette1 = c3a.newAutoLabel();

        c3a.ajouteInst(new C3aInstJumpIfEqual(op,c3a.False , etiquette0,""));
        node.getAlors().accept(this);
        c3a.ajouteInst(new C3aInstJump(etiquette1,""));
        c3a.addLabelToNextInst(etiquette0);
         if (node.getSinon() != null) {
             node.getSinon().accept(this);

         }
         c3a.addLabelToNextInst(etiquette1);
        return op;

    }



}
