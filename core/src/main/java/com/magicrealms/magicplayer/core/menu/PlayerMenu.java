package com.magicrealms.magicplayer.core.menu;

import com.magicrealms.magiclib.bukkit.utils.ItemUtil;
import com.magicrealms.magiclib.core.dispatcher.MessageDispatcher;
import com.magicrealms.magiclib.core.holder.PageMenuHolder;
import com.magicrealms.magicplayer.core.MagicPlayer;
import com.magicrealms.magicplayer.core.player.PlayerData;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.magicrealms.magicplayer.common.MagicPlayerConstant.YML_LANGUAGE;
import static com.magicrealms.magicplayer.common.MagicPlayerConstant.YML_PLAYER_MENU;

/**
 * @author Ryan-0916
 * @Desc 玩家列表
 * @date 2025-05-04
 */
@SuppressWarnings("unused")
public class PlayerMenu extends PageMenuHolder {

    /* 玩家列表 */
    private final List<PlayerData> DATA;
    /* 每页显示玩家数量 */
    private final int PAGE_NUM;

    public PlayerMenu(Player player, @NotNull List<PlayerData> data) {
        super(MagicPlayer.getInstance(), player, YML_PLAYER_MENU,
                "O######FXAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABC#####DE");
        /* 玩家数据 */
        this.DATA = data.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        /* 获取菜单布局中每页显示的玩家数量 */
        this.PAGE_NUM = StringUtils
                .countMatches(super.getLayout(), "A");
        /* 如果为空，提醒玩家 */
        if (this.DATA.isEmpty()) {
            MessageDispatcher.getInstance().sendMessage(super.getPlugin()
                   , player, super.getPlugin().getConfigManager().getYmlValue(YML_LANGUAGE,
                            "PlayerMessage.Error.NoAnyPlayer"));
            return;
        }
        super.setMaxPage(PAGE_NUM <= 0 ? 1 :
                this.DATA.size() % PAGE_NUM == 0 ?
                this.DATA.size() / PAGE_NUM : this.DATA.size() / PAGE_NUM + 1);
        asyncOpenMenu();
    }

    @Override
    protected String handleTitleMore(String title) {
        return title;
    }

    @Override
    protected void handleMenuUnCache(String layout) {
        int size =  layout.length();
        /* 当前显示的下标 */
        int appearIndex = ((super.getPage() - 1) * PAGE_NUM) - 1;
        for (int i = 0; i < size; i++){
            switch (layout.charAt(i)) {
                case 'A':
                    if (DATA.size() > ++appearIndex) {
                        setPlayerHeadSlot(i, DATA.get(appearIndex));
                        break;
                    }
                case 'B':
                case 'C':
                    super.setButtonSlot(i, !(super.getPage() > 1));
                    break;
                case 'D':
                case 'E':
                    super.setButtonSlot(i, !(super.getPage() < super.getMaxPage()));
                    break;
                default:
                    super.setItemSlot(i);
            }
        }
    }

    private void setPlayerHeadSlot(int slot, PlayerData playerData) {
        CompletableFuture.runAsync(() -> {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerData.getUniqueId());
            Map<String, String> placeholderMap = new HashMap<>();
            setItemSlot(slot, ItemUtil
                    .setItemStackByConfig(playerData.getHeadStack().clone(),
                            super.getPlugin().getConfigManager(),
                            super.getConfigPath(),
                            "Icons.A.Display",
                            placeholderMap));
        });
    }

    @Override
    public void topInventoryClickEvent(InventoryClickEvent event, int slot) {
        if (!super.getCooldownManager().tryCooldown(slot)) {
            MessageDispatcher.getInstance().sendMessage(super.getPlugin(),
                    super.getPlayer(), super.getPlugin().getConfigManager()
                            .getYmlValue(YML_LANGUAGE,
                                    "PlayerMessage.Error.ButtonCooldown"));
            return;
        }
        char c = super.getLayout().charAt(slot);
        asyncPlaySound("Icons." + c + ".Display.Sound");
        switch (c) {
            case 'X':
                asyncCloseMenu();
                break;
            case 'B':
            case 'C':
                super.changePage(- 1,b -> {
                    asyncPlaySound(b ? "Icons." + c + ".ActiveDisplay.Sound" : "Icons." + c + ".DisabledDisplay.Sound");
                    super.handleMenu(super.getLayout());
                    super.asyncUpdateTitle();
                });
                break;
            case 'D':
            case 'E':
                super.changePage(1, b -> {
                    asyncPlaySound(b ? "Icons." + c + ".ActiveDisplay.Sound" : "Icons." + c + ".DisabledDisplay.Sound");
                    super.handleMenu(super.getLayout());
                    super.asyncUpdateTitle();
                });
                break;
            case 'A':
                clickHead(event, slot);
                break;
        }
    }

    private void clickHead(InventoryClickEvent event, int slot) {
//        int count = StringUtil.countMatches(super.getLayout(), "A");
//        int index = StringUtil.countMatches(super.getLayout().substring(0, clickedSlot), "A");
//        MagicPlayerData playerData = data.get((super.getPage() - 1) * count + index);
//        PlayerMenuClickInfo clickInfo = e.isShiftClick() ? e.isLeftClick() ? shiftLeftClickInfo : shiftRightClickInfo : e.isLeftClick() ? leftClickInfo : rightClickInfo;
//        if (clickInfo == null) {
//            return;
//        }
//        super.getPlayer().closeInventory();
//        clickInfo.getClickCallback().accept(new PlayerMenuClickData(super.getPlayer(), playerData));
    }
}
