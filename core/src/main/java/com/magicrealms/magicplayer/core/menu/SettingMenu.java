package com.magicrealms.magicplayer.core.menu;

import com.magicrealms.magiclib.common.utils.StringUtil;
import com.magicrealms.magiclib.core.holder.PageMenuHolder;
import com.magicrealms.magiclib.core.utils.ItemUtil;
import com.magicrealms.magicplayer.core.BukkitMagicPlayer;
import com.magicrealms.magicplayer.api.player.PlayerData;
import com.magicrealms.magicplayer.api.setting.Setting;
import com.magicrealms.magicplayer.api.setting.SettingParam;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.magicrealms.magicplayer.common.MagicPlayerConstant.YML_LANGUAGE;
import static com.magicrealms.magicplayer.common.MagicPlayerConstant.YML_SETTING_MENU;

/**
 * @author Ryan-0916
 * @Desc 设置菜单
 * @date 2025-05-10
 */
public class SettingMenu extends PageMenuHolder {

    private final List<Setting> SETTINGS;

    private final PlayerData HOLDER_DATA;

    private final int PAGE_COUNT;

    private final String ICON_DISPLAY = "Icons.%s.Display";

    public SettingMenu(Player player, @Nullable Runnable backRunnable) {
        super(BukkitMagicPlayer.getInstance(), player, YML_SETTING_MENU,
                "A########BC##HJKKKCC###JKKKDEFG#JKKK####IJKKK", backRunnable);
        this.SETTINGS = BukkitMagicPlayer.getInstance()
                .getSettingRegistry().getSettings();
        /* 获取菜单布局中每页显示的设置数量 */
        this.PAGE_COUNT = StringUtils
                .countMatches(getLayout(), "J");
        /* 玩家的个人信息 */
        this.HOLDER_DATA = BukkitMagicPlayer.getInstance()
                .getPlayerDataRepository()
                .queryByPlayer(player);
        setMaxPage(PAGE_COUNT <= 0 ? 1 :
                this.SETTINGS.size() % PAGE_COUNT == 0 ?
                        this.SETTINGS.size() / PAGE_COUNT : this.SETTINGS.size() / PAGE_COUNT + 1);
        asyncOpenMenu();
    }

    @Override
    protected void handleMenuUnCache(String layout) {
        int size =  layout.length();
        /* 当前显示的下标 */
        int appearIndex = ((getPage() - 1) * PAGE_COUNT) - 1;
        for (int i = 0; i < size; i++){
            char c = layout.charAt(i);
            switch (c) {
                case 'A' -> setCheckBoxSlot(i, getBackMenuRunnable() != null);
                case 'J', 'K' -> {
                    if (SETTINGS.size() > (c == 'J' ? ++appearIndex : appearIndex)) {
                        setSetting(i, c, SETTINGS.get(appearIndex));
                    } else {
                        setItemSlot(i, ItemUtil.AIR);
                    }
                }
                case 'B', 'C' -> setHead(i, c);
                case 'H' -> setButtonSlot(i, !(getPage() > 1));
                case 'I' -> setButtonSlot(i, !(getPage() < getMaxPage()));
                default -> setItemSlot(i);
            }
        }
    }

    private void setHead(int slot, char key) {
        CompletableFuture.runAsync(() -> {
            String path = String.format(ICON_DISPLAY, key);
            ItemStack itemStack = key == 'B' ?
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

    private void setSetting(int i, char key, Setting setting) {
        setItemSlot(i, ItemUtil.getItemStackByConfig(getPlugin().getConfigManager()
            , getConfigPath(), String.format(ICON_DISPLAY, key), Map.of(
                        "setting_name", setting.getName(),
                        "setting_desc", setting.getDescription())
        ));
    }

    @Override
    protected LinkedHashMap<String, String> processHandTitle(LinkedHashMap<String, String> title) {
        Map<String, String> map = createPlaceholders(null);
        return title
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, (entry)
                        -> StringUtil.replacePlaceholders(entry.getValue(), map), (oldVal, newVal) -> oldVal, LinkedHashMap::new));
    }

    private Map<String, String> createPlaceholders(Integer selectedIndex) {
        Map<String, String> map = new HashMap<>();
        /* 变量部分处理 */
        final String TITLE = "setting_title_%s",  // 设置标题
                HAS = "has_setting_%s", // 存在设置
                SELECTED = "selected_setting_%s", // 选中设置
                TITLE_FORMAT = "title_format_%s"; // 设置标题格式化
        int pageOffset = (getPage() - 1) * PAGE_COUNT;
        for (int i = 0; i < PAGE_COUNT; i++) {
            int index = i + pageOffset // 设置的下标
                    , settingSort = i + 1; // 设置的顺序;
            /* 是否存在此下标的邮件 */
            boolean hasSetting = index < SETTINGS.size();
            /* 变量部分处理 */
            String papiTitle = String.format(TITLE, settingSort),
                    papiHas = String.format(HAS, settingSort),
                    papiSelected = String.format(SELECTED, settingSort),
                    papiTitleFormat = String.format(TITLE_FORMAT, settingSort);
            /* 是否存在变量 */
            map.put(papiHas, getCustomPapiText("HasSetting_" + settingSort, hasSetting));
            /* 是否选中变量 */
            map.put(papiSelected, getCustomPapiText("SelectedSetting_" + settingSort, selectedIndex != null && selectedIndex == index));
            /* Title格式化变量 */
            map.put(papiTitleFormat, getCustomPapiText("TitleFormat_" + settingSort, hasSetting));
            /* Title部分变量 */
            map.put(papiTitle, hasSetting ? SETTINGS.get(index).getTitle() : StringUtils.EMPTY);
        }
        return map;
    }

    @Override
    public void topInventoryClickEvent(InventoryClickEvent event, int slot) {
        if (!tryCooldown(slot, getPlugin().getConfigManager()
                .getYmlValue(YML_LANGUAGE,
                        "PlayerMessage.Error.ButtonCooldown"))) {
            return;
        }
        char c = getLayout().charAt(slot);
        asyncPlaySound("Icons." + c + ".Display.Sound");
        switch (c) {
            case 'A'-> backMenu();
            case 'H' -> changePage(- 1, b -> {
                asyncPlaySound(b ? "Icons." + c + ".ActiveDisplay.Sound" : "Icons." + c + ".DisabledDisplay.Sound");
                if (!b) return;
                handleMenu(getLayout());
                asyncUpdateTitle();
            });
            case 'I' -> changePage(1, b -> {
                asyncPlaySound(b ? "Icons." + c + ".ActiveDisplay.Sound" : "Icons." + c + ".DisabledDisplay.Sound");
                if (!b) return;
                handleMenu(getLayout());
                asyncUpdateTitle();
            });
            case 'J', 'K' -> clickSetting(slot, c);
        }
    }

    private void clickSetting(int slot, Character key) {
        int index = StringUtils
                .countMatches(getLayout().substring(0, slot), "J");
        if (key.equals('K')) {
            index--;
        }
        if (index < 0) return;
        int selectedIndex = (getPage() - 1) * PAGE_COUNT + index;
        SETTINGS.get(selectedIndex).getClickAction().accept(
                SettingParam.of(getPlayer(), createPlaceholders(selectedIndex), this::asyncOpenMenu));
    }
}
