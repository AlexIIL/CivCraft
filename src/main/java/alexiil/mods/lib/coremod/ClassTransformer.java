package alexiil.mods.lib.coremod;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IChatComponent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import alexiil.mods.lib.AlexIILLib;

public class ClassTransformer implements IClassTransformer {
    public static Logger log = LogManager.getLogger(AlexIILLib.MODID + ".classTransformer");
    
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        try {
            if (transformedName.equals("net.minecraft.client.gui.GuiMultiplayer") && AlexIILLib.roamingIP.getBoolean())
                return transformGuiMultiplayer(basicClass, !transformedName.equals(name));
            
            if (transformedName.equals("net.minecraft.client.gui.ServerListEntryNormal") && AlexIILLib.roamingIP.getBoolean())
                return transformServerListEntryNormal(basicClass, !name.equals(transformedName));
            
            if (transformedName.equals("net.minecraft.client.renderer.InventoryEffectRenderer") && AlexIILLib.betterPotions.getBoolean())
                return transformInventoryEffectRenderer(basicClass, !name.equals(transformedName));
            
            if (transformedName.equals("net.minecraft.client.gui.GuiNewChat") && AlexIILLib.timeText.getBoolean())
                return transformGuiNewChat(basicClass, !name.equals(transformedName));
        }
        catch (Throwable t) {
            log.warn("Transforming class " + transformedName + " FAILIED! Returning the old version of the class to avoid crashes.");
            t.printStackTrace();
        }
        return basicClass;
    }
    
    private void showDiff(String className, byte[] input, byte[] output) {
        log.info("Finished Transforming " + className);
        if (input == null)
            log.debug("input was null");
        if (output == null)
            log.debug("output was null");
        if (input == null || output == null)
            return;
        log.info(input.length + " bytes to " + output.length + " bytes.");
    }
    
    private byte[] transformInventoryEffectRenderer(byte[] input, boolean obfuscated) {
        ClassNode classNode = new ClassNode();
        ClassReader reader = new ClassReader(input);
        reader.accept(classNode, 0);
        
        for (MethodNode m : classNode.methods) {
            if (m.name.equals("drawActivePotionEffects") || m.equals("func_147044_g")) {
                int astores = 0;
                for (int i = 0; i < m.instructions.size(); i++) {
                    AbstractInsnNode node = m.instructions.get(i);
                    if (astores < 8 && node.getOpcode() == Opcodes.ASTORE)
                        astores++;
                    else if (astores == 8) {
                        astores++;
                    }
                    else if (astores == 9) {
                        astores++;
                        AbstractInsnNode node1 = new VarInsnNode(Opcodes.ALOAD, 9);
                        m.instructions.insert(node, node1);
                        
                        node = node1;
                        node1 = new VarInsnNode(Opcodes.ALOAD, 7);
                        m.instructions.insert(node, node1);
                        
                        node = node1;
                        node1 =
                                new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(VanillaMethods.class), "getEnchantmentLevel",
                                        "(Ljava/lang/String;" + Type.getDescriptor(PotionEffect.class) + ")Ljava/lang/String;", false);
                        m.instructions.insert(node, node1);
                        
                        node = node1;
                        m.instructions.insert(node, new VarInsnNode(Opcodes.ASTORE, 9));
                    }
                }
            }
        }
        
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);
        
        showDiff("InventoryEffectRenderer", input, cw.toByteArray());
        return cw.toByteArray();
    }
    
    private byte[] transformGuiNewChat(byte[] input, boolean obfs) {
        log.info("Transforming class GuiNewChat");
        ClassNode classNodeorig = new ClassNode();
        ClassReader rd = new ClassReader(input);
        rd.accept(classNodeorig, 0);
        // showMethod(classNodeorig, obfs ? "func_146230_a" : "drawChat");
        
        ClassNode classNode = new ClassNode();
        ClassReader reader = new ClassReader(input);
        reader.accept(classNode, 0);
        
        for (MethodNode m : classNode.methods) {
            if (m.name.equals("drawChat") || m.name.equals("func_146230_a")) {
                for (int i = 0; i < m.instructions.size(); i++) {
                    AbstractInsnNode node = m.instructions.get(i);
                    if (node instanceof MethodInsnNode) {
                        MethodInsnNode meth = (MethodInsnNode) node;
                        if (meth.owner.equals(Type.getInternalName(ChatLine.class))
                                && (meth.name.equals("getChatComponent") || meth.name.equals("func_151461_a"))) {
                            m.instructions.remove(meth.getNext());
                            m.instructions.insert(meth, new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(ChatTextTime.class),
                                    "getTimeText", "(" + Type.getDescriptor(IChatComponent.class) + "I)Ljava/lang/String;", false));
                            m.instructions.insert(meth, new VarInsnNode(Opcodes.ILOAD, obfs ? 12 : 11));
                            // annoying forge hack-thing
                        }
                    }
                }
            }
        }
        
        // VANILLA
        // 'net/minecraft/client/gui/GuiNewChat', integer, integer, integer, integer, integer, float, float, integer,
        // integer, 'net/minecraft/client/gui/ChatLine', integer, double,
        // double_2nd, integer, integer, integer
        // THIS
        // 'net/minecraft/client/gui/GuiNewChat', integer, integer, integer, integer, integer, float, float, integer,
        // integer, integer, integer, 'net/minecraft/client/gui/ChatLine',
        // double, double_2nd, integer, integer
        
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);
        log.info("Transformed class GuiNewChat");
        
        // showMethod(classNode, obfs ? "func_146230_a" : "drawChat");
        showDiff("GuiNewChat", input, cw.toByteArray());
        // showMethodDiff(classNodeorig, classNode, obfs ? "func_146230_a" : "drawChat");
        
        return cw.toByteArray();
    }
    
    private byte[] transformServerListEntryNormal(byte[] input, boolean obfuscated) {
        ClassNode classNode = new ClassNode();
        ClassReader reader = new ClassReader(input);
        reader.accept(classNode, 0);
        ClassNode classNodeorig = new ClassNode();
        new ClassReader(input).accept(classNodeorig, 0);
        
        String className = "net/minecraft/client/gui/ServerListEntryNormal";
        
        classNode.fields.add(new FieldNode(2, "roamingServer", Type.getDescriptor(ServerData.class), null, null));
        
        for (MethodNode m : classNode.methods) {
            if (m.name.equals("<init>")) {
                // After 2 DUP
                AbstractInsnNode before = null;
                int dups = 0;
                int aloads = 0;
                boolean done = false;
                AbstractInsnNode lastPutField = null;
                for (int i = 0; i < m.instructions.size(); i++) {
                    AbstractInsnNode current = m.instructions.get(i);
                    if (current.getOpcode() == Opcodes.ALOAD)
                        aloads++;
                    if (aloads == 5 && !done) {
                        m.instructions.insert(
                                current,
                                new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(RoamingIPAddress.class),
                                        "getModifiedRoamingServerData", "(" + Type.getDescriptor(ServerData.class) + ")"
                                                + Type.getDescriptor(ServerData.class), false));
                        i++;
                        done = true;
                    }
                    
                    if (dups < 2 && m.instructions.get(i).getOpcode() == Opcodes.DUP)
                        dups++;
                    else if (dups == 2) {
                        m.instructions.remove(m.instructions.get(i));
                        i--;
                        dups = 3;
                    }
                    else if (dups == 3) {
                        before = m.instructions.get(i);
                        log.warn(getInsn(before));
                        dups = 4;
                    }
                    else if (dups == 4) {
                        log.warn(getInsn(current));
                        m.instructions.remove(m.instructions.get(i));
                        m.instructions.insert(before, new MethodInsnNode(Opcodes.INVOKESPECIAL, Type.getInternalName(StringBuilder.class), "<init>",
                                "(Ljava/lang/String;)V", false));
                        dups = 5;
                    }
                    if (m.instructions.get(i).getOpcode() == Opcodes.PUTFIELD)
                        lastPutField = m.instructions.get(i);
                    
                    // INVOKESTATIC alexiil/mods/basicutils/coremod/RoamingIPAddress.getModifiedRoamingServerData
                    // (Lnet/minecraft/client/multiplayer/ServerData;)Lnet/minecraft/client/multiplayer/ServerData;
                }
                m.instructions.insert(lastPutField,
                        new FieldInsnNode(Opcodes.PUTFIELD, className, "roamingServer", Type.getDescriptor(ServerData.class)));
                m.instructions.insert(lastPutField, new VarInsnNode(Opcodes.ALOAD, 2));
                m.instructions.insert(lastPutField, new VarInsnNode(Opcodes.ALOAD, 0));
            }
            else if (m.name.equals("drawEntry") || m.name.equals("func_180790_a")) {
                int getFields = 0;
                for (int i = 0; i < m.instructions.size(); i++) {
                    AbstractInsnNode node = m.instructions.get(i);
                    if (node.getOpcode() == Opcodes.GETFIELD) {
                        getFields++;
                        if (getFields == 13)// Must be the one to replace with "roamingServer"
                        {
                            m.instructions.insert(node,
                                    new FieldInsnNode(Opcodes.GETFIELD, className, "roamingServer", Type.getDescriptor(ServerData.class)));
                            m.instructions.remove(node);
                        }
                    }
                }
            }
            else if (m.name.equals("getServerData") || m.name.equals("func_148296_a")) {
                for (int i = 0; i < m.instructions.size(); i++) {
                    AbstractInsnNode node = m.instructions.get(i);
                    if (m.instructions.get(i).getOpcode() == Opcodes.GETFIELD) {
                        m.instructions.insert(node,
                                new FieldInsnNode(Opcodes.GETFIELD, className, "roamingServer", Type.getDescriptor(ServerData.class)));
                        m.instructions.remove(node);
                    }
                }
            }
        }
        
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        // showMethod(classNodeorig, "drawEntry");
        // showMethod(classNode, "drawEntry");
        // showMethodDiff(classNodeorig, classNode, "drawEntry");
        classNode.accept(cw);
        byte[] out = cw.toByteArray();
        showDiff("net.minecraft.client.gui.ServerListEntryNormal", input, out);
        return out;
    }
    
    private byte[] transformGuiMultiplayer(byte[] input, boolean obfuscated) {
        String targetmethodName = obfuscated ? "func_146791_a" : "connectToServer";
        
        ClassNode classNode = new ClassNode();
        ClassReader reader = new ClassReader(input);
        reader.accept(classNode, 0);
        for (MethodNode m : classNode.methods) {
            if (m.name.equals(targetmethodName)) {
                AbstractInsnNode lastALOAD = null;
                for (int i = 0; i < m.instructions.size(); i++) {
                    AbstractInsnNode ins = m.instructions.get(i);
                    if (ins.getOpcode() == Opcodes.ALOAD)
                        lastALOAD = ins;
                }
                // INVOKESTATIC alexiil/mods/basicutils/coremod/RoamingIPAddress.getModifiedRoamingServerData
                // (Lnet/minecraft/client/multiplayer/ServerData;)Lnet/minecraft/client/multiplayer/ServerData;
                m.instructions.insert(lastALOAD, new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(RoamingIPAddress.class),
                        "getModifiedRoamingServerData", "(" + Type.getDescriptor(ServerData.class) + ")" + Type.getDescriptor(ServerData.class),
                        false));
            }
        }
        
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);
        byte[] out = cw.toByteArray();
        showDiff("net.minecraft.client.gui.GuiMultiplayer", input, out);
        return out;
    }
    
    @SuppressWarnings("unused")
    private void showMethodDiff(ClassNode node1, ClassNode node2, String methodName) {
        log.warn("---------------------------------------------------------------------------------------------------");
        log.warn("Showing Method Diffs of " + methodName + "----------------------------------------------------------------------------------");
        log.warn("---------------------------------------------------------------------------------------------------");
        MethodNode meth1 = getMethod(node1, methodName);
        MethodNode meth2 = getMethod(node2, methodName);
        
        for (int i = 0; i < meth1.instructions.size(); i++) {
            if (!getInsn(meth1.instructions.get(i)).equals(getInsn(meth2.instructions.get(i))))
                log.warn("Line " + i + ":" + getInsn(meth1.instructions.get(i)) + " -> " + getInsn(meth2.instructions.get(i)));
        }
    }
    
    private MethodNode getMethod(ClassNode node, String name) {
        for (MethodNode m : node.methods)
            if (m.name.equals(name))
                return m;
        return null;
    }
    
    private String getInsn(AbstractInsnNode ins) {
        if (ins instanceof MethodInsnNode) {
            MethodInsnNode n = (MethodInsnNode) ins;
            return (n.owner + "," + n.name + "," + n.desc);
        }
        if (ins instanceof FieldInsnNode) {
            FieldInsnNode n = (FieldInsnNode) ins;
            return (n.owner + "," + n.name + "," + n.desc);
        }
        if (ins instanceof VarInsnNode) {
            VarInsnNode n = (VarInsnNode) ins;
            return n.getOpcode() + " " + n.var;
        }
        return (ins.getOpcode() + ":" + ins.getClass().getSimpleName());
    }
    
    @SuppressWarnings("unused")
    private void showMethod(ClassNode classNode, String methodName) {
        log.warn("---------------------------------------------------------------------------------------------------");
        log.warn("Showing Method " + methodName + "----------------------------------------------------------------------------------");
        log.warn("---------------------------------------------------------------------------------------------------");
        for (MethodNode m : classNode.methods)
            if (m.name.equals(methodName))
                for (int i = 0; i < m.instructions.size(); i++)
                    log.warn("Line" + i + ":" + getInsn(m.instructions.get(i)));
    }
}
