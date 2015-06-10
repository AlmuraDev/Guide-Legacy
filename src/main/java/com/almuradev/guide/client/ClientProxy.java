/*
 * This file is part of Guide, licensed under the MIT License (MIT).
 *
 * Copyright (c) AlmuraDev <http://github.com/AlmuraDev/Guide/>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.almuradev.guide.client;

import com.almuradev.almurasdk.AlmuraSDK;
import com.almuradev.almurasdk.permissions.Permissions;
import com.almuradev.guide.CommonProxy;
import com.almuradev.guide.Guide;
import com.almuradev.guide.client.gui.ViewPagesGui;
import com.almuradev.guide.content.Page;
import com.almuradev.guide.content.PageRegistry;
import com.almuradev.guide.event.PageDeleteEvent;
import com.almuradev.guide.event.PageInformationEvent;
import com.almuradev.guide.server.network.play.S00PageInformation;
import com.almuradev.guide.server.network.play.S01PageDelete;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

public class ClientProxy extends CommonProxy {

    public static final String CLASSPATH = "com.almuradev.guide.client.ClientProxy";
    public static final ClientPermissible PERMISSIBLE_CLIENT = new ClientPermissible();

    public static Permissions getPermissions() {
        return AlmuraSDK.getPermissionsManager().getPermissions(PERMISSIBLE_CLIENT);
    }

    @Override
    public void onPreInitializationEvent(FMLPreInitializationEvent event) {
        super.onPreInitializationEvent(event);
        AlmuraSDK.getPermissionsManager().registerPermissible(PERMISSIBLE_CLIENT);
        FMLCommonHandler.instance().bus().register(this);
    }

    @Override
    public void handlePageInformation(MessageContext ctx, S00PageInformation message) {
        Page page = PageRegistry.getPage(message.identifier).orNull();

        if (page == null) {
            page = new Page(message.identifier, message.index, message.title, message.created, message.author,
                    message.lastModified, message.lastContributor, message.contents);
            PageRegistry.putPage(page);
        } else {
            page

                    .setIndex(message.index)
                    .setTitle(message.title)
                    .setCreated(message.created)
                    .setAuthor(message.author)
                    .setLastModified(message.lastModified)
                    .setLastContributor(message.lastContributor)
                    .setContents(message.contents);
        }
        MinecraftForge.EVENT_BUS.post(new PageInformationEvent(page));
    }

    @Override
    public void handlePageDelete(MessageContext ctx, S01PageDelete message) {
        super.handlePageDelete(ctx, message);
        if (ctx.side.isClient()) {
            MinecraftForge.EVENT_BUS.post(new PageDeleteEvent(message.identifier));
        } else if (!MinecraftServer.getServer().isDedicatedServer()) {
            Guide.NETWORK_FORGE.sendToAll(new S01PageDelete(message.identifier));
        }
    }

    @Override
    public boolean canSavePages() {
        return FMLCommonHandler.instance().getEffectiveSide().isServer() && !MinecraftServer.getServer().isDedicatedServer();
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        if (getPermissions().hasPermission("mod.guide.open") && Keyboard.isKeyDown(Keyboard.KEY_G)) {
            Minecraft.getMinecraft().displayGuiScreen(new ViewPagesGui());
        }
    }
}
