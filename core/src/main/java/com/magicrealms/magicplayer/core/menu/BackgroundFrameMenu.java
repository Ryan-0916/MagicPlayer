package com.magicrealms.magicplayer.core.menu;

import com.magicrealms.magicplayer.api.setting.SettingParam;
import com.magicrealms.magicplayer.core.BukkitMagicPlayer;
import com.magicrealms.magicplayer.core.frame.FrameTemplate;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.event.inventory.InventoryCloseEvent;

import static com.magicrealms.magicplayer.common.MagicPlayerConstant.YML_BACKGROUND_FRAME_MENU;
import static com.magicrealms.magicplayer.common.MagicPlayerConstant.YML_LANGUAGE;


/**
 * @author Ryan-0916
 * @Desc 背景框菜单
 * @date 2025-05-16
 */
public class BackgroundFrameMenu extends AbstractFrameMenu {

    public BackgroundFrameMenu(SettingParam param) {
        super(param, YML_BACKGROUND_FRAME_MENU,
                "A#####B##CDD#EEE##DDD#EEE##DDD#EEE##FG###HI##",
                BukkitMagicPlayer.getInstance()
                        .getBackgroundFrameManager().getFrames());
        previewPrompt = StringUtils.EMPTY;
        /* 默认预览为玩家佩戴的头像框 */
        if (HOLDER_DATA.getBackgroundFrameId() != null) {
            previewFrame = FRAMES.stream()
                    .filter(e -> e.getId() == HOLDER_DATA.getBackgroundFrameId())
                    .findFirst().orElse(null);
        }
        asyncOpenMenu();
    }

    public void closeEvent(InventoryCloseEvent e) {
        if (previewFrame != null
                && previewFrame.isUnlocked(super.getPlayer())
                && !Integer.valueOf(previewFrame.getId()).equals(HOLDER_DATA
                .getBackgroundFrameId())
        ) {
            BukkitMagicPlayer.getInstance().getPlayerDataRepository().
                    updateByPlayer(super.getPlayer(), data -> data.setBackgroundFrameId(previewFrame.getId()));
            super.backMenu();
        }
    }

    @Override
    protected void handlePreviewPrompt(FrameTemplate preview) {
        previewPrompt = getPlugin().getConfigManager()
                .getYmlValue(YML_LANGUAGE, this.previewFrame
                        .isUnlocked(super.getPlayer()) ? "Menu.BgMenu.PreviewUnlock": "Menu.BgMenu.PreviewLock");
    }
}
