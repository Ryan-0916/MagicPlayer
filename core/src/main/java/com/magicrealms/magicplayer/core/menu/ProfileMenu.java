package com.magicrealms.magicplayer.core.menu;

import com.magicrealms.magiclib.common.utils.StringUtil;
import com.magicrealms.magiclib.core.holder.BaseMenuHolder;
import com.magicrealms.magiclib.core.utils.ItemUtil;
import com.magicrealms.magiclib.core.utils.PlaceholderUtil;
import com.magicrealms.magicplayer.common.player.DailyPlayerSession;
import com.magicrealms.magicplayer.common.util.PlayerSessionUtil;
import com.magicrealms.magicplayer.core.MagicPlayer;
import com.magicrealms.magicplayer.core.entity.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
    private final DailyPlayerSession PROFILE_SESSION;

    public ProfileMenu(Player player, PlayerData profileData) {
        super(MagicPlayer.getInstance(), player, YML_PROFILE_MENU,
                "AE#######BF####QR#CG####RR#DH#######");
        Objects.requireNonNull(profileData);
        this.PROFILE_DATA = profileData;
        this.PROFILE_SESSION = PlayerSessionUtil
                .getPlayerSession(MagicPlayer.getInstance().getRedisStore(),
                        profileData.getName()).orElse(null);
        asyncOpenMenu();
    }

    @Override
    protected LinkedHashMap<String, String> initTitle() {
        OfflinePlayer player = Bukkit.getOfflinePlayer(PROFILE_DATA.getUniqueId());
        return getPlugin().getConfigManager()
                .getYmlSubKeys(getConfigPath(), "Title", false)
                .map(keys -> keys.stream()
                        .collect(Collectors.toMap(
                                key -> key,
                                key -> PlaceholderUtil.replacePlaceholders(
                                        getPlugin().getConfigManager()
                                                .getYmlValue(getConfigPath(), String.format(TITLE_TEXT_PATH, key)),
                                        player
                                ),
                                (oldVal, newVal) -> oldVal,  // 合并函数（避免重复键冲突）
                                LinkedHashMap::new           // 指定使用 LinkedHashMap
                        )))
                .orElseGet(LinkedHashMap::new);  // 默认也用 LinkedHashMap
    }

    @Override
    protected void handleMenu(String layout) {
        int size =  layout.length();
        for (int i = 0; i < size; i++){
            switch (layout.charAt(i)) {
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
                                MagicPlayer.getInstance().getConfigManager(), YML_PROFILE_MENU,
                                PROFILE_SESSION == null
                                        || !PROFILE_SESSION.isOnline() ? "Icons.I.OfflineDisplay" :
                                        PROFILE_SESSION.isAfk()
                                        ? "Icons.I.AfkDisplay"
                                        : "Icons.I.OnlineDisplay"));
                case 'Q', 'R' -> setHead(i, layout.charAt(i));
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
    protected LinkedHashMap<String, String> handleTitle(LinkedHashMap<String, String> title) {
        return title
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, (entry)
                        -> StringUtil.replacePlaceholders(entry.getValue(), createPlaceholders()), (oldVal, newVal) -> oldVal, LinkedHashMap::new));
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
        return MagicPlayer.getInstance().getConfigManager()
                .getYmlValue(YML_PROFILE_MENU, String.format(CUSTOM_PAPI_PATH, path, enabled ? "Enable" : "UnEnable"));
    }


}
