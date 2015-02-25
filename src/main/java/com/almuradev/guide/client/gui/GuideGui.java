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
package com.almuradev.guide.client.gui;

import com.almuradev.almurasdk.client.gui.SimpleGui;
import com.almuradev.almurasdk.client.gui.components.UIForm;
import com.almuradev.almurasdk.util.Colors;
import com.almuradev.guide.content.Page;
import com.almuradev.guide.content.PageRegistry;
import com.almuradev.guide.content.PageUtil;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import net.malisis.core.client.gui.Anchor;
import net.malisis.core.client.gui.component.interaction.UISelect;
import net.malisis.core.client.gui.component.interaction.UITextField;
import org.lwjgl.input.Keyboard;

import java.util.HashMap;
import java.util.Map;

public class GuideGui extends SimpleGui {
    private UISelect uisPage;
    private UITextField utfContents;

    public GuideGui(SimpleGui gui) {
        super(gui);
        buildGui();
    }

    @Override
    protected void buildGui() {
        guiscreenBackground = false;

        final UIForm frmGuide = new UIForm(this, 450, 250, Colors.BOLD + "" + Colors.GOLD + "Guide");
        frmGuide.setAnchor(Anchor.CENTER | Anchor.MIDDLE);
        frmGuide.setName("form.guide");
        frmGuide.setColor(13158600);
        frmGuide.setBackgroundAlpha(255);

        uisPage = new UISelect(this, 140, populate());
        uisPage.setAnchor(Anchor.TOP | Anchor.RIGHT);
        uisPage.setPosition(-5, 5);
        uisPage.setName("form.guide.select.page");
        uisPage.setTextShadow(false);
        uisPage.register(this);
        frmGuide.getContentContainer().add(uisPage);

        utfContents = new UITextField(this, true);
        utfContents.setPosition(5, 20);
        utfContents.setSize(440, 200);
        utfContents.setName("form.guide.textfield.contents");
        utfContents.setColors(utfContents.getTextColor(), 459343, 13158600, uisPage.getSelectTextColor(), false);
        frmGuide.getContentContainer().add(utfContents);

        addToScreen(frmGuide);

        uisPage.select(0);
    }

    @Subscribe
    public void onUISelectEvent(UISelect.SelectEvent event) {
        if (event.getOption() == null) {
            return;
        }
        if (event.getComponent().getName().equalsIgnoreCase("form.guide.select.page")) {
            ((UIForm) event.getComponent().getParent().getParent()).setTitle(Colors.GOLD + "" + Colors.ITALIC + "Guide (" + event.getNewValue().getLabel() + ")");
            utfContents.setText(((Page) event.getNewValue().getKey()).getContents());
        }
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) {
        super.keyTyped(keyChar, keyCode);

        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_F5)) {
            int selIndex = -1;

            if (uisPage.getSelectedOption() != null) {
                selIndex = uisPage.getSelectedOption().getIndex();
            }

            PageUtil.loadAll();

            final Map<Integer, UISelect.Option> options = populate();
            uisPage.setOptions(options);

            if (!options.isEmpty()) {
                if (selIndex != -1 && options.get(selIndex) != null) {
                    uisPage.select(selIndex);
                } else {
                    uisPage.select(0);
                }
            }
        }
    }

    public Map<Integer, UISelect.Option> populate() {
        final HashMap<Integer, UISelect.Option> options = Maps.newHashMap();

        int count = 0;
        for (Map.Entry<String, Page> entry : PageRegistry.getAll().entrySet()) {
            options.put(count, new UISelect.Option<>(count, entry.getValue(), entry.getValue().getName()));
            count++;
        }

        return options;
    }
}
