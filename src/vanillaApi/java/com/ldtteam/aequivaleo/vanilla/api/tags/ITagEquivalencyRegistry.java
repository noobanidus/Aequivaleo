package com.ldtteam.aequivaleo.vanilla.api.tags;

import com.ldtteam.aequivaleo.api.IAequivaleoAPI;
import com.ldtteam.aequivaleo.api.util.TagUtils;
import com.ldtteam.aequivaleo.vanilla.api.IVanillaAequivaleoPluginAPI;
import net.minecraft.tags.Tag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Registry that allows for the registration of tags that can be used during analysis.
 */
public interface ITagEquivalencyRegistry
{
    /**
     * Gives access to the current instance of the tag equivalency registry.
     *
     * @return The tag equivalency registry.
     */
    static ITagEquivalencyRegistry getInstance() {
        return IVanillaAequivaleoPluginAPI.getInstance().getTagEquivalencyRegistry();
    }

    /**
     * Adds a given tag to the registry.
     * Allowing the tag to be used by the analysis engine during analysis, and marks all game objects included in the tag to be equal.
     *
     * @param tag The tag that indicates that a given set of game objects contained in the tag are equal to one another.
     * @return The registry with the tag added for analysis.
     */
    ITagEquivalencyRegistry addTag(@NotNull final Tag.Named<?> tag);

    /**
     * Adds a given tag to the registry, via its name.
     * Allowing the tag to be used by the analysis engine during analysis, and marks all game objects included in the tag to be equal.
     *
     * @param tagName The name of the tag that indicates that a given set of game objects contained in the tag are equal to one another.
     * @return The registry with the tag added for analysis.
     */
    default ITagEquivalencyRegistry addTag(@NotNull final ResourceLocation tagName) {
        TagUtils.get(tagName).forEach(this::addTag);
        return this;
    }
}
