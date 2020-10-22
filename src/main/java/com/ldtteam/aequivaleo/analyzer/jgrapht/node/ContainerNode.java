package com.ldtteam.aequivaleo.analyzer.jgrapht.node;

import com.ldtteam.aequivaleo.analyzer.StatCollector;
import com.ldtteam.aequivaleo.analyzer.jgrapht.aequivaleo.*;
import com.ldtteam.aequivaleo.analyzer.jgrapht.core.IAnalysisGraphNode;
import com.ldtteam.aequivaleo.analyzer.jgrapht.core.IAnalysisNodeWithContainer;
import com.ldtteam.aequivaleo.api.compound.CompoundInstance;
import com.ldtteam.aequivaleo.api.compound.container.ICompoundContainer;
import com.ldtteam.aequivaleo.api.recipe.equivalency.IEquivalencyRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ContainerNode extends AbstractNode implements IContainerNode, IRecipeResidueNode, IRecipeOutputNode
{
    @NotNull
    private final ICompoundContainer<?> wrapper;

    public ContainerNode(@NotNull final ICompoundContainer<?> wrapper) {this.wrapper = wrapper;}

    @NotNull
    public Optional<ICompoundContainer<?>> getWrapper()
    {
        return Optional.of(wrapper);
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof ContainerNode))
        {
            return false;
        }

        final ContainerNode that = (ContainerNode) o;

        return getWrapper().equals(that.getWrapper());
    }

    @Override
    public void addCandidateResult(final INode neighbor, final IEdge sourceEdge, final Set<CompoundInstance> instances)
    {
        super.addCandidateResult(neighbor, sourceEdge, instances.stream().filter(i -> i.getType().getGroup().isValidFor(wrapper, i)).collect(Collectors.toSet()));
    }

    @Override
    public int hashCode()
    {
        return getWrapper().hashCode();
    }

    @Override
    public String toString()
    {
        return "ContainerNode{" +
                  wrapper +
                 '}';
    }

    @Override
    public void collectStats(final StatCollector statCollector)
    {
        statCollector.onVisitContainerNode();
    }

    @Override
    public Set<CompoundInstance> getResidueInstances(final IRecipeNode recipeNode)
    {
        return getResultingValue().orElse(Collections.emptySet());
    }
}
