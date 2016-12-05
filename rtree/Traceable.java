package lda.rtree;

import java.io.Serializable;

public interface Traceable extends Serializable
{
    public abstract void    setTraceable(boolean enable);
    public abstract boolean isTraceable();
}