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
import com.almuradev.guide.content.Page;
import com.google.common.base.Predicate;
import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.malisis.core.client.gui.Anchor;
import net.malisis.core.client.gui.component.decoration.UILabel;
import net.malisis.core.client.gui.component.decoration.UITooltip;
import net.malisis.core.client.gui.component.interaction.UIButton;
import net.malisis.core.client.gui.component.interaction.UITextField;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@SideOnly(Side.CLIENT)
public class ModifyPageGui extends SimpleGui {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    private Page page;
    private UIButton buttonSave, buttonClose;
    private UILabel labelIndex, labelTitle, labelCreated, labelAuthor, labelLastModified, labelLastContributor;
    private UITextField textFieldIndex, textFieldTitle, textFieldCreated, textFieldAuthor, textFieldLastModified,
            textFieldLastContributor;

    public ModifyPageGui(ViewPagesGui parent, Page page) {
        super(parent);
        this.page = page;
    }

    @Override
    public void construct() {
        final int textFieldTopPadding = 1;
        final int padding = 4;

        final UIForm form = new UIForm(this, 150, 166, "Guide - " + page.getName());
        form.setAnchor(Anchor.CENTER | Anchor.MIDDLE);
        form.setName("form.guide.modify");
        form.setColor(ViewPagesGui.CONTROL.getGuiColorCode());
        form.setBackgroundAlpha(255);

        final boolean hasPermission = ClientProxy.getPermissions().hasPermission("save." + page.getIdentifier());

        labelIndex = new UILabel(this, "Index");
        labelIndex.setAnchor(Anchor.TOP | Anchor.LEFT);
        labelIndex.setPosition(padding, padding);
        labelIndex.getFontRenderOptions().shadow = false;
        labelIndex.setVisible(hasPermission);

        textFieldIndex = new UITextField(this, "" + page.getIndex());
        textFieldIndex.setAnchor(Anchor.TOP | Anchor.LEFT);
        textFieldIndex.setPosition(padding, getPaddedY(labelIndex, textFieldTopPadding));
        textFieldIndex.setSize(form.getWidth() - 8, 0);
        textFieldIndex.setVisible(hasPermission);
        textFieldIndex.getFontRenderOptions().shadow = false;
        if (hasPermission) {
            textFieldIndex.setTooltip(new UITooltip(this, "Value must be equal to or greater than 0.", 20));
        }
        textFieldIndex.setValidator(new IntegerFilterPredicate());
        textFieldIndex.setFocused(true);

        labelTitle = new UILabel(this, "Title");
        labelTitle.setAnchor(Anchor.TOP | Anchor.LEFT);
        labelTitle.setPosition(padding, textFieldIndex.isVisible() ? getPaddedY(textFieldIndex, padding) : padding);
        labelTitle.getFontRenderOptions().shadow = false;

        textFieldTitle = new UITextField(this, page.getName());
        textFieldTitle.setAnchor(Anchor.TOP | Anchor.LEFT);
        textFieldTitle.setPosition(padding, getPaddedY(labelTitle, textFieldTopPadding));
        textFieldTitle.setSize(form.getWidth() - (padding * 2), 0);
        textFieldTitle.getFontRenderOptions().shadow = false;
        if (hasPermission) {
            textFieldTitle.setTooltip(new UITooltip(this, "Name must not be greater than 100 characters.", 20));
        }
        textFieldTitle.setEditable(hasPermission);
        textFieldTitle.setValidator(new StringLengthPredicate(1, 100));

        labelCreated = new UILabel(this, "Created");
        labelCreated.setAnchor(Anchor.TOP | Anchor.LEFT);
        labelCreated.setPosition(padding, getPaddedY(textFieldTitle, padding));
        labelCreated.getFontRenderOptions().shadow = false;

        textFieldCreated = new UITextField(this, dateFormat.format(page.getCreated()));
        textFieldCreated.setAnchor(Anchor.TOP | Anchor.LEFT);
        textFieldCreated.setPosition(padding, getPaddedY(labelCreated, textFieldTopPadding));
        textFieldCreated.setSize(form.getWidth() - (padding * 2), 0);
        textFieldCreated.setEditable(false);
        textFieldCreated.getFontRenderOptions().shadow = false;

        labelAuthor = new UILabel(this, "Author");
        labelAuthor.setAnchor(Anchor.TOP | Anchor.LEFT);
        labelAuthor.setPosition(padding, getPaddedY(textFieldCreated, padding));
        labelAuthor.getFontRenderOptions().shadow = false;

        textFieldAuthor = new UITextField(this, page.getAuthor());
        textFieldAuthor.setAnchor(Anchor.TOP | Anchor.LEFT);
        textFieldAuthor.setPosition(padding, getPaddedY(labelAuthor, textFieldTopPadding));
        textFieldAuthor.setSize(form.getWidth() - (padding * 2), 0);
        textFieldAuthor.setEditable(false);
        textFieldAuthor.getFontRenderOptions().shadow = false;

        labelLastModified = new UILabel(this, "Last Modified");
        labelLastModified.setAnchor(Anchor.TOP | Anchor.LEFT);
        labelLastModified.setPosition(padding, getPaddedY(textFieldAuthor, padding));
        labelLastModified.getFontRenderOptions().shadow = false;

        textFieldLastModified = new UITextField(this, dateFormat.format(page.getLastModified()));
        textFieldLastModified.setAnchor(Anchor.TOP | Anchor.LEFT);
        textFieldLastModified.setPosition(padding, getPaddedY(labelLastModified, textFieldTopPadding));
        textFieldLastModified.setEditable(false);
        textFieldLastModified.setSize(form.getWidth() - (padding * 2), 0);
        textFieldLastModified.getFontRenderOptions().shadow = false;

        labelLastContributor = new UILabel(this, "Last Contributor");
        labelLastContributor.setAnchor(Anchor.TOP | Anchor.LEFT);
        labelLastContributor.setPosition(padding, getPaddedY(textFieldLastModified, padding));
        labelLastContributor.getFontRenderOptions().shadow = false;

        textFieldLastContributor = new UITextField(this, page.getLastContributor());
        textFieldLastContributor.setAnchor(Anchor.TOP | Anchor.LEFT);
        textFieldLastContributor.setPosition(padding, getPaddedY(labelLastContributor, textFieldTopPadding));
        textFieldLastContributor.setSize(form.getWidth() - (padding * 2), 0);
        textFieldLastContributor.setEditable(false);
        textFieldLastContributor.getFontRenderOptions().shadow = false;

        buttonClose = new UIButton(this, "Close");
        buttonClose.setAnchor(Anchor.BOTTOM | Anchor.RIGHT);
        buttonClose.setPosition(-padding, -padding);
        buttonClose.setSize(0, 15);
        buttonClose.setName("form.guide.modify.button.close");
        buttonClose.getFontRenderOptions().shadow = false;
        buttonClose.getHoveredFontRendererOptions().shadow = false;
        buttonClose.register(this);

        buttonSave = new UIButton(this, "Save");
        buttonSave.setAnchor(Anchor.BOTTOM | Anchor.RIGHT);
        buttonSave.setPosition(getPaddedX(buttonClose, 2, Anchor.RIGHT), -padding);
        buttonSave.setSize(0, 10);
        buttonSave.setName("form.guide.modify.button.save");
        buttonSave.setVisible(hasPermission);
        buttonSave.getFontRenderOptions().shadow = false;
        buttonSave.getHoveredFontRendererOptions().shadow = false;
        buttonSave.register(this);

        if (labelIndex.isVisible()) {
            form.setHeight(192);
        }

        populate();

        form.getContentContainer().add(labelIndex, textFieldIndex, labelTitle, textFieldTitle, labelCreated,
                textFieldCreated, labelAuthor, textFieldAuthor, labelLastModified, textFieldLastModified, labelLastContributor,
                textFieldLastContributor, buttonSave, buttonClose);

        addToScreen(form);
    }

    private void populate() {
        textFieldIndex.setText("" + page.getIndex());
        textFieldTitle.setText(page.getName());
        textFieldCreated.setText(dateFormat.format(page.getCreated()));
        textFieldAuthor.setText(page.getAuthor());
        textFieldLastModified.setText(dateFormat.format(page.getLastModified()));
        textFieldLastContributor.setText(page.getLastContributor());
    }

    @Subscribe
    public void onUIButtonClickEvent(UIButton.ClickEvent event) throws ParseException {
        switch (event.getComponent().getName()) {
            case "form.guide.modify.button.save":
                if (ClientProxy.getPermissions().hasPermission("save." + page.getIdentifier())) {
                    if (textFieldIndex.getText().isEmpty() || textFieldTitle.getText().isEmpty()) {
                        break;
                    }
                    Guide.NETWORK_FORGE.sendToServer(
                            new C00PageInformation(page.getIdentifier(), Integer.parseInt(textFieldIndex.getText()),
                                    textFieldTitle.getText(), page.getContents()));
                }
            case "form.guide.modify.button.close":
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
