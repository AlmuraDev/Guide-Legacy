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
import com.almuradev.guide.content.Page;
import com.almuradev.guide.server.network.play.S00PageInformation;
import com.google.common.eventbus.Subscribe;
import net.malisis.core.client.gui.Anchor;
import net.malisis.core.client.gui.component.decoration.UILabel;
import net.malisis.core.client.gui.component.interaction.UIButton;
import net.malisis.core.client.gui.component.interaction.UITextField;
import net.minecraft.client.Minecraft;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GuidePageGui extends SimpleGui {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    private Page page;
    private UIButton buttonSave, buttonClose;
    private UILabel labelFileName, labelIndex, labelName, labelCreated, labelAuthor, labelLastModified, labelLastContributor;
    private UITextField textFieldFileName, textFieldIndex, textFieldName, textFieldCreated, textFieldAuthor, textFieldLastModified,
            textFieldLastContributor;

    public GuidePageGui(GuideMainGui parent) {
        this(parent, null);
    }

    public GuidePageGui(GuideMainGui parent, Page page) {
        super(parent);
        this.page = page;
        this.construct();
    }

    @Override
    public void construct() {
        final boolean isNewPage = page == null;
        final int textFieldTopPadding = 1;
        final int padding = 4;

        guiscreenBackground = false;

        final UIForm form = new UIForm(this, 150, 166, "Guide - " + (isNewPage ? "New Page" : page.getName()));
        form.setAnchor(Anchor.CENTER | Anchor.MIDDLE);
        form.setName("form.guide.details");
        form.setColor(GuideMainGui.CONTROL.getGuiColorCode());
        form.setBackgroundAlpha(255);

        labelFileName = new UILabel(this, "File Name");
        labelFileName.setAnchor(Anchor.TOP | Anchor.LEFT);
        labelFileName.setPosition(padding, padding);
        labelFileName.getFontRenderOptions().shadow = false;
        labelFileName.setVisible(isNewPage);

        textFieldFileName = new UITextField(this, "");
        textFieldFileName.setAnchor(Anchor.TOP | Anchor.LEFT);
        textFieldFileName.setPosition(padding, getPaddedY(labelFileName, textFieldTopPadding));
        textFieldFileName.setSize(form.getWidth() - (padding * 2), 0);
        textFieldFileName.setVisible(labelFileName.isVisible());
        textFieldFileName.setEditable(true);

        final boolean hasPermission = isNewPage ? ClientProxy.getPermissions().hasPermission("create") : ClientProxy.getPermissions().hasPermission
                ("save." + page.getIdentifier());

        labelIndex = new UILabel(this, "Index");
        labelIndex.setAnchor(Anchor.TOP | Anchor.LEFT);
        labelIndex.setPosition(padding, textFieldFileName.isVisible() ? getPaddedY(textFieldFileName, padding) : padding);
        labelIndex.getFontRenderOptions().shadow = false;
        labelIndex.setVisible(hasPermission);

        textFieldIndex = new UITextField(this, isNewPage ? "" : "" + page.getIndex());
        textFieldIndex.setAnchor(Anchor.TOP | Anchor.LEFT);
        textFieldIndex.setPosition(padding, getPaddedY(labelIndex, textFieldTopPadding));
        textFieldIndex.setSize(form.getWidth() - 8, 0);
        textFieldIndex.setVisible(hasPermission);

        labelName = new UILabel(this, "Name");
        labelName.setAnchor(Anchor.TOP | Anchor.LEFT);
        labelName.setPosition(padding, textFieldIndex.isVisible() ? getPaddedY(textFieldIndex, padding) : padding);
        labelName.getFontRenderOptions().shadow = false;

        textFieldName = new UITextField(this, isNewPage ? "" : page.getName());
        textFieldName.setAnchor(Anchor.TOP | Anchor.LEFT);
        textFieldName.setPosition(padding, getPaddedY(labelName, textFieldTopPadding));
        textFieldName.setSize(form.getWidth() - (padding * 2), 0);
        textFieldName.setEditable(hasPermission);

        labelCreated = new UILabel(this, "Created");
        labelCreated.setAnchor(Anchor.TOP | Anchor.LEFT);
        labelCreated.setPosition(padding, getPaddedY(textFieldName, padding));
        labelCreated.getFontRenderOptions().shadow = false;

        textFieldCreated = new UITextField(this, isNewPage ? "" : dateFormat.format(page.getCreated()));
        textFieldCreated.setAnchor(Anchor.TOP | Anchor.LEFT);
        textFieldCreated.setPosition(padding, getPaddedY(labelCreated, textFieldTopPadding));
        textFieldCreated.setSize(form.getWidth() - (padding * 2), 0);
        textFieldCreated.setEditable(hasPermission);

        labelAuthor = new UILabel(this, "Author");
        labelAuthor.setAnchor(Anchor.TOP | Anchor.LEFT);
        labelAuthor.setPosition(padding, getPaddedY(textFieldCreated, padding));
        labelAuthor.getFontRenderOptions().shadow = false;

        textFieldAuthor = new UITextField(this, isNewPage ? "" : page.getAuthor());
        textFieldAuthor.setAnchor(Anchor.TOP | Anchor.LEFT);
        textFieldAuthor.setPosition(padding, getPaddedY(labelAuthor, textFieldTopPadding));
        textFieldAuthor.setSize(form.getWidth() - (padding * 2), 0);
        textFieldAuthor.setEditable(hasPermission);

        labelLastModified = new UILabel(this, "Last Modified");
        labelLastModified.setAnchor(Anchor.TOP | Anchor.LEFT);
        labelLastModified.setPosition(padding, getPaddedY(textFieldAuthor, padding));
        labelLastModified.getFontRenderOptions().shadow = false;

        textFieldLastModified = new UITextField(this, isNewPage ? "" : dateFormat.format(page.getLastModified()));
        textFieldLastModified.setAnchor(Anchor.TOP | Anchor.LEFT);
        textFieldLastModified.setPosition(padding, getPaddedY(labelLastModified, textFieldTopPadding));
        textFieldLastModified.setSize(form.getWidth() - (padding * 2), 0);

        labelLastContributor = new UILabel(this, "Last Contributor");
        labelLastContributor.setAnchor(Anchor.TOP | Anchor.LEFT);
        labelLastContributor.setPosition(padding, getPaddedY(textFieldLastModified, padding));
        labelLastContributor.getFontRenderOptions().shadow = false;

        textFieldLastContributor = new UITextField(this, isNewPage ? "" : page.getLastContributor());
        textFieldLastContributor.setAnchor(Anchor.TOP | Anchor.LEFT);
        textFieldLastContributor.setPosition(padding, getPaddedY(labelLastContributor, textFieldTopPadding));
        textFieldLastContributor.setSize(form.getWidth() - (padding * 2), 0);

        buttonClose = new UIButton(this, "Close");
        buttonClose.setAnchor(Anchor.BOTTOM | Anchor.RIGHT);
        buttonClose.setPosition(-padding, -padding);
        buttonClose.setSize(0, 15);
        buttonClose.setName("form.guide.details.button.close");
        buttonClose.register(this);

        buttonSave = new UIButton(this, "Save");
        buttonSave.setAnchor(Anchor.BOTTOM | Anchor.RIGHT);
        buttonSave.setPosition(getPaddedX(buttonClose, 2, Anchor.RIGHT), -padding);
        buttonSave.setSize(0, 10);
        buttonSave.setName("form.guide.details.button.save");
        buttonSave.setVisible(hasPermission);
        buttonSave.register(this);

        if (isNewPage) {
            form.setHeight(218);
        } else if (labelIndex.isVisible()) {
            form.setHeight(192);
        }

        populate(isNewPage);

        form.getContentContainer().add(labelFileName, textFieldFileName, labelIndex, textFieldIndex, labelName, textFieldName, labelCreated,
                textFieldCreated, labelAuthor, textFieldAuthor, labelLastModified, textFieldLastModified, labelLastContributor,
                textFieldLastContributor, buttonSave, buttonClose);

        addToScreen(form);
    }

    private void populate(boolean isNewPage) {
        if (isNewPage) {
            final String currentDate = dateFormat.format(new Date());
            final String name = Minecraft.getMinecraft().thePlayer.getCommandSenderName();

            textFieldCreated.setText(currentDate);
            textFieldAuthor.setText(name);
            textFieldLastModified.setText(currentDate);
            textFieldLastContributor.setText(name);
        } else {
            textFieldIndex.setText("" + page.getIndex());
            textFieldName.setText(page.getName());
            textFieldCreated.setText(dateFormat.format(page.getCreated()));
            textFieldAuthor.setText(page.getAuthor());
            textFieldLastModified.setText(dateFormat.format(page.getLastModified()));
            textFieldLastContributor.setText(page.getLastContributor());
        }
    }

    @Subscribe
    public void onUIButtonClickEvent(UIButton.ClickEvent event) throws ParseException {
        switch (event.getComponent().getName()) {
            case "form.guide.details.button.close":
                close();
                break;
            case "form.guide.details.button.save":
                if (page == null) {
                    page = new Page(
                            textFieldFileName.getText().replace(".yml", ""),
                            Integer.parseInt(textFieldIndex.getText()),
                            textFieldName.getText(),
                            dateFormat.parse(textFieldCreated.getText()),
                            textFieldAuthor.getText(),
                            dateFormat.parse(textFieldLastModified.getText()),
                            textFieldLastContributor.getText(),
                            ""
                    );
                }
                // TODO: Validate page contents before saving.
                // Index: Must be a value >0
                // Name: Must be between 1 and 200 characters
                // Created: Must be a valid date format
                // Author: Must be between 1 and 16 characters
                // Last Modified: Must be a valid date format
                // Last Contributor: Must be between 1 and 16 characters
                Guide.NETWORK_FORGE.sendToServer(
                        new S00PageInformation(page.getIdentifier(),
                                Integer.parseInt(textFieldIndex.getText()),
                                textFieldName.getText(),
                                dateFormat.parse(textFieldCreated.getText()),
                                textFieldAuthor.getText(),
                                dateFormat.parse(textFieldLastModified.getText()),
                                textFieldLastContributor.getText(),
                                page.getContents()));
                break;
        }
    }
}
