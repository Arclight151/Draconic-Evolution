package com.brandon3055.draconicevolution.client;

import com.brandon3055.brandonscore.client.particle.IntParticleType;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.render.particle.*;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;

/**
 * Created by brandon3055 on 23/4/2016.
 * A list of all of DE's particles
 */
@Mod.EventBusSubscriber(modid = DraconicEvolution.MODID, bus = MOD)
@ObjectHolder(DraconicEvolution.MODID)
public class DEParticles {

    @ObjectHolder("flame")
    public static IntParticleType flame;
    @ObjectHolder("line_indicator")
    public static IntParticleType line_indicator;
    @ObjectHolder("energy")
    public static IntParticleType energy;
    @ObjectHolder("energy_core")
    public static IntParticleType energy_core;
    @ObjectHolder("guardian_projectile")
    public static BasicParticleType guardian_projectile;

    @SubscribeEvent
    public static void registerParticles(RegistryEvent.Register<ParticleType<?>> event) {
        event.getRegistry().register(new IntParticleType(false).setRegistryName("flame"));
        event.getRegistry().register(new IntParticleType(false).setRegistryName("line_indicator"));
        event.getRegistry().register(new IntParticleType(false).setRegistryName("energy"));
        event.getRegistry().register(new IntParticleType(false).setRegistryName("energy_core"));
        event.getRegistry().register(new BasicParticleType(false).setRegistryName("guardian_projectile"));
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerFactories(ParticleFactoryRegisterEvent event) {
        ParticleManager manager = Minecraft.getInstance().particles;
        manager.registerFactory(flame, CustomFlameParticle.Factory::new);
        manager.registerFactory(line_indicator, ParticleLineIndicator.Factory::new);
        manager.registerFactory(energy, ParticleEnergy.Factory::new);
        manager.registerFactory(energy_core, ParticleEnergyCoreFX.Factory::new);
        manager.registerFactory(guardian_projectile, GuardianProjectileParticle.Factory::new);
    }


    @OnlyIn(Dist.CLIENT)
    public static Particle addParticleDirect(World world, Particle particle) {
        if (world instanceof ClientWorld) {
            Minecraft mc = Minecraft.getInstance();
            ActiveRenderInfo activerenderinfo = mc.gameRenderer.getActiveRenderInfo();
            if (mc != null && activerenderinfo.isValid() && mc.particles != null) {
                mc.particles.addEffect(particle);
                return particle;
            }
        }
        return null;
    }






//    public static int ENERGY_PARTICLE;
//    public static int ENERGY_CORE_FX;
//    public static int LINE_INDICATOR;
//    public static int INFUSER;
//    public static int GUARDIAN_PROJECTILE;
//    public static int CHAOS_IMPLOSION;
//    public static int PORTAL;
//    public static int DRAGON_HEART;
//    public static int AXE_SELECTION;
//    public static int SOUL_EXTRACTION;
//    public static int ARROW_SHOCKWAVE;
//    public static int CUSTOM;
//    public static int FLAME;
//
//    public static void registerClient() {
////        ENERGY_PARTICLE = BCEffectHandler.registerFX(DE_SHEET, new ParticleEnergy.Factory());
////        ENERGY_CORE_FX = BCEffectHandler.registerFX(DE_SHEET, new ParticleEnergyCoreFX.Factory());
////        LINE_INDICATOR = BCEffectHandler.registerFX(DE_SHEET, new ParticleLineIndicator.Factory());
////        INFUSER = BCEffectHandler.registerFX(DE_SHEET, new ParticleInfuser.Factory());
////        GUARDIAN_PROJECTILE = BCEffectHandler.registerFX(DE_SHEET, new ParticleGuardianProjectile.Factory());
////        CHAOS_IMPLOSION = BCEffectHandler.registerFX(DE_SHEET, new ParticleChaosImplosion.Factory());
////        PORTAL = BCEffectHandler.registerFX(DE_SHEET, new ParticlePortal.Factory());
////        DRAGON_HEART = BCEffectHandler.registerFX(DE_SHEET, new ParticleDragonHeart.Factory());
////        AXE_SELECTION = BCEffectHandler.registerFX(new ResourceLocation("textures/items/diamond_axe.png"), new ParticleAxeSelection.Factory());
////        SOUL_EXTRACTION = BCEffectHandler.registerFX(DE_SHEET, new ParticleSoulExtraction.Factory());
////        ARROW_SHOCKWAVE = BCEffectHandler.registerFX(DE_SHEET, new ParticleArrowShockwave.Factory());
////        CUSTOM = BCEffectHandler.registerFX(CUSTOM_SHEET, new ParticleCustom.Factory());
////        FLAME = BCEffectHandler.registerFX(VANILLA_SHEET, new ParticleFlame.Factory());
//    }
//
//    public static void registerServer() {
////        ENERGY_PARTICLE = BCEffectHandler.registerFXServer();
////        ENERGY_CORE_FX = BCEffectHandler.registerFXServer();
////        LINE_INDICATOR = BCEffectHandler.registerFXServer();
////        INFUSER = BCEffectHandler.registerFXServer();
////        GUARDIAN_PROJECTILE = BCEffectHandler.registerFXServer();
////        CHAOS_IMPLOSION = BCEffectHandler.registerFXServer();
////        PORTAL = BCEffectHandler.registerFXServer();
////        DRAGON_HEART = BCEffectHandler.registerFXServer();
////        AXE_SELECTION = BCEffectHandler.registerFXServer();
////        SOUL_EXTRACTION = BCEffectHandler.registerFXServer();
////        ARROW_SHOCKWAVE = BCEffectHandler.registerFXServer();
////        CUSTOM = BCEffectHandler.registerFXServer();
////        FLAME = BCEffectHandler.registerFXServer();
//    }
}
