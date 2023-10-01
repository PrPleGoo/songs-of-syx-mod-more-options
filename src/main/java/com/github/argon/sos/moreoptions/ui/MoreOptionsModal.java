package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.ui.Button;
import com.github.argon.sos.moreoptions.game.ui.HorizontalLine;
import com.github.argon.sos.moreoptions.ui.panel.BoostersPanel;
import com.github.argon.sos.moreoptions.ui.panel.EventsPanel;
import com.github.argon.sos.moreoptions.ui.panel.SoundsPanel;
import com.github.argon.sos.moreoptions.ui.panel.WeatherPanel;
import com.github.argon.sos.moreoptions.util.UiUtil;
import game.VERSION;
import init.C;
import init.paths.ModInfo;
import init.sprite.UI.UI;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import snake2d.MButt;
import snake2d.Renderer;
import snake2d.util.color.COLOR;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.clickable.CLICKABLE;
import snake2d.util.sets.Tuple;
import util.gui.misc.GBox;
import util.gui.misc.GButt;
import util.gui.misc.GText;
import util.gui.panel.GPanelL;
import view.interrupter.Interrupter;
import view.main.VIEW;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Window containing all other UI elements.
 * Will pop up in the middle of the game and pauses the game.
 *
 * FIXME BUG? hovering over elements behind the window will trigger ui elements beneath e.g. showing tooltips from citizens
 */
@RequiredArgsConstructor
public class MoreOptionsModal extends Interrupter {

    private final GuiSection section = new GuiSection();
    private final ConfigStore configStore;
    private final ModInfo modInfo;

    private CLICKABLE.Switcher switcher;

    private EventsPanel eventsPanel;
    private  SoundsPanel soundsPanel;
    private  WeatherPanel weatherPanel;

    private  BoostersPanel boostersPanel;

    @Getter
    private Button cancelButton;
    @Getter
    private Button resetButton;

    @Getter
    private Button applyButton;

    @Getter
    private Button undoButton;

    @Getter
    private Button okButton;

    private double updateTimerSeconds = 0d;
    private final static int UPDATE_INTERVAL_SECONDS = 1;

    public void init(MoreOptionsConfig config) {
        GPanelL pan = new GPanelL();
        pan.setTitle("More Options");
        pan.setCloseAction(this::hide);
        section.add(pan);

        soundsPanel = new SoundsPanel(config.getSoundsAmbience(), config.getSoundsSettlement(), config.getSoundsRoom());
        eventsPanel = new EventsPanel(config.getEventsSettlement(), config.getEventsWorld(), config.getEventsChance());
        weatherPanel = new WeatherPanel(config.getWeather());
        boostersPanel = new BoostersPanel(config.getBoosters());

        Map<Tuple<String, String>, GuiSection> panels = new LinkedHashMap<>();
        panels.put(new Tuple.TupleImp<>("Sounds", "Tune the volume of various sounds."), soundsPanel);
        panels.put(new Tuple.TupleImp<>("Events", "Toggle and tune events."), eventsPanel);
        panels.put(new Tuple.TupleImp<>("Weather", "Influence weather effects."), weatherPanel);
        panels.put(new Tuple.TupleImp<>("Boosters", "Increase or decrease various bonuses."), boostersPanel);

        GuiSection header = header(panels);
        section.add(header);

        switcher = new CLICKABLE.Switcher(soundsPanel);
        section.add(switcher);

        HorizontalLine horizontalLine = new HorizontalLine(soundsPanel.body().width(), 14, 1);
        section.add(horizontalLine);

        GuiSection footer = footer();
        section.add(footer);

        int width = UiUtil.getMaxWidth(panels.values()) + 50;
        int height = switcher.body().height() + header.body().height() + horizontalLine.body().height() + footer.body().height() + 50;

        pan.body.setDim(width, height);
        pan.body().centerIn(C.DIM());

        header.body().moveY1(pan.getInnerArea().y1());
        header.body().centerX(pan);

        soundsPanel.body().moveY1(header.body().y2() + 15);
        soundsPanel.body().centerX(header);

        horizontalLine.body().moveY1(soundsPanel.body().y2() + 15);
        horizontalLine.body().centerX(header);

        footer.body().moveY1(horizontalLine.body().y2() + 10);
        footer.body().centerX(header);
    }

    public void show() {
        show(VIEW.inters().manager);
    }
    public void hide() {
        super.hide();
    }

    public MoreOptionsConfig getConfig() {
        return MoreOptionsConfig.builder()
            .eventsSettlement(eventsPanel.getSettlementEventsConfig())
            .eventsWorld(eventsPanel.getWorldEventsConfig())
            .eventsChance(eventsPanel.getEventsChanceConfig())
            .soundsAmbience(soundsPanel.getSoundsAmbienceConfig())
            .soundsSettlement(soundsPanel.getSoundsSettlementConfig())
            .soundsRoom(soundsPanel.getSoundsRoomConfig())
            .weather(weatherPanel.getConfig())
            .boosters(boostersPanel.getConfig())
            .build();
    }

    public void applyConfig(MoreOptionsConfig config) {
        eventsPanel.applyConfig(config.getEventsSettlement(), config.getEventsWorld(), config.getEventsChance());
        soundsPanel.applyConfig(config.getSoundsAmbience(), config.getSoundsSettlement(), config.getSoundsRoom());
        weatherPanel.applyConfig(config.getWeather());
        boostersPanel.applyConfig(config.getBoosters());
    }

    /**
     * @return whether panel configuration is different from {@link ConfigStore#getCurrentConfig()} ()}
     */
    private boolean isDirty() {
        return configStore.getCurrentConfig()
            .map(currentConfig -> !getConfig().equals(currentConfig))
            // no current config in memory
            .orElse(true);
    }

    private GuiSection header(Map<Tuple<String, String>, GuiSection> panels) {
        GuiSection section = new GuiSection();
        GuiSection versions = versions();
        GuiSection space = new GuiSection();
        space.body().setWidth(versions.body().width());
        section.addRightC(0, space);

        panels.forEach((titleDesc, panel) -> {

            GButt.ButtPanel button = new GButt.ButtPanel(titleDesc.a()) {
                @Override
                protected void clickA() {
                    switcher.set(panel, DIR.N);
                }

                @Override
                protected void renAction() {
                    selectedSet(switcher.get() == panel);
                }
            };
            button.hoverInfoSet(titleDesc.b());
            section.addRightC(0, button.setDim(136, 32));
        });

        section.addRightC(50, versions);

        return section;
    }

    private GuiSection versions() {
        COLOR color = COLOR.WHITE50;
        GuiSection versions = new GuiSection();

        GText gameVersion = new GText(UI.FONT().S, "Game Version: " + VERSION.VERSION_STRING);
        gameVersion.color(color);

        String modVersionString = "NO_VER";
        if (modInfo != null) {
            modVersionString = modInfo.version;
        }

        GText modVersion = new GText(UI.FONT().S, "Mod Version: " + modVersionString);
        modVersion.color(color);

        versions.addDown(0, gameVersion);
        versions.addDown(2, modVersion);

        return versions;
    }

    private GuiSection footer() {
        GuiSection section = new GuiSection();

        this.cancelButton = new Button("Cancel", COLOR.WHITE25);
        cancelButton.hoverInfoSet("Closes window without applying changes");
        section.addRight(0, cancelButton);

        this.resetButton = new Button("Default", COLOR.WHITE25);
        resetButton.hoverInfoSet("Resets to default options");
        section.addRight(10, resetButton);

        this.undoButton = new Button("Undo", COLOR.WHITE25);
        undoButton.hoverInfoSet("Undo made changes");
        undoButton.setEnabled(false);
        section.addRight(10, undoButton);

        this.applyButton = new Button("Apply", COLOR.WHITE15);
        applyButton.hoverInfoSet("Apply and save options");
        applyButton.setEnabled(false);
        section.addRight(150, applyButton);

        this.okButton = new Button("OK", COLOR.WHITE15);
        okButton.hoverInfoSet("Apply and exit");
        section.addRight(10, okButton);

        return section;
    }

    @Override
    protected boolean hover(COORDINATE coordinate, boolean b) {
        return section.hover(coordinate);
    }

    @Override
    protected void mouseClick(MButt mButt) {
        if (mButt == MButt.RIGHT)
            hide();
        else if (mButt == MButt.LEFT)
            section.click();
    }

    @Override
    protected void hoverTimer(GBox gBox) {
        section.hoverInfoGet(gBox);
    }

    @Override
    protected boolean render(Renderer renderer, float v) {
        section.render(renderer, v);
        return true;
    }

    @Override
    protected boolean update(float seconds) {
        updateTimerSeconds += seconds;

        // check for changes in config
        if (updateTimerSeconds >= UPDATE_INTERVAL_SECONDS) {
            updateTimerSeconds = 0d;

            applyButton.setEnabled(isDirty());
            undoButton.setEnabled(isDirty());
        }

        return false;
    }
}
