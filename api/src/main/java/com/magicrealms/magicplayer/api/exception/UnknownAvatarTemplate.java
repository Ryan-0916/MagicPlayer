package com.magicrealms.magicplayer.api.exception;

/**
 * @author Ryan-0916
 * @Desc 未知头像模板异常
 * @date 2025-05-13
 */
@SuppressWarnings("unused")
public class UnknownAvatarTemplate extends RuntimeException {

    public UnknownAvatarTemplate() {
        super();
    }

    public UnknownAvatarTemplate(String message) {
        super(message);
    }

    public UnknownAvatarTemplate(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownAvatarTemplate(Throwable cause) {
        super(cause);
    }

}
