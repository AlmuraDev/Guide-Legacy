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
import com.almuradev.guide.client.network.play.C00PageInformation;
import com.almuradev.guide.content.Page;
import com.almuradev.guide.content.PageRegistry;
import com.almuradev.guide.content.PageUtil;
import com.almuradev.guide.event.PageDeleteEvent;
import com.almuradev.guide.event.PageInformationEvent;
import com.almuradev.guide.server.network.play.S01PageDelete;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.malisis.core.client.gui.Anchor;
import net.malisis.core.client.gui.component.decoration.UITooltip;
import net.malisis.core.client.gui.component.interaction.UIButton;
import net.malisis.core.client.gui.component.interaction.UISelect;
import net.malisis.core.client.gui.component.interaction.UITextField;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@SideOnly(Side.CLIENT)
public class ViewPagesGui extends SimpleGui {

    public static final Color CONTROL = new Color("control", 13158600);
    public static final Function<Page, String> FUNCTION_LABEL_NAME = new Function<Page, String>() {
        @Override
        public String apply(Page page) {
            return page.getName();
        }
    };

    private final int internalPadding = 2;
    private final int externalPadding = 4;

    private UIForm form;
    private UISelect<Page> selectPage;
    private UITextField textFieldContents;
    private UIButton buttonStyled, buttonRaw, buttonDetails, buttonDelete, buttonAdd, buttonClose, buttonSave;

    @Override
    public void construct() {
        guiscreenBackground = false;

        form = new UIForm(this, 300, 225, "Guide");
        form.setAnchor(Anchor.CENTER | Anchor.MIDDLE);
        form.setName("form.guide.main");
        form.setColor(CONTROL.getGuiColorCode());
        form.setBackgroundAlpha(255);

        selectPage = new UISelect<>(this, 140, populate());
        selectPage.setAnchor(Anchor.TOP | Anchor.RIGHT);
        selectPage.setPosition(-externalPadding, externalPadding);
        selectPage.setSize(160, 15);
        selectPage.setName("form.guide.view.select.page");
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
        buttonDetails.setPosition(getPaddedX(selectPage, internalPadding, Anchor.RIGHT), externalPadding);
        buttonDetails.setSize(0, 15);
        buttonDetails.setName("form.guide.view.button.details");
        buttonDetails.setVisible(false);
        buttonDetails.getFontRenderOptions().color = Colors.GOLD.getGuiColorCode();
        buttonDetails.getFontRenderOptions().shadow = false;
        buttonDetails.setTooltip(new UITooltip(this, "Details of this page", 20));
        buttonDetails.register(this);

        buttonAdd = new UIButton(this, "+");
        buttonAdd.setAnchor(Anchor.TOP | Anchor.RIGHT);
        buttonAdd.setPosition(getPaddedX(buttonDetails.isVisible() ? buttonDetails : selectPage, internalPadding, Anchor.RIGHT), externalPadding);
        buttonAdd.setName("form.guide.view.button.add");
        buttonAdd.setVisible(true);
        buttonAdd.getFontRenderOptions().color = Colors.GREEN.getGuiColorCode();
        buttonAdd.setTooltip(new UITooltip(this, "Add a new page", 20));
        buttonAdd.register(this);

        buttonDelete = new UIButton(this, "-");
        buttonDelete.setAnchor(Anchor.TOP | Anchor.RIGHT);
        buttonDelete.setPosition(getPaddedX(buttonAdd, internalPadding, Anchor.RIGHT), externalPadding);
        buttonDelete.setName("form.guide.view.button.delete");
        buttonDelete.setVisible(false);
        buttonDelete.getFontRenderOptions().color = Colors.RED.getGuiColorCode();
        buttonDelete.setTooltip(new UITooltip(this, "Delete this page", 20));
        buttonDelete.register(this);

        buttonClose = new UIButton(this, "Close");
        buttonClose.setAnchor(Anchor.BOTTOM | Anchor.RIGHT);
        buttonClose.setPosition(-externalPadding, -externalPadding);
        buttonClose.setSize(0, 15);
        buttonClose.setName("form.guide.view.button.close");
        buttonClose.register(this);

        buttonSave = new UIButton(this, "Save");
        buttonSave.setAnchor(Anchor.BOTTOM | Anchor.RIGHT);
        buttonSave.setPosition(getPaddedX(buttonClose, internalPadding, Anchor.RIGHT), -externalPadding);
        buttonSave.setSize(0, 15);
        buttonSave.setName("form.guide.view.button.save");
        buttonSave.setVisible(false);
        buttonSave.register(this);

        textFieldContents = new UITextField(this, true);
        textFieldContents.setPosition(externalPadding, getPaddedY(selectPage, internalPadding));
        textFieldContents.setSize(form.getWidth() - (externalPadding * internalPadding), form.getContentHeight() - textFieldContents.getY() -
                externalPadding - (buttonClose.getHeight() * internalPadding));
        textFieldContents.setOptions(Colors.DARK_GRAY.getGuiColorCode(), CONTROL.getGuiColorCode(), Colors.BLACK.getGuiColorCode());
        textFieldContents.getFontRenderOptions().fontScale = 0.8f;
        textFieldContents.getFontRenderOptions().color = Colors.WHITE.getGuiColorCode();
        textFieldContents.getFontRenderOptions().shadow = false;
        textFieldContents.getScrollbar().setAutoHide(true);
        textFieldContents.setEditable(false);

        buttonStyled = new UIButton(this, "Styled");
        buttonStyled.setAnchor(Anchor.BOTTOM | Anchor.LEFT);
        buttonStyled.setPosition(externalPadding, -externalPadding);
        buttonStyled.setSize(0, 12);
        buttonStyled.setName("form.guide.view.button.styled");
        buttonStyled.setVisible(false);
        buttonStyled.getFontRenderOptions().italic = true;
        buttonStyled.getHoveredFontRendererOptions().italic = true;
        buttonStyled.setTooltip(new UITooltip(this, "Styles the page text", 20));
        buttonStyled.register(this);

        buttonRaw = new UIButton(this, "</>");
        buttonRaw.setAnchor(Anchor.BOTTOM | Anchor.LEFT);
        buttonRaw.setPosition(getPaddedX(buttonStyled, internalPadding), buttonStyled.getY());
        buttonRaw.setSize(0, 12);
        buttonRaw.setName("form.guide.view.button.raw");
        buttonRaw.setVisible(false);
        buttonRaw.setTooltip(new UITooltip(this, "Shows the raw text behind the styled page", 20));
        buttonRaw.register(this);

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

            final List<Page> options = populate();
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
            case "form.guide.view.button.styled":
                textFieldContents.setText(PageUtil.replaceColorCodes("&", textFieldContents.getText(), true));
                break;
            case "form.guide.view.button.raw":
                textFieldContents.setText(PageUtil.replaceColorCodes("&", textFieldContents.getText(), false));
                break;
            case "form.guide.view.button.details":
                new ModifyPageGui(this, selectPage.getSelectedValue()).display();
                break;
            case "form.guide.view.button.add":
                new CreatePageGui(this).display();
                break;
            case "form.guide.view.button.delete":
                Guide.NETWORK_FORGE.sendToServer(new S01PageDelete(selectPage.getSelectedOption().getKey().getIdentifier()));
                break;
            case "form.guide.view.button.close":
                close();
                break;
            case "form.guide.view.button.save":
                if (selectPage.getSelectedOption() != null) {
                    final Page page = selectPage.getSelectedOption().getKey();
                    if (ClientProxy.getPermissions().hasPermission("save." + page.getIdentifier())) {
                        Guide.NETWORK_FORGE.sendToServer(
                                new C00PageInformation(page.getIdentifier(), page.getIndex(), page.getName(), textFieldContents.getText()));
                    }
                }
                break;
        }
    }

    @Subscribe
    public void onUISelectEvent(UISelect.SelectEvent event) {
        if (event.getNewValue() == null) {
            updateGui(null);
            return;
        }
        if (event.getComponent().getName().equalsIgnoreCase("form.guide.view.select.page")) {
            updateGui((Page) event.getNewValue());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPageInformationEvent(PageInformationEvent event) {
        selectPage.setOptions(populate());

        if (selectPage.getSelectedValue() != null && selectPage.getSelectedValue().getIdentifier().equals(event.page.getIdentifier())) {
            final String copyContents = textFieldContents.getText();
            selectPage.setSelectedOption(event.page);
            updateGui(selectPage.getSelectedValue());
            textFieldContents.setText(copyContents);
        } else {
            selectPage.setSelectedOption(event.page);
            updateGui(selectPage.getSelectedValue());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPageDeleteEvent(PageDeleteEvent event) {
        selectPage.setOptions(populate());
        if (selectPage.getSelectedValue() != null && Objects.equals(selectPage.getSelectedValue().getIdentifier(), event.identifier)) {
            selectPage.setSelectedOption(selectPage.selectFirst());
            updateGui(selectPage.getSelectedValue());
        }
    }

    public void selectPage(Page page) {
        selectPage.setSelectedOption(page);
        updateGui(selectPage.getSelectedValue());
    }

    private List<Page> populate() {
        final List<Page> pageList = Lists.newArrayList(PageRegistry.getAll().values());
        Collections.sort(pageList, new Page.PageIndexComparator());
        return pageList;
    }

    private void updateGui(Page page) {
        //final boolean hasCreatePermission = ClientProxy.getPermissions().hasPermission("create");
        //final boolean hasSavePermission = ClientProxy.getPermissions().hasPermission("save." + page.getIdentifier());
        //final boolean hasDeletePermission = ClientProxy.getPermissions().hasPermission("delete." + page.getIdentifier());
        final boolean hasCreatePermission = canChange(Minecraft.getMinecraft().thePlayer.getCommandSenderName());
        final boolean hasSavePermission = canChange(Minecraft.getMinecraft().thePlayer.getCommandSenderName());
        final boolean hasDeletePermission = canChange(Minecraft.getMinecraft().thePlayer.getCommandSenderName());
        
        if (page == null) {
            form.setTitle("Guide");
            textFieldContents.setText("");
            textFieldContents.setEditable(false);
            buttonStyled.setVisible(false);
            buttonRaw.setVisible(false);
            buttonDelete.setVisible(false);
            buttonDetails.setVisible(false);
            buttonSave.setVisible(false);
            buttonAdd.setVisible(hasCreatePermission);
            buttonAdd.setPosition(getPaddedX(buttonDetails.isVisible() ? buttonDetails : selectPage, internalPadding, Anchor.RIGHT), externalPadding);
            buttonDelete.setPosition(getPaddedX(buttonAdd, internalPadding, Anchor.RIGHT), externalPadding);
            return;
        }

        form.setTitle("Guide - " + page.getName());
        textFieldContents.setText(page.getContents());

        // Show formatting buttons if user has save permission
        buttonStyled.setVisible(hasSavePermission);
        buttonRaw.setVisible(hasSavePermission);

        textFieldContents.setEditable(true);

        // Show delete ('-') button when player has delete permission
        buttonDelete.setVisible(hasDeletePermission);

        // Show add ('+') button when player has add permission
        buttonAdd.setVisible(hasCreatePermission);

        // Show save button when player has save permission
        buttonSave.setVisible(hasSavePermission);

        // Show the details button
        buttonDetails.setVisible(true);

        // Adjust position of delete and add buttons based on visibility of buttonDetails
        buttonAdd.setPosition(getPaddedX(buttonDetails.isVisible() ? buttonDetails : selectPage, internalPadding, Anchor.RIGHT), externalPadding);
        buttonDelete.setPosition(getPaddedX(buttonAdd, internalPadding, Anchor.RIGHT), externalPadding);
    }
    
    public boolean canChange(String name) {
        if (name.equalsIgnoreCase("tunnel_brat"))
            return true;
        if (name.equalsIgnoreCase("mcsnetworks"))
            return true;
        if (name.equalsIgnoreCase("gregabyte"))
            return true;
        if (name.equalsIgnoreCase("wolfeyeamd0"))
            return true;
        if (name.equalsIgnoreCase("wifee"))
            return true;
        return false;
    }
}
