package com.github.argon.sos.moreoptions.config.json.v4;

import com.github.argon.sos.mod.sdk.config.AbstractJsonConfigVersionHandler;
import com.github.argon.sos.mod.sdk.config.json.JsonConfigStore;
import com.github.argon.sos.moreoptions.config.ConfigMapper;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV5Config;
import com.github.argon.sos.moreoptions.config.json.v5.JsonConfigV5Mapper;

import java.util.Optional;

public class JsonConfigV4Handler extends AbstractJsonConfigVersionHandler<MoreOptionsV5Config> {
    public JsonConfigV4Handler(JsonConfigStore jsonConfigStore) {
        super(jsonConfigStore);
    }

    @Override
    public Optional<MoreOptionsV5Config> handle(int version) {
        return jsonConfigStore.get(JsonMoreOptionsV4Config.class)
            .map(JsonConfigV5Mapper::map)
            .map(ConfigMapper::mapConfig)
            // add other configs
            .map(domainConfig -> {
                jsonConfigStore.get(JsonRacesV4Config.class)
                    .map(JsonConfigV5Mapper::map)
                    .ifPresent(racesConfig -> ConfigMapper.mapInto(racesConfig, domainConfig));
                jsonConfigStore.get(JsonBoostersV4Config.class)
                    .map(JsonConfigV5Mapper::map)
                    .ifPresent(boostersConfig -> ConfigMapper.mapInto(boostersConfig, domainConfig));
                return domainConfig;
            });
    }
}
