package ig;

import fg.*;
import nasm.*;
import util.graph.*;
import util.intset.*;
import java.util.*;
import java.io.*;

public class Ig {
    public Graph graph;
    public FgSolution fgs;
    public int regNb;
    public Nasm nasm;
    public Node int2Node[];
    
    
    public Ig(FgSolution fgs){
	this.fgs = fgs;
 	this.graph = new Graph();
	this.nasm = fgs.nasm;
	this.regNb = this.nasm.getTempCounter();
	this.int2Node = new Node[regNb];
	this.build();

    }
	public void build(){
		for(NasmInst inst : fgs.in.keySet()) {
			IntSet liveSet = fgs.in.get(inst);
			List<Node> nodeList = new ArrayList<>();

			for(int reg =0; reg < liveSet.getSize(); reg++) {
				if (int2Node[reg] == null) {
					int2Node[reg] = graph.newNode();
				}
				if (liveSet.isMember(reg)){
					nodeList.add(int2Node[reg]);
				}
			}

			for (int i = 0; i < nodeList.size();i++){
				for (int j = 0; j < nodeList.size(); j++) {
					if (i!=j){
						graph.addEdge(nodeList.get(i), nodeList.get(j));
					}
				}
			}
		}
	}

	public int[] getPrecoloredTemporaries() {
		int[] couleur = new int[regNb];
		Arrays.fill(couleur, -1);

		for (NasmInst inst : nasm.sectionText) {
			if (inst.source instanceof NasmRegister) {
				NasmRegister reg = (NasmRegister) inst.source;
				if (reg.isGeneralRegister()) {
					couleur[reg.val] = reg.color;
				}
			}
			if (inst.destination instanceof NasmRegister) {
				NasmRegister reg = (NasmRegister) inst.destination;
				if (reg.isGeneralRegister()) {
					couleur[reg.val] = reg.color;
				}
			}
		}
		return couleur;
	}
	public void allocateRegisters() {
		ColorGraph colorGraph = new ColorGraph(graph, 4, getPrecoloredTemporaries());
		colorGraph.color();
		int[] color = colorGraph.color;

		for (int i = 1; i < nasm.sectionText.size(); i++) {

			if (nasm.sectionText.get(i).source instanceof NasmRegister nasmRegister) {
					if (nasmRegister.isGeneralRegister() && nasmRegister.val != -1)
						nasmRegister.colorRegister(color[nasmRegister.val]);
			}

			if (nasm.sectionText.get(i).destination instanceof NasmRegister nasmRegister) {
				if (nasmRegister.isGeneralRegister() && nasmRegister.val != -1)
					nasmRegister.colorRegister(color[nasmRegister.val]);
			}
		}
	}

	public void affiche(String baseFileName){
	String fileName;
	PrintStream out = System.out;
	
	if (baseFileName != null){
	    try {
		baseFileName = baseFileName;
		fileName = baseFileName + ".ig";
		out = new PrintStream(fileName);
	    }
	    
	    catch (IOException e) {
		System.err.println("Error: " + e.getMessage());
	    }
	}
	
	for(int i = 0; i < regNb; i++){
	    Node n = this.int2Node[i];
	    out.print(n + " : ( ");
	    for(NodeList q=n.succ(); q!=null; q=q.tail) {
		out.print(q.head.toString());
		out.print(" ");
	    }
	    out.println(")");
	}
    }
}
	    
    

    
    
