package com.brandon3055.draconicevolution.network;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.packet.ICustomPacketHandler;
import codechicken.lib.packet.PacketCustom;
import com.brandon3055.brandonscore.handlers.HandHelper;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleGrid;
import com.brandon3055.draconicevolution.client.gui.modular.itemconfig.PropertyData;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.api.itemconfig_dep.IConfigurableItem;
import com.brandon3055.draconicevolution.api.itemconfig_dep.ToolConfigHelper;
import com.brandon3055.draconicevolution.inventory.ContainerConfigurableItem;
import com.brandon3055.draconicevolution.inventory.ContainerModularItem;
import com.brandon3055.draconicevolution.inventory.ContainerModuleHost;
import com.brandon3055.draconicevolution.items.tools.old.IAOEWeapon;
import com.brandon3055.draconicevolution.items.tools.Magnet;
import com.brandon3055.draconicevolution.items.tools.old.MiningToolBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.ArrayList;
import java.util.List;

public class ServerPacketHandler implements ICustomPacketHandler.IServerPacketHandler {


    @Override
    public void handlePacket(PacketCustom packet, ServerPlayerEntity sender, IServerPlayNetHandler handler) {
        switch (packet.getType()) {
            case DraconicNetwork.S_TOGGLE_DISLOCATORS:
                toggleDislocators(sender);
                break;
            case DraconicNetwork.S_TOOL_PROFILE:
                changeToolProfile(sender, packet.readBoolean());
                break;
            case DraconicNetwork.S_CYCLE_DIG_AOE:
                cycleToolAOE(sender, packet.readBoolean());
                break;
            case DraconicNetwork.S_CYCLE_ATTACK_AOE:
                cycleAttackAOE(sender, packet.readBoolean());
                break;
            case DraconicNetwork.S_MODULE_CONTAINER_CLICK:
                moduleSlotClick(sender, packet);
                break;
            case DraconicNetwork.S_PROPERTY_DATA:
                propertyData(sender, packet);
                break;
            case DraconicNetwork.S_ITEM_CONFIG_GUI:
                if (packet.readBoolean())
                    ContainerModularItem.tryOpenGui(sender);
                else
                    ContainerConfigurableItem.tryOpenGui(sender);
                break;
            case DraconicNetwork.S_MODULE_CONFIG_GUI:
                ContainerModularItem.tryOpenGui(sender);
                break;
        }
    }

    private void toggleDislocators(PlayerEntity player) {
        List<ItemStack> dislocators = new ArrayList<>();

        for (ItemStack stack : player.inventory.mainInventory) {
            if (!stack.isEmpty() && stack.getItem() == DEContent.magnet) {
                dislocators.add(stack);
            }
        }

        for (ItemStack stack : player.inventory.offHandInventory) {
            if (!stack.isEmpty() && stack.getItem() == DEContent.magnet) {
                dislocators.add(stack);
            }
        }

//        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
//        if (handler != null) {
//            for (int i = 0; i < handler.getSlots(); i++) {
//                ItemStack stack = handler.getStackInSlot(i);
//                if (!stack.isEmpty() && stack.getItem() == DEFeatures.magnet) {
//                    dislocators.add(stack);
//                }
//            }
//        }

        for (ItemStack dislocator : dislocators) {
            Magnet.toggleEnabled(dislocator);
            boolean enabled = Magnet.isEnabled(dislocator);
            ChatHelper.sendIndexed(player, new TranslationTextComponent("chat.item_dislocator_" + (enabled ? "activate" : "deactivate") + ".msg"), 567);
        }
    }

    private void changeToolProfile(PlayerEntity player, boolean armor) {
        if (armor) {
            int i = 0;
            NonNullList<ItemStack> armorInventory = player.inventory.armorInventory;
            for (int i1 = armorInventory.size() - 1; i1 >= 0; i1--) {
                ItemStack stack = armorInventory.get(i1);
                if (!stack.isEmpty() && stack.getItem() instanceof IConfigurableItem) {
                    ToolConfigHelper.incrementProfile(stack);
                    int newProfile = ToolConfigHelper.getProfile(stack);
                    String name = ToolConfigHelper.getProfileName(stack, newProfile);
//                    ChatHelper.indexedTrans(player, new TranslationTextComponent("config.de.armor_profile_" + i + ".msg").toString() + " " + name, -30553045 + i);
                }
                i++;
            }
        } else {
            ItemStack stack = HandHelper.getMainFirst(player);
            if (!stack.isEmpty() && stack.getItem() instanceof IConfigurableItem) {
                ToolConfigHelper.incrementProfile(stack);
            }
        }
    }

    private void cycleToolAOE(PlayerEntity player, boolean depth) {
        ItemStack stack = player.getHeldItemMainhand();

        if (stack.getItem() instanceof MiningToolBase) {
            MiningToolBase tool = (MiningToolBase) stack.getItem();
            int value = depth ? tool.getDigDepth(stack) : tool.getDigAOE(stack);
            int maxValue = depth ? tool.getMaxDigDepth(stack) : tool.getMaxDigAOE(stack);

            value++;
            if (value > maxValue) {
                value = 0;
            }

            if (depth) {
                tool.setMiningDepth(stack, value);
            } else {
                tool.setMiningAOE(stack, value);
            }
        }
    }

    private void cycleAttackAOE(PlayerEntity player, boolean reverse) {
        ItemStack stack = player.getHeldItemMainhand();

        if (stack.getItem() instanceof IAOEWeapon) {
            IAOEWeapon weapon = (IAOEWeapon) stack.getItem();
            double value = weapon.getWeaponAOE(stack);
            double maxValue = weapon.getMaxWeaponAOE(stack);

            if (reverse) {
                value -= 0.5;
                if (value < 0) {
                    value = maxValue;
                }
            } else {
                value += 0.5;
                if (value > maxValue) {
                    value = 0;
                }
            }

            weapon.setWeaponAOE(stack, value);
        }

    }

    private void moduleSlotClick(PlayerEntity player, MCDataInput input) {
        if (player.openContainer instanceof ContainerModuleHost) {
            ModuleGrid grid = ((ContainerModuleHost<?>) player.openContainer).getGrid();
            if (grid != null) {
                ModuleGrid.GridPos pos = grid.getCell(input.readByte(), input.readByte());
                grid.cellClicked(pos, input.readByte(), input.readEnum(ClickType.class));
            }
        }
    }

    private void propertyData(ServerPlayerEntity sender, PacketCustom packet) {
        PropertyData data = PropertyData.read(packet);
        ContainerConfigurableItem.handlePropertyData(sender, data);
    }
}