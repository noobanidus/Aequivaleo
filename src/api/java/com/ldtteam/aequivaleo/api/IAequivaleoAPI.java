package com.ldtteam.aequivaleo.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ldtteam.aequivaleo.api.analysis.AnalysisState;
import com.ldtteam.aequivaleo.api.compound.container.registry.ICompoundContainerFactoryManager;
import com.ldtteam.aequivaleo.api.compound.information.ICompoundInformationRegistry;
import com.ldtteam.aequivaleo.api.gameobject.equivalent.IGameObjectEquivalencyHandlerRegistry;
import com.ldtteam.aequivaleo.api.instanced.IInstancedEquivalencyHandlerRegistry;
import com.ldtteam.aequivaleo.api.plugin.IAequivaleoPluginManager;
import com.ldtteam.aequivaleo.api.recipe.IRecipeTypeProcessingRegistry;
import com.ldtteam.aequivaleo.api.recipe.equivalency.IEquivalencyRecipeRegistry;
import com.ldtteam.aequivaleo.api.recipe.equivalency.calculator.IRecipeCalculator;
import com.ldtteam.aequivaleo.api.recipe.equivalency.ingredient.data.IIngredientSerializerRegistry;
import com.ldtteam.aequivaleo.api.results.IEquivalencyResults;
import com.ldtteam.aequivaleo.api.results.IResultsAdapterHandlerRegistry;
import com.ldtteam.aequivaleo.api.results.IResultsInformationCache;
import com.ldtteam.aequivaleo.api.util.Constants;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;

/**
 * The API for aequivaleo.
 * Retrieved via an IMC with a callback or via its {@link #getInstance()} method.
 */
public interface IAequivaleoAPI
{

    /**
     * Returns the instance of the api, once it has been initialized.
     * (Initialization happens during mod construction)
     *
     * @return The api.
     */
    static IAequivaleoAPI getInstance() {
        return IAequivaleoAPI.Holder.getInstance();
    }

    /**
     * Gives access to the registry that handles the callbacks that convert game objects to their wrapped instances.
     * @return The registry that handles the callbacks used to convert game objects into wrapped counterparts.
     */
    ICompoundContainerFactoryManager getCompoundContainerFactoryManager();

    /**
     * Gives access to a registry which handles the registration of callbacks which can tell the system if two objects are equal to one another.
     *
     * @return The registry which handles callbacks for equivalency checks.
     */
    IGameObjectEquivalencyHandlerRegistry getGameObjectEquivalencyHandlerRegistry();

    /**
     * Gives access to the registry that holds the recipe information for a given world.
     * @param worldKey The world key to get the equivalency recipe information for.
     * @return The recipe registry for a given world.
     */
    IEquivalencyRecipeRegistry getEquivalencyRecipeRegistry(@NotNull final ResourceKey<Level> worldKey);

    /**
     * Gives access to the registry that contains the locking information for game objects and wrappers in a given world.
     *
     * @param worldKey The world key that represents the world for which locking information registry is being retrieved.
     * @return The registry for locking type information for a given world.
     */
    ICompoundInformationRegistry getLockedCompoundWrapperToTypeRegistry(@NotNull final ResourceKey<Level> worldKey);

    /**
     * Gives access to the cache that contains the equivalency information after calculation.
     *
     * @param worldKey The world key to get the equivalency cache for.
     * @return The equivalency cache for a given dimension.
     */
    @Deprecated
    IResultsInformationCache getResultsInformationCache(@NotNull final ResourceKey<Level> worldKey);

    /**
     * Gives access to the results that contains the equivalency information after calculation.
     *
     * @param worldKey The world key to get the equivalency results for.
     * @return The equivalency results for a given dimension.
     */
    IEquivalencyResults getEquivalencyResults(ResourceKey<Level> worldKey);

    /**
     * Gives access to the aequivaleo plugin manager.
     *
     * @return The plugin manager.
     */
    IAequivaleoPluginManager getPluginManager();

    /**
     * Gives access to the recipe calculator.
     *
     * @return The recipe calculator.
     */
    IRecipeCalculator getRecipeCalculator();

    /**
     * Sets up a new Gson instance and create the serialization handler.
     *
     * @return The Gson serialization handler.
     */
    default Gson getGson() {
        return setupGson()
                 .setPrettyPrinting()
                 .create();
    }

    /**
     * Setups a new {@link GsonBuilder} to use with Aequivaleos serializers.
     *
     * @return The new {@link GsonBuilder} setup to be used with Aequivaleo.
     */
    default GsonBuilder setupGson() {
        return setupGson(new GsonBuilder());
    }

    /**
     * Allows for Aequivaleo to inject its serialization handlers into the given {@link GsonBuilder}.
     *
     * @param builder The builder to inject serializers into.
     * @return The builder with the serializers setup as type adapters.
     */
    GsonBuilder setupGson(GsonBuilder builder);

    /**
     * Gives access to the recipe type processing registry.
     *
     * @return The recipe type processing registry.
     */
    IRecipeTypeProcessingRegistry getRecipeTypeProcessingRegistry();

    /**
     * Gives access to the instanced equivalency handler registry.
     *
     * @return The instanced equivalency handler registry.
     */
    IInstancedEquivalencyHandlerRegistry getInstancedEquivalencyHandlerRegistry();

    /**
     * Gives access to the results adapter handler registry.
     *
     * @return The results adapter handler registry.
     */
    IResultsAdapterHandlerRegistry getResultsAdapterHandlerRegistry();

    /**
     * The registry which managers serializers for ingredients.
     *
     * @return The ingredient serializer registry.
     */
    IIngredientSerializerRegistry getIngredientSerializerRegistry();

    /**
     * Returns the aequivaleo mod container.
     * Allows for the creation of configurations in the name of aequivaleo by its plugins.
     *
     * @return The aequivaleo mod container.
     */
    default ModContainer getAequivaleoContainer() {
        return ModList.get().getModContainerById(Constants.MOD_ID).orElseThrow(()->new RuntimeException("Where is Aequivaleo???!"));
    }

    /**
     * Gives access to the current state of the analysis engine for a given world.
     *
     * @param key The registry key of the world to look up.
     * @return The current state.
     */
    AnalysisState getState(final ResourceKey<Level> key);

    class Holder {
        private static IAequivaleoAPI apiInstance;

        public static IAequivaleoAPI getInstance()
        {
            return apiInstance;
        }

        public static void setInstance(final IAequivaleoAPI instance)
        {
            if (apiInstance != null)
                throw new IllegalStateException("Can not setup API twice!");

            apiInstance = instance;
        }
    }
}
