package com.magicrealms.magicplayer.api;

import com.magicrealms.magiclib.bukkit.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.store.MongoDBStore;
import com.magicrealms.magiclib.common.store.RedisStore;
import com.magicrealms.magicplayer.api.avatar.IAvatarManager;
import com.magicrealms.magicplayer.api.repository.IPlayerDataRepository;
import com.magicrealms.magicplayer.api.setting.ISettingRegistry;
import com.magicrealms.magicplayer.api.skin.ISkinManager;
import lombok.Getter;

/**
 * @author Ryan-0916
 * @Desc Abstract MagicPlayer Plugin
 * @date 2025-05-13
 */
public abstract class MagicPlayer extends MagicRealmsPlugin {

    protected MagicPlayerAPI api;

    @Getter
    private static MagicPlayer instance;

    @Getter
    protected RedisStore redisStore;

    @Getter
    protected MongoDBStore mongoDBStore;

    @Getter
    protected IAvatarManager avatarManager;

    @Getter
    protected ISkinManager skinManager;

    @Getter
    protected ISettingRegistry settingRegistry;

    @Getter
    protected IPlayerDataRepository playerDataRepository;

    public MagicPlayer() {
        instance = this;
    }
}
