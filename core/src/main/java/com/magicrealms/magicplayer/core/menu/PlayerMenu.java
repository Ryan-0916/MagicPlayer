package com.magicrealms.magicplayer.core.menu;

import com.magicrealms.magiclib.bukkit.message.helper.AdventureHelper;
import com.magicrealms.magiclib.bukkit.utils.ItemUtil;
import com.magicrealms.magiclib.common.utils.StringUtil;
import com.magicrealms.magiclib.core.dispatcher.MessageDispatcher;
import com.magicrealms.magiclib.core.holder.PageMenuHolder;
import com.magicrealms.magicplayer.core.MagicPlayer;
import com.magicrealms.magicplayer.core.player.PlayerData;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private final int PAGE_COUNT;
    /* 鼠标左键点击事件 */
    private final ClickAction LEFT_ACTION;
    /* 鼠标右键点击事件 */
    private final ClickAction RIGHT_ACTION;
    /* 鼠标 SHIFT + 左键 点击事件 */
    private final ClickAction SHIFT_LEFT_ACTION;
    /* 鼠标 SHIFT + 右键 点击事件 */
    private final ClickAction SHIFT_RIGHT_ACTION;
    /* Lore */
    private final List<Component> CLICK_LORE;

    public PlayerMenu(Builder builder) {
        super(MagicPlayer.getInstance(), builder.player, YML_PLAYER_MENU,
                "O######FXAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABC#####DE");
        /* 玩家数据 */
        this.DATA = builder.data.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        /* 左键点击事件 */
        this.LEFT_ACTION = builder.leftAction;
        /* 右键点击事件 */
        this.RIGHT_ACTION = builder.rightAction;
        /* SHIFT + 左键点击事件 */
        this.SHIFT_LEFT_ACTION = builder.shiftLeftAction;
        /* SHIFT + 右键点击事件 */
        this.SHIFT_RIGHT_ACTION = builder.shiftRightAction;
        /* 点击 Lore */
        this.CLICK_LORE = setupClickLore();
        /* 获取菜单布局中每页显示的玩家数量 */
        this.PAGE_COUNT = StringUtils
                .countMatches(super.getLayout(), "A");
        /* 如果为空，提醒玩家 */
        if (this.DATA.isEmpty()) {
            MessageDispatcher.getInstance().sendMessage(super.getPlugin()
                    , builder.player, super.getPlugin().getConfigManager().getYmlValue(YML_LANGUAGE,
                            "PlayerMessage.Error.NoAnyPlayer"));
            return;
        }
        super.setMaxPage(PAGE_COUNT <= 0 ? 1 :
                this.DATA.size() % PAGE_COUNT == 0 ?
                        this.DATA.size() / PAGE_COUNT : this.DATA.size() / PAGE_COUNT + 1);
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
        int appearIndex = ((super.getPage() - 1) * PAGE_COUNT) - 1;
        for (int i = 0; i < size; i++){
            switch (layout.charAt(i)) {
                case 'A':
                    if (DATA.size() > ++appearIndex) {
                        setHead(i, DATA.get(appearIndex));
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

    private void setHead(int slot, PlayerData playerData) {
        CompletableFuture.runAsync(() -> {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerData.getUniqueId());
            ItemStack head = ItemUtil.setItemStackByConfig(playerData.getHeadStack().clone(),
                    super.getPlugin().getConfigManager(),
                    super.getConfigPath(),
                    "Icons.A.Display",
                    offlinePlayer);
            ItemMeta meta = head.getItemMeta();
            meta.lore(Optional.ofNullable(head.getItemMeta().lore())
                    .map(lore -> {
                        lore.addAll(CLICK_LORE);
                        return lore;
                    }).orElse(CLICK_LORE));
            head.setItemMeta(meta);
            setItemSlot(slot, head);
        });
    }

    private String getDescribe(ClickAction clickAction) {
        return Optional.ofNullable(clickAction)
                .map(ClickAction::describe)
                .orElse(StringUtils.EMPTY);
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

    private List<Component> setupClickLore() {
        List<Component> components = new ArrayList<>();
        Stream.of(new AbstractMap.SimpleEntry<>("ClickLore.Left", LEFT_ACTION),
                        new AbstractMap.SimpleEntry<>("ClickLore.Right", RIGHT_ACTION),
                        new AbstractMap.SimpleEntry<>("ClickLore.ShiftLeft", SHIFT_LEFT_ACTION),
                        new AbstractMap.SimpleEntry<>("ClickLore.ShiftRight", SHIFT_RIGHT_ACTION)
                )
                .filter(entry -> entry.getValue() != null)
                .forEach(entry ->
                        getPlugin().getConfigManager().getYmlListValue(getConfigPath(), entry.getKey())
                        .map(lore -> lore.stream()
                                .map(l -> ItemUtil.UN_ITALIC.append(AdventureHelper.deserializeComponent(
                                        StringUtil.replacePlaceholder(l, "desc", getDescribe(entry.getValue())))
                                ))
                                .collect(Collectors.toList())
                        )
                        .ifPresent(components::addAll));
        return components;
    }

    private void clickHead(InventoryClickEvent event, int slot) {
        int index = StringUtils.countMatches(super.getLayout().substring(0, slot), "A");
        PlayerData playerData = DATA.get((super.getPage() - 1) * PAGE_COUNT + index);
        ClickAction clickAction = event.isShiftClick() ? event.isLeftClick() ? SHIFT_LEFT_ACTION : SHIFT_RIGHT_ACTION : event.isLeftClick() ? LEFT_ACTION : RIGHT_ACTION;
        Optional.ofNullable(clickAction).ifPresent(
                action -> {
                    super.asyncCloseMenu();
                    action.handler().accept(ClickHandler.of(super.getPlayer(), playerData));
                }
        );
    }

    /**
     * @author Ryan-0916
     * @Desc 玩家菜单构造器
     * @date 2025-05-06
     */
    public static class Builder {

        /* 鼠标左键点击事件 */
        private ClickAction leftAction;
        /* 鼠标右键点击事件 */
        private ClickAction rightAction;
        /* 鼠标 SHIFT + 左键 点击事件 */
        private ClickAction shiftLeftAction;
        /* 鼠标 SHIFT + 右键 点击事件 */
        private ClickAction shiftRightAction;
        /* 玩家数据 */
        private List<PlayerData> data;
        /* 菜单查看者 */
        private Player player;

        public Builder leftAction(ClickAction action) {
            this.leftAction = action;
            return this;
        }

        public Builder shiftLeftAction(ClickAction action) {
            this.shiftLeftAction = action;
            return this;
        }

        public Builder rightAction(ClickAction action) {
            this.rightAction = action;
            return this;
        }

        public Builder shiftRightAction(ClickAction action) {
            this.shiftRightAction = action;
            return this;
        }

        public Builder player(Player player) {
            this.player = player;
            return this;
        }

        public Builder data(List<PlayerData> data) {
            this.data = data;
            return this;
        }

        public void open() {
            if (player == null || data == null )
                throw new NullPointerException("构建玩家菜单时出现未知异常，请填写必填参数");
            new PlayerMenu(this);
        }
    }
}
