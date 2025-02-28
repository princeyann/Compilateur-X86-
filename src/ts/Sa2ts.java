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




}


