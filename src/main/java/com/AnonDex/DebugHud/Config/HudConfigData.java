package com.AnonDex.DebugHud.Config;

public class HudConfigData {

    //Values Variables
    public boolean showCoordinates;
    public boolean showPing;
    public boolean showWorld;
    public boolean showGameTime;
    public boolean showIRLTime;
    public boolean showSpeed;

    //Config Variables
    public String hudColor;
    public String hudPosition;
    public String hudTransparency;
    public Integer hudFontSize;
    public String hudBackground;

    public HudConfigData() {
        //Default Values
        this.showCoordinates = true;
        this.showPing = true;
        this.showWorld = true;
        this.showGameTime = true;
        this.showIRLTime = true;
        this.showSpeed = true;

        //Default Configs
        this.hudBackground = "141c26";
        this.hudTransparency = "05";
        this.hudColor = "ffffff";
        this.hudFontSize = 12;
        this.hudPosition = "Top-Left";
    }

}
