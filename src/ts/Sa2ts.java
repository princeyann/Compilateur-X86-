package ts;
import sa.*;
import util.Error;

public class Sa2ts extends SaDepthFirstVisitor <Void> {
    enum Context {
	LOCAL,
	GLOBAL,
	PARAM
    }
    
    private Ts tableGlobale;
    private Ts tableLocaleCourante;
    private Context context; //si on est dans une decfonc on le fait passé a context a local 
    
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


    public Void visit(SaDecVar node) throws Exception {
        // Récupérer l'identifiant de la variable
        String identif = node.getNom();




        // Vérifier dans quel contexte on se trouve (global, local ou paramètre)
        switch (context) {
            case GLOBAL:
                // Vérifier si la variable existe déjà dans la table globale
                if (tableGlobale.getVar(identif) != null) {
                    throw new Exception("Erreur: Variable " + identif + " déjà déclarée dans la portée globale.");
                }
                tableGlobale.addVar(identif, node.getType());
                break;

            case LOCAL:
                // Vérifier si la variable existe déjà dans la table locale
                if (tableLocaleCourante.getVar(identif) != null) {
                    throw new Exception("Erreur: Variable " + identif + " déjà déclarée dans la portée locale.");
                }
                tableLocaleCourante.addVar(identif, node.getType());
                break;

            case PARAM:
                // Vérifier si le paramètre est déjà déclaré
                if (tableLocaleCourante.getVar(identif) != null) {
                    throw new Exception("Erreur: Paramètre " + identif + " déjà déclaré dans la fonction.");
                }
                tableLocaleCourante.addParam(identif, node.getType());
                break;
        }

        return null;
    }
    public Void visit(SaDecTab node) throws Exception {
// Récupérer l'identifiant du tableau
        String identif = node.getNom();
        int taille = node.getTaille();
        if (taille<0){
            throw new Exception("Erreur: la taille doit etre positive.");
        }


        switch (context) {
            case GLOBAL:
                // Vérifier si la variable existe déjà dans la table globale
                if (tableGlobale.getVar(identif) != null) {
                    throw new Exception("Erreur: Variable " + identif + " déjà déclarée dans la portée globale.");
                }
                tableGlobale.addTab(identif,node.getType(), taille);
                break;

            case LOCAL:
                // Vérifier si la variable existe déjà dans la table locale
                if (tableLocaleCourante.getVar(identif) != null) {
                    throw new Exception("Erreur: Variable " + identif + " déjà déclarée dans la portée locale.");
                }
                tableLocaleCourante.addTab(identif,node.getType(), taille);
                break;

            case PARAM:
                // Vérifier si le paramètre est déjà déclaré
                if (tableLocaleCourante.getVar(identif) != null) {
                    throw new Exception("Erreur: Paramètre " + identif + " déjà déclaré dans la fonction.");
                }
                tableLocaleCourante.addTab(identif,node.getType(), taille);
                break;
        }

        return null;

    }


    public Void visit(SaDecFonc node)  throws Exception {
        // Récupérer l'identifiant de la fonction
        String identif = node.getNom();
        SaLDecVar params = node.getParametres();
        SaInst bloc = node.getCorps();

        // Vérifier si la fonction existe déjà dans la table globale
        if (tableGlobale.getFct(identif) != null) {
            throw new Exception("Erreur: Fonction " + identif + " déjà déclarée.");
        }

        // Créer une nouvelle table des symboles locale pour la fonction
        Ts tableLocale = new Ts();

        // Sauvegarder l'ancienne table locale et mettre à jour le contexte
        Ts ancienneTableLocale = tableLocaleCourante;
        tableLocaleCourante = tableLocale;
        Context ancienContexte = context;
        context = Context.PARAM;

        // Ajouter les paramètres à la table locale
        if (params != null) {
            params.accept(this);
        }

        // Passer au contexte LOCAL pour ajouter les variables locales
        context = Context.LOCAL;
        if (node.getVariable() != null) {
            node.accept(this);
        }

        // Ajouter la fonction à la table globale
        tableGlobale.addFct(identif, node.getTypeRetour(), (params != null ? params.length() : 0), tableLocale, node);

        // Visiter le bloc de la fonction
        if (bloc != null) {
            bloc.accept(this);
        }

        // Restaurer l'ancienne table locale et le contexte
        tableLocaleCourante = ancienneTableLocale;
        context = ancienContexte;

        return null;
    }


    public Void visit(SaVarSimple node) throws Exception {
        // Récupérer l'identifiant de la variable
        String identif = node.getNom();
        TsItemVar var = null;

        // Vérifier dans l'ordre : locale -> paramètre -> globale
        if (tableLocaleCourante != null) {
            var = tableLocaleCourante.getVar(identif);
        }
        if (var == null) {
            var = tableGlobale.getVar(identif);
        }

        // Si la variable n'existe pas, lever une erreur
        if (var == null) {
            throw new Exception("Erreur: Variable " + identif + " non déclarée.");
        }

        return null;
    }


}


