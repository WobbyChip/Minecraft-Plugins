package net.minecraft.server.network;

import com.google.common.primitives.Ints;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.logging.LogUtils;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.PrivateKey;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.login.PacketLoginInEncryptionBegin;
import net.minecraft.network.protocol.login.PacketLoginInListener;
import net.minecraft.network.protocol.login.PacketLoginInStart;
import net.minecraft.network.protocol.login.PacketLoginOutDisconnect;
import net.minecraft.network.protocol.login.PacketLoginOutEncryptionBegin;
import net.minecraft.network.protocol.login.PacketLoginOutSetCompression;
import net.minecraft.network.protocol.login.PacketLoginOutSuccess;
import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import net.minecraft.network.protocol.login.ServerboundLoginAcknowledgedPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.CryptographyException;
import net.minecraft.util.MinecraftEncryption;
import net.minecraft.util.RandomSource;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

// CraftBukkit start
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.craftbukkit.util.Waitable;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
// CraftBukkit end

public class LoginListener implements PacketLoginInListener, TickablePacketListener {

    private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
    static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_TICKS_BEFORE_LOGIN = 600;
    private static final IChatBaseComponent DISCONNECT_UNEXPECTED_QUERY = IChatBaseComponent.translatable("multiplayer.disconnect.unexpected_query_response");
    private final byte[] challenge;
    final MinecraftServer server;
    public final NetworkManager connection;
    private volatile LoginListener.EnumProtocolState state;
    private int tick;
    @Nullable
    String requestedUsername;
    @Nullable
    private GameProfile authenticatedProfile;
    private final String serverId;
    private EntityPlayer player; // CraftBukkit

    public LoginListener(MinecraftServer minecraftserver, NetworkManager networkmanager) {
        this.state = LoginListener.EnumProtocolState.HELLO;
        this.serverId = "";
        this.server = minecraftserver;
        this.connection = networkmanager;
        this.challenge = Ints.toByteArray(RandomSource.create().nextInt());
    }

    @Override
    public void tick() {
        if (this.state == LoginListener.EnumProtocolState.VERIFYING) {
            this.verifyLoginAndFinishConnectionSetup((GameProfile) Objects.requireNonNull(this.authenticatedProfile));
        }

        if (this.state == LoginListener.EnumProtocolState.WAITING_FOR_DUPE_DISCONNECT && !this.isPlayerAlreadyInWorld((GameProfile) Objects.requireNonNull(this.authenticatedProfile))) {
            this.finishLoginAndWaitForClient(this.authenticatedProfile);
        }

        if (this.tick++ == 600) {
            this.disconnect(IChatBaseComponent.translatable("multiplayer.disconnect.slow_login"));
        }

    }

    // CraftBukkit start
    @Deprecated
    public void disconnect(String s) {
        disconnect(IChatBaseComponent.literal(s));
    }
    // CraftBukkit end

    @Override
    public boolean isAcceptingMessages() {
        return this.connection.isConnected();
    }

    public void disconnect(IChatBaseComponent ichatbasecomponent) {
        try {
            LoginListener.LOGGER.info("Disconnecting {}: {}", this.getUserName(), ichatbasecomponent.getString());
            this.connection.send(new PacketLoginOutDisconnect(ichatbasecomponent));
            this.connection.disconnect(ichatbasecomponent);
        } catch (Exception exception) {
            LoginListener.LOGGER.error("Error whilst disconnecting player", exception);
        }

    }

    private boolean isPlayerAlreadyInWorld(GameProfile gameprofile) {
        return this.server.getPlayerList().getPlayer(gameprofile.getId()) != null;
    }

    @Override
    public void onDisconnect(IChatBaseComponent ichatbasecomponent) {
        LoginListener.LOGGER.info("{} lost connection: {}", this.getUserName(), ichatbasecomponent.getString());
    }

    public String getUserName() {
        String s = this.connection.getLoggableAddress(this.server.logIPs());

        return this.requestedUsername != null ? this.requestedUsername + " (" + s + ")" : s;
    }

    @Override
    public void handleHello(PacketLoginInStart packetlogininstart) {
        Validate.validState(this.state == LoginListener.EnumProtocolState.HELLO, "Unexpected hello packet", new Object[0]);
        Validate.validState(isValidUsername(packetlogininstart.name()), "Invalid characters in username", new Object[0]);
        this.requestedUsername = packetlogininstart.name();
        GameProfile gameprofile = this.server.getSingleplayerProfile();

        if (gameprofile != null && this.requestedUsername.equalsIgnoreCase(gameprofile.getName())) {
            this.startClientVerification(gameprofile);
        } else {
            if (this.server.usesAuthentication() && !this.connection.isMemoryConnection()) {
                this.state = LoginListener.EnumProtocolState.KEY;
                this.connection.send(new PacketLoginOutEncryptionBegin("", this.server.getKeyPair().getPublic().getEncoded(), this.challenge));
            } else {
                // Spigot start
                new Thread("User Authenticator #" + LoginListener.UNIQUE_THREAD_ID.incrementAndGet()) {

                    @Override
                    public void run() {
                        try {
                            new LoginHandler().fireEvents(createOfflineProfile(LoginListener.this.requestedUsername));
                        } catch (Exception ex) {
                            disconnect("Failed to verify username!");
                            server.server.getLogger().log(java.util.logging.Level.WARNING, "Exception verifying " + LoginListener.this.requestedUsername, ex);
                        }
                    }
                }.start();
                // Spigot end
            }

        }
    }

    void startClientVerification(GameProfile gameprofile) {
        this.authenticatedProfile = gameprofile;
        this.state = LoginListener.EnumProtocolState.VERIFYING;
    }

    private void verifyLoginAndFinishConnectionSetup(GameProfile gameprofile) {
        PlayerList playerlist = this.server.getPlayerList();
        // CraftBukkit start - fire PlayerLoginEvent
        this.player = playerlist.canPlayerLogin(this, gameprofile); // CraftBukkit

        if (this.player == null) {
            // this.disconnect(ichatbasecomponent);
            // CraftBukkit end
        } else {
            if (this.server.getCompressionThreshold() >= 0 && !this.connection.isMemoryConnection()) {
                this.connection.send(new PacketLoginOutSetCompression(this.server.getCompressionThreshold()), PacketSendListener.thenRun(() -> {
                    this.connection.setupCompression(this.server.getCompressionThreshold(), true);
                }));
            }

            boolean flag = playerlist.disconnectAllPlayersWithProfile(gameprofile, this.player); // CraftBukkit - add player reference

            if (flag) {
                this.state = LoginListener.EnumProtocolState.WAITING_FOR_DUPE_DISCONNECT;
            } else {
                this.finishLoginAndWaitForClient(gameprofile);
            }
        }

    }

    private void finishLoginAndWaitForClient(GameProfile gameprofile) {
        this.state = LoginListener.EnumProtocolState.PROTOCOL_SWITCHING;
        this.connection.send(new PacketLoginOutSuccess(gameprofile));
    }

    public static boolean isValidUsername(String s) {
        return s.chars().filter((i) -> {
            return i <= 32 || i >= 127;
        }).findAny().isEmpty();
    }

    @Override
    public void handleKey(PacketLoginInEncryptionBegin packetlogininencryptionbegin) {
        Validate.validState(this.state == LoginListener.EnumProtocolState.KEY, "Unexpected key packet", new Object[0]);

        final String s;

        try {
            PrivateKey privatekey = this.server.getKeyPair().getPrivate();

            if (!packetlogininencryptionbegin.isChallengeValid(this.challenge, privatekey)) {
                throw new IllegalStateException("Protocol error");
            }

            SecretKey secretkey = packetlogininencryptionbegin.getSecretKey(privatekey);
            Cipher cipher = MinecraftEncryption.getCipher(2, secretkey);
            Cipher cipher1 = MinecraftEncryption.getCipher(1, secretkey);

            s = (new BigInteger(MinecraftEncryption.digestData("", this.server.getKeyPair().getPublic(), secretkey))).toString(16);
            this.state = LoginListener.EnumProtocolState.AUTHENTICATING;
            this.connection.setEncryptionKey(cipher, cipher1);
        } catch (CryptographyException cryptographyexception) {
            throw new IllegalStateException("Protocol error", cryptographyexception);
        }

        Thread thread = new Thread("User Authenticator #" + LoginListener.UNIQUE_THREAD_ID.incrementAndGet()) {
            public void run() {
                String s1 = (String) Objects.requireNonNull(LoginListener.this.requestedUsername, "Player name not initialized");

                try {
                    ProfileResult profileresult = LoginListener.this.server.getSessionService().hasJoinedServer(s1, s, this.getAddress());

                    if (profileresult != null) {
                        GameProfile gameprofile = profileresult.profile();

                        // CraftBukkit start - fire PlayerPreLoginEvent
                        if (!connection.isConnected()) {
                            return;
                        }

                        new LoginHandler().fireEvents(gameprofile);
                    } else if (LoginListener.this.server.isSingleplayer()) {
                        LoginListener.LOGGER.warn("Failed to verify username but will let them in anyway!");
                        LoginListener.this.startClientVerification(LoginListener.this.createOfflineProfile(s1)); // Spigot
                    } else {
                        LoginListener.this.disconnect(IChatBaseComponent.translatable("multiplayer.disconnect.unverified_username"));
                        LoginListener.LOGGER.error("Username '{}' tried to join with an invalid session", s1);
                    }
                } catch (AuthenticationUnavailableException authenticationunavailableexception) {
                    if (LoginListener.this.server.isSingleplayer()) {
                        LoginListener.LOGGER.warn("Authentication servers are down but will let them in anyway!");
                        LoginListener.this.startClientVerification(LoginListener.this.createOfflineProfile(s1)); // Spigot
                    } else {
                        LoginListener.this.disconnect(IChatBaseComponent.translatable("multiplayer.disconnect.authservers_down"));
                        LoginListener.LOGGER.error("Couldn't verify username because servers are unavailable");
                    }
                    // CraftBukkit start - catch all exceptions
                } catch (Exception exception) {
                    disconnect("Failed to verify username!");
                    server.server.getLogger().log(java.util.logging.Level.WARNING, "Exception verifying " + s1, exception);
                    // CraftBukkit end
                }

            }

            @Nullable
            private InetAddress getAddress() {
                SocketAddress socketaddress = LoginListener.this.connection.getRemoteAddress();

                return LoginListener.this.server.getPreventProxyConnections() && socketaddress instanceof InetSocketAddress ? ((InetSocketAddress) socketaddress).getAddress() : null;
            }
        };

        thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LoginListener.LOGGER));
        thread.start();
    }

    // Spigot start
    public class LoginHandler {

        public void fireEvents(GameProfile gameprofile) throws Exception {
                        String playerName = gameprofile.getName();
                        java.net.InetAddress address = ((java.net.InetSocketAddress) connection.getRemoteAddress()).getAddress();
                        java.util.UUID uniqueId = gameprofile.getId();
                        final org.bukkit.craftbukkit.CraftServer server = LoginListener.this.server.server;

                        AsyncPlayerPreLoginEvent asyncEvent = new AsyncPlayerPreLoginEvent(playerName, address, uniqueId);
                        server.getPluginManager().callEvent(asyncEvent);

                        if (PlayerPreLoginEvent.getHandlerList().getRegisteredListeners().length != 0) {
                            final PlayerPreLoginEvent event = new PlayerPreLoginEvent(playerName, address, uniqueId);
                            if (asyncEvent.getResult() != PlayerPreLoginEvent.Result.ALLOWED) {
                                event.disallow(asyncEvent.getResult(), asyncEvent.getKickMessage());
                            }
                            Waitable<PlayerPreLoginEvent.Result> waitable = new Waitable<PlayerPreLoginEvent.Result>() {
                                @Override
                                protected PlayerPreLoginEvent.Result evaluate() {
                                    server.getPluginManager().callEvent(event);
                                    return event.getResult();
                                }};

                            LoginListener.this.server.processQueue.add(waitable);
                            if (waitable.get() != PlayerPreLoginEvent.Result.ALLOWED) {
                                disconnect(event.getKickMessage());
                                return;
                            }
                        } else {
                            if (asyncEvent.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
                                disconnect(asyncEvent.getKickMessage());
                                return;
                            }
                        }
                        // CraftBukkit end
                        LoginListener.LOGGER.info("UUID of player {} is {}", gameprofile.getName(), gameprofile.getId());
                        LoginListener.this.startClientVerification(gameprofile);
        }
    }
    // Spigot end

    @Override
    public void handleCustomQueryPacket(ServerboundCustomQueryAnswerPacket serverboundcustomqueryanswerpacket) {
        this.disconnect(LoginListener.DISCONNECT_UNEXPECTED_QUERY);
    }

    @Override
    public void handleLoginAcknowledgement(ServerboundLoginAcknowledgedPacket serverboundloginacknowledgedpacket) {
        Validate.validState(this.state == LoginListener.EnumProtocolState.PROTOCOL_SWITCHING, "Unexpected login acknowledgement packet", new Object[0]);
        CommonListenerCookie commonlistenercookie = CommonListenerCookie.createInitial((GameProfile) Objects.requireNonNull(this.authenticatedProfile));
        ServerConfigurationPacketListenerImpl serverconfigurationpacketlistenerimpl = new ServerConfigurationPacketListenerImpl(this.server, this.connection, commonlistenercookie, this.player); // CraftBukkit

        this.connection.setListener(serverconfigurationpacketlistenerimpl);
        serverconfigurationpacketlistenerimpl.startConfiguration();
        this.state = LoginListener.EnumProtocolState.ACCEPTED;
    }

    // Spigot start
    protected GameProfile createOfflineProfile(String s) {
        UUID uuid;
        if ( connection.spoofedUUID != null )
        {
            uuid = connection.spoofedUUID;
        } else
        {
            uuid = UUIDUtil.createOfflinePlayerUUID( s );
        }

        GameProfile gameProfile = new GameProfile( uuid, s );

        if (connection.spoofedProfile != null)
        {
            for ( com.mojang.authlib.properties.Property property : connection.spoofedProfile )
            {
                if ( !HandshakeListener.PROP_PATTERN.matcher( property.name()).matches() ) continue;
                gameProfile.getProperties().put( property.name(), property );
            }
        }

        return gameProfile;
        // Spigot end
    }

    private static enum EnumProtocolState {

        HELLO, KEY, AUTHENTICATING, NEGOTIATING, VERIFYING, WAITING_FOR_DUPE_DISCONNECT, PROTOCOL_SWITCHING, ACCEPTED;

        private EnumProtocolState() {}
    }
}
