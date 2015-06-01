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
        Guide.PROXY.handlePageInformation(ctx, message);
        return null;
    }
}
