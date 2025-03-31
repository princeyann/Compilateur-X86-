package nasm;
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
        for (C3aInst c3a1 : c3a.listeInst)
            c3a1.accept(this);
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
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        nasm.ajouteInst(new NasmSub(label, esp, new NasmConstant(4),""));
        nasm.ajouteInst(new NasmCall(null, new NasmLabel(inst.op1.val.getIdentif()),""));
        nasm.ajouteInst(new NasmPop(null, inst.result.accept(this)," "));

        int nbArgs = inst.op1.val.getNbArgs();
        if (nbArgs > 0){
            nasm.ajouteInst(new NasmAdd(null, esp, new NasmConstant(nbArgs*4),""));
        }
        return null;
    }

    public NasmOperand visit(C3aInstFBegin inst){
        currentFct = inst.val;
        nasm.ajouteInst(new NasmPush(new NasmLabel(currentFct.identif),ebp," "));
        nasm.ajouteInst(new NasmMov(null,ebp,esp," "));

        NasmRegister reg_eax = nasm.newRegister();
        reg_eax.colorRegister(Nasm.REG_EAX);

        NasmRegister reg_ebx = nasm.newRegister();
        reg_ebx.colorRegister(Nasm.REG_EBX);

        NasmRegister reg_ecx = nasm.newRegister();
        reg_ecx.colorRegister(Nasm.REG_ECX);

        NasmRegister reg_edx = nasm.newRegister();
        reg_edx.colorRegister(Nasm.REG_EDX);

        nasm.ajouteInst(new NasmPush(null,reg_eax," "));
        nasm.ajouteInst(new NasmPush(null,reg_ebx," "));
        nasm.ajouteInst(new NasmPush(null,reg_ecx," "));
        nasm.ajouteInst(new NasmPush(null,reg_edx," "));

        nasm.ajouteInst(new NasmSub(null,esp,new NasmConstant(currentFct.table.getAdrVarCourante()),""));
        return null;
    }
    public NasmOperand visit(C3aInst inst){return null;}
    public NasmOperand visit(C3aInstJumpIfLess inst){return null;}

    public NasmOperand visit(C3aInstMult inst){
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand op1 = inst.op1.accept(this);
        NasmOperand op2 = inst.op2.accept(this);
        NasmOperand dest = inst.result.accept(this);
        nasm.ajouteInst(new NasmMov(label, dest, op1, ""));
        nasm.ajouteInst(new NasmMul(null, dest, op2, ""));
        return null;
    }

    public NasmOperand visit(C3aInstRead inst){
        NasmOperand op = inst.result.accept(this);
        NasmRegister eax = nasm.newEaxRegister();
        eax.colorRegister(Nasm.REG_EAX);
        nasm.ajouteInst(new NasmMov(null,eax,op,""));
        nasm.ajouteInst(new NasmCall(null,new NasmLabel("readline"),""));
        nasm.ajouteInst(new NasmCall(null,new NasmLabel("atoi"),""));
        return null;
    }

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
        NasmOperand oper1 = inst.op1.accept(this);
        NasmOperand result = inst.result.accept(this);
        nasm.ajouteInst(new NasmMov(label,result,oper1,"" ));
        return null;
    }


    public NasmOperand visit(C3aInstDiv inst){

        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;

        NasmRegister eax = nasm.newRegister();
        eax.colorRegister(Nasm.REG_EAX);

        NasmRegister ebx = nasm.newRegister();
        ebx.colorRegister(Nasm.REG_EBX);

        nasm.ajouteInst(new NasmMov(label,ebx,new NasmConstant(0),""));

        nasm.ajouteInst(new NasmMov(null,eax,inst.op1.accept(this),""));
        nasm.ajouteInst(new NasmMov(null, inst.result.accept(this),inst.op2.accept(this),""));

        nasm.ajouteInst(new NasmDiv(null,inst.result.accept(this),""));

        nasm.ajouteInst(new NasmMov(null,ebx,ebx,""));

        nasm.ajouteInst(new NasmMov(null,inst.result.accept(this),eax,""));
        return null;

    }

    public NasmOperand visit(C3aInstFEnd inst){
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;

        nasm.ajouteInst(new NasmAdd(label, esp, new NasmConstant(currentFct.table.getAdrVarCourante()),""));

        NasmRegister reg_eax = nasm.newRegister();
        reg_eax.colorRegister(Nasm.REG_EAX);

        NasmRegister reg_ebx = nasm.newRegister();
        reg_ebx.colorRegister(Nasm.REG_EBX);

        NasmRegister reg_ecx = nasm.newRegister();
        reg_ecx.colorRegister(Nasm.REG_ECX);

        NasmRegister reg_edx = nasm.newRegister();
        reg_edx.colorRegister(Nasm.REG_EDX);

        nasm.ajouteInst(new NasmPop(null,reg_eax,""));
        nasm.ajouteInst(new NasmPop(null,reg_ebx,""));
        nasm.ajouteInst(new NasmPop(null,reg_ecx,""));
        nasm.ajouteInst(new NasmPop(null,reg_edx,""));
        nasm.ajouteInst(new NasmPop(null,ebp,""));
        nasm.ajouteInst(new NasmRet(null," "));

        return null;
    }
    public NasmOperand visit(C3aInstJumpIfEqual inst){return null;}
    public NasmOperand visit(C3aInstJumpIfNotEqual inst){return null;}
    public NasmOperand visit(C3aInstJump inst){return null;}

    public NasmOperand visit(C3aInstParam inst){
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand parms = inst.op1.accept(this);
        nasm.ajouteInst(new NasmPush(label,parms,""));

        return null;
        }

    public NasmOperand visit(C3aInstReturn inst){
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand retour = inst.op1.accept(this);
        nasm.ajouteInst(new NasmMov(label, new NasmAddress(ebp,NasmSize.DWORD),retour,""));
        return null;
    }

    public NasmOperand visit(C3aInstWrite inst){
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;

        NasmOperand oper1 = inst.op1.accept(this);
        NasmRegister reg_eax = nasm.newRegister();
        reg_eax.colorRegister(Nasm.REG_EAX);
        nasm.ajouteInst(new NasmMov(label,reg_eax,oper1,""));
        nasm.ajouteInst(new NasmCall(null,new NasmLabel("iprintLF"),""));

        return null;
    }
    @Override
    public NasmOperand visit(C3aInstStop inst) {
        NasmRegister ebx = nasm.newRegister();
        ebx.colorRegister(Nasm.REG_EBX);

        NasmRegister eax = nasm.newRegister();
        eax.colorRegister(Nasm.REG_EAX);

        NasmOperand src = inst.returnValue.accept(this);
        nasm.ajouteInst(new NasmMov(null, ebx, src, ""));

        nasm.ajouteInst(new NasmMov(null, eax, new NasmConstant(1), ""));
        nasm.ajouteInst(new NasmInt(null, ""));

        return null;}

    public NasmOperand visit(C3aConstant oper){
        return new NasmConstant(oper.val);
    }

    public NasmOperand visit(C3aBooleanConstant oper){
        return new NasmConstant(oper.val ? 1 : 0);
    }

    public NasmOperand visit(C3aLabel oper){
        return new NasmLabel(""+oper.number);
    }

    public NasmOperand visit(C3aTemp oper){
        return new NasmRegister(oper.num);
    }

    public NasmOperand visit(C3aVar oper) {
        return null;
    }

    public NasmOperand visit(C3aFunction oper){
        return new NasmLabel(oper.val.identif);
    }

}
