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

import java.awt.Color;
import java.util.Arrays;

import com.almuradev.guide.client.ChatColor;
import com.google.common.eventbus.Subscribe;

import net.malisis.core.client.gui.Anchor;
import net.malisis.core.client.gui.MalisisGui;
import net.malisis.core.client.gui.component.container.UIBackgroundContainer;
import net.malisis.core.client.gui.component.control.UIMoveHandle;
import net.malisis.core.client.gui.component.decoration.UILabel;
import net.malisis.core.client.gui.component.interaction.UIButton;
import net.malisis.core.client.gui.component.interaction.UISelect;
import net.malisis.core.client.gui.component.interaction.UITextField;

public class GuideGui extends MalisisGui {
        
    private UIBackgroundContainer window, uiTitleBar;
    private UIButton xButton, closeButton, saveButton, deleteButton;
    private UILabel titleLabel;
    private UITextField textArea;
    private UISelect dropDownMenu;
    private int padding = 4;
  
    public GuideGui() {
        buildGui();
    }

    protected void buildGui() {
        guiscreenBackground = false;
        final UIBackgroundContainer window = new UIBackgroundContainer(this, 400, 225);
        window.setAnchor(Anchor.CENTER | Anchor.MIDDLE);
        window.setColor(Integer.MIN_VALUE);
        window.setBackgroundAlpha(125);

        // Create the title & Window layout 
        final UILabel titleLabel = new UILabel(this, ChatColor.WHITE + "Guide");
        titleLabel.setPosition(0, 5, Anchor.CENTER | Anchor.TOP);
        
        uiTitleBar = new UIBackgroundContainer(this);
        uiTitleBar.setSize(300, 1);
        uiTitleBar.setPosition(0, 17, Anchor.CENTER | Anchor.TOP);
        uiTitleBar.setColor(Color.gray.getRGB());
        
        xButton = new UIButton(this, ChatColor.BOLD + "X");
        xButton.setSize(5, 1);
        xButton.setPosition(-3, 1, Anchor.RIGHT | Anchor.TOP);
        xButton.setName("button.close");
        xButton.register(this);
        
        // Todo:  this list needs to be populated via the names of the guides that exist on the server pulled from the packet the server sends.
        dropDownMenu = new UISelect(this, 200, UISelect.Option.fromList(Arrays.asList("Almura - Banking System",
                "Almura - Chat System",
                //"Almura - Control Panel",
                "Almura - Help and Support",
                "Almura - Map Server",
                //"Almura - Member Levels",
                "Almura - News and Events",
                "Almura - Shop Locations",
                "Almura - Upcoming Changes",
                //"Almura - Known Bugs",
                "Almura - Voice Server",
                "Almura - Welcome",
                //"FAQ - Aqualock",
                "FAQ - Backpack",
                "FAQ - Becoming a Member",
                "FAQ - Cheating or Hacking",
                //"FAQ - Client Lag",
                "FAQ - Farming",
                //"FAQ - Finding a Recipe",
                "FAQ - Griefing Rules",
                "FAQ - Jobs and Money",
                "FAQ - Large Animal Farms",
                //"FAQ - New PLayers",
                //"FAQ - Our Server",
                //"FAQ - Player Accessories",
                //"FAQ - Read Me",
                "FAQ - Residence",
                "FAQ - Rules",
                "FAQ - Shops",
                //"FAQ - Sign Editor",
                "FAQ - Stargates",
                "FAQ - Why can't I cut down trees",
                "FAQ - Worlds and Maps")));        
        dropDownMenu.setPosition(5, 20, Anchor.TOP | Anchor.LEFT);
        dropDownMenu.setMaxExpandedWidth(200);
        dropDownMenu.select(0);
        dropDownMenu.register(this);

        // Create About us multi-line label
        textArea = new UITextField(this, "", true);
        String fieldText =
                "Almura 2.0 began June 1st, 2014. Based on the idea that we could finally get away from the broken and abandoned Spoutcraft client a brilliant developer came to Almura and said,"
                        + " \"Why don't you get rid of that out of date client and move into the present?\" This brilliant developer's name is "
                        + ChatColor.AQUA + "Zidane" + ChatColor.RESET + ". Along with him and another outstanding developer " + ChatColor.AQUA + "Grinch"
                        + ChatColor.RESET + ","
                        + " Almura 2.0 was born. Using the forge client as our basis these two developers, along with " + ChatColor.GOLD + "Dockter's"
                        + ChatColor.RESET + " content and gui abilities, built, in our opinion, one of the best content loading /"
                        + " gui enabled Minecraft experiences ever conceived. \r \r" + ChatColor.LIGHT_PURPLE + "More info to follow..." + ChatColor.RESET
                        + "";

        textArea.setSize(390, 160);
        textArea.setPosition(0, 40, Anchor.CENTER);
        textArea.setText(fieldText);
        textArea.setTextColor(Color.WHITE.getRGB());
        textArea.setName("mline.aboutus");
        
        final UIButton closeButton = new UIButton(this, "Close");
        closeButton.setSize(50, 14);
        closeButton.setPosition(-5, 0, Anchor.RIGHT | Anchor.BOTTOM);
        closeButton.setName("button.close");
        closeButton.register(this);

        window.add(titleLabel, uiTitleBar, xButton, textArea, closeButton, dropDownMenu);

        new UIMoveHandle(this, window);

        addToScreen(window);
    }
    
    @Subscribe
    public void onButtonClick(UIButton.ClickEvent event) {
        switch (event.getComponent().getName().toLowerCase()) {
        case "button.save":
            // To do:
            break;
        case "button.close":
            this.clearScreen();
            break;
        }
    }
    
    @Subscribe
    public void onSelectionChanged(UISelect.SelectEvent event) {        
        changeGuide(event.getNewValue().getLabel());
    }
    
    public void changeGuide(String guide) {
        // Query server or map for guide specified.
    }
}
