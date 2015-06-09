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
package com.almuradev.guide.client.gui;

import com.almuradev.almurasdk.client.gui.SimpleGui;
import com.almuradev.almurasdk.client.gui.components.UIForm;
import com.almuradev.guide.Guide;
import com.almuradev.guide.client.ClientProxy;
import com.almuradev.guide.client.network.play.C00PageInformation;
import com.almuradev.guide.content.PageRegistry;
import com.google.common.base.Predicate;
import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.malisis.core.client.gui.Anchor;
import net.malisis.core.client.gui.component.decoration.UILabel;
import net.malisis.core.client.gui.component.interaction.UIButton;
import net.malisis.core.client.gui.component.interaction.UITextField;

import java.text.ParseException;

@SideOnly(Side.CLIENT)
public class CreatePageGui extends SimpleGui {

    private UIButton buttonSave, buttonCancel;
    private UILabel labelFileName, labelIndex, labelTitle;
    private UITextField textFieldFileName, textFieldIndex, textFieldTitle;

    public CreatePageGui(ViewPagesGui parent) {
        super(parent);
        this.construct();
    }

    @Override
    public void construct() {
        final int textFieldTopPadding = 1;
        final int padding = 4;

        guiscreenBackground = false;

        final UIForm form = new UIForm(this, 150, 114, "Guide - Create Page");
        form.setAnchor(Anchor.CENTER | Anchor.MIDDLE);
        form.setName("form.guide.create");
        form.setColor(ViewPagesGui.CONTROL.getGuiColorCode());
        form.setBackgroundAlpha(255);

        labelFileName = new UILabel(this, "File Name");
        labelFileName.setAnchor(Anchor.TOP | Anchor.LEFT);
        labelFileName.setPosition(padding, padding);
        labelFileName.getFontRenderOptions().shadow = false;

        textFieldFileName = new UITextField(this, "");
        textFieldFileName.setAnchor(Anchor.TOP | Anchor.LEFT);
        textFieldFileName.setPosition(padding, getPaddedY(labelFileName, textFieldTopPadding));
        textFieldFileName.setSize(form.getWidth() - (padding * 2), 0);
        textFieldFileName.setTooltip("File must not exist already.");
        textFieldFileName.setFocused(true);

        labelIndex = new UILabel(this, "Index");
        labelIndex.setAnchor(Anchor.TOP | Anchor.LEFT);
        labelIndex.setPosition(padding, textFieldFileName.isVisible() ? getPaddedY(textFieldFileName, padding) : padding);
        labelIndex.getFontRenderOptions().shadow = false;

        textFieldIndex = new UITextField(this, "" + 0);
        textFieldIndex.setAnchor(Anchor.TOP | Anchor.LEFT);
        textFieldIndex.setPosition(padding, getPaddedY(labelIndex, textFieldTopPadding));
        textFieldIndex.setSize(form.getWidth() - 8, 0);
        textFieldIndex.setTooltip("Value must be equal to or greater than 0.");
        textFieldIndex.setValidator(new IntegerFilterPredicate());

        labelTitle = new UILabel(this, "Title");
        labelTitle.setAnchor(Anchor.TOP | Anchor.LEFT);
        labelTitle.setPosition(padding, textFieldIndex.isVisible() ? getPaddedY(textFieldIndex, padding) : padding);
        labelTitle.getFontRenderOptions().shadow = false;

        textFieldTitle = new UITextField(this, "");
        textFieldTitle.setAnchor(Anchor.TOP | Anchor.LEFT);
        textFieldTitle.setPosition(padding, getPaddedY(labelTitle, textFieldTopPadding));
        textFieldTitle.setSize(form.getWidth() - (padding * 2), 0);
        textFieldTitle.setTooltip("Name must not be greater than 100 characters.");
        textFieldTitle.setValidator(new StringLengthPredicate(1, 100));

        buttonCancel = new UIButton(this, "Cancel");
        buttonCancel.setAnchor(Anchor.BOTTOM | Anchor.RIGHT);
        buttonCancel.setPosition(-padding, -padding);
        buttonCancel.setSize(0, 15);
        buttonCancel.setName("form.guide.create.button.cancel");
        buttonCancel.register(this);

        buttonSave = new UIButton(this, "Save");
        buttonSave.setAnchor(Anchor.BOTTOM | Anchor.RIGHT);
        buttonSave.setPosition(getPaddedX(buttonCancel, 2, Anchor.RIGHT), -padding);
        buttonSave.setSize(0, 10);
        buttonSave.setName("form.guide.create.button.save");
        buttonSave.register(this);

        form.getContentContainer().add(labelFileName, textFieldFileName, labelIndex, textFieldIndex, labelTitle,
                textFieldTitle, buttonSave, buttonCancel);

        addToScreen(form);
    }

    @Subscribe
    public void onUIButtonClickEvent(UIButton.ClickEvent event) throws ParseException {
        switch (event.getComponent().getName()) {
            case "form.guide.create.button.save":
                if (ClientProxy.getPermissions().hasPermission("create")) {
                    textFieldFileName.setText(textFieldFileName.getText().trim());
                    if (textFieldFileName.getText().isEmpty() || textFieldIndex.getText().isEmpty() || textFieldTitle.getText().isEmpty()) {
                        break;
                    }
                    if (PageRegistry.getPage(textFieldFileName.getText()).isPresent()) {
                        break;
                    }
                    Guide.NETWORK_FORGE.sendToServer(
                            new C00PageInformation(textFieldFileName.getText().replace(".yml", ""),
                                    Integer.parseInt(textFieldIndex.getText()), textFieldTitle.getText(), ""));
                }
                close();
            case "form.guide.create.button.cancel":
                close();
                break;
        }
    }

    private final class IntegerFilterPredicate implements Predicate<String> {

        @Override
        public boolean apply(String input) {
            final int rawIndex;
            try {
                rawIndex = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                return false;
            }
            return rawIndex >= 0 && rawIndex < Integer.MAX_VALUE;
        }
    }

    private final class StringLengthPredicate implements Predicate<String> {

        private final int min, max;

        private StringLengthPredicate(int min, int max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public boolean apply(String input) {
            return input.length() >= min && input.length() <= max;
        }
    }
}
