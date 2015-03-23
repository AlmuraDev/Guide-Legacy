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
package com.almuradev.guide.server.network.play;

import com.almuradev.guide.Guide;
import com.almuradev.guide.content.Page;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

import java.util.Date;

/**
 * Instructs a client to synchronize a {@link Page}'s information or to store a new one.
 */
public class S00PageInformation implements IMessage, IMessageHandler<S00PageInformation, IMessage> {

    public String identifier, name, author, lastContributor, contents;
    public Date created, lastModified;

    public S00PageInformation() {
    }

    public S00PageInformation(String identifier, String name, Date created, String author, Date lastModified, String lastContributor,
            String contents) {
        this.identifier = identifier;
        this.name = name;
        this.created = created;
        this.author = author;
        this.lastModified = lastModified;
        this.lastContributor = lastContributor;
        this.contents = contents;
    }

    public S00PageInformation(Page page) {
        this(page.getIdentifier(), page.getName(), page.getCreated(), page.getAuthor(), page.getLastModified(), page.getLastContributor(),
                page.getContents());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        identifier = ByteBufUtils.readUTF8String(buf);
        name = ByteBufUtils.readUTF8String(buf);
        created = new Date(buf.readLong());
        author = ByteBufUtils.readUTF8String(buf);
        lastModified = new Date(buf.readLong());
        lastContributor = ByteBufUtils.readUTF8String(buf);
        contents = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, identifier);
        ByteBufUtils.writeUTF8String(buf, name);
        buf.writeLong(created.getTime());
        ByteBufUtils.writeUTF8String(buf, author);
        buf.writeLong(lastModified.getTime());
        ByteBufUtils.writeUTF8String(buf, lastContributor);
        ByteBufUtils.writeUTF8String(buf, contents);
    }

    @Override
    public IMessage onMessage(S00PageInformation message, MessageContext ctx) {
        Guide.PROXY.handlePageInformation(message);
        return null;
    }
}
