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
package com.almuradev.guide.server;

import com.almuradev.guide.CommonProxy;
import com.almuradev.guide.Guide;
import com.almuradev.guide.content.Page;
import com.almuradev.guide.content.PageRegistry;
import com.almuradev.guide.server.network.play.S00PageInformation;
import com.almuradev.guide.server.network.play.S01PageDelete;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.Map;

public class ServerProxy extends CommonProxy {

    public static final String CLASSPATH = "com.almuradev.guide.server.ServerProxy";

    @Override
    public void onInitialization(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(this);
    }

    @Override
    public void handlePageDelete(MessageContext ctx, S01PageDelete message) {
        super.handlePageDelete(ctx, message);

        Guide.NETWORK_FORGE.sendToAll(new S01PageDelete(message.identifier));
    }

    @Override
    public boolean canSavePages() {
        return true;
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        for (Map.Entry<String, Page> entry : PageRegistry.getAll().entrySet()) {
            Guide.NETWORK_FORGE.sendTo(new S00PageInformation(entry.getValue()), (EntityPlayerMP) event.player);
            // TODO This is how you open a gui on their client, replace "byes" with the page's identifier
            //Guide.NETWORK_FORGE.sendTo(new S02PageOpen("byes"), (EntityPlayerMP) event.player);
        }
    }
}
