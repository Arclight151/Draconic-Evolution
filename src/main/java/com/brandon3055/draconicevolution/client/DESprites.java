package com.brandon3055.draconicevolution.client;

import com.brandon3055.brandonscore.BCConfig;
import com.brandon3055.brandonscore.client.BCSprites;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyTransfuser;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;

import java.util.function.Supplier;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by brandon3055 on 14/12/20
 * This all goes through BCSprites which means these are also registered to the BC GUI Texture sheet.
 * This means i can use these sprites anywhere i the BCSprites can be used without having to worry about texture sheets.
 */
public class DESprites {

    public static void initialize(ColorHandlerEvent.Block event) {
        //Gui Transfuser
        for (TileEnergyTransfuser.ItemIOMode mode : TileEnergyTransfuser.ItemIOMode.values()) {
            register(MODID, mode.getSpriteName());
        }
        register(MODID, "transfuser/balanced_charge");
        register(MODID, "transfuser/sequential_charge");

    }


    //region register

    public static void registerThemed(String modid, String location) {
        BCSprites.registerThemed(modid, location);
    }
    public static void register(String modid, String location) {
        BCSprites.register(modid, location);
    }

    public static void register(ResourceLocation location) {
        BCSprites.register(location);
    }

    //endregion
    public static RenderMaterial getThemed(String modid, String location) {
        return BCSprites.getThemed(modid, location);
    }

    public static RenderMaterial getThemed(String location) {
        return BCSprites.getThemed(MODID, location);
    }

    public static RenderMaterial get(String modid, String location) {
        return BCSprites.get(modid, location);
    }

    public static RenderMaterial get(String location) {
        return BCSprites.get(MODID, location);
    }

    public static Supplier<RenderMaterial> themedGetter(String modid, String location) {
        return BCSprites.themedGetter(modid, location);
    }

    public static Supplier<RenderMaterial> themedGetter(String location) {
        return BCSprites.themedGetter(MODID, location);
    }

    public static Supplier<RenderMaterial> getter(String modid, String location) {
        return BCSprites.getter(modid, location);
    }

    public static Supplier<RenderMaterial> getter(String location) {
        return BCSprites.getter(MODID, location);
    }
}
