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
package com.almuradev.guide.client.gui;

import com.almuradev.almurasdk.client.gui.SimpleGui;
import com.almuradev.almurasdk.client.gui.components.UIForm;
import com.almuradev.almurasdk.util.Color;
import com.almuradev.guide.content.Page;
import com.almuradev.guide.content.PageRegistry;
import com.almuradev.guide.content.PageUtil;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import net.malisis.core.client.gui.Anchor;
import net.malisis.core.client.gui.component.interaction.UIButton;
import net.malisis.core.client.gui.component.interaction.UISelect;
import net.malisis.core.client.gui.component.interaction.UITextField;
import org.lwjgl.input.Keyboard;

import java.util.HashMap;
import java.util.Map;

public class GuideGui extends SimpleGui {
    public static final Color CONTROL = new Color(13158600);
    public static final Color GRAY_DARK_BLUE_MIX = new Color(459343);

    private UISelect usPage;
    private UITextField utfContents;

    public GuideGui() {
        buildGui();
    }

    @Override
    protected void buildGui() {
        guiscreenBackground = false;

        final UIForm frmGuide = new UIForm(this, 450, 275, "Guide");
        frmGuide.setAnchor(Anchor.CENTER | Anchor.MIDDLE);
        frmGuide.setName("form.guide");
        frmGuide.setColor(CONTROL.getGuiColorCode());
        frmGuide.setBackgroundAlpha(255);

        usPage = new UISelect(this, 140, populate());
        usPage.setAnchor(Anchor.TOP | Anchor.RIGHT);
        usPage.setPosition(-5, 5);
        usPage.setName("form.guide.select.page");
        usPage.setTextShadow(false);
        usPage.register(this);
        frmGuide.getContentContainer().add(usPage);

        utfContents = new UITextField(this, true);
        utfContents.setPosition(5, 20);
        utfContents.setSize(440, 215);
        utfContents.setName("form.guide.textfield.contents");
        utfContents.setColors(utfContents.getTextColor(), GRAY_DARK_BLUE_MIX.getGuiColorCode(), CONTROL.getGuiColorCode(), usPage.getSelectTextColor(), false);
        utfContents.getScrollbar().setAutoHide(true);
        frmGuide.getContentContainer().add(utfContents);

        final UIButton ubtnClose = new UIButton(this, "Close");
        ubtnClose.setAnchor(Anchor.BOTTOM | Anchor.RIGHT);
        ubtnClose.setPosition(-5, -5);
        ubtnClose.setSize(40, 18);
        ubtnClose.setName("form.guide.button.close");
        ubtnClose.register(this);
        frmGuide.getContentContainer().add(ubtnClose);

        final UIButton ubtnSave = new UIButton(this, "Save");
        ubtnSave.setAnchor(Anchor.BOTTOM | Anchor.RIGHT);
        ubtnSave.setPosition(ubtnClose.getX() - 5 - ubtnClose.getWidth(), ubtnClose.getY());
        ubtnSave.setSize(40, 18);
        ubtnSave.setName("form.guide.button.save");
        ubtnSave.setDisabled(true);
        ubtnSave.register(this);
        frmGuide.getContentContainer().add(ubtnSave);

        addToScreen(frmGuide);

        usPage.select(0);
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) {
        super.keyTyped(keyChar, keyCode);

        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_F5)) {
            int selIndex = -1;

            if (usPage.getSelectedOption() != null) {
                selIndex = usPage.getSelectedOption().getIndex();
            }

            PageUtil.loadAll();

            final Map<Integer, UISelect.Option> options = populate();
            usPage.setOptions(options);

            if (!options.isEmpty()) {
                if (selIndex != -1 && options.get(selIndex) != null) {
                    usPage.select(selIndex);
                } else {
                    usPage.select(0);
                }
            }
        }
    }

    @Subscribe
    public void onUIButtonClickEvent(UIButton.ClickEvent event) {
        if (event.getComponent().getName().equalsIgnoreCase("form.guide.button.close")) {
            close();
        }
    }

    @Subscribe
    public void onUISelectEvent(UISelect.SelectEvent event) {
        if (event.getOption() == null) {
            return;
        }
        if (event.getComponent().getName().equalsIgnoreCase("form.guide.select.page")) {
            ((UIForm) event.getComponent().getParent().getParent()).setTitle("Guide (" + event.getNewValue().getLabel() + ")");
            utfContents.setText(((Page) event.getNewValue().getKey()).getContents());
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
