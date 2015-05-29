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

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class GuidePageGui extends SimpleGui {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    private final Page page;
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
        guiscreenBackground = false;

        final UIForm form = new UIForm(this, 150, 195, "Guide");
        form.setAnchor(Anchor.CENTER | Anchor.MIDDLE);
        form.setName("form.guide.details");
        form.setColor(GuideMainGui.CONTROL.getGuiColorCode());
        form.setBackgroundAlpha(255);
        
        final boolean isNewPage = page == null;

        labelFileName = new UILabel(this, "File Name");
        labelFileName.setAnchor(Anchor.TOP | Anchor.LEFT);
        labelFileName.setPosition(2, 4);
        labelFileName.getFontRenderOptions().shadow = false;
        labelFileName.setVisible(isNewPage);

        textFieldFileName = new UITextField(this, "");
        textFieldFileName.setAnchor(Anchor.TOP | Anchor.LEFT);
        textFieldFileName.setPosition(2, getPaddedY(labelFileName, 1));
        textFieldFileName.setSize(form.getWidth() - 4, 0);
        textFieldFileName.setVisible(labelFileName.isVisible());
        textFieldFileName.setEditable(true);

        final boolean hasPermission = isNewPage ? ClientProxy.getPermissions().hasPermission("create") : ClientProxy.getPermissions().hasPermission
                ("save." + page.getIdentifier());

        labelIndex = new UILabel(this, "Index");
        labelIndex.setAnchor(Anchor.TOP | Anchor.LEFT);
        labelIndex.setPosition(2, textFieldFileName.isVisible() ? getPaddedY(textFieldFileName, 4) : 4);
        labelIndex.getFontRenderOptions().shadow = false;
        labelIndex.setVisible(hasPermission);

        textFieldIndex = new UITextField(this, isNewPage ? "" : "" + page.getIndex());
        textFieldIndex.setAnchor(Anchor.TOP | Anchor.LEFT);
        textFieldIndex.setPosition(2, getPaddedY(labelIndex, 1));
        textFieldIndex.setSize(form.getWidth() - 4, 0);
        textFieldIndex.setVisible(hasPermission);

        labelName = new UILabel(this, "Name");
        labelName.setAnchor(Anchor.TOP | Anchor.LEFT);
        labelName.setPosition(2, textFieldIndex.isVisible() ? getPaddedY(textFieldIndex, 4) : 4);
        labelName.getFontRenderOptions().shadow = false;

        textFieldName = new UITextField(this, isNewPage ? "" : page.getName());
        textFieldName.setAnchor(Anchor.TOP | Anchor.LEFT);
        textFieldName.setPosition(2, getPaddedY(labelName, 1));
        textFieldName.setSize(form.getWidth() - 4, 0);
        textFieldName.setEditable(hasPermission);

        labelCreated = new UILabel(this, "Created");
        labelCreated.setAnchor(Anchor.TOP | Anchor.LEFT);
        labelCreated.setPosition(2, getPaddedY(textFieldName, 4));
        labelCreated.getFontRenderOptions().shadow = false;

        textFieldCreated = new UITextField(this, isNewPage ? "" : dateFormat.format(page.getCreated()));
        textFieldCreated.setAnchor(Anchor.TOP | Anchor.LEFT);
        textFieldCreated.setPosition(2, getPaddedY(labelCreated, 1));
        textFieldCreated.setSize(form.getWidth() - 4, 0);
        textFieldCreated.setEditable(hasPermission);

        labelAuthor = new UILabel(this, "Author");
        labelAuthor.setAnchor(Anchor.TOP | Anchor.LEFT);
        labelAuthor.setPosition(2, getPaddedY(textFieldCreated, 4));
        labelAuthor.getFontRenderOptions().shadow = false;

        textFieldAuthor = new UITextField(this, isNewPage ? "" : page.getAuthor());
        textFieldAuthor.setAnchor(Anchor.TOP | Anchor.LEFT);
        textFieldAuthor.setPosition(2, getPaddedY(labelAuthor, 1));
        textFieldAuthor.setSize(form.getWidth() - 4, 0);
        textFieldAuthor.setEditable(hasPermission);

        labelLastModified = new UILabel(this, "Last Modified");
        labelLastModified.setAnchor(Anchor.TOP | Anchor.LEFT);
        labelLastModified.setPosition(2, getPaddedY(textFieldAuthor, 4));
        labelLastModified.getFontRenderOptions().shadow = false;

        textFieldLastModified = new UITextField(this, isNewPage ? "" : dateFormat.format(page.getLastModified()));
        textFieldLastModified.setAnchor(Anchor.TOP | Anchor.LEFT);
        textFieldLastModified.setPosition(2, getPaddedY(labelLastModified, 1));
        textFieldLastModified.setSize(form.getWidth() - 4, 0);
        textFieldLastModified.setEditable(hasPermission);

        labelLastContributor = new UILabel(this, "Last Contributor");
        labelLastContributor.setAnchor(Anchor.TOP | Anchor.LEFT);
        labelLastContributor.setPosition(2, getPaddedY(textFieldLastModified, 4));
        labelLastContributor.getFontRenderOptions().shadow = false;

        textFieldLastContributor = new UITextField(this, isNewPage ? "" : page.getLastContributor());
        textFieldLastContributor.setAnchor(Anchor.TOP | Anchor.LEFT);
        textFieldLastContributor.setPosition(2, getPaddedY(labelLastContributor, 1));
        textFieldLastContributor.setSize(form.getWidth() - 4, 0);
        textFieldLastContributor.setEditable(hasPermission);

        buttonClose = new UIButton(this, "Close");
        buttonClose.setAnchor(Anchor.BOTTOM | Anchor.RIGHT);
        buttonClose.setPosition(-2, -2);
        buttonClose.setSize(0, 15);
        buttonClose.setName("form.guide.details.button.close");
        buttonClose.register(this);

        buttonSave = new UIButton(this, "Save");
        buttonSave.setAnchor(Anchor.BOTTOM | Anchor.RIGHT);
        buttonSave.setPosition(getPaddedX(buttonClose, 2, Anchor.RIGHT), -2);
        buttonSave.setSize(0, 10);
        buttonSave.setName("form.guide.details.button.save");
        buttonSave.setVisible(hasPermission);
        buttonSave.register(this);

        if (isNewPage) {
            form.setHeight(215);
        } else if (labelIndex.isVisible()) {
            form.setHeight(195);
        }
        

        form.getContentContainer().add(labelFileName, textFieldFileName, labelIndex, textFieldIndex, labelName, textFieldName, labelCreated,
                textFieldCreated, labelAuthor, textFieldAuthor, labelLastModified, textFieldLastModified, labelLastContributor,
                textFieldLastContributor, buttonSave, buttonClose);

        addToScreen(form);
    }

    @Subscribe
    public void onUIButtonClickEvent(UIButton.ClickEvent event) throws ParseException {
        switch (event.getComponent().getName()) {
            case "form.guide.details.button.close":
                close();
                break;
            case "form.guide.details.button.save":
                if (page != null) {
                    Guide.NETWORK_FORGE.sendToServer(
                            new S00PageInformation(Integer.parseInt(textFieldIndex.getText()),
                                    page.getIdentifier(),
                                    textFieldName.getText(),
                                    dateFormat.parse(textFieldCreated.getText()),
                                    textFieldAuthor.getText(),
                                    dateFormat.parse(textFieldLastModified.getText()),
                                    textFieldLastContributor.getText(),
                                    page.getContents()));
                } else {
                    Guide.NETWORK_FORGE.sendToServer(
                            new S00PageInformation(Integer.parseInt(textFieldIndex.getText()),
                                    textFieldFileName.getText(),
                                    textFieldName.getText(),
                                    dateFormat.parse(textFieldCreated.getText()),
                                    textFieldAuthor.getText(),
                                    dateFormat.parse(textFieldLastModified.getText()),
                                    textFieldLastContributor.getText(),
                                    ""));
                }
                break;
        }
    }
}
