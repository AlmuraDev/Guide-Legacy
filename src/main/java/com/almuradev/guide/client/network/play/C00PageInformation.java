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
package com.almuradev.guide.client.network.play;

import com.almuradev.guide.Guide;
import com.almuradev.guide.content.Page;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class C00PageInformation implements IMessage, IMessageHandler<C00PageInformation, IMessage> {

    public int index;
    public String identifier, title, contents;

    public C00PageInformation() {
    }

    public C00PageInformation(Page page) {
        this.identifier = page.getIdentifier();
        this.index = page.getIndex();
        this.title = page.getName();
        this.contents = page.getContents();
    }

    public C00PageInformation(String identifier, int index, String title, String contents) {
        this.identifier = identifier;
        this.index = index;
        this.title = title;
        this.contents = contents;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        identifier = ByteBufUtils.readUTF8String(buf);
        index = buf.readInt();
        title = ByteBufUtils.readUTF8String(buf);
        contents = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, identifier);
        buf.writeInt(index);
        ByteBufUtils.writeUTF8String(buf, title);
        ByteBufUtils.writeUTF8String(buf, contents);
    }

    @Override
    public IMessage onMessage(C00PageInformation message, MessageContext ctx) {
        if (ctx.side.isServer()) {
            Guide.PROXY.handlePageInformation(ctx, message);
        }
        return null;
    }
}
