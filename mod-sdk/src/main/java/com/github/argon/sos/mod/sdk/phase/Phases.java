package com.github.argon.sos.mod.sdk.phase;

import com.github.argon.sos.mod.sdk.game.api.IFileLoad;
import com.github.argon.sos.mod.sdk.game.api.IFileSave;
import snake2d.util.file.FilePutter;

import java.nio.file.Path;

/**
 * Contains different game phases to implement.
 * Some game elements are unavailable until a certain phase.
 *
 * Classes implementing a certain init method can be registered
 * via the {@link PhaseManager#register(Phase, Phases)} method.
 *
 * Phases starting with "init" are executed only once per game.
 * Phases starting with "on" can be executed multiple times.
 */
public interface Phases {
    /**
     * 1. PHASE
     * Before a game is loaded
     */
    default void initBeforeGameCreated() {
        throw new PhaseNotImplemented(Phase.INIT_BEFORE_GAME_CREATED);
    }

    /**
     * 2. PHASE
     * When the mod instance is created
     */
    default void initModCreateInstance() {
        throw new PhaseNotImplemented(Phase.INIT_MOD_CREATE_INSTANCE);
    }

    /**
     * (3.) PHASE: can be called multiple times
     * When the game loaded a save game
     */
    default void onGameLoaded(Path saveFilePath, IFileLoad fileLoader) {
        throw new PhaseNotImplemented(Phase.ON_GAME_SAVE_LOADED);
    }

    /**
     * (4.) PHASE: can be called multiple times
     * When the player loads into game while already playing another one
     */
    default void onGameSaveReloaded() {
        throw new PhaseNotImplemented(Phase.ON_GAME_SAVE_RELOADED);
    }

    /**
     * 5. PHASE
     * When a new game session is started (not when a player starts a fresh new game)
     * This will not fire when the player loads from an existing game into another one.
     */
    default void initNewGameSession() {
        throw new PhaseNotImplemented(Phase.INIT_NEW_GAME_SESSION);
    }

    /**
     * 6. PHASE
     * When the game starts the update() process
     */
    default void initGameUpdating() {
        throw new PhaseNotImplemented(Phase.INIT_GAME_UPDATING);
    }

    /**
     * (7.) PHASE: can be called multiple times
     * Called by the games update loop
     */
    default void onGameUpdate(double seconds)  {
        throw new PhaseNotImplemented(Phase.ON_GAME_UPDATE);
    }

    /**
     * 8. PHASE
     * When the game UI is loaded
     */
    default void initGameUiPresent() {
        throw new PhaseNotImplemented(Phase.INIT_GAME_UI_PRESENT);
    }

    /**
     * 9. PHASE: can be called multiple times
     * When the game ui being initialized. This is called everytime the game builds the ui.
     * So also when switching between a battlefield screen and the settlement.
     */
    default void onViewSetup() {
        throw new PhaseNotImplemented(Phase.ON_VIEW_SETUP);
    }

    /**
     * 10. PHASE
     * When the settlement ui is loaded for the first time
     */
    default void initSettlementUiPresent() {
        throw new PhaseNotImplemented(Phase.INIT_SETTLEMENT_UI_PRESENT);
    }

    /**
     * When the game saves
     */
    default void onGameSaved(Path saveFilePath, IFileSave fileSaver) {
        throw new PhaseNotImplemented(Phase.ON_GAME_SAVED);
    }

    /**
     * Before a battle is started
     */
    default void onGameBeforeBattle() {
        throw new PhaseNotImplemented(Phase.ON_GAME_BEFORE_BATTLE);
    }

    /**
     * When the game saves a {@link game.battle.BattleState}
     */
    default void onGameBeforeBattleSaved(Path saveFilePath) {
        throw new PhaseNotImplemented(Phase.ON_GAME_BEFORE_BATTLE_SAVED);
    }

    /**
     * When a battle is started
     */
    default void onGameBattle() {
        throw new PhaseNotImplemented(Phase.ON_GAME_BATTLE);
    }

    /**
     * When the game saves a {@link game.battle.BattleState}
     */
    default void onGameBattleSaved(Path saveFilePath) {
        throw new PhaseNotImplemented(Phase.ON_GAME_BATTLE_SAVED);
    }

    /**
     * When the game crashes
     */
    default void onCrash(Throwable e) {
        throw new PhaseNotImplemented(Phase.ON_CRASH);
    }
}
