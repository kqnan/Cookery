package com.github.unldenis.hologram.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.github.unldenis.hologram.placeholder.Placeholders;
import com.github.unldenis.hologram.util.BukkitFuture;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/* loaded from: Hologram-Lib-1.4.0.jar:com/github/unldenis/hologram/packet/IPackets.class */
public interface IPackets {
    PacketContainerSendable spawnPacket(int i, Location location, Plugin plugin);

    PacketContainerSendable destroyPacket(int i);

    PacketContainerSendable equipmentPacket(int i, ItemStack itemStack);

    PacketContainerSendable metadataPacket(int i, String str, Player player, Placeholders placeholders, boolean z, boolean z2);

    PacketContainerSendable teleportPacket(int i, Location location);

    List<PacketContainerSendable> rotatePackets(int i, Location location, float f);

    default PacketContainerSendable metadataPacket(int i) {
        return metadataPacket(i, null, null, null, true, true);
    }

    default PacketContainerSendable newPacket(PacketType packetType) {
        return new PacketContainerSendable(packetType);
    }

    default byte getCompressAngle(double d) {
        return (byte) ((int) ((d * 256.0d) / 360.0d));
    }

    default int fixCoordinate(double d) {
        return (int) Math.floor(d * 32.0d);
    }

    /* loaded from: Hologram-Lib-1.4.0.jar:com/github/unldenis/hologram/packet/IPackets$PacketsV1_8.class */
    public static class PacketsV1_9V1_18 implements IPackets {
        private static WrappedDataWatcher defaultDataWatcher;

        @Override // com.github.unldenis.hologram.packet.IPackets
        public PacketContainerSendable spawnPacket(int i, Location location, Plugin plugin) {
            PacketContainerSendable newPacket = newPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
            newPacket.getIntegers().write(0, Integer.valueOf(i));
            newPacket.getIntegers().write(1, Integer.valueOf(EntityType.ARMOR_STAND.getTypeId()));
            newPacket.getIntegers().write(2, Integer.valueOf((int) (location.getX() * 32.0d)));
            newPacket.getIntegers().write(3, Integer.valueOf((int) (location.getY() * 32.0d)));
            newPacket.getIntegers().write(4, Integer.valueOf((int) (location.getZ() * 32.0d)));
            if (defaultDataWatcher == null) {
                loadDefaultWatcher(plugin).join();
            }
            newPacket.getDataWatcherModifier().write(0, defaultDataWatcher);
            return newPacket;
        }

        @Override // com.github.unldenis.hologram.packet.IPackets
        public PacketContainerSendable destroyPacket(int i) {
            PacketContainerSendable newPacket = newPacket(PacketType.Play.Server.ENTITY_DESTROY);
            newPacket.getIntegerArrays().write(0, new int[]{i});
            return newPacket;
        }

        @Override // com.github.unldenis.hologram.packet.IPackets
        public PacketContainerSendable equipmentPacket(int i, ItemStack itemStack) {
            PacketContainerSendable newPacket = newPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
            newPacket.getIntegers().write(0, Integer.valueOf(i));
            newPacket.getIntegers().write(1, 4);
            newPacket.getItemModifier().write(0, itemStack);
            return newPacket;
        }

        @Override // com.github.unldenis.hologram.packet.IPackets
        public PacketContainerSendable metadataPacket(int i, String str, Player player, Placeholders placeholders, boolean z, boolean z2) {
            PacketContainerSendable newPacket = newPacket(PacketType.Play.Server.ENTITY_METADATA);
            newPacket.getIntegers().write(0, Integer.valueOf(i));
            WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
            if (z) {
                wrappedDataWatcher.setObject(0, (byte) 32);
            }
            if (placeholders != null) {
                wrappedDataWatcher.setObject(2, placeholders.parse(str, player));
                wrappedDataWatcher.setObject(3, (byte) 1);
            }
            if (z2) {
                wrappedDataWatcher.setObject(15, (byte) 1);
            }
            newPacket.getWatchableCollectionModifier().write(0, wrappedDataWatcher.getWatchableObjects());
            return newPacket;
        }

        @Override // com.github.unldenis.hologram.packet.IPackets
        public PacketContainerSendable teleportPacket(int i, Location location) {
            PacketContainerSendable newPacket = newPacket(PacketType.Play.Server.ENTITY_TELEPORT);
            newPacket.getIntegers().write(0, Integer.valueOf(i));
            newPacket.getIntegers().write(1, Integer.valueOf(fixCoordinate(location.getX())));
            newPacket.getIntegers().write(2, Integer.valueOf(fixCoordinate(location.getY())));
            newPacket.getIntegers().write(3, Integer.valueOf(fixCoordinate(location.getZ())));
            newPacket.getBytes().write(0, Byte.valueOf(getCompressAngle((double) location.getYaw())));
            newPacket.getBytes().write(1, Byte.valueOf(getCompressAngle((double) location.getPitch())));
            newPacket.getBooleans().write(0, false);
            return newPacket;
        }

        @Override // com.github.unldenis.hologram.packet.IPackets
        public List<PacketContainerSendable> rotatePackets(int i, Location location, float f) {
            PacketContainerSendable newPacket = newPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
            newPacket.getIntegers().write(0, Integer.valueOf(i));
            newPacket.getBytes().write(0, Byte.valueOf(getCompressAngle((double) f)));
            PacketContainerSendable newPacket2 = newPacket(PacketType.Play.Server.ENTITY_TELEPORT);
            newPacket2.getIntegers().write(0, Integer.valueOf(i));
            newPacket2.getIntegers().write(1, Integer.valueOf(fixCoordinate(location.getX())));
            newPacket2.getIntegers().write(2, Integer.valueOf(fixCoordinate(location.getY())));
            newPacket2.getIntegers().write(3, Integer.valueOf(fixCoordinate(location.getZ())));
            newPacket2.getBytes().write(0, Byte.valueOf(getCompressAngle((double) f)));
            newPacket2.getBytes().write(1, (byte) 0);
            newPacket2.getBooleans().write(0, true);
            return Arrays.asList(newPacket, newPacket2);
        }

        protected CompletableFuture<Void> loadDefaultWatcher(Plugin plugin) {
            return BukkitFuture.runSync(plugin, () -> {
                World world = (World) Bukkit.getWorlds().get(0);
                Entity spawnEntity = world.spawnEntity(new Location(world, 0.0d, 256.0d, 0.0d), EntityType.ARMOR_STAND);
                defaultDataWatcher = WrappedDataWatcher.getEntityWatcher(spawnEntity).deepClone();
                spawnEntity.remove();
            });
        }
    }

    /* loaded from: Hologram-Lib-1.4.0.jar:com/github/unldenis/hologram/packet/IPackets$PacketsV1_9V1_18.class */

    public static class PacketsV1_8 implements IPackets {
        @Override // com.github.unldenis.hologram.packet.IPackets
        public PacketContainerSendable spawnPacket(int i, Location location, Plugin plugin) {
            PacketContainerSendable newPacket = newPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
            newPacket.getIntegers().write(0, Integer.valueOf(i));
            newPacket.getIntegers().write(1, 1);
            newPacket.getIntegers().write(2, 1);
            newPacket.getUUIDs().write(0, UUID.randomUUID());
            newPacket.getDoubles().write(0, Double.valueOf(location.getX()));
            newPacket.getDoubles().write(1, Double.valueOf(location.getY()));
            newPacket.getDoubles().write(2, Double.valueOf(location.getZ()));
            return newPacket;
        }

        @Override // com.github.unldenis.hologram.packet.IPackets
        public PacketContainerSendable destroyPacket(int i) {
            PacketContainerSendable newPacket = newPacket(PacketType.Play.Server.ENTITY_DESTROY);
            try {
                newPacket.getIntegerArrays().write(0, new int[]{i});
            } catch (Exception e) {
                try {
                    newPacket.getIntLists().write(0, Collections.singletonList(Integer.valueOf(i)));
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            return newPacket;
        }

        @Override // com.github.unldenis.hologram.packet.IPackets
        public PacketContainerSendable equipmentPacket(int i, ItemStack itemStack) {
            PacketContainerSendable newPacket = newPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
            newPacket.getIntegers().write(0, Integer.valueOf(i));
            ArrayList arrayList = new ArrayList();
            arrayList.add(new Pair(EnumWrappers.ItemSlot.HEAD, itemStack));
            newPacket.getSlotStackPairLists().write(0, arrayList);
            return newPacket;
        }

        @Override // com.github.unldenis.hologram.packet.IPackets
        public PacketContainerSendable metadataPacket(int i, String str, Player player, Placeholders placeholders, boolean z, boolean z2) {
            PacketContainerSendable newPacket = newPacket(PacketType.Play.Server.ENTITY_METADATA);
            newPacket.getIntegers().write(0, Integer.valueOf(i));
            WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
            if (z) {
                wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 32);
            }
            if (placeholders != null) {
                wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(6, WrappedDataWatcher.Registry.getChatComponentSerializer()), Optional.of(WrappedChatComponent.fromChatMessage(placeholders.parse(str, player))[0].getHandle()));
                wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)), true);
            }
            if (z2) {
                wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(15, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 1);
            }
            newPacket.getWatchableCollectionModifier().write(0, wrappedDataWatcher.getWatchableObjects());
            return newPacket;
        }

        @Override // com.github.unldenis.hologram.packet.IPackets
        public PacketContainerSendable teleportPacket(int i, Location location) {
            PacketContainerSendable newPacket = newPacket(PacketType.Play.Server.ENTITY_TELEPORT);
            newPacket.getIntegers().write(0, Integer.valueOf(i));
            newPacket.getDoubles().write(0, Double.valueOf(location.getX()));
            newPacket.getDoubles().write(1, Double.valueOf(location.getY()));
            newPacket.getDoubles().write(2, Double.valueOf(location.getZ()));
            newPacket.getBytes().write(0, Byte.valueOf(getCompressAngle((double) location.getYaw())));
            newPacket.getBytes().write(1, Byte.valueOf(getCompressAngle((double) location.getPitch())));
            newPacket.getBooleans().write(0, false);
            return newPacket;
        }

        @Override // com.github.unldenis.hologram.packet.IPackets
        public List<PacketContainerSendable> rotatePackets(int i, Location location, float f) {
            PacketContainerSendable newPacket = newPacket(PacketType.Play.Server.ENTITY_LOOK);
            newPacket.getIntegers().write(0, Integer.valueOf(i));
            newPacket.getBytes().write(0, Byte.valueOf(getCompressAngle((double) f))).write(1, (byte) 0);
            newPacket.getBooleans().write(0, true);
            return Collections.singletonList(newPacket);
        }
    }

    /* loaded from: Hologram-Lib-1.4.0.jar:com/github/unldenis/hologram/packet/IPackets$PacketsV1_19.class */
    public static class PacketsV1_19 extends PacketsV1_9V1_18 {
        @Override // com.github.unldenis.hologram.packet.IPackets.PacketsV1_9V1_18, com.github.unldenis.hologram.packet.IPackets
        public PacketContainerSendable spawnPacket(int i, Location location, Plugin plugin) {
            PacketContainerSendable newPacket = newPacket(PacketType.Play.Server.SPAWN_ENTITY);
            newPacket.getIntegers().write(0, Integer.valueOf(i));
            newPacket.getIntegers().write(1, 1);
            newPacket.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
            newPacket.getUUIDs().write(0, UUID.randomUUID());
            newPacket.getDoubles().write(0, Double.valueOf(location.getX()));
            newPacket.getDoubles().write(1, Double.valueOf(location.getY()));
            newPacket.getDoubles().write(2, Double.valueOf(location.getZ()));
            return newPacket;
        }
    }
}