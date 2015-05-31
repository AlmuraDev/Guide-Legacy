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
import com.almuradev.almurasdk.util.Color;
import com.almuradev.almurasdk.util.Colors;
import com.almuradev.guide.Guide;
import com.almuradev.guide.client.ClientProxy;
import com.almuradev.guide.content.Page;
import com.almuradev.guide.content.PageRegistry;
import com.almuradev.guide.content.PageUtil;
import com.almuradev.guide.event.PageInformationEvent;
import com.almuradev.guide.server.network.play.S00PageInformation;
import com.google.common.base.Function;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.malisis.core.client.gui.Anchor;
import net.malisis.core.client.gui.component.interaction.UIButton;
import net.malisis.core.client.gui.component.interaction.UISelect;
import net.malisis.core.client.gui.component.interaction.UITextField;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

import java.util.Set;

public class GuideViewPagesGui extends SimpleGui {

    public static final Color CONTROL = new Color("control", 13158600);
    public static final Function<Page, String> FUNCTION_LABEL_NAME = new Function<Page, String>() {
        @Override
        public String apply(Page page) {
            return page.getName();
        }
    };

    private UISelect<Page> selectPage;
    private UITextField textFieldContents;
    private UIButton buttonStyled, buttonRaw, buttonDetails, buttonDelete, buttonAdd, buttonClose, buttonSave;

    public GuideViewPagesGui() {
        construct();
    }

    @Override
    public void construct() {
        final int internalPadding = 2;
        final int externalPadding = 4;

        guiscreenBackground = false;

        final UIForm form = new UIForm(this, 300, 225, "Guide");
        form.setAnchor(Anchor.CENTER | Anchor.MIDDLE);
        form.setName("form.guide.main");
        form.setColor(CONTROL.getGuiColorCode());
        form.setBackgroundAlpha(255);

        buttonStyled = new UIButton(this, "Styled");
        buttonStyled.setAnchor(Anchor.TOP | Anchor.LEFT);
        buttonStyled.setPosition(externalPadding, externalPadding);
        buttonStyled.setSize(0, 12);
        buttonStyled.setName("form.guide.main.button.styled");
        buttonStyled.getFontRenderOptions().italic = true;
        buttonStyled.getHoveredFontRendererOptions().italic = true;
        buttonStyled.setTooltip("Styles the page text");
        buttonStyled.register(this);

        buttonRaw = new UIButton(this, "</>");
        buttonRaw.setAnchor(Anchor.TOP | Anchor.LEFT);
        buttonRaw.setPosition(getPaddedX(buttonStyled, 2), buttonStyled.getY());
        buttonRaw.setSize(0, 12);
        buttonRaw.setName("form.guide.main.button.raw");
        buttonRaw.setTooltip("Shows the raw text behind the styled page");
        buttonRaw.register(this);

        selectPage = new UISelect<>(this, 140, populate());
        selectPage.setAnchor(Anchor.TOP | Anchor.RIGHT);
        selectPage.setPosition(-externalPadding, externalPadding);
        selectPage.setSize(130, 15);
        selectPage.setName("form.guide.main.select.page");
        selectPage.setLabelFunction(FUNCTION_LABEL_NAME);
        selectPage.setColors(selectPage.getBgColor(), Colors.DARK_GRAY.getGuiColorCode());
        selectPage.getFontRenderOptions().color = Colors.WHITE.getGuiColorCode();
        selectPage.getFontRenderOptions().shadow = false;
        selectPage.getHoveredFontRendererOptions().color = Colors.GRAY.getGuiColorCode();
        selectPage.getHoveredFontRendererOptions().shadow = false;
        selectPage.getSelectedFontRendererOptions().color = Colors.YELLOW.getGuiColorCode();
        selectPage.getSelectedFontRendererOptions().shadow = false;
        selectPage.getSelectedFontRendererOptions().bold = true;
        selectPage.register(this);

        buttonDetails = new UIButton(this, "?");
        buttonDetails.setAnchor(Anchor.TOP | Anchor.RIGHT);
        buttonDetails.setPosition(getPaddedX(selectPage, 2, Anchor.RIGHT), externalPadding);
        buttonDetails.setSize(0, 15);
        buttonDetails.setName("form.guide.main.button.details");
        buttonDetails.getFontRenderOptions().color = Colors.GOLD.getGuiColorCode();
        buttonDetails.getFontRenderOptions().shadow = false;
        buttonDetails.setTooltip("Details of this page");
        buttonDetails.register(this);

        buttonDelete = new UIButton(this, "-");
        buttonDelete.setAnchor(Anchor.BOTTOM | Anchor.LEFT);
        buttonDelete.setPosition(externalPadding, -externalPadding);
        buttonDelete.setName("form.guide.main.button.delete");
        buttonDelete.setVisible(false);
        buttonDelete.getFontRenderOptions().color = Colors.RED.getGuiColorCode();
        buttonDelete.setTooltip("Delete this page");
        buttonDelete.register(this);

        buttonAdd = new UIButton(this, "+");
        buttonAdd.setAnchor(Anchor.BOTTOM | Anchor.LEFT);
        buttonAdd.setPosition(getPaddedX(buttonDelete, 2), buttonDelete.getY());
        buttonAdd.setName("form.guide.main.button.add");
        buttonAdd.setVisible(false);
        buttonAdd.getFontRenderOptions().color = Colors.GREEN.getGuiColorCode();
        buttonAdd.setTooltip("Add a new page");
        buttonAdd.register(this);

        buttonClose = new UIButton(this, "Close");
        buttonClose.setAnchor(Anchor.BOTTOM | Anchor.RIGHT);
        buttonClose.setPosition(-externalPadding, -externalPadding);
        buttonClose.setSize(0, 15);
        buttonClose.setName("form.guide.main.button.close");
        buttonClose.register(this);

        buttonSave = new UIButton(this, "Save");
        buttonSave.setAnchor(Anchor.BOTTOM | Anchor.RIGHT);
        buttonSave.setPosition(getPaddedX(buttonClose, 2, Anchor.RIGHT), -externalPadding);
        buttonSave.setSize(0, 15);
        buttonSave.setName("form.guide.main.button.save");
        buttonSave.setVisible(false);
        buttonSave.register(this);

        textFieldContents = new UITextField(this, true);
        textFieldContents.setPosition(externalPadding, getPaddedY(buttonStyled, 2));
        textFieldContents.setSize(form.getWidth() - (externalPadding * 2), form.getContentHeight() - textFieldContents.getY() -
                externalPadding - (buttonClose.getHeight() * 2));
        textFieldContents.setOptions(Colors.GRAY.getGuiColorCode(), CONTROL.getGuiColorCode(), Colors.BLACK.getGuiColorCode());
        textFieldContents.getFontRenderOptions().color = Colors.WHITE.getGuiColorCode();
        textFieldContents.getFontRenderOptions().shadow = false;
        textFieldContents.getScrollbar().setAutoHide(true);

        form.getContentContainer().add(buttonStyled, buttonRaw, selectPage, textFieldContents, buttonDetails, buttonDelete, buttonAdd,
                buttonSave, buttonClose);

        addToScreen(form);

        selectPage.selectFirst();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onClose() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) {
        super.keyTyped(keyChar, keyCode);

        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_F5)) {
            Page selected = null;

            if (selectPage.getSelectedValue() != null) {
                selected = selectPage.getSelectedValue();
            }

            PageUtil.loadAll();

            final Set<Page> options = populate();
            selectPage.setOptions(options);

            if (!options.isEmpty()) {
                if (selected != null && selectPage.getOption(selected) != null) {
                    selectPage.select(selected);
                } else {
                    selectPage.selectFirst();
                }
            }
        }
    }

    @Subscribe
    public void onUIButtonClickEvent(UIButton.ClickEvent event) {
        switch (event.getComponent().getName().toLowerCase()) {
            case "form.guide.main.button.styled":
                textFieldContents.setText(PageUtil.replaceColorCodes("&", textFieldContents.getText(), true));
                break;
            case "form.guide.main.button.raw":
                textFieldContents.setText(PageUtil.replaceColorCodes("&", textFieldContents.getText(), false));
                break;
            case "form.guide.main.button.details":
                Minecraft.getMinecraft().displayGuiScreen(new GuideModifyPageGui(this, selectPage.getSelectedValue()));
                break;
            case "form.guide.main.button.add":
                Minecraft.getMinecraft().displayGuiScreen(new GuideModifyPageGui(this));
                break;
            case "form.guide.main.button.close":
                close();
                break;
            case "form.guide.main.button.save":
                if (selectPage.getSelectedOption() != null) {
                    final Page page = selectPage.getSelectedOption().getKey();
                    Guide.NETWORK_FORGE.sendToServer(
                            new S00PageInformation(page.getIdentifier(), page.getIndex(), page.getName(), page.getCreated(), page.getAuthor(),
                                    page.getLastModified(), page.getLastContributor(), textFieldContents.getText()));
                }
                break;

        }
    }

    @Subscribe
    public void onUISelectEvent(UISelect.SelectEvent event) {
        if (event.getNewValue() == null) {
            buttonStyled.setVisible(false);
            buttonRaw.setVisible(false);
            buttonDelete.setVisible(false);
            buttonSave.setVisible(false);
            return;
        }
        if (event.getComponent().getName().equalsIgnoreCase("form.guide.main.select.page")) {
            ((UIForm) event.getComponent().getParent().getParent()).setTitle("Guide - " + ((Page) event.getNewValue()).getName());
            textFieldContents.setText(((Page) event.getNewValue()).getContents());

            final boolean hasSavePermission = ClientProxy.getPermissions().hasPermission("save." + ((Page) event.getNewValue()).getIdentifier());
            final boolean hasDeletePermission = ClientProxy.getPermissions().hasPermission("delete." + ((Page) event.getNewValue()).getIdentifier());
            final boolean hasAddPermission = ClientProxy.getPermissions().hasPermission("add");

            // Show formatting buttons if user has save permission
            buttonStyled.setVisible(hasSavePermission);
            buttonRaw.setVisible(hasSavePermission);

            // Show delete ('-') button when player has delete permission
            buttonDelete.setVisible(hasDeletePermission);

            // Show add ('+') button when player has add permission
            buttonAdd.setVisible(hasAddPermission);
            buttonAdd.setPosition(buttonDelete.isVisible() ? getPaddedX(buttonDelete, 2) : 2, buttonAdd.getY());

            // Show save button when player has save permission
            buttonSave.setVisible(hasSavePermission);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPageInformationEvent(PageInformationEvent event) {
        if (selectPage.getSelectedOption() != null) {
            if (selectPage.getSelectedOption().getKey().equals(event.page)) {
                textFieldContents.setText(event.page.getContents());
            }
        }
    }

    private Set<Page> populate() {
        return Sets.newHashSet(PageRegistry.getAll().values());
    }
}
