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
package com.almuradev.guide;

import com.almuradev.guide.client.network.play.C00PageInformation;
import com.almuradev.guide.content.Page;
import com.almuradev.guide.content.PageRegistry;
import com.almuradev.guide.content.PageUtil;
import com.almuradev.guide.server.network.play.S00PageInformation;
import com.almuradev.guide.server.network.play.S01PageDelete;
import com.almuradev.guide.server.network.play.S02PageOpen;
import com.google.common.base.Optional;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

import java.io.IOException;
import java.util.Date;

public class CommonProxy {

    public void onPreInitializationEvent(FMLPreInitializationEvent event) {
        Guide.NETWORK_FORGE.registerMessage(S00PageInformation.class, S00PageInformation.class, 0, Side.CLIENT);
        Guide.NETWORK_FORGE.registerMessage(C00PageInformation.class, C00PageInformation.class, 1, Side.SERVER);
        Guide.NETWORK_FORGE.registerMessage(S01PageDelete.class, S01PageDelete.class, 2, Side.CLIENT);
        Guide.NETWORK_FORGE.registerMessage(S01PageDelete.class, S01PageDelete.class, 3, Side.SERVER);
        Guide.NETWORK_FORGE.registerMessage(S02PageOpen.class, S02PageOpen.class, 4, Side.CLIENT);
    }

    public void onInitialization(FMLInitializationEvent event) {
    }

    public void onServerStartingEvent(FMLServerStartingEvent event) {
        PageUtil.loadAll();
    }

    public void handlePageInformation(MessageContext ctx, S00PageInformation message) {
    }

    public void handlePageInformation(MessageContext ctx, C00PageInformation message){
        final Optional<Page> optPage = PageRegistry.getPage(message.identifier);
        final Page page;

        if (optPage.isPresent()) {
            page = optPage.get();
            page
                    .setIndex(message.index)
                    .setLastContributor(ctx.getServerHandler().playerEntity.getCommandSenderName())
                    .setLastModified(new Date())
                    .setTitle(message.title)
                    .setContents(PageUtil.replaceColorCodes("&", message.contents, true));
        } else {
            // String identifier, int index, String name, Date created, String author, Date lastModified, String lastContributor, String contents
            page = new Page(message.identifier, message.index, message.title, new Date(),
                    ctx.getServerHandler().playerEntity.getCommandSenderName(), new Date(),
                    ctx.getServerHandler().playerEntity.getCommandSenderName(), message.contents);
            PageRegistry.putPage(page);
        }

        if (canSavePages()) {
            savePage(message.identifier, page);
        }

        Guide.NETWORK_FORGE.sendToAll(new S00PageInformation(page));
    }

    public void handlePageDelete(MessageContext ctx, S01PageDelete message) {
        PageRegistry.removePage(message.identifier);
        if (canSavePages()) {
            deletePage(message.identifier);
        }
    }

    public void handlePageOpen(MessageContext ctx, S02PageOpen message) {
    }

    public boolean canSavePages() {
        return false;
    }

    private void savePage(String identifier, Page page) {
        try {
            PageUtil.savePage(identifier, page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deletePage(String identifier) {
        try {
            PageUtil.deletePage(identifier);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
