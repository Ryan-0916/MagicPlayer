package com.magicrealms.magicplayer.core.menu;

import com.magicrealms.magiclib.common.utils.StringUtil;
import com.magicrealms.magiclib.core.holder.BaseMenuHolder;
import com.magicrealms.magiclib.core.utils.ItemUtil;
import com.magicrealms.magiclib.core.utils.PlaceholderUtil;
import com.magicrealms.magicplayer.common.player.PlayerSession;
import com.magicrealms.magicplayer.common.storage.PlayerSessionStorage;
import com.magicrealms.magicplayer.core.BukkitMagicPlayer;
import com.magicrealms.magicplayer.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.magicrealms.magicplayer.common.MagicPlayerConstant.YML_LANGUAGE;
import static com.magicrealms.magicplayer.common.MagicPlayerConstant.YML_PROFILE_MENU;

/**
 * @author Ryan-0916
 * @Desc 玩家资料卡
 * @date 2025-05-08
 */
@SuppressWarnings("unused")
public class ProfileMenu extends BaseMenuHolder {
    /* 自定义变量相关 */
    private final static String CUSTOM_PAPI_PATH = "CustomPapi.%s.%s";
    /* 资料卡者信息 */
    private final PlayerData PROFILE_DATA;
    /* 资料卡者今日信息  */
    private final PlayerSession PROFILE_SESSION;

    public ProfileMenu(Player player, PlayerData profileData) {
        super(BukkitMagicPlayer.getInstance(), player, YML_PROFILE_MENU,
                "AE#######BF####QR#CG####RR#DH#######");
        Objects.requireNonNull(profileData);
        this.PROFILE_DATA = profileData;
        this.PROFILE_SESSION = PlayerSessionStorage
                .getPlayerSession(BukkitMagicPlayer.getInstance().getRedisStore(),
                        profileData.getName()).orElse(null);
        asyncOpenMenu();
    }

    @Override
    protected void handleMenu(String layout) {
        int size =  layout.length();
        for (int i = 0; i < size; i++){
            char c = layout.charAt(i);
            switch (c) {
                case 'A' -> super.setItemSlot(i, PROFILE_DATA.getArmor().getHelmet());
                case 'B' -> super.setItemSlot(i, PROFILE_DATA.getArmor().getChestplate());
                case 'C' -> super.setItemSlot(i, PROFILE_DATA.getArmor().getLeggings());
                case 'D' -> super.setItemSlot(i, PROFILE_DATA.getArmor().getBoots());
                case 'E' -> super.setItemSlot(i, PROFILE_DATA.getCosmetic().getHat());
                case 'F' -> super.setItemSlot(i, PROFILE_DATA.getCosmetic().getBag());
                case 'G' -> super.setItemSlot(i, PROFILE_DATA.getCosmetic().getWalkingStick());
                case 'H' -> super.setItemSlot(i, PROFILE_DATA.getCosmetic().getBalloon());
                case 'I' -> super.setItemSlot(i, ItemUtil
                        .getItemStackByConfig(
                                BukkitMagicPlayer.getInstance().getConfigManager(), YML_PROFILE_MENU,
                                PROFILE_SESSION == null
                                        || !PROFILE_SESSION.isOnline() ? "Icons.I.OfflineDisplay" :
                                        PROFILE_SESSION.isAfk()
                                        ? "Icons.I.AfkDisplay"
                                        : "Icons.I.OnlineDisplay"));
                case 'Q', 'R' -> setHead(i, c);
                default -> super.setItemSlot(i);
            }
        }
    }

    private void setHead(int slot, Character key) {
        CompletableFuture.runAsync(() -> {
            OfflinePlayer player = Bukkit.getOfflinePlayer(PROFILE_DATA.getUniqueId());
            String path = String.format("Icons.%s.Display", key);
            ItemStack itemStack = key.equals('Q') ?
                    ItemUtil.setItemStackByConfig(PROFILE_DATA.getHeadStack().clone(),
                            super.getPlugin().getConfigManager(),
                            super.getConfigPath(), path
                            , player) :
                    ItemUtil.getItemStackByConfig(super.getPlugin().getConfigManager(),
                    super.getConfigPath(), path
                    , player);
            setItemSlot(slot, itemStack);
        });
    }

    @Override
    protected LinkedHashMap<String, String> buildTitle(LinkedHashMap<String, String> title) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(PROFILE_DATA.getUniqueId());
        return title.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> PlaceholderUtil.replacePlaceholders(entry.getValue(), player),
                        (oldVal, newVal) -> newVal,  // 处理 key 冲突（可选）
                        LinkedHashMap::new            // 保持顺序
                ));
    }

    @Override
    protected LinkedHashMap<String, String> handleTitle(LinkedHashMap<String, String> title) {
        Map<String, String> map = createPlaceholders();
        return title
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, (entry)
                        -> StringUtil.replacePlaceholders(entry.getValue(), map), (oldVal, newVal) -> oldVal, LinkedHashMap::new));
    }

    private Map<String, String> createPlaceholders() {
        return Map.of("helmet",
                this.getCustomPapi("Helmet", PROFILE_DATA.getArmor().isEquippedHelmet()),
                "chestplate",
                this.getCustomPapi("Chestplate", PROFILE_DATA.getArmor().isEquippedChestplate()),
                "leggings",
                this.getCustomPapi("Leggings", PROFILE_DATA.getArmor().isEquippedLeggings()),
                "boots",
                this.getCustomPapi("Boots", PROFILE_DATA.getArmor().isEquippedBoots()),
                "hat",
                this.getCustomPapi("Hat", PROFILE_DATA.getCosmetic().isEquippedHat()),
                "bag",
                this.getCustomPapi("Bag", PROFILE_DATA.getCosmetic().isEquippedBag()),
                "walking_stick",
                this.getCustomPapi("WalkingStick", PROFILE_DATA.getCosmetic().isEquippedWalkingStick()),
                "balloon",
                this.getCustomPapi("Balloon", PROFILE_DATA.getCosmetic().isEquippedBalloon())
        );
    }

    private String getCustomPapi(String path, boolean enabled) {
        return BukkitMagicPlayer.getInstance().getConfigManager()
                .getYmlValue(YML_PROFILE_MENU, String.format(CUSTOM_PAPI_PATH, path, enabled ? "Enable" : "UnEnable"));
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
            case 'S'-> new SettingMenu(getPlayer(), this::asyncOpenMenu);
            case 'A' -> System.out.println(StringUtil.EMPTY);
        }
    }
}
