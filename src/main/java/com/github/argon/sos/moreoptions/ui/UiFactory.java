package com.github.argon.sos.moreoptions.ui;


import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.MoreOptionsV2Config;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.game.ui.Modal;
import com.github.argon.sos.moreoptions.game.ui.Window;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.metric.MetricExporter;
import com.github.argon.sos.moreoptions.ui.panel.BoostersPanel;
import com.github.argon.sos.moreoptions.ui.panel.RacesSelectionPanel;
import com.github.argon.sos.moreoptions.ui.panel.RacesPanel;
import init.paths.ModInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import util.save.SaveFile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UiFactory {

    @Getter(lazy = true)
    private final static UiFactory instance = new UiFactory(
        GameApis.getInstance(),
        ConfigStore.getInstance(),
        MetricExporter.getInstance(),
        UiMapper.getInstance()
    );

    private final static Logger log = Loggers.getLogger(UiFactory.class);

    private final GameApis gameApis;
    private final ConfigStore configStore;
    private final MetricExporter metricExporter;
    private final UiMapper uiMapper;

    /**
     * Generates More Options ui via available config
     */
    public Modal<MoreOptions> buildMoreOptionsModal(String title, MoreOptionsV2Config config) {
        log.debug("Building '%s' ui", title);

        List<BoostersPanel.Entry> boosterEntries = uiMapper.mapToBoosterPanelEntries(config.getBoosters());
        Map<String, List<RacesPanel.Entry>> raceEntries = uiMapper.mapToRacePanelEntries(config.getRaces().getLikings());

        Set<String> availableStats = gameApis.stats().getAvailableStatKeys();
        ModInfo modInfo = gameApis.mod().getCurrentMod().orElse(null);
        Path exportFolder = MetricExporter.EXPORT_FOLDER;
        Path exportFile = metricExporter.getExportFile();

        Modal<MoreOptions> moreOptionsModal = new Modal<>(title, new MoreOptions(
            config,
            configStore,
            boosterEntries,
            raceEntries,
            availableStats,
            exportFolder,
            exportFile,
            modInfo
        ));
        moreOptionsModal.center();

        return moreOptionsModal;
    }

    /**
     * Generates race config selection window
     */
    public Window<RacesSelectionPanel> buildRacesConfigSelection(String title) {
        log.debug("Building '%s' ui", title);
        RacesSelectionPanel.Entry current = null;

        // prepare entries
        List<RacesSelectionPanel.Entry> racesConfigs = new ArrayList<>();
        List<ConfigStore.RaceConfigMeta> raceConfigMetas = configStore.loadRacesConfigMetas();
        for (ConfigStore.RaceConfigMeta configMeta : raceConfigMetas) {
            SaveFile saveFile = gameApis.save().findByPathContains(configMeta.getConfigPath()).orElse(null);

            RacesSelectionPanel.Entry entry = RacesSelectionPanel.Entry.builder()
                .configPath(configMeta.getConfigPath())
                .creationDate(configMeta.getCreationTime())
                .updateDate(configMeta.getUpdateTime())
                .saveFile(saveFile)
                .build();
            SaveFile currentFile = gameApis.save().getCurrentFile();

            // is the file the currently active one?
            if (current == null && (
                saveFile != null &&
                currentFile != null &&
                saveFile.fullName.equals(currentFile.fullName)
            )) {
                current = entry;
            }

            racesConfigs.add(entry);
        }

        Window<RacesSelectionPanel> window = new Window<>(title, new RacesSelectionPanel(racesConfigs, current));
        window.center();

        return window;
    }
}
