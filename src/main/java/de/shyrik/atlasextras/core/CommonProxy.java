package de.shyrik.atlasextras.core;

import de.shyrik.atlasextras.AtlasExtras;
import de.shyrik.atlasextras.compat.WaystonesHandler;
import de.shyrik.atlasextras.features.travel.AtlasExtrasCostHandler;
import de.shyrik.atlasextras.compat.SignpostHandler;
import de.shyrik.atlasextras.features.travel.TravelHandler;
import de.shyrik.atlasextras.network.NetworkHelper;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageEditWaystone;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy {

    public static final ResourceLocation MARKER_TRAVELFROM = new ResourceLocation(AtlasExtras.MODID, "travelfrom");
    public static final ResourceLocation MARKER_TRAVELTO = new ResourceLocation(AtlasExtras.MODID, "travelto");
    public static final ResourceLocation MARKER_TRAVEL = new ResourceLocation(AtlasExtras.MODID, "travel");

    public void preInit(FMLPreInitializationEvent event) {
        TravelHandler.registerCostHandler(AtlasExtras.MODID, new AtlasExtrasCostHandler());
        NetworkHelper.registerPackets();

        if (Loader.isModLoaded(SignpostHandler.MODID) && Configuration.COMPAT.compatSignpost) {
            SignpostHandler sph = new SignpostHandler();
            MinecraftForge.EVENT_BUS.register(sph);
            TravelHandler.registerCostHandler(SignpostHandler.MODID, sph);
        }

        if (Loader.isModLoaded(WaystonesHandler.MODID) && Configuration.COMPAT.compatWaystone) {
            WaystonesHandler wsh = new WaystonesHandler();
            MinecraftForge.EVENT_BUS.register(wsh);
            TravelHandler.registerCostHandler(WaystonesHandler.MODID, wsh);
            NetworkHandler.channel.registerMessage(WaystonesHandler.class, MessageEditWaystone.class, 12, Side.SERVER);
        }
    }

    public void init(FMLInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) {

    }

    /**
     * Returns a side-appropriate EntityPlayer for use during message handling
     */
    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return ctx.getServerHandler().player;
    }
}
