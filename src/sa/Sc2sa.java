package sa;
import lParser.analysis.*;
import lParser.node.*;
import util.Type;

public class Sc2sa extends DepthFirstAdapter
{
    private SaNode returnValue;
    private Type returnType;
    private SaProg saRoot = null;

    public void defaultIn(@SuppressWarnings("unused") Node node)
    {
	    System.out.println("<" + node.getClass().getSimpleName() + ">");
    }

    public void defaultOut(@SuppressWarnings("unused") Node node)
    {
	    System.out.println("</" + node.getClass().getSimpleName() + ">");
    }
    
    public SaProg getRoot()
    {
	return this.saRoot;
    }


    // {et} exp1 et exp2
    public void caseAEtExp1(AEtExp1 node)
    {
        SaExp op1 = null;
        SaExp op2 = null;

        inAEtExp1(node);
        if(node.getExp1() != null)
        {
            node.getExp1().apply(this);
            op1 = (SaExp) this.returnValue;
        }
        if(node.getExp2() != null)
        {
            node.getExp2().apply(this);
            op2 = (SaExp) this.returnValue;
        }
        this.returnValue = new SaExpAnd(op1, op2);
        outAEtExp1(node);
    }

    //{affect} exp2 affect exp3
    public void caseAAffectExp2(AAffectExp2 node)
    {
        SaExp op1 = null;
        SaExp op2 = null;

        inAAffectExp2(node);
        if(node.getExp2() != null)
        {
            node.getExp2().apply(this);
            op1 = (SaExp) this.returnValue;
        }

        if(node.getExp3() != null)
        {
            node.getExp3().apply(this);
            op2 = (SaExp) this.returnValue;
        }
        this.returnValue = new SaExpEqual(op1, op2);
        outAAffectExp2(node);
    }

    //{next} exp3
    public void caseANextExp2(ANextExp2 node)
    {
        inANextExp2(node);
        if(node.getExp3() != null)
        {
            node.getExp3().apply(this);
        }
        outANextExp2(node);
    }

    //{plus} exp3 plus exp4
    public void caseAPlusExp3(APlusExp3 node)
    {
        SaExp op1 = null;
        SaExp op2 = null;
        node.getExp3().apply(this);
        op1 = (SaExp) this.returnValue;
        node.getExp4().apply(this);
        op2 = (SaExp) this.returnValue;
        this.returnValue = new SaExpAdd(op1, op2);
    }

    //{moins} exp3 moins exp4
    public void caseAMoinsExp3(AMoinsExp3 node)
    {
        SaExp op1 = null;
        SaExp op2 = null;

        inAMoinsExp3(node);
        if(node.getExp3() != null)
        {
            node.getExp3().apply(this);
            op1 = (SaExp) this.returnValue;
        }
        if(node.getMoins() != null)
        {
            node.getMoins().apply(this);
        }
        if(node.getExp4() != null)
        {
            node.getExp4().apply(this);
            op2 = (SaExp) this.returnValue;
        }
        this.returnValue = new SaExpSub(op1, op2);
        outAMoinsExp3(node);
    }

    public void caseANextExp3(ANextExp3 node)
    {
        inANextExp3(node);
        if(node.getExp4() != null)
        {
            node.getExp4().apply(this);
        }
        outANextExp3(node);
    }

}
