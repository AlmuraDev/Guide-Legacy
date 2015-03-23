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
import com.almuradev.guide.content.PageUtil;
import com.almuradev.guide.event.PageInformationEvent;
import com.almuradev.guide.server.network.play.S00PageInformation;
import com.google.common.base.Optional;
import net.minecraftforge.common.MinecraftForge;

import java.io.IOException;

public class ServerProxy extends CommonProxy {

    public static final String CLASSPATH = "com.almuradev.guide.server.ServerProxy";

    @Override
    public void handlePageInformation(S00PageInformation packet) {
        super.handlePageInformation(packet);

        final Optional<Page> optPage = PageRegistry.getPage(packet.identifier);

        if (optPage.isPresent()) {
            final Page page = optPage.get();

            MinecraftForge.EVENT_BUS.post(new PageInformationEvent(page));

            try {
                PageUtil.savePage(packet.identifier, page);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Guide.NETWORK_FORGE.sendToAll(
                    new S00PageInformation(page.getIdentifier(), page.getName(), page.getCreated(), page.getAuthor(), page.getLastModified(),
                            page.getLastContributor(), page.getContents()));

        }
    }
}
