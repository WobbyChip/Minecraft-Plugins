package net.minecraft.network.protocol;

import com.mojang.logging.LogUtils;
import net.minecraft.ReportedException;
import net.minecraft.network.PacketListener;
import net.minecraft.server.CancelledPacketHandleException;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.thread.IAsyncTaskHandler;
import org.slf4j.Logger;

// CraftBukkit start
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
// CraftBukkit end

public class PlayerConnectionUtils {

    private static final Logger LOGGER = LogUtils.getLogger();

    public PlayerConnectionUtils() {}

    public static <T extends PacketListener> void ensureRunningOnSameThread(Packet<T> packet, T t0, WorldServer worldserver) throws CancelledPacketHandleException {
        ensureRunningOnSameThread(packet, t0, (IAsyncTaskHandler) worldserver.getServer());
    }

    public static <T extends PacketListener> void ensureRunningOnSameThread(Packet<T> packet, T t0, IAsyncTaskHandler<?> iasynctaskhandler) throws CancelledPacketHandleException {
        if (!iasynctaskhandler.isSameThread()) {
            iasynctaskhandler.executeIfPossible(() -> {
                if (MinecraftServer.getServer().hasStopped() || (t0 instanceof ServerCommonPacketListenerImpl && ((ServerCommonPacketListenerImpl) t0).processedDisconnect)) return; // CraftBukkit, MC-142590
                if (t0.shouldHandleMessage(packet)) {
                    try {
                        packet.handle(t0);
                    } catch (Exception exception) {
                        if (exception instanceof ReportedException) {
                            ReportedException reportedexception = (ReportedException) exception;

                            if (reportedexception.getCause() instanceof OutOfMemoryError) {
                                throw exception;
                            }
                        }

                        if (!t0.shouldPropagateHandlingExceptions()) {
                            PlayerConnectionUtils.LOGGER.error("Failed to handle packet {}, suppressing error", packet, exception);
                            return;
                        }

                        throw exception;
                    }
                } else {
                    PlayerConnectionUtils.LOGGER.debug("Ignoring packet due to disconnection: {}", packet);
                }

            });
            throw CancelledPacketHandleException.RUNNING_ON_DIFFERENT_THREAD;
            // CraftBukkit start - SPIGOT-5477, MC-142590
        } else if (MinecraftServer.getServer().hasStopped() || (t0 instanceof ServerCommonPacketListenerImpl && ((ServerCommonPacketListenerImpl) t0).processedDisconnect)) {
            throw CancelledPacketHandleException.RUNNING_ON_DIFFERENT_THREAD;
            // CraftBukkit end
        }
    }
}