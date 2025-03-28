package nasm;
import java.util.*;
import ts.*;
import c3a.*;

public class C3a2nasm implements C3aVisitor <NasmOperand> {
    private C3a c3a;
    private Nasm nasm;
    private Ts tableGlobale;
    private TsItemFct currentFct;
    private NasmRegister esp;
    private NasmRegister ebp;

    
    public C3a2nasm(C3a c3a, Ts tableGlobale){
	this.c3a = c3a;
	nasm = new Nasm(tableGlobale);
	nasm.setTempCounter(c3a.getTempCounter());

	this.tableGlobale = tableGlobale;
	this.currentFct = null;
	esp = new NasmRegister(-1);
	esp.colorRegister(Nasm.REG_ESP);

	ebp = new NasmRegister(-1);
	ebp.colorRegister(Nasm.REG_EBP);
    }
    
    public Nasm getNasm(){return nasm;}
    
    public NasmOperand getLabelFromC3aInst(C3aInst inst){
	return (inst.label != null) ? inst.label.accept(this) : null;
    }
    
    public NasmOperand visit(C3a c3a){
        for(C3aInst inst : c3a.listeInst) {
            inst.accept(this);
        }
        return null;
    }

    public NasmOperand visit(C3aInstAdd inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand oper1 = inst.op1.accept(this);
        NasmOperand oper2 = inst.op2.accept(this);
        NasmOperand dest = inst.result.accept(this);
        nasm.ajouteInst(new NasmMov(label, dest, oper1, ""));
        nasm.ajouteInst(new NasmAdd(null, dest, oper2, ""));
        return null;
    }
    public NasmOperand visit(C3aInstCall inst){

        return null;
    }

    public NasmOperand visit(C3aInstFBegin inst){

        //inst.label.accept(this);
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;

        nasm.ajouteInst(new NasmPush(null, ebp, ""));
        nasm.ajouteInst(new NasmMov(null, ebp, esp, ""));

        nasm.ajouteInst(new NasmSub(label, esp, new NasmConstant(4*currentFct.getTable().variables.size()), ""));

        return null;
    }
    public NasmOperand visit(C3aInst inst){
        nasm.ajouteInst(new NasmAdd(null, esp, new NasmConstant(4), ""));
        nasm.ajouteInst(new NasmPop(null, ebp, ""));
        nasm.ajouteInst(new NasmRet(null, ""));

        return null;}

    public NasmOperand visit(C3aInstJumpIfLess inst){
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand op1 = inst.op1.accept(this);
        NasmOperand op2 = inst.op2.accept(this);
        // On traduit en comparaison puis saut conditionnel (JL)
        nasm.ajouteInst(new NasmCmp(label, op1, op2, ""));
        // Supposons que l'étiquette de destination se trouve dans inst.result
        NasmOperand destLabel = inst.result.accept(this);
        nasm.ajouteInst(new NasmJl(null, destLabel, ""));
        return null;
    }

    public NasmOperand visit(C3aInstMult inst){
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand op1 = inst.op1.accept(this);
        NasmOperand op2 = inst.op2.accept(this);
        NasmOperand dest = inst.result.accept(this);
        nasm.ajouteInst(new NasmMov(label, dest, op1, ""));
        nasm.ajouteInst(new NasmMul(null, dest, op2, ""));
        return null;
    }
    public NasmOperand visit(C3aInstRead inst){return null;}
    public NasmOperand visit(C3aInstSub inst){
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand oper1 = inst.op1.accept(this);
        NasmOperand oper2 = inst.op2.accept(this);
        NasmOperand dest = inst.result.accept(this);
        nasm.ajouteInst(new NasmMov(label, dest, oper1, ""));
        nasm.ajouteInst(new NasmSub(null, dest, oper2, ""));
        return null;
    }
    public NasmOperand visit(C3aInstAffect inst){
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;

        NasmOperand source = inst.op1.accept(this);
        NasmOperand dest = inst.result.accept(this);

        nasm.ajouteInst(new NasmMov(label, dest, source, ""));
        return null;
    }

    public NasmOperand visit(C3aInstDiv inst){
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;

        NasmRegister reg_eax = nasm.newRegister();
        reg_eax.colorRegister(Nasm.REG_EAX);

        NasmOperand oper1 = inst.op1.accept(this);
        NasmOperand oper2 = inst.op2.accept(this);
        NasmOperand result = inst.result.accept(this);
        nasm.ajouteInst(new NasmMov(label,reg_eax, oper1, ""));
        nasm.ajouteInst(new NasmDiv(null, oper2, ""));
        nasm.ajouteInst(new NasmMov(label,result,reg_eax,""));
        return null;}

    public NasmOperand visit(C3aInstFEnd inst){
        return null;
    }
    public NasmOperand visit(C3aInstJumpIfEqual inst){return null;}
    public NasmOperand visit(C3aInstJumpIfNotEqual inst){return null;}
    public NasmOperand visit(C3aInstJump inst){return null;}
    public NasmOperand visit(C3aInstParam inst){return null;}
    public NasmOperand visit(C3aInstReturn inst){return null;}
    public NasmOperand visit(C3aInstWrite inst){return null;}
    public NasmOperand visit(C3aInstStop inst){return null;}

    public NasmOperand visit(C3aConstant oper){return null;}
    public NasmOperand visit(C3aBooleanConstant oper){return null;}
    public NasmOperand visit(C3aLabel oper){return null;}
    public NasmOperand visit(C3aTemp oper){return null;}
    public NasmOperand visit(C3aVar oper){return null;}
    public NasmOperand visit(C3aFunction oper){return null;}
    
}
