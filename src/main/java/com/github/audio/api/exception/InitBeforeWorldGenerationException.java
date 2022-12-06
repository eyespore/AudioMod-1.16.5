package com.github.audio.api.exception;

/**
 * @Description: This exception might be caused because this method call some of those values such as
 * {@value world} or {@value player}, and these value will be null before players get into the world and the
 * world finished generation, so if you try to call this method before a player join into the world you may
 * be more cautious as it can cause a {@link NullPointerException}.
 */
public class InitBeforeWorldGenerationException extends Exception{

    private static final String world = "Minecraft.getInstance().world";
    private static final String player = "Minecraft.getInstance().player";
    private static final String MSG = "Check if those values are null : ";
    public final String reason;

    public InitBeforeWorldGenerationException(String reason) {
        super(MSG + reason);
        this.reason = reason;
    }


}
