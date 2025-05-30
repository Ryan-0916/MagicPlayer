package com.magicrealms.magicplayer.core.menu;

import com.magicrealms.magiclib.common.utils.StringUtil;
import com.magicrealms.magiclib.core.utils.ItemUtil;
import com.magicrealms.magicplayer.api.player.PlayerData;
import com.magicrealms.magicplayer.api.setting.AbstractSubSettingMenu;
import com.magicrealms.magicplayer.api.setting.SettingParam;
import com.magicrealms.magicplayer.core.BukkitMagicPlayer;
import com.magicrealms.magicplayer.core.frame.FrameTemplate;
import com.magicrealms.magicplayer.core.menu.sort.FrameSort;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.magicrealms.magicplayer.common.MagicPlayerConstant.YML_LANGUAGE;

/**
 * @author Ryan-0916
 * @Desc 抽象框菜单
 * @date 2025-05-16
 */
public abstract class AbstractFrameMenu extends AbstractSubSettingMenu {

    protected final List<FrameTemplate> FRAMES;

    /* 当前试穿的框 */
    protected FrameTemplate previewFrame;

    protected String previewPrompt;

    protected final int PAGE_COUNT;

    protected final PlayerData HOLDER_DATA;

    protected final String ICON_DISPLAY = "Icons.%s.Display";

    protected FrameSort sort = FrameSort.PUBLISH_TIME_OLDEST;

    public AbstractFrameMenu(SettingParam param,
                             String configPath,
                             String defLayout,
                             List<FrameTemplate> frames) {
        super(BukkitMagicPlayer.getInstance(),
                configPath,
                defLayout,
                param);
        this.FRAMES = frames;
        sortFrames();
        this.previewPrompt = StringUtils.EMPTY;
        /* 玩家的个人信息 */
        this.HOLDER_DATA = BukkitMagicPlayer.getInstance()
                .getPlayerDataRepository()
                .queryByPlayer(param.player());
        /* 获取菜单布局中每页显示的设置数量 */
        this.PAGE_COUNT = StringUtils
                .countMatches(getLayout(), "E");
        setMaxPage(PAGE_COUNT <= 0 || FRAMES.isEmpty() ? 1 :
                FRAMES.size() % PAGE_COUNT == 0 ?
                        FRAMES.size() / PAGE_COUNT : FRAMES.size() / PAGE_COUNT + 1);
    }

    public void sortFrames() {
        switch (sort) {
            case PUBLISH_TIME_NEWEST -> FRAMES.sort(Comparator.comparingInt(FrameTemplate::getId).reversed());
            case PUBLISH_TIME_OLDEST -> FRAMES.sort(Comparator.comparingInt(FrameTemplate::getId));
            case UNLOCKED -> FRAMES.sort(Comparator
                        .comparing((FrameTemplate f) -> !f.isUnlocked(getPlayer()))
                        .thenComparingInt(FrameTemplate::getId));
            case LOCKED -> FRAMES.sort(Comparator
                        .comparing((FrameTemplate f) -> f.isUnlocked(getPlayer()))
                        .thenComparingInt(FrameTemplate::getId));
        }
    }

    @Override
    protected void handleMenuUnCache(String layout) {
        int size =  layout.length();
        /* 当前显示的下标 */
        int appearIndex = ((getPage() - 1) * PAGE_COUNT) - 1;
        for (int i = 0; i < size; i++){
            switch (layout.charAt(i)) {
                case 'A' -> setCheckBoxSlot(i, getBackMenuRunnable() != null);
                case 'B' -> setItemSlot(i, sort.getItemSlot(getPlugin().getConfigManager(), getConfigPath(), 'B'));
                case 'E' -> {
                    if (FRAMES.size() > ++appearIndex) {
                        setFrame(i, FRAMES.get(appearIndex));
                    } else {
                        setItemSlot(i, ItemUtil.AIR);
                    }
                }
                case 'C', 'D' -> setHead(i, layout.charAt(i));
                case 'F', 'G' -> setButtonSlot(i, !(getPage() > 1));
                case 'H', 'I' -> setButtonSlot(i, !(getPage() < getMaxPage()));
                default -> setItemSlot(i);
            }
        }
    }

    private void setFrame(int i, FrameTemplate frame) {
        setItemSlot(i, frame.isUnlocked(getPlayer()) ?
                frame.getUnlockItem() : frame.getLockItem());
    }

    private void setHead(int slot, Character key) {
        CompletableFuture.runAsync(() -> {
            String path = String.format(ICON_DISPLAY, key);
            ItemStack itemStack = key.equals('C') ?
                    ItemUtil.setItemStackByConfig(HOLDER_DATA.getHeadStack().clone(),
                            getPlugin().getConfigManager(),
                            getConfigPath(), path
                            , getPlayer()) :
                    ItemUtil.getItemStackByConfig(getPlugin().getConfigManager(),
                            getConfigPath(), path
                            , getPlayer());
            setItemSlot(slot, itemStack);
        });
    }

    @Override
    protected LinkedHashMap<String, String> processHandTitle(LinkedHashMap<String, String> title) {
        Map<String, String> map = createPlaceholders();
        return title
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, (entry)
                        -> StringUtil.replacePlaceholders(entry.getValue(), map), (oldVal, newVal) -> oldVal, LinkedHashMap::new));
    }

    protected Map<String, String> createPlaceholders() {
        Map<String, String> map = super.createPlaceholders();
        map.put("preview_frame", previewFrame != null
                ? previewFrame.getFrame() : StringUtils.EMPTY);
        map.put("preview_prompt", previewPrompt);
        return map;
    }

    @Override
    public void topInventoryClickEvent(InventoryClickEvent event, int slot) {
        if (!tryCooldown(slot, getPlugin().getConfigManager().getYmlValue(YML_LANGUAGE,"PlayerMessage.Error.ButtonCooldown"))) {
            return;
        }
        char c = getLayout().charAt(slot);
        asyncPlaySound("Icons." + c + ".Display.Sound");
        switch (c) {
            case 'A'-> backMenu();
            case 'B' -> {
                cleanItemCache();
                sort = sort.next();
                sortFrames();
                goToFirstPage();
                handleMenu(getLayout());
                asyncUpdateTitle();
            }
            case 'F', 'J' -> changePage(- 1, b -> {
                asyncPlaySound(b ? "Icons." + c + ".ActiveDisplay.Sound" : "Icons." + c + ".DisabledDisplay.Sound");
                if (!b) return;
                handleMenu(getLayout());
                asyncUpdateTitle();
            });
            case 'H', 'I' -> changePage(1, b -> {
                asyncPlaySound(b ? "Icons." + c + ".ActiveDisplay.Sound" : "Icons." + c + ".DisabledDisplay.Sound");
                if (!b) return;
                handleMenu(getLayout());
                asyncUpdateTitle();
            });
            case 'E' -> clickFrame(slot);
        }
    }

    private void clickFrame(int slot) {
        int index = StringUtils
                .countMatches(getLayout().substring(0, slot), "E");
        this.previewFrame = FRAMES.get((getPage() - 1) * PAGE_COUNT + index);
        handlePreviewPrompt(previewFrame);
        asyncUpdateTitle();
    }

    protected abstract void handlePreviewPrompt(FrameTemplate preview);

}
