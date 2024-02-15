
package com.github.argon.sos.moreoptions.game.api;

import com.github.argon.sos.moreoptions.MoreOptionsScript;
import com.github.argon.sos.moreoptions.game.booster.FactionOpinionBooster;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.MathUtil;
import game.boosting.*;
import game.faction.FACTIONS;
import init.race.RACES;
import init.sprite.SPRITES;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import snake2d.util.sets.LIST;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameBoosterApi {

    private final static Logger log = Loggers.getLogger(GameBoosterApi.class);

    private Map<String, Boostable> allBoosters;
    private Map<String, Boostable> enemyBoosters;
    private Map<String, Boostable> playerBoosters;

    private Map<String, BoostableCat> catBootsers;

    public final static String KEY_PREFIX = "booster";

    @Getter(lazy = true)
    private final static GameBoosterApi instance = new GameBoosterApi();

    public void clearCached() {
        allBoosters = null;
        playerBoosters = null;
        enemyBoosters = null;
        catBootsers = null;
    }

    public Map<String, Boostable> getAllBoosters() {
        if (allBoosters == null) {
            allBoosters = new HashMap<>();
            BOOSTABLES.CIVICS().all().forEach(civ -> {allBoosters.put(KEY_PREFIX + "." + civ.key, civ);});
            BOOSTABLES.BATTLE().all().forEach(civ -> {allBoosters.put(KEY_PREFIX + "." + civ.key, civ);});
            BOOSTABLES.BEHAVIOUR().all().forEach(civ -> {allBoosters.put(KEY_PREFIX + "." + civ.key, civ);});
            BOOSTABLES.PHYSICS().all().forEach(civ -> {allBoosters.put(KEY_PREFIX + "." + civ.key, civ);});
            BOOSTABLES.ROOMS().all().forEach(civ -> {allBoosters.put(KEY_PREFIX + "." + civ.key, civ);});
        }

        return allBoosters;
    }

    public Map<String, Boostable> getPlayerBoosters() {
        if (playerBoosters == null) {
            playerBoosters = new HashMap<>();

            BOOSTABLES.all().forEach(boostable -> { //TODO::differentiate player and enemy
                boostable.get(FACTIONS.player());
                playerBoosters.put(KEY_PREFIX + "." + boostable.key, boostable);
            });
        }

        return allBoosters;
    }

    public Map<String, Boostable> getEnemyBoosters() {
        if (enemyBoosters == null) {
            enemyBoosters = new HashMap<>();
            BOOSTABLES.all().forEach(boostable -> {
                enemyBoosters.put(KEY_PREFIX + "." + boostable.key, boostable);
            });
        }
        return allBoosters;
    }

    public Map<String, BoostableCat> getCatBoosters() {
        if (catBootsers == null) {
            catBootsers = new HashMap<>();
            BOOSTABLES.CIVICS().all().forEach(civ -> {catBootsers.put(KEY_PREFIX + "." + civ.key, civ.cat);});
            BOOSTABLES.BATTLE().all().forEach(civ -> {catBootsers.put(KEY_PREFIX + "." + civ.key, civ.cat);});
            BOOSTABLES.BEHAVIOUR().all().forEach(civ -> {catBootsers.put(KEY_PREFIX + "." + civ.key, civ.cat);});
            BOOSTABLES.PHYSICS().all().forEach(civ -> {catBootsers.put(KEY_PREFIX + "." + civ.key, civ.cat);});
            BOOSTABLES.ROOMS().all().forEach(civ -> {catBootsers.put(KEY_PREFIX + "." + civ.key, civ.cat);});
        }
        return catBootsers;
    }


    public boolean isEnemyBooster(String key) {
        return getEnemyBoosters().containsKey(key);
    }

    public BoostableCat getCat(String key) {
        return getCatBoosters().get(key);
    }

    public boolean isPlayerBooster(String key) {
        return getPlayerBoosters().containsKey(key);
    }

    public void setBoosterValue(Boostable boostable, int boost) {
        double currentValue = boostable.baseValue;
        double newValue = currentValue * MathUtil.toPercentage(boost);

        log.trace("Applying boost value %s%% to %s = %s", boost, boostable.key, newValue);
        boostable.setBaseValue(newValue);
    }
}
