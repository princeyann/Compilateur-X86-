package fg;
import nasm.*;
import util.graph.*;
import java.util.*;
import java.io.*;

public class Fg implements NasmVisitor <Void> {
    public Nasm nasm;
    public Graph graph;
    Map< NasmInst, Node> inst2Node;
    Map< Node, NasmInst> node2Inst;
    Map< String, NasmInst> label2Inst;

    public Fg(Nasm nasm){
	this.nasm = nasm;
	this.inst2Node = new HashMap< NasmInst, Node>();
	this.node2Inst = new HashMap< Node, NasmInst>();
	this.label2Inst = new HashMap< String, NasmInst>();
	this.graph = new Graph();


        //on crée d'abord les sommets
        for (NasmInst instuction : nasm.sectionText){
            Node node = graph.newNode();
            this.inst2Node.put(instuction,node);
            this.node2Inst.put(node,instuction);
            if (instuction.label != null){
                this.label2Inst.put(instuction.label.toString(),instuction);
            }
        }
        // on creé les arcs
        for (Iterator<NasmInst> it = nasm.sectionText.iterator(); it.hasNext();){
            NasmInst inst = it.next();
            Node currentNode = inst2Node.get(inst);
            if(currentNode.label() == nasm.sectionText.size()){
                break;
            }

            if(inst instanceof NasmJe || inst instanceof NasmJg || inst instanceof NasmJl || inst instanceof NasmJmp ||
                    inst instanceof NasmJge || inst instanceof NasmJle || inst instanceof NasmJne || inst instanceof NasmCall
            ) {

                Node nodeJe = inst2Node.getOrDefault(label2Inst.get(inst.address.toString()),null);
                if(nodeJe != null){
                    graph.addEdge(currentNode, nodeJe);
                }

            }
            if (!(inst instanceof NasmRet || inst instanceof NasmCall || inst instanceof NasmJmp)){
                if (currentNode.label()+1 < nasm.sectionText.size()){
                    graph.addEdge(currentNode, inst2Node.get(nasm.sectionText.get(currentNode.label()+1)));

                }
            }

        }
    }

    public void affiche(String baseFileName){
	String fileName;
	PrintStream out = System.out;

	if (baseFileName != null){
	    try {
		baseFileName = baseFileName;
		fileName = baseFileName + ".fg";
		out = new PrintStream(fileName);
	    }
	    
	    catch (IOException e) {
		System.err.println("Error: " + e.getMessage());
	    }
	}
	
	for(NasmInst nasmInst : nasm.sectionText){
	    Node n = this.inst2Node.get(nasmInst);
	    out.print(n + " : ( ");
	    for(NodeList q=n.succ(); q!=null; q=q.tail) {
		out.print(q.head.toString());
		out.print(" ");
	    }
	    out.println(")\t" + nasmInst);
	}
	
    }
    
    public Void visit(NasmAdd inst){return null;}
    public Void visit(NasmCall inst){return null;}
    public Void visit(NasmDiv inst){return null;}
    public Void visit(NasmJe inst){return null;}
    public Void visit(NasmJle inst){return null;}
    public Void visit(NasmJne inst){return null;}
    public Void visit(NasmMul inst){return null;}
    public Void visit(NasmOr inst){return null;}
    public Void visit(NasmCmp inst){return null;}
    public Void visit(NasmInst inst){return null;}
    public Void visit(NasmJge inst){return null;}
    public Void visit(NasmJl inst){return null;}
    public Void visit(NasmNot inst){return null;}
    public Void visit(NasmPop inst){return null;}
    public Void visit(NasmRet inst){return null;}
    public Void visit(NasmXor inst){return null;}
    public Void visit(NasmAnd inst){return null;}
    public Void visit(NasmJg inst){return null;}
    public Void visit(NasmJmp inst){return null;}
    public Void visit(NasmMov inst){return null;}
    public Void visit(NasmPush inst){return null;}
    public Void visit(NasmSub inst){return null;}
    public Void visit(NasmEmpty inst){return null;}
    public Void visit(NasmInt inst){return null;}
	public Void visit(NasmAddress operand){return null;}
    public Void visit(NasmConstant operand){return null;}
    public Void visit(NasmLabel operand){return null;}
    public Void visit(NasmRegister operand){return null;}
	public Void visit(NasmResb pseudoInst){return null;}
    public Void visit(NasmResw pseudoInst){return null;}
    public Void visit(NasmResd pseudoInst){return null;}
    public Void visit(NasmResq pseudoInst){return null;}
    public Void visit(NasmRest pseudoInst){return null;}
	public Void visit(NasmExpPlus exp){return null;}
	public Void visit(NasmExpMinus exp){return null;}
	public Void visit(NasmExpTimes exp){return null;}
	public Void visit(NasmExp exp){return null;}


}
