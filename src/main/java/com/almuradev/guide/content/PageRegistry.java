/**
 * This file is part of Guide, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 AlmuraDev <http://github.com/AlmuraDev/>
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
package com.almuradev.guide.content;

import com.almuradev.guide.client.network.play.C00PageInformation;
import com.almuradev.guide.server.network.play.S00PageInformation;
import com.almuradev.guide.server.network.play.S01PageInformationResponse;
import com.almuradev.guide.server.network.play.S02PageOpen;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.Map;

public class PageRegistry {
    private static final Map<String, Page> PAGES = Maps.newHashMap();

    public static Optional<Page> getPage(String identifier) {
        return Optional.fromNullable(PAGES.get(identifier));
    }

    public static Page putPage(Page page) {
        return PAGES.put(page.getIdentifier(), page);
    }

    public static Map<String, Page> getAll() {
        return Collections.unmodifiableMap(PAGES);
    }

    public static void clearPages() {
        PAGES.clear();
    }

    @SideOnly(Side.CLIENT)
    public static void handlePageInformation(S00PageInformation packet) {
        Page page = PAGES.get(packet.identifier);

        if (page == null) {
            page = new Page(packet.identifier, packet.name, packet.created, packet.author, packet.lastModified, packet.lastContributor, packet.contents);
            PAGES.put(packet.identifier, page);
        } else {
            page
                    .setCreated(packet.created)
                    .setAuthor(packet.author)
                    .setLastModified(packet.lastModified)
                    .setLastContributor(packet.lastContributor)
                    .setContents(packet.contents);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void handlePageInformationResponse(S01PageInformationResponse packet) {
    }

    @SideOnly(Side.CLIENT)
    public static void handlePageOpen(S02PageOpen packet) {}

    @SideOnly(Side.SERVER)
    public static S01PageInformationResponse handlePageInformation(C00PageInformation packet) {
        return null;
    }
}
