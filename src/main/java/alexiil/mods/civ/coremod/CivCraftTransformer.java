package alexiil.mods.civ.coremod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import alexiil.mods.lib.Lib;
import alexiil.mods.lib.coremod.ClassTransformer;

public class CivCraftTransformer extends ClassTransformer {
    public static Logger log = LogManager.getLogger(Lib.Mod.ID + ".classTransformer");

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        try {
            // CivCraft Transforming
            if (transformedName.equals("net.minecraft.inventory.ContainerPlayer"))
                return transformContainerPlayer(basicClass, !name.equals(transformedName));

            if (transformedName.equals("net.minecraft.inventory.ContainerWorkbench"))
                return transformContainerWorkbench(basicClass, !name.equals(transformedName));

            if (transformedName.equals("net.minecraft.tileentity.TileEntityFurnace"))
                return transformTileEntityFurnace(basicClass, !name.equals(transformedName));

        }
        catch (Throwable t) {
            log.warn("Transforming class " + transformedName + " FAILIED! Returning the old version of the class to avoid crashes.");
            t.printStackTrace();
        }
        return basicClass;
    }

    private byte[] transformContainerPlayer(byte[] input, boolean obfuscated) {
        String targetMethodName = obfuscated ? "func_75130_a" : "onCraftMatrixChanged";

        ClassNode classNode = new ClassNode();
        ClassReader reader = new ClassReader(input);
        reader.accept(classNode, 0);
        boolean found = false;

        for (MethodNode m : classNode.methods) {
            if (m.name.equals(targetMethodName)) {
                found = true;
                int ins = 0;
                for (int i = 0; i < m.instructions.size(); i++) {
                    if (m.instructions.get(i).getOpcode() == Opcodes.INVOKESTATIC) {
                        m.instructions.remove(m.instructions.get(i));
                        ins = i;
                        break;
                    }
                }
                ins += 4;
                m.instructions.remove(m.instructions.get(ins));
                m.instructions.remove(m.instructions.get(ins));
                m.instructions.insert(m.instructions.get(ins - 1), new MethodInsnNode(Opcodes.INVOKESTATIC,
                        "alexiil/mods/civ/event/VanillaEventHooks", "canCraftPlayerEvent",
                        "(Lnet/minecraft/inventory/InventoryCrafting;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;",
                        false));
            }
        }
        if (!found) {
            log.warn("Didn't find the method " + targetMethodName + " in ContainerPlayer!");
            log.info("However, these methods exist, is it one of these?");
            for (MethodNode m : classNode.methods) {
                log.info("  -" + m.name + " with desc " + m.desc);
            }
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);
        byte[] out = cw.toByteArray();
        showDiff("net.minecraft.inventory.ContainerPlayer", input, out);
        return out;
    }

    private byte[] transformContainerWorkbench(byte[] input, boolean obfuscated) {
        String targetMethodName = obfuscated ? "func_75130_a" : "onCraftMatrixChanged";

        ClassNode classNode = new ClassNode();
        ClassReader reader = new ClassReader(input);
        reader.accept(classNode, 0);
        boolean found = false;

        for (MethodNode m : classNode.methods) {
            if (m.name.equals(targetMethodName)) {
                found = true;
                int ins = 0;
                for (int i = 0; i < m.instructions.size(); i++) {
                    if (m.instructions.get(i).getOpcode() == Opcodes.INVOKESTATIC) {
                        m.instructions.remove(m.instructions.get(i));
                        ins = i;
                        break;
                    }
                }

                ins += 4;
                m.instructions.remove(m.instructions.get(ins));
                ins--;
                m.instructions.insert(m.instructions.get(ins), new VarInsnNode(Opcodes.ALOAD, 0));
                ins++;
                m.instructions.insert(m.instructions.get(ins), new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/inventory/ContainerWorkbench",
                        "field_178145_h", "Lnet/minecraft/util/BlockPos;"));
                ins++;

                String owner = "alexiil/mods/civ/event/VanillaEventHooks";
                String desc = "(Lnet/minecraft/inventory/InventoryCrafting;Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;)";
                String desc2 = "Lnet/minecraft/item/ItemStack;";
                MethodInsnNode node = new MethodInsnNode(Opcodes.INVOKESTATIC, owner, "canCraftBlockEvent", desc + desc2, false);
                m.instructions.insert(m.instructions.get(ins), node);
            }
        }
        if (!found) {
            log.warn("Didn't find the method " + targetMethodName + " in ContainerWorkbench!");
            log.info("However, these methods exist, is it one of these?");
            for (MethodNode m : classNode.methods) {
                log.info("  -" + m.name + " with desc " + m.desc);
            }
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);
        byte[] out = cw.toByteArray();
        showDiff("net.minecraft.inventory.ContainerWorkbench", input, out);
        return out;
    }

    private byte[] transformTileEntityFurnace(byte[] input, boolean obfuscated) {
        String targetMethodName = obfuscated ? "func_145948_k" : "canSmelt";

        ClassNode classNode = new ClassNode();
        ClassReader reader = new ClassReader(input);
        reader.accept(classNode, 0);
        boolean found = false;

        for (MethodNode m : classNode.methods) {
            if (m.name.equals(targetMethodName)) {
                found = true;
                int ins = 0;
                for (int i = 0; i < m.instructions.size(); i++) {
                    if (m.instructions.get(i).getOpcode() == Opcodes.INVOKESTATIC) {
                        m.instructions.remove(m.instructions.get(i));
                        ins = i;
                        break;
                    }
                }

                ins += 4;

                m.instructions.remove(m.instructions.get(ins));
                ins--;

                m.instructions.insert(m.instructions.get(ins), new VarInsnNode(Opcodes.ALOAD, 0));
                ins++;

                String worldObj = obfuscated ? "field_145850_b" : "worldObj";

                m.instructions.insert(m.instructions.get(ins), new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/tileentity/TileEntityFurnace",
                        worldObj, "Lnet/minecraft/world/World;"));
                ins++;

                m.instructions.insert(m.instructions.get(ins), new VarInsnNode(Opcodes.ALOAD, 0));
                ins++;

                String pos = obfuscated ? "field_174879_c" : "pos";

                m.instructions.insert(m.instructions.get(ins), new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/tileentity/TileEntityFurnace", pos,
                        "Lnet/minecraft/util/BlockPos;"));
                ins++;

                m.instructions.insert(m.instructions.get(ins), new MethodInsnNode(Opcodes.INVOKESTATIC, "alexiil/mods/civ/event/VanillaEventHooks",
                        "canSmeltEvent",
                        "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;)Lnet/minecraft/item/ItemStack;",
                        false));
            }
        }
        if (!found) {
            log.warn("Didn't find the method " + targetMethodName + " in TileEntityFurnace !");
            log.info("However, these methods exist, is it one of these?");
            for (MethodNode m : classNode.methods) {
                log.info("  -" + m.name + " with desc " + m.desc);
            }
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);
        byte[] out = cw.toByteArray();
        showDiff("net.minecraft.inventory.ContainerWorkbench", input, out);
        return out;
    }
}
