package com.enderio.base.common.enchantment;

import com.enderio.base.common.init.EIOEnchantments;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.ArrayList;
import java.util.Iterator;

//TODO testing against graves and other mods
@EventBusSubscriber
public class SoulBoundHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void deathHandler(LivingDropsEvent event) {
        if (event.getEntity() == null || event.getEntity() instanceof FakePlayer || event.isCanceled()) {
            return;
        }
        if (event.getEntity().level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            return;
        }
        ArrayList<ItemStack> soulItems = new ArrayList<>();
        if (event.getEntity() instanceof Player player) {
            Iterator<ItemEntity> iter = event.getDrops().iterator();
            while (iter.hasNext()) {
                ItemEntity ei = iter.next();
                ItemStack item = ei.getItem();
                if (isSoulBound(item)) {
                    if (player.addItem(item)) {
                        iter.remove();
                    }//TODO More detailed and better item storage.
                    soulItems.add(item);
                }
            }
            if (soulItems.isEmpty()) {
                return;
            }
            soulItems.forEach(player::addItem);
        }
    }

    @SubscribeEvent
    public static void reviveHandler(PlayerEvent.Clone event) {
        if (!event.getOriginal().isDeadOrDying()) {
            return;
        }//TODO More detailed and better item recovery.
        event.getOriginal().getInventory().items.forEach(item -> event.getEntity().addItem(item));
        event.getOriginal().getInventory().armor.forEach(armor -> event.getEntity().addItem(armor));
        event.getOriginal().getInventory().offhand.forEach(offhand -> event.getEntity().addItem(offhand));
    }

    public static boolean isSoulBound(ItemStack item) {
        return EnchantmentHelper.getItemEnchantmentLevel(EIOEnchantments.SOULBOUND.get(), item) > 0;
    }

}
