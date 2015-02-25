/*
 * This file is part of Guide, licensed under the MIT License (MIT).
 *
 * Copyright (c) AlmuraDev <http://beta.almuramc.com/>
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

import com.almuradev.almurasdk.AlmuraSDK;
import com.almuradev.guide.client.network.play.C00PageInformation;
import com.almuradev.guide.content.PageUtil;
import com.almuradev.guide.server.network.play.S00PageInformation;
import com.almuradev.guide.server.network.play.S01PageInformationResponse;
import com.almuradev.guide.server.network.play.S02PageOpen;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {
    public void onPreInitializationEvent(FMLPreInitializationEvent event) {
        Guide.NETWORK_FORGE.registerMessage(S00PageInformation.class, S00PageInformation.class, 0, Side.CLIENT);
        Guide.NETWORK_FORGE.registerMessage(S01PageInformationResponse.class, S01PageInformationResponse.class, 1, Side.CLIENT);
        Guide.NETWORK_FORGE.registerMessage(S02PageOpen.class, S02PageOpen.class, 2, Side.CLIENT);
        Guide.NETWORK_FORGE.registerMessage(C00PageInformation.class, C00PageInformation.class, 0, Side.SERVER);
    }

    public void onInitialization(FMLInitializationEvent event) {
    }

    public void onServerStartingEvent(FMLServerStartingEvent event) {
        PageUtil.loadAll();
    }
}
