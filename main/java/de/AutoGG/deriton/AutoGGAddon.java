// Written by Deriton | E-Mail: deriton@outlook.de | Discord: Deriton#2913 |

package de.AutoGG.deriton;


import com.ibm.icu.impl.Differ;
import net.labymod.api.LabyModAddon;
import net.labymod.api.events.MessageReceiveEvent;
import net.labymod.ingamegui.enums.EnumModuleAlignment;
import net.labymod.settings.elements.*;
import net.labymod.utils.Consumer;
import net.labymod.utils.Material;
import net.labymod.utils.ServerData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.net.InetAddress;
import java.util.List;

public class AutoGGAddon extends LabyModAddon {

    private boolean enabled = true;
    private String GGMessage = "EMPTY";
    private Differ<DropDownElement<EnumModuleAlignment>> subSettings;
    private int sendDelay = 0;
    private int nextGG = 200;

    /*
    * server[0].name = "GommeHD.net";
    * server[0].german = "Statistik";
    * server[0].englisch = "Statistics";
    *
    * server[1].name = "Hypixel.net";
    * server[1].german = "";
    * server[1].englisch = "";
    * */

    @Override
    public void onEnable() {
        System.out.println("[AutoGG] Started!");
        this.getApi().registerForgeListener(this);
        this.getApi().getEventManager().registerOnJoin(new Consumer<ServerData>() {
            public void accept(final ServerData serverData) {
                if (!enabled) {
                    return;
                }

            }
        });

        this.getApi().getEventManager().register(new MessageReceiveEvent() {
            @Override
            public boolean onReceive(String s, String s1) {
                // player messages includes a ":" and Server Messages didnt
                // Check repeat delay
                if (nextGG == 0 && enabled && !s1.contains(":") &&
                     //GommeHD German
                    (s1.contains("-= Statistiken dieser Runde =-") || s1.contains("--------- Match-Statistiken ---------")) ||
                    //GommeHD English
                    s1.contains("-= Statistics of this game =-") || s1.contains("--------- Match statistics ---------") ||
                    // Hypixel German & English
                    s.contains("§r                            §f§lReward Summary§f§l")

                ) {
                        // Dont write in statistics
                    sendDelay = 5;
                    // Spam Protection
                    nextGG = 200 + sendDelay;
                }
                return false;
            }
        });
    }

    @SubscribeEvent
    public void onTick(final TickEvent.ClientTickEvent Tick) {
        if(sendDelay > 0) {
            if(sendDelay == 1) {
                EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
                player.sendChatMessage(GGMessage);
            }
            sendDelay--;
        }
        if(nextGG > 0) {
            nextGG--;
        }
    }

    @Override
    public void loadConfig() {
        this.enabled = this.getConfig().has("enabled") ? this.getConfig().get("enabled").getAsBoolean() : true;
        this.GGMessage = this.getConfig().has("Text") ? this.getConfig().get("Text").getAsString() : "GG";
    }


    @Override
    protected void fillSettings(final List<SettingsElement> list) {
        final BooleanElement booleanElement = new BooleanElement("Enabled", new ControlElement.IconData(Material.LEVER), new Consumer<Boolean>() {

            public void accept(final Boolean enabledAddon) {
                AutoGGAddon.this.enabled = enabledAddon;

                AutoGGAddon.this.getConfig().addProperty("enabled", enabled);
                AutoGGAddon.this.saveConfig();

            }
        }, this.enabled);
        list.add(booleanElement);

        StringElement channelStringElement = new StringElement("Text" , new ControlElement.IconData(Material.PAPER),
                GGMessage , new Consumer<String>() {
            public void accept(final String accepted) {
            AutoGGAddon.this.GGMessage = accepted;

                AutoGGAddon.this.getConfig().addProperty("Text", GGMessage);
                AutoGGAddon.this.saveConfig();
            }
        });
        list.add(channelStringElement);

    }


}