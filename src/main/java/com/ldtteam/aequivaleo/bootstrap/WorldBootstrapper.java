package com.ldtteam.aequivaleo.bootstrap;

import com.google.common.collect.Sets;
import com.ldtteam.aequivaleo.analyzer.EquivalencyRecipeRegistry;
import com.ldtteam.aequivaleo.api.compound.CompoundInstance;
import com.ldtteam.aequivaleo.api.compound.container.ICompoundContainer;
import com.ldtteam.aequivaleo.api.compound.information.ICompoundInformationRegistry;
import com.ldtteam.aequivaleo.api.util.ModRegistries;
import com.ldtteam.aequivaleo.compound.container.registry.CompoundContainerFactoryManager;
import com.ldtteam.aequivaleo.compound.information.CompoundInformationRegistry;
import com.ldtteam.aequivaleo.instanced.InstancedEquivalencyHandlerRegistry;
import com.ldtteam.aequivaleo.plugin.PluginManger;
import com.ldtteam.aequivaleo.recipe.equivalency.InstancedEquivalency;
import com.ldtteam.aequivaleo.recipe.equivalency.TagEquivalencyRecipe;
import com.ldtteam.aequivaleo.vanilla.tags.TagEquivalencyRegistry;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.Tag;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.StreamSupport;

@SuppressWarnings("rawtypes")
public final class WorldBootstrapper
{

    private static final Logger LOGGER = LogManager.getLogger();

    private WorldBootstrapper()
    {
        throw new IllegalStateException("Tried to initialize: WorldBootstrapper but this is a Utility class.");
    }

    public static void onWorldReload(final ServerLevel world)
    {
        resetDataForWorld(world);

        doBootstrapTagInformation(world);
        doBootstrapInstancedEquivalencies(world);

        doHandleCompoundTypeWrappers(world);

        doHandlePluginLoad(world);
    }

    private static void resetDataForWorld(final Level world)
    {
        CompoundInformationRegistry.getInstance(world.dimension()).reset();
        EquivalencyRecipeRegistry.getInstance(world.dimension()).reset();
    }

    private static void doBootstrapTagInformation(final Level world)
    {
        for (Tag.Named<?> tag : TagEquivalencyRegistry.getInstance().get())
        {
            doBootstrapSingleTagInformation(world, tag);
        }
    }

    private static <T> void doBootstrapSingleTagInformation(final Level world, final Tag.Named<T> tag) {
        final ICompoundContainer<Tag.Named> tagContainer = CompoundContainerFactoryManager.getInstance().wrapInContainer(tag, 1d);


        final Collection<ICompoundContainer<?>> elementsOfTag = new ArrayList<>();
        for (T stack : tag.getValues())
        {
            ICompoundContainer<T> tiCompoundContainer = CompoundContainerFactoryManager.getInstance().wrapInContainer(stack, 1d);
            elementsOfTag.add(tiCompoundContainer);
        }

        for (ICompoundContainer<?> inputStack : elementsOfTag)
        {
            EquivalencyRecipeRegistry.getInstance(world.dimension())
              .register(
                new TagEquivalencyRecipe<>(
                  tag,
                  tagContainer,
                  inputStack
                ));

            EquivalencyRecipeRegistry.getInstance(world.dimension())
              .register(
                new TagEquivalencyRecipe<>(
                  tag,
                  inputStack,
                  tagContainer
                ));
        }
    }

    private static void doBootstrapInstancedEquivalencies(
      @NotNull final ServerLevel world
    ) {
        StreamSupport.stream(ForgeRegistries.ITEMS.spliterator(), true).forEach(item -> InstancedEquivalencyHandlerRegistry.getInstance().process(
          item,
          o -> {
              final ICompoundContainer<?> sourceContainer = CompoundContainerFactoryManager.getInstance().wrapInContainer(item, 1);
              final ICompoundContainer<?> targetContainer = CompoundContainerFactoryManager.getInstance().wrapInContainer(o, 1);

              try {
                  EquivalencyRecipeRegistry.getInstance(world.dimension())
                    .register(new InstancedEquivalency(
                      sourceContainer, targetContainer
                    ))
                    .register(new InstancedEquivalency(
                      targetContainer, sourceContainer
                    ));
              } catch (Exception ex) {
                  LOGGER.error(String.format("Failed to register equivalency between: %s and: %s",
                    item.getRegistryName(),
                    o), ex);
              }
          },
          consumer -> {
              final NonNullList<ItemStack> group = NonNullList.create();
              if (item.getItemCategory() == null)
                  return;

              item.fillItemCategory(Objects.requireNonNull(item.getItemCategory()), group);

              if (group.size() == 1)
              {
                  consumer.accept(group.get(0));
              }
          }
        ));

        StreamSupport.stream(ForgeRegistries.FLUIDS.spliterator(), true).forEach(fluid -> InstancedEquivalencyHandlerRegistry.getInstance().process(
          fluid,
          o -> {
              final ICompoundContainer<?> sourceContainer = CompoundContainerFactoryManager.getInstance().wrapInContainer(fluid, 1);
              final ICompoundContainer<?> targetContainer = CompoundContainerFactoryManager.getInstance().wrapInContainer(o, 1);

              try {
                  EquivalencyRecipeRegistry.getInstance(world.dimension())
                    .register(new InstancedEquivalency(
                      sourceContainer, targetContainer
                    ))
                    .register(new InstancedEquivalency(
                      targetContainer, sourceContainer
                    ));
              } catch (Exception ex) {
                  LOGGER.error(String.format("Failed to register equivalency between: %s and: %s",
                    fluid.getRegistryName(),
                    o), ex);
              }
          },
          consumer -> {
              if (fluid.isSame(Fluids.EMPTY))
                  return;

              consumer.accept(new FluidStack(fluid, 1));
          }
        ));
    }

    private static void doHandleCompoundTypeWrappers(
      @NotNull final ServerLevel world) {
        LOGGER.info(String.format("Setting up compound type instantiations: %s", world.dimension().location()));
        ModRegistries.COMPOUND_TYPE.forEach(type -> ICompoundInformationRegistry.getInstance(world.dimension())
          .registerLocking(type, Sets.newHashSet(new CompoundInstance(type, 1))));
    }

    private static void doHandlePluginLoad(
      @NotNull final ServerLevel world) {
        LOGGER.info(String.format("Invoking plugin callbacks: %s", world.dimension().location()));
        PluginManger.getInstance().run(plugin -> plugin.onReloadStartedFor(world));
    }
}
