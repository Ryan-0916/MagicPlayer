package com.magicrealms.magicplayer.core.menu;

import com.magicrealms.magiclib.common.utils.StringUtil;
import com.magicrealms.magiclib.core.dispatcher.MessageDispatcher;
import com.magicrealms.magiclib.core.holder.BaseMenuHolder;
import com.magicrealms.magiclib.core.utils.ItemUtil;
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
    /* 查看者本人 */
    private final boolean IS_HOLDER;
    /* 查看者基本信息  */
    private final PlayerData HOLDER_DATA;
    /* 资料卡者信息 */
    private final PlayerData PROFILE_DATA;
    /* 查看者今日信息  */
    private final DailyPlayerSession HOLDER_SESSION;
    /* 资料卡者今日信息  */
    private final DailyPlayerSession PROFILE_SESSION;

    public ProfileMenu(Player player, PlayerData profileData) {
        super(MagicPlayer.getInstance(), player, YML_PROFILE_MENU,
                "AE#######BF####QR#CG####RR#DH#######");
        Objects.requireNonNull(profileData);
        this.IS_HOLDER = player.getUniqueId().equals(profileData.getUniqueId());
        this.PROFILE_DATA = profileData;
        this.HOLDER_DATA = MagicPlayer.getInstance()
                .getPlayerDataRepository().queryByPlayer(player);
        this.HOLDER_SESSION = PlayerSessionUtil
                .getPlayerSession(MagicPlayer.getInstance().getRedisStore(),
                        player.getName()).orElse(null);
        this.PROFILE_SESSION = PlayerSessionUtil
                .getPlayerSession(MagicPlayer.getInstance().getRedisStore(),
                        profileData.getName()).orElse(null);
        if (HOLDER_SESSION == null) {
            MessageDispatcher.getInstance().sendMessage(super.getPlugin()
                    , player, super.getPlugin().getConfigManager().getYmlValue(YML_LANGUAGE,
                            "PlayerMessage.Error.UnKnow"));
            return;
        }
        asyncOpenMenu();
//        this.time = System.currentTimeMillis();
//        this.setting = MagicPlayerSettingUtil.getMagicPlayerSetting(data.getName());
//        this.localSetting = MagicPlayerLocalSettingUtil.getMagicPlayerLocalSetting(data.getName(), player.getUniqueId());
//        final OfflinePlayer offlinePlayer = isHolder ? player : Bukkit.getOfflinePlayer(data.getUniqueId());
//        placeholderMap.put("money", StringUtil.replacePlaceholder("%xconomy_balance_value%", offlinePlayer));
//        placeholderMap.put("name", data.getName());
//        placeholderMap.put("friend_number", String.valueOf(data.getFriendNumber()));
//        placeholderMap.put("email", data.getEmail() != null ? data.getEmail() : "未绑定邮箱");
//        Map<String, String> registerTimeMap = DateTimeUtil.getFormatDateTime(data.getRegisterTime());
//        placeholderMap.put("register_time_yyyy", registerTimeMap.getOrDefault("yyyy", "????"));
//        placeholderMap.put("register_time_yy", registerTimeMap.getOrDefault("yy", "??"));
//        placeholderMap.put("register_time_MM", registerTimeMap.getOrDefault("MM", "??"));
//        placeholderMap.put("register_time_dd", registerTimeMap.getOrDefault("dd", "??"));
//        placeholderMap.put("register_time_HH", registerTimeMap.getOrDefault("HH", "??"));
//        placeholderMap.put("register_time_mm", registerTimeMap.getOrDefault("mm", "??"));
//        placeholderMap.put("register_time_ss", registerTimeMap.getOrDefault("ss", "??"));
//        Map<String, String> playtimeMap = DateTimeUtil.getFormatMillisecond(data.getPlaytime() + (magicData == null || magicData.isOffline() ? 0 : time - magicData.getUpTime()));
//        placeholderMap.put("playtime_HH", playtimeMap.getOrDefault("HH", "00"));
//        placeholderMap.put("playtime_mm", playtimeMap.getOrDefault("mm", "00"));
//        placeholderMap.put("playtime_ss", playtimeMap.getOrDefault("ss", "00"));
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
