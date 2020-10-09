package com.ldtteam.aequivaleo.analyzer.jgrapht.node;

import com.ldtteam.aequivaleo.analyzer.StatCollector;
import com.ldtteam.aequivaleo.api.compound.container.ICompoundContainer;
import org.jetbrains.annotations.NotNull;

public class ContainerWrapperGraphNode extends AbstractAnalysisGraphNode
{
    @NotNull
    private final ICompoundContainer<?> wrapper;

    public ContainerWrapperGraphNode(@NotNull final ICompoundContainer<?> wrapper) {this.wrapper = wrapper;}

    @NotNull
    public ICompoundContainer<?> getWrapper()
    {
        return wrapper;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof ContainerWrapperGraphNode))
        {
            return false;
        }

        final ContainerWrapperGraphNode that = (ContainerWrapperGraphNode) o;

        return getWrapper().equals(that.getWrapper());
    }

    @Override
    public int hashCode()
    {
        return getWrapper().hashCode();
    }

    @Override
    public String toString()
    {
        return "ContainerWrapperGraphNode{" +
                 "wrapper=" + wrapper +
                 '}';
    }

    @Override
    public void collectStats(final StatCollector statCollector)
    {
        statCollector.onVisitContainerNode();
    }

    @Override
    public void onNeighboringSource()
    {
        if (!this.getResultingValue().isPresent())
            throw new IllegalStateException("A container node touched by a source node, should have a value!");
    }
}
