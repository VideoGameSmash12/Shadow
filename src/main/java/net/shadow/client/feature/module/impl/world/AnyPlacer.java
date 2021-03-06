/*
 * Copyright (c) Shadow client, Saturn5VFive and contributors 2022. All rights reserved.
 */

package net.shadow.client.feature.module.impl.world;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleType;
import net.shadow.client.helper.event.EventType;
import net.shadow.client.helper.event.Events;
import net.shadow.client.helper.event.events.MouseEvent;
import net.shadow.client.helper.render.Renderer;
import net.shadow.client.helper.util.Utils;

import java.awt.Color;
import java.util.Objects;

public class AnyPlacer extends Module {
    public AnyPlacer() {
        super("AnyPlacer", "Places spawn eggs with infinite reach (requires creative)", ModuleType.WORLD);
        Events.registerEventHandler(EventType.MOUSE_EVENT, event -> {
            if (!this.isEnabled()) {
                return;
            }
            if (ShadowMain.client.player == null || ShadowMain.client.world == null) {
                return;
            }
            if (ShadowMain.client.currentScreen != null) {
                return;
            }
            //            PacketEvent pe = (PacketEvent) event;
            MouseEvent me = (MouseEvent) event;
            if ((me.getAction() == 1 || me.getAction() == 2) && me.getButton() == 1) {
                ItemStack sex = ShadowMain.client.player.getMainHandStack();
                if (sex.getItem() instanceof SpawnEggItem) {
                    event.setCancelled(true);
                    HitResult hr = ShadowMain.client.player.raycast(500, 0, true);
                    Vec3d spawnPos = hr.getPos();
                    NbtCompound entityTag = sex.getOrCreateSubNbt("EntityTag");
                    NbtList nl = new NbtList();
                    nl.add(NbtDouble.of(spawnPos.x));
                    nl.add(NbtDouble.of(spawnPos.y));
                    nl.add(NbtDouble.of(spawnPos.z));
                    entityTag.put("Pos", nl);
                    CreativeInventoryActionC2SPacket a = new CreativeInventoryActionC2SPacket(Utils.Inventory.slotIndexToId(ShadowMain.client.player.getInventory().selectedSlot), sex);
                    Objects.requireNonNull(ShadowMain.client.getNetworkHandler()).sendPacket(a);
                    BlockHitResult bhr = new BlockHitResult(ShadowMain.client.player.getPos(), Direction.DOWN, new BlockPos(ShadowMain.client.player.getPos()), false);
                    PlayerInteractBlockC2SPacket ib = new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, bhr);
                    ShadowMain.client.getNetworkHandler().sendPacket(ib);
                }
            }
        });
    }

    @Override
    public void tick() {
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    @Override
    public String getContext() {
        return null;
    }

    @Override
    public void onWorldRender(MatrixStack matrices) {
        if (isDebuggerEnabled()) {
            HitResult hr = Objects.requireNonNull(ShadowMain.client.player).raycast(500, 0, true);
            Vec3d spawnPos = hr.getPos();
            Renderer.R3D.renderFilled(spawnPos.subtract(.3, 0, .3), new Vec3d(.6, 0.001, .6), Color.WHITE, matrices);
        }
    }

    @Override
    public void onHudRender() {

    }
}
