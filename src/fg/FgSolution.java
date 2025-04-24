package fg;
import util.graph.*;
import nasm.*;
import util.intset.*;
import java.io.*;
import java.util.*;

public class FgSolution{
    int iterNum = 0;
    public Nasm nasm;
    Fg fg;
    public Map< NasmInst, IntSet> use;
    public Map< NasmInst, IntSet> def;
    public Map< NasmInst, IntSet> in;
    public Map< NasmInst, IntSet> out;
    
    public FgSolution(Nasm nasm, Fg fg){
	this.nasm = nasm;
	this.fg = fg;
	this.use = new HashMap< NasmInst, IntSet>();
	this.def = new HashMap< NasmInst, IntSet>();
	this.in =  new HashMap< NasmInst, IntSet>();
	this.out = new HashMap< NasmInst, IntSet>();
		for (Iterator<NasmInst> it = nasm.sectionText.iterator(); it.hasNext();){
			NasmInst inst = it.next();


			use.put(inst,new IntSet(nasm.getTempCounter()));
			def.put(inst,new IntSet(nasm.getTempCounter()));
			in.put(inst,new IntSet(nasm.getTempCounter()));
			out.put(inst,new IntSet(nasm.getTempCounter()));

			if (inst.srcDef && inst.source.isGeneralRegister()){
				NasmRegister source = (NasmRegister) inst.source;
				def.get(inst).add(source.val);
			}
			if(inst.srcUse && inst.source.isGeneralRegister()){
				NasmRegister source = (NasmRegister) inst.source;
				use.get(inst).add(source.val);
			}
			if (inst.source instanceof NasmAddress op ){
				List<NasmRegister> sources = op.val.getRegisters();
				for (NasmRegister dest : sources) {
					if (dest.isGeneralRegister()){
						use.get(inst).add(dest.val);
					}
				}
			}

			if (inst.destDef && inst.destination.isGeneralRegister()){
				NasmRegister destination = (NasmRegister) inst.destination;
				def.get(inst).add(destination.val);
			}
			if(inst.destUse && inst.destination.isGeneralRegister()){
				NasmRegister destination = (NasmRegister) inst.destination;
				use.get(inst).add(destination.val);
			}
			if (inst.destination instanceof NasmAddress op && op.val != null){
				List<NasmRegister> destination = op.val.getRegisters();
				for (NasmRegister dest : destination) {
					if (dest.isGeneralRegister()){
						use.get(inst).add(dest.val);
					}
				}
			}

		}

		boolean until;

		do {
			iterNum++;
			until = false;
			for (Iterator<NasmInst> it = nasm.sectionText.iterator(); it.hasNext();) {
				NasmInst inst = it.next();
				IntSet inCopy = in.get(inst).copy();
				IntSet outCopy = out.get(inst).copy();

				//on calcul in(s) = use(s) ∪ (out(s) − def(s))
				IntSet inInst = use.get(inst).union(out.get(inst).minus(def.get(inst)));
				in.put(inst, inInst);

				//on calcul out(s) = union des in de tous les successeurs
				IntSet outInst = new IntSet(nasm.getTempCounter());
				Node node = fg.inst2Node.get(inst);
				for (NodeList succ = node.succ(); succ != null; succ = succ.tail) {
					NasmInst succInst = fg.node2Inst.get(succ.head);
					outInst = outInst.union(in.get(succInst));
				}
				out.put(inst, outInst);

				if (!inInst.equal(inCopy) || !outInst.equal(outCopy)) {
					until = true;
				}
			}

		}while (until);

    }
    
    public void affiche(String baseFileName){
	String fileName;
	PrintStream out = System.out;

	if (baseFileName != null){
	    try {
		baseFileName = baseFileName;
		fileName = baseFileName + ".fgs";
		out = new PrintStream(fileName);
	    }
	    
	    catch (IOException e) {
		System.err.println("Error: " + e.getMessage());
	    }
	}
	
	out.println("iter num = " + iterNum);
	for(NasmInst nasmInst : this.nasm.sectionText){
	    out.println("use = "+ this.use.get(nasmInst) + " def = "+ this.def.get(nasmInst) + "\tin = " + this.in.get(nasmInst) + "\t \tout = " + this.out.get(nasmInst) + "\t \t" + nasmInst);
	}
	
    }

}
