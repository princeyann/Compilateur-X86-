package ts;
import sa.*;
import util.Error;
import util.Type;

public class Sa2ts extends SaDepthFirstVisitor <Void> {
    enum Context {
	LOCAL,
	GLOBAL,
	PARAM
    }
    
    private Ts tableGlobale;
    private Ts tableLocaleCourante;
    private Context context;
    
    public Ts getTableGlobale(){return this.tableGlobale;}

    public Sa2ts()
    {
	tableGlobale = new Ts();
	tableLocaleCourante = null;
	context = Context.GLOBAL;
    }

    public void defaultIn(SaNode node)
    {
	//	System.out.println("<" + node.getClass().getSimpleName() + ">");
    }

    public void defaultOut(SaNode node)
    {
	//	System.out.println("</" + node.getClass().getSimpleName() + ">");
    }


    public Void visit(SaDecVar node) throws ErrorException {
        String identif = node.getNom();
        Type type = node.getType();

        Ts currentTable = tableGlobale;
        if (context != Context.GLOBAL){
            currentTable = tableLocaleCourante;
        }
        if(currentTable.getVar(identif) != null){
            throw new ErrorException(Error.TS, "Erreur: Variable " + identif + " déjà déclarée.");
        }
        if (context == Context.GLOBAL){
            tableGlobale.addVar(identif, type);
        }else if(context == Context.LOCAL) {
            tableLocaleCourante.addVar(identif, type);
        }else if(context == Context.PARAM){
            tableLocaleCourante.addParam(identif, type);
        }

        return null;
    }
    public Void visit(SaDecTab node) throws Exception {
        String identif = node.getNom();
        int taille = node.getTaille();
        Type type = node.getType();

        if (taille<0){
            throw new ErrorException(Error.TS,"Erreur: la taille doit etre positive.");
        }
        Ts currentTable = tableGlobale;
        if (context != Context.GLOBAL){
            currentTable = tableLocaleCourante;
        }
        if (currentTable.getVar(identif) != null){
            throw new ErrorException(Error.TS,"Erreur l'id "+identif+" existe déja." );
        }
        TsItemVarTab tsItemVarTab = null;
        if (context == Context.GLOBAL){
            tsItemVarTab= (TsItemVarTab) tableGlobale.addTab(identif,type,taille);
        }else {

            tsItemVarTab = (TsItemVarTab) tableLocaleCourante.addTab(identif,type,taille);
        }
        node.tsItem = tsItemVarTab;
        return null;
    }


    public Void visit(SaDecFonc node) throws Exception {
        String identif = node.getNom();
        SaLDecVar params = node.getParametres();
        SaInst bloc = node.getCorps();
        Type typeRetour = node.getTypeRetour();

        if (tableGlobale.getFct(identif) != null) {
            throw new ErrorException(Error.TS,"Erreur: Fonction " + identif + " déjà déclarée.");
        }
        Ts ancienneTableLocale = tableLocaleCourante;
        tableLocaleCourante = new Ts();
        Context ancienContexte = context;
        context = Context.PARAM;

        if (params != null){
            params.accept(this);
        }

        context = Context.LOCAL;
        if (node.getVariable() != null){
            node.getVariable().accept(this);
        }
        TsItemFct fonction = tableGlobale.addFct(identif,typeRetour,(params != null ? params.length() : 0),tableLocaleCourante,node);

        if (bloc != null) {
            bloc.accept(this);
        }
        node.tsItem = fonction;
        tableLocaleCourante = ancienneTableLocale;
        context = ancienContexte;

        return null;
    }


    public Void visit(SaVarSimple node) throws Exception {
        String identif = node.getNom();
        TsItemVar var = null;
        if (tableLocaleCourante != null) {
            var = tableLocaleCourante.getVar(identif);
        }
        if (var == null) {
            var = tableGlobale.getVar(identif);
        }
        if (var == null) {
            throw new ErrorException(Error.TS,"Erreur: Variable " + identif + " non déclarée.");
        }
        node.tsItem = (TsItemVarSimple) var;

        return null;
    }
    public Void visit(SaVarIndicee node) throws Exception {
        String identif = node.getNom();

        TsItemVar var = (tableLocaleCourante != null) ? tableLocaleCourante.getVar(identif) : null;
        if (var == null) var = tableGlobale.getVar(identif);

        if (var == null) {
            throw new ErrorException(Error.TS, "Tableau non déclaré : " + identif);
        }
        if (!(var instanceof TsItemVarTab)) {
            throw new ErrorException(Error.TS, "La variable " + identif + " n'est pas un tableau.");
        }

        if (node.getIndice() != null) {
            node.getIndice().accept(this);
        } else {
            throw new ErrorException(Error.TS, "Un tableau doit être indexé.");
        }
        node.tsItem = var;

        return null;
    }
    public Void visit(SaAppel node) throws Exception {
        String identif = node.getNom();
        TsItemFct fonction = tableGlobale.getFct(identif);

        if (fonction == null) {
            throw new ErrorException(Error.TS, "la fonction "+ identif+ " n'est pas déclarée " );
        }

        int nbArgsAppeles = 0;
        if (node.getArguments() != null){
            nbArgsAppeles = node.getArguments().length();
        }
        if (nbArgsAppeles != fonction.getNbArgs()) {
            throw new ErrorException(Error.TS, "Mauvais nombre d'arguments pour la fonction " + identif +" attendu :" +fonction.nbArgs +" est la valeur est :"+ nbArgsAppeles);
        }

        if (node.getArguments() != null) {
            node.getArguments().accept(this);
        }
        node.tsItem = fonction;

        return null;
    }



}


