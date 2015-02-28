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
import com.almuradev.almurasdk.util.Colors;
import com.almuradev.guide.Guide;
import com.almuradev.guide.client.ClientProxy;
import com.almuradev.guide.content.Page;
import com.almuradev.guide.content.PageRegistry;
import com.almuradev.guide.content.PageUtil;
import com.almuradev.guide.event.PageInformationEvent;
import com.almuradev.guide.server.network.play.S00PageInformation;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.malisis.core.client.gui.Anchor;
import net.malisis.core.client.gui.component.UIComponent;
import net.malisis.core.client.gui.component.container.UIContainer;
import net.malisis.core.client.gui.component.container.UIPanel;
import net.malisis.core.client.gui.component.container.UITabGroup;
import net.malisis.core.client.gui.component.interaction.UIButton;
import net.malisis.core.client.gui.component.interaction.UISelect;
import net.malisis.core.client.gui.component.interaction.UITab;
import net.malisis.core.client.gui.component.interaction.UITextField;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

import java.util.HashMap;
import java.util.Map;

public class GuideGui extends SimpleGui {
    public static final Color CONTROL = new Color(13158600);

    private UISelect usPage;
    private UITextField utfContents;
    private UIButton ubtnFormat, ubtnCode, ubtnDelete, ubtnSave;

    public GuideGui() {
        buildGui();
    }

    @Override
    protected void buildGui() {
        guiscreenBackground = false;

        final UIForm frmGuide = new UIForm(this, 450, 270, "Guide");
        frmGuide.setAnchor(Anchor.CENTER | Anchor.MIDDLE);
        frmGuide.setName("form.guide");
        frmGuide.setColor(CONTROL.getGuiColorCode());
        frmGuide.setBackgroundAlpha(255);

        usPage = new UISelect(this, 140, populate());
        usPage.setAnchor(Anchor.TOP | Anchor.RIGHT);
        usPage.setPosition(-5, 5);
        usPage.setName("form.guide.select.page");
        usPage.setColors(Colors.WHITE.getGuiColorCode(), usPage.getBgColor(), Colors.RED.getGuiColorCode(), usPage.getHoverBgColor(), Colors.WHITE.getGuiColorCode(), false);
        usPage.register(this);
        frmGuide.getContentContainer().add(usPage);

        // =================== TABS ==================================
        final UITabGroup utgTabs = new UITabGroup(this, UITabGroup.TabPosition.TOP);
        utgTabs.setPosition(UIComponent.INHERITED, UIComponent.INHERITED);
        final UIPanel upTabs = new UIPanel(this).setSize(440, 215).setPosition(5, 20);
        frmGuide.getContentContainer().add(upTabs);

        final UITab utPage = new UITab(this, "Page");
        utPage.setColor(Colors.DARK_GRAY.getGuiColorCode());
        final UIContainer ucPage = new UIContainer(this);
        utgTabs.addTab(utPage, ucPage);
        upTabs.add(ucPage);

        ubtnFormat = new UIButton(this, "F");
        ubtnFormat.setPosition(2, 5);
        ubtnFormat.setAutoSize(false);
        ubtnFormat.setSize(12, 12);
        ubtnFormat.setName("form.guide.button.format");
        ubtnFormat.setTextShadow(false);
        ubtnFormat.setVisible(false);
        ubtnFormat.register(this);
        ucPage.add(ubtnFormat);

        ubtnCode = new UIButton(this, "C");
        ubtnCode.setPosition(ubtnFormat.getWidth() + ubtnFormat.getX() + 2, ubtnFormat.getY());
        ubtnCode.setAutoSize(false);
        ubtnCode.setSize(12, 12);
        ubtnCode.setName("form.guide.button.code");
        ubtnCode.setTextShadow(false);
        ubtnCode.setVisible(false);
        ubtnCode.register(this);
        ucPage.add(ubtnCode);

        utfContents = new UITextField(this, true);
        utfContents.setPosition(2, ubtnFormat.getY() + ubtnFormat.getHeight() + 2);
        utfContents.setSize(430, 187);
        utfContents.setName("form.guide.textfield.contents");
        utfContents.setColors(utfContents.getTextColor(), Colors.GRAY.getGuiColorCode(), CONTROL.getGuiColorCode(),
                              Colors.BLACK.getGuiColorCode(), false);
        utfContents.getScrollbar().setAutoHide(true);
        ucPage.add(utfContents);

        final UITab utDetails = new UITab(this, "Details");
        utDetails.setColor(Colors.DARK_GRAY.getGuiColorCode());
        final UIContainer ucDetails = new UIContainer(this);
        utgTabs.addTab(utDetails, ucDetails);

        utgTabs.setActiveTab(utPage);
        utgTabs.attachTo(upTabs, false);
        frmGuide.getContentContainer().add(utgTabs);

        // ========================= TABS END =============================
        final UIButton ubtnAdd = new UIButton(this, "+");
        ubtnAdd.setAnchor(Anchor.TOP | Anchor.RIGHT);
        ubtnAdd.setPosition(usPage.getX() - usPage.getWidth() - 2, 5);
        ubtnAdd.setAutoSize(false);
        ubtnAdd.setSize(12, 12);
        ubtnAdd.setName("form.guide.button.add");
        ubtnAdd.setTextShadow(false);
        ubtnAdd.setVisible(ClientProxy.getPermissions().hasPermission("create"));
        ubtnAdd.register(this);
        frmGuide.getContentContainer().add(ubtnAdd);

        ubtnDelete = new UIButton(this, "-");
        ubtnDelete.setAnchor(Anchor.TOP | Anchor.RIGHT);
        ubtnDelete.setPosition(ubtnAdd.getX() - ubtnAdd.getWidth() - 2, ubtnAdd.getY());
        ubtnDelete.setAutoSize(false);
        ubtnDelete.setSize(12, 12);
        ubtnDelete.setName("form.guide.button.delete");
        ubtnDelete.setTextShadow(false);
        ubtnDelete.setVisible(false);
        ubtnDelete.register(this);
        frmGuide.getContentContainer().add(ubtnDelete);

        final UIButton ubtnClose = new UIButton(this, "Close");
        ubtnClose.setAnchor(Anchor.BOTTOM | Anchor.RIGHT);
        ubtnClose.setPosition(-5, -5);
        ubtnClose.setSize(30, 15);
        ubtnClose.setTextShadow(false);
        ubtnClose.setName("form.guide.button.close");
        ubtnClose.register(this);
        frmGuide.getContentContainer().add(ubtnClose);

        ubtnSave = new UIButton(this, "Save");
        ubtnSave.setAnchor(Anchor.BOTTOM | Anchor.RIGHT);
        ubtnSave.setPosition(ubtnClose.getX() - ubtnClose.getWidth() - 2, ubtnClose.getY());
        ubtnSave.setSize(30, 15);
        ubtnSave.setTextShadow(false);
        ubtnSave.setName("form.guide.button.save");
        ubtnSave.setVisible(false);
        ubtnSave.register(this);
        frmGuide.getContentContainer().add(ubtnSave);

        addToScreen(frmGuide);

        usPage.select(0);
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
        switch (event.getComponent().getName()) {
            case "form.guide.button.format":
                utfContents.setText(PageUtil.replaceColorCodes("&", utfContents.getText(), true));
                break;
            case "form.guide.button.code":
                utfContents.setText(PageUtil.replaceColorCodes("&", utfContents.getText(), false));
                break;
            case "form.guide.button.close":
                close();
                break;
            case "form.guide.button.save":
                if (usPage.getSelectedOption() != null) {
                    final Page page = (Page) usPage.getSelectedOption().getKey();
                    Guide.NETWORK_FORGE.sendToServer(new S00PageInformation(page.getIdentifier(), page.getName(), page.getCreated(), page.getAuthor(), page.getLastModified(), page.getLastContributor(), utfContents.getText()));
                }
                break;

        }
    }

    @Subscribe
    public void onUISelectEvent(UISelect.SelectEvent event) {
        if (event.getNewValue() == null) {
            ubtnFormat.setVisible(false);
            ubtnCode.setVisible(false);
            ubtnDelete.setVisible(false);
            ubtnSave.setVisible(false);
            return;
        }
        if (event.getComponent().getName().equalsIgnoreCase("form.guide.select.page")) {
            ((UIForm) event.getComponent().getParent().getParent()).setTitle("Guide (" + event.getNewValue().getLabel() + ")");
            utfContents.setText(((Page) event.getNewValue().getKey()).getContents());

            ubtnFormat.setVisible(ClientProxy.getPermissions().hasPermission("save." + ((Page) event.getNewValue().getKey()).getIdentifier()));
            ubtnCode.setVisible(ClientProxy.getPermissions().hasPermission("save." + ((Page) event.getNewValue().getKey()).getIdentifier()));

            // Only show '-' button if they have permission to delete the chosen guide.
            ubtnDelete.setVisible(ClientProxy.getPermissions().hasPermission("delete." + ((Page) event.getNewValue().getKey()).getIdentifier()));

            // Only show 'Save' button if they have permission to save the chosen guide.
            ubtnSave.setVisible(ClientProxy.getPermissions().hasPermission("save." + ((Page) event.getNewValue().getKey()).getIdentifier()));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPageInformationEvent(PageInformationEvent event) {
        if (usPage.getSelectedOption() != null) {
            if (usPage.getSelectedOption().getKey().equals(event.page)) {
                utfContents.setText(event.page.getContents());
            }
        }
    }

    private Map<Integer, UISelect.Option> populate() {
        final HashMap<Integer, UISelect.Option> options = Maps.newHashMap();

        int count = 0;
        for (Map.Entry<String, Page> entry : PageRegistry.getAll().entrySet()) {
            options.put(count, new UISelect.Option<>(count, entry.getValue(), entry.getValue().getName()));
            count++;
        }

        return options;
    }
}
