package com.magicrealms.magicplayer.core.menu;

import com.magicrealms.magicplayer.api.setting.SettingParam;
import com.magicrealms.magicplayer.core.BukkitMagicPlayer;
import com.magicrealms.magicplayer.core.frame.FrameTemplate;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.event.inventory.InventoryCloseEvent;
import static com.magicrealms.magicplayer.common.MagicPlayerConstant.YML_AVATAR_FRAME_MENU;
import static com.magicrealms.magicplayer.common.MagicPlayerConstant.YML_LANGUAGE;


/**
 * @author Ryan-0916
 * @Desc 头像框菜单
 * @date 2025-05-12
 */
public class AvatarFrameMenu extends AbstractFrameMenu {

    public AvatarFrameMenu(SettingParam param) {
        super(param, YML_AVATAR_FRAME_MENU,
                "A#####B##CDD#EEE##DDD#EEE##DDD#EEE##FG###HI##",
                BukkitMagicPlayer.getInstance()
                        .getAvatarFrameManager().getFrames());

        previewPrompt = StringUtils.EMPTY;
        /* 默认预览为玩家佩戴的头像框 */
        if (HOLDER_DATA.getAvatarFrameId() != null) {
            previewFrame = FRAMES.stream()
                    .filter(e -> e.getId() == HOLDER_DATA.getAvatarFrameId())
                    .findFirst().orElse(null);
        }
        asyncOpenMenu();
    }

    @Override
    public void closeEvent(InventoryCloseEvent e) {
        super.closeEvent(e);
        if (previewFrame != null
                && previewFrame.isUnlocked(getPlayer())
                && !Integer.valueOf(previewFrame.getId()).equals(HOLDER_DATA
                .getAvatarFrameId())
        ) {
            BukkitMagicPlayer.getInstance().getPlayerDataRepository().
                    updateByPlayer(getPlayer(), data -> data.setAvatarFrameId(previewFrame.getId()));
            backMenu();
        }
    }

    @Override
    protected void handlePreviewPrompt(FrameTemplate preview) {
        previewPrompt = getPlugin().getConfigManager()
                .getYmlValue(YML_LANGUAGE, this.previewFrame.isUnlocked(getPlayer()) ? "Menu.FrameMenu.PreviewUnlock": "Menu.FrameMenu.PreviewLock");
    }
}
