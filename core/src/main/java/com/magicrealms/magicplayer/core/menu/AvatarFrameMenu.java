package com.magicrealms.magicplayer.core.menu;

import com.magicrealms.magiclib.common.utils.StringUtil;
import com.magicrealms.magiclib.core.utils.ItemUtil;
import com.magicrealms.magicplayer.core.BukkitMagicPlayer;
import com.magicrealms.magicplayer.api.player.PlayerData;
import com.magicrealms.magicplayer.core.avatar.frame.AvatarFrame;
import com.magicrealms.magicplayer.core.setting.Setting;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.magicrealms.magicplayer.common.MagicPlayerConstant.YML_AVATAR_FRAME_MENU;
import static com.magicrealms.magicplayer.common.MagicPlayerConstant.YML_LANGUAGE;


/**
 * @author Ryan-0916
 * @Desc 头像框菜单
 * @date 2025-05-12
 */
public class AvatarFrameMenu extends AbstractSubSettingMenu{

    private final List<AvatarFrame> FRAMES;

    private AvatarFrame previewFrame;

    private String previewPrompt;

    private final int PAGE_COUNT;

    private final PlayerData HOLDER_DATA;

    private final String ICON_DISPLAY = "Icons.%s.Display";

    public AvatarFrameMenu(Player player,
                           @Nullable Runnable backRunnable,
                           List<Setting> settings,
                           int settingPage,
                           int settingPageCount,
                           int selectSettingIndex) {
        super(BukkitMagicPlayer.getInstance(), player, YML_AVATAR_FRAME_MENU,
                "A#####B##CDD#EEE##DDD#EEE##DDD#EEE##FG###HI##",
                backRunnable, settings, settingPage, settingPageCount, selectSettingIndex);
        this.FRAMES = BukkitMagicPlayer.getInstance()
                .getFrameManager().getFrames();
        this.previewPrompt = StringUtils.EMPTY;
        /* 获取菜单布局中每页显示的设置数量 */
        this.PAGE_COUNT = StringUtils
                .countMatches(super.getLayout(), "E");
        /* 玩家的个人信息 */
        this.HOLDER_DATA = BukkitMagicPlayer.getInstance()
                .getPlayerDataRepository()
                .queryByPlayer(player);
        super.setMaxPage(PAGE_COUNT <= 0 ? 1 :
                this.FRAMES.size() % PAGE_COUNT == 0 ?
                        this.FRAMES.size() / PAGE_COUNT : this.FRAMES.size() / PAGE_COUNT + 1);
        asyncOpenMenu();
    }


    @Override
    protected void handleMenuUnCache(String layout) {
        int size =  layout.length();
        /* 当前显示的下标 */
        int appearIndex = ((super.getPage() - 1) * PAGE_COUNT) - 1;
        for (int i = 0; i < size; i++){
            switch (layout.charAt(i)) {
                case 'A' -> super.setCheckBoxSlot(i, super.getBackMenuRunnable() != null);
                case 'E' -> {
                    if (FRAMES.size() > ++appearIndex) {
                        setFrame(i, FRAMES.get(appearIndex));
                    } else {
                        super.setItemSlot(i, ItemUtil.AIR);
                    }
                }
                case 'C', 'D' -> setHead(i, layout.charAt(i));
                case 'F', 'G' -> super.setButtonSlot(i, !(super.getPage() > 1));
                case 'H', 'I' -> super.setButtonSlot(i, !(super.getPage() < super.getMaxPage()));
                default -> super.setItemSlot(i);
            }
        }
    }

    private void setFrame(int i, AvatarFrame frame) {
        super.setItemSlot(i, frame.getDisabledItem());
    }

    private void setHead(int slot, Character key) {
        CompletableFuture.runAsync(() -> {
            String path = String.format(ICON_DISPLAY, key);
            ItemStack itemStack = key.equals('C') ?
                    ItemUtil.setItemStackByConfig(HOLDER_DATA.getHeadStack().clone(),
                            super.getPlugin().getConfigManager(),
                            super.getConfigPath(), path
                            , getPlayer()) :
                    ItemUtil.getItemStackByConfig(super.getPlugin().getConfigManager(),
                            super.getConfigPath(), path
                            , getPlayer());
            setItemSlot(slot, itemStack);
        });
    }

    @Override
    protected LinkedHashMap<String, String>
    processHandTitle(LinkedHashMap<String, String> title) {
        return title
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, (entry)
                        -> StringUtil.replacePlaceholders(entry.getValue(), createPlaceholders()), (oldVal, newVal) -> oldVal, LinkedHashMap::new));
    }

    protected Map<String, String> createPlaceholders() {
        Map<String, String> map = super.createPlaceholders();
        map.put("preview_frame", previewFrame != null ? previewFrame.getFrame() : StringUtils.EMPTY);
        map.put("preview_prompt", previewPrompt);
        return map;
    }

    @Override
    public void topInventoryClickEvent(InventoryClickEvent event, int slot) {
        if (!super.tryCooldown(slot, super.getPlugin().getConfigManager()
                .getYmlValue(YML_LANGUAGE,
                        "PlayerMessage.Error.ButtonCooldown"))) {
            return;
        }
        char c = super.getLayout().charAt(slot);
        asyncPlaySound("Icons." + c + ".Display.Sound");
        switch (c) {
            case 'A'-> super.backMenu();
            case 'F', 'J' -> super.changePage(- 1, b -> {
                asyncPlaySound(b ? "Icons." + c + ".ActiveDisplay.Sound" : "Icons." + c + ".DisabledDisplay.Sound");
                if (!b) return;
                super.handleMenu(super.getLayout());
                super.asyncUpdateTitle();
            });
            case 'H', 'I' -> super.changePage(1, b -> {
                asyncPlaySound(b ? "Icons." + c + ".ActiveDisplay.Sound" : "Icons." + c + ".DisabledDisplay.Sound");
                if (!b) return;
                super.handleMenu(super.getLayout());
                super.asyncUpdateTitle();
            });
            case 'E' -> clickFrame(slot);
        }
    }

    private void clickFrame(int slot) {
        int index = StringUtils
                .countMatches(super.getLayout().substring(0, slot), "E");
        this.previewFrame = FRAMES.get((super.getPage() - 1) * PAGE_COUNT + index);
        this.previewPrompt = getPlugin().getConfigManager().getYmlValue(YML_LANGUAGE, "Menu.FrameMenu.PreviewLock");
        asyncUpdateTitle();
    }
}
