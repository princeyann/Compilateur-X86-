package sa;
import lParser.analysis.*;
import lParser.node.*;
import lParser.parser.Parser;
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


    //programme = ldv ldf;
    public void caseAProgramme(AProgramme node)
    {
        SaLDecVar op1 = null;
        SaLDecFonc op2 = null;
        inAProgramme(node);
        if(node.getLdv() != null)
        {
            node.getLdv().apply(this);
            op1 = (SaLDecVar) this.returnValue;
        }
        if(node.getLdf() != null)
        {
            node.getLdf().apply(this);
            op2 = (SaLDecFonc) this.returnValue;
        }
        this.saRoot = new SaProg(op1, op2);
        this.returnValue = this.saRoot;
        outAProgramme(node);
    }

    public void caseAEps(AEps node)
    {
        inAEps(node);
        this.returnValue = null;
        outAEps(node);
    }

    public void caseAEntierType(AEntierType node)
    {
        inAEntierType(node);
        if(node.getInt() != null)
        {
            node.getInt().apply(this);
        }
        this.returnType = Type.ENTIER;
        outAEntierType(node);
    }

    public void caseABoolType(ABoolType node)
    {
        inABoolType(node);
        if(node.getBool() != null)
        {
            node.getBool().apply(this);
        }
        this.returnType = Type.BOOL;
        outABoolType(node);
    }
    public void caseATypeopt1Typeopt(ATypeopt1Typeopt node)
    {
        inATypeopt1Typeopt(node);
        if(node.getType() != null)
        {
            node.getType().apply(this);
        }
        outATypeopt1Typeopt(node);
    }


    public void caseATypeopt2Typeopt(ATypeopt2Typeopt node)
    {
        inATypeopt2Typeopt(node);
        if(node.getEps() != null)
        {
            node.getEps().apply(this);
        }
        this.returnType = Type.NUL;
        outATypeopt2Typeopt(node);
    }

    public void caseASimpleVar(ASimpleVar node)
    {
        String id = null;
        inASimpleVar(node);
        if(node.getIdentifiant() != null)
        {
            id = node.getIdentifiant().getText();
        }
        this.returnValue = new SaVarSimple(id);
        outASimpleVar(node);
    }
    public void caseATabVar(ATabVar node)
    {
        String id = null;
        SaExp op1 = null;

        inATabVar(node);
        if(node.getIdentifiant() != null)
        {
            id = node.getIdentifiant().getText();
        }
        if(node.getCrochetOuvrant() != null)
        {
            node.getCrochetOuvrant().apply(this);
        }
        if(node.getExp() != null)
        {
            node.getExp().apply(this);
            op1 = (SaExp) this.returnValue;
        }
        if(node.getCrochetFermant() != null)
        {
            node.getCrochetFermant().apply(this);
        }
        this.returnValue = new SaVarIndicee(id, op1);
        outATabVar(node);
    }

    public void caseABlocBloc(ABlocBloc node)
    {
        inABlocBloc(node);
        if(node.getAccoladeOuvrante() != null)
        {
            node.getAccoladeOuvrante().apply(this);
        }
        SaLInst l = null;
        if(node.getListInstruction() != null){
            node.getListInstruction().apply(this);
            l = (SaLInst) this.returnValue;
        }
        if(node.getAccoladeFermante() != null)
        {
            node.getAccoladeFermante().apply(this);
        }
        this.returnValue = (l == null)? null : new SaInstBloc(l);
        outABlocBloc(node);
    }

    public void caseAListInstructionListInstruction(AListInstructionListInstruction node)
    {
        SaInst op1 = null;
        SaLInst op2 = null;
        inAListInstructionListInstruction(node);
        if(node.getInstruction() != null)
        {
            node.getInstruction().apply(this);
            op1 = (SaInst) this.returnValue;
        }
        if(node.getListInstruction() != null)
        {
            node.getListInstruction().apply(this);
            op2 = (SaLInst) this.returnValue;
        }
        this.returnValue = new SaLInst(op1, op2);
        outAListInstructionListInstruction(node);
    }

    public void caseAListInstruction(AListInstruction node)
    {
        inAListInstruction(node);
        if(node.getEps() != null)
        {
            node.getEps().apply(this);
        }
        this.returnValue = null;
        outAListInstruction(node);
    }

    public void caseADeclaration1Declaration(ADeclaration1Declaration node)
    {
        String id = null;


        inADeclaration1Declaration(node);
        if(node.getType() != null)
        {
            node.getType().apply(this);
        }
        if(node.getIdentifiant() != null)
        {
            id = node.getIdentifiant().getText();
        }
        this.returnValue = new SaDecVarSimple(id,this.returnType);
        outADeclaration1Declaration(node);
    }

    public void caseADeclaration2Declaration(ADeclaration2Declaration node)
    {
        String id = null;
        int taille = 0;

        inADeclaration2Declaration(node);
        if(node.getType() != null)
        {
            node.getType().apply(this);
        }
        if(node.getIdentifiant() != null)
        {
            id = node.getIdentifiant().getText();
        }
        if(node.getCrochetOuvrant() != null)
        {
            node.getCrochetOuvrant().apply(this);
        }
        if(node.getNombre() != null)
        {
            taille = Integer.parseInt(node.getNombre().getText());
        }
        if(node.getCrochetFermant() != null)
        {
            node.getCrochetFermant().apply(this);
        }
        this.returnValue = new SaDecTab(id,this.returnType,taille);
        outADeclaration2Declaration(node);
    }

    /*  exp = {ou} exp ou exp1 | {next} exp1;
        exp1 = {et} exp1 et exp2 | {next} exp2;
        exp2 = {inferieur} exp2 inferieur exp3 | {affect} exp2 affect exp3 | {next} exp3;
        exp3 = {plus} exp3 plus exp4 | {moins} exp3 moins exp4 | {next} exp4;
        exp4 = {mult} exp4 mult exp5 | {div} exp4 div exp5 | {next} exp5;
        exp5 = {non} non exp5 | {next} exp6;
        exp6 = {parenthese} parenthese_ouvrante exp parenthese_fermante | {nb} nombre | {vrai} vrai |
        {faux} faux |{var} var |{appel_fonction} appel_fonction | {lire} lire; */

    //{ou} exp ou exp1
    public void caseAOuExp(AOuExp node)
    {
        SaExp op1 = null;
        SaExp op2 = null;
        inAOuExp(node);
        if(node.getExp() != null)
        {
            node.getExp().apply(this);
            op1 = (SaExp)this.returnValue;
        }
        if(node.getOu() != null)
        {
            node.getOu().apply(this);
        }
        if(node.getExp1() != null)
        {
            node.getExp1().apply(this);
            op2 = (SaExp)this.returnValue;
        }
        this.returnValue = new SaExpOr(op1, op2);
        outAOuExp(node);
    }

    //{next} exp1
    public void caseANextExp(ANextExp node)
    {
        inANextExp(node);
        if(node.getExp1() != null)
        {
            node.getExp1().apply(this);
        }
        outANextExp(node);
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

    //{next} exp2
    public void caseANextExp1(ANextExp1 node)
    {
        inANextExp1(node);
        if(node.getExp2() != null)
        {
            node.getExp2().apply(this);
        }

        outANextExp1(node);
    }
    //{inferieur} exp2 inferieur exp3
    public void caseAInferieurExp2(AInferieurExp2 node)
    {
        SaExp op1 = null;
        SaExp op2 = null;
        inAInferieurExp2(node);
        if(node.getExp2() != null)
        {
            node.getExp2().apply(this);
            op1 = (SaExp) this.returnValue;
        }
        if(node.getInferieur() != null)
        {
            node.getInferieur().apply(this);
        }
        if(node.getExp3() != null)
        {
            node.getExp3().apply(this);
            op2 = (SaExp) this.returnValue;
        }
        this.returnValue = new SaExpInf(op1, op2);
        outAInferieurExp2(node);
    }

    //{egal} exp2 egal exp3
    public void caseAEgalExp2(AEgalExp2 node)
    {
        SaExp op1 = null;
        SaExp op2 = null;
        inAEgalExp2(node);
        if(node.getExp2() != null)
        {
            node.getExp2().apply(this);
            op1 = (SaExp) this.returnValue;
        }
        if(node.getEgal() != null)
        {
            node.getEgal().apply(this);
        }
        if(node.getExp3() != null)
        {
            op2 = (SaExp) this.returnValue;
            node.getExp3().apply(this);
        }
        this.returnValue = new SaExpEqual(op1, op2);
        outAEgalExp2(node);
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

    //{next} exp4
    public void caseANextExp3(ANextExp3 node)
    {
        inANextExp3(node);
        if(node.getExp4() != null)
        {
            node.getExp4().apply(this);
        }
        outANextExp3(node);
    }

    //{mult} exp4 mult exp5
    public void caseAMultExp4(AMultExp4 node)
    {
        SaExp op1 = null;
        SaExp op2 = null;
        inAMultExp4(node);
        if(node.getExp4() != null)
        {
            node.getExp4().apply(this);
            op1 = (SaExp) this.returnValue;
        }
        if(node.getMult() != null)
        {
            node.getMult().apply(this);
        }
        if(node.getExp5() != null)
        {
            node.getExp5().apply(this);
            op2 = (SaExp) this.returnValue;
        }
        this.returnValue = new SaExpMult(op1, op2);
        outAMultExp4(node);
    }

    //{div} exp4 div exp5
    public void caseADivExp4(ADivExp4 node)
    {
        SaExp op1 = null;
        SaExp op2 = null;
        inADivExp4(node);
        if(node.getExp4() != null)
        {
            node.getExp4().apply(this);
            op1 = (SaExp) this.returnValue;
        }
        if(node.getDiv() != null)
        {
            node.getDiv().apply(this);
        }
        if(node.getExp5() != null)
        {
            node.getExp5().apply(this);
            op2 = (SaExp) this.returnValue;
        }
        this.returnValue = new SaExpDiv(op1, op2);
        outADivExp4(node);
    }

    //{next} exp5
    public void caseANextExp4(ANextExp4 node)
    {
        inANextExp4(node);
        if(node.getExp5() != null)
        {
            node.getExp5().apply(this);
        }
        outANextExp4(node);
    }

    //{non} non exp5
    public void caseANonExp5(ANonExp5 node)
    {
        SaExp op1 = null;
        inANonExp5(node);
        if(node.getNon() != null)
        {
            node.getNon().apply(this);
        }
        if(node.getExp5() != null)
        {
            node.getExp5().apply(this);
            op1 = (SaExp) this.returnValue;
        }
        this.returnValue = new SaExpNot(op1);
        outANonExp5(node);
    }

    //{next} exp6
    public void caseANextExp5(ANextExp5 node)
    {
        inANextExp5(node);
        if(node.getExp6() != null)
        {
            node.getExp6().apply(this);
        }
        outANextExp5(node);
    }

    //parenthese_ouvrante exp parenthese_fermante
    public void caseAParentheseExp6(AParentheseExp6 node)
    {
        SaExp op1 = null;
        inAParentheseExp6(node);
        if(node.getParentheseOuvrante() != null)
        {
            node.getParentheseOuvrante().apply(this);
        }
        if(node.getExp() != null)
        {
            node.getExp().apply(this);
            op1 = (SaExp) this.returnValue;
        }
        if(node.getParentheseFermante() != null)
        {
            node.getParentheseFermante().apply(this);
        }
        this.returnValue= op1;
        outAParentheseExp6(node);
    }

    //{nb} nombre
    public void caseANbExp6(ANbExp6 node)
    {
        int entier = 0;
        inANbExp6(node);
        entier = Integer.parseInt(node.getNombre().getText());
        this.returnValue = new SaExpInt(entier);
        outANbExp6(node);
    }

    //{vrai} vrai
    public void caseAVraiExp6(AVraiExp6 node)
    {
        inAVraiExp6(node);
        if(node.getVrai() != null)
        {
            node.getVrai().apply(this);
        }
        this.returnValue = new SaExpVrai();
        outAVraiExp6(node);
    }

    //{faux} faux
    public void caseAFauxExp6(AFauxExp6 node)
    {
        inAFauxExp6(node);
        if(node.getFaux() != null)
        {
            node.getFaux().apply(this);
        }
        this.returnValue = new SaExpFaux();
        outAFauxExp6(node);
    }

    //{var} var
    public void caseAVarExp6(AVarExp6 node)
    {
        node.getVar().apply(this);

        this.returnValue = new SaExpVar((SaVar) this.returnValue);
    }


    //{appel_fonction} appel_fonction
    public void caseAAppelFonctionExp6(AAppelFonctionExp6 node)
    {
        inAAppelFonctionExp6(node);
        if(node.getAppelFonction() != null)
        {
            node.getAppelFonction().apply(this);
        }
        outAAppelFonctionExp6(node);
    }

    //{lire} lire
    public void caseALireExp6(ALireExp6 node)
    {
        inALireExp6(node);
        if(node.getLire() != null)
        {
            node.getLire().apply(this);
        }
        this.returnValue = new SaExpLire();
        outALireExp6(node);
    }

    //list_expression = {list_expression} exp list_expression1
    public void caseAListExpressionListExpression(AListExpressionListExpression node)
    {
        SaExp op1 = null;
        SaLExp op2 = null;
        inAListExpressionListExpression(node);
        if(node.getExp() != null)
        {
            node.getExp().apply(this);
            op1 = (SaExp) this.returnValue;
        }
        if(node.getListExpression1() != null)
        {
            node.getListExpression1().apply(this);
            op2 = (SaLExp) this.returnValue;
        }
        this.returnValue = new SaLExp(op1, op2);
        outAListExpressionListExpression(node);
    }

    //list_expression = eps
    public void caseAListExpression(AListExpression node)
    {
        inAListExpression(node);
        if(node.getEps() != null)
        {
            node.getEps().apply(this);
        }
        this.returnValue = null;
        outAListExpression(node);
    }

    //list_expression1 = {list_expression1} virgule exp list_expression1
    public void caseAListExpression1ListExpression1(AListExpression1ListExpression1 node)
    {
        SaExp op1 = null;
        SaLExp op2 = null;
        inAListExpression1ListExpression1(node);
        if(node.getVirgule() != null)
        {
            node.getVirgule().apply(this);
        }
        if(node.getExp() != null)
        {
            node.getExp().apply(this);
            op1 = (SaExp) this.returnValue;
        }
        if(node.getListExpression1() != null)
        {
            node.getListExpression1().apply(this);
            op2 = (SaLExp) this.returnValue;
        }
        this.returnValue = new SaLExp(op1, op2);
        outAListExpression1ListExpression1(node);
    }

    //list_expression1 = eps
    public void caseAListExpression1(AListExpression1 node)
    {
        inAListExpression1(node);
        if(node.getEps() != null)
        {
            node.getEps().apply(this);
        }
        this.returnValue = null;
        outAListExpression1(node);
    }

    public void caseAAffectInstruction(AAffectInstruction node)
    {
        SaVar op1 = null;
        SaExp op2 = null;
        inAAffectInstruction(node);
        if(node.getVar() != null)
        {
            node.getVar().apply(this);
            op1 = (SaVar) this.returnValue;
        }
        if(node.getEgal() != null)
        {
            node.getEgal().apply(this);
        }
        if(node.getExp() != null)
        {
            node.getExp().apply(this);
            op2 = (SaExp) this.returnValue;
        }
        if(node.getPointVirgule() != null)
        {
            node.getPointVirgule().apply(this);
        }
        this.returnValue = new SaInstAffect(op1,op2);
        outAAffectInstruction(node);
    }

    public void caseASiInstruction(ASiInstruction node)
    {
        SaExp op1 = null;
        SaInstBloc op2 = null;
        inASiInstruction(node);
        if(node.getSi() != null)
        {
            node.getSi().apply(this);
        }
        if(node.getExp() != null)
        {
            node.getExp().apply(this);
            op1 = (SaExp) this.returnValue;
        }
        if(node.getAlors() != null)
        {
            node.getAlors().apply(this);
        }
        if(node.getBloc() != null)
        {
            node.getBloc().apply(this);
            op2 = (SaInstBloc) this.returnValue;
        }
        this.returnValue = new SaInstSi(op1,op2,null);
        outASiInstruction(node);
    }

    public void caseASi1Instruction(ASi1Instruction node)
    {
        SaExp op1 = null;
        SaInstBloc op2 = null;
        SaInstBloc op3 = null;
        inASi1Instruction(node);
        if(node.getSi() != null)
        {
            node.getSi().apply(this);
        }
        if(node.getExp() != null)
        {
            node.getExp().apply(this);
            op1 = (SaExp) this.returnValue;
        }
        if(node.getAlors() != null)
        {
            node.getAlors().apply(this);
        }
        if(node.getBc1() != null)
        {
            node.getBc1().apply(this);
            op2 = (SaInstBloc) this.returnValue;
        }
        if(node.getSinon() != null)
        {
            node.getSinon().apply(this);
        }
        if(node.getBc2() != null)
        {
            node.getBc2().apply(this);
            op3 = (SaInstBloc) this.returnValue;
        }
        this.returnValue = new SaInstSi(op1,op2,op3);
        outASi1Instruction(node);
    }

    public void caseATantqueInstruction(ATantqueInstruction node)
    {
        SaExp op1 = null;
        SaInstBloc op2 = null;
        inATantqueInstruction(node);
        if(node.getTantque() != null)
        {
            node.getTantque().apply(this);
        }
        if(node.getExp() != null)
        {
            node.getExp().apply(this);
            op1 = (SaExp) this.returnValue;
        }
        if(node.getFaire() != null)
        {
            node.getFaire().apply(this);
        }
        if(node.getBloc() != null)
        {
            node.getBloc().apply(this);
            op2 = (SaInstBloc) this.returnValue;
        }
        this.returnValue = new SaInstTantQue(op1,op2);
        outATantqueInstruction(node);
    }

    public void caseAAppelInstruction(AAppelInstruction node)
    {
        SaLExp op1 = null;
        String id = null;
        inAAppelInstruction(node);
        if(node.getIdentifiant() != null)
        {
            id = node.getIdentifiant().getText();
        }
        if(node.getParentheseOuvrante() != null)
        {
            node.getParentheseOuvrante().apply(this);
        }
        if(node.getListExpression() != null)
        {
            node.getListExpression().apply(this);
            op1 = (SaLExp) this.returnValue;
        }
        if(node.getParentheseFermante() != null)
        {
            node.getParentheseFermante().apply(this);
        }
        if(node.getPointVirgule() != null)
        {
            node.getPointVirgule().apply(this);
        }
        this.returnValue = new SaAppel(id,op1);
        outAAppelInstruction(node);
    }

    public void caseARetourInstruction(ARetourInstruction node)
    {
        SaExp op1 = null;
        inARetourInstruction(node);
        if(node.getRetour() != null)
        {
            node.getRetour().apply(this);
        }
        if(node.getExp() != null)
        {
            node.getExp().apply(this);
            op1 = (SaExp) this.returnValue;
        }
        if(node.getPointVirgule() != null)
        {
            node.getPointVirgule().apply(this);
        }
        this.returnValue = new SaInstRetour(op1);
        outARetourInstruction(node);
    }

    public void caseAEcrireInstruction(AEcrireInstruction node)
    {
        SaExp op1 = null;
        inAEcrireInstruction(node);
        if(node.getEcrire() != null)
        {
            node.getEcrire().apply(this);
        }
        if(node.getParentheseOuvrante() != null)
        {
            node.getParentheseOuvrante().apply(this);
        }
        if(node.getExp() != null)
        {
            node.getExp().apply(this);
            op1 = (SaExp) this.returnValue;
        }
        if(node.getParentheseFermante() != null)
        {
            node.getParentheseFermante().apply(this);
        }
        if(node.getPointVirgule() != null)
        {
            node.getPointVirgule().apply(this);
        }
        this.returnValue = new SaInstEcriture(op1);
        outAEcrireInstruction(node);
    }

    public void caseADefinitionFonctionDf(ADefinitionFonctionDf node)
    {
        String id = null;
        SaLDecVar op1 = null;
        SaLDecVar op2 = null;
        SaInst op3 = null;

        inADefinitionFonctionDf(node);
        if(node.getTypeopt() != null)
        {
            node.getTypeopt().apply(this);
        }
        if(node.getIdentifiant() != null)
        {
            id = node.getIdentifiant().getText();
        }
        if(node.getParentheseOuvrante() != null)
        {
            node.getParentheseOuvrante().apply(this);
        }
        if(node.getParam() != null)
        {
            node.getParam().apply(this);
            op1 = (SaLDecVar) this.returnValue;
        }
        if(node.getParentheseFermante() != null)
        {
            node.getParentheseFermante().apply(this);
        }
        if(node.getVarloc() != null)
        {
            node.getVarloc().apply(this);
            op2 = (SaLDecVar) this.returnValue;
        }
        if(node.getBloc() != null)
        {
            node.getBloc().apply(this);
            op3 = (SaInst) this.returnValue;
        }
        this.returnValue = new SaDecFonc(id,this.returnType,op1,op2,op3);
        outADefinitionFonctionDf(node);
    }

    public void caseAListVariableLdv(AListVariableLdv node)
    {
        SaDecVar op2 = null;
        SaLDecVar op1 = null;

        inAListVariableLdv(node);
        if(node.getDeclaration() != null)
        {
            node.getDeclaration().apply(this);
            op2 = (SaDecVar) this.returnValue;
        }
        if(node.getLdvp() != null)
        {
            node.getLdvp().apply(this);
            op1 = (SaLDecVar) this.returnValue;
        }
        this.returnValue = new SaLDecVar(op2,op1);

        outAListVariableLdv(node);
    }

    public void caseALdv(ALdv node)
    {
        inALdv(node);
        if(node.getEps() != null)
        {
            node.getEps().apply(this);
        }
        this.returnValue = null;
        outALdv(node);
    }

    public void caseAListVariable1Ldvp(AListVariable1Ldvp node)
    {
        SaDecVar op1 = null;
        SaLDecVar op2 = null;
        inAListVariable1Ldvp(node);
        if(node.getVirgule() != null)
        {
            node.getVirgule().apply(this);
        }
        if(node.getDeclaration() != null)
        {
            node.getDeclaration().apply(this);
            op1 = (SaDecVar) this.returnValue;
        }
        if(node.getLdvp() != null)
        {
            node.getLdvp().apply(this);
            op2 = (SaLDecVar) this.returnValue;
        }
        this.returnValue = new SaLDecVar(op1,op2);
        outAListVariable1Ldvp(node);
    }
    public void caseALdvp(ALdvp node)
    {
        inALdvp(node);
        if(node.getEps() != null)
        {
            node.getEps().apply(this);
        }
        this.returnValue = null;
        outALdvp(node);
    }

    public void caseAListDeclarationFonctionLdf(AListDeclarationFonctionLdf node)
    {
        SaDecFonc op1 = null;
        SaLDecFonc op2 = null;
        inAListDeclarationFonctionLdf(node);
        if(node.getDf() != null)
        {
            node.getDf().apply(this);
            op1 = (SaDecFonc) this.returnValue;
        }
        if(node.getLdf() != null)
        {
            node.getLdf().apply(this);
            op2 = (SaLDecFonc) this.returnValue;
        }
        this.returnValue = new SaLDecFonc(op1,op2);
        outAListDeclarationFonctionLdf(node);
    }

    public void caseALdf(ALdf node)
    {
        inALdf(node);
        if(node.getEps() != null)
        {
            node.getEps().apply(this);
        }
        this.returnValue = null;
        outALdf(node);
    }

    public void caseAAfAppelFonction(AAfAppelFonction node)
    {
        String id = null;
        SaLExp op1 = null;
        inAAfAppelFonction(node);
        if(node.getIdentifiant() != null)
        {
            id = node.getIdentifiant().getText();
        }
        if(node.getParentheseOuvrante() != null)
        {
            node.getParentheseOuvrante().apply(this);
        }
        if(node.getListExpression() != null)
        {
            node.getListExpression().apply(this);
            op1 = (SaLExp) this.returnValue;
        }
        if(node.getParentheseFermante() != null)
        {
            node.getParentheseFermante().apply(this);
        }
        this.returnValue = new SaAppel(id, op1);
        outAAfAppelFonction(node);
    }
}
