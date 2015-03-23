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
import com.almuradev.guide.client.gui.GuideGui;
import com.almuradev.guide.content.Page;
import com.almuradev.guide.content.PageRegistry;
import com.almuradev.guide.event.PageInformationEvent;
import com.almuradev.guide.server.network.play.S00PageInformation;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

public class ClientProxy extends CommonProxy {

    public static final String CLASSPATH = "com.almuradev.guide.client.ClientProxy";
    public static final ClientPermissible PERMISSIBLE_CLIENT = new ClientPermissible();

    @Override
    public void onPreInitializationEvent(FMLPreInitializationEvent event) {
        super.onPreInitializationEvent(event);
        AlmuraSDK.getPermissionsManager().registerPermissible(PERMISSIBLE_CLIENT);
        FMLCommonHandler.instance().bus().register(this);
    }

    @Override
    public void handlePageInformation(S00PageInformation packet) {
        super.handlePageInformation(packet);

        final Page page = PageRegistry.getPage(packet.identifier).get();
        MinecraftForge.EVENT_BUS.post(new PageInformationEvent(page));
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        if (getPermissions().hasPermission("mod.guide.open") && Keyboard.isKeyDown(Keyboard.KEY_G)) {
            Minecraft.getMinecraft().displayGuiScreen(new GuideGui());
        }
    }

    public static Permissions getPermissions() {
        return AlmuraSDK.getPermissionsManager().getPermissions(PERMISSIBLE_CLIENT);
    }
}
