package com.brandon3055.draconicevolution.api.modules.entities;

import com.brandon3055.brandonscore.api.power.IOPStorageModifiable;
import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.LastStandData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.api.modules.lib.StackModuleContext;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.thread.EffectiveSide;

import java.util.Iterator;

public class LastStandEntity extends ModuleEntity {

    private int charge;

    public LastStandEntity(Module<LastStandData> module) {
        super(module);
    }

    @Override
    public void tick(ModuleContext moduleContext) {
        IOPStorageModifiable storage = moduleContext.getOpStorage();
        if (!(moduleContext instanceof StackModuleContext && EffectiveSide.get().isServer() && storage != null)) return;
        StackModuleContext context = (StackModuleContext) moduleContext;
        LastStandData data = (LastStandData) module.getData();
        if (!context.isEquipped() || charge >= data.getChargeTime()) return;
        if (storage.getOPStored() >= data.getChargeEnergyRate()) {
            storage.modifyEnergyStored(-data.getChargeEnergyRate());
            charge++;
//            charge+=100;
        }
    }

    public boolean tryBlockDeath(LivingDeathEvent event) {
        LastStandData data = (LastStandData) module.getData();
        if (charge >= data.getChargeTime()) {
            LivingEntity entity = event.getEntityLiving();
            entity.setHealth(entity.getHealth() + data.getHealthBoost());
            ItemStack stack = entity.getItemStackFromSlot(EquipmentSlotType.CHEST);
            if (!stack.isEmpty()) {
                LazyOptional<ModuleHost> optionalHost = stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY);
                optionalHost.ifPresent(stackHost -> {
                    ShieldControlEntity shield = stackHost.getEntitiesByType(ModuleTypes.SHIELD_CONTROLLER).map(e -> (ShieldControlEntity) e).findAny().orElse(null);
                    if (shield != null) {
                        shield.boost(data.getShieldBoost(), data.getShieldBoostTime());
                    }
                });
            }
            if (module.getModuleTechLevel().index >= 2) {
                entity.extinguish();
                Iterator<EffectInstance> iterator = entity.getActivePotionMap().values().iterator();
                while (iterator.hasNext()) {
                    EffectInstance effect = iterator.next();
                    if (!effect.getPotion().isBeneficial()) {
                        entity.onFinishedPotionEffect(effect);
                        iterator.remove();
                    }
                }
            }
            charge = 0;
            DraconicNetwork.sendLastStandActivation(entity, module.getItem());
            entity.world.playSound(null, entity.getPosition(), SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 5F, (0.95F + (entity.world.rand.nextFloat() * 0.1F)));
            return true;
        }
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderSlotOverlay(IRenderTypeBuffer getter, Minecraft mc, int x, int y, int width, int height, double mouseX, double mouseY, boolean mouseOver, float partialTicks) {
        LastStandData data = (LastStandData) module.getData();
        if (charge >= data.getChargeTime()) return;
        double diameter = Math.min(width, height) * 0.425;
        double progress = charge / Math.max(1D, data.getChargeTime());

        GuiHelper.drawColouredRect(getter.getBuffer(GuiHelper.TRANS_TYPE), x, y, width, height, 0x20FF0000, 0);
        IVertexBuilder builder = getter.getBuffer(GuiHelper.FAN_TYPE);
        builder.pos(x + (width / 2D), y + (height / 2D), 0).color(0, 255, 255, 64).endVertex();
        for (double d = 0; d <= 1; d += 1D / 30D) {
            double angle = (d * progress) + 0.5 - progress;
            double vertX = x + (width / 2D) + Math.sin(angle * (Math.PI * 2)) * diameter;
            double vertY = y + (height / 2D) + Math.cos(angle * (Math.PI * 2)) * diameter;
            builder.pos(vertX, vertY, 0).color(255, 255, 255, 64).endVertex();
        }
        ((IRenderTypeBuffer.Impl) getter).finish();

        String pText = (int) (progress * 100) + "%";
        String tText = ((data.getChargeTime() - charge) / 20) + "s";
        GuiHelper.drawBackgroundString(getter.getBuffer(GuiHelper.TRANS_TYPE), mc.fontRenderer, pText, x + width / 2F, y + height / 2F - 8, 0, 0x4000FF00, 1, false, true);
        GuiHelper.drawBackgroundString(getter.getBuffer(GuiHelper.TRANS_TYPE), mc.fontRenderer, tText, x + width / 2F, y + height / 2F + 1, 0, 0x4000FF00, 1, false, true);
    }


    @Override
    public void writeToItemStack(ItemStack stack, ModuleContext context) {
        super.writeToItemStack(stack, context);
        stack.getOrCreateTag().putInt("charge", charge);
    }

    @Override
    public void readFromItemStack(ItemStack stack, ModuleContext context) {
        super.readFromItemStack(stack, context);
        if (stack.hasTag()) {
            charge = stack.getOrCreateTag().getInt("charge");
        }
    }

    @Override
    public void writeToNBT(CompoundNBT compound) {
        super.writeToNBT(compound);
        compound.putInt("charge", charge);
    }

    @Override
    public void readFromNBT(CompoundNBT compound) {
        super.readFromNBT(compound);
        charge = compound.getInt("charge");
    }
}
