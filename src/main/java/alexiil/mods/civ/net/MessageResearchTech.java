package alexiil.mods.civ.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import alexiil.mods.civ.event.TechResearchedEvent;
import alexiil.mods.civ.gui.GuiTechTree;
import alexiil.mods.civ.item.CivItems;
import alexiil.mods.civ.item.ItemTechBag;
import alexiil.mods.civ.item.ItemTechBag.TechProgress;
import alexiil.mods.civ.item.ItemTechnology.EResearchState;
import alexiil.mods.civ.tech.TechTree;
import alexiil.mods.civ.tech.TechTree.Tech;
import alexiil.mods.lib.item.ItemBase;
import alexiil.mods.lib.net.MessageBase;

public class MessageResearchTech extends MessageBase<MessageResearchTech> {
    private Tech tech = null;

    public MessageResearchTech() {}

    public MessageResearchTech(Tech tech) {
        this.tech = tech;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int l = buf.readInt();
        byte[] bytes = new byte[l];
        buf.readBytes(bytes);
        String name = new String(bytes);
        tech = TechTree.currentTree.getTech(name);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if (tech == null) {
            buf.writeInt(0);
        }
        else {
            byte[] bytes = tech.name.getBytes();
            buf.writeInt(bytes.length);
            buf.writeBytes(bytes);
        }
    }

    @Override
    public IMessage onMessage(MessageResearchTech message, MessageContext ctx) {
        if (ctx.side == Side.SERVER) {
            Tech t = message.tech;
            EntityPlayer player = ctx.getServerHandler().playerEntity;
            ItemStack bag = player.getCurrentEquippedItem();
            if (bag == null || !(bag.getItem() instanceof ItemTechBag))
                return null;
            // TOOD: re-write from here!
            TechProgress[] progresses = CivItems.techBag.getTechs(bag);
            TechProgress progress = null;
            int ind = -1;
            for (int i = 0; i < progresses.length; i++)
                if (progresses[i].tech == t)
                    ind = i;
            if (ind == -1)
                return null;
            progress = progresses[ind];
            int[] required = CivItems.technology.getSciencePacksRequired(CivItems.technology.getItemForTech(progress));
            int[] newProgress = new int[t.getSciencePacksNeeded().length];
            for (int index = 0; index < newProgress.length; index++) {
                int needed = required.length > index ? required[index] : 0;
                int got = progress.progress.length > index ? progress.progress[index] : 0;
                if (needed > 0 && got < needed) {
                    ItemBase sciencePack = CivItems.sciencePacks[index];
                    for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                        ItemStack inv = player.inventory.getStackInSlot(i);
                        if (inv == null || inv.getItem() != sciencePack)
                            continue;
                        ItemStack stack = player.inventory.decrStackSize(i, needed);
                        newProgress[index] += stack.stackSize;
                        required[index] -= stack.stackSize;
                        needed -= stack.stackSize;
                        if (needed == 0)
                            break;
                    }
                }
            }
            progresses[ind] = progress.add(newProgress);
            CivItems.techBag.setTechs(bag, progresses);
            if (progresses[ind].state == EResearchState.RESEARCHED)
                MinecraftForge.EVENT_BUS.post(new TechResearchedEvent(t, player));
            player.inventoryContainer.detectAndSendChanges();
            return new MessageResearchTech();
        }
        else if (ctx.side == Side.CLIENT) {
            Gui screen = Minecraft.getMinecraft().currentScreen;
            if (screen != null && screen instanceof GuiTechTree) {
                Minecraft.getMinecraft().displayGuiScreen(null);
            }
        }
        return null;
    }
}
